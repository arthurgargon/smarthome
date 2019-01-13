package com.gargon.smarthome.supradin;

import com.gargon.smarthome.supradin.socket.SupradinSocketResponseFilter;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

/**Класс реализует абстрактный фильтр данных, получаемых с модуля Supradin.
 * Производится первоначальная проверка данных на соответствие формату SupradinDataMessage
 *
 * @author gargon
 */
public abstract class SupradinConnectionResponseFilter implements SupradinSocketResponseFilter {

    /**Абстрактный метод фильтрации данных формата SupradinDataMessage
     * по дополнительным параметрам
     * 
     * @param supradinRecieved полученное сообщение формата SupradinDataMessage
     * @return Возвращает TRUE если сообщение прошло фильтр
     */
    public abstract boolean filter(SupradinDataMessage supradinRecieved);

    /**Реализует фильтр интерфейса SupradinSocketResponseFilter,
     * в котором производится предпроверка на соответствие данных формату SupradinDataMessage
     * 
     * @param dataRecieved фильтруемый пакет данных
     * @return Возвращает признак соответствия данных формату SupradinDataMessage
     */
    @Override
    public boolean filter(byte[] dataRecieved) {
        if (dataRecieved != null) {
            SupradinDataMessage s = new SupradinDataMessage(dataRecieved);
            if (s.isValid()) {
                return filter(s);
            }
        }
        return false;
    }
}
