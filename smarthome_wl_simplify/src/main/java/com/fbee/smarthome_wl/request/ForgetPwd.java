package com.fbee.smarthome_wl.request;

/**
 * Created by WLPC on 2017/5/3.
 */

public class ForgetPwd {

    /**
     * username : 18888888888
     * new_password : yyyyyyy
     * sms_code : 666666
     */

    private String username;
    private String new_password;
    private String sms_code;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }
}
