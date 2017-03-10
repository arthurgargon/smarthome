package supradinmulticastbridge;

import com.gargon.smarthome.clunet.Clunet;
import com.gargon.smarthome.multicast.MulticastConnection;
import com.gargon.smarthome.multicast.MulticastDataListener;
import com.gargon.smarthome.multicast.messages.MulticastDataMessage;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gargon
 */
public class SupradinMulticastBridge {

    private static SupradinConnection supradinConnection;
    private static MulticastConnection multicastConnection;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            supradinConnection = new SupradinConnection();
            multicastConnection = new MulticastConnection();

            supradinConnection.addDataListener(new SupradinDataListener() {
                @Override
                public void dataRecieved(SupradinConnection connection, SupradinDataMessage sm) {
                    //ip может быть только у супрадин, все остальные
                    //приходят назад, зацикливаясь из мультикаст сети
                    if (sm.getIp()==0 || sm.getSrc()==Clunet.ADDRESS_SUPRADIN){
                        multicastConnection.sendData(new MulticastDataMessage(sm.getDst(), sm.getSrc(), sm.getCommand(), sm.getData()));
                        //System.out.println("supradin recieved: " + message.toString());
                    }
                }
            });

            multicastConnection.addDataListener(new MulticastDataListener() {
                @Override
                public void dataRecieved(MulticastConnection connection, InetAddress ip, MulticastDataMessage mm) {
                    
                    int ip4 = 0;
                    if (ip != null) {
                        for (int i=ip.getAddress().length-1; i>=0; i--) {
                            ip4 = ip4 << 8 | (ip.getAddress()[i] & 0xFF);
                        }
                    }

                    //отправляет в супрадин сообщение с IP адерсом отправителя из мультикаст-сети
                    supradinConnection.sendData(new SupradinDataMessage(ip4, mm.getDst(), mm.getSrc(), mm.getCommand(), mm.getData()));
                    //System.out.println("multicast recieved: " + message.toString());
                }
            });

            multicastConnection.open();
            supradinConnection.open();
            supradinConnection.connect();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (supradinConnection != null) {
                        supradinConnection.close();
                    }

                    if (multicastConnection != null) {
                        multicastConnection.close();
                    }

                    Logger.getLogger(SupradinMulticastBridge.class.getName()).log(Level.INFO, "SupradinMulticastBridge closed");
                }
            });

        } catch (Exception e) {

            if (supradinConnection != null) {
                supradinConnection.close();
            }

            if (multicastConnection != null) {
                multicastConnection.close();
            }

            Logger.getLogger(SupradinMulticastBridge.class.getName()).log(Level.SEVERE, "initialization error", e);
        }
    }

}
