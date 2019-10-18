package com.gargon.smarthome.supradin.messages;

import com.gargon.smarthome.enums.Address;
import com.gargon.smarthome.enums.Command;
import com.gargon.smarthome.enums.Priority;
import com.gargon.smarthome.utils.HexDataUtils;

import java.util.Arrays;

/**
 *
 * @author gargon
 */
public class SupradinDataMessage {

    private final static int MESSAGE_LENGTH  = 8;
    
    private final static int OFFSET_IP       = 0;
    private final static int OFFSET_SRC_PRIO = 4;
    private final static int OFFSET_DST      = 5;
    private final static int OFFSET_COMMAND  = 6;
    private final static int OFFSET_SIZE     = 7;

    
    private int ip;
    private int src;    //union prio
    private int dst;    
    private int command;
    private int size = -1;
    
    private byte[] data;

    public SupradinDataMessage(byte[] buf) {
        if (buf != null && buf.length >= MESSAGE_LENGTH) {
            size = buf[OFFSET_SIZE] & 0xFF;
            if (buf.length == size + MESSAGE_LENGTH) {
                ip = (((buf[OFFSET_IP + 3] & 0xFF) << 24)
                        | ((buf[OFFSET_IP + 2] & 0xFF) << 16)
                        | ((buf[OFFSET_IP + 1] & 0xFF) << 8)
                        | ((buf[OFFSET_IP + 0] & 0xFF) << 0));
                
                src = buf[OFFSET_SRC_PRIO] & 0xFF;
                dst = buf[OFFSET_DST] & 0xFF;
                command = buf[OFFSET_COMMAND] & 0xFF;
                
                data = Arrays.copyOfRange(buf, MESSAGE_LENGTH, buf.length);
            } else {
                size = -1;  //not valid packet
            }
        }
    }

     public SupradinDataMessage(int ip, Address dst, Priority src_prio, Command command, byte[] data) {
        if (data != null) {
            this.ip = ip;
            this.src = src_prio.getValue();
            this.dst = dst.getValue();
            this.command = command.getValue();

            this.size = data.length;
            this.data = data;
        }
    }
    
    public SupradinDataMessage(Address dst, Priority src_prio, Command command, byte[] data) {
        this(0, dst, src_prio, command, data);
    }
    
     public SupradinDataMessage(Address dst, Priority src_prio, Command command) {
         this(dst, src_prio, command, new byte[]{});
    }

    public boolean isValid() {
        return size >= 0;
    }

    public byte[] toByteArray() {
        byte[] array = null;
        if (size >= 0) {
            array = new byte[MESSAGE_LENGTH + size];

            array[OFFSET_IP + 0] = (byte)((ip >> 0) & 0xFF);
            array[OFFSET_IP + 1] = (byte)((ip >> 8) & 0xFF);
            array[OFFSET_IP + 2] = (byte)((ip >> 16) & 0xFF);
            array[OFFSET_IP + 3] = (byte)((ip >> 24) & 0xFF);
            
            array[OFFSET_SRC_PRIO] = (byte) src;
            array[OFFSET_DST] = (byte) dst;
            array[OFFSET_COMMAND] = (byte) command;
            
            array[OFFSET_SIZE] = (byte) size;
            System.arraycopy(data, 0, array, MESSAGE_LENGTH, size);
        }
        return array;
    }

    public Address getSrc() {
        return Address.getByValue(src);
    }

    public Address getDst() {
        return Address.getByValue(dst);
    }

    public Command getCommand() {

        return Command.getByValue(command);
    }

    public int getIp() {
        return ip;
    }

    public byte[] getData() {
        return data;
    }
    
    public boolean isIpValid() {
        return ip != 0
                && (src == Address.SUPRADIN.getValue() || src > 0x80);
    }
    
    public String getIpAsString() {
        return String.format("%d.%d.%d.%d",
                (ip >> 0) & 0xFF,
                (ip >> 8) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 24) & 0xFF);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.ip;
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
        final SupradinDataMessage other = (SupradinDataMessage) obj;
        if (this.ip != other.ip) {
            return false;
        }
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
        return "ip=" + getIpAsString() + ", src=" + getSrc() + ", dst=" + getDst() + ", command=" + getCommand() + ", data=" + HexDataUtils.bytesToHex(data);
    }
}
