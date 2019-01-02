package com.example.wl.WangLiPro_v1.api;

import android.app.Application;

import com.jwl.android.jwlandroidlib.bean.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wl on 2018/5/14.
 */

public class AppContext {
    public static final String company = "wl";
    public static String TOKEN;

    public static String getTOKEN() {
        return TOKEN;
    }

    public static void setTOKEN(String TOKEN) {
        AppContext.TOKEN = TOKEN;
    }

    public static String getUSERID() {
        return USERID;
    }

    public static void setUSERID(String USERID) {
        AppContext.USERID = USERID;
    }

    public static String USERID;

    public static List<String> getMapList() {
        return mapList;
    }

    public static void setMapList(List mapList) {
        AppContext.mapList = mapList;
    }

    public static List<String> mapList = new ArrayList();

    public static List<Device> deviceList = new ArrayList<>();

    public static void setDevices(List deviceList) {
        AppContext.deviceList = deviceList;
    }

    public static List<Device> getDeviceS() {
        return deviceList;
    }
}
