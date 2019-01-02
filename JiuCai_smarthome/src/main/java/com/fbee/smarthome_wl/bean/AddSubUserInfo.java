package com.fbee.smarthome_wl.bean;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/4/20.
 */

public class AddSubUserInfo implements Serializable{
   private String send;

    public AddSubUserInfo(String send) {
        this.send = send;
    }

    public AddSubUserInfo() {
    }
}
