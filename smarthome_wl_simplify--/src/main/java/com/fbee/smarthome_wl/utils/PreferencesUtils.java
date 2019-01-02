package com.fbee.smarthome_wl.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.fbee.smarthome_wl.base.BaseApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 保存配置信息
 */
public class PreferencesUtils {

    public static final String SP_SETTING = BaseApplication.getInstance().getContext().getPackageName();
    //飞比账号，用户名
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";

    //自动登录记录
    public static final String ISAUTOLOGIN = "isautologin";
    //本地账号，密码
    public static final String LOCAL_USERNAME = "local_name";
    public static final String LOCAL_PSW = "local_psw";
    public static final String LOCAL_ALAIRS = "user_alairs";
    //缓存当前网关
    public static final String GATEWAY = "fbee_gateway";
    //猫眼视频极光推送
    public static final String JPUSH_CALL = "jpush_call";
    //猫眼警报极光推送
    public static final String JPUSH_ALARM = "jpush_alarm";
    //门锁警报极光推送
    public static final String JPUSH_LOCK = "jpush_lock";
    //申请token的秘钥
    public static final String SECRET_KEY = "secret_key";

    //cookie
    public static final String COOKIE = "secret_key";

    //头像地址
    public static final String HEAD_ICON = "head_icon";

    //app 模式（简化-家居）
    public static final String MODEL ="model";
    /**
     * 读取对象
     *
     * @param key 要读取对象的key
     * @return
     */
    public static Object getObject(String key) {
        SharedPreferences sharedPreferences = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, Context.MODE_PRIVATE);
        String userValue = sharedPreferences.getString(key, "");
        if (userValue.equals(""))
            return null;
        byte[] userByte = Base64.decode(userValue.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(userByte);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存对象
     *
     * @param key    要保存对象的key
     * @param object 要保存的对象
     */
    public static void saveObject(String key, Object object) {
        SharedPreferences sharedPreferences = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, Context.MODE_PRIVATE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String objValue = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            Editor editor = sharedPreferences.edit();
            editor.putString(key, objValue);
            editor.commit();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            object = null;
        }
    }

    /**
     * 保存字符串
     *
     * @param key    对应KEY
     * @param values 对应值
     */
    public static void saveString(String key, String values) {
        SharedPreferences sp = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, 0);
        sp.edit().putString(key, values).commit();
    }

    /**
     * 获取字符串的值
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        SharedPreferences sp = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, 0);
        return sp.getString(key, null);
    }

    /**
     * 保存int数据
     *
     * @param key
     * @param values
     */
    public static void saveInt(String key, int values) {
        SharedPreferences sp = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, 0);
        sp.edit().putInt(key, values).commit();
    }

    /**
     * 获取int类型数据
     *
     * @param key
     * @return
     */
    public static int getInt(String key) {
        SharedPreferences sp = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, 0);
        return sp.getInt(key, 0);
    }

    /**
     * 保存boolean值
     *
     * @param key
     * @param values
     */
    public static void saveBoolean(String key, boolean values) {
        SharedPreferences sp = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, 0);
        sp.edit().putBoolean(key, values).commit();
    }

    /**
     * 获取boolean值
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(String key) {
        SharedPreferences sp = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, 0);
        return sp.getBoolean(key, false);
    }

    /**
     * 保存 long类型数据
     *
     * @param key
     * @param value
     */
    public static void saveLong(String key, long value) {
        SharedPreferences sp = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, 0);
        sp.edit().putLong(key, value).commit();
    }


    /**
     * 获取long 值
     *
     * @param key
     * @return
     */
    public static long getLong(String key) {
        SharedPreferences sp = BaseApplication.getInstance().getContext().getSharedPreferences(SP_SETTING, 0);
        return sp.getLong(key, 0);
    }


}
