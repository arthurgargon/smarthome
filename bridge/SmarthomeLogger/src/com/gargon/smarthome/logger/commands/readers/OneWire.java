package com.gargon.smarthome.logger.commands.readers;

import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class OneWire implements Reader{

    @Override
    public Map<String, Object> perform(SupradinDataMessage supradin) {

        List<String> v = ClunetDictionary.oneWireInfo(supradin.getData());
        if (v != null) {
            Map<String, Object> r = new HashMap();
            for (String o : v) {
                r.put(o, o);
            }
            return r;
        }
        return null;
    }

}
