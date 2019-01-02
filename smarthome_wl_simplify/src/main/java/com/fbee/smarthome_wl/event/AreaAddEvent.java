package com.fbee.smarthome_wl.event;

/**
 * Created by WLPC on 2017/5/10.
 */

public class AreaAddEvent {
    private String area;

    public AreaAddEvent(String area) {
        this.area = area;
    }

    public AreaAddEvent() {
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
