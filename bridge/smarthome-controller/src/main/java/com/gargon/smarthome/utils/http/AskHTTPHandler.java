package com.gargon.smarthome.utils.http;

import com.gargon.smarthome.SmarthomeDictionary;
import com.gargon.smarthome.utils.DataFormat;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class AskHTTPHandler extends SupradinHTTPHandler {

    public static final String URI = "/ask";

    public static final String PARAM_RESPONSE_SRC = "rsrc";
    public static final String PARAM_RESPONSE_CMD = "rcmd";
    public static final String PARAM_RESPONSE_FORMAT = "rft";
    public static final String PARAM_RESPONSE_TIMEOUT = "rtm";

    public static final String[] PARAM_RESPONSE_FORMAT_TYPE = {"txt", "hex"};

    protected final AskHTTPCallback callback;

    public AskHTTPHandler(AskHTTPCallback callback) {
        super(URI);
        this.callback = callback;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = parseParams(t);

        String response = "Error while sending";
        int responseCode = 500;

        try {
            int dst = Integer.parseInt(params.get(PARAM_DST));
            int pri = Integer.parseInt(params.get(PARAM_PRI));
            int cmd = Integer.parseInt(params.get(PARAM_CMD));

            String hexData = "";
            if (params.containsKey(PARAM_DAT)
                    && !params.get(PARAM_DAT).isEmpty()) {
                hexData = params.get(PARAM_DAT);
            }

            int rsrc = Integer.parseInt(params.get(PARAM_RESPONSE_SRC));
            int rcmd = Integer.parseInt(params.get(PARAM_RESPONSE_CMD));

            String rft = PARAM_RESPONSE_FORMAT_TYPE[0];
            if (params.containsKey(PARAM_RESPONSE_FORMAT)
                    && !params.get(PARAM_RESPONSE_FORMAT).isEmpty()) {
                rft = params.get(PARAM_RESPONSE_FORMAT);
            }

            int rftCode = -1;
            for (int i = 0; i < PARAM_RESPONSE_FORMAT_TYPE.length; i++) {
                if (PARAM_RESPONSE_FORMAT_TYPE[i].equals(rft)) {
                    rftCode = i;
                    break;
                }
            }
            if (rftCode < 0) {
                throw new Exception("Wrong format type");
            }

            int rtm = 3000; //3 sec
            if (params.containsKey(PARAM_RESPONSE_TIMEOUT) 
                    && !params.get(PARAM_RESPONSE_TIMEOUT).isEmpty()) {
                rtm = Integer.parseInt(params.get(PARAM_RESPONSE_TIMEOUT));
            }

            //send command 
            try {
                byte[] rdata = callback.ask(dst, pri, cmd, DataFormat.hexToByteArray(hexData),
                        rsrc, rcmd, rtm);

                if (rdata != null) {
                    switch (rftCode) {
                        case 1:
                            response = DataFormat.bytesToHex(rdata);
                            break;
                        case 0:
                            String r = SmarthomeDictionary.toString(rcmd, rdata);
                            if (r == null) {
                                r = "";
                            }
                            response = r;
                            break;
                    }

                    responseCode = 200;
                }
            } catch (Exception e) {
            }

        } catch (Exception e) {
            response = "Wrong request format!"
                    + "\n\n\nusage: http://HOST:PORT" + URI + "?" + PARAM_DST + "={dst_id}&" + PARAM_PRI + "={priority}&"
                    + PARAM_CMD + "={command_id}[&" + PARAM_DAT + "={hex data}]&" + PARAM_RESPONSE_SRC + "={rsrc_id}&" + PARAM_RESPONSE_CMD + "={rcommand_id}[&"
                    + PARAM_RESPONSE_FORMAT + "={rformat_id}][&" + PARAM_RESPONSE_TIMEOUT + "={timeout_in_ms}]"
                    + "\n\ndst_id, rsrc_id = {";
            for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getDevicesList().entrySet()) {
                response += entry.getValue() + " (" + entry.getKey() + "), ";
            }

            response += "}";
            response += "\n\npriority = {";
            for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getPrioritiesList().entrySet()) {
                response += entry.getValue() + " (" + entry.getKey() + "), ";
            }
            response += "}";
            response += "\n\ncommand_id, rcommand_id = {";
            for (Map.Entry<Integer, String> entry : SmarthomeDictionary.getCommandsList().entrySet()) {
                response += entry.getValue() + " (" + entry.getKey() + "), ";
            }
            response += "}";
            response += "\n\nrformat_id = {";
            for (int i = 0; i < PARAM_RESPONSE_FORMAT_TYPE.length; i++) {
                response += PARAM_RESPONSE_FORMAT_TYPE[i] + ", ";
            }
            response += "}";

            responseCode = 400;
        }

        sendTextResponse(t, responseCode, response);
    }

}
