package com.fbee.smarthome_wl.bean;

/**
 * Created by WLPC on 2017/4/27.
 */

public class ModifyAccountInfo {
    private String account;

    public ModifyAccountInfo(String account) {
        this.account = account;
    }

    public ModifyAccountInfo() {
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
