package com.gargon.smarthome.supradin.socket;

/**Интерфейс описывает фильтр данных ответа Supradin модуля
 *
 * @author gargon
 */
public interface SupradinSocketResponseFilter {
    
    /**Фильтрация данных
     * 
     * @param dataRecieved пакет фильтруемых данных
     * @return Возвращает true если данные прошли фильтрацию
     */
    public boolean filter(byte[] dataRecieved);
    
}
