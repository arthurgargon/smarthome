package com.gargon.smarthome;

/**
 *
 * @author gargon
 */
public class Smarthome {

    /*----------COMMANDS--------*/
    /* Поиск других устройств, параметров нет */
    public final static int COMMAND_DISCOVERY = 0x00;
    /* Ответ устройств на поиск, в качестве параметра - название устройства (текст) */
    public final static int COMMAND_DISCOVERY_RESPONSE = 0x01;

    /* Работа с загрузчиком. Данные - субкоманда.
     <-0 - загрузчик запущен
     ->1 - перейти в режим обновления прошивки
     <-2 - подтверждение перехода, плюс два байта - размер страницы
     ->3 запись прошивки, 4 байта - адрес, всё остальное - данные (равные размеру страницы)
     <-4 блок прошивки записан
     ->5 выход из режима прошивки */
    public final static int COMMAND_BOOT_CONTROL = 0x02;
    /* Перезагружает устройство в загрузчик. */
    public final static int COMMAND_REBOOT = 0x03;
    /* Посылается устройством после инициализации библиотеки, сообщает об успешной загрузке устройства. 
     Параметр - содержимое MCU регистра, говорящее о причине перезагрузки. */
    public final static int COMMAND_BOOT_COMPLETED = 0x04;
    
    /*Запрос текущего времени*/
    public final static int COMMAND_TIME = 0x08;
    
    /*Сообщение о текущем времени*/
    public final static int COMMAND_TIME_INFO = 0x09;

    /* Пинг, на эту команду устройство должно ответить следующей командой, возвратив весь буфер */
    public final static int COMMAND_PING = 0xFE;
    /* Ответ на пинг, в данных то, что было прислано в предыдущей команде */
    public final static int COMMAND_PING_REPLY = 0xFF;
    
    /* Команда переключения канала*/
    public final static int COMMAND_CHANNEL = 0x10;
   
    /* Сообщение о номере текущего канала*/
    public final static int COMMAND_CHANNEL_INFO = 0x11;

    /* Команда управления громкостью устройства*/
    public final static int COMMAND_VOLUME = 0x15;

    /* Сообщение о состоянии громкости устройства*/
    public final static int COMMAND_VOLUME_INFO = 0x16;

    /* Команда отключения звука устройства*/
    public final static int COMMAND_MUTE = 0x17;
    
     /* Команда управления эквалайзером*/
    public final static int COMMAND_EQUALIZER = 0x18;
    
    /* Сообщение о состоянии эквалайзера*/
    public final static int COMMAND_EQUALIZER_INFO = 0x19;
    
    /* Команда управления FM-приемником*/
    public final static int COMMAND_FM = 0x1C;
    
    /* Сообщение о состоянии FM-приемника*/
    public final static int COMMAND_FM_INFO = 0x1D;

    /* Команда управления включением/выключением устройства*/
    public final static int COMMAND_POWER = 0x1E;
    
    /* Команда запроса состояния (вкл/выкл) устройства*/
    public final static int COMMAND_POWER_INFO = 0x1F;
    
    /* Команда управления выключателями/реле*/
    public final static int COMMAND_SWITCH = 0x20;
    
    /* Сообщение о состоянии всех выключателей устройства в виде битовой маски*/
    public final static int COMMAND_SWITCH_INFO = 0x21;
    
    /* Команда запроса состояния нефиксируемых кнопок*/
    public final static int COMMAND_BUTTON = 0x22;
    
    /* Сообщает о состоянии нефиксируемой кнопки*/
    public final static int COMMAND_BUTTON_INFO = 0x23;
    
    /* Сообщает код набранный с помощью диска телефона*/
    public final static int COMMAND_ROTARY_DIAL_NUMBER_INFO = 0x24;
    
    /* Команда запроса текущей температуры*/
    public final static int COMMAND_TEMPERATURE = 0x25;
    
    /* Сообщение о температуре*/
    public final static int COMMAND_TEMPERATURE_INFO = 0x26;
    
    /* Команда запроса текущей влажности*/
    public final static int COMMAND_HUMIDITY = 0x27;
    
    /* Сообщает об уровне влажности*/
    public final static int COMMAND_HUMIDITY_INFO = 0x28;
    
    /* Команда запроса текущего атмосферного давления*/
    public final static int COMMAND_PRESSURE = 0x29;
    
    /* Сообщает об уровне атмосферного давления*/
    public final static int COMMAND_PRESSURE_INFO = 0x2A;
    
    /* Команда запроса метеоданных*/
    public final static int COMMAND_METEO = 0x2E;
    
    /* Сообщает метеоданные*/
    public final static int COMMAND_METEO_INFO = 0x2F;
    
    
    /* Команда поиска 1-wire устройств*/
    public final static int COMMAND_ONEWIRE_SEARCH = 0x30;
    
    /* Сообщает о найденном 1-wire устройстве*/
    public final static int COMMAND_ONEWIRE_INFO = 0x31;
    
    /* Команда запроса напряжения*/
    public final static int COMMAND_VOLTAGE = 0x32;
    
    /* Сообщает об уровне напряжения*/
    public final static int COMMAND_VOLTAGE_INFO = 0x33;

    /* Команда запроса наличия движения*/
    public final static int COMMAND_MOTION = 0x40;
    
    /* Сообщает о наличии движения в помещении*/
    public final static int COMMAND_MOTION_INFO = 0x41;
    
    /* Команда запроса уровня освещенности*/
    public final static int COMMAND_LIGHT_LEVEL = 0x45;
    
    /* Сообщает об уровне  освещенности*/
    public final static int COMMAND_LIGHT_LEVEL_INFO = 0x46;
    
    /* Команда управления вентилятором */
    public final static int COMMAND_FAN = 0x50;
    
    /* Сообщает о текущем состоянии вентилятора */
    public final static int COMMAND_FAN_INFO = 0x51;
    
    /* Команда управления подзарядкой устройств */
    public final static int COMMAND_CHARGE = 0x52;
    
    /* Сообщает о текущем состоянии процесса зарядки */
    public final static int COMMAND_CHARGE_INFO = 0x53;
    
    /* Команда запроса состояния дверей*/
    public final static int COMMAND_DOORS = 0x55;
    
    /* Сообщает о текущем состоянии дверей */
    public final static int COMMAND_DOORS_INFO = 0x56;
    
    
    /* Команда упрвления диммером*/
    public final static int COMMAND_DIMMER = 0x57;
    
    /* Сообщает о текущем состоянии диммера */
    public final static int COMMAND_DIMMER_INFO = 0x58;
    
    
    /* Команда управления теплым полом */
    public final static int COMMAND_HEATFLOOR = 0x60;
    
    /* Сообщает о состоянии теплого пола */
    public final static int COMMAND_HEATFLOOR_INFO = 0x61;
    
    
    /* Команда управления сервоприводом */
    public final static int COMMAND_SERVO = 0x66;
    
    /* Сообщает о состоянии сервопривода */
    public final static int COMMAND_SERVO_INFO = 0x67;
    
    
     /* Команда управления состоянием произвольного устройства */
    public final static int COMMAND_DEVICE_STATE = 0x70;

    /* Сообщает о состоянии произвольного устройства */
    public final static int COMMAND_DEVICE_STATE_INFO = 0x71;
    
    /* Сообщает о  о нажатии кнопки на ПДУ */
    public final static int COMMAND_RC_BUTTON_PRESSED = 0x75;
    
    /* Эмулирует нажтие кнопки на ПДУ */
    //public final static int CLUNET_COMMAND_RC_BUTTON_SEND = 0x76;
    
    
     /* Команда блокирования*/
    public final static int COMMAND_ANDROID = 0xA0;

    /* Команда отладки*/
    public final static int COMMAND_DEBUG = 0x99;
    
    

    /*----------PRIORITIES--------*/
    /* Приоритет пакета 1 - неважное уведомление, которое вообще может быть потеряно без последствий */
    public final static int PRIORITY_NOTICE = 1;
    /* Приоритет пакета 2 - какая-то информация, не очень важная */
    public final static int PRIORITY_INFO = 2;
    /* Приоритет пакета 3 - сообщение с какой-то важной информацией */
    public final static int PRIORITY_MESSAGE = 3;
    /* Приоритет пакета 4 - команда, на которую нужно сразу отреагировать */
    public final static int PRIORITY_COMMAND = 4;

    /*----------ADDRESSES----------*/
    public final static int ADDRESS_SUPRADIN  = 0x00;
    public final static int ADDRESS_BROADCAST = 0xFF;
    
    
    public final static int ADDRESS_AUDIOBOX        = 0x84;
    public final static int ADDRESS_AUDIOBATH       = 0x0B;
    
    public final static int ADDRESS_RELAY_1         = 0x14;
    public final static int ADDRESS_RELAY_2         = 0x15;
    
    public final static int ADDRESS_SOCKET_DIMMER   = 0x20;
    
    public final static int ADDRESS_KITCHEN         = 0x1D;
    public final static int ADDRESS_BATH_SENSORS    = 0x1E;
    public final static int ADDRESS_WARDROBE        = 0x1F;
    
    public final static int ADDRESS_METEO           = 0x81;
    public final static int ADDRESS_KITCHEN_LIGHT   = 0x82;
    public final static int ADDRESS_WATER_SYSTEM    = 0x83;
    
    public final static int ADDRESS_TELEPHONE       = 0x91;
    
}
