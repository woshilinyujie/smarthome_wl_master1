package com.fbee.smarthome_wl.request;

/**
 * 删除设备请求体
 * Created by WLPC on 2017/4/18.
 */

public class DeleteDevicesReq {


    /**
     * gateway_vendor_name : feibee
     * gateway_uuid : xxxxxxxxxx
     * device : {"vendor_name":"feibee","uuid":"xxxxxxxxxx"}
     */

    private String gateway_vendor_name;
    private String gateway_uuid;
    private DeviceBean device;

    public String getGateway_vendor_name() {
        return gateway_vendor_name;
    }

    public void setGateway_vendor_name(String gateway_vendor_name) {
        this.gateway_vendor_name = gateway_vendor_name;
    }

    public String getGateway_uuid() {
        return gateway_uuid;
    }

    public void setGateway_uuid(String gateway_uuid) {
        this.gateway_uuid = gateway_uuid;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public static class DeviceBean {
        /**
         * vendor_name : feibee
         * uuid : xxxxxxxxxx
         */

        private String vendor_name;
        private String uuid;

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
    }
}
