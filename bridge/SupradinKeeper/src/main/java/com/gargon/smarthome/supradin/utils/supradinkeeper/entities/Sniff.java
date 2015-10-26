package com.gargon.smarthome.supradin.utils.supradinkeeper.entities;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author gargon
 */
@Entity
@Table(name = "sniff")
public class Sniff {

    private Date s_time;

    private int src;
    private int dst;
    private int cmd;
    private byte[] data;
    private String interpretation;

    public Sniff() {
    }

    public Sniff(int src, int dst, int cmd, byte[] data, String interpretation) {
        this.src = src;
        this.dst = dst;
        this.cmd = cmd;
        this.data = data;
        this.interpretation = interpretation;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getDst() {
        return dst;
    }

    public void setDst(int dst) {
        this.dst = dst;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }
    
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    @Id
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getS_time() {
        return s_time;
    }

    public void setS_time(Date s_time) {
        this.s_time = s_time;
    }

    @PrePersist
    protected void onCreate() {
        s_time = new Date();
    }

}
