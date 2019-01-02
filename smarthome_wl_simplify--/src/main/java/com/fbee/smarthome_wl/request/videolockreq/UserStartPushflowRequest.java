package com.fbee.smarthome_wl.request.videolockreq;


public class UserStartPushflowRequest extends MnsBaseRequest {

    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * data : {"app_rtsp_url":"rtsp://123.123.123.123:10700/(uuid+timestamp).sdp"}
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
         * app_rtsp_url : rtsp://123.123.123.123:10700/(uuid+timestamp).sdp
         */

        private String app_rtsp_url;

        public String getApp_rtsp_url() {
            return app_rtsp_url;
        }

        public void setApp_rtsp_url(String app_rtsp_url) {
            this.app_rtsp_url = app_rtsp_url;
        }
    }
}
