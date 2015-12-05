package com.gargon.smarthome.supradin.utils.logger.commands;

import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class CommandFactory {

    public static Command createCommand(JSONObject config) {
        Command c = null;
        if (config != null) {

            String commandType = config.optString("type", "always");

            switch (commandType) {
                case "always":
                    c = new AlwaysCommand(config);
                    break;
                case "period":
                    c = new PeriodCommand(config);
                    break;
                case "change":
                    c = new ChangeCommand(config);
                    break;
            }

        }
        return c;
    }

}
