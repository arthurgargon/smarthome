package com.gargon.smarthome.supradin.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Класс обеспечивает взаимодействие с модулем Supradin посредством UDP
 * соединения
 *
 * @author gargon
 */
public class SupradinSocket {

    private volatile boolean active;
    
    private final DatagramSocket socket;
    private final InetAddress ipAddress;

    private final byte[] recvBuf = new byte[1024];
    private final ExecutorService socketDataReciever;
    
    private final List<SupradinSocketDataListener> dataListeners = new CopyOnWriteArrayList();

    /**Создает объект SupradinSocket:
    *  1) создает сокет для взаимодействия по UDP
    *  2) создет поток-слушатель входящих сообщений, который
    * в случае получения сообщений ретранслирует их dataListeners
    * 
    *  @param ip IP-адрес модуля Supradin
    * 
    *  @throws SocketException
    *  @throws UnknownHostException
    */
    public SupradinSocket(String ip) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.ipAddress = InetAddress.getByName(ip);
        this.active = true;

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
                            byte[] data = Arrays.copyOfRange(recvPacket.getData(), recvPacket.getOffset(), recvPacket.getLength() - recvPacket.getOffset());

                            for (SupradinSocketDataListener listener : dataListeners) {
                                listener.dataRecieved(recvPacket.getPort(), data);
                            }
                        }
                    } catch (Exception ex) {
                        //close (interrupt recieve waiting), or some error
                    }
                }
            }
        });
    }

    /**Отправка пакета данных модулю Supradin на произвольный порт
     * через созданный UDP сокет
     * 
     * @param port номер UDP порта сервера
     * @param data передаваемые данные
     * @return Возвращает признак успешной отправки данных
     */
    public synchronized boolean send(int port, byte[] data) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, this.ipAddress, port);
            socket.send(sendPacket);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    /**Отправка пакета данных модулю Supradin на произвольный порт
     * через ранее созданный UDP сокет, а также ожидание responseCount ответов сервера(Supradin) 
     * соответствующих фильтру responseFilter в течение responseTimeout миллисекунд
     * 
     * @param port номер UDP порта сервера
     * @param data передаваемые данные
     * @param responseFilter фильтр входящих сообщений
     * @param responseCount количество ожидаемых ответов сервера
     *       0 - только отправка сообщения
     *      -1 - для ожидания всех сообщений в течение responseTimeout
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return Возвращает список всех полученных за время responseTimeout ответов
     */
    public List<byte[]> sendAndWaitResponses(final int port, final byte[] data, 
            final SupradinSocketResponseFilter responseFilter, final int responseCount, final int responseTimeout) {
        ExecutorService exService = null;
        List<byte[]> r = null;
        try {
            exService = Executors.newSingleThreadExecutor();
            FutureTask<List<byte[]>> futureTask = new FutureTask(new Callable() {

                private final List<byte[]> rdata = new ArrayList();

                @Override
                public List<byte[]> call() {
                    SupradinSocketDataListener listener;
                    //добавляем слушателя входящих сообщений
                    addDatagramDataListener(listener = new SupradinSocketDataListener() {

                        @Override
                        public void dataRecieved(int port_, byte[] data_) {
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
      
    /**Отправка пакета данных модулю Supradin на произвольный порт
     * через ранее созданный UDP сокет, а также ожидание ответа сервера(Supradin) 
     * соответствующего фильтру responseFilter в течение responseTimeout миллисекунд
     * 
     * @param port номер UDP порта сервера
     * @param data передаваемые данные
     * @param responseFilter фильтр входящих сообщений
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return Возвращает ответ сервера или null если ответ не был получен
     */
    public byte[] sendAndWaitResponse(int port, byte[] data, SupradinSocketResponseFilter responseFilter, int responseTimeout) {
        List<byte[]> responses = sendAndWaitResponses(port, data, responseFilter, 1, responseTimeout);
        if (responses != null && responses.size() == 1) {
            return responses.get(0);
        }
        return null;
    }

    /**Закрывает текущее соединение (UDP сокет)
     * 
     */
    public void close() {
        active = false;
        socket.close();
        socketDataReciever.shutdown();
    }

    /**Добавляет слушателя входящих сообщений Supradin модуля
     * 
     * @param listener объект слушателя входящих сообщений Supradin модуля
     */
    public void addDatagramDataListener(SupradinSocketDataListener listener) {
        dataListeners.add(listener);
    }

    /**Удаляет слушателя входящих сообщений Supradin модуля
     * 
     * @param listener объект слушателя входящих сообщений Supradin модуля
     */
    public void removeDatagramDataListener(SupradinSocketDataListener listener) {
        dataListeners.remove(listener);
    }

}
