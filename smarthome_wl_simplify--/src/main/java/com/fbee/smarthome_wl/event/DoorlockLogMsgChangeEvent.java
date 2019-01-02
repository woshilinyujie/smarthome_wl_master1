package com.fbee.smarthome_wl.event;

/**
 * Created by WLPC on 2017/5/17.
 */

public class DoorlockLogMsgChangeEvent {
    private String msg;

    public DoorlockLogMsgChangeEvent(String msg) {
        this.msg = msg;
    }

    public DoorlockLogMsgChangeEvent() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
