package com.fbee.smarthome_wl.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wl on 2017/10/30.
 */

public class RecordResponse extends BaseResponse implements Serializable {


    /**
     * header : {"api_version":"1.0","message_type":"MSG_OPERATED_RECORD_QUERY_RSP","seq_id":"1","http_code":"200","return_string":"RETURN_SUCCESS_OK_STRING"}
     * body : [{"_id":"5420013272fe096c39901048","timestamp":"1499681215","device_user_id":"001,002","device_user_note":"阿三,阿九","be_operated":{"mode":"del","object":"001","object_level":"adm","object_note":"阿四","unlock_mode":"pwd,fp,card"}},{"_id":"5420013272fe096c39901049","timestamp":"1499681215","device_user_id":"002,003","device_user_note":"阿三,阿九","be_operated":{"mode":"add","object":"001","object_level":"adm","object_note":"阿四","unlock_mode":"pwd,fp,card"}}]
     */

    private List<BodyEntity> body;

    public List<BodyEntity> getBody() {
        return body;
    }

    public void setBody(List<BodyEntity> body) {
        this.body = body;
    }

    public static class BodyEntity implements Serializable{
        /**
         * _id : 5420013272fe096c39901048
         * timestamp : 1499681215
         * device_user_id : 001,002
         * device_user_note : 阿三,阿九
         * be_operated : {"mode":"del","object":"001","object_level":"adm","object_note":"阿四","unlock_mode":"pwd,fp,card"}
         */

        private String _id;
        private String timestamp;
        private String device_user_id;
        private String device_user_note;
        private BeOperatedEntity be_operated;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDevice_user_id() {
            return device_user_id;
        }

        public void setDevice_user_id(String device_user_id) {
            this.device_user_id = device_user_id;
        }

        public String getDevice_user_note() {
            return device_user_note;
        }

        public void setDevice_user_note(String device_user_note) {
            this.device_user_note = device_user_note;
        }

        public BeOperatedEntity getBe_operated() {
            return be_operated;
        }

        public void setBe_operated(BeOperatedEntity be_operated) {
            this.be_operated = be_operated;
        }

        public static class BeOperatedEntity implements Serializable{
            /**
             * mode : del
             * object : 001
             * object_level : adm
             * object_note : 阿四
             * unlock_mode : pwd,fp,card
             */

            private String mode;
            private String object;
            private String object_level;
            private String object_note;
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

            public String getObject_note() {
                return object_note;
            }

            public void setObject_note(String object_note) {
                this.object_note = object_note;
            }

            public String getUnlock_mode() {
                return unlock_mode;
            }

            public void setUnlock_mode(String unlock_mode) {
                this.unlock_mode = unlock_mode;
            }
        }
    }
}
