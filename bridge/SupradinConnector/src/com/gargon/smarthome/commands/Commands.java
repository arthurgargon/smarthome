package com.gargon.smarthome.commands;


import com.gargon.smarthome.clunet.Clunet;
import com.gargon.smarthome.supradin.SupradinConnection;
import com.gargon.smarthome.supradin.SupradinConnectionResponseFilter;
import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

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
    public static final int ROOM_AUDIOSOURCE_RADIO = 4;


    public static final int RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID = 1;
    public static final int RELAY_1_FLOOR_KITCHEN_SWITCH_ID = 2;
    public static final int RELAY_1_FLOOR_BATHROOM_SWITCH_ID = 3;
    
    //public static final int RELAY_2_SWITCH_1_ID = 1;
    public static final int RELAY_2_FAN_SWITCH_ID = 2;
    public static final int RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID = 3;
    
    
    private static final int RELAY_1_WAITING_RESPONSE_TIMEOUT  = 200; //ms
    private static final int RELAY_2_WAITING_RESPONSE_TIMEOUT  = 200; //ms
    private static final int AUDIOBOX_WAITING_RESPONSE_TIMEOUT = 150; //ms

    private static final int NUM_ATTEMPTS_COMMAND = 5;
    private static final int NUM_ATTEMPTS_STATE = 2;


     /**
     * Переключает свет в гардеробной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchLightInCloackroom(SupradinConnection connection) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_RELAY_1,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte) 2, RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_RELAY_1
                                && supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
                Clunet.ADDRESS_RELAY_1,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_RELAY_1
                                && supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
    public static Boolean isSwitchLightOnInCloackroom(SupradinDataMessage message){
        if (message != null){
            if (message.getSrc() == Clunet.ADDRESS_RELAY_1
                    && message.getCommand() == Clunet.COMMAND_SWITCH_INFO
                    && message.getData().length == 1){
                return ((message.getData()[0] >> (RELAY_1_LIGHT_CLOACKROOM_SWITCH_ID - 1)) & 1) == 1;
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
    public static Boolean isSwitchLightOnInCloackroom(SupradinConnection connection) {
        return isSwitchLightOnInCloackroom(Clunet.sendResponsible(connection,
                Clunet.ADDRESS_RELAY_1,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte)0xFF},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
                Clunet.ADDRESS_RELAY_1,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_1_FLOOR_KITCHEN_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_RELAY_1
                                && supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
                Clunet.ADDRESS_RELAY_1,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_1_FLOOR_BATHROOM_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_RELAY_1
                                && supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
                                && supradinRecieved.getData().length == 1
                                && ((supradinRecieved.getData()[0] >> (RELAY_1_FLOOR_BATHROOM_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                    }
                }, RELAY_1_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    
    
    /**
     * Переключает вентилятор в ванной
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchFanInBathroom(SupradinConnection connection) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_RELAY_2,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte) 2, RELAY_2_FAN_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_RELAY_2
                                && supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
                                && supradinRecieved.getData().length == 1;
                    }
                }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
     /**
     * Управляет включением вентилятора в ванной
     *
     * @param connection текущее соединение
     * @param on признак включить/выключить
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean switchFanInBathroom(SupradinConnection connection, final boolean on) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_RELAY_2,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_2_FAN_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_RELAY_2
                        && supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
                        && supradinRecieved.getData().length == 1
                        && ((supradinRecieved.getData()[0] >> (RELAY_2_FAN_SWITCH_ID - 1)) & 1) == (on ? 1 : 0);
                    }
                }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
    }
    
    
    /**
     * Определяет включен ли вентилятор в ванной по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает true - если вентилятор в ванной включен; false - если
     * вентилятор в ванной выключен; null - если передано неверное по формату
     * сообщение для анализа
     */
    public static Boolean isSwitchFanOnInBathroom(SupradinDataMessage message) {
        if (message != null) {
            if (message.getSrc() == Clunet.ADDRESS_RELAY_2
                    && message.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
    public static Boolean isSwitchFanOnInBathroom(SupradinConnection connection) {
        return isSwitchFanOnInBathroom(
                Clunet.sendResponsible(connection,
                        Clunet.ADDRESS_RELAY_2,
                        Clunet.PRIORITY_COMMAND,
                        Clunet.COMMAND_SWITCH,
                        new byte[]{(byte) 0xFF},
                        new SupradinConnectionResponseFilter() {

                            @Override
                            public boolean filter(SupradinDataMessage supradinRecieved) {
                                return supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
    public static boolean switchLightInMirroredBoxInBathroom(SupradinConnection connection) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_RELAY_2,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte) 2, RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_RELAY_2
                        && supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
    public static boolean switchLightInMirroredBoxInBathroom(SupradinConnection connection, final boolean on) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_RELAY_2,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_SWITCH,
                new byte[]{(byte) (on ? 1 : 0), RELAY_2_LIGHT_MIRRORED_BOX_SWITCH_ID},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_RELAY_2
                        && supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
    public static Boolean isSwitchLightOnInMirroredBoxInBathroom(SupradinDataMessage message) {
        if (message != null) {
            if (message.getSrc() == Clunet.ADDRESS_RELAY_2
                    && message.getCommand() == Clunet.COMMAND_SWITCH_INFO
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
    public static Boolean isSwitchLightOnInMirroredBoxInBathroom(SupradinConnection connection) {
        return isSwitchFanOnInBathroom(
                Clunet.sendResponsible(connection,
                        Clunet.ADDRESS_RELAY_2,
                        Clunet.PRIORITY_COMMAND,
                        Clunet.COMMAND_SWITCH,
                        new byte[]{(byte) 0xFF},
                        new SupradinConnectionResponseFilter() {

                            @Override
                            public boolean filter(SupradinDataMessage supradinRecieved) {
                                return supradinRecieved.getCommand() == Clunet.COMMAND_SWITCH_INFO
                                && supradinRecieved.getData().length == 1;
                            }
                        }, RELAY_2_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
    }
    
    /**
     * Управляет выбором источника аудиосигнла в комнате
     *
     * @param connection текущее соединение
     * @param source идентификатор источника сигнала
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean selectSourceOfSoundInRoom(SupradinConnection connection, final int source) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_AUDIOBOX,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_CHANNEL,
                new byte[]{0x00, (byte)source},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_AUDIOBOX
                                && supradinRecieved.getCommand() == Clunet.COMMAND_CHANNEL_INFO
                                && supradinRecieved.getData().length == 1
                                && supradinRecieved.getData()[0] == source;
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_COMMAND) != null;
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
            if (message.getSrc() == Clunet.ADDRESS_AUDIOBOX
                    && message.getCommand() == Clunet.COMMAND_CHANNEL_INFO
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
                Clunet.ADDRESS_AUDIOBOX,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_CHANNEL,
                new byte[]{(byte)0xFF},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_AUDIOBOX
                                && supradinRecieved.getCommand() == Clunet.COMMAND_CHANNEL_INFO
                                && supradinRecieved.getData().length == 1;
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE);
        return getSelectedSourceOfSoundInRoom(response);
    }


    
    /**
     * Выключает звук в комнате
     *
     * @param connection текущее соединение
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean muteInRoom(SupradinConnection connection) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_AUDIOBOX,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_MUTE,
                new byte[]{0x00}, //пробуем переключить (как с пульта)
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_AUDIOBOX
                        && supradinRecieved.getCommand() == Clunet.COMMAND_VOLUME_INFO
                        && supradinRecieved.getData().length == 2
                        && supradinRecieved.getData()[0] == 0x00;
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }


    /**
     * Устанавливает уровень громкости (в процентах от максимального) звука в
     * комнате
     *
     * @param connection текущее соединение
     * @param percent уровень громкости в процентах
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean setPercentVolumeOfSoundInRoom(SupradinConnection connection, final int percent) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_AUDIOBOX,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_VOLUME,
                new byte[]{0x00, (byte) percent},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_AUDIOBOX
                        && supradinRecieved.getCommand() == Clunet.COMMAND_VOLUME_INFO
                        && supradinRecieved.getData().length == 2 /*&& supradinRecieved.getData()[0] == percent*/;       //TODO: баг с передачей громкости в процентах
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }
    
    
    
    /**
     * Изменяет уровень громкости звука в комнате
     *
     * @param connection текущее соединение
     * @param inc признак задает увеличение или уменьшение уровня громкости
     * @return возвращает true, если команда успешно выполнена
     */
    public static boolean changeVolumeOfSoundInRoom(SupradinConnection connection, final boolean inc) {
        return Clunet.sendResponsible(connection,
                Clunet.ADDRESS_AUDIOBOX,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_VOLUME,
                new byte[]{(byte)(inc ? 0x02 : 0x03)},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_AUDIOBOX
                        && supradinRecieved.getCommand() == Clunet.COMMAND_VOLUME_INFO
                        && supradinRecieved.getData().length == 2;
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
    }
  
    /**
     * Определяет уровень громкости (в процентах от максимального) звука в
     * комнате по сообщению
     *
     * @param message входящее сообщение для анализа
     * @return возвращает уровень громкости звука в комнате; null - если
     * передано неверное по формату сообщение для анализа
     */
    public static Integer getPercentVolumeOfSoundInRoom(SupradinDataMessage message) {
        if (message != null) {
            if (message.getSrc() == Clunet.ADDRESS_AUDIOBOX
                    && message.getCommand() == Clunet.COMMAND_VOLUME_INFO
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
    public static Integer getVolumePercentOfSoundInRoom(SupradinConnection connection) {
        return getPercentVolumeOfSoundInRoom(Clunet.sendResponsible(connection,
                Clunet.ADDRESS_AUDIOBOX,
                Clunet.PRIORITY_COMMAND,
                Clunet.COMMAND_VOLUME,
                new byte[]{(byte) 0xFF},
                new SupradinConnectionResponseFilter() {

                    @Override
                    public boolean filter(SupradinDataMessage supradinRecieved) {
                        return supradinRecieved.getSrc() == Clunet.ADDRESS_AUDIOBOX
                        && supradinRecieved.getCommand() == Clunet.COMMAND_VOLUME_INFO
                        && supradinRecieved.getData().length == 2;
                    }
                }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE));
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
    public static boolean changeEqualizerOfSoundInRoom(SupradinConnection connection, final int param, final boolean inc) {
        if (param == EQUALIZER_TREBLE || param == EQUALIZER_GAIN || param == EQUALIZER_BASS) {
            return Clunet.sendResponsible(connection,
                    Clunet.ADDRESS_AUDIOBOX,
                    Clunet.PRIORITY_COMMAND,
                    Clunet.COMMAND_EQUALIZER,
                    new byte[]{(byte) param, (byte) (inc ? 0x02 : 0x03)},
                    new SupradinConnectionResponseFilter() {

                        @Override
                        public boolean filter(SupradinDataMessage supradinRecieved) {
                            return supradinRecieved.getSrc() == Clunet.ADDRESS_AUDIOBOX
                            && supradinRecieved.getCommand() == Clunet.COMMAND_EQUALIZER_INFO
                            && supradinRecieved.getData().length == 3;
                        }
                    }, AUDIOBOX_WAITING_RESPONSE_TIMEOUT, NUM_ATTEMPTS_STATE) != null;
        }
        return false;
    }


    /**
     * Проверяет должен ли быть планшет в ванной заблокирован или разблокирован
     * по входящему сообщению и текущему состоянию
     *
     * @param message входящее сообщение для анализа
     * @return возвращает true - если планшет должен быть разблоокирован; false
     * - если планшет должен быть разблокирован; null - если переданное
     * сообщение не имеет значения
     */
    public static Boolean isPadInBathtroomShouldBeUnlocked(SupradinDataMessage message, boolean isNowUnlocked) {
        if (message != null) {
            switch (message.getCommand()) {
                case Clunet.COMMAND_ANDROID:
                    if (message.getData().length == 1){
                        switch(message.getData()[0]){
                            case 0:
                                return false;
                            case 1: 
                                return true;
                            case 2:
                                return !isNowUnlocked;
                        }
                    }
                break;
                case Clunet.COMMAND_LIGHT_LEVEL_INFO:
                    if (message.getSrc() == Clunet.ADDRESS_BATH_SENSORS
                            && message.getData().length == 2) {
                        return message.getData()[0] == 0x01;
                    }
            }
        }
        return null;
    }

}
