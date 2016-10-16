package com.gargon.smarthome.clunet;

import com.gargon.smarthome.FMDictionary;
import com.gargon.smarthome.clunet.utils.DataFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author gargon
 */
public class ClunetDictionary {

    
    private static final Map<Integer, String> PRIORITIES = new LinkedHashMap(){{
        put(Clunet.PRIORITY_NOTICE, "PRIORITY_NOTICE");
        put(Clunet.PRIORITY_INFO, "PRIORITY_INFO");
        put(Clunet.PRIORITY_MESSAGE, "PRIORITY_MESSAGE");
        put(Clunet.PRIORITY_COMMAND, "PRIORITY_COMMAND");
    }};
    
    private static final Map<Integer, String> DEVICES = new LinkedHashMap(){{
        put(Clunet.ADDRESS_BROADCAST, "BROADCAST");
        put(Clunet.ADDRESS_SUPRADIN, "Supradin");
        
        put(Clunet.ADDRESS_AUDIOBOX, "AudioBox");
        put(Clunet.ADDRESS_AUDIOBATH, "AudioBath");
        
        put(Clunet.ADDRESS_RELAY_1, "Relay_1");
        put(Clunet.ADDRESS_RELAY_2, "Relay_2");
        put(Clunet.ADDRESS_KITCHEN_LIGHT, "KitchenLight");
        
        put(Clunet.ADDRESS_KITCHEN, "Kitchen");
        put(Clunet.ADDRESS_BATH_SENSORS, "BathSensors");
        put(Clunet.ADDRESS_WARDROBE, "Wardrobe");
    }};
    
    private static final Map<Integer, String> COMMANDS = new LinkedHashMap(){{
        put(Clunet.COMMAND_DISCOVERY, "Discovery");
        put(Clunet.COMMAND_DISCOVERY_RESPONSE, "DiscoveryResponse");
        put(Clunet.COMMAND_BOOT_CONTROL, "BootControl");
        put(Clunet.COMMAND_REBOOT, "Reboot");
        put(Clunet.COMMAND_BOOT_COMPLETED, "BootCompleted");
        
        put(Clunet.COMMAND_TIME, "Time");
        put(Clunet.COMMAND_TIME_INFO, "TimeInfo");
        
        put(Clunet.COMMAND_PING, "Ping");
        put(Clunet.COMMAND_PING_REPLY, "PingReply");
        
        put(Clunet.COMMAND_CHANNEL, "Channel");
        put(Clunet.COMMAND_CHANNEL_INFO, "ChannelInfo");
        
        put(Clunet.COMMAND_VOLUME, "Volume");
        put(Clunet.COMMAND_VOLUME_INFO, "VolumeInfo");
        put(Clunet.COMMAND_MUTE, "Mute");
        put(Clunet.COMMAND_EQUALIZER, "Equalizer");
        put(Clunet.COMMAND_EQUALIZER_INFO, "EqualizerInfo");
        
        put(Clunet.COMMAND_FM, "FM");
        put(Clunet.COMMAND_FM_INFO, "FMInfo");
        
        put(Clunet.COMMAND_POWER, "Power");
        put(Clunet.COMMAND_POWER_INFO, "PowerInfo");
        put(Clunet.COMMAND_SWITCH, "Switch");
        put(Clunet.COMMAND_SWITCH_INFO, "SwitchInfo");
        put(Clunet.COMMAND_BUTTON, "Button");
        put(Clunet.COMMAND_BUTTON_INFO, "ButtonInfo");
        
        put(Clunet.COMMAND_TEMPERATURE, "Temperature");
        put(Clunet.COMMAND_TEMPERATURE_INFO, "TemperatureInfo");
        put(Clunet.COMMAND_HUMIDITY, "Humidity");
        put(Clunet.COMMAND_HUMIDITY_INFO, "HumidityInfo");
        
        put(Clunet.COMMAND_ONEWIRE_SEARCH, "OneWireSearch");
        put(Clunet.COMMAND_ONEWIRE_INFO, "OneWireInfo");
        
        put(Clunet.COMMAND_MOTION, "Motion");
        put(Clunet.COMMAND_MOTION_INFO, "MotionInfo");
        
        put(Clunet.COMMAND_LIGHT_LEVEL, "LightLevel");
        put(Clunet.COMMAND_LIGHT_LEVEL_INFO, "LightLevelInfo");
        
        put(Clunet.COMMAND_FAN, "Fan");
        put(Clunet.COMMAND_FAN_INFO, "FanInfo");
        
        put(Clunet.COMMAND_DOORS, "Doors");
        put(Clunet.COMMAND_DOORS_INFO, "DoorsInfo");
        
        put(Clunet.COMMAND_HEATFLOOR, "Heatfloor");
        put(Clunet.COMMAND_HEATFLOOR_INFO, "HeatfloorInfo");
        
        put(Clunet.COMMAND_DEVICE_STATE, "DeviceState");
        put(Clunet.COMMAND_DEVICE_STATE_INFO, "DeviceStateInfo");
        
        put(Clunet.COMMAND_RC_BUTTON_PRESSED, "RCButtonPressed");
        
        put(Clunet.COMMAND_ANDROID, "Android");
        put(Clunet.COMMAND_DEBUG, "Debug");
    }};
    
    public static Map<Integer, String> getPrioritiesList(){
        return new LinkedHashMap(PRIORITIES);
    }
    
    public static Map<Integer, String> getDevicesList(){
        return new LinkedHashMap(DEVICES);
    }
    
    public static Map<Integer, String> getCommandsList(){
        return new LinkedHashMap(COMMANDS);
    }
    
    
    
    public static String getPriorityById(int id){
        return PRIORITIES.get(id);
    }
    
    public static String getDeviceById(int id){
        return DEVICES.get(id);
    }
    
    public static String getCommandById(int id){
        return COMMANDS.get(id);
    }
    
    
    
    
    //response decoders
    
    private static Float ds18b20Temperature(byte[] value, int startIndex) {
        if (value != null) {
            int v = ((value[startIndex+1] & 0xFF) << 8) | (value[startIndex] & 0xFF);
            if (v != 0xFFFF){
                return v / 10f;
            }
        }
        return null;
    }
    
    public static Map<String, Float> temperatureInfo(byte[] value) {
        try {
            Map<String, Float> r = new HashMap();
            if (value.length > 0) {
                int num = value[0] & 0xFF;
                int pos = 1;
                for (int i = 0; i < num; i++) {
                    int type = value[pos++];
                    String sensorId = null;
                    Float temperatureValue = null;
                    switch (type) {
                        case 0: //1-wire
                            sensorId = DataFormat.bytesToHex(Arrays.copyOfRange(value, pos, pos + 8));
                            //temperatureValue = (short) (((value[pos + 9] & 0xFF) << 8) | (value[pos + 8] & 0xFF)) * .0625f;
                            //temperatureValue = (((value[pos + 9] & 0xFF) << 8) | (value[pos + 8] & 0xFF)) / 10f;
                            temperatureValue = ds18b20Temperature(value, pos+8);
                            pos += 10;
                            break;
                        case 1: //dht
                            sensorId = "DHT-22 (" + String.valueOf(value[pos] & 0xFF) + ")";
                            if (value[pos + 2] != 0xFF && value[pos + 1] != 0xFF){
                                temperatureValue = (((value[pos + 2] & 0xFF) << 8) | (value[pos + 1] & 0xFF)) / 10f;
                            }
                            pos += 3;
                    }
                    if (temperatureValue != null) {
                        r.put(sensorId, temperatureValue);
                    }
                }
            }
            return r;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static List<String> oneWireInfo(byte[] value) {
        try {
            List<String> r = new ArrayList();
            if (value.length > 0) {
                for (int i = 0; i < value.length; i+=8) {
                    r.add(DataFormat.bytesToHex(Arrays.copyOfRange(value, i, i + 8)));
                }
            }
            return r;
        } catch (Exception e) {
            return null;
        }
    }
    
    /*Краткая информация о состоянии теплого пола, в консоле используется полная */
    public static Map<String, Integer> heatfloorInfo(byte[] value) {
        try {
            Map<String, Integer> r = null;
            if (value.length > 0 
                    && value[0] >=0 && value[0]<8) {
                r = new HashMap();
                int cnt = value[0];
                if (value.length == cnt * 6 + 1) {
                    for (int i = 0; i < cnt; i++) {
                        int index = i * 6 + 1;
                        r.put(String.valueOf(i), ((value[index+0] & 0xFF) << 8) | (value[index+1] & 0xFF)); //канал -> (режим | состояние)
                    }
                }
            }
            return r;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Float humidityInfo(byte[] value) {
        try {
            if (value[0] != (byte)0xFF && value[1] != (byte)0xFF){
                return (((value[1] & 0xFF) << 8) | (value[0] & 0xFF)) / 10f;
            }
        } catch (Exception e) {
        }
        return null;
    }

   public static int[] lightLevelInfo(byte[] value) {
        if (value.length == 2) {
            return new int[]{value[0], value[1]};
        }
        return null;
    }
    
    public static String heatfloorMode(int m) {
        String mode;
        switch (m) {
            case 0:
                mode = "Выкл.";
                break;
            case 1:
                mode = "режим ручной";
                break;
            case 2:
                mode = "режим дневной";
                break;
            case 3:
                mode = "режим недельный";
                break;
            case 4:
                mode = "режим вечеринка";
                break;
            case 5:
                mode = "режим дневной на день";
                break;
            default:
                mode = "???";
        }
        return mode;
    }
    
    public static String heatfloorModeExt(byte[] value, int startIndex){
        String mode = heatfloorMode(value[startIndex]);
        switch (value[startIndex]){
            case 1:
                mode = String.format("%s (t=%d °C)", mode, value[startIndex + 1]);
                break;
            case 2:
            case 5:
                mode = String.format("%s (Пр.=%d)", mode, value[startIndex + 1]);
                break;
            case 3:
                mode = String.format("%s (Пp.[пн-пт]=%d; Пр.[сб]=%d; Пр.[вс]=%d)", mode, value[startIndex + 1], value[startIndex + 2], value[startIndex + 3]);
                break;
            case 4:
                mode = String.format("%s (t=%d °C, осталось %d сек.)", mode, value[startIndex + 1], ((value[startIndex + 3] & 0xFF) << 8) | (value[startIndex + 2] & 0xFF));
                break;
        }
        return mode;
    }
    
    public static String fmInfo(byte[] value) {
        try {
            String r = null;
            switch (value[0]) {
                case 0x00:  //channel info
                    if (value.length == 6) {
                        float freq = (((value[3] & 0xFF) << 8) | (value[2] & 0xFF)) / 100f;
                        FMDictionary fmDictionary = FMDictionary.getInstance();
                        String station = null;
                        if (fmDictionary != null) {
                            station = fmDictionary.getStationList().get(freq);
                        }
                        r = String.format(Locale.ROOT, "FM: канал=%s; %sчастота=%.2f МГц; уровень=%d%%; %s",
                                value[1] < 0 ? "-" : (value[1] + 1),
                                station != null ? "Станция=\"" + station + "\"; " : "",
                                freq,
                                100 * value[4] / 15,
                                value[5] > 0 ? "Стерео" : ""
                        );
                    }
                    break;
                case 0x01:  //state info
                    if (value.length == 6) {
                        r = String.format("FM: standby=%s; mute=%s; mono=%s; hcc=%s; snc=%s",
                                value[1] > 0 ? "on" : "off",
                                value[2] > 0 ? "on" : "off",
                                value[3] > 0 ? "on" : "off",
                                value[4] > 0 ? "on" : "off",
                                value[5] > 0 ? "on" : "off");
                    }
                    break;
                case 0x02:  //search info
                    break;
            }
            return r;

        } catch (Exception e) {
        }
        return null;
    }
    
    public static String toString(int commandId, byte[] value) {
        switch (commandId) {
            case Clunet.COMMAND_DISCOVERY_RESPONSE:
                return String.format("Обнаружено устройство: %s", new String(value));
            case Clunet.COMMAND_BOOT_COMPLETED:
                return String.format("Устройство перезагружено");
            case Clunet.COMMAND_POWER_INFO:
                if (value.length == 1){
                    return String.format("Устройство %s", value[0] == 1 ? "включено" : "отключено");
                }
            case Clunet.COMMAND_SWITCH_INFO:
                if (value.length == 1){
                    if (value[0] == 0){
                        return "Отключены все выключатели";
                    }else{
                        String r = "Включены выключатели: ";
                        for (int i=0; i<8; i++){
                            if (((value[0]>>i) & 1) == 1){
                                r += (i+1) + ", ";
                            }
                        }
                        return r.substring(0, r.length() - 2);
                    }
                }
                break;
            case Clunet.COMMAND_BUTTON_INFO:
                if (value.length == 2){
                    return String.format("Кнопка %d: %s", value[0], value[1] == 1 ? "нажата" : "не нажата");
                }
                break;
             case Clunet.COMMAND_TIME_INFO:
                if (value.length == 7){
                    return String.format("Текущая дата: %02d-%02d-%04d %02d:%02d:%02d, %d день недели", value[2], value[1], value[0]+2000, value[3], value[4], value[5], value[6]);
                }
                break;
            case Clunet.COMMAND_TEMPERATURE_INFO:
                String response = "";
                Map<String, Float> t = temperatureInfo(value);
                if (t != null){
                    for (Map.Entry<String, Float> entry : t.entrySet()){
                        response += String.format(Locale.ROOT, "T[%s]=%.01f°C; ", entry.getKey(), entry.getValue());
                    }
                }
                return response;
            case Clunet.COMMAND_ONEWIRE_INFO:
                response = "";
                List<String> ow = oneWireInfo(value);
                if (ow != null) {
                    for (String o : ow) {
                        response += o + "; ";
                    }
                }
                return response;
            case Clunet.COMMAND_HUMIDITY_INFO:
                Float h = humidityInfo(value);
                if (h != null){
                    return String.format(Locale.ROOT, "H=%.01f%%", h);
                }
                break;
            case Clunet.COMMAND_MOTION_INFO:
                if (value.length == 2) {
                    response = value[0] == 1 ? "Обнаружено движение" : "Движение отсутствует";
                    response += " (локация "+value[1]+")";
                    return response;
                }
                break;
            case Clunet.COMMAND_DOORS_INFO:       
                if (value.length == 1) {
                    return value[0] > 0 ? "Дверь открыта" : "Дверь закрыта";
                }
                break;
            case Clunet.COMMAND_DEVICE_STATE_INFO:       
                if (value.length == 2) {
                    return String.format("Устройство %d: %s", value[0], value[1] == 1 ? "включено" : "отключено");
                }
                break;
            case Clunet.COMMAND_LIGHT_LEVEL_INFO:
                int[] ll = lightLevelInfo(value);
                if (ll != null) {
                    return String.format("Уровень освещенности %s (%d%%)", ll[0] == 1 ? "высокий" : "низкий", ll[1]);
                }
                break;
            case Clunet.COMMAND_HEATFLOOR_INFO:
                if (value.length > 0) {
                    response = "";
                    switch (value[0] & 0xFF) {
                        case 0x00:  //выкл
                            response = "Устройство отключено";
                            break;
                        case 0xFE:  //режимы
                            int cnt = (value.length - 1) / 4;
                            for (int i = 0; i < cnt; i++) {
                                response += String.format("Канал %d: %s; ", i, heatfloorModeExt(value, i * 4 + 1));
                            }
                            break;
                        case 0xF0:
                        case 0xF1:
                        case 0xF2:
                        case 0xF3:
                        case 0xF4:
                        case 0xF5:
                        case 0xF6:
                        case 0xF7:
                        case 0xF8:
                        case 0xF9:  //программы
                            response = String.format("Пр.%d: ", value[0] & 0x0F);
                            cnt = value[1];
                            if (cnt > 0 && cnt < 10){
                                for (int i=0; i<cnt; i++){
                                    int pos = i*2 + 2;
                                    response+= String.format("%02d ч.→%d °C; ", value[pos], value[pos+1]);
                                }
                            }else{
                                response += "не установлена";
                            }
                            break;
                        default:    //статус
                            cnt = value[0];
                            if (value.length == cnt * 6 + 1) {
                                for (int i = 0; i < cnt; i++) {
                                    int index = i * 6 + 1;

                                    String mode = heatfloorMode(value[index]);

                                    response += String.format("Канал %d: %s", i, mode);

                                    if (value[index] > 0) {  //не выкл
                                        String solution = "???";
                                        switch (value[index + 1]) {
                                            case 0:
                                                solution = "ожидание";
                                                break;
                                            case 1:
                                                solution = "нагрев";
                                                break;
                                            case -1:
                                                solution = "охлаждение";
                                                break;
                                            case -2:
                                                solution = "ошибка чтения датчика";
                                                break;
                                            case -3:
                                                solution = "ошибка диапазона значения датчика";
                                                break;
                                            case -4:
                                                solution = "ошибка диспетчера";
                                                break;
                                        }
                                        Float sensorT = ds18b20Temperature(value, index + 2);
                                        String sensorTStr = "-";
                                        if (sensorT != null) {
                                            sensorTStr = String.format(Locale.ROOT, "%.01f°C", sensorT);
                                        }
                                        Float settingT = ds18b20Temperature(value, index + 4);
                                        String settingTStr = "-";
                                        if (settingT != null) {
                                            settingTStr = String.format(Locale.ROOT, "%.01f°C", settingT);
                                        }
                                        response += String.format(" [%s (%s/%s)]", solution, sensorTStr, settingTStr);
                                    }
                                    response += "; ";
                                }
                            }
                    }
                    return response;
                }
                break;
            case Clunet.COMMAND_FAN_INFO:
                if (value.length > 1){
                    String mode = null;
                    switch (value[0]){
                        case 0:
                            mode = "авто режим выкл.";
                            break;
                        case 1:
                            mode = "авто режим";
                            break;
                    }
                    
                    String state = null;
                    switch (value[1]){
                        case 1:
                            state = "ожидание";
                            break;
                        case 2:
                            state = "требуется включение";
                            break;
                        case 3:
                            state = "включен (авто)";
                            break;
                        case 4:
                            state = "включен (ручной)";
                            break;
                    }
                    if (mode != null && state != null){
                        return String.format("Вентилятор (%s): %s", mode, state);
                    }
                }
            break;
            case Clunet.COMMAND_VOLUME_INFO:
                if (value.length == 2){
                    return String.format("Значение уровня громкости: %d %% (%d dB)", value[0], value[1]);
                }
            break;
            case Clunet.COMMAND_CHANNEL_INFO:
                if (value.length == 1){
                    return String.format("Текущий канал: %d", value[0]);
                }
            break;
            case Clunet.COMMAND_EQUALIZER_INFO:
                if (value.length == 3){
                    return String.format("Эквалайзер: gain: %d dB; treble: %d dB; bass: %d dB; ", value[0], value[1], value[2]);
                }
            break;
            case Clunet.COMMAND_FM_INFO:
                return fmInfo(value);
        }
        return null;
    }
    
}
