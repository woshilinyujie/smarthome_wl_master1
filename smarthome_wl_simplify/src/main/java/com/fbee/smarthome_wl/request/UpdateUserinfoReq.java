package com.fbee.smarthome_wl.request;

/**
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/18 14:36
 */
public class UpdateUserinfoReq {


    /**
     * safe_update : true
     * password : xxxxxxxx
     * new_alias : 王力小王子
     * new_username : xxxxxx
     * new_sms_code : 666666
     */

    private String safe_update;
    private String password;
    private String new_alias;
    private String new_username;
    private String new_sms_code;

    public String getSafe_update() {
        return safe_update;
    }

    public void setSafe_update(String safe_update) {
        this.safe_update = safe_update;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNew_alias() {
        return new_alias;
    }

    public void setNew_alias(String new_alias) {
        this.new_alias = new_alias;
    }

    public String getNew_username() {
        return new_username;
    }

    public void setNew_username(String new_username) {
        this.new_username = new_username;
    }

    public String getNew_sms_code() {
        return new_sms_code;
    }

    public void setNew_sms_code(String new_sms_code) {
        this.new_sms_code = new_sms_code;
    }
}
