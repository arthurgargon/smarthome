package com.gargon.smarthome.supradin.utils.config.commands.readers;

import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class ReaderFactory {

    public static Reader createFilter(JSONObject config) {
        if (config != null) {
            if (config.has("name")) {
                String readerName = config.optString("name").toLowerCase();
                switch (readerName) {
                    case "temperature":
                        return new Temperature();
                    case "onewire":
                        return new OneWire();
                    case "databyte":
                        return new DataByte(config);
                    case "databytes":
                        return new DataBytes(config);
                    case "heatfloor":
                        return new Heatfloor();
                    default:
                }
            }
        }
        return null;
    }
}
