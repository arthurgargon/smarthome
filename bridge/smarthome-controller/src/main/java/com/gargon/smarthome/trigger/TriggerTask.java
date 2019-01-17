package com.gargon.smarthome.trigger;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.utils.DataFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    /**
     * Проверяет соответствие <code>test_data</code> сообщения
     * шаблону регулярного выражения <code>pattern_data</code>
     * 
     * @param pattern_data
     * @param test_data
     * @return null, если соответствия нет; список найденных групп,
     * если соответствие есть
     */
    private List<String> check_data(String pattern_data, byte[] test_data) {
        if (pattern_data == null) {
            return new ArrayList();
        }

        String test_hex = DataFormat.bytesToHex(test_data);
        if (test_hex == null) {
            return null;
        }

        Pattern p = Pattern.compile(pattern_data);
        Matcher m = p.matcher(test_hex);

        if (m.matches()) {
            m.reset();
            List<String> groups = new ArrayList();
            if (m.find()) {
                for (int i = 0; i <= m.groupCount(); i++) {
                    groups.add(m.group(i));
                }
            }
            return groups;
        } else {
            return null;
        }
    }
    
    public List<String> match(SupradinDataMessage m) {
        if (m != null) {

            if ((message.getSrc_id() == -1 || message.getSrc_id() == m.getSrc())
                    && (message.getDst_id() == -1 || message.getDst_id() == m.getDst())
                    && (message.getCmd_id() == -1 || message.getCmd_id() == m.getCommand())) {
                return check_data(message.getData(), m.getData());
            }
        }
        return null;
    }
}
