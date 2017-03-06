package com.gargon.smarthome.multicast.socket;

/*
 * @author gargon
 */
public interface MulticastSocketDataListener {

    /**
     * Получение новых данных
     *
     * @param port порт отправителя данных
     * @param data пакет данных
     */
    void dataRecieved(int port, byte[] data);

}
