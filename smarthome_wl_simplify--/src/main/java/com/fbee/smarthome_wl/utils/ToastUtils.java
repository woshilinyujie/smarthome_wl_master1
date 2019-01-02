package com.fbee.smarthome_wl.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.fbee.smarthome_wl.base.BaseApplication;


/**
 * Created by ZhaoLi.Wang on 2016/9/23.
 * toast工具类
 */
public class ToastUtils {

    public static final boolean isShow = true;

    /**
     * 短时间显示Toast
     * @param message
     */
    public static void showShort( CharSequence message)
    {
        if (TextUtils.isEmpty(message)) {
            message = "";
            return;
        }
        if (isShow)
            Toast.makeText(BaseApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
    }



}
