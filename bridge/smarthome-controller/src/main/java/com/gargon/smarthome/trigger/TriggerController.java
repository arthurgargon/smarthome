package com.gargon.smarthome.trigger;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class TriggerController {

    private List<TriggerTask> tasks = new ArrayList();

    private static final Logger LOG = Logger.getLogger(TriggerController.class.getName());

    public TriggerController(JSONArray config) {
        if (config != null) {
            for (int i = 0; i < config.length(); i++) {
                JSONObject obj = config.getJSONObject(i);
                TriggerTask tt = TriggerTask.parseJson(obj);
                if (tt.getTask() != null) {
                    tasks.add(tt);
                } else {
                    LOG.log(Level.WARNING, "Empty task for trigger command: \"{0}\"", obj.toString());
                }
            }
        }
        LOG.log(Level.INFO, "TriggerController activated with {0} tasks", tasks.size());
    }

    public void trigger(SupradinDataMessage message) {
        for (final TriggerTask tt : tasks) {
            if (tt.match(message)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ProcessBuilder pb = new ProcessBuilder(tt.getTask());
                            pb.start();
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, "Error while executing task: \"{0}\": {1}", 
                                    new Object[]{tt.getTask(), ex.getMessage()});
                        }
                    }
                }).start();
                LOG.log(Level.INFO, "Triggered task: \"{0}\" for message: \"{1}\"", 
                        new Object[]{tt.getTask(), message.toString()});
            }
        }
    }

}
