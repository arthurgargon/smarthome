package com.gargon.smarthome.clunet;

import com.gargon.smarthome.enums.Address;
import com.gargon.smarthome.enums.Command;
import com.gargon.smarthome.enums.Priority;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author gargon
 */
public class ClunetDateTimeResolver implements SupradinDataListener {

    @Override
    public void dataRecieved(SupradinConnection connection, SupradinDataMessage message) {
        if (message != null && message.getCommand() == Command.TIME
                && (message.getDst() == Address.SUPRADIN || message.getDst() == Address.BROADCAST)) {

            GregorianCalendar c = new GregorianCalendar();
            int dw = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (dw == 0) {
                dw = 7;
            }
            connection.sendData(new SupradinDataMessage(message.getSrc(), Priority.INFO, Command.TIME_INFO,
                    new byte[]{
                        (byte) (c.get(Calendar.YEAR) - 2000),
                        (byte) (c.get(Calendar.MONTH) + 1),
                        (byte) c.get(Calendar.DAY_OF_MONTH),
                        (byte) c.get(Calendar.HOUR_OF_DAY),
                        (byte) c.get(Calendar.MINUTE),
                        (byte) c.get(Calendar.SECOND),
                        (byte) dw}));
        }
    }

}
