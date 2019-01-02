package com.fbee.smarthome_wl.request.videolockreq;


public class UserInitiateIntercomRequest extends MnsBaseRequest {

    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * data : {"action":"call"}
     */

    private String vendor_name;
    private String uuid;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * action : call
         */

        private String action;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
}
