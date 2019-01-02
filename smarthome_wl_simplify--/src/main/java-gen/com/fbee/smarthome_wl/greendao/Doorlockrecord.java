package com.fbee.smarthome_wl.greendao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
public class Doorlockrecord implements Comparable<Doorlockrecord>{
// KEEP INCLUDES END

    private Long id;
    private String userName;
    private int userID;
    private int deviceID;
    private String msg;
    private String msgType;
    private String time;
    private String remark;

    public Doorlockrecord() {
    }

    public Doorlockrecord(Long id) {
        this.id = id;
    }

    public Doorlockrecord(Long id, String userName, int userID, int deviceID, String msg, String msgType, String time, String remark) {
        this.id = id;
        this.userName = userName;
        this.userID = userID;
        this.deviceID = deviceID;
        this.msg = msg;
        this.msgType = msgType;
        this.time = time;
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    // KEEP METHODS - put your custom methods here
    @Override
    public int compareTo(Doorlockrecord doorlockrecord) {
        return -this.time.compareTo(doorlockrecord.getTime());
    }
    // KEEP METHODS END

}
