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
     * body : {"type":"猫眼","username":"xxxxxxx","password":"xxxxxxx","room_area":"卧室","note":"备备的设备","version":"xxxxx","context_vendor_name":"feibee","context_uuid":"Z103","device_user_list":[{"id":"001","note":"设设的设备","without_notice_user_list":["18888888888","16666666666"]},{"id":"002","note":"备备的设备","without_notice_user_list":["18888888888","16666666666"]}]}
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
         * type : 猫眼
         * username : xxxxxxx
         * password : xxxxxxx
         * room_area : 卧室
         * note : 备备的设备
         * version : xxxxx
         * context_vendor_name : feibee
         * context_uuid : Z103
         * device_user_list : [{"id":"001","note":"设设的设备","without_notice_user_list":["18888888888","16666666666"]},{"id":"002","note":"备备的设备","without_notice_user_list":["18888888888","16666666666"]}]
         */

        private String type;
        private String username;
        private String password;
        private String room_area;
        private String note;
        private String version;
        private String context_vendor_name;
        private String context_uuid;
        private List<DeviceUserListBean> device_user_list;

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

        public List<DeviceUserListBean> getDevice_user_list() {
            return device_user_list;
        }

        public void setDevice_user_list(List<DeviceUserListBean> device_user_list) {
            this.device_user_list = device_user_list;
        }

        public static class DeviceUserListBean implements Serializable{
            /**
             * id : 001
             * note : 设设的设备
             * without_notice_user_list : ["18888888888","16666666666"]
             */

            private String id;
            private String note;
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

            public List<String> getWithout_notice_user_list() {
                return without_notice_user_list;
            }

            public void setWithout_notice_user_list(List<String> without_notice_user_list) {
                this.without_notice_user_list = without_notice_user_list;
            }
        }
    }
}
