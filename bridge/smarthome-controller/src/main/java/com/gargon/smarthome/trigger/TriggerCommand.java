package com.gargon.smarthome.trigger;

import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class TriggerCommand {
    
    private String scriptCommand;
    private TriggerMessage messageCommand;
    
    private static final String KEY_TASK = "task";
    
    public static final TriggerCommand parseJson(JSONObject config) {
        TriggerCommand tc = new TriggerCommand();

        JSONObject obj = config.optJSONObject(KEY_TASK);
        if (obj != null) {
            tc.messageCommand = TriggerMessage.parseJson(obj);
        } else {
            tc.scriptCommand = config.optString(KEY_TASK, null); //use empty String for empty data, or null -> for no matter data
        }
        return tc;
    }
    
    public boolean hasCommand(){
        return scriptCommand != null || messageCommand != null;
    }

    public String getScriptCommand() {
        return scriptCommand;
    }

    public TriggerMessage getMessageCommand() {
        return messageCommand;
    }

    @Override
    public String toString() {
        if (hasCommand()){
        if (scriptCommand != null){
            return scriptCommand;
        }else{
            return messageCommand.toString();
        }
        }
        return null;
    }
    
}
