package com.gargon.smarthome.logger.commands.readers;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class DataByte implements Reader {

    private int idByte = -1;
    private int valueByte = -1;

    public DataByte(JSONObject config) {
        if (config != null) {
            idByte = config.optInt("idByte", -1);
            valueByte = config.optInt("valueByte", -1);
        }

        if (idByte < 0 && valueByte < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Map<String, Object> perform(SupradinDataMessage supradin) {

        if (supradin.getData().length > idByte && supradin.getData().length > valueByte) {

            String id = null;
            if (idByte >= 0) {
                id = String.format("%02x", supradin.getData()[idByte] & 0xff);
            }

            Object value = null;
            if (valueByte >= 0) {
                value = supradin.getData()[valueByte];
            }

            Map<String, Object> r = new HashMap<>();
            r.put(id, value);

            return r;
        }
        return null;
    }

}
