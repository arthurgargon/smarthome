package com.gargon.smarthome.supradin.utils;

import com.gargon.smarthome.clunet.Clunet;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import com.gargon.smarthome.supradin.SupradinConnection;

/**
 *
 * @author gargon
 */
public class SupradinFlasher {

    public static final int BOOTLOADER_TIMEOUT = 2000;

    private static void log(String message) {
        System.out.println(message);
    }

    private static int firmwareMaxSize(String chipName) {
        switch (chipName) {
            case "a8":
                return (0x1FFF - 512 * 2);
            case "a16":
                return (0x3FFF - 512 * 2);
            case "a32":
                return (0x7FFF - 512 * 2);
            case "a64":
                return (0xFFFF - 512 * 2);
            case "a128":
                return (0x1FFFF - 512 * 2);
        }

        throw new IllegalArgumentException("Chip type not supported");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String firmwarePath = null;
        int deviceId = -1;
        int bootTimeout = BOOTLOADER_TIMEOUT;

        int firmwareMaxSize = -1;

        String supradinHost = SupradinConnection.SUPRADIN_IP;
        int supradinControlPort = SupradinConnection.SUPRADIN_CONTROL_PORT;
        int supradinDataPort = SupradinConnection.SUPRADIN_DATA_PORT;

        CommandLine a = null;
        Options options = null;

        boolean ea;
        try {

            CommandLineParser parser = new BasicParser();
            options = new Options();

            Option opt = new Option("d", true, "Device ID");
            opt.setRequired(true);
            options.addOption(opt);

            opt = new Option("f", true, "Intel hex firmware file path (*.hex)");
            opt.setRequired(true);
            options.addOption(opt);

            options.addOption(new Option("t", true, "Bootloader waiting timeout, in ms. " + BOOTLOADER_TIMEOUT + ", by default. Use -1 to skip reboot."));

            options.addOption(new Option("sip", true, "Supradin IP. " + SupradinConnection.SUPRADIN_IP + ", by default."));
            options.addOption(new Option("scp", true, "Supradin control port. " + SupradinConnection.SUPRADIN_CONTROL_PORT + ", by default."));
            options.addOption(new Option("sdp", true, "Supradin data port. " + SupradinConnection.SUPRADIN_DATA_PORT + ", by default."));

            options.addOption(new Option("a", true, "Chip name (a8, a16, a32, a64, a128). a8, by default. To limit firmware size and not to damage bootloader."));

            a = parser.parse(options, args);

            try {

                deviceId = Integer.parseInt(a.getOptionValue("d"));
                firmwarePath = a.getOptionValue("f");
                ea = firmwarePath == null;
                if (a.hasOption("t")) {
                    bootTimeout = Integer.parseInt(a.getOptionValue("t"));
                }
                if (a.hasOption("sip")) {
                    supradinHost = a.getOptionValue("sip");
                    ea = supradinHost == null;
                }
                if (a.hasOption("scp")) {
                    supradinControlPort = Integer.parseInt(a.getOptionValue("scp"));
                }
                if (a.hasOption("sdp")) {
                    supradinDataPort = Integer.parseInt(a.getOptionValue("sdp"));
                }

                String chipName = "a8";
                if (a.hasOption("a")) {
                    chipName = a.getOptionValue("a");
                }
                firmwareMaxSize = firmwareMaxSize(chipName);

            } catch (Exception e) {
                System.out.println("Incorrect types of arguments.  Reason: " + e.getMessage());
                ea = true;
            }

        } catch (org.apache.commons.cli.ParseException exp) {
            // oops, something went wrong
            System.out.println("Incorrect arguments.  Reason: " + exp.getMessage());
            ea = true;
        }

        if (ea || a.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("SupradinFlasher", options);
            return;
        }

        SupradinConnection connection = null;
        try {
            connection = new SupradinConnection(supradinHost, supradinControlPort, supradinDataPort);
            connection.open();
            log("Connecting to " + connection.getHost() + "...");
            if (connection.connect()) {
                log("Connected");
                Clunet.sendFirmware(connection, deviceId, bootTimeout, firmwarePath, firmwareMaxSize, System.out);
            } else {
                log("Unable to connect");
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

}
