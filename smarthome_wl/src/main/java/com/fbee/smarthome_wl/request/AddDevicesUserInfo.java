package com.fbee.smarthome_wl.request;

import java.util.List;

/**
 * Created by wl on 2017/4/18.
 */

/***
 * 添加设备用户
 */
public class AddDevicesUserInfo extends BaseReq {


    /**
     * body : {"vendor_name":"feibee","uuid":"xxxxxxxxxx","device_user":{"id":"001","alias":"设设的设备","without_notice_user_list":["18888888888","16666666666"]}}
     */

    private BodyEntity body;

    public void setBody(BodyEntity body) {
        this.body = body;
    }

    public BodyEntity getBody() {
        return body;
    }

    public static class BodyEntity {
        /**
         * vendor_name : feibee
         * uuid : xxxxxxxxxx
         * device_user : {"id":"001","alias":"设设的设备","without_notice_user_list":["18888888888","16666666666"]}
         */

        private String vendor_name;
        private String uuid;
        private DeviceUserEntity device_user;

        public void setVendor_name(String vendor_name) {
            this.vendor_name = vendor_name;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public void setDevice_user(DeviceUserEntity device_user) {
            this.device_user = device_user;
        }

        public String getVendor_name() {
            return vendor_name;
        }

        public String getUuid() {
            return uuid;
        }

        public DeviceUserEntity getDevice_user() {
            return device_user;
        }

        public static class DeviceUserEntity {
            /**
             * id : 001
             * alias : 设设的设备
             * without_notice_user_list : ["18888888888","16666666666"]
             */

            private String id;
            private String alias;
            private List<String> without_notice_user_list;

            public void setId(String id) {
                this.id = id;
            }

            public void setAlias(String alias) {
                this.alias = alias;
            }

            public void setWithout_notice_user_list(List<String> without_notice_user_list) {
                this.without_notice_user_list = without_notice_user_list;
            }

            public String getId() {
                return id;
            }

            public String getAlias() {
                return alias;
            }

            public List<String> getWithout_notice_user_list() {
                return without_notice_user_list;
            }
        }
    }
}
