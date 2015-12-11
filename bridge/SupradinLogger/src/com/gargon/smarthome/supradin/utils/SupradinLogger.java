package com.gargon.smarthome.supradin.utils;

import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.supradin.utils.logger.LoggerController;
import com.gargon.smarthome.supradin.utils.logger.ConfigReader;
import com.gargon.smarthome.supradin.utils.logger.RealTimeSupradinDataMessage;
import com.gargon.smarthome.supradin.utils.logger.listeners.LoggerControllerMessageListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author gargon
 */
public class SupradinLogger {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/smarthome?useUnicode=true&characterEncoding=utf8";

    //  Database credentials
    private static final String DB_USER = "smarthome";
    private static final String DB_PASS = "";

    //Queries
    private static final String INSERT_QUERY = "insert into sniffs (s_time, src, dst, cmd, data, interpretation) values (?, ?, ?, ?, ?, ?)";

    private static LoggerController controller = null;

    private static Connection dbConnection = null;
    private static PreparedStatement preparedStmt = null;

    private static synchronized void log(RealTimeSupradinDataMessage message) {
        try {
            //System.out.println("inserting " + message.toString());
            preparedStmt.setLong(1, message.getTime());
            preparedStmt.setByte(2, (byte) message.getSrc());
            preparedStmt.setByte(3, (byte) message.getDst());
            preparedStmt.setByte(4, (byte) message.getCommand());
            preparedStmt.setBytes(5, message.getData());
            preparedStmt.setString(6, ClunetDictionary.toString(message.getCommand(), message.getData()));

            preparedStmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(SupradinLogger.class.getName()).log(Level.SEVERE, "insert error on: " + message, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                JSONObject config = ConfigReader.read(args[0]); //config.json

                JSONObject db = config.getJSONObject("db");
                
                Class.forName(JDBC_DRIVER);
                dbConnection = DriverManager.getConnection(db.optString("jdbc_url", DB_URL), db.optString("jdbc_user", DB_USER), db.optString("jdbc_pass", DB_PASS));
                dbConnection.setAutoCommit(true);
                preparedStmt = dbConnection.prepareStatement(INSERT_QUERY);

                controller = new LoggerController(config.optJSONObject("commands"));
                controller.addMessageListener(new LoggerControllerMessageListener() {

                    @Override
                    public void messages(List<RealTimeSupradinDataMessage> messages) {
                        for (RealTimeSupradinDataMessage m : messages) {
                            log(m);
                        }
                    }
                });

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        if (controller != null) {
                            controller.shutdown();
                        }

                        if (dbConnection != null) {
                            try {
                                dbConnection.close();
                            } catch (SQLException ex) {
                                Logger.getLogger(SupradinLogger.class.getName()).log(Level.SEVERE, "connection closing error", ex);
                            }
                        }

                        Logger.getLogger(SupradinLogger.class.getName()).log(Level.INFO, "SupradinKeeper closed");
                    }
                });

            } catch (Exception e) {
                Logger.getLogger(SupradinLogger.class.getName()).log(Level.SEVERE, "initialization error", e);

                if (controller != null) {
                    controller.shutdown();
                }
                if (dbConnection != null) {
                    try {
                        dbConnection.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(SupradinLogger.class.getName()).log(Level.SEVERE, "connection closing error", ex);
                    }
                }
            }
        } else {
            Logger.getLogger(SupradinLogger.class.getName()).log(Level.SEVERE, "Required path to config.json");
        }
    }

}