package com.fbee.smarthome_wl.response;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/11 9:12
 */
public class QueryGateWayInfoResponse extends BaseResponse{

    /**
     * 网关信息请求返回
     * body : {"username":"xxxxxxxxx","password":"xxxxxxxxx","authorization":"admin","note":"网网的网关","version":"xxx","location":{"longitude":"xxxx","latitude":"xxxx","highly":"xxxx","countries":"xxxx","province":"xxxx","city":"xxxx","partition":"xxxx","street":"xxxx"},"device_list":[{"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","username":"xxxxxxx","password":"xxxxxxx","room_id":"xxxxx","room_name":"卧室","note":"备备的设备","context_vendor_name":"feibee","context_uuid":"xxxxxxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","username":"xxxxxxx","password":"xxxxxxx","room_id":"xxxxx","room_name":"卧室","note":"备备的设备","context_vendor_name":"feibee","context_uuid":"xxxxxxx"}]}
     */

    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        /**
         * username : xxxxxxxxx
         * password : xxxxxxxxx
         * authorization : admin
         * note : 网网的网关
         * version : xxx
         * location : {"longitude":"xxxx","latitude":"xxxx","highly":"xxxx","countries":"xxxx","province":"xxxx","city":"xxxx","partition":"xxxx","street":"xxxx"}
         * device_list : [{"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","username":"xxxxxxx","password":"xxxxxxx","room_id":"xxxxx","room_name":"卧室","note":"备备的设备","context_vendor_name":"feibee","context_uuid":"xxxxxxx"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","username":"xxxxxxx","password":"xxxxxxx","room_id":"xxxxx","room_name":"卧室","note":"备备的设备","context_vendor_name":"feibee","context_uuid":"xxxxxxx"}]
         */

        private String username;
        private String password;
        private String authorization;
        private String note;
        private String version;
        private LocationBean location;
        private List<DeviceListBean> device_list;

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

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public List<DeviceListBean> getDevice_list() {
            return device_list;
        }

        public void setDevice_list(List<DeviceListBean> device_list) {
            this.device_list = device_list;
        }

        public static class LocationBean {
            /**
             * longitude : xxxx
             * latitude : xxxx
             * highly : xxxx
             * countries : xxxx
             * province : xxxx
             * city : xxxx
             * partition : xxxx
             * street : xxxx
             */

            private String longitude;
            private String latitude;
            private String highly;
            private String countries;
            private String province;
            private String city;
            private String partition;
            private String street;

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getHighly() {
                return highly;
            }

            public void setHighly(String highly) {
                this.highly = highly;
            }

            public String getCountries() {
                return countries;
            }

            public void setCountries(String countries) {
                this.countries = countries;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getPartition() {
                return partition;
            }

            public void setPartition(String partition) {
                this.partition = partition;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }
        }

        public static class DeviceListBean {
            /**
             * vendor_name : feibee
             * uuid : xxxxxxxxxx
             * type : 门锁
             * username : xxxxxxx
             * password : xxxxxxx
             * room_id : xxxxx
             * room_name : 卧室
             * note : 备备的设备
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
            private String context_vendor_name;
            private String context_uuid;
            private String state;

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
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
}
