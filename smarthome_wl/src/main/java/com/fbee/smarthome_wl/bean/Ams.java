package com.fbee.smarthome_wl.bean;

/**
 * Created by linyujie on 18/12/22.
 */

public class Ams {

    /**
     * AMS : {"header":{"api_version":"1.0","message_type":"MSG_TOKEN_GET_EX_REQ","seq_id":"1"},"body":{"username":"18888888888","secret_key":"xxxxxxxxx","vendor_name":"ys7"}}
     */

    private AMSBean AMS;

    public AMSBean getAMS() {
        return AMS;
    }

    public void setAMS(AMSBean AMS) {
        this.AMS = AMS;
    }

    public static class AMSBean {
        /**
         * header : {"api_version":"1.0","message_type":"MSG_TOKEN_GET_EX_REQ","seq_id":"1"}
         * body : {"username":"18888888888","secret_key":"xxxxxxxxx","vendor_name":"ys7"}
         */

        private HeaderBean header;
        private BodyBean body;

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public BodyBean getBody() {
            return body;
        }

        public void setBody(BodyBean body) {
            this.body = body;
        }

        public static class HeaderBean {
            /**
             * api_version : 1.0
             * message_type : MSG_TOKEN_GET_EX_REQ
             * seq_id : 1
             */

            private String api_version;
            private String message_type;
            private String seq_id;

            public String getApi_version() {
                return api_version;
            }

            public void setApi_version(String api_version) {
                this.api_version = api_version;
            }

            public String getMessage_type() {
                return message_type;
            }

            public void setMessage_type(String message_type) {
                this.message_type = message_type;
            }

            public String getSeq_id() {
                return seq_id;
            }

            public void setSeq_id(String seq_id) {
                this.seq_id = seq_id;
            }
        }

        public static class BodyBean {
            /**
             * username : 18888888888
             * secret_key : xxxxxxxxx
             * vendor_name : ys7
             */

            private String username;
            private String secret_key;
            private String vendor_name;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getSecret_key() {
                return secret_key;
            }

            public void setSecret_key(String secret_key) {
                this.secret_key = secret_key;
            }

            public String getVendor_name() {
                return vendor_name;
            }

            public void setVendor_name(String vendor_name) {
                this.vendor_name = vendor_name;
            }
        }
    }
}
