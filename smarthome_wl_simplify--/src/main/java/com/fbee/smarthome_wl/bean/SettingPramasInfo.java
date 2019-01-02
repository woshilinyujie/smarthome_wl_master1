package com.fbee.smarthome_wl.bean;

/**
 * @class name：com.fbee.smarthome_wl.bean
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/22 15:11
 */
public class SettingPramasInfo {


    /**
     * timestamp : 1499681215
     * cmd : DEVICE_CONFIG
     * unlock_adm_pwd : xxxxx,xxxxxx
     * infrared_alarm_interval_time : 30
     * infrared_alarm_type : txt
     * volume_level : 2
     * infrared_sensitivity : 50
     * auth_mode : 0
     * activation_code : 12311
     * on_off : 0,1,1,0,1,0,1,0
     */

    private String timestamp;
    private String cmd;
    private String unlock_adm_pwd;
    private String infrared_alarm_interval_time;
    private String infrared_alarm_type;
    private String volume_level;
    private String infrared_sensitivity;
    private String auth_mode;
    private String activation_code;
    private String on_off;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getUnlock_adm_pwd() {
        return unlock_adm_pwd;
    }

    public void setUnlock_adm_pwd(String unlock_adm_pwd) {
        this.unlock_adm_pwd = unlock_adm_pwd;
    }

    public String getInfrared_alarm_interval_time() {
        return infrared_alarm_interval_time;
    }

    public void setInfrared_alarm_interval_time(String infrared_alarm_interval_time) {
        this.infrared_alarm_interval_time = infrared_alarm_interval_time;
    }

    public String getInfrared_alarm_type() {
        return infrared_alarm_type;
    }

    public void setInfrared_alarm_type(String infrared_alarm_type) {
        this.infrared_alarm_type = infrared_alarm_type;
    }

    public String getVolume_level() {
        return volume_level;
    }

    public void setVolume_level(String volume_level) {
        this.volume_level = volume_level;
    }

    public String getInfrared_sensitivity() {
        return infrared_sensitivity;
    }

    public void setInfrared_sensitivity(String infrared_sensitivity) {
        this.infrared_sensitivity = infrared_sensitivity;
    }

    public String getAuth_mode() {
        return auth_mode;
    }

    public void setAuth_mode(String auth_mode) {
        this.auth_mode = auth_mode;
    }

    public String getActivation_code() {
        return activation_code;
    }

    public void setActivation_code(String activation_code) {
        this.activation_code = activation_code;
    }

    public String getOn_off() {
        return on_off;
    }

    public void setOn_off(String on_off) {
        this.on_off = on_off;
    }
}
