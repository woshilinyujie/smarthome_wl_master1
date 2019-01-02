package com.fbee.smarthome_wl.request;

/**
 * Created by WLPC on 2017/12/4.
 */

public class ServiceConfigureReq {


    /**
     * username : 18888888888
     * secret_key : xxxxxxxx
     * vendor_name : general
     * uuid : xxxxxxxxx
     * service_type : DAS
     */

    private String username;
    private String secret_key;
    private String vendor_name;
    private String uuid;
    private String service_type;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
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

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }
}
