package com.fbee.smarthome_wl.event;

/**
 * Created by WLPC on 2017/5/18.
 */

public class ControlTimeEvent {
    private int nowTime;
    private int deviceId;

    public ControlTimeEvent(int nowTime, int deviceId) {
        this.nowTime = nowTime;
        this.deviceId = deviceId;
    }

    public ControlTimeEvent() {
    }

    public int getNowTime() {
        return nowTime;
    }

    public void setNowTime(int nowTime) {
        this.nowTime = nowTime;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
