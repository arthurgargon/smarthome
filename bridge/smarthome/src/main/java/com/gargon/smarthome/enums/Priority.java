package com.gargon.smarthome.enums;

/**
 * @author gargon
 */
public enum Priority {
    /* Приоритет пакета 1 - неважное уведомление, которое вообще может быть потеряно без последствий */
    NOTICE(1),
    /* Приоритет пакета 2 - какая-то информация, не очень важная */
    INFO(2),
    /* Приоритет пакета 3 - сообщение с какой-то важной информацией */
    MESSAGE(3),
    /* Приоритет пакета 4 - команда, на которую нужно сразу отреагировать */
    COMMAND(4);

    private int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Priority getByValue(int value) {
        for (Priority p : values()) {
            if (value == p.value) {
                return p;
            }
        }
        return null;
    }
}
