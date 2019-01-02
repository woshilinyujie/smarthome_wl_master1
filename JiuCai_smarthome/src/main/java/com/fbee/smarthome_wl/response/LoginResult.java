package com.fbee.smarthome_wl.response;

import com.fbee.smarthome_wl.utils.AES256Encryption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录返回
 *
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/1 10:04
 */
public class LoginResult extends BaseResponse implements Serializable {


    /**
     * body : {"secret_key":"xxxxxxxxx","user_alias":"xxxxxxx","father_username":"xxxxxxxxx","gateway_list":[{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxxxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxxxx"}],"child_user_list":[{"username":"xxxxxxx","user_alias":"老婆"},{"username":"xxxxxxx","user_alias":"女儿"}]}
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
         * secret_key : xxxxxxxxx
         * user_alias : xxxxxxx
         * father_username : xxxxxxxxx
         * gateway_list : [{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxxxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxxxx"}]
         * child_user_list : [{"username":"xxxxxxx","user_alias":"老婆"},{"username":"xxxxxxx","user_alias":"女儿"}]
         */

        private String secret_key;
        private String user_alias;
        private String father_username;
        private List<GatewayListBean> gateway_list;
        private List<ChildUserListBean> child_user_list;

        public String getSecret_key() {
            return secret_key;
        }

        public void setSecret_key(String secret_key) {
            this.secret_key = secret_key;
        }

        public String getUser_alias() {
            return user_alias;
        }

        public void setUser_alias(String user_alias) {
            this.user_alias = user_alias;
        }

        public String getFather_username() {
            return father_username;
        }

        public void setFather_username(String father_username) {
            this.father_username = father_username;
        }

        public List<GatewayListBean> getGateway_list() {
            if(gateway_list==null||gateway_list.size()==0){
                gateway_list = new ArrayList<>();
            }
            return gateway_list;
        }

        public void setGateway_list(List<GatewayListBean> gateway_list) {
            if(this.gateway_list==null){
                this.gateway_list= new ArrayList<>();
            }
            this.gateway_list = gateway_list;
        }

        public List<ChildUserListBean> getChild_user_list() {
            if(child_user_list==null){
                child_user_list=new ArrayList<ChildUserListBean>();
            }
            return child_user_list;
        }

        public void setChild_user_list(List<ChildUserListBean> child_user_list) {
            this.child_user_list = child_user_list;
        }

        public static class GatewayListBean implements Serializable{
            /**
             * vendor_name : feibee
             * uuid : xxxxxxxxxx
             * username : xxxxxxxxx
             * password : xxxxxxxxx
             * authorization : admin
             * note : 网网的网关
             * version : xxxxx
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
                String psw = password;
                try {
                    psw = AES256Encryption.decrypt(password, uuid);
                } catch (Exception e) {
                }
                return psw;
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

        public static class ChildUserListBean implements Serializable{
            /**
             * username : xxxxxxx
             * user_alias : 老婆
             */

            private String username;
            private String user_alias;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getUser_alias() {
                return user_alias;
            }

            public void setUser_alias(String user_alias) {
                this.user_alias = user_alias;
            }
        }
    }
}
