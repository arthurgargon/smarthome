package com.gargon.smarthome.multicast.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/*
 * @author gargon
 */
public class MulticastSocket {
    
    private volatile boolean active;
    
    private final java.net.MulticastSocket socket;

    private final byte[] recvBuf = new byte[1024];
    private final ExecutorService socketDataReciever;
    private final InetAddress multicastGroup;
    
    private final List<MulticastSocketDataListener> dataListeners = new CopyOnWriteArrayList();

    /**Создает объект MulticastSocket:
    *  1) создает сокет для взаимодействия по UDP
    *  2) создет поток-слушатель входящих сообщений, который
    * в случае получения сообщений ретранслирует их dataListeners
    * 
     * @param multicast_ip IP-адрес мултикаст группы
     * @param port порт входящих сообщений
    * 
    *  @throws SocketException
    *  @throws UnknownHostException
    */
    public MulticastSocket(String multicast_ip, int port) throws SocketException, UnknownHostException, IOException {
        socket = new java.net.MulticastSocket(port);
        //socket.setLoopbackMode(true); -> не работает на RPI, но работает на винде
                
        multicastGroup = InetAddress.getByName(multicast_ip);
        socket.joinGroup(multicastGroup);
        active = true;

        socketDataReciever = Executors.newSingleThreadExecutor();
        socketDataReciever.execute(new Runnable() {

            @Override
            public void run() {
                while (active) {
                    try {
                        DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
                        while (true) {
                            //fucking android ->
                            //после получения первого сообщения устанавливает размер recvBuf равный размеру этого первого сообщения
                            recvPacket.setLength(recvBuf.length);
                            socket.receive(recvPacket);
                            
                            //check your own message
                            boolean skip = false;
                            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
                            for (NetworkInterface netint : Collections.list(nets)) {
                                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                                    if (inetAddress.equals(recvPacket.getAddress())) {
                                        skip = true;
                                        break;
                                    }
                                }
                            }
                            
                            if (skip) {
                                continue;
                            }
                            
                            byte[] data = Arrays.copyOfRange(recvPacket.getData(), recvPacket.getOffset(), recvPacket.getLength() - recvPacket.getOffset());

                            for (MulticastSocketDataListener listener : dataListeners) {
                                listener.dataRecieved(recvPacket.getAddress(), recvPacket.getPort(), data);
                            }
                        }
                    } catch (Exception ex) {
                        //close (interrupt recieve waiting), or some error
                    }
                }
            }
        });
    }

    /**Отправка пакета данных в мультикаст группу на произвольный порт
     * через созданный UDP сокет
     * 
     * @param port номер UDP порта сервера
     * @param data передаваемые данные
     * @return Возвращает признак успешной отправки данных
     */
    public synchronized boolean send(int port, byte[] data) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, multicastGroup, port);
            socket.send(sendPacket);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    /**Отправка пакета данных в мультикаст группу на произвольный порт
     * через ранее созданный UDP сокет, а также ожидание responseCount ответов
     * соответствующих фильтру responseFilter в течение responseTimeout миллисекунд
     * 
     * @param port номер UDP порта сервера
     * @param data передаваемые данные
     * @param responseFilter фильтр входящих сообщений
     * @param responseCount количество ожидаемых ответов
     *       0 - только отправка сообщения
     *      -1 - для ожидания всех сообщений в течение responseTimeout
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return Возвращает список всех полученных за время responseTimeout ответов
     */
    public List<byte[]> sendAndWaitResponses(final int port, final byte[] data, 
            final MulticastSocketResponseFilter responseFilter, final int responseCount, final int responseTimeout) {
        ExecutorService exService = null;
        List<byte[]> r = null;
        try {
            exService = Executors.newSingleThreadExecutor();
            FutureTask<List<byte[]>> futureTask = new FutureTask(new Callable() {

                private final List<byte[]> rdata = new ArrayList();

                @Override
                public List<byte[]> call() {
                    MulticastSocketDataListener listener;
                    //добавляем слушателя входящих сообщений
                    addDatagramDataListener(listener = new MulticastSocketDataListener() {

                        @Override
                        public void dataRecieved(InetAddress ip, int port_, byte[] data_) {
                            if (port == port_ && responseFilter.filter(data_)) {
                                synchronized (rdata) {
                                    rdata.add(data_);
                                    if (responseCount >= 0
                                            && responseCount == rdata.size()) {
                                        rdata.notify();
                                    }
                                }
                            }
                        }
                    });

                    //отправка сообщения
                    if (send(port, data)) {
                        synchronized (rdata) {
                            if (responseCount < 0 
                                    || rdata.size() < responseCount) {
                                try {
                                    rdata.wait(responseTimeout);
                                } catch (InterruptedException ex) {
                                }
                            }
                        }
                    }
                    //удаляем временного слушателя входящих сообщений
                    removeDatagramDataListener(listener);
                    return rdata;
                }

            });
            exService.execute(futureTask);
            r = futureTask.get();
        } catch (InterruptedException ex) {
        } catch (ExecutionException ex) {
        } finally {
            if (exService != null) {
                exService.shutdown();
            }
        }
        return r;
    }
      
    /**Отправка пакета данных в мультикаст группу на произвольный порт
     * через ранее созданный UDP сокет, а также ожидание ответа
     * соответствующего фильтру responseFilter в течение responseTimeout миллисекунд
     * 
     * @param port номер UDP порта сервера
     * @param data передаваемые данные
     * @param responseFilter фильтр входящих сообщений
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return Возвращает ответ сервера или null если ответ не был получен
     */
    public byte[] sendAndWaitResponse(int port, byte[] data, MulticastSocketResponseFilter responseFilter, int responseTimeout) {
        List<byte[]> responses = sendAndWaitResponses(port, data, responseFilter, 1, responseTimeout);
        if (responses != null && responses.size() == 1) {
            return responses.get(0);
        }
        return null;
    }

    /**
     * Закрывает текущее соединение (UDP сокет)
     *
     */
    public void close() {
        active = false;
        try {
            socket.leaveGroup(multicastGroup);
        } catch (IOException e) {
        } finally {
            socket.close();
            socketDataReciever.shutdown();
        }
    }

    /**Добавляет слушателя входящих сообщений Supradin модуля
     * 
     * @param listener объект слушателя входящих сообщений Supradin модуля
     */
    public void addDatagramDataListener(MulticastSocketDataListener listener) {
        dataListeners.add(listener);
    }

    /**Удаляет слушателя входящих сообщений Supradin модуля
     * 
     * @param listener объект слушателя входящих сообщений Supradin модуля
     */
    public void removeDatagramDataListener(MulticastSocketDataListener listener) {
        dataListeners.remove(listener);
    }

}
