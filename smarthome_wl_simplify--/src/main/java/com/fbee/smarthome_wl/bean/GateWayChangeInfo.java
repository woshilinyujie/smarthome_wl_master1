package com.fbee.smarthome_wl.bean;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/4/21.
 */

public class GateWayChangeInfo implements Serializable{
    String userName;
    String alairs;

    public GateWayChangeInfo() {
    }

    public GateWayChangeInfo(String userName, String alairs) {
        this.userName = userName;
        this.alairs = alairs;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAlairs() {
        return alairs;
    }

    public void setAlairs(String alairs) {
        this.alairs = alairs;
    }
}
