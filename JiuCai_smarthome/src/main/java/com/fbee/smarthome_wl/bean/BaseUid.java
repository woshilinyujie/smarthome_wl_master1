package com.fbee.smarthome_wl.bean;

import com.fbee.smarthome_wl.utils.PreferencesUtils;

/**
 * Created by chenwenlu on 2016/2/26.
 * 需要这个参数的选择继承
 */
public class BaseUid {
   public String uid;//	string	必填	用户ID
   //public String HTTP_TOKEN;//	string	必填	TOKEN

    public BaseUid(/*String uid, String token*/) {
        this.uid = String.valueOf(PreferencesUtils.getInt("ANYCHAT_USERID"));
    }
}
