package com.fbee.smarthome_wl.event;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2018/1/26 8:54
 */
public class AddDevicesToDefenseArea {

    private byte id;
    private int[] devices;
    private byte success;

    public AddDevicesToDefenseArea(byte id, int[] devices, byte success) {
        this.id = id;
        this.devices = devices;
        this.success = success;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public int[] getDevices() {
        return devices;
    }

    public void setDevices(int[] devices) {
        this.devices = devices;
    }

    public byte getSuccess() {
        return success;
    }

    public void setSuccess(byte success) {
        this.success = success;
    }
}
