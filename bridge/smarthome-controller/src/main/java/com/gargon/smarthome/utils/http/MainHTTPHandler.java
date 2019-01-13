package com.gargon.smarthome.utils.http;

import com.gargon.smarthome.Smarthome;
import com.gargon.smarthome.SmarthomeDictionary;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class MainHTTPHandler extends SupradinHTTPHandler {
    
    public static final String URI = "/";
    
    public MainHTTPHandler() {
        super(URI);
    }
    
    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = "<http>\n<body>";
        response += "\n<form action=\""+AskHTTPHandler.URI+"\" method=\"GET\">";
        response += "\n<b>ASK:</b><br/>";
        
        response += "\n"+PARAM_DST+"*:";
        response += "\n<select name=\""+PARAM_DST+"\">";
        for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getDevicesList().entrySet()) {
            response += "<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>";
        }
        response += "\n</select><br/>";
        
        response += "\n"+PARAM_PRI+"*:";
        response += "\n<select name=\""+PARAM_PRI+"\">";
        for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getPrioritiesList().entrySet()) {
            response += "<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>";
        }
        response += "\n</select><br/>";
        
        response += "\n"+PARAM_CMD+"*:";
        response += "\n<select name=\""+PARAM_CMD+"\">";
        for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getCommandsList().entrySet()) {
            response += "<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>";
        }
        response += "\n</select><br/>";
        
        response += "\n"+PARAM_DAT+":";
        response += "\n<input name=\""+PARAM_DAT+"\"><br/>";
        
        response += "\n"+AskHTTPHandler.PARAM_RESPONSE_SRC+"*:";
        response += "\n<select name=\""+AskHTTPHandler.PARAM_RESPONSE_SRC+"\">";
        for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getDevicesList().entrySet()) {
            if (entry.getKey() != Smarthome.ADDRESS_BROADCAST){
                response += "<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>";
            }
        }
        response += "\n</select><br/>";
        
        response += "\n"+AskHTTPHandler.PARAM_RESPONSE_CMD+"*:";
        response += "\n<select name=\""+AskHTTPHandler.PARAM_RESPONSE_CMD+"\">";
        for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getCommandsList().entrySet()) {
                response += "<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>";
        }
        response += "\n</select><br/>";
        
        response += "\n"+AskHTTPHandler.PARAM_RESPONSE_FORMAT+"*:";
        response += "\n<select name=\""+AskHTTPHandler.PARAM_RESPONSE_FORMAT+"\">";
        for (String f : AskHTTPHandler.PARAM_RESPONSE_FORMAT_TYPE){
            response += "<option value=\""+f+"\">"+f+"</option>";
        }
        response += "\n</select><br/>";
        
        response += "\n"+AskHTTPHandler.PARAM_RESPONSE_TIMEOUT+":";
        response += "\n<input name=\""+AskHTTPHandler.PARAM_RESPONSE_TIMEOUT+"\">ms<br/>";
        
        response += "<input type=\"submit\" value=\"ASK\"/>";
        
        response += "\n</form><br/><br/>";
        
        
        //SEND
        
        response += "\n<form action=\""+SendHTTPHandler.URI+"\" method=\"GET\">";
        response += "\n<b>SEND:</b><br/>";
        
        response += "\n"+PARAM_DST+"*:";
        response += "\n<select name=\""+PARAM_DST+"\">";
        for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getDevicesList().entrySet()) {
            response += "<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>";
        }
        response += "\n</select><br/>";
        
        response += "\n"+PARAM_PRI+"*:";
        response += "\n<select name=\""+PARAM_PRI+"\">";
        for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getPrioritiesList().entrySet()) {
            response += "<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>";
        }
        response += "\n</select><br/>";
        
        response += "\n"+PARAM_CMD+"*:";
        response += "\n<select name=\""+PARAM_CMD+"\">";
        for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getCommandsList().entrySet()) {
            response += "<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>";
        }
        response += "\n</select><br/>";
        
        response += "\n"+PARAM_DAT+":";
        response += "\n<input name=\""+PARAM_DAT+"\"><br/>";
        
        response += "<input type=\"submit\" value=\"SEND\"/>";
        
        response += "\n</form><br/><br/>";
        
        response += "\n</body></http>";
        sendHtmlResponse(t, 200, response);
    }

}
