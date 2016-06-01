package com.gargon.smarthome.supradin.utils.http;

/**
 *
 * @author gargon
 */
public interface SendHTTPCallback {
    
    public boolean send(int dst, int prio, int command, byte[] data);
    
}
