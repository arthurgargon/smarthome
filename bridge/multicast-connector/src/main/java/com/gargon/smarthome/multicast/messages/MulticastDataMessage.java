package com.gargon.smarthome.multicast.messages;

import com.gargon.smarthome.utils.DataFormat;
import java.util.Arrays;

/**
 *
 * @author gargon
 */
public class MulticastDataMessage {

    private final static int MESSAGE_LENGTH  = 4;
    
    private final static int OFFSET_SRC_PRIO = 0;
    private final static int OFFSET_DST      = 1;
    private final static int OFFSET_COMMAND  = 2;
    private final static int OFFSET_SIZE     = 3;

    
    private int src;    //union prio
    private int dst;    
    private int command;
    private int size = -1;
    
    private byte[] data;

    public MulticastDataMessage(byte[] buf) {
        if (buf != null && buf.length >= MESSAGE_LENGTH) {
            size = buf[OFFSET_SIZE] & 0xFF;
            if (buf.length == size + MESSAGE_LENGTH) {
                src = buf[OFFSET_SRC_PRIO] & 0xFF;
                dst = buf[OFFSET_DST] & 0xFF;
                command = buf[OFFSET_COMMAND] & 0xFF;
                
                data = Arrays.copyOfRange(buf, MESSAGE_LENGTH, buf.length);
            } else {
                size = -1;  //not valid packet
            }
        }
    }

    public MulticastDataMessage(int dst, int src_prio, int command, byte[] data) {
        if (data != null) {
            this.src = src_prio;
            this.dst = dst;
            this.command = command;

            this.size = data.length;
            this.data = data;
        }
    }
    
     public MulticastDataMessage(int dst, int src_prio, int command) {
         this(dst, src_prio, command, new byte[]{});
    }

    public boolean isValid() {
        return size >= 0;
    }

    public byte[] toByteArray() {
        byte[] array = null;
        if (size >= 0) {
            array = new byte[MESSAGE_LENGTH + size];
            
            array[OFFSET_SRC_PRIO] = (byte) src;
            array[OFFSET_DST] = (byte) dst;
            array[OFFSET_COMMAND] = (byte) command;
            
            array[OFFSET_SIZE] = (byte) size;
            System.arraycopy(data, 0, array, MESSAGE_LENGTH, size);
        }
        return array;
    }

    public int getSrc() {
        return src;
    }

    public int getDst() {
        return dst;
    }

    public int getCommand() {
        return command;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.src;
        hash = 19 * hash + this.dst;
        hash = 19 * hash + this.command;
        hash = 19 * hash + this.size;
        hash = 19 * hash + Arrays.hashCode(this.data);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MulticastDataMessage other = (MulticastDataMessage) obj;

        if (this.src != other.src) {
            return false;
        }
        if (this.dst != other.dst) {
            return false;
        }
        if (this.command != other.command) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        if (!Arrays.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }

    
    @Override
    public String toString() {
        return "src=" + src + ", dst=" + dst + ", command=" + command + ", data=" + DataFormat.bytesToHex(data);
    }
}
