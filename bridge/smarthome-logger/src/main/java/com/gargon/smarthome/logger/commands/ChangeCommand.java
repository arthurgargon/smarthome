package com.gargon.smarthome.logger.commands;

import com.gargon.smarthome.logger.RealTimeSupradinDataMessage;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class ChangeCommand extends Command {

    private final Map<String, Object> lastValues = new HashMap();

    public ChangeCommand(JSONObject config) {
        super(config);
    }

    @Override
    public boolean approve(RealTimeSupradinDataMessage message) {
        boolean approve = false;
        if (filter(message)) {
                Map<String, Object> v = read(message);
            for (Map.Entry<String, Object> entry : v.entrySet()) {
                String key = message.getSrc() + ";" + entry.getKey();
                if (lastValues.containsKey(key)) {
                    if (!lastValues.get(key).equals(entry.getValue())) {
                        approve = true;
                    }
                } else {
                    approve = true;
                }
                lastValues.put(key, entry.getValue());
            }
        }
        return approve;
    }

}
