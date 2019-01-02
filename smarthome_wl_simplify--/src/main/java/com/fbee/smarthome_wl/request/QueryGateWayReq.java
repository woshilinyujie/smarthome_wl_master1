package com.fbee.smarthome_wl.request;

/**
 * 查询网关信息
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/11 9:06
 */
public class QueryGateWayReq {

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
