package com.gargon.smarthome.logger.commands.readers;

import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class Heatfloor implements Reader {

    @Override
    public Map<String, Object> perform(SupradinDataMessage supradin) {
        Map<String, Integer> v = ClunetDictionary.heatfloorInfo(supradin.getData()); 
        if (v != null) {
            return (Map) v;
        }
        return null;
    }

}
