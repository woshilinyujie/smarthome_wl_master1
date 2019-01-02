package com.fbee.smarthome_wl.request.videolockreq;

/**
 * Created by WLPC on 2017/9/30.
 */

public class DataBean {

        /**
         * device_user_id : 001
         * alarm_type : noatmpt
         * alarm_message : https://download.wonlycloud.com/slideshow/banner2.png
         * alarm_message_type : pic
         */

        private String device_user_id;
        private String alarm_type;
        private String alarm_message;
        private String alarm_message_type;

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
