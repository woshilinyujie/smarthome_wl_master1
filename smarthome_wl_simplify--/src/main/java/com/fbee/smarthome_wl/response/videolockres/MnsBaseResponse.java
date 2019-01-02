package com.fbee.smarthome_wl.response.videolockres;


public class MnsBaseResponse {
    /**
     * api_version : 1.0
     * return_string : RETURN_SUCCESS_OK_STRING
     * cmd : REMOTE_UNLOCK
     */

    private String api_version="1.0";
    private String return_string;
    private String cmd;

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

    public String getReturn_string() {
        return return_string;
    }

    public void setReturn_string(String return_string) {
        this.return_string = return_string;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

}
