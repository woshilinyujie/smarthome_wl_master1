package com.fbee.smarthome_wl.event;

/**
 * Created by WLPC on 2017/8/18.
 */

public class CookieEvent {
    private String cookie;

    public CookieEvent() {
    }

    public CookieEvent(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

}
