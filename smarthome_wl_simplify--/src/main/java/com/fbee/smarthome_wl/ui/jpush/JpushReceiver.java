package com.fbee.smarthome_wl.ui.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.EquesAlarmActivity;
import com.fbee.smarthome_wl.ui.equesdevice.videocall.EquesVideoCallActivity;
import com.fbee.smarthome_wl.ui.login.LoginActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.ui.splash.SplashActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private JSONObject json;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//        	processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");

            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 用户点击打开了通知");

            //APP是否处于前台进程
           boolean isruning = AppUtil.isRunningForeground(context);
			if(isruning)
				return;
            int appSatus = AppUtil.getAppSatus(context, "com.fbee.smarthome_wl");
            switch (appSatus) {
                case 1:
                    break;
                case 2:
                    String str = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    try {
                        JSONObject json = new JSONObject(str);
                        String type = json.optString("vendor");
                        if(type.equals("feibit")){
                            Intent i = new Intent(context, SplashActivity.class);
                            i.putExtra("json", json.toString());
                            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            return;
                        }
                        String method = json.optString("method");
                        if (method.equals("1")) {
                            Intent i = new Intent(context, EquesAlarmActivity.class);
                            i.putExtra("json", json.toString());
                            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        } else if (method.equals("2")) {
                            Intent i = new Intent(context, EquesVideoCallActivity.class);
                            i.putExtra("json", json.toString());
                            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    String str1 = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    try {
                        JSONObject json = new JSONObject(str1);
                        String type = json.optString("vendor");
                        if(type.equals("feibit")){
                            Intent i = new Intent(context, SplashActivity.class);
                            i.putExtra("json", json.toString());
                            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            return;
                        }
                        String method = json.optString("method");
                        if (method.equals("1")) {
                            Intent i = new Intent(context, EquesAlarmActivity.class);
                            i.putExtra("json", json.toString());
                            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        } else if (method.equals("2")) {
                            Intent i = new Intent(context, EquesVideoCallActivity.class);
                            i.putExtra("json", json.toString());
                            i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }


        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:11" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:123" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.e(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:...." + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                }

            } else {
                sb.append("\nkey:789" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

}
