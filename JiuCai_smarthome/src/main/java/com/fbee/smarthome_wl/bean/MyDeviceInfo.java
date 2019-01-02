package com.fbee.smarthome_wl.bean;

/**
 * 显示设备（猫眼和飞比设备）
 * @class name：com.fbee.smarthome_wl.bean
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/24 9:22
 */
public class MyDeviceInfo {
    private String name;//设备别名
    private String  id;//设备唯一id
    private String supplier;//区分猫眼和飞比设备
    private String deviceType;//设备类型

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getSupplier() {
        return supplier;

    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
