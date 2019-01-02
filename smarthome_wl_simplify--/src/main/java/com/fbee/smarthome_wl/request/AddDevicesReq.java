package com.fbee.smarthome_wl.request;

/**
 * 添加设备请求体
 * Created by WLPC on 2017/4/18.
 */

public class AddDevicesReq {


    /**
     * gateway_vendor_name : feibee
     * gateway_uuid : xxxxxxxxxx
     * device : {"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","username":"xxxxxxx","password":"xxxxxxx","room_id":"xxxxx","room_name":"卧室","note":"备备的设备","version":"xxxxx","context_vendor_name":"feibee","context_uuid":"xxxxxxx"}
     */

    private String gateway_vendor_name;
    private String gateway_uuid;
    private DeviceBean device;

    public String getGateway_vendor_name() {
        return gateway_vendor_name;
    }

    public void setGateway_vendor_name(String gateway_vendor_name) {
        this.gateway_vendor_name = gateway_vendor_name;
    }

    public String getGateway_uuid() {
        return gateway_uuid;
    }

    public void setGateway_uuid(String gateway_uuid) {
        this.gateway_uuid = gateway_uuid;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public static class DeviceBean {
        /**
         * vendor_name : feibee
         * uuid : xxxxxxxxxx
         * type : 门锁
         * username : xxxxxxx
         * password : xxxxxxx
         * room_id : xxxxx
         * room_name : 卧室
         * note : 备备的设备
         * version : xxxxx
         * context_vendor_name : feibee
         * context_uuid : xxxxxxx
         */

        private String vendor_name;
        private String uuid;
        private String type;
        private String username;
        private String password;
        private String room_id;
        private String room_name;
        private String note;
        private String version;
        private String context_vendor_name;
        private String context_uuid;
        private String short_id;

        public String getShort_id() {
            return short_id;
        }

        public void setShort_id(String short_id) {
            this.short_id = short_id;
        }

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

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
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
    }
}
