package com.gargon.smarthome.trigger;

import com.gargon.smarthome.Smarthome;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.utils.DataFormat;
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
                if (tt.getCommand().hasCommand()) {
                    tasks.add(tt);
                } else {
                    LOG.log(Level.WARNING, "Empty task for trigger command: \"{0}\"", obj.toString());
                }
            }
        }
        LOG.log(Level.INFO, "TriggerController activated with {0} tasks", tasks.size());
    }

    public void trigger(SupradinConnection connection, SupradinDataMessage message) {
        for (final TriggerTask tt : tasks) {
            if (tt.match(message)) {
                    final String scriptCommand = tt.getCommand().getScriptCommand();
                    if (scriptCommand != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ProcessBuilder pb = new ProcessBuilder(scriptCommand);
                                    pb.start();
                                } catch (IOException ex) {
                                    LOG.log(Level.SEVERE, "Error while executing task: \"{0}\": {1}",
                                            new Object[]{scriptCommand, ex.getMessage()});
                                }
                            }
                        }).start();
                    } else {
                        TriggerMessage m = tt.getCommand().getMessageCommand();
                        byte[] data = DataFormat.hexToByteArray(m.getData());
                        
                        if (data == null){
                            data = new byte[]{};
                        }
                        
                        connection.sendData(new SupradinDataMessage(m.getDst_id(), Smarthome.PRIORITY_COMMAND, 
                            m.getCmd_id(), data));
                        
                    }
                LOG.log(Level.INFO, "Triggered task: \"{0}\" for message: \"{1}\"", 
                        new Object[]{tt.getCommand().toString(), message.toString()});
            }
        }
    }

}
