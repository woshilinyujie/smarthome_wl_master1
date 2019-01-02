package com.fbee.smarthome_wl.bean;

import com.fbee.zllctl.SenceData;

import java.io.Serializable;
import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.bean
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/14 9:19
 */
public class SenceBean implements Serializable {
    private short senceId;
    private int deviceNumber;
    public List<SenceData> senceDatas;
    private String senceName;

    public String getSenceName() {
        return senceName;
    }

    public void setSenceName(String senceName) {
        this.senceName = senceName;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }


    public void setDeviceNumber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public short getSenceId() {
        return senceId;
    }

    public void setSenceId(short senceId) {
        this.senceId = senceId;
    }

    public List<SenceData> getSenceDatas() {
        return senceDatas;
    }

    public void setSenceDatas(List<SenceData> senceDatas) {
        this.senceDatas = senceDatas;
    }
}
