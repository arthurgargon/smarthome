package com.gargon.smarthome;

import com.gargon.smarthome.multicast.MulticastConnection;
import com.gargon.smarthome.multicast.MulticastDataListener;
import com.gargon.smarthome.multicast.messages.MulticastDataMessage;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinConnectionResponseFilter;
import com.gargon.smarthome.supradin.SupradinDataListener;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.utils.http.AskHTTPCallback;
import com.gargon.smarthome.utils.http.AskHTTPHandler;
import com.gargon.smarthome.utils.http.MainHTTPHandler;
import com.gargon.smarthome.utils.http.SendHTTPCallback;
import com.gargon.smarthome.utils.http.SendHTTPHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gargon
 */
public class SmarthomeController {

    private static SupradinConnection supradinConnection;
    private static MulticastConnection multicastConnection;
    
    private static HttpServer httpServer = null;

    private static final Logger LOG = Logger.getLogger(SmarthomeController.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int http_port = -1;
        if (args.length == 2){
            if ("-http".equals(args[0])){
                try{
                    http_port = Integer.parseInt(args[1]);
                }catch(NumberFormatException e){
                    LOG.log(Level.WARNING, "Incorrect HTTP port specified (\"{0}\"). Skip HTTP server initialization", args[1]);
                }
            }
        }
        
        try {
            supradinConnection = new SupradinConnection();
            multicastConnection = new MulticastConnection();

            supradinConnection.addDataListener(new SupradinDataListener() {
                @Override
                public void dataRecieved(SupradinConnection connection, SupradinDataMessage sm) {
                    //ip может быть только у супрадин, все остальные
                    //приходят назад, зацикливаясь из мультикаст сети
                    if (sm.getSrc() <= 0x80) {  //supradin addresses
                        multicastConnection.sendData(new MulticastDataMessage(sm.getDst(), sm.getSrc(), sm.getCommand(), sm.getData()));
                        //System.out.println("supradin recieved: " + sm.toString());
                    }
                }
            });

            multicastConnection.addDataListener(new MulticastDataListener() {
                @Override
                public synchronized void dataRecieved(MulticastConnection connection, InetAddress ip, MulticastDataMessage mm) {

                    int ip4 = 0;
                    if (ip != null) {
                        for (int i = ip.getAddress().length - 1; i >= 0; i--) {
                            ip4 = ip4 << 8 | (ip.getAddress()[i] & 0xFF);
                        }
                    }

                    //отправляет в супрадин сообщение с IP адерсом отправителя из мультикаст-сети
                    supradinConnection.sendData(new SupradinDataMessage(ip4, mm.getDst(), mm.getSrc(), mm.getCommand(), mm.getData()));
                    //System.out.println("multicast recieved: " + mm.toString());

                    //помогаем supradin разрулить подряд идущие сообщения
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            });

            LOG.log(Level.INFO, "Open multicast connection");
            multicastConnection.open();
            LOG.log(Level.INFO, "Open supradin connection");
            supradinConnection.open();
            LOG.log(Level.INFO, "Connect to supradin connection");
            supradinConnection.connect();

            if (http_port > 0) {
                LOG.log(Level.INFO, "Starting HTTP server ({0} port)", http_port);
                //http server
                httpServer = HttpServer.create(new InetSocketAddress(http_port), 0);
                httpServer.createContext(MainHTTPHandler.URI, new MainHTTPHandler());
                httpServer.createContext(SendHTTPHandler.URI, new SendHTTPHandler(new SendHTTPCallback() {
                    @Override
                    public boolean send(int dst, int prio, int command, byte[] data) {
                        if (data == null) {
                            data = new byte[]{};
                        }
                        return supradinConnection.sendData(new SupradinDataMessage(dst, prio, command, data));
                    }
                }));
                httpServer.createContext(AskHTTPHandler.URI, new AskHTTPHandler(new AskHTTPCallback() {
                    @Override
                    public byte[] ask(int dst, int prio, int command, byte[] data,
                            final int rsrc, final int rcmd, int rtimeout) {
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
                        return null;
                    }
                }));

                httpServer.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
                httpServer.start();
            }
            

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (httpServer != null) {
                        httpServer.stop(1);
                    }

                    if (supradinConnection != null) {
                        supradinConnection.close();
                    }

                    if (multicastConnection != null) {
                        multicastConnection.close();
                    }

                    LOG.log(Level.INFO, "Application closed");
                }
            });

        } catch (Exception e) {

            if (supradinConnection != null) {
                supradinConnection.close();
            }

            if (multicastConnection != null) {
                multicastConnection.close();
            }

            LOG.log(Level.SEVERE, "initialization error", e);
        }
    }

}
