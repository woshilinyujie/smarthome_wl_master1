package com.fbee.smarthome_wl.event;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/4/14.
 */

public class AreaDeleteEvent implements Serializable {
    private String name;

    public AreaDeleteEvent(String name) {
        this.name = name;
    }

    public AreaDeleteEvent() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
