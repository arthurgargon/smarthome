package com.gargon.smarthome.supradin;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.supradin.messages.SupradinControlMessage;
import com.gargon.smarthome.supradin.socket.SupradinSocket;
import com.gargon.smarthome.supradin.socket.SupradinSocketResponseFilter;
import com.gargon.smarthome.supradin.socket.SupradinSocketDataListener;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**Класс реализует API для взаимодействия с модулем Supradin.
 *  Порядок вызова методов для взаимодействия с модулем:
 *  1)open;
 *  2)connect, addDataListener;
 *  3)sendData / sendDataAndWaitResponse / sendDataAndWaitResponses;
 *  4)close.
 *
 * @author gargon
 */
public class SupradinConnection{
    
    /**
     * IP адрес Supradin модуля по умолчанию
     */
    public static final String SUPRADIN_IP = "192.168.1.10";
    
    /**
     * Номер порта управления Supradin по умолчанию
     */
    public static final int SUPRADIN_CONTROL_PORT = 1234;
    
    /**
     * Номер порта данных Supradin по умолчанию
     */
    public static final int SUPRADIN_DATA_PORT = 1235;

    private String host = SUPRADIN_IP;
    private int controlPort = SUPRADIN_CONTROL_PORT;
    private int dataPort = SUPRADIN_DATA_PORT;
    
    /**
     * Период отправки "пинг" сообщений для поддержания соединения, в секундах
     */
    private final static int SUPRADIN_CONNECTION_KEEPER_PERIOD = 10; //s
    
    /**
     * Время ожидания ответа на отправленное сообщение нп порт управления, в миллисекундах
     */
    private final static int SUPRADIN_CONNECTION_CONTROL_RESPONSE_TIMEOUT = 2000; //ms
    
    private volatile boolean connected;
    private volatile boolean active;
    private char lastControlMessageId;
    
    private SupradinSocket socketWrapper;
    private SupradinSocketDataListener socketListener;
    private ScheduledExecutorService connectionKeeper;
    
    private final List<SupradinDataListener> dataListeners = new CopyOnWriteArrayList();

    /**Создает объект класса с предустановленными параметрами подключения:
     * IP адрес модуля Supradin - "192.168.1.10".
     * Номер порта управления модуля Supradin - 1234.
     * Номер порта данных модуля Supradin - 1235.
     * 
     */
    public SupradinConnection() {
    }

    /**Создает объект класса с предустановленными параметрами подключения:
     * Номер порта управления модуля Supradin - 1234.
     * Номер порта данных модуля Supradin - 1235.
     * 
     * @param host IP адрес модуля Supradin
     */
    public SupradinConnection(String host) {
        this.host = host;
    }
    /**Создает объект класса
     * 
     * @param host IP адрес модуля Supradin
     * @param controlPort номер порта управления модуля Supradin
     * @param dataPort номер порта данных модуля Supradin
     */
    public SupradinConnection(String host, int controlPort, int dataPort) {
        this(host);
        this.controlPort = controlPort;
        this.dataPort = dataPort;
    }
    
    /**Проверяет статус подключения к модулю Supradin
     * Определяется только логикой вызова команд 
     * 
     * @return признак подключения к модулю Supradin
     */
    public boolean isConnected(){
        return connected;
    }
    
    /**Проверяет состояние подключения к модулю Supradin
     * Определяется на основании ответов на команду ping
     * Рекомендуется использовать для проверки соединения
     * 
     * @return признак состояния подключения к модулю Supradin
     */
    public boolean isActive(){
        return active;
    }
    
    /**Открывает соединение с модулем Supradin.
     * Внимание! Подключение еще не установлено, команда на подключение модулю не отправлена, положительный ответ не получен.
     * Открывается лишь сокет соединения. Для успешного установления активного подключения в дальнейшем следует вызывать {@link SupradinConnection#connect()}.
     * Для закрытия текущего соединения следует вызывать  {@link SupradinConnection#close()}.
     * 
     * @return Возвращает признак успешного открытия соединения
     */
    public boolean open() {
        try {
            socketWrapper = new SupradinSocket(host);
            connected = false;
            return true;
        } catch (SocketException | UnknownHostException ex) {
            socketWrapper = null;
        }
        return false;
    }
    
    /**Производит разрыв текущего активного подключения
     * и закрывет соединение (сокет). Дальнейшее повторное открытие сокета методом {@link SupradinConnection#open()} невозможно.
     * Предварительный вызов метода  {@link SupradinConnection#disconnect()} делать не нужно.
     */
    public void close(){
        if (socketWrapper != null){
            disconnect();
            socketWrapper.close();
        }
    }
    
    /**Отправляет команду на управляющий порт модуля Supradin и получает ответ на нее.
     * 
     * @param command код команды
     * @return Возвращает ответ сервера на отправленную команду или null если ответ получен не был
     */
    private synchronized byte[] control(byte command) {
        if (socketWrapper != null) {
            ++lastControlMessageId;
            final byte[] dataToSend = new byte[]{(byte) (lastControlMessageId >> 8 & 0xFF), (byte) lastControlMessageId, command};
            return socketWrapper.sendAndWaitResponse(controlPort,
                    dataToSend,
                    new SupradinSocketResponseFilter() {

                        @Override
                        public boolean filter(byte[] dataRecieved) {
                            char id = SupradinControlMessage.getCommandId(dataToSend);
                            return id > 0 && id == SupradinControlMessage.getCommandId(dataRecieved);
                        }
                    },
                    SUPRADIN_CONNECTION_CONTROL_RESPONSE_TIMEOUT);
        }
        return null;
    }
    
    /**Отправляет команду на подключение к модулю Supradin
     * Дополнительно добавляется слушатель сообщений соединения 
     * и запускается дополнительный поток периодически отправляющий {@link SupradinConnection#ping()} 
     * серверу и, тем самым, поддерживающим соединение в активном состоянии 
     * (в том числе открывающий соединение, если его не удалось установить командой COMMAND_CONNECT).
     * 
     * @return возвращает признак, если соединение удалось установить сразу. Так или иначе,
     * даже если получен false, попытки установить соединение будут осуществляться запущенным
     * connectionKeeper'ом вызовом ping()
     */
    public boolean connect() {
        if (!connected) {
            byte[] response = control(SupradinControlMessage.COMMAND_CONNECT);
            socketWrapper.addDatagramDataListener(socketListener = new SupradinSocketDataListener() {

                @Override
                public void dataRecieved(int port, byte[] data) {
                    if (port == dataPort) {
                        SupradinDataMessage supradin = new SupradinDataMessage(data);
                        if (supradin.isValid()) {
                            for (SupradinDataListener listener : dataListeners) {
                                listener.dataRecieved(supradin);
                            }
                        }
                    }
                }
            });

            connectionKeeper = Executors.newSingleThreadScheduledExecutor();
            connectionKeeper.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    active = ping();
                }
            }, 0, SUPRADIN_CONNECTION_KEEPER_PERIOD, TimeUnit.SECONDS);
            //}

            connected = true;
            return SupradinControlMessage.isConnectionActive(response);
        }
        return active;
    }

    /**Отправляет команду ping модулю Supradin,
     * которая служит для поддержания подключения активным или восстановления ранее разорванного подключения.
     * В самостоятельном вызове данного метода нет необходимости
     * 
     * @return Возвращает признак активности соединения
     */
    protected boolean ping() {
        if (connected) {
            return SupradinControlMessage.isConnectionActive(control(SupradinControlMessage.COMMAND_PING));
        }
        return false;
    }

    /**Отправляет команду на разрыв текущего подключения с модулем Supradin.
     * Дальнейшей повторное и далее подключение возможно методом {@link SupradinConnection#connect()}.
     * 
     * @return Возвращает признак успешного разрыва активного подключения
     */
    public boolean disconnect() {
        if (connected) {
            if (connectionKeeper != null) {
                connectionKeeper.shutdown();
            }
            if (socketWrapper != null && socketListener != null) {
                socketWrapper.removeDatagramDataListener(socketListener);
            }
            connected = false;
            return SupradinControlMessage.isConnectionActive(control(SupradinControlMessage.COMMAND_DISCONNECT));
        }
        return false;
    }
    
    /**Отправляет пакет данных на порт данных модуля Supradin для ранее установленго активного подключения.
     * 
     * @param message пакет данных в формате SupradinDataMessage
     * @return Возвращает признак успешной отправки сообщения
     */
    public boolean sendData(SupradinDataMessage message) {
        if (socketWrapper != null && connected) {
            return socketWrapper.send(dataPort, message.toByteArray());
        }
        return false;
    }
    
    /**Отправляет пакет данных на порт данных модуля Supradin для ранее установленго активного подключения и 
     * ожидает responseCount ответов сервера
     * соответствующих фильтру responseFilter в течение responseTimeout миллисекунд.
     * 
     * @param message пакет данных в формате SupradinDataMessage
     * @param responseFilter фильтр входящих сообщений
     * @param responseCount количество ожидаемых ответов сервера:
     *       0 - только отправка сообщения;
     *      -1 - для ожидания всех сообщений в течение responseTimeout;
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return Возвращает список всех полученных за время responseTimeout ответов 
     */
    public List<SupradinDataMessage> sendDataAndWaitResponses(SupradinDataMessage message, SupradinConnectionResponseFilter responseFilter, 
            int responseCount, int responseTimeout) {
        if (socketWrapper != null && connected) {
            List<byte[]> responses = socketWrapper.sendAndWaitResponses(dataPort, message.toByteArray(), responseFilter, responseCount, responseTimeout);
            if (responses != null) {
                List<SupradinDataMessage> r = new ArrayList();
                for (byte[] response : responses) {
                    r.add(new SupradinDataMessage(response));
                }
                return r;
            }
        }
        return null;
    }
    
     /**Отправляет пакет данных на порт данных модуля Supradin для ранее установленго активного подключения и 
     * ожидает ответа сервера соответствующего фильтру responseFilter в течение responseTimeout миллисекунд.
     * @param message пакет данных в формате SupradinDataMessage
     * @param responseFilter фильтр входящих сообщений
     * @param responseTimeout время ожидания всех ответов в миллисекундах
     * @return Возвращает ответ сервера или null если ответ не был получен 
     */
    public SupradinDataMessage sendDataAndWaitResponse(SupradinDataMessage message, SupradinConnectionResponseFilter responseFilter, int responseTimeout) {
        if (socketWrapper != null && connected) {
            byte[] response = socketWrapper.sendAndWaitResponse(dataPort, message.toByteArray(), responseFilter, responseTimeout);
            if (response != null){
                return new SupradinDataMessage(response);
            }
        }
        return null;
    }
    
    /**Добавляет слушателя входящих сообщений данных Supradin модуля
     * 
     * @param listener объект слушателя входящих сообщений Supradin модуля
     */
    public void addDataListener(SupradinDataListener listener) {
        dataListeners.add(listener);
    }
    
     /**Удаляет слушателя входящих сообщений данных Supradin модуля
     * 
     * @param listener объект слушателя входящих сообщений Supradin модуля
     */
    public void removeDataListener(SupradinDataListener listener) {
        dataListeners.remove(listener);
    }

    /**IP адрес соединения с Supradin модулем
     * 
     * @return Возвращает текущий IP адрес соединения с Supradin модулем
     */
    public String getHost() {
        return host;
    }

    /**Номер порта управления соединения с Supradin модулем
     * 
     * @return Возвращает номер порта управления соединения с Supradin модулем
     */
    public int getControlPort() {
        return controlPort;
    }

    /**Номер порта данных соединения с Supradin модулем
     * 
     * @return Возвращает номер порта данных соединения с Supradin модулем
     */
    public int getDataPort() {
        return dataPort;
    }
    
    
}
