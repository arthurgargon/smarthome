package com.gargon.smarthome.supradin.utils.http;

/**
 *
 * @author gargon
 */
public interface SendCallback {
    
public boolean send(int dst, int prio, int command, byte[] data);
    
}
