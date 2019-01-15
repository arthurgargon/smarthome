package com.gargon.smarthome.trigger;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class TriggerMessage {

    private int src_id;
    private int dst_id;
    private int cmd_id;
    private String data;

    private static final String KEY_SRC = "src";
    private static final String KEY_DST = "dst";
    private static final String KEY_CMD = "cmd";
    private static final String KEY_DATA = "data";

    public static final TriggerMessage parseJson(JSONObject config) {
        TriggerMessage tt = new TriggerMessage();
        tt.src_id = config.optInt(KEY_SRC, -1);
        tt.dst_id = config.optInt(KEY_DST, -1);
        tt.cmd_id = config.optInt(KEY_CMD, -1);
        tt.data = config.optString(KEY_DATA, null); //use empty String for empty data, or null -> for no matter data
        return tt;
    }

    public int getSrc_id() {
        return src_id;
    }

    public int getDst_id() {
        return dst_id;
    }

    public int getCmd_id() {
        return cmd_id;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        List<String> s = new ArrayList();
        if (src_id >= 0) {
            s.add("src_id=" + src_id);
        }
        if (dst_id >= 0) {
            s.add("dst_id=" + dst_id);
        }
        if (cmd_id >= 0) {
            s.add("cmd_id=" + cmd_id);
        }
        if (data != null) {
            s.add("data=" + data);
        }
        return String.join("; ", s);
    }
    
    

}
