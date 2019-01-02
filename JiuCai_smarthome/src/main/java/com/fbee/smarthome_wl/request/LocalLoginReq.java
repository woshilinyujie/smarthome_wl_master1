package com.fbee.smarthome_wl.request;

/**
 * 本地服务器登录请求
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/1 10:58
 */
public class LocalLoginReq{


    /**
     * username : 18888888888
     * password : xxxxxxxxx
     * sms_code : 666666
     * phone : {"uuid":"xxxxxxxxxx","endpoint_type":"OPPO R9s","platform":"android","platform_version":"4.3","network_type":"3G"}
     */

    private String username;
    private String password;
    private String sms_code;
    private PhoneBean phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public PhoneBean getPhone() {
        return phone;
    }

    public void setPhone(PhoneBean phone) {
        this.phone = phone;
    }

    public static class PhoneBean {
        /**
         * uuid : xxxxxxxxxx
         * endpoint_type : OPPO R9s
         * platform : android
         * platform_version : 4.3
         * network_type : 3G
         */

        private String uuid;
        private String endpoint_type;
        private String platform;
        private String platform_version;
        private String network_type;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getEndpoint_type() {
            return endpoint_type;
        }

        public void setEndpoint_type(String endpoint_type) {
            this.endpoint_type = endpoint_type;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getPlatform_version() {
            return platform_version;
        }

        public void setPlatform_version(String platform_version) {
            this.platform_version = platform_version;
        }

        public String getNetwork_type() {
            return network_type;
        }

        public void setNetwork_type(String network_type) {
            this.network_type = network_type;
        }
    }
}
