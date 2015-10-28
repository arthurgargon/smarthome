package com.gargon.smarthome.supradin.utils.config.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class CommandBuilder {
    
    public static Map<Integer, Command> build(JSONObject config){
        Map<Integer, Command> commands = null;
        if (config != null){
            commands = new HashMap();
            for (String key : config.keySet()){
                try{
                    Integer commandId = Integer.parseInt(key);
                    
                    JSONObject commandJSON = config.getJSONObject(key);
                    String commandType = commandJSON.optString("type", "always");
                    
                    Command c = null;
                    switch (commandType){
                        case "always":
                            break;
                        case "period":
                            break;
                        case "change":
                            break;
                    }
                    
                    if (c != null){
                        commands.put(commandId, c);
                    }
                    
                }catch(Exception e){
                    Logger.getLogger(CommandBuilder.class.getName()).log(Level.WARNING, "Can't read non integer command id", e);
                }
            }
        }
        return commands;
    }
    
}
