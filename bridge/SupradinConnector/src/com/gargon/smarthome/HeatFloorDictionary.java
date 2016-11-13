package com.gargon.smarthome;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author gargon
 */
public class HeatFloorDictionary {
    
    private Map<Integer, Integer[]> programList = null;
    
    private static HeatFloorDictionary instance;
    
    private HeatFloorDictionary(Map<Integer, Integer[]> programList) {
        if (programList != null) {
            this.programList = new TreeMap<>(programList);
        }
    }
    
    public static synchronized void init(Map<Integer, Integer[]> programList){
        if (instance == null){
            instance = new HeatFloorDictionary(programList);
        }
    }
    
    public static HeatFloorDictionary getInstance(){
        return instance;
    }

    public Map<Integer, Integer[]> getProgramList() {
        return programList;
    }
    
}
