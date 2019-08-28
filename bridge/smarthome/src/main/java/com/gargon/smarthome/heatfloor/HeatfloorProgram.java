package com.gargon.smarthome.heatfloor;

/**
 *
 * @author gargon
 */
public class HeatfloorProgram {
    
    private String name = null;
    private Integer[] schedule = null;

    public void setName(String name) {
        this.name = name;
    }

    public void setSchedule(Integer[] schedule) {
        this.schedule = schedule;
    }
    
    public String getName() {
        return name;
    }

    public Integer[] getSchedule() {
        return schedule;
    }
    
}
