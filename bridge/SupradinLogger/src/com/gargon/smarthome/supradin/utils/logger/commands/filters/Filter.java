package com.gargon.smarthome.supradin.utils.logger.commands.filters;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

/**
 *
 * @author gargon
 */
public interface Filter {
    
    public boolean filter(SupradinDataMessage supradin);
    
}
