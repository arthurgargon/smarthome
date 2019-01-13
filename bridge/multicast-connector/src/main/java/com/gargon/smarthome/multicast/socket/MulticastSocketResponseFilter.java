package com.gargon.smarthome.multicast.socket;

/**Интерфейс описывает фильтр данных ответа  мультикаст-группы
 *
 * @author gargon
 */
public interface MulticastSocketResponseFilter {
    
    /**Фильтрация данных
     * 
     * @param dataRecieved пакет фильтруемых данных
     * @return Возвращает true если данные прошли фильтрацию
     */
    public boolean filter(byte[] dataRecieved);
    
}
