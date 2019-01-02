package com.fbee.smarthome_wl.bean;

/**
 * Created by WLPC on 2017/9/11.
 */

public class AddVideoLockInfo {
    private String name;//设备别名
    private String  id;//设备唯一id
    private String deviceType;//设备类型
    private String status;//在线状态
    public AddVideoLockInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
