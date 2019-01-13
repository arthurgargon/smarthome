package com.gargon.smarthome.logger;

import com.gargon.smarthome.clunet.ClunetDateTimeResolver;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinConnectionResponseFilter;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.logger.listeners.LoggerControllerMessageListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.logger.commands.Command;
import com.gargon.smarthome.logger.commands.CommandFactory;
import com.gargon.smarthome.logger.commands.PeriodCommand;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public final class LoggerController {

    private final List<LoggerControllerMessageListener> messageListeners = new CopyOnWriteArrayList();

    private static SupradinConnection supradinConnection;
    private ScheduledExecutorService periodicalCommandsScheduledExecutor;

    Map<Integer, List<Command>> commands = new HashMap();

    public LoggerController(JSONObject config) throws Exception {
        if (config != null) {
            try {
                supradinConnection = new SupradinConnection();
                supradinConnection.open();

                supradinConnection.addDataListener(new SupradinDataListener() {
                    @Override
                    public void dataRecieved(SupradinConnection connection, SupradinDataMessage message) {
                        final RealTimeSupradinDataMessage rtm = new RealTimeSupradinDataMessage(message, System.currentTimeMillis());
                        
                        //System.out.println(rtm.toString());
                        
                        if (commands.containsKey(message.getCommand())) {
                            List<Command> c_list = commands.get(message.getCommand());
                            for (Command c : c_list) {
                                if (c.approve(rtm)) {
                                    List<RealTimeSupradinDataMessage> messages = new ArrayList() {
                                        {
                                            add(rtm);
                                        }
                                    };
                                    for (LoggerControllerMessageListener listener : messageListeners) {
                                        listener.messages(messages);
                                    }
                                }
                            }

                        }
                    }
                });
                
                supradinConnection.addDataListener(new ClunetDateTimeResolver());

                List<PeriodCommand> periodCommands = new ArrayList();
                for (String key : config.keySet()) {
                    try {
                        int commandId = Integer.parseInt(key);

                        //может быть как массивом элементов так и отдельным элементов
                        JSONArray commandArray = config.optJSONArray(key);
                        if (commandArray == null) {
                            commandArray = new JSONArray();
                            commandArray.put(config.optJSONObject(key));
                        }

                        for (int i = 0; i < commandArray.length(); i++) {
                            Command c = CommandFactory.createCommand(commandArray.optJSONObject(i));
                            if (c != null) {
                                List list = commands.get(commandId);
                                if (list == null){
                                    commands.put(commandId, list=new ArrayList());
                                }
                                list.add(c);

                                if (c instanceof PeriodCommand) {
                                    periodCommands.add((PeriodCommand) c);
                                }
                            }
                        }

                    } catch (Exception e) {
                        Logger.getLogger(LoggerController.class.getName()).log(Level.WARNING, "Can't read non integer command id", e);
                    }
                }

                supradinConnection.connect();

                //run periodical commands
                periodicalCommandsScheduledExecutor = Executors.newScheduledThreadPool(periodCommands.size());
                for (final PeriodCommand p : periodCommands) {

                    periodicalCommandsScheduledExecutor.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //стартуем триггер, если надо
                                p.startTrigger(supradinConnection);
                                //ждем пока триггер соберет ответы
                                Thread.sleep(p.getTriggerTimeout());

                                //теперь мы сделали все что могли да и время полностью вышло -> отдаем в базу
                                List<RealTimeSupradinDataMessage> messages = p.timeout();

                                for (LoggerControllerMessageListener listener : messageListeners) {
                                    listener.messages(messages);
                                }
                            } catch (InterruptedException ex) {
                                Logger.getLogger(LoggerController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }, p.getPeriod() - p.getTriggerTimeout(), p.getPeriod(), TimeUnit.MILLISECONDS);
                }

            } catch (Exception e) {
                Logger.getLogger(LoggerController.class.getName()).log(Level.SEVERE, "init controller error", e);
                shutdown();
            }
        } else {
            Logger.getLogger(LoggerController.class.getName()).log(Level.SEVERE, "can't read config file. Application will be closed");
        }
    }

    public void addMessageListener(LoggerControllerMessageListener listener) {
        messageListeners.add(listener);
    }

    public void removePeriodCommandsListener(LoggerControllerMessageListener listener) {
        messageListeners.remove(listener);
    }
    
    public boolean send(int dst, int prio, int command, byte[] data) {
        if (supradinConnection != null) {
            if (data == null) {
                data = new byte[]{};
            }
            return supradinConnection.sendData(new SupradinDataMessage(dst, prio, command, data));
        }
        return false;
    }

    public byte[] ask(int dst, int prio, int command, byte[] data,
            final int rsrc, final int rcmd, int rtimeout) {
        if (supradinConnection != null) {
            if (data == null) {
                data = new byte[]{};
            }
            SupradinDataMessage m = supradinConnection.sendDataAndWaitResponse(new SupradinDataMessage(dst, prio, command, data),
                    new SupradinConnectionResponseFilter() {
                @Override
                public boolean filter(SupradinDataMessage supradinRecieved) {
                    return supradinRecieved.getSrc() == rsrc && supradinRecieved.getCommand() == rcmd;
                }
            }, rtimeout);
            if (m != null) {
                return m.getData();
            }
        }
        return null;
    }

    public void shutdown() {
        if (supradinConnection != null) {
            supradinConnection.close();
        }
        if (periodicalCommandsScheduledExecutor != null) {
            periodicalCommandsScheduledExecutor.shutdown();
        }
    }

}
