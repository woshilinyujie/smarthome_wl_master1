package com.fbee.smarthome_wl.event;

import java.util.Arrays;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2018/1/23 14:40
 */
public class DefenseAreaIDsEvent {
    private byte[] defenseAreaIDs;

    public DefenseAreaIDsEvent(byte[] defenseAreaIDs) {
        this.defenseAreaIDs = defenseAreaIDs;
    }

    public byte[] getDefenseAreaIDs() {
        return defenseAreaIDs;
    }

    public void setDefenseAreaIDs(byte[] defenseAreaIDs) {
        this.defenseAreaIDs = defenseAreaIDs;
    }

    @Override
    public String toString() {
        return "DefenseAreaIDsEvent{" +
                "defenseAreaIDs=" + Arrays.toString(defenseAreaIDs) +
                '}';
    }
}
