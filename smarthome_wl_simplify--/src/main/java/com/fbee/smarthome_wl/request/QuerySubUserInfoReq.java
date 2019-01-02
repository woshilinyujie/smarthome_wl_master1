package com.fbee.smarthome_wl.request;

import java.io.Serializable;

/**
 * 查询子用户信息请求体
 * Created by WLPC on 2017/4/18.
 */

public class QuerySubUserInfoReq implements Serializable{


    /**
     * username : xxxxxxxx
     */

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
