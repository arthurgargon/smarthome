package com.gargon.smarthome.supradin.utils.logger.commands.filters;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class DataByteCmp implements Filter {

    private int byteNum = -1;

    private Byte eq = null;
    private Byte ne = null;

    public DataByteCmp(JSONObject config) {
        if (config != null) {
            byteNum = config.optInt("byte", -1);
            if (config.has("eq")) {
                try {
                    eq = (byte) config.getInt("eq");
                } catch (Exception e) {
                    eq = null;
                }
            } else if (config.has("ne")) {
                try {
                    ne = (byte) config.getInt("ne");
                } catch (Exception e) {
                    ne = null;
                }
            }
        }

        if ((eq == null && ne == null) || byteNum < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean filter(SupradinDataMessage supradin) {
        if (supradin.getData().length > byteNum) {
            if (eq != null) {
                return supradin.getData()[byteNum] == eq;
            } else if (ne != null) {
                return supradin.getData()[byteNum] != ne;
            }
        }
        return false;
    }

}
