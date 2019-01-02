package com.fbee.smarthome_wl.request;

import java.util.List;

/**
 * 添加设备用户
 * Created by WLPC on 2017/4/24.
 */

public class AddDeviceUser {
    /**
     * vendor_name : feibee
     * uuid : xxxxxxxxxx
     * device_user : {"id":"001","note":"设设的设备","without_notice_user_list":["18888888888","16666666666"]}
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

    public static class DeviceUserBean {
        /**
         * id : 001
         * note : 设设的设备
         * without_notice_user_list : ["18888888888","16666666666"]
         */

        private String id;
        private String note;
        private List<String> without_notice_user_list;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public List<String> getWithout_notice_user_list() {
            return without_notice_user_list;
        }

        public void setWithout_notice_user_list(List<String> without_notice_user_list) {
            this.without_notice_user_list = without_notice_user_list;
        }
    }
}
