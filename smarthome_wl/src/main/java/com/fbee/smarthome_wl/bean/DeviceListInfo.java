package com.fbee.smarthome_wl.bean;

import java.util.List;

/**
 * @class name：com.fbee.model
 * @anthor create by Zhaoli.Wang
 * @time 2016/12/13 12:01
 */
public class DeviceListInfo {
    /**
     * userNo : 20151
     * total : 3
     * item : [{"saddr":"21153","sep":"8","cid":"","pushoption":"1","appsn":"3","apptag":"0","applan":"0","uid":545441},{"saddr":"39023","sep":"8","cid":"","pushoption":"1","appsn":"3","apptag":"0","applan":"0","uid":563311},{"saddr":"21289","sep":"8","cid":"","pushoption":"1","appsn":"3","apptag":"0","applan":"0","uid":545577}]
     */
    private String userNo;
    private int total;
    private List<ItemBean> item;

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ItemBean> getItem() {
        return item;
    }

    public void setItem(List<ItemBean> item) {
        this.item = item;
    }

    public static class ItemBean {
        /**
         * saddr : 21153
         * sep : 8
         * cid :
         * pushoption : 1
         * appsn : 3
         * apptag : 0
         * applan : 0
         * uid : 545441
         */

        private String saddr;
        private String sep;
        private String cid;
        private String pushoption;
        private String appsn;
        private String apptag;
        private String applan;
        private int uid;

        public String getSaddr() {
            return saddr;
        }

        public void setSaddr(String saddr) {
            this.saddr = saddr;
        }

        public String getSep() {
            return sep;
        }

        public void setSep(String sep) {
            this.sep = sep;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getPushoption() {
            return pushoption;
        }

        public void setPushoption(String pushoption) {
            this.pushoption = pushoption;
        }

        public String getAppsn() {
            return appsn;
        }

        public void setAppsn(String appsn) {
            this.appsn = appsn;
        }

        public String getApptag() {
            return apptag;
        }

        public void setApptag(String apptag) {
            this.apptag = apptag;
        }

        public String getApplan() {
            return applan;
        }

        public void setApplan(String applan) {
            this.applan = applan;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }
    }
}
