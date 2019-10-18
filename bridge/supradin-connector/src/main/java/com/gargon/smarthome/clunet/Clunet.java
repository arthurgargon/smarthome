package com.gargon.smarthome.clunet;

import com.gargon.smarthome.enums.Address;
import com.gargon.smarthome.enums.Command;
import com.gargon.smarthome.enums.Priority;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinConnectionResponseFilter;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

import java.util.List;

/**
 *
 * @author gargon
 */
public class Clunet {

    public final static int DATA_MAX_LENGTH = 128;

     /**Отправляет сообщение по сети clunet:
      * 
      * @param conn установленное соединение
      * @param address адрес устройства в сети clunet, которому производится отправка
      * @param priority приоритет отправляемого сообщения
      * @param command команда для отправки
      * @param data данные для отправки, не может быть null
      * @return true в случае успешной отправки сообщения
     */
    public static boolean send(SupradinConnection conn, final Address address, final Priority priority, final Command command, final byte[] data) {
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
    public static boolean send(SupradinConnection conn, final Address address, final Priority priority, final Command command) {
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
    public static SupradinDataMessage sendResponsible(SupradinConnection conn, final Address address, final Priority priority,
                                                      final Command command, final byte[] data,
            SupradinConnectionResponseFilter responseFilter, int resonseTimeout, int numAttempts) {
        SupradinDataMessage r = null;
        if (conn != null) {
            while (numAttempts-- > 0 
                    && (r = conn.sendDataAndWaitResponse(new SupradinDataMessage(address, priority, command, data), responseFilter, resonseTimeout))== null){
            }
        }
        return r;
    }
    
    public static SupradinDataMessage sendResponsible(SupradinConnection conn, final Address address, final Priority priority, final Command command,
            SupradinConnectionResponseFilter responseFilter, int resonseTimeout, int numAttempts) {
        SupradinDataMessage r = null;
        if (conn != null) {
            while (numAttempts-- > 0
                    && (r = conn.sendDataAndWaitResponse(new SupradinDataMessage(address, priority, command), responseFilter, resonseTimeout)) == null) {
            }
        }
        return r;
    }
    
    
    public static SupradinDataMessage sendResponsible(SupradinConnection conn, final Address address, final Priority priority,
                                                      final Command command, final byte[] data,
            SupradinConnectionResponseFilter responseFilter, int resonseTimeout) {
        return sendResponsible(conn, address, priority, command, data, responseFilter, resonseTimeout, 1);
    }
    
    public static SupradinDataMessage sendResponsible(SupradinConnection conn, final Address address, final Priority priority, final Command command,
            SupradinConnectionResponseFilter responseFilter, int responseTimeout) {
        return sendResponsible(conn, address, priority, command, responseFilter, responseTimeout, 1);
    }
    
    
   
    
    public static List<SupradinDataMessage> sendDiscovery(SupradinConnection conn, final int timeout) {
        if (conn != null) {
            return conn.sendDataAndWaitResponses(new SupradinDataMessage(Address.BROADCAST, Priority.MESSAGE, Command.DISCOVERY),
                    new SupradinConnectionResponseFilter() {

                        @Override
                        public boolean filter(SupradinDataMessage supradinRecieved) {
                            return supradinRecieved.getCommand() == Command.DISCOVERY_RESPONSE;
                        }
                    }, -1, timeout);
        }
        return null;
    }

    public static SupradinDataMessage sendPing(SupradinConnection conn, final Address address, byte[] data, final int timeout) {
        return sendResponsible(conn, address, Priority.MESSAGE, Command.PING, data, new SupradinConnectionResponseFilter() {

            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getCommand() == Command.PING_REPLY
                        && supradinRecieved.getSrc() == address
                        && supradinRecieved.getDst() == Address.SUPRADIN;
            }
        }, timeout);
    }
    
    public static SupradinDataMessage sendReboot(SupradinConnection conn, final Address address, final int bootTimeout) {
        return sendResponsible(conn, address, Priority.MESSAGE, Command.PING, new SupradinConnectionResponseFilter() {

            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getCommand() == Command.BOOT_COMPLETED
                        && supradinRecieved.getSrc() == address;
            }
        }, bootTimeout);
    }
    
}
