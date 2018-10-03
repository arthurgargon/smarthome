package com.gargon.smarthome.utils;

/**
 *
 * @author gargon
 */
public class DataFormat {

    private static String byteArrayToHex(byte[] a) {
        if (a != null) {
            StringBuilder sb = new StringBuilder(a.length * 2);
            for (byte b : a) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString().toUpperCase();
        }
        return null;
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] data = null;
        if (s != null) {
            int len = s.length();
            data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        }
        return data;
    }

    public static String byteToHex(int value) {
        return byteArrayToHex(new byte[]{(byte) value});
    }

    public static String bytesToHex(byte[] value) {
        return byteArrayToHex(value);
    }

    public static byte[] hexToByteArray(String hex) {
        return hexStringToByteArray(hex);
    }

}
