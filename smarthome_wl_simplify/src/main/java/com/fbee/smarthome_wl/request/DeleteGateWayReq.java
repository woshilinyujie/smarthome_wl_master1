package com.fbee.smarthome_wl.request;

/**
 * 删除网关请求体
 * Created by WLPC on 2017/4/18.
 */

public class DeleteGateWayReq {

    /**
     * body : {"gateway_vendor_name":"feibee","gateway_uuid":"xxxxxxxxx"}
     */

    private String vendor_name;
    private String uuid;

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getUuuid() {
        return uuid;
    }

    public void setUuid(String gateway_uuid) {
        this.uuid = gateway_uuid;
    }

}
