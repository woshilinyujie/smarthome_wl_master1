package com.fbee.smarthome_wl.response;

import java.util.List;

/**
 * 视频锁开锁记录返回
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/11 14:54
 */
public class VideolockRecordResponse extends BaseResponse {


    private List<BodyBean> body;

    public List<BodyBean> getBody() {
        return body;
    }

    public void setBody(List<BodyBean> body) {
        this.body = body;
    }

    public static class BodyBean {
        /**
         * _id : 5420013272fe096c39901049
         * timestamp : 1499681215
         * device_user_id : 001
         * unlock_mode : pwd
         * auth_mode : dbl
         * stress_status : false
         * op_type : enter_menu
         */

        private String _id;
        private String timestamp;
        private String device_user_id;
        private String unlock_mode;
        private String auth_mode;
        private String stress_status;
        private String op_type;

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

        public String getUnlock_mode() {
            return unlock_mode;
        }

        public void setUnlock_mode(String unlock_mode) {
            this.unlock_mode = unlock_mode;
        }

        public String getAuth_mode() {
            return auth_mode;
        }

        public void setAuth_mode(String auth_mode) {
            this.auth_mode = auth_mode;
        }

        public String getStress_status() {
            return stress_status;
        }

        public void setStress_status(String stress_status) {
            this.stress_status = stress_status;
        }

        public String getOp_type() {
            return op_type;
        }

        public void setOp_type(String op_type) {
            this.op_type = op_type;
        }
    }
}
