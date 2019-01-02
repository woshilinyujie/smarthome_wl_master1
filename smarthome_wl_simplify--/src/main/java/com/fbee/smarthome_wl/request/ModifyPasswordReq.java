package com.fbee.smarthome_wl.request;

/**
 * Created by WLPC on 2017/4/17.
 */

public class ModifyPasswordReq {


    /**
     * password : xxxxxxxxx
     * new_password : yyyyyyy
     */

    private String password;
    private String new_password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
