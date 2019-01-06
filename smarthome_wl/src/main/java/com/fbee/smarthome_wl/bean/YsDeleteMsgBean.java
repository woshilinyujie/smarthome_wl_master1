package com.fbee.smarthome_wl.bean;

/**
 * Created by linyujie on 19/1/3.
 */

public class YsDeleteMsgBean  {

    /**
     * UMS : {"header":{"api_version":"1.0","message_type":"MSG_ALARM_DATA_DEL_RSP","seq_id":"1","http_code":"200","return_string":"RETURN_SUCCESS_OK_STRING"}}
     */

    private UMSBean UMS;

    public UMSBean getUMS() {
        return UMS;
    }

    public void setUMS(UMSBean UMS) {
        this.UMS = UMS;
    }

    public static class UMSBean {
        /**
         * header : {"api_version":"1.0","message_type":"MSG_ALARM_DATA_DEL_RSP","seq_id":"1","http_code":"200","return_string":"RETURN_SUCCESS_OK_STRING"}
         */

        private HeaderBean header;

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public static class HeaderBean {
            /**
             * api_version : 1.0
             * message_type : MSG_ALARM_DATA_DEL_RSP
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
    }
}
