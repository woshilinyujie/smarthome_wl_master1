package com.fbee.smarthome_wl.event;

/**
 * Created by wl on 2017/4/24.
 */

public class UpDataGwName {
    private String name;
    private boolean isAdd;

    public UpDataGwName() {
    }

    public UpDataGwName(String name) {
        this.name = name;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public UpDataGwName(String name, boolean isAdd) {
        this.name = name;
        this.isAdd = isAdd;
    }

    public String getName() {
        return name;
    }
}
