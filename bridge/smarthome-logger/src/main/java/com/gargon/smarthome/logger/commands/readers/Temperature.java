package com.gargon.smarthome.logger.commands.readers;

import com.gargon.smarthome.SmarthomeDictionary;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class Temperature implements Reader {

    @Override
    public Map<String, Object> perform(SupradinDataMessage supradin) {
        Map<String, Float> v = SmarthomeDictionary.temperatureInfo(supradin.getData());
        if (v != null) {
            return (Map) v;
        }
        return null;
    }

}
