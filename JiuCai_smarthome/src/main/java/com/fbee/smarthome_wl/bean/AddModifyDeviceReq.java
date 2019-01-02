package com.fbee.smarthome_wl.bean;

/**
 * Created by chenwenlu on 2016/2/29.
 */
public class AddModifyDeviceReq extends BaseUid{
    public String alias;	//string	必填	设备备注名
    public String device;	//string	必填	设备号
    public String type;	//string	必填	1:家   2:公司

    public String id;//modify独有

}
