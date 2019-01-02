package com.fbee.smarthome_wl.request.videolockreq;


public class DeviceStopPushflowRequest extends MnsBaseRequest {

    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * username : 18888888888
     */

    private String vendor_name;
    private String uuid;
    private String username;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "DeviceStopPushflowRequest{" +
                "vendor_name='" + vendor_name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
