package com.fbee.smarthome_wl.event;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/5/16.
 */

public class AreaNameChange implements Serializable {
    private short groupId;
    private String groupName;

    public AreaNameChange() {
    }

    public AreaNameChange(short groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public short getGroupId() {
        return groupId;
    }

    public void setGroupId(short groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
