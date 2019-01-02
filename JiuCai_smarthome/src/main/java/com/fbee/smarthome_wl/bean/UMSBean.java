package com.fbee.smarthome_wl.bean;

/**
 * @class name：com.fbee.smarthome_wl.bean
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/28 18:02
 */
public class UMSBean<E> {

    private HeaderBean header;
    private  E body;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public E getBody() {
        return body;
    }

    public void setBody(E body) {
        this.body = body;
    }

    public  static class HeaderBean {
        /**
         * api_version : 1.0
         * message_type : MSG_SMS_CODE_REQ
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
