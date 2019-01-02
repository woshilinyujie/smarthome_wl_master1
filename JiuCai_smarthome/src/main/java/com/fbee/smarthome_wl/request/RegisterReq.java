package com.fbee.smarthome_wl.request;

/**
 * 九彩注册请求参数
 */
public class RegisterReq {
    public String name;//	string	必填	用户名
    public String mobile;//	string	必填	手机号
    public String password;//	string	必填	用户密码
    public String city;
    public String device;
    public String email;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
