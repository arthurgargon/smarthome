package com.gargon.smarthome.multicast;

import com.gargon.smarthome.Smarthome;
import com.gargon.smarthome.SmarthomeDictionary;
import com.gargon.smarthome.multicast.messages.MulticastDataMessage;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gargon
 */
public class MulticastDevice {
    
    private int device_id;
    private String device_name;
    
    private MulticastConnection multicastConnection;
    
    private static final Logger LOG = Logger.getLogger(MulticastDevice.class.getName());
    
    public void send(int address, int command, byte[] data) {
        multicastConnection.sendData(new MulticastDataMessage(address, device_id, command, data));
    }
    
    private void send(int address, int command) {
        multicastConnection.sendData(new MulticastDataMessage(address, device_id, command));
    }
    
    public MulticastDevice(final int device_id, MulticastDataListener dataListener) {
        try {
            this.device_id = device_id;
            
            this.device_name = SmarthomeDictionary.getDeviceById(device_id);
            if (this.device_name == null) {
                this.device_name = String.valueOf(device_id);
            }
            
            multicastConnection = new MulticastConnection();
            multicastConnection.open();
            
            send(Smarthome.ADDRESS_BROADCAST, Smarthome.COMMAND_BOOT_COMPLETED);
            LOG.log(Level.INFO, "MulticastDevice ID={0} created", device_id);
            
            multicastConnection.addDataListener(new MulticastDataListener() {
                @Override
                public void dataRecieved(MulticastConnection connection, InetAddress ip, MulticastDataMessage mm) {
                    if (mm.getDst() == device_id || mm.getDst() == Smarthome.ADDRESS_BROADCAST) {
                        switch (mm.getCommand()) {
                            case Smarthome.COMMAND_DISCOVERY:
                                
                                send(mm.getSrc(), Smarthome.COMMAND_DISCOVERY_RESPONSE, device_name.getBytes());
                                break;
                            case Smarthome.COMMAND_PING:
                                send(mm.getSrc(), Smarthome.COMMAND_PING_REPLY, mm.getData());
                                break;
                            //case CLUNET_COMMAND_REBOOT:
                            //  restart();
                            //  break;
                            default:
                                if (dataListener != null) {
                                    dataListener.dataRecieved(connection, ip, mm);
                                }
                        }
                    }
                }
            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while creating multicast device", e);
            
        }
    }
    
    public void close() {
        if (multicastConnection != null) {
            multicastConnection.close();
            LOG.log(Level.INFO, "MulticastDevice ID={0} closed", device_id);
        }
    }
    
}