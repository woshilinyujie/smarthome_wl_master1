package com.fbee.smarthome_wl.request;

/**
 * 九彩注册请求参数
 */
public class RegisterReq {
    public String name;//	string	必填	用户名
    public String phone;//	string	必填	手机号
    public String password;//	string	必填	用户密码

    public String id;

    public RegisterReq(String phone, String password, String name, String id) {
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
