package com.gargon.smarthome.supradin.utils.logger.commands.filters;

import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class FilterFactory {

    public static Filter createFilter(JSONObject config) {
        if (config != null) {
            if (config.has("name")) {
                String filterName = config.optString("name").toLowerCase();
                switch (filterName) {
                    case "databytecmp":
                        return new DataByteCmp(config);
                    default:
                }
            }
        }
        return null;
    }
}
