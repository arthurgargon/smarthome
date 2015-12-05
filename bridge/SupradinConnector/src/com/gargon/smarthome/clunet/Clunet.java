package com.gargon.smarthome.clunet;

import com.gargon.smarthome.clunet.utils.IntelHexReader;
import java.io.PrintStream;
import java.util.List;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinConnectionResponseFilter;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author gargon
 */
public class Clunet {

    public final static int DATA_MAX_LENGTH = 128;

    /*----------COMMANDS--------*/
    /* Поиск других устройств, параметров нет */
    public final static int COMMAND_DISCOVERY = 0x00;
    /* Ответ устройств на поиск, в качестве параметра - название устройства (текст) */
    public final static int COMMAND_DISCOVERY_RESPONSE = 0x01;

    /* Работа с загрузчиком. Данные - субкоманда.
     <-0 - загрузчик запущен
     ->1 - перейти в режим обновления прошивки
     <-2 - подтверждение перехода, плюс два байта - размер страницы
     ->3 запись прошивки, 4 байта - адрес, всё остальное - данные (равные размеру страницы)
     <-4 блок прошивки записан
     ->5 выход из режима прошивки */
    public final static int COMMAND_BOOT_CONTROL = 0x02;
    /* Перезагружает устройство в загрузчик. */
    public final static int COMMAND_REBOOT = 0x03;
    /* Посылается устройством после инициализации библиотеки, сообщает об успешной загрузке устройства. 
     Параметр - содержимое MCU регистра, говорящее о причине перезагрузки. */
    public final static int COMMAND_BOOT_COMPLETED = 0x04;

    /* Пинг, на эту команду устройство должно ответить следующей командой, возвратив весь буфер */
    public final static int COMMAND_PING = 0xFE;
    /* Ответ на пинг, в данных то, что было прислано в предыдущей команде */
    public final static int COMMAND_PING_REPLY = 0xFF;
    
    /* Команда переключения канала*/
    public final static int COMMAND_CHANNEL = 0x10;
   
    /* Сообщение о номере текущего канала*/
    public final static int COMMAND_CHANNEL_INFO = 0x11;

    /* Команда управления громкостью устройства*/
    public final static int COMMAND_VOLUME = 0x15;

    /* Сообщение о состоянии громкости устройства*/
    public final static int COMMAND_VOLUME_INFO = 0x16;

    /* Команда отключения звука устройства*/
    public final static int COMMAND_MUTE = 0x17;
    
     /* Команда управления эквалайзером*/
    public final static int COMMAND_EQUALIZER = 0x18;
    
    /* Сообщение о состоянии эквалайзера*/
    public final static int COMMAND_EQUALIZER_INFO = 0x19;

    /* Команда управления выключателями/реле*/
    public final static int COMMAND_SWITCH = 0x20;

    /* Сообщение о состоянии всех выключателей устройства в виде битовой маски*/
    public final static int COMMAND_SWITCH_INFO = 0x21;
    
    /* Команда запроса состояния нефиксируемых кнопок*/
    public final static int COMMAND_BUTTON = 0x22;
    
    /* Сообщает о состоянии нефиксируемой кнопки*/
    public final static int COMMAND_BUTTON_INFO = 0x23;

    /* Команда запроса текущей температуры*/
    public final static int COMMAND_TEMPERATURE = 0x25;
    
    /* Сообщение о температуре*/
    public final static int COMMAND_TEMPERATURE_INFO = 0x26;
    
    /* Команда запроса текущей влажности*/
    public final static int COMMAND_HUMIDITY = 0x27;
    
    /* Сообщает об уровне влажности*/
    public final static int COMMAND_HUMIDITY_INFO = 0x28;
    
    /* Команда поиска 1-wire устройств*/
    public final static int COMMAND_ONEWIRE_SEARCH = 0x30;
    
    /* Сообщает о найденном 1-wire устройстве*/
    public final static int COMMAND_ONEWIRE_INFO = 0x31;

    /* Команда запроса наличия движения*/
    public final static int COMMAND_MOTION = 0x40;
    
    /* Сообщает о наличии движения в помещении*/
    public final static int COMMAND_MOTION_INFO = 0x41;
    
    /* Команда запроса уровня освещенности*/
    public final static int COMMAND_LIGHT_LEVEL = 0x45;
    
    /* Сообщает об уровне  освещенности*/
    public final static int COMMAND_LIGHT_LEVEL_INFO = 0x46;
    
    /* Команда управления вентилятором */
    public final static int COMMAND_FAN = 0x50;
    
    /* Сообщает о текущем состоянии вентилятора */
    public final static int COMMAND_FAN_INFO = 0x51;
    
    /* Команда запроса состояния дверей*/
    public final static int COMMAND_DOORS = 0x55;
    
    /* Сообщает о текущем состоянии дверей */
    public final static int COMMAND_DOORS_INFO = 0x56;
    
    
    /* Команда управления теплым полом */
    public final static int COMMAND_HEATFLOOR = 0x60;
    
    /* Сообщает о состоянии теплого пола */
    public final static int COMMAND_HEATFLOOR_INFO = 0x61;
    
     /* Команда управления состоянием произвольного устройства */
    public final static int COMMAND_DEVICE_STATE = 0x70;

    /* Сообщает о состоянии произвольного устройства */
    public final static int COMMAND_DEVICE_STATE_INFO = 0x71;
    
     /* Команда блокирования*/
    public final static int COMMAND_ANDROID = 0xA0;
    
    

    /*----------PRIORITIES--------*/
    /* Приоритет пакета 1 - неважное уведомление, которое вообще может быть потеряно без последствий */
    public final static int PRIORITY_NOTICE = 1;
    /* Приоритет пакета 2 - какая-то информация, не очень важная */
    public final static int PRIORITY_INFO = 2;
    /* Приоритет пакета 3 - сообщение с какой-то важной информацией */
    public final static int PRIORITY_MESSAGE = 3;
    /* Приоритет пакета 4 - команда, на которую нужно сразу отреагировать */
    public final static int PRIORITY_COMMAND = 4;

    /*----------ADDRESSES----------*/
    public final static int ADDRESS_SUPRADIN  = 0x00;
    public final static int ADDRESS_BROADCAST = 0xFF;
    
    
    public final static int ADDRESS_AUDIOBOX        = 0x0A;
    public final static int ADDRESS_AUDIOBATH       = 0x0B;
    
    public final static int ADDRESS_RELAY_1         = 0x14;
    public final static int ADDRESS_RELAY_2         = 0x15;
    
    public final static int ADDRESS_KITCHEN         = 0x1D;
    public final static int ADDRESS_BATH_SENSORS    = 0x1E;
    public final static int ADDRESS_WARDROBE        = 0x1F;
    
    
    
    
     /**Отправляет сообщение по сети clunet:
      * 
      * @param conn установленное соединение
      * @param address адрес устройства в сети clunet, которому производится отправка
      * @param priority приоритет отправляемого сообщения
      * @param command команда для отправки
      * @param data данные для отправки, не может быть null
      * @return true в случае успешной отправки сообщения
     */
    public static boolean send(SupradinConnection conn, final int address, final int priority, final int command, final byte[] data) {
        if (conn != null) {
            return conn.sendData(new SupradinDataMessage(address, priority, command, data));
        }
        return false;
    }

    /**Отправляет сообщение без дополнительных данных по сети clunet:
      * 
      * @param conn установленное соединение
      * @param address адрес устройства в сети clunet, которому производится отправка
      * @param priority приоритет отправляемого сообщения
      * @param command команда для отправки
      * @return true в случае успешной отправки сообщения
     */
    public static boolean send(SupradinConnection conn, final int address, final int priority, final int command) {
        if (conn != null) {
            return conn.sendData(new SupradinDataMessage(address, priority, command));
        }
        return false;
    }
    
    /**
     * Отправляет сообщение по сети clunet и получает ответ-подтверждение отправки,
     * соответствующее @param responseFilter. В случае неудачи отправки, отправляет
     * еще раз и так @param numAttempts раз. Ожидание ответа после каждой отправки состовляет
     * @param resonseTimeout миллисекунд.
     * Реальная польза использования подобного метода наряду однократной отправкой
     * проявлятся в случаях когда устройство-получатель подвисло или временно
     * отключило обработку прерываний в результате чего протокол clunet на этом устройстве
     * оказывается временно парализованым. Подобная ситуация возникает, например, при обращении
     * к устройствам на которых производятся длительные операции измерения (температуры, влажности...).
     * Многократный посыл команды через незначительные промежутки времени позволяет избежать коллизий
     * и "поймать" момент свободного для приема команд устройства. 
     * @param resonseTimeout  следует выбирать соответствующим предельной длительности выполнения
     * преобразований. 
     * Выполнение данной команды с @param numAttempts большим единицы
     * недопустимо для операций последовательного изменения какой-либо величины,
     * в отличии от перевода в какое-либо постоянное состояния. Т.к в этом случае возможно возникновение
     * ошибки в момент подвисания сети clunet именно в момент передачи подтверждающей команды.
     * 
     * @param conn установленное соединение
     * @param address адрес устройства в сети clunet, которому производится отправка
     * @param priority приоритет отправляемого сообщения
     * @param command команда для отправки
     * @param responseFilter фильтр входящих сообщений для отбора сообщения 
     *  с подтверждением успешной отправки
     * @param resonseTimeout время ожидания ответа после каждой отправки, в мс
     * @param numAttempts количество попыток отправки сообщений
     * @return 
     */
    public static SupradinDataMessage sendResponsible(SupradinConnection conn, final int address, final int priority, final int command, final byte[] data,
            SupradinConnectionResponseFilter responseFilter, int resonseTimeout, int numAttempts) {
        SupradinDataMessage r = null;
        if (conn != null) {
            while (numAttempts-- > 0 
                    && (r = conn.sendDataAndWaitResponse(new SupradinDataMessage(address, priority, command, data), responseFilter, resonseTimeout))== null){
            }
        }
        return r;
    }
    
    public static SupradinDataMessage sendResponsible(SupradinConnection conn, final int address, final int priority, final int command,
            SupradinConnectionResponseFilter responseFilter, int resonseTimeout, int numAttempts) {
        SupradinDataMessage r = null;
        if (conn != null) {
            while (numAttempts-- > 0
                    && (r = conn.sendDataAndWaitResponse(new SupradinDataMessage(address, priority, command), responseFilter, resonseTimeout)) == null) {
            }
        }
        return r;
    }
    
    
    public static SupradinDataMessage sendResponsible(SupradinConnection conn, final int address, final int priority, final int command, final byte[] data,
            SupradinConnectionResponseFilter responseFilter, int resonseTimeout) {
        return sendResponsible(conn, address, priority, command, data, responseFilter, resonseTimeout, 1);
    }
    
    public static SupradinDataMessage sendResponsible(SupradinConnection conn, final int address, final int priority, final int command,
            SupradinConnectionResponseFilter responseFilter, int responseTimeout) {
        return sendResponsible(conn, address, priority, command, responseFilter, responseTimeout, 1);
    }
    
    
   
    
    public static List<SupradinDataMessage> sendDiscovery(SupradinConnection conn, final int timeout) {
        if (conn != null) {
            return conn.sendDataAndWaitResponses(new SupradinDataMessage(ADDRESS_BROADCAST, PRIORITY_MESSAGE, COMMAND_DISCOVERY),
                    new SupradinConnectionResponseFilter() {

                        @Override
                        public boolean filter(SupradinDataMessage supradinRecieved) {
                            return supradinRecieved.getCommand() == COMMAND_DISCOVERY_RESPONSE;
                        }
                    }, -1, timeout);
        }
        return null;
    }

    public static SupradinDataMessage sendPing(SupradinConnection conn, final int address, byte[] data, final int timeout) {
        return sendResponsible(conn, address, PRIORITY_MESSAGE, COMMAND_PING, data, new SupradinConnectionResponseFilter() {

            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getCommand() == COMMAND_PING_REPLY
                        && supradinRecieved.getSrc() == address
                        && supradinRecieved.getDst() == ADDRESS_SUPRADIN;
            }
        }, timeout);
    }
    
    public static SupradinDataMessage sendReboot(SupradinConnection conn, final int address, final int bootTimeout) {
        return sendResponsible(conn, address, PRIORITY_MESSAGE, COMMAND_PING, new SupradinConnectionResponseFilter() {

            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getCommand() == COMMAND_BOOT_COMPLETED
                        && supradinRecieved.getSrc() == address;
            }
        }, bootTimeout);
    }
    
    //добавить логирование ??
    
    private static final int COMMAND_FIRMWARE_UPDATE_START = 0;
    private static final int COMMAND_FIRMWARE_UPDATE_INIT = 1;
    private static final int COMMAND_FIRMWARE_UPDATE_READY = 2;
    private static final int COMMAND_FIRMWARE_UPDATE_WRITE = 3;
    private static final int COMMAND_FIRMWARE_UPDATE_WRITTEN = 4;
    private static final int COMMAND_FIRMWARE_UPDATE_DONE = 5;
    
    private static final int COMMAND_FIRMWARE_UPDATE_ERROR = 255;
    
    
    private static final DateFormat logDateFormat = new SimpleDateFormat(/*"dd.MM.yy */"HH:mm:ss.S");

    private static void log(PrintStream log, String message) {
        if (log != null) {
            log.println(logDateFormat.format(new Date()) + ": " + message);
        }
    }
    
    /*
        set bootTimeout = -1, if you do not need to restart a device
    */
    public static boolean sendFirmware(SupradinConnection conn, final int address, final int bootTimeout, final String filePath, PrintStream log) {
        if (conn != null) {
            IntelHexReader hexReader = new IntelHexReader();
            log(log, "Loading HEX");
            if (hexReader.read(filePath)) {
                log(log, "Loaded " + hexReader.getLength() + " bytes");
                SupradinConnectionResponseFilter bootControlFilter = new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getCommand() == COMMAND_BOOT_CONTROL
                                && supradinRecieved.getSrc() == address;
                    }
                };

                SupradinDataMessage r = null;
                if (bootTimeout >= 0) {
                    log(log, "Reboot command...");
                    r = conn.sendDataAndWaitResponse(new SupradinDataMessage(address, PRIORITY_COMMAND, COMMAND_REBOOT),
                            bootControlFilter, bootTimeout);
                }
                if (bootTimeout < 0 || r != null) {
                    //Устройство посылает CLUNET_COMMAND_BOOT_CONTROL, в данных 0. 
                    //Так мы узнали, что бутлоадер запустился.
                    if (bootTimeout < 0 || (r.getData().length == 1 && r.getData()[0] == COMMAND_FIRMWARE_UPDATE_START)) {
                        log(log, "Bootloader detected");
                        //Сразу же посылаем на адрес устройства CLUNET_COMMAND_BOOT_CONTROL, в данных 1. Это переводит устройство в режим прошивки.
                        r = conn.sendDataAndWaitResponse(new SupradinDataMessage(address, PRIORITY_COMMAND, COMMAND_BOOT_CONTROL,
                                new byte[]{COMMAND_FIRMWARE_UPDATE_INIT}), bootControlFilter, 1000);
                        //Устройство посылает CLUNET_COMMAND_BOOT_CONTROL, первый байт данных - 2, подтверждает, что перешли в режим прошивки.
                        //Следующие два байта - это размер страницы.
                        if (r != null && r.getData().length == 3 && r.getData()[0] == COMMAND_FIRMWARE_UPDATE_READY) {
                            int pageSize = ((r.getData()[2] & 0xFF) << 8) | (r.getData()[1] & 0xFF);
                            log(log, "Page size: " + pageSize + " bytes");
                            //Посылаем в устройство CLUNET_COMMAND_BOOT_CONTROL, байт 0 = 3.  Это команда на запись страницы прошивки;
                            //Байт 1 - номер передаваемой страницы;
                            //Байт 2,3 - размер передаваемых данных;
                            //Все остальные байты - сам кусок прошивки. Этот кусок должен быть равен размеру страницы, 
                            //который мы получили в пункте 4 (может и не соответствовать реальному, если реальный слишком большой).

                            int dataMaxLen = DATA_MAX_LENGTH - 4;   //или можно задавать меньшее число, в случае возникновения проблем при передаче
                            for (int i = 0; i < hexReader.getLength(); i += pageSize) {
                                int len0 = Math.min(pageSize, (int) (hexReader.getLength() - i));
                                //разбиваем при необходимости на куски, влазящие в протокол
                                for (int j = 0; j < len0; j += dataMaxLen) {
                                    int len1 = Math.min(dataMaxLen, (int) (len0 - j));

                                    byte[] data = new byte[len1 + 4];
                                    data[0] = COMMAND_FIRMWARE_UPDATE_WRITE;
                                    data[1] = (byte) (i / pageSize);
                                    data[2] = (byte) (len1 >> 0);
                                    data[3] = (byte) (len1 >> 8);

                                    System.arraycopy(hexReader.getData(), i + j, data, 4, len1);
                                    r = conn.sendDataAndWaitResponse(new SupradinDataMessage(address, PRIORITY_COMMAND, COMMAND_BOOT_CONTROL, data),
                                            bootControlFilter, 1000);

                                    //Устройство посылает CLUNET_COMMAND_BOOT_CONTROL, в данных 4 - это подтверждает, что страница прошита.
                                    if (r == null || r.getData().length != 1 || r.getData()[0] != COMMAND_FIRMWARE_UPDATE_WRITTEN) {
                                        return false;
                                    } else {
                                        if (log != null) {
                                            log.print("\r[");
                                            float percent = (float)(i + j + len1) / hexReader.getLength();
                                            int percent50 = (int) (50 * percent);
                                            for (int a = 0; a < 50; a++) {
                                                log.print(a < percent50 ? "#" : " ");
                                            }
                                            log.print("] " + (int) (percent * 100) + "%");
                                        }
                                    }
                                }
                            }
                            //log(log, "");
                            if (log != null){
                                log.println();
                            }
                            
                            //Посылаем в устройство CLUNET_COMMAND_BOOT_CONTROL, в данных 5 
                            //Это завершает работу бутлоадера и запускает только что прошитый код.
                            log(log, "Done!");
                            return conn.sendData(new SupradinDataMessage(address, PRIORITY_COMMAND, COMMAND_BOOT_CONTROL,
                                    new byte[]{COMMAND_FIRMWARE_UPDATE_DONE}));
                        } else {
                            log(log, "Waiting timeout or wrong answer: " + COMMAND_FIRMWARE_UPDATE_READY);
                        }
                    } else {
                        log(log, "Waiting timeout or wrong answer: " + COMMAND_FIRMWARE_UPDATE_START);
                    }
                } else {
                    log(log, "Device does not reply");
                }
            } else {
                log(log, "Invalid HEX");
            }
        } else {
            log(log, "NULL connection");
        }
        log(log, "An error occured while uploading. The operation failed!");
        return false;
    }
    
    public static boolean sendFirmware(SupradinConnection conn, final int address, final int bootTimeout, final String filePath) {
        return sendFirmware(conn, address, bootTimeout, filePath, null);
    }
}
