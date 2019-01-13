package com.gargon.smarthome.logger.commands.filters;

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
                    case "notaresponse":
                        return new NotAResponse();
                    case "dst":
                        return new Dst(config);
                    case "src":
                        return new Src(config);
                    default:
                }
            }
        }
        return null;
    }
}
