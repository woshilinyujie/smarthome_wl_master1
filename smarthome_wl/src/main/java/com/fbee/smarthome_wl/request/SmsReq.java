package com.fbee.smarthome_wl.request;

/**
 * 验证码
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/1 8:41
 */
public class SmsReq{

        /**
         * global_roaming : +86
         * username : 18888888888
         */

        private String global_roaming;
        private String username;

        public String getGlobal_roaming() {
            return global_roaming;
        }

        public void setGlobal_roaming(String global_roaming) {
            this.global_roaming = global_roaming;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
}
