package com.gargon.smarthome.supradin.utils;

import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.clunet.utils.DataFormat;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author gargon
 */
public class SupradinLogger {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //establish connection
        SupradinConnection connection = new SupradinConnection();
        connection.open();
        connection.addDataListener(new SupradinDataListener() {

            DateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");

            @Override
            public void dataRecieved(SupradinDataMessage supradin) {

                String src = DataFormat.byteToHex(supradin.getSrc());
                String srcName = ClunetDictionary.getDeviceById(supradin.getSrc());
                if (srcName != null) {
                    src += " - " + srcName;
                }

                String rcv = DataFormat.byteToHex(supradin.getDst());
                String rcvName = ClunetDictionary.getDeviceById(supradin.getDst());
                if (rcvName != null) {
                    rcv += " - " + rcvName;
                }

                String cmd = DataFormat.byteToHex(supradin.getCommand());
                String cmdName = ClunetDictionary.getCommandById(supradin.getCommand());
                if (cmdName != null) {
                    cmd += " - " + cmdName;
                }

                String interpretation = ClunetDictionary.toString(supradin.getCommand(), supradin.getData());

                //add row
                //model.addRow(new Object[]{sdf.format(new Date()), src, rcv, cmd, DataFormat.bytesToHex(supradin.getData()), interpretation});
                
                //import org.apache.commons.lang.StringUtils;
                //output.append("|\t" + StringUtils.rightPad(column1, 25) + "\t\t\t:\t" + StringUtils.rightPad(column2, 15) +"  \t\t\n");
                
                System.out.println(String.format("%s, %s, %s, %s", src, rcv, cmd, interpretation));

            }
        });
        connection.connect();
        
    }
    
}
