package com.gargon.smarthome.trigger;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.utils.DataFormat;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class TriggerTask {

    private int src_id;
    private int dst_id;
    private int cmd_id;
    private String data;
    private String task;

    private static final String KEY_SRC = "src";
    private static final String KEY_DST = "dst";
    private static final String KEY_CMD = "cmd";
    private static final String KEY_DATA = "data";
    private static final String KEY_TASK = "task";

    public static final TriggerTask parseJson(JSONObject config) {
        TriggerTask tt = new TriggerTask();
        tt.src_id = config.optInt(KEY_SRC, -1);
        tt.dst_id = config.optInt(KEY_DST, -1);
        tt.cmd_id = config.optInt(KEY_CMD, -1);
        tt.data = config.optString(KEY_DATA, null);
        tt.task = config.optString(KEY_TASK, null); //use empty String for empty data, or null -> for no matter data
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

    public String getTask() {
        return task;
    }

    public boolean match(SupradinDataMessage message) {
        if (message != null) {
            return (src_id == -1 || src_id == message.getSrc())
                    && (dst_id == -1 || dst_id == message.getDst())
                    && (cmd_id == -1 || cmd_id == message.getCommand())
                    && (data == null || data.equals(DataFormat.bytesToHex(message.getData())));
        }
        return false;
    }
}
