package com.gargon.smarthome.trigger;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.utils.DataFormat;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class TriggerTask {

    private TriggerMessage message;
    private TriggerCommand command;

    

    public static final TriggerTask parseJson(JSONObject config) {
        TriggerTask tt = new TriggerTask();
        tt.message = TriggerMessage.parseJson(config);
        tt.command = TriggerCommand.parseJson(config);
        
        return tt;
    }

    public TriggerMessage getMessage() {
        return message;
    }

    public TriggerCommand getCommand() {
        return command;
    }

    public boolean match(SupradinDataMessage m) {
        if (m != null) {
            return (message.getSrc_id() == -1 || message.getSrc_id() == m.getSrc())
                    && (message.getDst_id() == -1 || message.getDst_id() == m.getDst())
                    && (message.getCmd_id() == -1 || message.getCmd_id() == m.getCommand())
                    && (message.getData() == null || message.getData().equals(DataFormat.bytesToHex(m.getData())));
        }
        return false;
    }
}
