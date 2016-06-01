package com.gargon.smarthome.supradin.utils.http;

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

    public static final String PARAM_DST = "dst";
    public static final String PARAM_PRI = "pri";
    public static final String PARAM_CMD = "cmd";
    public static final String PARAM_DAT = "dat";
    
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
    
    protected void sendResponse(HttpExchange t, int responseCode, String response) throws IOException {
        String encoding = "UTF-8";
        t.getResponseHeaders().set("Content-Type", "text/plain; charset=" + encoding);
        t.sendResponseHeaders(responseCode, 0);

        try (Writer out = new OutputStreamWriter(t.getResponseBody(), encoding)) {
            out.write(response);
        }
    }
    
}
