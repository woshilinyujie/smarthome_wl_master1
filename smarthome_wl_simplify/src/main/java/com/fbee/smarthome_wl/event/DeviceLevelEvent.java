package com.fbee.smarthome_wl.event;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/17 18:59
 */
public class DeviceLevelEvent {
    private int level;
    private int uId;

    public DeviceLevelEvent(int uId, int level) {
        this.uId = uId;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }
}
