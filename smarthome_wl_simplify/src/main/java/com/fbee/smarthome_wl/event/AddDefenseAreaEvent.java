package com.fbee.smarthome_wl.event;

/**
 * @class name：com.fbee.smarthome_wl.event
 * @anthor create by Zhaoli.Wang
 * @time 2018/1/23 15:36
 */
public class AddDefenseAreaEvent {
   private byte id;
   private byte enable;
   private String name;
   private byte  success;

    public AddDefenseAreaEvent(byte id, byte enable, String name, byte success) {
        this.id = id;
        this.enable = enable;
        this.name = name;
        this.success = success;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getEnable() {
        return enable;
    }

    public void setEnable(byte enable) {
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getSuccess() {
        return success;
    }

    public void setSuccess(byte success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "AddDefenseAreaEvent{" +
                "id=" + id +
                ", enable=" + enable +
                ", name='" + name + '\'' +
                ", success=" + success +
                '}';
    }
}
