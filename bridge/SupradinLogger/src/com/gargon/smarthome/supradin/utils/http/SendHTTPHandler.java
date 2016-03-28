package com.gargon.smarthome.supradin.utils.http;

import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.clunet.utils.DataFormat;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class SendHTTPHandler implements HttpHandler {

    private static final String PARAM_DST = "dst";
    private static final String PARAM_PRI = "pri";
    private static final String PARAM_CMD = "cmd";
    private static final String PARAM_DAT = "dat";

    private final SendCallback callback;
    
    public SendHTTPHandler(SendCallback callback) {
        this.callback = callback;
    }
    
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = new HashMap<String, String>();

        String query = t.getRequestURI().getQuery();
        if (query != null) {
            try {
                for (String param : query.split("&")) {
                    String pair[] = param.split("=");
                    if (pair.length > 1) {
                        params.put(pair[0], pair[1]);
                    } else {
                        params.put(pair[0], "");
                    }
                }
            } catch (Exception e) {
                params.clear();
                //wrong query uri
            }
        }

        //try to parse params
        String response;
        try {
            int dst = Integer.parseInt(params.get(PARAM_DST));
            int pri = Integer.parseInt(params.get(PARAM_PRI));
            int cmd = Integer.parseInt(params.get(PARAM_CMD));
            String hexData = "";
            if (params.containsKey(PARAM_DAT)
                    && !params.get(PARAM_DAT).isEmpty()) {
                hexData = params.get(PARAM_DAT);
            }

            //send command 
            try {
                callback.send(dst, pri, cmd, DataFormat.hexToByteArray(hexData));
                response = "Ok";
            } catch (Exception e) {
                response = "Error while sending";
            }

        } catch (Exception e) {
            response = "Wrong request format:"+
                    "\n\n\nusage: http://HOST:PORT"+t.getRequestURI().getPath()+"?"+PARAM_DST+"={dst_id}&"+PARAM_PRI+"={priority}&"+PARAM_CMD+"={command_id}[&"+PARAM_DAT+"={hex data}]"+
                    "\n\ndst_id = {";
            for (Map.Entry<Integer,String> entry : ClunetDictionary.getDevicesList().entrySet()){
                response += entry.getValue()+" (" + entry.getKey() + "), ";
            }
            
            response += "}";
            response += "\n\npriority = {";
            for (Map.Entry<Integer,String> entry : ClunetDictionary.getPrioritiesList().entrySet()){
                response += entry.getValue()+" (" + entry.getKey() + "), ";
            }
            response += "}";
            response += "\n\ncommand_id = {";
            for (Map.Entry<Integer,String> entry : ClunetDictionary.getCommandsList().entrySet()){
                response += entry.getValue()+" (" + entry.getKey() + "), ";
            }
            response += "}";
        }

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }

}
