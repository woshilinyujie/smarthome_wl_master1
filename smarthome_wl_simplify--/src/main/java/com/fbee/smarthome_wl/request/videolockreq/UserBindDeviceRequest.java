package com.fbee.smarthome_wl.request.videolockreq;


public class UserBindDeviceRequest extends MnsBaseRequest {
    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * username : 18888888888
     * timestamp : 1499681215
     * random_key : xxxxxxxxx
     * network_ssid : xxxxxxxxx
     * version : xxxx
     * mac_addr : xxxxxxxxx
     */

    private String vendor_name;
    private String uuid;
    private String username;
    private String timestamp;
    private String random_key;
    private String network_ssid;
    private String version;
    private String mac_addr;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRandom_key() {
        return random_key;
    }

    public void setRandom_key(String random_key) {
        this.random_key = random_key;
    }

    public String getNetwork_ssid() {
        return network_ssid;
    }

    public void setNetwork_ssid(String network_ssid) {
        this.network_ssid = network_ssid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMac_addr() {
        return mac_addr;
    }

    public void setMac_addr(String mac_addr) {
        this.mac_addr = mac_addr;
    }

    @Override
    public String toString() {
        return "UserBindDeviceRequest{" +
                "vendor_name='" + vendor_name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", random_key='" + random_key + '\'' +
                ", network_ssid='" + network_ssid + '\'' +
                ", version='" + version + '\'' +
                ", mac_addr='" + mac_addr + '\'' +
                '}';
    }
}
