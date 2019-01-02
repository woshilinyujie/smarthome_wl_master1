package com.fbee.smarthome_wl.request;

/**
 * Created by WLPC on 2017/10/10.
 */

public class AMSBean<T> {

    /**
     * header : {"api_version":"1.0","message_type":"MSG_TOKEN_ADD_REQ","seq_id":"1"}
     * body : {"username":"18888888888","secret_key":"xxxxxxxxx","attitude":"write"}
     */

    private HeaderBean header;
    private T body;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public static class HeaderBean {
        /**
         * api_version : 1.0
         * message_type : MSG_TOKEN_ADD_REQ
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
}
