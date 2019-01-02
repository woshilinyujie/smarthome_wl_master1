package com.fbee.smarthome_wl.utils;

import android.content.Context;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseApplication;

/**
 * Created by WLPC on 2017/4/1.
 */

public class MessageUtil {
    private Context mContext;
    public MessageUtil() {
        mContext= BaseApplication.getInstance().getContext();
    }

    /**显示不同的信息*/
    public String getTamperString(byte alarmCode){
        String str="";
        switch (alarmCode) {
            case 0:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_4);
                break;
            case 4:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_0);
                break;
            case 5:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_1);
                break;
            case 6:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_2);
                break;
            case 7:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_3);
                break;
            case 51:
                str=mContext.getString(R.string.str_device_door_lock_alarm_type_5);
                break;
            case (byte) 0x85:
                str="门锁已关闭";
                break;
            case (byte) 0x87:
                str="门锁已关闭";
                break;
            default:
                break;
        }
        return str;
    }
}
