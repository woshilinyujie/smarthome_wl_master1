package com.fbee.smarthome_wl.event;

/**
 * Created by WLPC on 2017/6/7.
 */

public class AreaDevicesChangeEvent {
    private  int groupId;

    public AreaDevicesChangeEvent(int groupId) {
        this.groupId = groupId;
    }

    public AreaDevicesChangeEvent() {
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
