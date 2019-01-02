package com.fbee.smarthome_wl.event;

/**
 * Created by WLPC on 2018/1/23.
 */

public class BindDeviceInfoEvent {
    private int uid;
    private int[] targetDeviceIds;

    public BindDeviceInfoEvent() {
    }

    public BindDeviceInfoEvent(int uid, int[] targetDeviceIds) {
        this.uid = uid;
        this.targetDeviceIds = targetDeviceIds;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int[] getTargetDeviceIds() {
        return targetDeviceIds;
    }

    public void setTargetDeviceIds(int[] targetDeviceIds) {
        this.targetDeviceIds = targetDeviceIds;
    }
}
