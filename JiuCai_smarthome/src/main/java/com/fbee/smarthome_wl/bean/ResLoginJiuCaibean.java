package com.fbee.smarthome_wl.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wl on 2017/8/24.
 */

public class ResLoginJiuCaibean {
    /**
     * status : success
     * user : {"id":"1","name":"测试","city":"深圳","email":"\u201910798985555@qq.com\u2019","device_count":1}
     * user_devices : [{"id":1,"alias":"\u2019标签名\u2019","device_ name":"设备一","device_anychat_id":"\u201911602\u2019","version":"\u20191.0.2\u2019"},{"id":2,"alias":"\u2019标签名\u2019","device_ name":"设备二","device_anychat_id":"\u201911603\u2019","version":"\u20191.0.2\u2019"}]
     */

    private String status;
    private UserEntity user;
    private List<UserDevicesEntity> user_devices;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<UserDevicesEntity> getUser_devices() {
        return user_devices;
    }

    public void setUser_devices(List<UserDevicesEntity> user_devices) {
        this.user_devices = user_devices;
    }

    public static class UserEntity {
        /**
         * id : 1
         * name : 测试
         * city : 深圳
         * email : ’10798985555@qq.com’
         * device_count : 1
         */

        private String id;
        private String name;
        private String city;
        private String email;
        private int device_count;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getDevice_count() {
            return device_count;
        }

        public void setDevice_count(int device_count) {
            this.device_count = device_count;
        }
    }

    public static class UserDevicesEntity {
        /**
         * id : 1
         * alias : ’标签名’
         * device_ name : 设备一
         * device_anychat_id : ’11602’
         * version : ’1.0.2’
         */

        private int id;
        private String alias;
        @SerializedName("device_ name")
        private String _$Device_Name222; // FIXME check this code
        private String device_anychat_id;
        private String version;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String get_$Device_Name222() {
            return _$Device_Name222;
        }

        public void set_$Device_Name222(String _$Device_Name222) {
            this._$Device_Name222 = _$Device_Name222;
        }

        public String getDevice_anychat_id() {
            return device_anychat_id;
        }

        public void setDevice_anychat_id(String device_anychat_id) {
            this.device_anychat_id = device_anychat_id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
