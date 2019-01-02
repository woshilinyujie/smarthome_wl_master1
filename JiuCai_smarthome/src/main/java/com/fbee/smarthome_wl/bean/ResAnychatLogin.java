package com.fbee.smarthome_wl.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wl on 2017/8/24.
 */

public class ResAnychatLogin {


    /**
     * status : success
     * user : {"id":14360,"name":"33333333331","city":null,"email":"","device_count":1,"user_devices":[{"id":30035,"alias":"11111","device_name":"052020163200106","device_anychat_id":"100362","version":"1.0.3","device_type_id":null}]}
     */

    private String status;
    private UserBean user;
    public String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean implements Serializable {
        /**
         * id : 14360
         * name : 33333333331
         * city : null
         * email :
         * device_count : 1
         * user_devices : [{"id":30035,"alias":"11111","device_name":"052020163200106","device_anychat_id":"100362","version":"1.0.3","device_type_id":null}]
         */

        private int id;
        private String name;
        private Object city;
        private String email;
        private int device_count;
        private List<UserDevicesBean> user_devices;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getCity() {
            return city;
        }

        public void setCity(Object city) {
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

        public List<UserDevicesBean> getUser_devices() {
            return user_devices;
        }

        public void setUser_devices(List<UserDevicesBean> user_devices) {
            this.user_devices = user_devices;
        }

        public static class UserDevicesBean implements Serializable{
            /**
             * id : 30035
             * alias : 11111
             * device_name : 052020163200106
             * device_anychat_id : 100362
             * version : 1.0.3
             * device_type_id : null
             */

            private int id;
            private String alias;
            private String device_name;
            private int device_anychat_id;
            private String version;
            private Object device_type_id;

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

            public String getDevice_name() {
                return device_name;
            }

            public void setDevice_name(String device_name) {
                this.device_name = device_name;
            }

            public int getDevice_anychat_id() {
                return device_anychat_id;
            }

            public void setDevice_anychat_id(int device_anychat_id) {
                this.device_anychat_id = device_anychat_id;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public Object getDevice_type_id() {
                return device_type_id;
            }

            public void setDevice_type_id(Object device_type_id) {
                this.device_type_id = device_type_id;
            }

            @Override
            public String toString() {
                return "UserDevicesBean{" +
                        "id=" + id +
                        ", alias='" + alias + '\'' +
                        ", device_name='" + device_name + '\'' +
                        ", device_anychat_id='" + device_anychat_id + '\'' +
                        ", version='" + version + '\'' +
                        ", device_type_id=" + device_type_id +
                        '}';
            }
        }
    }

    @Override
    public String toString() {
        return "ResAnychatLogin{" +
                "status='" + status + '\'' +
                ", user=" + user +
                ", code='" + code + '\'' +
                '}';
    }
}
