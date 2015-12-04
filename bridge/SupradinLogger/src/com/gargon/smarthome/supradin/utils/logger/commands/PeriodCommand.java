package com.gargon.smarthome.supradin.utils.logger.commands;

import com.gargon.smarthome.clunet.Clunet;
import com.gargon.smarthome.clunet.utils.DataFormat;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.utils.logger.RealTimeSupradinDataMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class PeriodCommand extends Command {

    private final Map<String, RealTimeSupradinDataMessage> lastValues = new HashMap();

    private int period;                         //required
    private int uniqueCount = -1;
    private int triggerRequired = -1;

    private Trigger trigger = null;             //required

    public PeriodCommand(JSONObject config) {
        super(config);

        try {
            period = config.getInt("period");
        } catch (Exception e) {
            throw new IllegalArgumentException("wrong format of 'period' attribute in command of period type");
        }

        try {
            trigger = new Trigger(config.optJSONObject("trigger"));
        } catch (Exception e) {
            trigger = null;
            throw new IllegalArgumentException("wrong format of 'trigger' attribute in command of period type");
        }

        triggerRequired = config.optInt("triggerRequired", -1);
        uniqueCount = config.optInt("uniqueCount", -1);

        if (triggerRequired <= 0 && uniqueCount < 0) {
            throw new IllegalArgumentException("wrong combination of triggerRequired and uniqueCount attributes in command of period type");
        }
    }

    public long getPeriod() {
        return period * 1000;
    }
    
    public int getTriggerTimeout(){
        return trigger.getTimeout();
    }

    /**
     *
     * @param message
     * @return всегда возвращает false. Сообщения отдаются в отдельном методе по
     * истечению периода. В данном методе только отбираются наиболее актуальные
     * (поступившие последними)
     */
    @Override
    public boolean approve(RealTimeSupradinDataMessage message) {
        if (filter(message)) {
            Map<String, Object> v = read(message);
            try {
                if (v != null) {
                    for (Map.Entry<String, Object> entry : v.entrySet()) {
                        String key = message.getSrc() + ";" + entry.getKey();
                        lastValues.put(key, message);
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(PeriodCommand.class.getName()).log(Level.SEVERE, "reader format error", e);
            }
        }
        return false;
    }

    
    public void startTrigger(SupradinConnection connection){
        if (triggerRequired > 0 || lastValues.size() < uniqueCount) {
            Clunet.send(connection, Clunet.ADDRESS_BROADCAST, Clunet.PRIORITY_MESSAGE,
                    (byte) trigger.getCommand(), trigger.getData());

        }
    }
    
    public List<RealTimeSupradinDataMessage> timeout() {
        List<RealTimeSupradinDataMessage> messages = new ArrayList();
        for (Map.Entry<String, RealTimeSupradinDataMessage> entry : lastValues.entrySet()) {
            if (messages.indexOf(entry.getValue()) < 0) {
                messages.add(entry.getValue());
            }
        }

        lastValues.clear(); //сообщения отдали -> всю историю почистили

        return messages;
    }

}

class Trigger {

    private final int command;
    private final int timeout;      //время ожидания ответов на запрос тригера (мс)
    private final byte[] data;

    public Trigger(JSONObject config) {
        command = config.getInt("command");
        timeout = config.optInt("timeout", 1000);
        if (config.has("data")) {
            data = DataFormat.hexToByteArray(config.getString("data"));
        } else {
            data = new byte[]{};
        }
    }

    public int getCommand() {
        return command;
    }

    public int getTimeout() {
        return timeout;
    }

    public byte[] getData() {
        return data;
    }
}
