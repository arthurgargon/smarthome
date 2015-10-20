package com.gargon.smarthome.supradin.messages;

/**
 *
 * @author gargon
 */
public class SupradinControlMessage {

    //control command codes
    public final static byte COMMAND_PING       = 0;
    public final static byte COMMAND_CONNECT    = 1;
    public final static byte COMMAND_DISCONNECT = 2;
    
    
    private final static int MESSAGE_LENGTH           = 7;

    private final static int OFFSET_ID                = 0;    //uint16
    private final static int OFFSET_COMMAND           = 2;
    private final static int OFFSET_RESULT            = 3;
    private final static int OFFSET_CONNECTION_STATUS = 4;
    private final static int OFFSET_CONNECTION_USED   = 5;
    private final static int OFFSET_CONNECTION_FREE   = 6;
    
    public static char getCommandId(byte[] data) {
        char r = 0;
        if (data != null && data.length >= OFFSET_COMMAND) {
            r = (char) ((data[OFFSET_ID + 0] << 8) | (data[OFFSET_ID + 1]));
        }
        return r;
    }
    
    public static byte getCommandType(byte[] data) {
        byte r = -1;
        if (data != null && data.length >= OFFSET_COMMAND) {
            r = data[OFFSET_COMMAND];
        }
        return r;
    }

    public static boolean isResultSuccess(byte[] data) {
        boolean r = false;
        if (data != null && data.length == MESSAGE_LENGTH) {
            if (data[OFFSET_RESULT] == 1) {
                r = true;
            }
        }
        return r;
    }

    public static boolean isConnectionActive(byte[] data) {
        boolean r = false;
        if (data != null && data.length == MESSAGE_LENGTH) {
            if (data[OFFSET_CONNECTION_STATUS] == 1) {
                r = true;
            }
        }
        return r;
    }

    public static byte getConnectionCountUsed(byte[] data) {
        byte r = -1;
        if (data != null && data.length == MESSAGE_LENGTH) {
            r = data[OFFSET_CONNECTION_USED];
        }
        return r;
    }
    
    public static byte getConnectionCountFree(byte[] data) {
        byte r = -1;
        if (data != null && data.length == MESSAGE_LENGTH) {
            r = data[OFFSET_CONNECTION_FREE];
        }
        return r;
    }

}
