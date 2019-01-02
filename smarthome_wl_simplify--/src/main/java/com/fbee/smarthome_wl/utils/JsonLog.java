package com.fbee.smarthome_wl.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ZhaoLi.Wang on 2016/9/23.
 */
public class JsonLog {

    public static void printJson(String tag, String msg, String headString) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(LogUtil.JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(LogUtil.JSON_INDENT);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        LogUtil.printLine(tag, true);
        message = headString + LogUtil.LINE_SEPARATOR + message;
        String[] lines = message.split(LogUtil.LINE_SEPARATOR);
    }

}
