package com.fbee.smarthome_wl.bean;

/**
 * Created by WLPC on 2017/4/5.
 */

public class ArriveReportCBInfo {
    private int uId;
    private int data;
    private short clusterId;
    private short attribID;

    public ArriveReportCBInfo(int uId, int data, short clusterId, short attribID) {
        this.uId = uId;
        this.data = data;
        this.clusterId = clusterId;
        this.attribID = attribID;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public short getClusterId() {
        return clusterId;
    }

    public void setClusterId(short clusterId) {
        this.clusterId = clusterId;
    }

    public short getAttribID() {
        return attribID;
    }

    public void setAttribID(short attribID) {
        this.attribID = attribID;
    }
}
