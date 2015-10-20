package com.gargon.smarthome.supradin.socket;

/**Интерфейс описывает слушателя данных сокета Supradin модуля
 *
 * @author gargon
 */
public interface SupradinSocketDataListener {
    
    /**Получение новых данных
     * 
     * @param port порт отправителя (Supradin) данных
     * @param data пакет данных
     */
    void dataRecieved(int port, byte[] data);
    
}
