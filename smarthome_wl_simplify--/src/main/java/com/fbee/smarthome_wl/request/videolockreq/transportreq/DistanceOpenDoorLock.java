package com.fbee.smarthome_wl.request.videolockreq.transportreq;

/**
 * Created by WLPC on 2017/9/5.
 */

public class DistanceOpenDoorLock {

    /**
     * timestamp : 1499681215
     * cmd : REMOTE_UNLOCK
     * unlock_pwd : xxxxxxxxxxx,xxxxxxxx
     */

    private String timestamp;
    private String cmd;
    private String unlock_pwd;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getUnlock_pwd() {
        return unlock_pwd;
    }

    public void setUnlock_pwd(String unlock_pwd) {
        this.unlock_pwd = unlock_pwd;
    }
}
