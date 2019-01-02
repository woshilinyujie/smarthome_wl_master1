package com.fbee.smarthome_wl.response.videolockres;

/**
 * Created by WLPC on 2017/9/27.
 */

public class DeviceTransportResponse {

    /**
     * api_version : 1.0
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * data : 具体透传的内容
     */

    private String api_version;
    private String vendor_name;
    private String uuid;
    private String data;

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
