package com.gargon.smarthome.logger;

import com.gargon.smarthome.supradin.messages.SupradinDataMessage;

/**
 *
 * @author gargon
 */
public class RealTimeSupradinDataMessage extends SupradinDataMessage {

    /**
     * Класс переопределяет обычный SupradinDataMessage добавляя в него дату и
     * время получения реального сообщения. Подобное решение вызвано лишь тем,
     * что перед непосредственной "укладкой" сообщений в БД возможна ситуация,
     * когда они первоначально "отлеживаются" в буфере (для PeriodCommand) и
     * лишь по истечению периода самая актуальная команда "кладется" в БД.
     * Понятно, что в избежании двусмысленных ситуаций при анализе данных из БД,
     * логично класть сообщения в БД со временем соответсвующем времени их
     * получения(наступления)
     *
     * @param dst
     * @param prio
     * @param command
     * @param data
     */
    
    private final long time;
    
    public RealTimeSupradinDataMessage(SupradinDataMessage message, long time) {
        super(message.getDst(), message.getSrc(), message.getCommand(), message.getData());
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return time + " " + super.toString();
    }
    
    

}
