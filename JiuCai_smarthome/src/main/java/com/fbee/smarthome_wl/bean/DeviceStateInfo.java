package com.fbee.smarthome_wl.bean;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/4/10.
 */

public class DeviceStateInfo implements Serializable{
    private int state;
    private int uId;

    public DeviceStateInfo(int state, int uId) {
        this.state = state;
        this.uId = uId;
    }

    public DeviceStateInfo() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }
}
