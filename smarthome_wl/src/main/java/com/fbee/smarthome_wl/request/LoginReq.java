package com.fbee.smarthome_wl.request;

/**
 * 九彩登录请求实体类
 * Created by WLPC on 2017/1/6.
 */

public class LoginReq {
    public String account;	//string	必填	账号(手机号)
    public String password;	//string	必填	用户密码

    public LoginReq(String account,String password) {
        this.password = password;
        this.account = account;
    }
}
