package com.fbee.smarthome_wl.request;

import java.util.List;

/**
 * 添加子用户
 * @class name：com.fbee.smarthome_wl.request
 *
 * @time 2017/4/18 14:52
 */
public class AddChildUserReq  {


    /**
     * global_roaming : 0086
     * username : 18888888888
     * password : xxxxxxx
     * sms_code : 666666
     * user_alias : 老婆
     * gateway_list : [{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxx"}]
     */

    private String global_roaming;
    private String username;
    private String password;
    private String sms_code;
    private String user_alias;
    private List<GatewayListBean> gateway_list;

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

    public String getUser_alias() {
        return user_alias;
    }

    public void setUser_alias(String user_alias) {
        this.user_alias = user_alias;
    }

    public List<GatewayListBean> getGateway_list() {
        return gateway_list;
    }

    public void setGateway_list(List<GatewayListBean> gateway_list) {
        this.gateway_list = gateway_list;
    }

    public static class GatewayListBean {
        /**
         * vendor_name : feibee
         * uuid : xxxxxxxxxx
         * username : xxxxxxxxx
         * password : xxxxxxxxx
         * authorization : admin
         * note : 网网的网关
         * version : xxx
         */

        private String vendor_name;
        private String uuid;
        private String username;
        private String password;
        private String authorization;
        private String note;
        private String version;

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

        public String getAuthorization() {
            return authorization;
        }

        public void setAuthorization(String authorization) {
            this.authorization = authorization;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
