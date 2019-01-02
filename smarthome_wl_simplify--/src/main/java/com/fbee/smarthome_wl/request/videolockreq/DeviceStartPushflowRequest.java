package com.fbee.smarthome_wl.request.videolockreq;


public class DeviceStartPushflowRequest extends MnsBaseRequest {

    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * username : 18888888888
     * data : {"device_rtsp_url":"rtsp://123.123.123.123:10700/(uuid+timestamp).sdp"}
     */

    private String vendor_name;
    private String uuid;
    private String username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * device_rtsp_url : rtsp://123.123.123.123:10700/(uuid+timestamp).sdp
         */

        private String device_rtsp_url;

        public String getDevice_rtsp_url() {
            return device_rtsp_url;
        }

        public void setDevice_rtsp_url(String device_rtsp_url) {
            this.device_rtsp_url = device_rtsp_url;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "device_rtsp_url='" + device_rtsp_url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DeviceStartPushflowRequest{" +
                "vendor_name='" + vendor_name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
