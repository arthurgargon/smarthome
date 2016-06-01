package com.gargon.smarthome.supradin.utils.http;

/**
 *
 * @author gargon
 */
public interface AskHTTPCallback {
    
    public byte[] ask(int dst, int prio, int command, byte[] data,
            int rsrc, int rcmd, int rtimeout);
    
}
