package com.fbee.smarthome_wl.request;

import com.fbee.smarthome_wl.response.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wl on 2017/6/9.
 */

public class QueryGateWayInfoReq {

        private BodyEntity body;

        public BodyEntity getBody() {
            return body;
        }

        public void setBody(BodyEntity body) {
            this.body = body;
        }

        public static class BodyEntity {
            /**
             * password : 123456
             * device_list : [{"type":"1001","vendor_name":"eques","uuid":"97f9eafe08ee4c6aa6e8cc82e11d77fe"}]
             * version : 1.0.0
             * note : 17706815219
             * username : 17706815219
             * authorization : admin
             */

            private String password;
            private String version;
            private String note;
            private String username;
            private String authorization;
            private List<DeviceListEntity> device_list;

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getAuthorization() {
                return authorization;
            }

            public void setAuthorization(String authorization) {
                this.authorization = authorization;
            }

            public List<DeviceListEntity> getDevice_list() {
                return device_list;
            }

            public void setDevice_list(List<DeviceListEntity> device_list) {
                this.device_list = device_list;
            }

            public static class DeviceListEntity {
                /**
                 * type : 1001
                 * vendor_name : eques
                 * uuid : 97f9eafe08ee4c6aa6e8cc82e11d77fe
                 */

                private String type;
                private String vendor_name;
                private String uuid;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
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
            }
        }
    }
