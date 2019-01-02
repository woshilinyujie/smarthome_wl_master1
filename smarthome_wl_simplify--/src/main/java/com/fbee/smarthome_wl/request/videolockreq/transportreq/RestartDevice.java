package com.fbee.smarthome_wl.request.videolockreq.transportreq;

/**
 * Created by WLPC on 2017/9/26.
 */

public class RestartDevice {

    /**
     * timestamp : 1499681215
     * cmd : REMOTE_RESTART_DEVICE
     * unlock_adm_pwd : xxxxx,xxxxxx
     */

    private String timestamp;
    private String cmd;
    private String unlock_adm_pwd;

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

    public String getUnlock_adm_pwd() {
        return unlock_adm_pwd;
    }

    public void setUnlock_adm_pwd(String unlock_adm_pwd) {
        this.unlock_adm_pwd = unlock_adm_pwd;
    }
}
