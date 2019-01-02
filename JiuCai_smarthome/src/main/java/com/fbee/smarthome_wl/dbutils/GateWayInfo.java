//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.fbee.smarthome_wl.dbutils;

/**
 * 网关信息实体类
 */
public class GateWayInfo {
    private String userName;
    private String passwd;
    private String gateway_ip;
    private String gateway_snid;
    private String gateway_remark;

    public GateWayInfo(String userName, String passwd) {
        this.userName = userName;
        this.passwd = passwd;
    }

    public GateWayInfo(String userName, String passwd, String gateway_ip, String gateway_snid, String gateway_remark) {
        this.userName = userName;
        this.passwd = passwd;
        this.gateway_ip = gateway_ip;
        this.gateway_snid = gateway_snid;
        this.gateway_remark = gateway_remark;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return this.passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getGateway_ip() {
        return this.gateway_ip;
    }

    public void setGateway_ip(String gateway_ip) {
        this.gateway_ip = gateway_ip;
    }

    public String getGateway_snid() {
        return this.gateway_snid;
    }

    public void setGateway_snid(String gateway_snid) {
        this.gateway_snid = gateway_snid;
    }

    public String getGateway_remark() {
        return this.gateway_remark;
    }

    public void setGateway_remark(String gateway_remark) {
        this.gateway_remark = gateway_remark;
    }
}
