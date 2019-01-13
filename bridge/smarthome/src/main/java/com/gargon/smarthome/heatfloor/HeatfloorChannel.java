package com.gargon.smarthome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author gargon
 */
public class HeatfloorChannel {

    private final int num;
    private final String name;
    
    private final List<Integer> programList = new ArrayList();
    private final Map<Integer, int[]> weekProgramSet = new TreeMap();

    public HeatfloorChannel(int num, String name) {
        this.num = num;
        this.name = name;
    }
   
    public void addWeekProgramSet(int num, int[] set){
        weekProgramSet.put(num, set);
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getProgramList() {
        return programList;
    }

   public void addProgram(int program){
       programList.add(program);
   }
    
    public Map<Integer, int[]> getWeekProgramSet() {
        return weekProgramSet;
    }
}
