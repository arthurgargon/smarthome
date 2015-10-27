package com.gargon.smarthome.supradin.utils;

import com.gargon.smarthome.clunet.ClunetDictionary;
import com.gargon.smarthome.clunet.utils.DataFormat;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gargon
 */
public class SupradinKeeper {

    private static final Logger LOG = Logger.getLogger(SupradinKeeper.class.getName());

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/smarthome?useUnicode=true&characterEncoding=utf8";

    //  Database credentials
    private static final String DB_USER = "smarthome";
    private static final String DB_PASS = "medvedAn";

    //Queries
    private static final String INSERT_QUERY = "insert into sniffs (s_time, src, dst, cmd, data, interpretation) values (?, ?, ?, ?, ?, ?)";

    private static Connection dbConnection = null;
    private static SupradinConnection supradinConnection;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            Class.forName(JDBC_DRIVER);
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            final PreparedStatement preparedStmt = dbConnection.prepareStatement(INSERT_QUERY);

            supradinConnection = new SupradinConnection();
            supradinConnection.open();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    if (dbConnection != null) {
                        try {
                            dbConnection.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, "connection closing error", ex);
                        }
                    }

                    if (supradinConnection != null) {
                        supradinConnection.close();
                    }
                    
                    Logger.getLogger(SupradinKeeper.class.getName()).log(Level.INFO, "SupradinKeeper closed");
                }
            });

            supradinConnection.addDataListener(new SupradinDataListener() {

                @Override
                public void dataRecieved(SupradinDataMessage supradin) {

                    try {
                        Calendar calendar = Calendar.getInstance();

                        preparedStmt.setLong(1, calendar.getTime().getTime());
                        preparedStmt.setByte(2, (byte)supradin.getSrc());
                        preparedStmt.setByte(3, (byte)supradin.getDst());
                        preparedStmt.setByte(4, (byte)supradin.getCommand());
                        preparedStmt.setBytes(5, supradin.getData());
                        preparedStmt.setString(6, ClunetDictionary.toString(supradin.getCommand(), supradin.getData()));

                        preparedStmt.execute();
                    } catch (SQLException ex) {
                        Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, "insert error", ex);
                    }
                }
            });
            supradinConnection.connect();

        } catch (Exception e) {
            Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, "initialization error", e);

            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SupradinKeeper.class.getName()).log(Level.SEVERE, "connection closing error", ex);
                }
            }

            if (supradinConnection != null) {
                supradinConnection.close();
            }
        }
    }

}
