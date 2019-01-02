package com.fbee.smarthome_wl.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.fbee.smarthome_wl.R;

import java.lang.reflect.Field;

public class BadgeUtil {



    private static final String TAG = "BadgeUtil";


    public static void setBadgeCount(Context context, int count,String platform) {
        Intent badgeIntent = null;
        if(platform.equals("samsung")){
            badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            badgeIntent.putExtra("badge_count", count);
            badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
            badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
            context.sendBroadcast(badgeIntent);
        }else if(platform.equals("Xiaomi")){
            sendToXiaoMi(context,count,R.mipmap.ic_logol);
        }else if(platform.equals("sony")){
            badgeIntent = new Intent();
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
            badgeIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", getLauncherClassName(context));
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", count);
            badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
            context.sendBroadcast(badgeIntent);
        }
        else if(platform.equals("htc")){
            badgeIntent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
            badgeIntent.putExtra("packagename", getLauncherClassName(context));
            badgeIntent.putExtra("count", count);
            context.sendBroadcast(badgeIntent);
        }

        else if(platform.equals("lg")){
            badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
            badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
            badgeIntent.putExtra("badge_count", count);
        }
        else if(platform.equals("adw")){
            badgeIntent = new Intent("org.adw.launcher.counter.SEND");
            badgeIntent.putExtra("PNAME", context.getPackageName());
            badgeIntent.putExtra("CNAME",  getLauncherClassName(context));
            badgeIntent.putExtra("COUNT", count);
            context.sendBroadcast(badgeIntent);

        }
        else if(platform.equals("apex")){
            badgeIntent = new Intent("com.anddoes.launcher.COUNTER_CHANGED");
            badgeIntent.putExtra("package", context.getPackageName());
            badgeIntent.putExtra("count",  count);
            badgeIntent.putExtra("class", getLauncherClassName(context));
            context.sendBroadcast(badgeIntent);

        }
        else if(platform.equals("asus")){
            badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
            badgeIntent.putExtra("badge_count",  count);
            badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
            context.sendBroadcast(badgeIntent);

        }
        else if(platform.equals("huawei")){
            try {
                //com.huawei.android.launcher.permission.WRITE_SETTINGS
                //华为手机设置这个权限，依旧会报错
                String launcherClassName = getLauncherClassName(context);
                if (launcherClassName == null) {
                    return;
                }
                Bundle localBundle = new Bundle();
                localBundle.putString("package", context.getPackageName());
                localBundle.putString("class",  launcherClassName);
                localBundle.putInt("badgenumber", count);
                context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        else if(platform.equals("nova")){
            ContentValues contentValues = new ContentValues();
            contentValues.put("tag", context.getPackageName()+ "/" + getLauncherClassName(context));
            contentValues.put("count", count);
            context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);

        }
        else {
            badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
            badgeIntent.putExtra("badge_count",  count);
            badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
            context.sendBroadcast(badgeIntent);

        }
    }
  
    public static void resetBadgeCount(Context context,String platform) {
        setBadgeCount(context, 0,platform);
    }


   
    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();


        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);


        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);


        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        return info.activityInfo.name;
    }




    private static void sendToXiaoMi(Context context,int number,int iconResId) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("您有新消息未读");
            builder.setTicker("您有新消息未读");
            builder.setAutoCancel(true);
            builder.setSmallIcon(iconResId);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, number);// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);

        }catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra("android.intent.extra.update_application_component_name",context.getPackageName() + "/"+ getLauncherClassName(context) );
            localIntent.putExtra("android.intent.extra.update_application_message_text",number);
            context.sendBroadcast(localIntent);
        }
        finally
        {
            if(notification!=null && isMiUIV6 )
            {
                //if(!AppUtil.isRunningForeground(context)){
                //miui6以上版本需要使用通知发送
                nm.notify(101010, notification);
               //}
            }
        }

    }

}