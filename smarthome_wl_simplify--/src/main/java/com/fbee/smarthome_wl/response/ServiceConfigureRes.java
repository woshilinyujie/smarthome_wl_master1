package com.fbee.smarthome_wl.response;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/12/4.
 */

public class ServiceConfigureRes extends BaseResponse implements Serializable {

    /**
     * body : {"server_ip":"xxx.xxx.xxx.xxx","server_port":"10600","server_area":"Asia:China:HangZhou","vendor_name":"general","run_seconds":"78979","status":"33344","active_index":"ActiveIndex","ret_ok":"1123534","ret_error":"2342"}
     */

    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        /**
         * server_ip : xxx.xxx.xxx.xxx
         * server_port : 10600
         * server_area : Asia:China:HangZhou
         * vendor_name : general
         * run_seconds : 78979
         * status : 33344
         * active_index : ActiveIndex
         * ret_ok : 1123534
         * ret_error : 2342
         */

        private String server_ip;
        private String server_port;
        private String server_area;
        private String vendor_name;
        private String run_seconds;
        private String status;
        private String active_index;
        private String ret_ok;
        private String ret_error;

        public String getServer_ip() {
            return server_ip;
        }

        public void setServer_ip(String server_ip) {
            this.server_ip = server_ip;
        }

        public String getServer_port() {
            return server_port;
        }

        public void setServer_port(String server_port) {
            this.server_port = server_port;
        }

        public String getServer_area() {
            return server_area;
        }

        public void setServer_area(String server_area) {
            this.server_area = server_area;
        }

        public String getVendor_name() {
            return vendor_name;
        }

        public void setVendor_name(String vendor_name) {
            this.vendor_name = vendor_name;
        }

        public String getRun_seconds() {
            return run_seconds;
        }

        public void setRun_seconds(String run_seconds) {
            this.run_seconds = run_seconds;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getActive_index() {
            return active_index;
        }

        public void setActive_index(String active_index) {
            this.active_index = active_index;
        }

        public String getRet_ok() {
            return ret_ok;
        }

        public void setRet_ok(String ret_ok) {
            this.ret_ok = ret_ok;
        }

        public String getRet_error() {
            return ret_error;
        }

        public void setRet_error(String ret_error) {
            this.ret_error = ret_error;
        }
    }
}
