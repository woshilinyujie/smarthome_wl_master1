package com.fbee.smarthome_wl.response;

import java.io.Serializable;
import java.util.List;

/**
 * 查询子用户信息响应体
 * Created by WLPC on 2017/4/18.
 */

public class QuerySubUserInfoResponse extends BaseResponse implements Serializable {


    /**
     * body : {"father_username":"xxxxxxxxx","user_alias":"老婆","gateway_list":[{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxx"}]}
     */

    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean implements Serializable{
        /**
         * father_username : xxxxxxxxx
         * user_alias : 老婆
         * gateway_list : [{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxx"}]
         */

        private String father_username;
        private String user_alias;
        private List<GatewayListBean> gateway_list;

        public String getFather_username() {
            return father_username;
        }

        public void setFather_username(String father_username) {
            this.father_username = father_username;
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

        public static class GatewayListBean implements Serializable{
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
}
