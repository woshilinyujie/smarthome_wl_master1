package com.fbee.smarthome_wl.request.videolockreq;

public class DeviceAlarmRequest extends MnsBaseRequest {


    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * data : {"timestamp":"1499681215","type":"WonlyVideoLock","note":"我的视频锁","device_user_id":"001","alarm_type":"noatmpt","alarm_message":"https://download.wonlycloud.com/slideshow/banner2.png","alarm_message_type":"pic"}
     */

    private String vendor_name;
    private String uuid;
    private DataBean data;

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
         * alarm_type : noatmpt
         * alarm_message : https://download.wonlycloud.com/slideshow/banner2.png
         * alarm_message_type : pic
         */

        private String timestamp;
        private String type;
        private String note;
        private String device_user_id;
        private String alarm_type;
        private String alarm_message;
        private String alarm_message_type;

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

        public String getAlarm_type() {
            return alarm_type;
        }

        public void setAlarm_type(String alarm_type) {
            this.alarm_type = alarm_type;
        }

        public String getAlarm_message() {
            return alarm_message;
        }

        public void setAlarm_message(String alarm_message) {
            this.alarm_message = alarm_message;
        }

        public String getAlarm_message_type() {
            return alarm_message_type;
        }

        public void setAlarm_message_type(String alarm_message_type) {
            this.alarm_message_type = alarm_message_type;
        }
    }
}
