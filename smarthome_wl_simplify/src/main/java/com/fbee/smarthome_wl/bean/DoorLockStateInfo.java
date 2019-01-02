package com.fbee.smarthome_wl.bean;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/4/1.
 *
 * Serial中arriveReportgatedoor_CallBack(int uId, byte[] data,
    char clusterId, char attribID) 回调数据需要的实体类
 */

public class DoorLockStateInfo implements Serializable{
    //门锁uid
    private int doorLockUid;
    //新协议门锁状态
    private int newDoorLockState;
    //开锁方式 01：密码 02：指纹03：刷卡 04:远程 05:多重验证开锁
    private int doorLockWay;
    private int falg;
    //用户名id
    private int doorLockCareNum;
    //门锁上报字节数组
    private byte[] doorLockData;

    private int doorLockSensorData;

    public DoorLockStateInfo() {
    }

    public DoorLockStateInfo(int doorLockUid, int newDoorLockState, int doorLockWay, int falg, int doorLockCareNum, byte[] doorLockData, int doorLockSensorData) {
        this.doorLockUid = doorLockUid;
        this.newDoorLockState = newDoorLockState;
        this.doorLockWay = doorLockWay;
        this.falg = falg;
        this.doorLockCareNum = doorLockCareNum;
        this.doorLockData = doorLockData;
        this.doorLockSensorData = doorLockSensorData;
    }

    public int getDoorLockUid() {
        return doorLockUid;
    }

    public void setDoorLockUid(int doorLockUid) {
        this.doorLockUid = doorLockUid;
    }

    public int getNewDoorLockState() {
        return newDoorLockState;
    }

    public void setNewDoorLockState(int newDoorLockState) {
        this.newDoorLockState = newDoorLockState;
    }

    public int getDoorLockWay() {
        return doorLockWay;
    }

    public void setDoorLockWay(int doorLockWay) {
        this.doorLockWay = doorLockWay;
    }

    public int getFalg() {
        return falg;
    }

    public void setFalg(int falg) {
        this.falg = falg;
    }

    public int getDoorLockCareNum() {
        return doorLockCareNum;
    }

    public void setDoorLockCareNum(int doorLockCareNum) {
        this.doorLockCareNum = doorLockCareNum;
    }

    public byte[] getDoorLockData() {
        return doorLockData;
    }

    public void setDoorLockData(byte[] doorLockData) {
        this.doorLockData = doorLockData;
    }

    public int getDoorLockSensorData() {
        return doorLockSensorData;
    }

    public void setDoorLockSensorData(int doorLockSensorData) {
        this.doorLockSensorData = doorLockSensorData;
    }
}
