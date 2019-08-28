package com.gargon.smarthome.enums;

/**
 * @author gargon
 */
public enum Address {

    SUPRADIN(0x00),
    BROADCAST(0xFF),

    AUDIOBOX(0x84),
    AUDIOBATH(0x0B),

    RELAY_1(0x14),
    RELAY_2(0x15),

    SOCKET_DIMMER(0x20),

    KITCHEN(0x1D),
    BATH_SENSORS(0x1E),
    WARDROBE(0x1F),

    METEO(0x81),
    KITCHEN_LIGHT(0x82),
    WATER_SYSTEM(0x83),

    TELEPHONE(0x91);

    private int value;

    Address(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Address getByValue(int value) {
        for (Address a : values()) {
            if (value == a.value) {
                return a;
            }
        }
        return null;
    }
}
