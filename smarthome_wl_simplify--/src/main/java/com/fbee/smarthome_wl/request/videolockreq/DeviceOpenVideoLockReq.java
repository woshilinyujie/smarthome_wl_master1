package com.fbee.smarthome_wl.request.videolockreq;

/**
 * Created by WLPC on 2017/10/13.
 */

public class DeviceOpenVideoLockReq {


    /**
     * api_version : 1.0
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * data : {"timestamp":"1499681215","type":"WonlyVideoLock","note":"我的视频锁","device_user_id":"001","device_user_note":"阿三","unlock_mode":"pwd","auth_mode":"dbl","stress_status":"false","op_type":"enter_menu"}
     */

    private String api_version;
    private String vendor_name;
    private String uuid;
    private DataBean data;

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * timestamp : 1499681215
         * type : WonlyVideoLock
         * note : 我的视频锁
         * device_user_id : 001
         * device_user_note : 阿三
         * unlock_mode : pwd
         * auth_mode : dbl
         * stress_status : false
         * op_type : enter_menu
         */

        private String timestamp;
        private String type;
        private String note;
        private String device_user_id;
        private String device_user_note;
        private String unlock_mode;
        private String auth_mode;
        private String stress_status;
        private String op_type;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getDevice_user_id() {
            return device_user_id;
        }

        public void setDevice_user_id(String device_user_id) {
            this.device_user_id = device_user_id;
        }

        public String getDevice_user_note() {
            return device_user_note;
        }

        public void setDevice_user_note(String device_user_note) {
            this.device_user_note = device_user_note;
        }

        public String getUnlock_mode() {
            return unlock_mode;
        }

        public void setUnlock_mode(String unlock_mode) {
            this.unlock_mode = unlock_mode;
        }

        public String getAuth_mode() {
            return auth_mode;
        }

        public void setAuth_mode(String auth_mode) {
            this.auth_mode = auth_mode;
        }

        public String getStress_status() {
            return stress_status;
        }

        public void setStress_status(String stress_status) {
            this.stress_status = stress_status;
        }

        public String getOp_type() {
            return op_type;
        }

        public void setOp_type(String op_type) {
            this.op_type = op_type;
        }
    }
}
