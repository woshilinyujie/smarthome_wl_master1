package com.fbee.smarthome_wl.bean;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/4/11.
 */

public class GroupMemberInfo implements Serializable{
    private short groupId;
    private int[] deviceUid;

    public GroupMemberInfo(short groupId, int[] deviceUid) {
        this.groupId = groupId;
        this.deviceUid = deviceUid;
    }

    public GroupMemberInfo() {
    }

    public short getGroupId() {
        return groupId;
    }

    public void setGroupId(short groupId) {
        this.groupId = groupId;
    }

    public int[] getDeviceUid() {
        return deviceUid;
    }

    public void setDeviceUid(int[] deviceUid) {
        this.deviceUid = deviceUid;
    }
}
