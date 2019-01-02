package com.fbee.smarthome_wl.bean;

/**
 * Created by wl on 2017/9/27.
 */

public class UpdateDoorLockName {

    public UpdateDoorLockName(String uuid, String updateName) {
        this.UpdateName = updateName;
        this.uuid=uuid;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String updateName) {
        UpdateName = updateName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String UpdateName;
    private String uuid;
}
