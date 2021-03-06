package com.gargon.smarthome.commands;

import com.gargon.smarthome.SmarthomeDictionary;
import com.gargon.smarthome.clunet.Clunet;
import com.gargon.smarthome.enums.Address;
import com.gargon.smarthome.enums.Command;
import com.gargon.smarthome.enums.Priority;
import com.gargon.smarthome.fm.FMDictionary;
import com.gargon.smarthome.heatfloor.HeatFloorDictionary;
import com.gargon.smarthome.heatfloor.HeatfloorProgram;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinConnectionResponseFilter;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

import java.util.Map;

/**
 *
 * @author gargon
 */
public class Commands {
    
    public static final int EQUALIZER_GAIN = 1;
    public static final int EQUALIZER_TREBLE = 2;
    public static final int EQUALIZER_BASS = 3;
    
    
    public static final int ROOM_AUDIOSOURCE_PC = 1;
    //public static final int ROOM_AUDIOSOURCE_PC = 2;
    public static final int ROOM_AUDIOSOURCE_BLUETOOTH = 3;
    public static final int ROOM_AUDIOSOURCE_FM = 2;
    
    public static final int BATHROOM_AUDIOSOURCE_PAD = 1;
    public static final int BATHROOM_AUDIOSOURCE_FM = 4;


    public static final int RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID = 1;
    public static final int RELAY_1_FLOOR_KITCHEN_SWITCH_ID = 2;
    public static final int RELAY_1_FLOOR_BATHROOM_SWITCH_ID = 3;
    
    //public static final int RELAY_2_SWITCH_1_ID = 1;
    public static final int RELAY_2_FAN_SWITCH_ID = 2;
    public static final int RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID = 3;
    
    public static final int KITCHEN_FAN_SWITCH_ID = 1;
    public static final int KITCHEN_LIGHT_SWITCH_ID = 1;
    
    public static final int SOCKET_DIMMER_SWITCH_ID = 1;
    
    private static final int RELAY_1_WAITING_RESPONSE_TIMEOUT  = 500; //ms
    private static final int RELAY_2_WAITING_RESPONSE_TIMEOUT  = 500; //ms
    private static final int AUDIOBOX_WAITING_RESPONSE_TIMEOUT = 500; //ms
    private static final int AUDIOBATH_WAITING_RESPONSE_TIMEOUT = 500; //ms
    private static final int KITCHEN_WAITING_RESPONSE_TIMEOUT = 500; //ms
    private static final int KITCHEN_LIGHT_WAITING_RESPONSE_TIMEOUT = 500; //ms
    private static final int SOCKET_DIMMER_WAITING_RESPONSE_TIMEOUT = 50; //ms
    
    private static final int AUDIO_CHANGE_VOLUME_RESPONSE_TIMEOUT = 500; //ms
    private static final int AUDIO_SELECT_CHANNEL_RESPONSE_TIMEOUT = 500; //ms
    private static final int FM_EEPROM_WAITING_RESPONSE_TIMEOUT    = 500; //ms

    private static final int HEATFLOOR_EEPROM_WAITING_RESPONSE_TIMEOUT    = 500; //ms

    
    private static final int NUM_ATTEMPTS_COMMAND = 5;
    private static final int NUM_ATTEMPTS_STATE = 2;


     /**
     * Переключает свет в гардеробной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean toggleLightInCloackroom(SupradinConnection connection) {
        return Clunet.sendResponsible(connection,
                Address.RELAY_1,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte) 2, RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.RELAY_1
                                && supradinRecieved.getCommand() == Command.SWITCH_INFO
                                && supradinRecieved.getData().length == 1;
                    }
                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    
    /**
     * Управляет включением света в гардеробной
     *
     * @param connection текущее соединение
     * @param on признак включить/выключить
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchLightInCloackroom(SupradinConnection connection, final boolean on) {
        return Clunet.sendResponsible(connection,
                Address.RELAY_1,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.RELAY_1
                                && supradinRecieved.getCommand() == Command.SWITCH_INFO
                                && supradinRecieved.getData().length == 1
                                && ((supradinRecieved.getData()[0] >> (RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                    }
                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }

    /**
     * Определяет включен ли свет в гардеробной по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает true -  если свет в гардеробной включен;
     *                    false - если свет в гардеробной выключен;
     *                    null - если передано неверное по формату сообщение для анализа
     */
    public static Boolean checkLightInCloackroomSwitchedOn(SupradinDataMessage message){
        if (message != null){
            if (message.getSrc() == Address.RELAY_1
                    && message.getCommand() == Command.SWITCH_INFO
                    && message.getData().length == 1){
                return ((message.getData()[0] >> (RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID - 1)) & 1) == 1;
            }
        }
        return null;
    }
    
    /**
     * Определяет включен ли свет на кухне по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает true -  если свет на кухне включен (также и любое состояние диммера);
     *                    false - если свет на кухне выключен;
     *                    null - если передано неверное по формату сообщение для анализа
     */
    public static Boolean checkLightInKitchenSwitchedOn(SupradinDataMessage message){
        if (message != null){
            if (message.getSrc() == Address.KITCHEN_LIGHT
                    && message.getCommand() == Command.SWITCH_INFO
                    && message.getData().length == 1){
                return ((message.getData()[0] >> (KITCHEN_LIGHT_SWITCH_ID - 1)) & 1) == 1;
            }
        }
        return null;
    }
    
    /**
     * Определяет включен ли свет в комнате по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает true -  если ночник включен (также и любое состояние диммера);
     *                    false - если ночник выключен;
     *                    null - если передано неверное по формату сообщение для анализа
     */
    public static Boolean checkLightInSocketSwitchedOn(SupradinDataMessage message){
        if (message != null){
            if (message.getSrc() == Address.SOCKET_DIMMER
                    && message.getCommand() == Command.SWITCH_INFO
                    && message.getData().length == 1){
                return ((message.getData()[0] >> (SOCKET_DIMMER_SWITCH_ID - 1)) & 1) == 1;
            }
        }
        return null;
    }

    /**
     * Проверяет включен ли свет в гардеробной.
     * Отправляет команду на проверку состояния выключателей и
     * в случае успешного ее выполнения производит анализ состояния
     * выключателя света в гардеробной
     *
     * @param connection текущее соединение
     * @return возвращает true -  если свет в гардеробной включен;
     *                    false - если свет в гардеробной выключен;
     *                    null - не удалось определить (возникла ошибка при отправке или ответ не получен)
     */
    public static Boolean isLightInCloackroomSwitchedOn(SupradinConnection connection) {
        return checkLightInCloackroomSwitchedOn(Clunet.sendResponsible(connection,
                Address.RELAY_1,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte)0xFF},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getCommand() == Command.SWITCH_INFO
                                && supradinRecieved.getData().length == 1;
                    }
                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
    }

     /**
     * Управляет включением теплого пола на кухне
     *
     * @param connection текущее соединение
     * @param on признак включить/выключить
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchHeatingFloorInKitchen(SupradinConnection connection, final boolean on) {
        return Clunet.sendResponsible(connection,
                Address.RELAY_1,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_1_FLOOR_KITCHEN_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.RELAY_1
                                && supradinRecieved.getCommand() == Command.SWITCH_INFO
                                && supradinRecieved.getData().length == 1
                                && ((supradinRecieved.getData()[0] >> (RELAY_1_FLOOR_KITCHEN_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                    }
                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    /**
     * Управляет включением теплого пола в ванной комнате
     *
     * @param connection текущее соединение
     * @param on признак включить/выключить
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchHeatingFloorInBathroom(SupradinConnection connection, final boolean on) {
        return Clunet.sendResponsible(connection,
                Address.RELAY_1,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_1_FLOOR_BATHROOM_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.RELAY_1
                                && supradinRecieved.getCommand() == Command.SWITCH_INFO
                                && supradinRecieved.getData().length == 1
                                && ((supradinRecieved.getData()[0] >> (RELAY_1_FLOOR_BATHROOM_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                    }
                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    
    /**
     * Выбирает режим управления работой вентилятора в ванной
     * (автоматический/ручной)
     *
     * @param connection текущее соединение
     * @param auto_mode
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean selectFanModeInBathroom(SupradinConnection connection, boolean auto_mode) {
        final byte mode = (byte)(auto_mode ? 1 : 0);
        return Clunet.sendResponsible(connection,
                Address.RELAY_2,
                Priority.COMMAND,
                Command.FAN,
                new byte[]{mode},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.RELAY_2
                                && supradinRecieved.getCommand() == Command.FAN_INFO
                                && supradinRecieved.getData().length == 9
                                && supradinRecieved.getData()[0] == mode;
                    }
                }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
     /**
     * Переключает вентилятор в ванной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean toggleFanInBathroom(SupradinConnection connection) {
        return Clunet.sendResponsible(connection,
                Address.RELAY_2,
                Priority.COMMAND,
                Command.FAN,
                new byte[]{(byte) 2},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.RELAY_2
                                && supradinRecieved.getCommand() == Command.FAN_INFO
                                && supradinRecieved.getData().length == 9;
                    }
                }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    /**
     * Переключает вентилятор на кухне
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean toggleFanInKitchen(SupradinConnection connection) {
        return Clunet.sendResponsible(connection,
                Address.KITCHEN,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte) 2, KITCHEN_FAN_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.KITCHEN
                                && supradinRecieved.getCommand() == Command.SWITCH_INFO
                                && supradinRecieved.getData().length == 1;
                    }
                }, KITCHEN_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
     /**
     * Управляет включением вентилятора на кухне
     *
     * @param connection текущее соединение
     * @param on признак включить/выключить
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchFanInKitchen(SupradinConnection connection, final boolean on) {
        return Clunet.sendResponsible(connection,
                Address.KITCHEN,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte) (on ? 1 : 0), KITCHEN_FAN_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.KITCHEN
                        && supradinRecieved.getCommand() == Command.SWITCH_INFO
                        && supradinRecieved.getData().length == 1
                        && ((supradinRecieved.getData()[0] >> (KITCHEN_FAN_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                    }
                }, KITCHEN_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
        
    /**
     * Определяет включен ли вентилятор на кухне по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает true - если вентилятор на кухне включен; false - если
     * вентилятор в ванной выключен; null - если передано неверное по формату
     * сообщение для анализа
     */
    public static Boolean checkFanInKitchenSwitchedOn(SupradinDataMessage message) {
        if (message != null) {
            if (message.getSrc() == Address.KITCHEN
                    && message.getCommand() == Command.SWITCH_INFO
                    && message.getData().length == 1) {
                return ((message.getData()[0] >> (KITCHEN_FAN_SWITCH_ID - 1)) & 1) == 1;
            }
        }
        return null;
    }
    
    
    /**
     * Определяет включен ли вентилятор в ванной по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает true - если вентилятор в ванной включен; false - если
     * вентилятор в ванной выключен; null - если передано неверное по формату
     * сообщение для анализа
     */
    public static Boolean checkFanInBathroomSwitchedOn(SupradinDataMessage message) {
        if (message != null) {
            if (message.getSrc() == Address.RELAY_2
                    && message.getCommand() == Command.SWITCH_INFO
                    && message.getData().length == 1) {
                return ((message.getData()[0] >> (RELAY_2_FAN_SWITCH_ID - 1)) & 1) == 1;
            }
        }
        return null;
    }

    /**
     * Проверяет включен вентилятор в ванной. Отправляет команду на проверку
     * состояния выключателей и в случае успешного ее выполнения производит
     * анализ состояния выключателя вентилятора в ванной
     *
     * @param connection текущее соединение
     * @return возвращает true - если вентилятор в ванной комнате включен; false
     * - если вентилятор в ванной комнате выключен; null - не удалось определить
     * (возникла ошибка при отправке или ответ не получен)
     */
    public static Boolean isFanInBathroomSwitchedOn(SupradinConnection connection) {
        return checkFanInBathroomSwitchedOn(
                Clunet.sendResponsible(connection,
                        Address.RELAY_2,
                        Priority.COMMAND,
                        Command.SWITCH,
                        new byte[]{(byte) 0xFF},
                        new SupradinConnectionResponseFilter() {

                            @Override
                            public boolean filter(SupradinDataMessage supradinRecieved) {
                                return supradinRecieved.getCommand() == Command.SWITCH_INFO
                                && supradinRecieved.getData().length == 1;
                            }
                        }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
    }
    
    
    /**
     * Переключает свет в зеркальном шкафу в ванной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean toggleLightInBathroom(SupradinConnection connection) {
        return Clunet.sendResponsible(connection,
                Address.RELAY_2,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte) 2, RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.RELAY_2
                        && supradinRecieved.getCommand() == Command.SWITCH_INFO
                        && supradinRecieved.getData().length == 1;
                    }
                }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    /**
     * Управляет включением света в зеркальном шкафу в ванной
     *
     * @param connection текущее соединение
     * @param on признак включить/выключить
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchLightInBathroom(SupradinConnection connection, final boolean on) {
        return Clunet.sendResponsible(connection,
                Address.RELAY_2,
                Priority.COMMAND,
                Command.SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.RELAY_2
                        && supradinRecieved.getCommand() == Command.SWITCH_INFO
                        && supradinRecieved.getData().length == 1
                        && ((supradinRecieved.getData()[0] >> (RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                    }
                }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    
    /**
     * Определяет включен ли свет в зеркальном шкафу в ванной комнате
     *
     * @param message входящее сообщение для анализа
     * @return возвращает true - если свет включен; false - если свет выключен;
     * null - если передано неверное по формату сообщение для анализа
     */
    public static Boolean checkLightInBathroomSwitchedOn(SupradinDataMessage message) {
        if (message != null) {
            if (message.getSrc() == Address.RELAY_2
                    && message.getCommand() == Command.SWITCH_INFO
                    && message.getData().length == 1) {
                return ((message.getData()[0] >> (RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID - 1)) & 1) == 1;
            }
        }
        return null;
    }

    /**
     * Проверяет включен ли свет в зеркальном шкафу в ванной комнате Отправляет
     * команду на проверку состояния выключателей и в случае успешного ее
     * выполнения производит анализ состояния выключателя света в зеркальном
     * шкафу в ванной
     *
     * @param connection текущее соединение
     * @return возвращает true - если свет включен; false - если свет выключен;
     * null - не удалось определить (возникла ошибка при отправке или ответ не
     * получен)
     */
    public static Boolean isLightInBathroomSwitchedOn(SupradinConnection connection) {
        return checkFanInBathroomSwitchedOn(
                Clunet.sendResponsible(connection,
                        Address.RELAY_2,
                        Priority.COMMAND,
                        Command.SWITCH,
                        new byte[]{(byte) 0xFF},
                        new SupradinConnectionResponseFilter() {

                            @Override
                            public boolean filter(SupradinDataMessage supradinRecieved) {
                                return supradinRecieved.getCommand() == Command.SWITCH_INFO
                                && supradinRecieved.getData().length == 1;
                            }
                        }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
    }
    
    
     /**
     * Управляет выбором источника аудиосигнла
     *
     * @param connection текущее соединение
     * @param source идентификатор источника сигнала
     * @return возвращает true, если команда успешно выполнена
     */
    private static boolean selectSourceOfSound(SupradinConnection connection, final Address address, final int source) {
        return Clunet.sendResponsible(connection,
                address,
                Priority.COMMAND,
                Command.CHANNEL,
                new byte[]{0x00, (byte)source},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == address
                                && supradinRecieved.getCommand() == Command.CHANNEL_INFO
                                && supradinRecieved.getData().length == 1
                                && supradinRecieved.getData()[0] == source;
                    }
                }, AUDIO_SELECT_CHANNEL_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    
    /**
     * Управляет выбором источника аудиосигнла в комнате
     *
     * @param connection текущее соединение
     * @param source идентификатор источника сигнала
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean selectSourceOfSoundInRoom(SupradinConnection connection, final int source) {
       return selectSourceOfSound(connection, Address.AUDIOBOX, source);
    }
    
     /**
     * Управляет выбором источника аудиосигнла в ванной комнате
     *
     * @param connection текущее соединение
     * @param source идентификатор источника сигнала
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean selectSourceOfSoundInBathroom(SupradinConnection connection, final int source) {
       return selectSourceOfSound(connection, Address.AUDIOBATH, source);
    }

    /**
     * Определяет номер источника аудиосигнала в комнате по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает номер источника аудиосигнала;
     *                    null - если передано неверное по формату сообщение для анализа
     */
    public static Integer getSelectedSourceOfSoundInRoom(SupradinDataMessage message){
        if (message != null){
            if (message.getSrc() == Address.AUDIOBOX
                    && message.getCommand() == Command.CHANNEL_INFO
                    && message.getData().length == 1){
                return (int)message.getData()[0];
            }
        }
        return null;
    }
    
    /**
     * Определяет номер источника аудиосигнала в в ванной комнате по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает номер источника аудиосигнала;
     *                    null - если передано неверное по формату сообщение для анализа
     */
    public static Integer getSelectedSourceOfSoundInBathRoom(SupradinDataMessage message){
        if (message != null){
            if (message.getSrc() == Address.AUDIOBATH
                    && message.getCommand() == Command.CHANNEL_INFO
                    && message.getData().length == 1){
                return (int)message.getData()[0];
            }
        }
        return null;
    }

    /**
     * Определяет номер источника аудиосигнала в комнате.
     * Отправляет команду аудиобоксу и
     * в случае успешного ее выполнения определяет номер активного источника аудиосигнала
     *
     * @param connection текущее соединение
     * @return возвращает номер источника аудиосигнала;
     *                    null - не удалось определить (возникла ошибка при отправке или ответ не получен)
     */
    public static Integer getSelectedSourceOfSoundInRoom(SupradinConnection connection) {
        SupradinDataMessage response = Clunet.sendResponsible(connection,
                Address.AUDIOBOX,
                Priority.COMMAND,
                Command.CHANNEL,
                new byte[]{(byte)0xFF},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.AUDIOBOX
                                && supradinRecieved.getCommand() == Command.CHANNEL_INFO
                                && supradinRecieved.getData().length == 1;
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE);
        return getSelectedSourceOfSoundInRoom(response);
    }

    
    
    /**
     * Определяет номер источника аудиосигнала в ванной комнате.
     * Отправляет команду аудиобоксу и
     * в случае успешного ее выполнения определяет номер активного источника аудиосигнала
     *
     * @param connection текущее соединение
     * @return возвращает номер источника аудиосигнала;
     *                    null - не удалось определить (возникла ошибка при отправке или ответ не получен)
     */
    public static Integer getSelectedSourceOfSoundInBathRoom(SupradinConnection connection) {
        SupradinDataMessage response = Clunet.sendResponsible(connection,
                Address.AUDIOBATH,
                Priority.COMMAND,
                Command.CHANNEL,
                new byte[]{(byte)0xFF},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.AUDIOBATH
                                && supradinRecieved.getCommand() == Command.CHANNEL_INFO
                                && supradinRecieved.getData().length == 1;
                    }
                }, AUDIOBATH_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE);
        return getSelectedSourceOfSoundInBathRoom(response);
    }

    /**
     * Выключает звук
     *
     * @param connection текущее соединение
     * @param address адрес устройства в сети clunet
     * @return возвращает true, если команда успешно выполнена
     */
     public static boolean mute(SupradinConnection connection, final Address address) {
        return Clunet.sendResponsible(connection,
                address,
                Priority.COMMAND,
                Command.MUTE,
                new byte[]{0x00}, //пробуем переключить (как с пульта)
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == address
                        && supradinRecieved.getCommand() == Command.VOLUME_INFO
                        && supradinRecieved.getData().length == 2
                        && supradinRecieved.getData()[0] == 0x00;
                    }
                }, AUDIO_CHANGE_VOLUME_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }

    
    /**
     * Выключает звук в комнате
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean muteInRoom(SupradinConnection connection) {
       return mute(connection, Address.AUDIOBOX);
    }
    
      /**
     * Выключает звук в ванной комнате
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean muteInBathroom(SupradinConnection connection) {
       return mute(connection, Address.AUDIOBATH);
    }


    /**
     * Устанавливает уровень громкости (в процентах от максимального) звука в
     * комнате
     *
     * @param connection текущее соединение
     * @param percent уровень громкости в процентах
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean setSoundVolumeLevelInRoom(SupradinConnection connection, final int percent) {
        return Clunet.sendResponsible(connection,
                Address.AUDIOBOX,
                Priority.COMMAND,
                Command.VOLUME,
                new byte[]{0x00, (byte) percent},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.AUDIOBOX
                        && supradinRecieved.getCommand() == Command.VOLUME_INFO
                        && supradinRecieved.getData().length == 2 /*&& supradinRecieved.getData()[0] == percent*/;       //TODO: баг с передачей громкости в процентах
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }
    
     /**
     * Устанавливает уровень громкости (в процентах от максимального) звука в
     * ванной комнате
     *
     * @param connection текущее соединение
     * @param percent уровень громкости в процентах
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean setSoundVolumeLevelInBathRoom(SupradinConnection connection, final int percent) {
        return Clunet.sendResponsible(connection,
                Address.AUDIOBATH,
                Priority.COMMAND,
                Command.VOLUME,
                new byte[]{0x00, (byte) percent},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.AUDIOBATH
                        && supradinRecieved.getCommand() == Command.VOLUME_INFO
                        && supradinRecieved.getData().length == 2 /*&& supradinRecieved.getData()[0] == percent*/;       //TODO: баг с передачей громкости в процентах
                    }
                }, AUDIOBATH_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }
    
    /**
     * Изменяет уровень громкости звука
     *
     * @param connection текущее соединение
     * @param inc признак задает увеличение или уменьшение уровня громкости
     * @return возвращает true, если команда успешно выполнена
     */
    private static boolean changeSoundVolumeLevel(SupradinConnection connection, final Address address, final boolean inc) {
        return Clunet.sendResponsible(connection,
                address,
                Priority.COMMAND,
                Command.VOLUME,
                new byte[]{(byte)(inc ? 0x02 : 0x03)},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == address
                        && supradinRecieved.getCommand() == Command.VOLUME_INFO
                        && supradinRecieved.getData().length == 2;
                    }
                }, AUDIO_CHANGE_VOLUME_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }
    
    /**
     * Изменяет уровень громкости звука в комнате
     *
     * @param connection текущее соединение
     * @param inc признак задает увеличение или уменьшение уровня громкости
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean changeSoundVolumeLevelInRoom(SupradinConnection connection, final boolean inc) {
        return changeSoundVolumeLevel(connection, Address.AUDIOBOX, inc);
    }
    
     /**
     * Изменяет уровень громкости звука в ванной комнате
     *
     * @param connection текущее соединение
     * @param inc признак задает увеличение или уменьшение уровня громкости
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean changeSoundVolumeLevelInBathroom(SupradinConnection connection, final boolean inc) {
        return changeSoundVolumeLevel(connection, Address.AUDIOBATH, inc);
    }
  
    /**
     * Определяет уровень громкости (в процентах от максимального) звука в
     * комнате по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает уровень громкости звука в комнате; null - если
     * передано неверное по формату сообщение для анализа
     */
    public static Integer getSoundVolumeLevelInRoom(SupradinDataMessage message) {
        if (message != null) {
            if (message.getSrc() == Address.AUDIOBOX
                    && message.getCommand() == Command.VOLUME_INFO
                    && message.getData().length == 2
                    && message.getData()[0] >= 0 && message.getData()[0] <= 100) {
                return (int) message.getData()[0];
            }
        }
        return null;
    }
    
        /**
     * Определяет уровень громкости (в процентах от максимального) звука в
     * ванной комнате по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает уровень громкости звука в комнате; null - если
     * передано неверное по формату сообщение для анализа
     */
    public static Integer getSoundVolumeLevelInBathRoom(SupradinDataMessage message) {
        if (message != null) {
            if (message.getSrc() == Address.AUDIOBATH
                    && message.getCommand() == Command.VOLUME_INFO
                    && message.getData().length == 2
                    && message.getData()[0] >= 0 && message.getData()[0] <= 100) {
                return (int) message.getData()[0];
            }
        }
        return null;
    }

    /**
     * Определяет уровень громкости (в процентах от максимального) звука в
     * комнате
     *
     * @param connection текущее соединение
     * @return возвращает уровень громкости звука в комнате; null - не удалось
     * определить (возникла ошибка при отправке или ответ не получен)
     */
    public static Integer getSoundVolumeLevelInRoom(SupradinConnection connection) {
        return getSoundVolumeLevelInRoom(Clunet.sendResponsible(connection,
                Address.AUDIOBOX,
                Priority.COMMAND,
                Command.VOLUME,
                new byte[]{(byte) 0xFF},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.AUDIOBOX
                        && supradinRecieved.getCommand() == Command.VOLUME_INFO
                        && supradinRecieved.getData().length == 2;
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
    }
    
    
        /**
     * Определяет уровень громкости (в процентах от максимального) звука в
     * ванной комнате
     *
     * @param connection текущее соединение
     * @return возвращает уровень громкости звука в комнате; null - не удалось
     * определить (возникла ошибка при отправке или ответ не получен)
     */
    public static Integer getSoundVolumeLevelInBathRoom(SupradinConnection connection) {
        return getSoundVolumeLevelInBathRoom(Clunet.sendResponsible(connection,
                Address.AUDIOBATH,
                Priority.COMMAND,
                Command.VOLUME,
                new byte[]{(byte) 0xFF},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Address.AUDIOBATH
                        && supradinRecieved.getCommand() == Command.VOLUME_INFO
                        && supradinRecieved.getData().length == 2;
                    }
                }, AUDIOBATH_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
    }
    
    
    
    /**
     * Изменяет значения экавалайзера звука в комнате
     *
     * @param connection текущее соединение
     * @param param параметр эквалайзера для изменения
     * {@link Commands.EQUALIZER_GAIN}, {@link Commands.EQUALIZER_TREBLE}, {@link Commands.EQUALIZER_BASS}
     * @param inc признак задает увеличение или уменьшение значения параметра
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean changeSoundEqualizerInRoom(SupradinConnection connection, final int param, final boolean inc) {
        if (param == EQUALIZER_TREBLE || param == EQUALIZER_GAIN || param == EQUALIZER_BASS) {
            return Clunet.sendResponsible(connection,
                    Address.AUDIOBOX,
                    Priority.COMMAND,
                    Command.EQUALIZER,
                    new byte[]{(byte) param, (byte) (inc ? 0x02 : 0x03)},
                    new SupradinConnectionResponseFilter() {

                        @Override
                        public boolean filter(SupradinDataMessage supradinRecieved) {
                            return supradinRecieved.getSrc() == Address.AUDIOBOX
                            && supradinRecieved.getCommand() == Command.EQUALIZER_INFO
                            && supradinRecieved.getData().length == 3;
                        }
                    }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
        }
        return false;
    }

    
    private static boolean selectNextFMStation(SupradinConnection connection, final Address address, final boolean up){
        return Clunet.sendResponsible(connection,
                address,
                Priority.COMMAND,
                Command.FM,
                    new byte[]{(byte) (up ? 0x02 : 0x03)},
                    new SupradinConnectionResponseFilter() {

                        @Override
                        public boolean filter(SupradinDataMessage supradinRecieved) {
                            return supradinRecieved.getSrc() == address
                            && supradinRecieved.getCommand() == Command.FM_INFO
                            && supradinRecieved.getData().length == 6;
                        }
                    }, AUDIO_SELECT_CHANNEL_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    public static boolean selectNextFMStationInRoom(SupradinConnection connection, final boolean up){
        return selectNextFMStation(connection, Address.AUDIOBOX, up);
    }
    
    public static boolean selectNextFMStationInBathroom(SupradinConnection connection, final boolean up){
        return selectNextFMStation(connection, Address.AUDIOBATH, up);
    }
    
    private static boolean selectFMFrequency(SupradinConnection connection, final Address address, float frequency) {
        int freq = (int)(frequency * 100);
        final byte byte1 = (byte)(freq & 0xFF);
        final byte byte2 = (byte)((freq >> 8) & 0xFF);
        return Clunet.sendResponsible(connection,
                address,
                Priority.COMMAND,
                Command.FM,
                new byte[]{(byte) 0x00, byte1, byte2},
                new SupradinConnectionResponseFilter() {

            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getSrc() == address
                        && supradinRecieved.getCommand() == Command.FM_INFO
                        && supradinRecieved.getData().length == 6
                        && supradinRecieved.getData()[0] == 0x00
                        && supradinRecieved.getData()[2] == byte1
                        && supradinRecieved.getData()[3] == byte2;
            }
        }, AUDIO_SELECT_CHANNEL_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    public static boolean selectFMFrequencyInRoom(SupradinConnection connection, float frequency){
        return selectFMFrequency(connection, Address.AUDIOBOX, frequency);
    }
    
    public static boolean selectFMFrequencyInBathroom(SupradinConnection connection, float frequency){
        return selectFMFrequency(connection, Address.AUDIOBATH, frequency);
    }

    public static Integer checkAndroidInBathtroomCommand(SupradinDataMessage message) {
        if (message != null) {
            switch (message.getCommand()) {
                case LIGHT_LEVEL_INFO:
                    if (message.getSrc() == Address.BATH_SENSORS && message.getData().length == 2) {
                        return message.getData()[0] == 1 ? 1 : 0;
                    }
                    break;
                case ANDROID:
                    if (message.getData().length == 1) {
                        return (int) message.getData()[0];
                    }
                    break;
                case RC_BUTTON_PRESSED:
                    if (message.getData().length == 3){
                        if (message.getData()[0] == 0x00 && message.getData()[1] == 0x00){
                            switch (message.getData()[2]){
                                case 0x42:
                                    return 0x02;
                                case 0x52:
                                    return 0x0A;
                            }
                        }
                    }
            }
        }

        return null;
    }
    
    private static void writeFMStationsToEEPROM(SupradinConnection connection, final Address address) {
        FMDictionary fmDict = FMDictionary.getInstance();
        if (fmDict != null) {
            //стираем
            if (Clunet.sendResponsible(connection,
                    address,
                    Priority.COMMAND,
                    Command.FM, new byte[]{(byte) 0xEE, (byte) 0xEE, (byte) 0xFF},
                    new SupradinConnectionResponseFilter() {
                @Override
                public boolean filter(SupradinDataMessage supradinRecieved) {
                    return supradinRecieved.getSrc() == address
                            && supradinRecieved.getCommand() == Command.FM_INFO
                            && supradinRecieved.getData().length == 2
                            && supradinRecieved.getData()[0] == (byte)0xEE
                            && supradinRecieved.getData()[1] == (byte)0x01;
                }
            }, FM_EEPROM_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null) {

                //пишем
                int i = 0;
                for (Map.Entry<Float, String> entry : fmDict.getStationList().entrySet()) {

                    int freq = (int) (entry.getKey() * 100);

                    Clunet.sendResponsible(connection,
                            address,
                            Priority.COMMAND,
                            Command.FM, new byte[]{(byte) 0xED, (byte) i++, (byte) (freq & 0xFF), (byte) ((freq >> 8) & 0xFF)},
                            new SupradinConnectionResponseFilter() {
                        @Override
                        public boolean filter(SupradinDataMessage supradinRecieved) {
                            return supradinRecieved.getSrc() == address
                                    && supradinRecieved.getCommand() == Command.FM_INFO
                                    && supradinRecieved.getData().length == 2
                                    && supradinRecieved.getData()[0] == (byte)0xED;
                        }
                    }, FM_EEPROM_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND);
                }

            }
        }
    }

    public static void writeFMStationsTOEEPROMInRoom(SupradinConnection connection){
        writeFMStationsToEEPROM(connection, Address.AUDIOBOX);
    }
    
    public static void writeFMStationsTOEEPROMInBathroom(SupradinConnection connection){
        writeFMStationsToEEPROM(connection, Address.AUDIOBATH);
    }
    
    
    public static void writeHeatfloorProgramsToEEPROM(SupradinConnection connection) {
        HeatFloorDictionary hfDict = HeatFloorDictionary.getInstance();
        if (hfDict != null) {

            //сначала стирать ??
            for (int i = 0; i < 10; i++) {   //max 10 programs
                final HeatfloorProgram program = hfDict.getProgramList().get(i);
                if (program != null) {

                    final byte[] data = new byte[program.getSchedule().length + 1];
                    data[0] = (byte) (0xF0 | i);
                    for (int j = 0; j < program.getSchedule().length; j++) {
                        data[j + 1] = (byte)((int)program.getSchedule()[j]);
                    }

                    Clunet.sendResponsible(connection,
                            Address.RELAY_1,
                            Priority.COMMAND,
                            Command.HEATFLOOR, data,
                            new SupradinConnectionResponseFilter() {
                        @Override
                        public boolean filter(SupradinDataMessage supradinRecieved) {
                            return supradinRecieved.getSrc() == Address.RELAY_1
                                    && supradinRecieved.getCommand() == Command.HEATFLOOR_INFO
                                    && supradinRecieved.getData().length > 2
                                    && supradinRecieved.getData()[0] == data[0]
                                    && supradinRecieved.getData()[1] == program.getSchedule().length / 2;
                        }
                    }, HEATFLOOR_EEPROM_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND);
                }
            }
        }
    }
    
    private static void selectHeatfloorMode(SupradinConnection connection, int mode, int channel, byte[] params) {
        byte[] rdata = new byte[params.length + 3];
        rdata[0] = (byte) 0xFE;
        rdata[1] = (byte) (0x01 << channel);
        rdata[2] = (byte) mode;
        System.arraycopy(params, 0, rdata, 3, params.length);

        Clunet.sendResponsible(connection,
                Address.RELAY_1,
                Priority.COMMAND,
                Command.HEATFLOOR, rdata,
                new SupradinConnectionResponseFilter() {
            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getSrc() == Address.RELAY_1
                        && supradinRecieved.getCommand() == Command.HEATFLOOR_INFO;
            }
        }, HEATFLOOR_EEPROM_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND);
    }
    
    public static void selectHeatfloorModeOff(SupradinConnection connection, int channel){
        selectHeatfloorMode(connection, SmarthomeDictionary.HEATFLOOR_MODE_OFF, channel, new byte[]{});
    }
    
    public static void selectHeatfloorModeManual(SupradinConnection connection, int channel, int temperature){
        selectHeatfloorMode(connection, SmarthomeDictionary.HEATFLOOR_MODE_MANUAL, channel, new byte[]{(byte)temperature});
    }
    
    public static void selectHeatfloorModeDay(SupradinConnection connection, int channel, int program){
        selectHeatfloorMode(connection, SmarthomeDictionary.HEATFLOOR_MODE_DAY, channel, new byte[]{(byte)program});
    }
    
    public static void selectHeatfloorModeWeek(SupradinConnection connection, int channel, int program_mo_fr, int program_sa, int program_su){
        selectHeatfloorMode(connection, SmarthomeDictionary.HEATFLOOR_MODE_WEEK, channel, 
                new byte[]{(byte)program_mo_fr, (byte)program_sa, (byte)program_su});
    }
    
    public static void selectHeatfloorModeParty(SupradinConnection connection, int channel, int temperature, int num_seconds){
        selectHeatfloorMode(connection, SmarthomeDictionary.HEATFLOOR_MODE_PARTY, channel,
                new byte[]{(byte)temperature, (byte)(num_seconds & 0xFF), (byte)((num_seconds >> 8) & 0xFF)});
    }
    
    public static void selectHeatfloorModeDayForToday(SupradinConnection connection, int channel, int program){
        selectHeatfloorMode(connection, SmarthomeDictionary.HEATFLOOR_MODE_DAY_FOR_TODAY, channel, new byte[]{(byte)program});
    }
    
    
    /**
     * Устанавливает уровень диммера 
     *
     * @param connection текущее соединение
     * @param deviceId идентификатор устройства
     * @param dimmerChannel номер канала диммера
     * @param value значение диммера (0-255)
     * @param responseTimeout
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean setDimmerLevel(SupradinConnection connection,
            final Address deviceId, final int dimmerChannel, final int value,
            int responseTimeout
            ) {
        return Clunet.sendResponsible(connection,
                deviceId,
                Priority.COMMAND,
                Command.DIMMER,
                new byte[]{(byte) dimmerChannel, (byte) value},
                new SupradinConnectionResponseFilter() {

            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getSrc() == deviceId
                        && supradinRecieved.getCommand() == Command.DIMMER_INFO
                        && supradinRecieved.getData().length == 3;
            }
        }, responseTimeout, NUM_ATTEMPTS_STATE) != null;
    }
    
    public static boolean setDimmerLevelInKitchenLight(SupradinConnection connection, final int value) {
        return setDimmerLevel(connection, Address.KITCHEN_LIGHT, 1, value, KITCHEN_LIGHT_WAITING_RESPONSE_TIMEOUT);
    }
    
    public static boolean setDimmerLevelInSocketDimmer(SupradinConnection connection, final int value) {
        return setDimmerLevel(connection, Address.SOCKET_DIMMER, 1, value, SOCKET_DIMMER_WAITING_RESPONSE_TIMEOUT);
    }
    
    /**
     * Определяет уровень диммера (0-255) по сообщению
     *
     * @param message входящее сообщение для анализа
     * @param deviceId
     * @param dimmerChannel
     * @return возвращает значение диммера; null - если передано неверное по
     * формату сообщение для анализа
     */
    private static Integer getDimmerLevel(SupradinDataMessage message, Address deviceId, int dimmerChannel) {
        if (message != null) {
            if (message.getSrc() == deviceId
                    && message.getCommand() == Command.DIMMER_INFO
                    && message.getData().length > 1) {
                int cnt = message.getData()[0] & 0xFF;
                if (message.getData().length == cnt * 2 + 1) {
                    for (int i = 1; i < message.getData().length; i += 2) {
                        if ((message.getData()[i] & 0xFF) == dimmerChannel) {
                            return message.getData()[i + 1] & 0xFF;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    
        public static Integer getDimmerLevelInKitchen(SupradinDataMessage message) {
            return getDimmerLevel(message, Address.KITCHEN_LIGHT, 1);
        }
        
        public static Integer getDimmerLevelInSocket(SupradinDataMessage message) {
            return getDimmerLevel(message, Address.SOCKET_DIMMER, 1);
        }

    /**
     * Получает значение диммера на кухне
     *
     * @param connection текущее соединение
     * @return возвращает значение диммера на кухне; null - не удалось
     * определить (возникла ошибка при отправке или ответ не получен)
     */
    public static Integer getDimmerLevelInKitchen(SupradinConnection connection) {
        return getDimmerLevel(Clunet.sendResponsible(connection,
                Address.KITCHEN_LIGHT,
                Priority.COMMAND,
                Command.DIMMER,
                new byte[]{(byte) 0xFF},
                new SupradinConnectionResponseFilter() {

            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getSrc() == Address.KITCHEN_LIGHT
                        && supradinRecieved.getCommand() == Command.DIMMER_INFO;
            }
        }, KITCHEN_LIGHT_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE),
                Address.KITCHEN_LIGHT, 1);
    }
    
       /**
     * Получает значение диммера в комнате
     *
     * @param connection текущее соединение
     * @return возвращает значение диммера в комнате; null - не удалось
     * определить (возникла ошибка при отправке или ответ не получен)
     */
    public static Integer getDimmerLevelInSocket(SupradinConnection connection) {
        return getDimmerLevel(Clunet.sendResponsible(connection,
                Address.SOCKET_DIMMER,
                Priority.COMMAND,
                Command.DIMMER,
                new byte[]{(byte) 0xFF},
                new SupradinConnectionResponseFilter() {

            @Override
            public boolean filter(SupradinDataMessage supradinRecieved) {
                return supradinRecieved.getSrc() == Address.SOCKET_DIMMER
                        && supradinRecieved.getCommand() == Command.DIMMER_INFO;
            }
        }, KITCHEN_LIGHT_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE),
                Address.SOCKET_DIMMER, 1);
    }
}
