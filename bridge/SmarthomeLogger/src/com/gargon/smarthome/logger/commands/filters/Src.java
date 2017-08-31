package com.gargon.smarthome.logger.commands.filters;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class Src implements Filter {

    private Integer eq = null;
    private Integer ne = null;

    public Src(JSONObject config) {
        if (config != null) {
            if (config.has("eq")) {
                try {
                    eq = config.getInt("eq");
                } catch (Exception e) {
                    eq = null;
                }
            } else if (config.has("ne")) {
                try {
                    ne = config.getInt("ne");
                } catch (Exception e) {
                    ne = null;
                }
            }
        }

        if (eq == null && ne == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean filter(SupradinDataMessage supradin) {
        if (eq != null) {
            return supradin.getSrc() == eq;
        } else if (ne != null) {
            return supradin.getSrc() != ne;
        }
        return false;
    }

}
