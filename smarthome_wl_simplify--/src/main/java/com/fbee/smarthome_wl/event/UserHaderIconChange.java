package com.fbee.smarthome_wl.event;

/**
 * Created by WLPC on 2017/5/13.
 */

public class UserHaderIconChange {
    private  String uri;

    public UserHaderIconChange(String uri) {
        this.uri = uri;
    }

    public UserHaderIconChange() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
