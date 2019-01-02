package com.fbee.smarthome_wl.response.videolockres;

/**
 * @class name：com.fbee.smarthome_wl.response.videolockres
 * @anthor create by Zhaoli.Wang
 * @time 2018/2/28 8:55
 */
public class FileDownloadResponse {


    /**
     * timestamp : 1499681215
     * cmd : FILE_DOWNLOAD
     * part : wifi
     * file_url : http://download.wonlycloud.com/general/device/v1.bin
     * md5 : 202cb962ac59075b964b07152d234b70
     * be_operated_object : 001
     */

    private String timestamp;
    private String cmd;
    private String part;
    private String file_url;
    private String md5;
    private String be_operated_object;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getBe_operated_object() {
        return be_operated_object;
    }

    public void setBe_operated_object(String be_operated_object) {
        this.be_operated_object = be_operated_object;
    }
}
