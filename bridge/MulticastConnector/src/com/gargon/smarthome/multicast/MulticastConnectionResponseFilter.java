package com.gargon.smarthome.multicast;

import com.gargon.smarthome.multicast.messages.MulticastDataMessage;
import com.gargon.smarthome.multicast.socket.MulticastSocketResponseFilter;

/**Класс реализует абстрактный фильтр данных, получаемых из мультикаст группы.
 * Производится первоначальная проверка данных на соответствие формату MulticastDataMessage
 *
 * @author gargon
 */
public abstract class MulticastConnectionResponseFilter implements MulticastSocketResponseFilter {

    /**Абстрактный метод фильтрации данных формата MulticastDataMessage
     * по дополнительным параметрам
     * 
     * @param multicastMessageRecieved полученное сообщение формата MulticastDataMessage
     * @return Возвращает TRUE если сообщение прошло фильтр
     */
    public abstract boolean filter(MulticastDataMessage multicastMessageRecieved);

    /**Реализует фильтр интерфейса MulticastSocketResponseFilter,
     * в котором производится предпроверка на соответствие данных формату MulticastDataMessage
     * 
     * @param dataRecieved фильтруемый пакет данных
     * @return Возвращает признак соответствия данных формату MulticastDataMessage
     */
    @Override
    public boolean filter(byte[] dataRecieved) {
        if (dataRecieved != null) {
            MulticastDataMessage s = new MulticastDataMessage(dataRecieved);
            if (s.isValid()) {
                return filter(s);
            }
        }
        return false;
    }
}
