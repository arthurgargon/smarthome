package com.gargon.smarthome.multicast.socket;

import java.net.InetAddress;

/*
 * @author gargon
 */
public interface MulticastSocketDataListener {

    /**
     * Получение новых данных
     *
     * @param ip IP-адрес отправителя данных
     * @param port порт отправителя данных
     * @param data пакет данных
     */
    void dataRecieved(InetAddress ip, int port,  byte[] data);

}
