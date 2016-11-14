package com.gargon.smarthome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author gargon
 */
public class HeatFloorDictionary {
    
    private Map<Integer, HeatfloorProgram> programList = null;
    private Map<String, HeatfloorChannel> channelList = null;
    
    private static HeatFloorDictionary instance;
    
    private HeatFloorDictionary(Map<Integer, HeatfloorProgram> programList, Map<String, HeatfloorChannel> channelList) {
        if (programList != null) {
            this.programList = new TreeMap<>(programList);
        }
        if (channelList != null){
            this.channelList = new HashMap<>(channelList);
        }
    }
    
    public static synchronized void init(Map<Integer, HeatfloorProgram> programList, Map<String, HeatfloorChannel> channelList){
        if (instance == null){
            instance = new HeatFloorDictionary(programList, channelList);
        }
    }
    
    public static HeatFloorDictionary getInstance(){
        return instance;
    }

    public Map<Integer, HeatfloorProgram> getProgramList() {
        return programList;
    }

    public Map<String, HeatfloorChannel> getChannelList() {
        return channelList;
    }
    
}
