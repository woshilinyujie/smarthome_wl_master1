package com.fbee.smarthome_wl.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 网关信息
 * Created by WLPC on 2017/4/25.
 */

public class GateWayInfo implements Serializable {
    //byte[] ver, byte[] snid, byte[] uname,
    //byte[] passwd, byte DevSum, byte GroupSum, byte TimerSum,
    //byte SceneSum, byte TaskSum
    private byte[] ver;
    private String snid;
    private byte[] uname;
    private byte[] passwd;

    public GateWayInfo(byte[] ver, String snid, byte[] uname, byte[] passwd) {
        this.ver = ver;
        this.snid = snid;
        this.uname = uname;
        this.passwd = passwd;
    }

    public GateWayInfo() {
    }

    public byte[] getVer() {
        return ver;
    }

    public void setVer(byte[] ver) {
        this.ver = ver;
    }

    public String getSnid() {
        return snid;
    }

    public void setSnid(String snid) {
        this.snid = snid;
    }

    public byte[] getUname() {
        return uname;
    }

    public void setUname(byte[] uname) {
        this.uname = uname;
    }

    public byte[] getPasswd() {
        return passwd;
    }

    public void setPasswd(byte[] passwd) {
        this.passwd = passwd;
    }

    @Override
    public String toString() {
        return "GateWayInfo{" +
                "ver=" + Arrays.toString(ver) +
                ", snid='" + snid + '\'' +
                ", uname=" + Arrays.toString(uname) +
                ", passwd=" + Arrays.toString(passwd) +
                '}';
    }
}
