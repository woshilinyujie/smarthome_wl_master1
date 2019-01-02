package com.fbee.smarthome_wl.response;

import java.util.List;

/**
 * Created by wl on 2017/9/11.
 */

public class DoorLockAlarmResponse extends BaseResponse {

    private List<BodyEntity> body;

    public List<BodyEntity> getBody() {
        return body;
    }

    public void setBody(List<BodyEntity> body) {
        this.body = body;
    }

    public static class BodyEntity {
        /**
         * _id : 5420013272fe096c39901049
         * timestamp : 1499681215
         * device_user_id : 001
         * alarm_type : noatmpt
         * alarm_message : https://download.wonlycloud.com/slideshow/banner1.png
         */

        private String _id;
        private String timestamp;
        private String device_user_id;
        private String alarm_type;
        private String alarm_message;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
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
    }
}
