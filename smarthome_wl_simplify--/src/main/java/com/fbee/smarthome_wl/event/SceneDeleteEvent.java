package com.fbee.smarthome_wl.event;

/**
 * 删除场景
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/13 20:31
 */
public class SceneDeleteEvent {
    private String name;

    public SceneDeleteEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
