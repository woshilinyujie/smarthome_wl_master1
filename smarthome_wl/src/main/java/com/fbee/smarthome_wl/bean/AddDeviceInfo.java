package com.fbee.smarthome_wl.bean;

/**
 * Created by wl on 2017/3/27.
 */

public class AddDeviceInfo {

    /**
     * method : on_addbdy_req
     * bdyname : xxx
     * reqid : 123456
     * extra : {"oldbdy":"xxx1"}
     */

    private String method;
    private String bdyname;
    private String reqid;
    private ExtraEntity extra;

    public void setMethod(String method) {
        this.method = method;
    }

    public void setBdyname(String bdyname) {
        this.bdyname = bdyname;
    }

    public void setReqid(String reqid) {
        this.reqid = reqid;
    }

    public void setExtra(ExtraEntity extra) {
        this.extra = extra;
    }

    public String getMethod() {
        return method;
    }

    public String getBdyname() {
        return bdyname;
    }

    public String getReqid() {
        return reqid;
    }

    public ExtraEntity getExtra() {
        return extra;
    }

    public static class ExtraEntity {
        /**
         * oldbdy : xxx1
         */

        private String oldbdy;

        public void setOldbdy(String oldbdy) {
            this.oldbdy = oldbdy;
        }

        public String getOldbdy() {
            return oldbdy;
        }
    }
}
