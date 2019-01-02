package com.fbee.smarthome_wl.event;

import com.fbee.zllctl.DeviceInfo;

import java.util.List;

/**
 * Created by WLPC on 2018/1/24.
 */

public class BindDevSenceEvent {
    private int bindTag;
    private int bindSenceId;
    private String name;
    private List<DeviceInfo> bindDevs;

    public BindDevSenceEvent(int bindTag, String name) {
        this.bindTag = bindTag;
        this.name = name;
    }

    public BindDevSenceEvent(int bindTag, int bindSenceId, String name) {
        this.bindTag = bindTag;
        this.bindSenceId = bindSenceId;
        this.name = name;
    }

    public BindDevSenceEvent(int bindTag, int bindSenceId) {
        this.bindTag = bindTag;
        this.bindSenceId = bindSenceId;
    }

    public BindDevSenceEvent(int bindTag, List<DeviceInfo> bindDevs) {
        this.bindTag = bindTag;
        this.bindDevs = bindDevs;
    }

    public List<DeviceInfo> getBindDevs() {
        return bindDevs;
    }

    public void setBindDevs(List<DeviceInfo> bindDevs) {
        this.bindDevs = bindDevs;
    }

    public int getBindTag() {
        return bindTag;
    }

    public void setBindTag(int bindTag) {
        this.bindTag = bindTag;
    }

    public int getBindSenceId() {
        return bindSenceId;
    }

    public void setBindSenceId(int bindSenceId) {
        this.bindSenceId = bindSenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
