package com.fbee.smarthome_wl.request.videolockreq;

import java.util.List;

public class UserDeviceStatusQueryRequest extends MnsBaseRequest {

    private List<DeviceListBean> device_list;

    public List<DeviceListBean> getDevice_list() {
        return device_list;
    }

    public void setDevice_list(List<DeviceListBean> device_list) {
        this.device_list = device_list;
    }

    public static class DeviceListBean {
        /**
         * vendor_name : general
         * uuid : xxxxxxxxxxxxx
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
