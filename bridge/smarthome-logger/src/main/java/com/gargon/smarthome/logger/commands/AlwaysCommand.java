package com.gargon.smarthome.logger.commands;

import com.gargon.smarthome.logger.RealTimeSupradinDataMessage;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class AlwaysCommand extends Command{

    public AlwaysCommand(JSONObject config) {
        super(config);
    }

    @Override
    public boolean approve(RealTimeSupradinDataMessage message) {
        return filter(message);
    }
    
}
