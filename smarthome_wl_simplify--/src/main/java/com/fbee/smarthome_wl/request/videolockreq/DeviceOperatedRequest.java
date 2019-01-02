package com.fbee.smarthome_wl.request.videolockreq;


public class DeviceOperatedRequest extends MnsBaseRequest {

    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * data : {"device_user_id":"001,002","be_operated":{"mode":"del","object":"001","object_level":"adm","unlock_mode":"pwd,fp,card"}}
     */

    private String vendor_name;
    private String uuid;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * device_user_id : 001,002
         * be_operated : {"mode":"del","object":"001","object_level":"adm","unlock_mode":"pwd,fp,card"}
         */

        private String device_user_id;
        private BeOperatedBean be_operated;

        public String getDevice_user_id() {
            return device_user_id;
        }

        public void setDevice_user_id(String device_user_id) {
            this.device_user_id = device_user_id;
        }

        public BeOperatedBean getBe_operated() {
            return be_operated;
        }

        public void setBe_operated(BeOperatedBean be_operated) {
            this.be_operated = be_operated;
        }

        public static class BeOperatedBean {
            /**
             * mode : del
             * object : 001
             * object_level : adm
             * unlock_mode : pwd,fp,card
             */

            private String mode;
            private String object;
            private String object_level;
            private String unlock_mode;

            public String getMode() {
                return mode;
            }

            public void setMode(String mode) {
                this.mode = mode;
            }

            public String getObject() {
                return object;
            }

            public void setObject(String object) {
                this.object = object;
            }

            public String getObject_level() {
                return object_level;
            }

            public void setObject_level(String object_level) {
                this.object_level = object_level;
            }

            public String getUnlock_mode() {
                return unlock_mode;
            }

            public void setUnlock_mode(String unlock_mode) {
                this.unlock_mode = unlock_mode;
            }

            @Override
            public String toString() {
                return "BeOperatedBean{" +
                        "mode='" + mode + '\'' +
                        ", object='" + object + '\'' +
                        ", object_level='" + object_level + '\'' +
                        ", unlock_mode='" + unlock_mode + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "device_user_id='" + device_user_id + '\'' +
                    ", be_operated=" + be_operated.toString() +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DeviceOperatedRequest{" +
                "vendor_name='" + vendor_name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
