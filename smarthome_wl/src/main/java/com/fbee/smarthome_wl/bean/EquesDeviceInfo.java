package com.fbee.smarthome_wl.bean;

/**
 * Created by wl on 2017/3/29.
 */

public class EquesDeviceInfo {

    /**
     *  storage_free_size  : 1000
     *  storage_total_size  : 2000
     * alarm_enable : 1
     * battery_level : 50
     * battery_status : 3
     * db_light_enable : 1
     * device_type : 5
     * doorbell_ring : 1
     * doorbell_ring_name : xxxxxxx
     * from : user id
     * hw_version : 10
     * method : deviceinfo_result
     * sn : xxxx
     * support_type : 0
     * sw_version : 10
     * to : device id
     * wifi_config : SSID
     * wifi_level : 4
     */

    private int storage_free_size;
    private int storage_total_size;
    private int alarm_enable;
    private int battery_level;
    private int battery_status;
    private int db_light_enable;
    private String device_type;
    private int doorbell_ring;
    private String doorbell_ring_name;
    private String from;
    private String hw_version;
    private String method;
    private String sn;
    private String support_type;
    private String sw_version;
    private String to;
    private String wifi_config;
    private int wifi_level;

    public int getStorage_free_size() {
        return storage_free_size;
    }

    public void setStorage_free_size(int storage_free_size) {
        this.storage_free_size = storage_free_size;
    }

    public int getStorage_total_size() {
        return storage_total_size;
    }

    public void setStorage_total_size(int storage_total_size) {
        this.storage_total_size = storage_total_size;
    }

    public int getAlarm_enable() {
        return alarm_enable;
    }

    public void setAlarm_enable(int alarm_enable) {
        this.alarm_enable = alarm_enable;
    }

    public int getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(int battery_level) {
        this.battery_level = battery_level;
    }

    public int getBattery_status() {
        return battery_status;
    }

    public void setBattery_status(int battery_status) {
        this.battery_status = battery_status;
    }

    public int getDb_light_enable() {
        return db_light_enable;
    }

    public void setDb_light_enable(int db_light_enable) {
        this.db_light_enable = db_light_enable;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public int getDoorbell_ring() {
        return doorbell_ring;
    }

    public void setDoorbell_ring(int doorbell_ring) {
        this.doorbell_ring = doorbell_ring;
    }

    public String getDoorbell_ring_name() {
        return doorbell_ring_name;
    }

    public void setDoorbell_ring_name(String doorbell_ring_name) {
        this.doorbell_ring_name = doorbell_ring_name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHw_version() {
        return hw_version;
    }

    public void setHw_version(String hw_version) {
        this.hw_version = hw_version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSupport_type() {
        return support_type;
    }

    public void setSupport_type(String support_type) {
        this.support_type = support_type;
    }

    public String getSw_version() {
        return sw_version;
    }

    public void setSw_version(String sw_version) {
        this.sw_version = sw_version;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getWifi_config() {
        return wifi_config;
    }

    public void setWifi_config(String wifi_config) {
        this.wifi_config = wifi_config;
    }

    public int getWifi_level() {
        return wifi_level;
    }

    public void setWifi_level(int wifi_level) {
        this.wifi_level = wifi_level;
    }
}
