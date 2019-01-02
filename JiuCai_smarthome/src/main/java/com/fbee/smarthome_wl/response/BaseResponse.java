package com.fbee.smarthome_wl.response;

import java.io.Serializable;

/**
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/1 10:52
 */
public class BaseResponse implements Serializable{

    //为确保序列化与反序列化一致，UID必须不可改变
    private static final long serialVersionUID = 1L;
    /**
     * header : {"api_version":"1.0","message_type":"MSG_USER_CONFIG_QUERY_RSP","seq_id":"1","http_code":"200","return_string":"Success OK"}
     */

    private HeaderBean header;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public static class HeaderBean  implements Serializable{
        /**
         * api_version : 1.0
         * message_type : MSG_USER_CONFIG_QUERY_RSP
         * seq_id : 1
         * http_code : 200
         * return_string : Success OK
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
