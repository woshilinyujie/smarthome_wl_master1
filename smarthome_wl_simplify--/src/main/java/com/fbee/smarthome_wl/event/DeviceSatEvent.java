package com.fbee.smarthome_wl.event;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/17 19:18
 */
public class DeviceSatEvent {
    private int sat;
    private int uId;

    public DeviceSatEvent(int sat, int uId) {
        this.sat = sat;
        this.uId = uId;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }
}
