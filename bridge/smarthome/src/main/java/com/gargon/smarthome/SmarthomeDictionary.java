package com.gargon.smarthome;

import com.gargon.smarthome.utils.DataFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
public class SmarthomeDictionary {

    
    private static final Map<Integer, String> PRIORITIES = new LinkedHashMap(){{
        put(Smarthome.PRIORITY_NOTICE, "PRIORITY_NOTICE");
        put(Smarthome.PRIORITY_INFO, "PRIORITY_INFO");
        put(Smarthome.PRIORITY_MESSAGE, "PRIORITY_MESSAGE");
        put(Smarthome.PRIORITY_COMMAND, "PRIORITY_COMMAND");
    }};
    
    private static final Map<Integer, String> DEVICES = new LinkedHashMap(){{
        put(Smarthome.ADDRESS_BROADCAST, "BROADCAST");
        
        put(Smarthome.ADDRESS_SUPRADIN, "Supradin");
        
        put(Smarthome.ADDRESS_AUDIOBOX, "AudioBox");
        put(Smarthome.ADDRESS_AUDIOBATH, "AudioBath");
        
        put(Smarthome.ADDRESS_RELAY_1, "Relay_1");
        put(Smarthome.ADDRESS_RELAY_2, "Relay_2");
        
        put(Smarthome.ADDRESS_SOCKET_DIMMER, "SocketDimmer");
        
        put(Smarthome.ADDRESS_KITCHEN, "Kitchen");
        put(Smarthome.ADDRESS_BATH_SENSORS, "BathSensors");
        put(Smarthome.ADDRESS_WARDROBE, "Wardrobe");
        
        put(Smarthome.ADDRESS_METEO, "Meteo");
        put(Smarthome.ADDRESS_KITCHEN_LIGHT, "KitchenLight");
        put(Smarthome.ADDRESS_WATER_SYSTEM, "WaterSystem");
        
        put(Smarthome.ADDRESS_TELEPHONE, "Telephone");
    }};
    
    private static final Map<Integer, String> COMMANDS = new LinkedHashMap(){{
        put(Smarthome.COMMAND_DISCOVERY, "Discovery");
        put(Smarthome.COMMAND_DISCOVERY_RESPONSE, "DiscoveryResponse");
        put(Smarthome.COMMAND_BOOT_CONTROL, "BootControl");
        put(Smarthome.COMMAND_REBOOT, "Reboot");
        put(Smarthome.COMMAND_BOOT_COMPLETED, "BootCompleted");
        
        put(Smarthome.COMMAND_TIME, "Time");
        put(Smarthome.COMMAND_TIME_INFO, "TimeInfo");
        
        put(Smarthome.COMMAND_PING, "Ping");
        put(Smarthome.COMMAND_PING_REPLY, "PingReply");
        
        put(Smarthome.COMMAND_CHANNEL, "Channel");
        put(Smarthome.COMMAND_CHANNEL_INFO, "ChannelInfo");
        
        put(Smarthome.COMMAND_VOLUME, "Volume");
        put(Smarthome.COMMAND_VOLUME_INFO, "VolumeInfo");
        put(Smarthome.COMMAND_MUTE, "Mute");
        put(Smarthome.COMMAND_EQUALIZER, "Equalizer");
        put(Smarthome.COMMAND_EQUALIZER_INFO, "EqualizerInfo");
        
        put(Smarthome.COMMAND_FM, "FM");
        put(Smarthome.COMMAND_FM_INFO, "FMInfo");
        
        put(Smarthome.COMMAND_POWER, "Power");
        put(Smarthome.COMMAND_POWER_INFO, "PowerInfo");
        put(Smarthome.COMMAND_SWITCH, "Switch");
        put(Smarthome.COMMAND_SWITCH_INFO, "SwitchInfo");
        put(Smarthome.COMMAND_BUTTON, "Button");
        put(Smarthome.COMMAND_BUTTON_INFO, "ButtonInfo");
        put(Smarthome.COMMAND_ROTARY_DIAL_NUMBER_INFO, "RotaryDialNumber");
        
        put(Smarthome.COMMAND_TEMPERATURE, "Temperature");
        put(Smarthome.COMMAND_TEMPERATURE_INFO, "TemperatureInfo");
        put(Smarthome.COMMAND_HUMIDITY, "Humidity");
        put(Smarthome.COMMAND_HUMIDITY_INFO, "HumidityInfo");
        put(Smarthome.COMMAND_PRESSURE, "Pressure");
        put(Smarthome.COMMAND_PRESSURE_INFO, "PressureInfo");
        put(Smarthome.COMMAND_METEO, "Meteo");
        put(Smarthome.COMMAND_METEO_INFO, "MeteoInfo");
        
        put(Smarthome.COMMAND_ONEWIRE_SEARCH, "OneWireSearch");
        put(Smarthome.COMMAND_ONEWIRE_INFO, "OneWireInfo");
        put(Smarthome.COMMAND_VOLTAGE, "Voltage");
        put(Smarthome.COMMAND_VOLTAGE_INFO, "VoltageInfo");
        
        put(Smarthome.COMMAND_MOTION, "Motion");
        put(Smarthome.COMMAND_MOTION_INFO, "MotionInfo");
        
        put(Smarthome.COMMAND_LIGHT_LEVEL, "LightLevel");
        put(Smarthome.COMMAND_LIGHT_LEVEL_INFO, "LightLevelInfo");
        
        put(Smarthome.COMMAND_FAN, "Fan");
        put(Smarthome.COMMAND_FAN_INFO, "FanInfo");
        
        put(Smarthome.COMMAND_CHARGE, "Charge");
        put(Smarthome.COMMAND_CHARGE_INFO, "ChargeInfo");
        
        put(Smarthome.COMMAND_DOORS, "Doors");
        put(Smarthome.COMMAND_DOORS_INFO, "DoorsInfo");
        
        put(Smarthome.COMMAND_DIMMER, "Dimmer");
        put(Smarthome.COMMAND_DIMMER_INFO, "DimmerInfo");
        
        put(Smarthome.COMMAND_HEATFLOOR, "Heatfloor");
        put(Smarthome.COMMAND_HEATFLOOR_INFO, "HeatfloorInfo");
        
        put(Smarthome.COMMAND_SERVO, "Servo");
        put(Smarthome.COMMAND_SERVO_INFO, "ServoInfo");
        
        put(Smarthome.COMMAND_DEVICE_STATE, "DeviceState");
        put(Smarthome.COMMAND_DEVICE_STATE_INFO, "DeviceStateInfo");
        
        put(Smarthome.COMMAND_RC_BUTTON_PRESSED, "RCButtonPressed");
        
        put(Smarthome.COMMAND_ANDROID, "Android");
        put(Smarthome.COMMAND_DEBUG, "Debug");
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
                            temperatureValue = ds18b20Temperature(value, pos + 8);
                            pos += 10;
                            break;
                        case 1: //dht
                            sensorId = "DHT-22 (" + String.valueOf(value[pos] & 0xFF) + ")";
                            ByteBuffer bb = ByteBuffer.wrap(value, pos + 1, 2);
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                            int t = bb.getShort();
                            if (t != 0xFFFF) {
                                temperatureValue = t / 10f;
                            }
                            pos += 3;
                            break;
                        case 2: //bmp/bme
                            sensorId = "BME280 (" + String.valueOf(value[pos] & 0xFF) + ")";
                            bb = ByteBuffer.wrap(value, pos + 1, 2);
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                            t = bb.getShort();
                            if (t != 0xFFFF) {
                                temperatureValue = t / 100f;
                            }
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
   
    public static Float pressureInfo(byte[] value) {
        try {
            if (value.length >= 4) {
                ByteBuffer bb = ByteBuffer.wrap(value);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                int p = bb.getInt();
                if (p != 0xFFFFFFFF) {
                    return p / 1000f;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public static Float humidityInfo(byte[] value) {
        try {
            if (value.length >= 2) {
                ByteBuffer bb = ByteBuffer.wrap(value);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                int h = bb.getChar();
                if (h != 0xFFFF) {
                    return h / 10f;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Float voltageInfo(byte[] value) {
        try {
            if (value.length >= 2) {
                ByteBuffer bb = ByteBuffer.wrap(value);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                int v = bb.getChar();
                if (v != 0xFFFF) {
                    return v / 100f;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static int[] lightLevelInfo(byte[] value) {
        if (value.length == 2 && (value[0] == 0 || value[0] == 1)) {
            return new int[]{value[0], value[1]};
        } else if (value.length == 3 && value[0] == 2) {
            ByteBuffer bb = ByteBuffer.wrap(value, 1, 2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int l = bb.getChar();
            if (l != 0xFFFF) {
                return new int[]{value[0], l};
            }
        }
        return null;
    }
    
    
    private static final int METEO_PARAM_T = 0;
    private static final int METEO_PARAM_H = 1;
    private static final int METEO_PARAM_P = 2;
    private static final int METEO_PARAM_L = 3;
    
    public static Float[] meteoInfo(byte[] value) {
        try {
            if (value.length == 9) {
                Float[] meteo = {null, null, null, null};
                ByteBuffer bb = ByteBuffer.wrap(value);
                bb.order(ByteOrder.LITTLE_ENDIAN);

                int valid = bb.get();

                int t = bb.getShort();
                if (((valid >> METEO_PARAM_T) & 0x01) == 0x01) {
                    meteo[METEO_PARAM_T] = t / 100f;
                }

                int h = bb.getChar();
                if (((valid >> METEO_PARAM_H) & 0x01) == 0x01) {
                    meteo[METEO_PARAM_H] = h / 10f;
                }

                int p = bb.getChar();
                if (((valid >> METEO_PARAM_P) & 0x01) == 0x01) {
                    meteo[METEO_PARAM_P] = p / 10f;
                }

                int l = bb.getChar();
                if (((valid >> METEO_PARAM_L) & 0x01) == 0x01) {
                    meteo[METEO_PARAM_L] = (float) l;
                }
                return meteo;
            }
        } catch (Exception e) {
        }
        return null;
    }
   
   public static final int HEATFLOOR_MODE_OFF = 0;
   public static final int HEATFLOOR_MODE_MANUAL = 1;
   public static final int HEATFLOOR_MODE_DAY = 2;
   public static final int HEATFLOOR_MODE_WEEK = 3;
   public static final int HEATFLOOR_MODE_PARTY = 4;
   public static final int HEATFLOOR_MODE_DAY_FOR_TODAY = 5;
   
    public static String heatfloorMode(int m) {
        String mode;
        switch (m) {
            case HEATFLOOR_MODE_OFF:
                mode = "Выкл.";
                break;
            case HEATFLOOR_MODE_MANUAL:
                mode = "режим ручной";
                break;
            case HEATFLOOR_MODE_DAY:
                mode = "режим дневной";
                break;
            case HEATFLOOR_MODE_WEEK:
                mode = "режим недельный";
                break;
            case HEATFLOOR_MODE_PARTY:
                mode = "режим вечеринка";
                break;
            case HEATFLOOR_MODE_DAY_FOR_TODAY:
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
            case Smarthome.COMMAND_DISCOVERY_RESPONSE:
                return String.format("Обнаружено устройство: %s", new String(value));
            case Smarthome.COMMAND_BOOT_COMPLETED:
                return String.format("Устройство перезагружено");
            case Smarthome.COMMAND_POWER_INFO:
                if (value.length == 1){
                    return String.format("Устройство %s", value[0] == 1 ? "включено" : "отключено");
                }
            case Smarthome.COMMAND_SWITCH_INFO:
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
            case Smarthome.COMMAND_BUTTON_INFO:
                if (value.length == 2){
                    String buttonState;
                    switch (value[1]){
                        case 0:
                           buttonState = "не нажата";
                           break;
                        case 1:
                           buttonState = "нажата";
                           break;
                        case 2:
                           buttonState = "в положении ВКЛ";
                           break;
                        case 3:
                           buttonState = "в положении ОТКЛ";
                           break;
                        default:
                            buttonState = "???";
                    }
                    return String.format("Кнопка %d: %s", value[0], buttonState);
                }
                break;
            case Smarthome.COMMAND_ROTARY_DIAL_NUMBER_INFO:
                if (value.length > 0){
                    return String.format("Набран номер: %s", Arrays.toString(value));
                }
                break;
             case Smarthome.COMMAND_TIME_INFO:
                if (value.length == 7){
                    return String.format("Текущая дата: %02d-%02d-%04d %02d:%02d:%02d, %d день недели", value[2], value[1], value[0]+2000, value[3], value[4], value[5], value[6]);
                }
                break;
            case Smarthome.COMMAND_TEMPERATURE_INFO:
                String response = "";
                Map<String, Float> t = temperatureInfo(value);
                if (t != null){
                    for (Map.Entry<String, Float> entry : t.entrySet()){
                        response += String.format(Locale.ROOT, "T[%s]=%.2f°C; ", entry.getKey(), entry.getValue());
                    }
                }
                return response;
            case Smarthome.COMMAND_ONEWIRE_INFO:
                response = "";
                List<String> ow = oneWireInfo(value);
                if (ow != null) {
                    for (String o : ow) {
                        response += o + "; ";
                    }
                }
                return response;
            case Smarthome.COMMAND_HUMIDITY_INFO:
                Float h = humidityInfo(value);
                if (h != null){
                    return String.format(Locale.ROOT, "%.1f%%", h);
                }
                break;
            case Smarthome.COMMAND_PRESSURE_INFO:
                Float p = pressureInfo(value);
                if (p != null){
                    return String.format(Locale.ROOT, "%.3f мм рт.ст.", p);
                }
                break;
            case Smarthome.COMMAND_METEO_INFO:
                Float[] meteo = meteoInfo(value);
                if (meteo != null){
                    String meteo_s = "";
                    for (int i=0; i<meteo.length; i++){
                        if (meteo[i] != null){
                            switch (i){
                                case METEO_PARAM_T:
                                    meteo_s += String.format(Locale.ROOT, "T=%.2f°C; ", meteo[i]);
                                    break;
                                case METEO_PARAM_H:
                                    meteo_s += String.format(Locale.ROOT, "H=%.1f%%; ", meteo[i]);
                                    break;
                                case METEO_PARAM_P:
                                    meteo_s += String.format(Locale.ROOT, "P=%.1f мм рт.ст.; ", meteo[i]);
                                    break;
                                case METEO_PARAM_L:
                                    meteo_s += String.format(Locale.ROOT, "L=%d лк; ", meteo[i].intValue());
                                    break;
                            }
                        }
                    }
                    return meteo_s;
                }
                break;
            case Smarthome.COMMAND_VOLTAGE_INFO:
                Float v = voltageInfo(value);
                if (v != null){
                    return String.format(Locale.ROOT, "%.2f В", v);
                }
                break;
            case Smarthome.COMMAND_MOTION_INFO:
                if (value.length == 2) {
                    response = value[0] == 1 ? "Обнаружено движение" : "Движение отсутствует";
                    response += " (локация " + value[1] + ")";
                    return response;
                }
                break;
            case Smarthome.COMMAND_DOORS_INFO:       
                if (value.length == 1) {
                    return value[0] > 0 ? "Дверь открыта" : "Дверь закрыта";
                }
                break;
            case Smarthome.COMMAND_DEVICE_STATE_INFO:       
                if (value.length == 2) {
                    return String.format("Устройство %d: %s", value[0], value[1] == 1 ? "включено" : "отключено");
                }
                break;
            case Smarthome.COMMAND_LIGHT_LEVEL_INFO:
                int[] ll = lightLevelInfo(value);
                if (ll != null) {
                    if (ll[0] == 0 || ll[0] == 1){
                    return String.format("Уровень освещенности %s (%d%%)", 
                            ll[0] == 1 ? "высокий" : "низкий", ll[1]);
                    }else if (ll[0] == 2){
                        return String.format("%d лк", ll[1]);
                    }
                }
                break;
                
            case Smarthome.COMMAND_CHARGE_INFO:
                if (value.length == 1) {
                    if (value[0] == 0x00) {
                        return "Зарядное устройство отключено";
                    }
                } else if (value.length == 3) {
                    if (value[0] == 0x01) {
                        return String.format("Зарядное устройство включено (осталось: %d секунд)",
                                ((value[2] & 0xFF) << 8) | (value[1] & 0xFF));
                    }
                }
                break;
                
            case Smarthome.COMMAND_HEATFLOOR_INFO:
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
                                            sensorTStr = String.format(Locale.ROOT, "%.1f°C", sensorT);
                                        }
                                        Float settingT = ds18b20Temperature(value, index + 4);
                                        String settingTStr = "-";
                                        if (settingT != null) {
                                            settingTStr = String.format(Locale.ROOT, "%.1f°C", settingT);
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
            case Smarthome.COMMAND_FAN_INFO:
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
            case Smarthome.COMMAND_SERVO_INFO:
                if (value.length == 2){
                    int angle = ((value[1] & 0xFF) << 8) | (value[0] & 0xFF);
                    return String.format("Угол поворота сервопривода: %d°", angle);
                }
            case Smarthome.COMMAND_VOLUME_INFO:
                if (value.length == 2){
                    return String.format("Значение уровня громкости: %d %% (%d dB)", value[0], value[1]);
                }
            break;
            case Smarthome.COMMAND_CHANNEL_INFO:
                if (value.length == 1){
                    return String.format("Текущий канал: %d", value[0]);
                }
            break;
            case Smarthome.COMMAND_EQUALIZER_INFO:
                if (value.length == 3){
                    return String.format("Эквалайзер: gain: %d dB; treble: %d dB; bass: %d dB; ", value[0], value[1], value[2]);
                }
            break;
            case Smarthome.COMMAND_FM_INFO:
                return fmInfo(value);
        }
        return null;
    }
    
}
