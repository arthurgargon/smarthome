package com.gargon.smarthome.utils.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gargon
 */
public abstract class SupradinHTTPHandler implements HttpHandler {

    protected static final String PARAM_DST = "dst";
    protected static final String PARAM_PRI = "pri";
    protected static final String PARAM_CMD = "cmd";
    protected static final String PARAM_DAT = "dat";
    
    protected String uri;

    public SupradinHTTPHandler(String uri) {
        this.uri = uri;
    }
    
    protected Map<String, String> parseParams(HttpExchange t) {
        Map<String, String> params = new HashMap();

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
        return params;
    }
   
    private void sendResponse(HttpExchange t, int responseCode, String response, String contentType) throws IOException {
        t.getResponseHeaders().set("Content-Type", contentType);
        t.sendResponseHeaders(responseCode, 0);

        try (Writer out = new OutputStreamWriter(t.getResponseBody(), "UTF-8")) {
            out.write(response);
        }
    }

    protected void sendTextResponse(HttpExchange t, int responseCode, String response) throws IOException {
        sendResponse(t, responseCode, response, "text/plain; charset=UTF-8");
    }

    protected void sendHtmlResponse(HttpExchange t, int responseCode, String response) throws IOException {
        sendResponse(t, responseCode, response, "text/html; charset=UTF-8");
    }
    
}
