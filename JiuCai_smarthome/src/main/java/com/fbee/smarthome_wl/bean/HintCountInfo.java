package com.fbee.smarthome_wl.bean;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/4/12.
 */

public class HintCountInfo implements Serializable{
    private int uid;
    private int isNeedReceive;//1代表不接收，0代表接收，默认接收
    public int getIsNeedReceive() {
        return isNeedReceive;
    }

    public void setIsNeedReceive(int isNeedReceive) {
        this.isNeedReceive = isNeedReceive;
    }

    public HintCountInfo(int uid, int isNeedReceive) {
        this.uid = uid;
        this.isNeedReceive = isNeedReceive;
    }


    public HintCountInfo(int uid) {
        this.uid = uid;
    }

    public HintCountInfo() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
