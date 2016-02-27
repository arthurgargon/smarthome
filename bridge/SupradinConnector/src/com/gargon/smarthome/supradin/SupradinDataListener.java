package com.gargon.smarthome.supradin;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

/**Интерфейс описывает слушателя данных Supradin модуля
 *
 * @author gargon
 */
public interface SupradinDataListener {
    
    /**Получение новых данных
     * 
     * @param connection текущее соединение, например для немедленной отправки ответа
     * @param message пакет данных
     */
    public void dataRecieved(SupradinConnection connection, SupradinDataMessage message);
    
}
