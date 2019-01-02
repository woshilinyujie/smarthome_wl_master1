package com.fbee.smarthome_wl.request;

/**
 * Created by WLPC on 2017/10/10.
 */

public class AddTokenReq {


    /**
     * username : 18888888888
     * secret_key : xxxxxxxxx
     * attitude : write
     */

    private String username;
    private String secret_key;
    private String attitude;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    public String getAttitude() {
        return attitude;
    }

    public void setAttitude(String attitude) {
        this.attitude = attitude;
    }
}
