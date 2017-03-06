package com.gargon.smarthome.multicast;

import com.gargon.smarthome.multicast.messages.MulticastDataMessage;

/**Интерфейс описывает слушателя данных мультикаст группы
 *
 * @author gargon
 */
public interface MulticastDataListener {
    
    /**Получение новых данных
     * 
     * @param connection текущее соединение, например для немедленной отправки ответа
     * @param message пакет данных
     */
    public void dataRecieved(MulticastConnection connection, MulticastDataMessage message);
    
}
