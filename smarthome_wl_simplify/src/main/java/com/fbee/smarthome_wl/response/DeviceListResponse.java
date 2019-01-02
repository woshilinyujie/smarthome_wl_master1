package com.fbee.smarthome_wl.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wl on 2017/4/18.
 */

/***
 * 添加设备用户返回码
 */
public class DeviceListResponse extends BaseResponse implements Serializable{

    /**
     * body : [{"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"猫眼","username":"xxxxxxx","password":"xxxxxxx","room_area":"卧室","alias":"备备的设备","version":"xxxxx","context_vendor_name":"feibee","context_uuid":"Z103"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","username":"xxxxxxx","password":"xxxxxxx","room_area":"卧室","alias":"设备的设备","version":"xxxxx","context_vendor_name":"feibee","context_uuid":"xxxxxxx"}]
     */

    private List<BodyEntity> body;

    public void setBody(List<BodyEntity> body) {
        this.body = body;
    }

    public List<BodyEntity> getBody() {
        return body;
    }

    public static class BodyEntity implements Serializable{
        /**
         * vendor_name : feibee
         * uuid : xxxxxxxxxx
         * type : 猫眼
         * username : xxxxxxx
         * password : xxxxxxx
         * room_area : 卧室
         * alias : 备备的设备
         * version : xxxxx
         * context_vendor_name : feibee
         * context_uuid : Z103
         */

        private String vendor_name;
        private String uuid;
        private String type;
        private String username;
        private String password;
        private String room_area;
        private String alias;
        private String version;
        private String context_vendor_name;
        private String context_uuid;

        public void setVendor_name(String vendor_name) {
            this.vendor_name = vendor_name;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setRoom_area(String room_area) {
            this.room_area = room_area;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void setContext_vendor_name(String context_vendor_name) {
            this.context_vendor_name = context_vendor_name;
        }

        public void setContext_uuid(String context_uuid) {
            this.context_uuid = context_uuid;
        }

        public String getVendor_name() {
            return vendor_name;
        }

        public String getUuid() {
            return uuid;
        }

        public String getType() {
            return type;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRoom_area() {
            return room_area;
        }

        public String getAlias() {
            return alias;
        }

        public String getVersion() {
            return version;
        }

        public String getContext_vendor_name() {
            return context_vendor_name;
        }

        public String getContext_uuid() {
            return context_uuid;
        }
    }
}
