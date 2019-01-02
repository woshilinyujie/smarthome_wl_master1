package com.fbee.smarthome_wl.request;

/**
 * Created by wl on 2017/4/18.
 */

/***
 * \
 * 查询设备列表
 */
public class QueryDevicesListInfo {

    /**
     * gateway_vendor_name : feibee
     * gateway_uuid : xxxxxxxxxx
     */

    private String vendor_name;
    private String uuid;

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public String getUuid() {
        return uuid;
    }

}
