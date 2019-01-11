package com.gargon.smarthome.clunet;

import java.util.List;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinConnectionResponseFilter;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

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
    
    /*Запрос текущего времени*/
    public final static int COMMAND_TIME = 0x08;
    
    /*Сообщение о текущем времени*/
    public final static int COMMAND_TIME_INFO = 0x09;

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
    
    /* Команда управления FM-приемником*/
    public final static int COMMAND_FM = 0x1C;
    
    /* Сообщение о состоянии FM-приемника*/
    public final static int COMMAND_FM_INFO = 0x1D;

    /* Команда управления включением/выключением устройства*/
    public final static int COMMAND_POWER = 0x1E;
    
    /* Команда запроса состояния (вкл/выкл) устройства*/
    public final static int COMMAND_POWER_INFO = 0x1F;
    
    /* Команда управления выключателями/реле*/
    public final static int COMMAND_SWITCH = 0x20;
    
    /* Сообщение о состоянии всех выключателей устройства в виде битовой маски*/
    public final static int COMMAND_SWITCH_INFO = 0x21;
    
    /* Команда запроса состояния нефиксируемых кнопок*/
    public final static int COMMAND_BUTTON = 0x22;
    
    /* Сообщает о состоянии нефиксируемой кнопки*/
    public final static int COMMAND_BUTTON_INFO = 0x23;
    
    /* Сообщает код набранный с помощью диска телефона*/
    public final static int COMMAND_ROTARY_DIAL_NUMBER_INFO = 0x24;
    
    /* Команда запроса текущей температуры*/
    public final static int COMMAND_TEMPERATURE = 0x25;
    
    /* Сообщение о температуре*/
    public final static int COMMAND_TEMPERATURE_INFO = 0x26;
    
    /* Команда запроса текущей влажности*/
    public final static int COMMAND_HUMIDITY = 0x27;
    
    /* Сообщает об уровне влажности*/
    public final static int COMMAND_HUMIDITY_INFO = 0x28;
    
    /* Команда запроса текущего атмосферного давления*/
    public final static int COMMAND_PRESSURE = 0x29;
    
    /* Сообщает об уровне атмосферного давления*/
    public final static int COMMAND_PRESSURE_INFO = 0x2A;
    
    /* Команда запроса метеоданных*/
    public final static int COMMAND_METEO = 0x2E;
    
    /* Сообщает метеоданные*/
    public final static int COMMAND_METEO_INFO = 0x2F;
    
    
    /* Команда поиска 1-wire устройств*/
    public final static int COMMAND_ONEWIRE_SEARCH = 0x30;
    
    /* Сообщает о найденном 1-wire устройстве*/
    public final static int COMMAND_ONEWIRE_INFO = 0x31;
    
    /* Команда запроса напряжения*/
    public final static int COMMAND_VOLTAGE = 0x32;
    
    /* Сообщает об уровне напряжения*/
    public final static int COMMAND_VOLTAGE_INFO = 0x33;

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
    
    /* Команда управления подзарядкой устройств */
    public final static int COMMAND_CHARGE = 0x52;
    
    /* Сообщает о текущем состоянии процесса зарядки */
    public final static int COMMAND_CHARGE_INFO = 0x53;
    
    /* Команда запроса состояния дверей*/
    public final static int COMMAND_DOORS = 0x55;
    
    /* Сообщает о текущем состоянии дверей */
    public final static int COMMAND_DOORS_INFO = 0x56;
    
    
    /* Команда упрвления диммером*/
    public final static int COMMAND_DIMMER = 0x57;
    
    /* Сообщает о текущем состоянии диммера */
    public final static int COMMAND_DIMMER_INFO = 0x58;
    
    
    /* Команда управления теплым полом */
    public final static int COMMAND_HEATFLOOR = 0x60;
    
    /* Сообщает о состоянии теплого пола */
    public final static int COMMAND_HEATFLOOR_INFO = 0x61;
    
    
    /* Команда управления сервоприводом */
    public final static int COMMAND_SERVO = 0x66;
    
    /* Сообщает о состоянии сервопривода */
    public final static int COMMAND_SERVO_INFO = 0x67;
    
    
     /* Команда управления состоянием произвольного устройства */
    public final static int COMMAND_DEVICE_STATE = 0x70;

    /* Сообщает о состоянии произвольного устройства */
    public final static int COMMAND_DEVICE_STATE_INFO = 0x71;
    
    /* Сообщает о  о нажатии кнопки на ПДУ */
    public final static int COMMAND_RC_BUTTON_PRESSED = 0x75;
    
    /* Эмулирует нажтие кнопки на ПДУ */
    //public final static int CLUNET_COMMAND_RC_BUTTON_SEND = 0x76;
    
    
     /* Команда блокирования*/
    public final static int COMMAND_ANDROID = 0xA0;

    /* Команда отладки*/
    public final static int COMMAND_DEBUG = 0x99;
    
    

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
    
    
    public final static int ADDRESS_AUDIOBOX        = 0x84;
    public final static int ADDRESS_AUDIOBATH       = 0x0B;
    
    public final static int ADDRESS_RELAY_1         = 0x14;
    public final static int ADDRESS_RELAY_2         = 0x15;
    
    public final static int ADDRESS_SOCKET_DIMMER   = 0x20;
    
    public final static int ADDRESS_KITCHEN         = 0x1D;
    public final static int ADDRESS_BATH_SENSORS    = 0x1E;
    public final static int ADDRESS_WARDROBE        = 0x1F;
    
    public final static int ADDRESS_METEO           = 0x81;
    public final static int ADDRESS_KITCHEN_LIGHT   = 0x82;
    public final static int ADDRESS_WATER_SYSTEM    = 0x83;
    public final static int ADDRESS_TELEPHONE       = 0x84;
    
    
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
    
}
