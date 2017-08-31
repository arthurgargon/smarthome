package com.gargon.smarthome.logger.commands.readers;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.util.Map;

/**
 *
 * @author gargon
 */
public interface Reader {
    
    public Map<String, Object> perform(SupradinDataMessage supradin);
    
}
