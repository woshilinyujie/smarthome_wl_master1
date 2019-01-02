package com.fbee.smarthome_wl.response.videolockres;

import java.util.List;

public class UserDeviceStatusQueryResponse extends MnsBaseResponse {

    private List<StatusListBean> status_list;

    public List<StatusListBean> getStatus_list() {
        return status_list;
    }

    public void setStatus_list(List<StatusListBean> status_list) {
        this.status_list = status_list;
    }

    public static class StatusListBean {
        /**
         * vendor_name : general
         * uuid : xxxxxxxxxxxxx
         * status : online
         * power : 50
         */

        private String vendor_name;
        private String uuid;
        private String status;
        private String power;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPower() {
            return power;
        }

        public void setPower(String power) {
            this.power = power;
        }
    }
}
