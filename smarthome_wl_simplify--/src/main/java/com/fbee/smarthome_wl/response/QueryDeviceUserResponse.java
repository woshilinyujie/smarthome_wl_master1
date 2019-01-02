package com.fbee.smarthome_wl.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wl on 2017/4/19.
 */

/***
 * 查询设备用户列表
 */
public class QueryDeviceUserResponse extends BaseResponse implements Serializable{


    /**
     * body : {"type":"1001","username":"xxxxxxx","password":"xxxxxxx","room_area":"卧室","note":"备备的设备","version":"xxxxx","context_vendor_name":"feibee","context_uuid":"Z103","device_user_list":[{"id":"001","note":"设设的设备","without_notice_user_list":["18888888888","16666666666"],"level":"adm"},{"id":"002","note":"备备的设备","without_notice_user_list":["18888888888","16666666666"],"level":"usr"}],"bind_username_list":["18888888888","18888888888"],"network_ssid":"xxxxxxxxx","infrared_alarm_interval_time":"30","infrared_alarm_type":"txt","volume_level":"2","infrared_sensitivity":"50","auth_mode":"0","activation_code":"12311","on_off":"0,1,1,0,1,0,1,0","mac_addr":"xxxxxxxxx","short_id":"123123","dev_model":"Z501","dev_id":"xxxxxxx"}
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
         * type : 1001
         * username : xxxxxxx
         * password : xxxxxxx
         * room_area : 卧室
         * note : 备备的设备
         * version : xxxxx
         * context_vendor_name : feibee
         * context_uuid : Z103
         * device_user_list : [{"id":"001","note":"设设的设备","without_notice_user_list":["18888888888","16666666666"],"level":"adm"},{"id":"002","note":"备备的设备","without_notice_user_list":["18888888888","16666666666"],"level":"usr"}]
         * bind_username_list : ["18888888888","18888888888"]
         * network_ssid : xxxxxxxxx
         * infrared_alarm_interval_time : 30
         * infrared_alarm_type : txt
         * volume_level : 2
         * infrared_sensitivity : 50
         * auth_mode : 0
         * activation_code : 12311
         * on_off : 0,1,1,0,1,0,1,0
         * mac_addr : xxxxxxxxx
         * short_id : 123123
         * dev_model : Z501
         * dev_id : xxxxxxx
         */

        private String type;
        private String username;
        private String password;
        private String room_area;
        private String note;
        private String version;
        private String context_vendor_name;
        private String context_uuid;
        private String network_ssid;
        private String infrared_alarm_interval_time;
        private String infrared_alarm_type;
        private String volume_level;
        private String infrared_sensitivity;
        private String auth_mode;
        private String activation_code;
        private String on_off;
        private String mac_addr;
        private String short_id;
        private String dev_model;
        private String dev_id;
        private List<DeviceUserListBean> device_user_list;
        private List<String> bind_username_list;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public String getRoom_area() {
            return room_area;
        }

        public void setRoom_area(String room_area) {
            this.room_area = room_area;
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

        public String getContext_vendor_name() {
            return context_vendor_name;
        }

        public void setContext_vendor_name(String context_vendor_name) {
            this.context_vendor_name = context_vendor_name;
        }

        public String getContext_uuid() {
            return context_uuid;
        }

        public void setContext_uuid(String context_uuid) {
            this.context_uuid = context_uuid;
        }

        public String getNetwork_ssid() {
            return network_ssid;
        }

        public void setNetwork_ssid(String network_ssid) {
            this.network_ssid = network_ssid;
        }

        public String getInfrared_alarm_interval_time() {
            return infrared_alarm_interval_time;
        }

        public void setInfrared_alarm_interval_time(String infrared_alarm_interval_time) {
            this.infrared_alarm_interval_time = infrared_alarm_interval_time;
        }

        public String getInfrared_alarm_type() {
            return infrared_alarm_type;
        }

        public void setInfrared_alarm_type(String infrared_alarm_type) {
            this.infrared_alarm_type = infrared_alarm_type;
        }

        public String getVolume_level() {
            return volume_level;
        }

        public void setVolume_level(String volume_level) {
            this.volume_level = volume_level;
        }

        public String getInfrared_sensitivity() {
            return infrared_sensitivity;
        }

        public void setInfrared_sensitivity(String infrared_sensitivity) {
            this.infrared_sensitivity = infrared_sensitivity;
        }

        public String getAuth_mode() {
            return auth_mode;
        }

        public void setAuth_mode(String auth_mode) {
            this.auth_mode = auth_mode;
        }

        public String getActivation_code() {
            return activation_code;
        }

        public void setActivation_code(String activation_code) {
            this.activation_code = activation_code;
        }

        public String getOn_off() {
            return on_off;
        }

        public void setOn_off(String on_off) {
            this.on_off = on_off;
        }

        public String getMac_addr() {
            return mac_addr;
        }

        public void setMac_addr(String mac_addr) {
            this.mac_addr = mac_addr;
        }

        public String getShort_id() {
            return short_id;
        }

        public void setShort_id(String short_id) {
            this.short_id = short_id;
        }

        public String getDev_model() {
            return dev_model;
        }

        public void setDev_model(String dev_model) {
            this.dev_model = dev_model;
        }

        public String getDev_id() {
            return dev_id;
        }

        public void setDev_id(String dev_id) {
            this.dev_id = dev_id;
        }

        public List<DeviceUserListBean> getDevice_user_list() {
            return device_user_list;
        }

        public void setDevice_user_list(List<DeviceUserListBean> device_user_list) {
            this.device_user_list = device_user_list;
        }

        public List<String> getBind_username_list() {
            return bind_username_list;
        }

        public void setBind_username_list(List<String> bind_username_list) {
            this.bind_username_list = bind_username_list;
        }

        public static class DeviceUserListBean implements Serializable{
            /**
             * id : 001
             * note : 设设的设备
             * without_notice_user_list : ["18888888888","16666666666"]
             * level : adm
             */

            private String id;
            private String note;
            private String level;
            private List<String> without_notice_user_list;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public List<String> getWithout_notice_user_list() {
                return without_notice_user_list;
            }

            public void setWithout_notice_user_list(List<String> without_notice_user_list) {
                this.without_notice_user_list = without_notice_user_list;
            }
        }
    }
}
