package com.fbee.smarthome_wl.event;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2017/5/12 14:29
 */
public class UpdateProgressEvevt {
    int type ;
    int upgradedata;

    public UpdateProgressEvevt(int type, int upgradedata) {
        this.upgradedata = upgradedata;
        this.type = type;
    }

    public int getUpgradedata() {
        return upgradedata;
    }

    public void setUpgradedata(int upgradedata) {
        this.upgradedata = upgradedata;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
