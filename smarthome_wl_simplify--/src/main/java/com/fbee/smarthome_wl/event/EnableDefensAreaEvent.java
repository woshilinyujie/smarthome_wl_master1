package com.fbee.smarthome_wl.event;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2018/1/23 16:55
 */
public class EnableDefensAreaEvent {
    private byte id;
    private byte enable;
    private byte  success;

    public EnableDefensAreaEvent(byte id, byte enable, byte success) {
        this.id = id;
        this.enable = enable;
        this.success = success;
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

    public byte getSuccess() {
        return success;
    }

    public void setSuccess(byte success) {
        this.success = success;
    }
}
