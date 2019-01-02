package com.fbee.smarthome_wl.request;

/**
 * 查询用户设备列表
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/19 11:21
 */
public class QueryDeviceuserlistReq{


    /**
     * vendor_name : feibee
     * uuid : xxxxxxxxxx
     * short_id : 123123
     */

    private String vendor_name;
    private String uuid;
    private String short_id;

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

    public String getShort_id() {
        return short_id;
    }

    public void setShort_id(String short_id) {
        this.short_id = short_id;
    }
}
