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
public class SupradinKeeper {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/smarthome?useUnicode=true&characterEncoding=utf8";

    //  Database credentials
    private static final String DB_USER = "smarthome";
    private static final String DB_PASS = "medvedAn";

    //Queries
    private static final String INSERT_QUERY = "insert into sniffs (s_time, src, dst, cmd, data, interpretation) values (?, ?, ?, ?, ?, ?)";

    private static LoggerController controller = null;

    private static Connection dbConnection = null;
    private static PreparedStatement preparedStmt = null;

    private static void dbInsert(RealTimeSupradinDataMessage message) {
        try {
            
            System.out.println("inserting " + message.toString());
            
            preparedStmt.setLong(1, message.getTime());
            preparedStmt.setByte(2, (byte) message.getSrc());
            preparedStmt.setByte(3, (byte) message.getDst());
            preparedStmt.setByte(4, (byte) message.getCommand());
            preparedStmt.setBytes(5, message.getData());
            preparedStmt.setString(6, ClunetDictionary.toString(message.getCommand(), message.getData()));

            preparedStmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, "insert error", ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            JSONObject config = ConfigReader.read("config.json");

            Class.forName(JDBC_DRIVER);
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            preparedStmt = dbConnection.prepareStatement(INSERT_QUERY);

            controller = new LoggerController(config.optJSONObject("commands"));
            controller.addMessageListener(new LoggerControllerMessageListener() {

                @Override
                public void messages(List<RealTimeSupradinDataMessage> messages) {
                    for (RealTimeSupradinDataMessage m : messages) {
                        dbInsert(m);
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
                            Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, "connection closing error", ex);
                        }
                    }

                    Logger.getLogger(SupradinKeeper.class.getName()).log(Level.INFO, "SupradinKeeper closed");
                }
            });

        } catch (Exception e) {
            Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, "initialization error", e);

            if (controller != null) {
                controller.shutdown();
            }
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, "connection closing error", ex);
                }
            }
        }
    }

}
