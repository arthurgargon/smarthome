package com.gargon.smarthome.supradin.utils.logger;

import com.gargon.smarthome.clunet.ClunetDateTimeResolver;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.utils.logger.listeners.LoggerControllerMessageListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.supradin.utils.logger.commands.Command;
import com.gargon.smarthome.supradin.utils.logger.commands.CommandFactory;
import com.gargon.smarthome.supradin.utils.logger.commands.PeriodCommand;
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
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public final class LoggerController {

    private final List<LoggerControllerMessageListener> messageListeners = new CopyOnWriteArrayList();

    private static SupradinConnection supradinConnection;
    private ScheduledExecutorService periodicalCommandsScheduledExecutor;

    Map<Byte, Command> commands = new HashMap();

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
                        
                        if (commands.containsKey((byte) message.getCommand())) {
                            if (commands.get((byte) message.getCommand()).approve(rtm)) {
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
                });
                
                supradinConnection.addDataListener(new ClunetDateTimeResolver());

                List<PeriodCommand> periodCommands = new ArrayList();
                for (String key : config.keySet()) {
                    try {
                        int commandId = Integer.parseInt(key);

                        Command c = CommandFactory.createCommand(config.optJSONObject(key));
                        if (c != null) {
                            commands.put((byte) commandId, c);

                            if (c instanceof PeriodCommand) {
                                periodCommands.add((PeriodCommand) c);
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

    public void shutdown() {
        if (supradinConnection != null) {
            supradinConnection.close();
        }
        if (periodicalCommandsScheduledExecutor != null) {
            periodicalCommandsScheduledExecutor.shutdown();
        }
    }

}
