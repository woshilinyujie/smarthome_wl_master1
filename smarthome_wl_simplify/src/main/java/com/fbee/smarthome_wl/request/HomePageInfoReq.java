package com.fbee.smarthome_wl.request;

/**
 * 获取首页配置
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/1 17:46
 */
public class HomePageInfoReq{


    /**
     * gateway_vendor_name : feibee
     * gateway_uuid : xxxxxxxxxx
     */

    private String gateway_vendor_name;
    private String gateway_uuid;

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
}
