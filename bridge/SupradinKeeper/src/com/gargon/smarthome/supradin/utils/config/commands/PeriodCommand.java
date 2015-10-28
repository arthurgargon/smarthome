package com.gargon.smarthome.supradin.utils.config.commands;

import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class PeriodCommand extends Command{

    private int period;                         //required
    private int uniqueCount = -1;
    private boolean triggerRequired = false;
    
    private Trigger trigger = null;             //required
    
    public PeriodCommand(JSONObject config) {
        super(config);
    }
    
}

class Trigger{
    
    private int command;
    private byte[] data;
    
    public Trigger(JSONObject config){
        
    }
}