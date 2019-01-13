package com.gargon.smarthome;

import com.gargon.smarthome.clunet.Clunet;
import com.gargon.smarthome.clunet.ClunetDateTimeResolver;
import com.gargon.smarthome.devices.dial.RotaryDial;
import com.gargon.smarthome.devices.dial.RotaryDialListener;
import com.gargon.smarthome.multicast.MulticastConnection;
import com.gargon.smarthome.multicast.MulticastDataListener;
import com.gargon.smarthome.multicast.messages.MulticastDataMessage;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gargon
 */
public class SmarthomeRotaryDial {

    private static final int PIN_CRADLE = 14;
    private static final int PIN_DIAL_ACTIVATE = 13;
    private static final int PIN_DIAL_IMPULLSE = 12;
    
    private static final int BUTTON_ID = 5;
    
    private static MulticastConnection multicastConnection;
    private static RotaryDial dial;

    private static final Logger LOG = Logger.getLogger(SmarthomeRotaryDial.class.getName());

    private static void buttonResponse(boolean pushed) {
        multicastConnection.sendData(new MulticastDataMessage(Clunet.ADDRESS_BROADCAST, Clunet.ADDRESS_TELEPHONE, Clunet.COMMAND_BUTTON_INFO,
                new byte[]{(byte) BUTTON_ID, (byte) (pushed ? 0x01 : 0x00)}));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            multicastConnection = new MulticastConnection();
            multicastConnection.open();
            
            multicastConnection.addDataListener(new MulticastDataListener() {
                @Override
                public void dataRecieved(MulticastConnection connection, InetAddress ip, MulticastDataMessage mm) {
                    if (mm.getCommand() == Clunet.COMMAND_BUTTON
                            && mm.getDst() == Clunet.ADDRESS_BROADCAST){
                        buttonResponse(!dial.isCradle_state());
                    }
                }
            });
            
            LOG.log(Level.INFO, "Connection to supradin established");

            dial = new RotaryDial(PIN_CRADLE, PIN_DIAL_ACTIVATE, PIN_DIAL_IMPULLSE);
            dial.addListener(new RotaryDialListener() {
                @Override
                public void handleRotaryDialEvent(RotaryDial.EVENT event, byte... args) {
                    switch (event) {
                        case PICKED_UP:
                            LOG.log(Level.INFO, "Picked up");
                            buttonResponse(false);
                            break;
                        case PUT_DOWN:
                            LOG.log(Level.INFO, "Put down");
                            buttonResponse(true);
                            break;
                        case NUMBER_DIALED:
                            if (args != null) {
                                LOG.log(Level.INFO, "Number dialed: {0}", Arrays.toString(args));
                                multicastConnection.sendData(new MulticastDataMessage(Clunet.ADDRESS_BROADCAST, Clunet.ADDRESS_TELEPHONE, Clunet.COMMAND_ROTARY_DIAL_NUMBER_INFO,
                                        args));
                            }
                            break;
                    }
                }
            });

            dial.listen();
            LOG.log(Level.INFO, "Rotary dial started to listen");

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
if (multicastConnection != null) {
                        multicastConnection.close();
                    }

                    LOG.log(Level.INFO, "Application closed");
                }
            });

        } catch (Exception e) {

            if (multicastConnection != null) {
                        multicastConnection.close();
                    }
            LOG.log(Level.SEVERE, "initialization error", e);

        }
    }

}
