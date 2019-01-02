package com.fbee.smarthome_wl.request;

/**
 * Created by wl on 2017/4/18.
 */

import java.io.Serializable;

/***
 * 删除设备用户
 */
public class DeleteDeviceUserInfo implements Serializable{


    /**
     * vendor_name : feibee
     * uuid : xxxxxxxxxx
     * device_user : {"id":"001"}
     */

    private String vendor_name;
    private String uuid;
    private DeviceUserBean device_user;

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DeviceUserBean getDevice_user() {
        return device_user;
    }

    public void setDevice_user(DeviceUserBean device_user) {
        this.device_user = device_user;
    }

    public static class DeviceUserBean implements Serializable{
        /**
         * id : 001
         */

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
