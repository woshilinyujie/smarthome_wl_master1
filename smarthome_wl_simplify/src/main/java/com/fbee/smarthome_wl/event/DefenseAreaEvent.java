package com.fbee.smarthome_wl.event;

import java.util.Arrays;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2018/1/23 13:47
 */
public class DefenseAreaEvent {
    private byte id;
    private byte enable;
    private String  name;
    private int[] devices;

    public DefenseAreaEvent(byte id, byte enable, String name, int[] devices) {
        this.id = id;
        this.enable = enable;
        this.name = name;
        this.devices = devices;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getEnable() {
        return enable;
    }

    public void setEnable(byte enable) {
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getDevices() {
        return devices;
    }

    public void setDevices(int[] devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return "DefenseArea{" +
                "id=" + id +
                ", enable=" + enable +
                ", name='" + name + '\'' +
                ", devices=" + Arrays.toString(devices) +
                '}';
    }
}
