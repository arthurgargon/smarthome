package com.gargon.smarthome.supradin;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

/**Интерфейс описывает слушателя данных Supradin модуля
 *
 * @author gargon
 */
public interface SupradinDataListener {
    
    /**Получение новых данных
     * 
     * @param message пакет данных
     */
    public void dataRecieved(SupradinDataMessage message);
    
}
