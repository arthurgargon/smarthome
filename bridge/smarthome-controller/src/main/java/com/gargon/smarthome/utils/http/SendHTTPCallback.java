package com.gargon.smarthome.utils.http;

/**
 *
 * @author gargon
 */
public interface SendHTTPCallback {
    
    public boolean send(int dst, int prio, int command, byte[] data);
    
}
