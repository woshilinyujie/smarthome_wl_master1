package com.fbee.smarthome_wl.event;

/**
 * Created by WLPC on 2018/1/23.
 */

public class BindSenceInfoEvent {
    private int uid;
    private int sceneId;

    public BindSenceInfoEvent() {
    }

    public BindSenceInfoEvent(int uid, int sceneId) {
        this.uid = uid;
        this.sceneId = sceneId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }
}
