package com.fbee.smarthome_wl.request;

/**
 * Created by wl on 2017/4/25.
 */

public class UpdataApkReq extends BaseReq{

    /**
     * body : {"token":"xxxxxxxxxxxxxxx","product_name":"wonly","platform":"android","endpoint_type":"H60-L11","current_version":"1.0.0"}
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
         * token : xxxxxxxxxxxxxxx
         * product_name : wonly
         * platform : android
         * endpoint_type : H60-L11
         * current_version : 1.0.0
         */

        private String token;
        private String product_name;
        private String platform;
        private String endpoint_type;
        private String current_version;

        public void setToken(String token) {
            this.token = token;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public void setEndpoint_type(String endpoint_type) {
            this.endpoint_type = endpoint_type;
        }

        public void setCurrent_version(String current_version) {
            this.current_version = current_version;
        }

        public String getToken() {
            return token;
        }

        public String getProduct_name() {
            return product_name;
        }

        public String getPlatform() {
            return platform;
        }

        public String getEndpoint_type() {
            return endpoint_type;
        }

        public String getCurrent_version() {
            return current_version;
        }
    }
}
