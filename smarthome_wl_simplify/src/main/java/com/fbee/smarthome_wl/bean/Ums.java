package com.fbee.smarthome_wl.bean;

/**
 * @class name：com.fbee.smarthome_wl.bean
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/28 17:00
 */
public class Ums<T> {

    /**
     * UMS : {"header":{"api_version":"1.0","message_type":"MSG_SMS_CODE_REQ","seq_id":"1"},"body":{"global_roaming":"0086","username":"18888888888"}}
     */
    private T UMS;

    public T getUMS() {
        return UMS;
    }

    public void setUMS(T UMS) {
        this.UMS = UMS;
    }

//    public  class UMSBean {
//        /**
//         * header : {"api_version":"1.0","message_type":"MSG_SMS_CODE_REQ","seq_id":"1"}
//         * body : {"global_roaming":"0086","username":"18888888888"}
//         */


//        public static class BodyBean {
//            /**
//             * global_roaming : 0086
//             * username : 18888888888
//             */
//
//            private String global_roaming;
//            private String username;
//
//            public String getGlobal_roaming() {
//                return global_roaming;
//            }
//
//            public void setGlobal_roaming(String global_roaming) {
//                this.global_roaming = global_roaming;
//            }
//
//            public String getUsername() {
//                return username;
//            }
//
//            public void setUsername(String username) {
//                this.username = username;
//            }
//        }
//    }
}
