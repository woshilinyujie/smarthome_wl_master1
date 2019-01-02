package com.fbee.smarthome_wl.event;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/17 19:06
 */
public class DeviceHueEvent {
   private int hue;
   private int uId;

    public DeviceHueEvent(int hue, int uId) {
        this.hue = hue;
        this.uId = uId;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }
}
