package com.fbee.smarthome_wl.bean;

/**
 * Created by linyujie on 18/12/22.
 */

public class AmsR {

    /**
     * AMS : {"header":{"api_version":"1.0","message_type":"MSG_TOKEN_GET_EX_RSP","seq_id":"1","http_code":"200","return_string":"RETURN_SUCCESS_OK_STRING"},"body":{"accessToken":"XXXXXXXXXXXXXX","expire_time":"xxxxxxxxx"}}
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
         * header : {"api_version":"1.0","message_type":"MSG_TOKEN_GET_EX_RSP","seq_id":"1","http_code":"200","return_string":"RETURN_SUCCESS_OK_STRING"}
         * body : {"accessToken":"XXXXXXXXXXXXXX","expire_time":"xxxxxxxxx"}
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
             * message_type : MSG_TOKEN_GET_EX_RSP
             * seq_id : 1
             * http_code : 200
             * return_string : RETURN_SUCCESS_OK_STRING
             */

            private String api_version;
            private String message_type;
            private String seq_id;
            private String http_code;
            private String return_string;

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

            public String getHttp_code() {
                return http_code;
            }

            public void setHttp_code(String http_code) {
                this.http_code = http_code;
            }

            public String getReturn_string() {
                return return_string;
            }

            public void setReturn_string(String return_string) {
                this.return_string = return_string;
            }
        }

        public static class BodyBean {
            /**
             * accessToken : XXXXXXXXXXXXXX
             * expire_time : xxxxxxxxx
             */

            private String accessToken;
            private String expire_time;

            public String getAccessToken() {
                return accessToken;
            }

            public void setAccessToken(String accessToken) {
                this.accessToken = accessToken;
            }

            public String getExpire_time() {
                return expire_time;
            }

            public void setExpire_time(String expire_time) {
                this.expire_time = expire_time;
            }
        }
    }
}
