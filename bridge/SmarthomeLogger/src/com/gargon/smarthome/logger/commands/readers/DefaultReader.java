package com.gargon.smarthome.logger.commands.readers;

import com.gargon.smarthome.utils.DataFormat;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class DefaultReader implements Reader {

    @Override
    public Map<String, Object> perform(final SupradinDataMessage supradin) {
        return new HashMap<String, Object>(){{
            put(String.valueOf(supradin.getSrc()), DataFormat.bytesToHex(supradin.getData()));
        }};
    }

}
