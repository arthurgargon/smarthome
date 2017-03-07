package com.gargon.smarthome.multicast;


import com.gargon.smarthome.multicast.messages.MulticastDataMessage;
import com.gargon.smarthome.multicast.socket.MulticastSocket;
import com.gargon.smarthome.multicast.socket.MulticastSocketDataListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**Класс реализует API для взаимодействия с мульткаст группой.
 *
 * @author gargon
 */
public class MulticastConnection{
    
    public static final String MULTICAST_IP = "234.5.6.7";
    
    public static final int MULTICAST_DATA_PORT = 12345;
    
    private String groupIp = MULTICAST_IP;
    private int dataPort = MULTICAST_DATA_PORT;
    
    private volatile boolean connected;
    
    private MulticastSocket socketWrapper;
    private MulticastSocketDataListener socketListener;
    
    private final List<MulticastDataListener> dataListeners = new CopyOnWriteArrayList();

    /**Создает объект класса с предустановленными параметрами подключения:
     * IP адрес мультикаст группы - "234.5.6.7".
     * Номер порта данных - 12345.
     * 
     */
    public MulticastConnection() {
    }

    /**Создает объект класса с предустановленными параметрами подключения:
     * Номер порта данных - 12345.
     * 
     * @param groupIp
     */
    public MulticastConnection(String groupIp) {
        this.groupIp = groupIp;
    }
    
    /**Создает объект класса
     * 
     * @param groupIp IP адрес мультикаст группы
     * @param dataPort номер порта данных
     */
    public MulticastConnection(String groupIp, int dataPort) {
        this(groupIp);
        this.dataPort = dataPort;
    }
    
    /**Проверяет статус подключения к мультикаст группе
     * Определяется только логикой вызова команд 
     * 
     * @return признак подключения к мультикаст группе
     */
    public boolean isConnected(){
        return connected;
    }
    
    /**Устанавливает соединение с мультикаст группой.
     * 
     * @return Возвращает признак успешного открытия соединения
     */
    public boolean open() {
        try {
            socketWrapper = new MulticastSocket(groupIp, dataPort);

            final MulticastConnection connection = this;
            socketWrapper.addDatagramDataListener(socketListener = new MulticastSocketDataListener() {

                @Override
                public void dataRecieved(InetAddress ip, int port, byte[] data) {
                    if (port == dataPort) {
                        MulticastDataMessage message = new MulticastDataMessage(data);
                        if (message.isValid()) {
                            for (MulticastDataListener listener : dataListeners) {
                                listener.dataRecieved(connection, ip, message);
                            }
                        }
                    }
                }
            });

            connected = true;
            return true;
        } catch (SocketException | UnknownHostException ex) {
            socketWrapper = null;
        } catch (IOException ex) {
            Logger.getLogger(MulticastConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**Производит разрыв текущего активного подключения и закрывет соединение (сокет)
     */
    public void close(){
        if (socketWrapper != null){
            
            connected = false;
            
            if (socketWrapper != null && socketListener != null) {
                socketWrapper.removeDatagramDataListener(socketListener);
            }
            
            socketWrapper.close();
        }
    }

    
    /**Отправляет пакет данных на порт данных в мультикаст группу для ранее установленого активного подключения.
     * 
     * @param message пакет данных в формате MulticastDataMessage
     * @return Возвращает признак успешной отправки сообщения
     */
    public boolean sendData(MulticastDataMessage message) {
        if (socketWrapper != null && connected) {
            return socketWrapper.send(dataPort, message.toByteArray());
        }
        return false;
    }
    
    /**Отправляет пакет данных на порт данных мультикаст группы для ранее установленго активного подключения и 
     * ожидает responseCount ответов сервера
     * соответствующих фильтру responseFilter в течение responseTimeout миллисекунд.
     * 
     * @param message пакет данных в формате MulticastDataMessage
     * @param responseFilter фильтр входящих сообщений
     * @param responseCount количество ожидаемых ответов сервера:
     *       0 - только отправка сообщения;
     *      -1 - для ожидания всех сообщений в течение responseTimeout;
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return Возвращает список всех полученных за время responseTimeout ответов 
     */
    public List<MulticastDataMessage> sendDataAndWaitResponses(MulticastDataMessage message, MulticastConnectionResponseFilter responseFilter, 
            int responseCount, int responseTimeout) {
        if (socketWrapper != null && connected) {
            List<byte[]> responses = socketWrapper.sendAndWaitResponses(dataPort, message.toByteArray(), responseFilter, responseCount, responseTimeout);
            if (responses != null) {
                List<MulticastDataMessage> r = new ArrayList();
                for (byte[] response : responses) {
                    r.add(new MulticastDataMessage(response));
                }
                return r;
            }
        }
        return null;
    }
    
     /**Отправляет пакет данных на порт данных мультикаст группы для ранее установленго активного подключения и 
     * ожидает ответа соответствующего фильтру responseFilter в течение responseTimeout миллисекунд.
     * @param message пакет данных в формате MulticastDataMessage
     * @param responseFilter фильтр входящих сообщений
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return Возвращает ответ сервера или null если ответ не был получен 
     */
    public MulticastDataMessage sendDataAndWaitResponse(MulticastDataMessage message, MulticastConnectionResponseFilter responseFilter, int responseTimeout) {
        if (socketWrapper != null && connected) {
            byte[] response = socketWrapper.sendAndWaitResponse(dataPort, message.toByteArray(), responseFilter, responseTimeout);
            if (response != null){
                return new MulticastDataMessage(response);
            }
        }
        return null;
    }
    
    /**Добавляет слушателя входящих сообщений данных мультикаст группы
     * 
     * @param listener объект слушателя входящих сообщений мультикаст группы
     */
    public void addDataListener(MulticastDataListener listener) {
        dataListeners.add(listener);
    }
    
     /**Удаляет слушателя входящих сообщений данных мультикаст группы
     * 
     * @param listener объект слушателя входящих сообщений мультикаст модуля
     */
    public void removeDataListener(MulticastDataListener listener) {
        dataListeners.remove(listener);
    }

    /**IP адрес мультикаст группы
     * 
     * @return Возвращает текущий IP адрес мультикаст группы
     */
    public String getGroupIp() {
        return groupIp;
    }


    /**Номер порта данных соединения
     * 
     * @return Возвращает номер порта данных соединения
     */
    public int getDataPort() {
        return dataPort;
    }
    
    
}
