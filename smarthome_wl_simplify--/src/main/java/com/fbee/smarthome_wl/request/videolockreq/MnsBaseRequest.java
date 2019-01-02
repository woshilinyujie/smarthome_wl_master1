package com.fbee.smarthome_wl.request.videolockreq;

public class MnsBaseRequest {
    /**
     * token : xxxxxxxxxxxxxxx
     * api_version : 1.0
     */

    private String token;
    private String api_version;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

}
