package com.fbee.smarthome_wl.request.videolockreq;


public class UserTransportRequest extends MnsBaseRequest {


    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * cmd : REMOTE_UNLOCK
     * data : 具体透传的内容
     */

    private String vendor_name;
    private String uuid;
    private String cmd;
    private String data;

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

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
