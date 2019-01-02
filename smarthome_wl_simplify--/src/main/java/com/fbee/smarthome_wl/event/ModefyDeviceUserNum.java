package com.fbee.smarthome_wl.event;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/5/10.
 */

public class ModefyDeviceUserNum implements Serializable{
    String userNum;

    public ModefyDeviceUserNum(String userNum) {
        this.userNum = userNum;
    }

    public ModefyDeviceUserNum() {
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }
}
