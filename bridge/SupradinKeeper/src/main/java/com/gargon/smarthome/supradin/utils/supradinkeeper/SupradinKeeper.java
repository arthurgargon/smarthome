/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gargon.smarthome.supradin.utils.supradinkeeper;

import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.supradin.utils.supradinkeeper.entities.Sniff;
import com.gargon.smarthome.supradin.utils.supradinkeeper.entities.dao.SniffDAO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gargon
 */
public class SupradinKeeper {

    private static Logger logger = Logger.getLogger(SupradinKeeper.class.getName());

    public static void main(String[] args) {
        //establish connection
        SupradinConnection connection = new SupradinConnection();
        connection.open();
        connection.addDataListener(new SupradinDataListener() {

            DateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");

            @Override
            public void dataRecieved(SupradinDataMessage supradin) {

                try {
                    //                String src = DataFormat.byteToHex(supradin.getSrc());
//                String srcName = ClunetDictionary.getDeviceById(supradin.getSrc());
//                if (srcName != null) {
//                    src += " - " + srcName;
//                }
//
//                String rcv = DataFormat.byteToHex(supradin.getDst());
//                String rcvName = ClunetDictionary.getDeviceById(supradin.getDst());
//                if (rcvName != null) {
//                    rcv += " - " + rcvName;
//                }
//
//                String cmd = DataFormat.byteToHex(supradin.getCommand());
//                String cmdName = ClunetDictionary.getCommandById(supradin.getCommand());
//                if (cmdName != null) {
//                    cmd += " - " + cmdName;
//                }
//
//                String interpretation = ClunetDictionary.toString(supradin.getCommand(), supradin.getData());
                    
                    //add row
                    //model.addRow(new Object[]{sdf.format(new Date()), src, rcv, cmd, DataFormat.bytesToHex(supradin.getData()), interpretation});
                    //import org.apache.commons.lang.StringUtils;
                    //output.append("|\t" + StringUtils.rightPad(column1, 25) + "\t\t\t:\t" + StringUtils.rightPad(column2, 15) +"  \t\t\n");
                    //System.out.println(String.format("%s, %s, %s, %s", src, rcv, cmd, interpretation));
                    
                    Sniff sniff = new SniffDAO().createSniff(supradin.getSrc(), supradin.getDst(), supradin.getCommand(), supradin.getData(), ClunetDictionary.toString(supradin.getCommand(), supradin.getData()));
                } catch (Exception ex) {
                    Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        connection.connect();
    }

}
