package com.fbee.smarthome_wl.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import com.fbee.smarthome_wl.BuildConfig;
import com.fbee.smarthome_wl.utils.DateUtil;
import com.fbee.smarthome_wl.utils.FileUtils;
import com.fbee.smarthome_wl.utils.LogUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by ZhaoLi.Wang on 2016/9/23.
 * 全局异常捕捉
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{
    private Context mContext;
    public boolean openUpload = true;
    private static CrashHandler sInstance = null;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private CrashHandler(Context cxt) {
        this.mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(this);

        this.mContext = cxt.getApplicationContext();
    }

    public static synchronized CrashHandler create(Context cxt) {
        if (sInstance == null) {
            sInstance = new CrashHandler(cxt);
        }
        return sInstance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            if (!handleException(throwable) && mDefaultCrashHandler != null) {
                //如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultCrashHandler.uncaughtException(thread, throwable);
            }else{
                //退出
                ActivityPageManager.exit(mContext);
                //应用自动重启
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);
         //       System.gc();

            }

        }catch(Exception e){
        }

    }


    private void saveToSDCard(Throwable ex) throws Exception {
        File file = FileUtils.getSaveFile(this.mContext.getPackageName()+ File.separator + "log", DateUtil.getDataTime("yyyy-MM-dd-HH-mm-ss") + ".log");
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        pw.println(DateUtil.getDataTime("yyyy-MM-dd-HH-mm-ss"));
        dumpPhoneInfo(pw);
        pw.println();
        ex.printStackTrace(pw);
        pw.flush();
        pw.close();
    }
    private void dumpPhoneInfo(PrintWriter pw)
            throws PackageManager.NameNotFoundException {
        PackageManager pm = this.mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(this.mContext.getPackageName(), 0);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);
        pw.println();

        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        pw.println();

        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        pw.println();

        pw.print("Model: ");
        pw.println(Build.MODEL);
        pw.println();

        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
        pw.println();
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) throws Exception {
        if (ex == null) {
            return false;
        }
        LogUtil.e(this.mContext.getPackageName(),ex.getMessage());
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出错了即将退出", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        //保存到本地sd卡文件中
        if(BuildConfig.DEBUG)
        saveToSDCard(ex);
        //发送给友盟统计，或者压缩日志发送邮件...

        return true;
    }



}
