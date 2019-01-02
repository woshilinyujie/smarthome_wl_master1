package com.fbee.smarthome_wl.request;

/**
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/5/2 16:31
 */
public class PusBodyBean {

    /**
     * token : xxxxxxxxxxxxxxx
     * product_name : wonly
     * platform : android
     * endpoint_type : H60-L11
     */

    private String token;
    private String product_name;
    private String platform;
    private String endpoint_type;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getEndpoint_type() {
        return endpoint_type;
    }

    public void setEndpoint_type(String endpoint_type) {
        this.endpoint_type = endpoint_type;
    }

}
