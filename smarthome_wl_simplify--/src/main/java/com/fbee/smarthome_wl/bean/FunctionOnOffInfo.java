package com.fbee.smarthome_wl.bean;

/**
 * Created by WLPC on 2017/9/22.
 */

public class FunctionOnOffInfo {
    private String name;
    private String isSelect;

    public FunctionOnOffInfo() {
    }

    public FunctionOnOffInfo(String name, String  isSelect) {
        this.name = name;
        this.isSelect = isSelect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String isSelect() {
        return isSelect;
    }

    public void setSelect(String select) {
        isSelect = select;
    }
}
