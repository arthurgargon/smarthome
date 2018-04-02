package com.gargon.smarthome.supradin.utils;

import com.gargon.smarthome.clunet.Clunet;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinConnectionResponseFilter;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;
import com.gargon.smarthome.utils.IntelHexReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.cli.DefaultParser;

/**
 *
 * @author gargon
 */
public class SupradinFlasher {

    public static final int BOOTLOADER_TIMEOUT = 5000;
    public static final int RESPONSE_TIMEOUT = 500;

    private static final int COMMAND_FIRMWARE_UPDATE_START = 0;
    private static final int COMMAND_FIRMWARE_UPDATE_INIT = 1;
    private static final int COMMAND_FIRMWARE_UPDATE_READY = 2;
    private static final int COMMAND_FIRMWARE_UPDATE_WRITE = 3;
    private static final int COMMAND_FIRMWARE_UPDATE_WRITTEN = 4;
    private static final int COMMAND_FIRMWARE_UPDATE_DONE = 5;

    private static final int COMMAND_FIRMWARE_UPDATE_ERROR = 255;

    private static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat(/*"dd.MM.yy */"HH:mm:ss.SSS");

    private static void log(String message) {
        System.out.println(LOG_DATE_FORMAT.format(new Date()) + ": " + message);
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
        int responseTimeout = RESPONSE_TIMEOUT;
        String chipName = "a8";

        int firmwareMaxSize = -1;

        String supradinHost = SupradinConnection.SUPRADIN_IP;
        int supradinControlPort = SupradinConnection.SUPRADIN_CONTROL_PORT;
        int supradinDataPort = SupradinConnection.SUPRADIN_DATA_PORT;

        CommandLine a = null;
        Options options = null;

        boolean ea;
        try {

            CommandLineParser parser = new DefaultParser();
            options = new Options();

            Option opt = new Option("d", true, "Device ID");
            opt.setRequired(true);
            options.addOption(opt);

            opt = new Option("f", true, "Intel-HEX firmware file path (*.hex)");
            opt.setRequired(true);
            options.addOption(opt);

            options.addOption(new Option("bt", true, "Bootloader waiting timeout, in ms. " + BOOTLOADER_TIMEOUT + ", by default. Use -1 to skip reboot."));
            options.addOption(new Option("rt", true, "Response waiting timeout, in ms. " + RESPONSE_TIMEOUT + ", by default"));

            options.addOption(new Option("sip", true, "Supradin IP. " + SupradinConnection.SUPRADIN_IP + ", by default."));
            options.addOption(new Option("scp", true, "Supradin control port. " + SupradinConnection.SUPRADIN_CONTROL_PORT + ", by default."));
            options.addOption(new Option("sdp", true, "Supradin data port. " + SupradinConnection.SUPRADIN_DATA_PORT + ", by default."));

            options.addOption(new Option("a", true, "Chip name (a8, a16, a32, a64, a128). a8, by default. To limit firmware size and avoid bootloader damage."));

            a = parser.parse(options, args);

            try {

                deviceId = Integer.parseInt(a.getOptionValue("d"));
                firmwarePath = a.getOptionValue("f");
                ea = firmwarePath == null;
                if (a.hasOption("bt")) {
                    bootTimeout = Integer.parseInt(a.getOptionValue("bt"));
                }
                if (a.hasOption("rt")) {
                    responseTimeout = Integer.parseInt(a.getOptionValue("rt"));
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

                if (a.hasOption("a")) {
                    chipName = a.getOptionValue("a");
                }
                firmwareMaxSize = firmwareMaxSize(chipName);

            } catch (Exception e) {
                System.err.println("Incorrect types of arguments.  Reason: " + e.getMessage());
                ea = true;
            }

        } catch (org.apache.commons.cli.ParseException exp) {
            // oops, something went wrong
            System.err.println("Incorrect arguments.  Reason: " + exp.getMessage());
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
                //Clunet.sendFirmware(connection, deviceId, bootTimeout, firmwarePath, firmwareMaxSize, System.out);

                IntelHexReader hexReader = new IntelHexReader();
                log("Loading HEX");
                if (hexReader.read(firmwarePath)) {

                    if (firmwareMaxSize < 0 || firmwareMaxSize >= hexReader.getLength()) {

                        log("Loaded " + hexReader.getLength() + " bytes");
                        final int deviceId_ = deviceId;
                        SupradinConnectionResponseFilter bootControlFilter = new SupradinConnectionResponseFilter() {

                            @Override
                            public boolean filter(SupradinDataMessage supradinRecieved) {
                                return supradinRecieved.getCommand() == Clunet.COMMAND_BOOT_CONTROL
                                        && supradinRecieved.getSrc() == deviceId_;
                            }
                        };

                        SupradinDataMessage r = null;
                        if (bootTimeout >= 0) {
                            log("Sending reboot command...");
                            r = connection.sendDataAndWaitResponse(new SupradinDataMessage(deviceId, Clunet.PRIORITY_COMMAND, Clunet.COMMAND_REBOOT),
                                    bootControlFilter, bootTimeout);
                        }
                        if (bootTimeout < 0 || r != null) {
                            //Устройство посылает CLUNET_COMMAND_BOOT_CONTROL, в данных 0. 
                            //Так мы узнали, что бутлоадер запустился.
                            if (bootTimeout < 0 || (r.getData().length == 1 && r.getData()[0] == COMMAND_FIRMWARE_UPDATE_START)) {
                                log("Bootloader detected");
                                //Сразу же посылаем на адрес устройства CLUNET_COMMAND_BOOT_CONTROL, в данных 1. Это переводит устройство в режим прошивки.
                                r = connection.sendDataAndWaitResponse(new SupradinDataMessage(deviceId, Clunet.PRIORITY_COMMAND, Clunet.COMMAND_BOOT_CONTROL,
                                        new byte[]{COMMAND_FIRMWARE_UPDATE_INIT}), bootControlFilter, responseTimeout);
                                //Устройство посылает CLUNET_COMMAND_BOOT_CONTROL, первый байт данных - 2, подтверждает, что перешли в режим прошивки.
                                //Следующие два байта - это размер страницы.
                                if (r != null && r.getData().length == 3 && r.getData()[0] == COMMAND_FIRMWARE_UPDATE_READY) {
                                    int pageSize = ((r.getData()[2] & 0xFF) << 8) | (r.getData()[1] & 0xFF);
                                    log("Page size: " + pageSize + " bytes");
                                    //Посылаем в устройство CLUNET_COMMAND_BOOT_CONTROL, байт 0 = 3.  Это команда на запись страницы прошивки;
                                    //Байт 1 - номер передаваемой страницы;
                                    //Байт 2,3 - размер передаваемых данных;
                                    //Все остальные байты - сам кусок прошивки. Этот кусок должен быть равен размеру страницы, 
                                    //который мы получили в пункте 4 (может и не соответствовать реальному, если реальный слишком большой).

                                    int dataMaxLen = Clunet.DATA_MAX_LENGTH - 4;   //или можно задавать меньшее число, в случае возникновения проблем при передаче
                                    for (int i = 0; i < hexReader.getLength(); i += pageSize) {
                                        int len0 = Math.min(pageSize, (int) (hexReader.getLength() - i));
                                        //разбиваем при необходимости на куски, влазящие в протокол
                                        for (int j = 0; j < len0; j += dataMaxLen) {
                                            int len1 = Math.min(dataMaxLen, (int) (len0 - j));

                                            byte[] data = new byte[len1 + 4];
                                            data[0] = COMMAND_FIRMWARE_UPDATE_WRITE;
                                            data[1] = (byte) (i / pageSize);
                                            data[2] = (byte) (len1 >> 0);
                                            data[3] = (byte) (len1 >> 8);

                                            System.arraycopy(hexReader.getData(), i + j, data, 4, len1);
                                            r = connection.sendDataAndWaitResponse(new SupradinDataMessage(deviceId, Clunet.PRIORITY_COMMAND, Clunet.COMMAND_BOOT_CONTROL, data),
                                                    bootControlFilter, responseTimeout);

                                            //Устройство посылает CLUNET_COMMAND_BOOT_CONTROL, в данных 4 - это подтверждает, что страница прошита.
                                            if (r == null || r.getData().length != 1 || r.getData()[0] != COMMAND_FIRMWARE_UPDATE_WRITTEN) {
                                                log("Error while transfering firmware!");
                                            } else {
                                                System.out.print("\r[");
                                                float percent = (float) (i + j + len1) / hexReader.getLength();
                                                int percent50 = (int) (50 * percent);
                                                for (int p = 0; p < 50; p++) {
                                                    System.out.print(p < percent50 ? "#" : " ");
                                                }
                                                System.out.print("] " + (int) (percent * 100) + "%");
                                                System.out.flush();
                                            }
                                        }
                                    }
                                    System.out.println();

                                    //Посылаем в устройство CLUNET_COMMAND_BOOT_CONTROL, в данных 5 
                                    //Это завершает работу бутлоадера и запускает только что прошитый код.
                                    connection.sendData(new SupradinDataMessage(deviceId, Clunet.PRIORITY_COMMAND, Clunet.COMMAND_BOOT_CONTROL,
                                            new byte[]{COMMAND_FIRMWARE_UPDATE_DONE}));
                                    log("Done!");

                                } else {
                                    log("Timeout or wrong answer on command: COMMAND_FIRMWARE_UPDATE_READY");
                                }
                            } else {
                                log("Timeout or wrong answer on command: COMMAND_FIRMWARE_UPDATE_START");
                            }
                        } else {
                            log("Device does not reply");
                        }
                    } else {
                        log("Too big firmware size (" + hexReader.getLength() + " bytes, but only " + firmwareMaxSize + " bytes allowable). "
                                + "Check chip type or reduce firmware size");
                    }
                } else {
                    log("Invalid firmware HEX-file");
                }
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
