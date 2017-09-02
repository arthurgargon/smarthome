package com.gargon.smarthome;

import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.logger.LoggerController;
import com.gargon.smarthome.logger.ConfigReader;
import com.gargon.smarthome.logger.RealTimeSupradinDataMessage;
import com.gargon.smarthome.logger.listeners.LoggerControllerMessageListener;
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
public class SmarthomeLogger {

    private static final Logger LOG = Logger.getLogger(SmarthomeLogger.class.getName());

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String DB_URL = "jdbc:mysql://localhost/smarthome?useUnicode=true&characterEncoding=utf8";

    //  Database credentials
    private static String DB_USER = "smarthome";
    private static String DB_PASS = "";

    //Queries
    private static String DB_TABLE = "sniffs";
    private static final String INSERT_QUERY = "insert into %s (s_time, src, dst, cmd, data, interpretation) values (?, ?, ?, ?, ?, ?)";

    private static Connection dbConnection = null;

    private static LoggerController controller = null;

    private static void closeController() {
        if (controller != null) {
            controller.shutdown();
        }
    }

    private static void closeDB() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
            } finally {
                dbConnection = null;
            }
        }
    }

    private static Connection getDBConnection() {
        if (dbConnection != null) {
            try {
                if (dbConnection.isValid(3)) {
                    return dbConnection;
                } else {
                    LOG.log(Level.WARNING, "DB connection is not valid. Will attempt to reconnect");
                    closeDB();
                }
            } catch (SQLException ex) {
            }
        }

        if (dbConnection == null) {
            try {
                Class.forName(JDBC_DRIVER);
                dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                dbConnection.setAutoCommit(true);
                LOG.log(Level.INFO, "Connection to DB ({0}) established", DB_URL);
            } catch (ClassNotFoundException | SQLException ex) {
                LOG.log(Level.WARNING, "Error while connecting to DB: {0}", ex.getMessage());
                dbConnection = null;
            }
        }
        return dbConnection;
    }

    private static synchronized void log(RealTimeSupradinDataMessage message) {
        Connection c = getDBConnection();
        if (c != null) {
            try {
                PreparedStatement preparedStmt = dbConnection.prepareStatement(String.format(INSERT_QUERY, DB_TABLE));
                preparedStmt.setLong(1, message.getTime());
                preparedStmt.setInt(2, message.getSrc());
                preparedStmt.setInt(3, message.getDst());
                preparedStmt.setInt(4, message.getCommand());
                preparedStmt.setBytes(5, message.getData());
                preparedStmt.setString(6, ClunetDictionary.toString(message.getCommand(), message.getData()));

                preparedStmt.execute();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error while inserting a message ({0}): {1}",
                        new Object[]{message, ex.getMessage()});
            }
        } else {
            LOG.log(Level.WARNING, "DB connection is not available. Skip message inserting: {0}", message);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOG.log(Level.INFO, "Application started");
        if (args.length == 1) {
            try {
                JSONObject config = ConfigReader.read(args[0]); //config.json

                JSONObject db = config.getJSONObject("db");
                DB_URL = db.optString("jdbc_url", DB_URL);
                DB_USER = db.optString("jdbc_user", DB_USER);
                DB_PASS = db.optString("jdbc_pass", DB_PASS);

                DB_TABLE = db.optString("db_table", DB_TABLE);

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
                    @Override
                    public void run() {
                        LOG.log(Level.INFO, "Application termination");
                        closeController();
                        closeDB();
                    }
                });

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error while initialization", e);

                closeController();
                closeDB();
            }
        } else {
            LOG.log(Level.SEVERE, "Required path to config.json");
        }
    }

}
