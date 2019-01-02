package com.fbee.smarthome_wl.utils;

import android.text.TextUtils;
import android.util.Log;

import com.fbee.smarthome_wl.BuildConfig;


/**
 * Created by ZhaoLi.Wang on 2016/9/22.
 * 日志管理类
 */
public class LogUtil {
    /**是否开启日志*/
    public static final boolean DE_BUG = BuildConfig.LOG_DEBUG;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final int JSON_INDENT = 4;
    private static final int STACK_TRACE_INDEX = 5;
    private static final String SUFFIX = ".java";
    private static final String TAG_DEFAULT = "LogUtil";
    public static final String NULL_TIPS = "Log with null object";
    private static String mGlobalTag;
    public static void  init(String tag){
        mGlobalTag = tag;
    }

    public static void e(String tag, String text) {
        if (DE_BUG) {
            Log.e(tag, text);
        }
    }

    public static void d(String tag, String text) {
        if (DE_BUG) {
            Log.d(tag, text);
        }
    }

    public static void i(String tag, String text) {
        if (DE_BUG) {
            Log.i(tag, text);
        }
    }

    public static void w(String tag, String text) {
        if (DE_BUG) {
            Log.w(tag, text);
        }
    }

    public static void v(String tag, String text) {
        if (DE_BUG) {
            Log.v(tag, text);
        }
    }


    public static void json(String mytag ,String text){
        if(DE_BUG){
            String[] contents = wrapperContent(mytag, text);
            String tag = contents[0];
            String msg = contents[1];
            String headString = contents[2];

            JsonLog.printJson(tag, msg, headString);

        }
    }



    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }


    private static String[] wrapperContent(String tagStr, Object... objects) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = stackTrace[STACK_TRACE_INDEX];
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1] + SUFFIX;
        }

        if (className.contains("$")) {
            className = className.split("\\$")[0] + SUFFIX;
        }

        String methodName = targetElement.getMethodName();
        int lineNumber = targetElement.getLineNumber();

        if (lineNumber < 0) {
            lineNumber = 0;
        }
        String methodNameShort = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        String tag = (tagStr == null ? className : tagStr);

        if ( TextUtils.isEmpty(tag)) {
            tag = TAG_DEFAULT;
        } else{
            tag = mGlobalTag;
        }

        String msg = (objects == null) ? NULL_TIPS : getObjectsString(objects);
        String headString = "[ (" + className + ":" + lineNumber + ")#" + methodNameShort + " ] ";

        return new String[]{tag, msg, headString};
    }
    private static String getObjectsString(Object... objects) {

        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if (object == null) {
                    stringBuilder.append("param").append("[").append(i).append("]").append(" = ").append("null").append("\n");
                } else {
                    stringBuilder.append("param").append("[").append(i).append("]").append(" = ").append(object.toString()).append("\n");
                }
            }
            return stringBuilder.toString();
        } else {
            Object object = objects[0];
            return object == null ? "null" : object.toString();
        }
    }



}
