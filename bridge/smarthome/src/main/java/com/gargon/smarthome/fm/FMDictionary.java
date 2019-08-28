package com.gargon.smarthome.fm;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author gargon
 */
public class FMDictionary {

    private Map<Float, String> stationList = null;

    private static FMDictionary instance;

    private FMDictionary(Map<Float, String> stationList) {
        if (stationList != null) {
            this.stationList = new TreeMap<>(stationList);
        }
    }

    public static synchronized void init(Map<Float, String> stationList) {
        if (instance == null) {
            instance = new FMDictionary(stationList);
        }
    }

    public static FMDictionary getInstance() {
        return instance;
    }

    public Map<Float, String> getStationList() {
        return stationList;
    }

}
