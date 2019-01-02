package com.fbee.smarthome_wl.base;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.fbee.smarthome_wl.BuildConfig;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.greendao.DaoMaster;
import com.fbee.smarthome_wl.greendao.DaoSession;
import com.fbee.smarthome_wl.greendao.DoorlockrecordDao;
import com.fbee.smarthome_wl.greendao.IconDao;
import com.fbee.smarthome_wl.greendao.UserDao;
import com.fbee.smarthome_wl.utils.LogUtil;

import java.net.URISyntaxException;

import de.greenrobot.dao.query.QueryBuilder;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by ZhaoLi.Wang on 2016/9/22.
 */
public class BaseApplication extends Application {
    private Context context;
    private static BaseApplication instance;
    private static DaoSession mDaoSession;
    private SQLiteDatabase db;
    private Socket mSocket;
    private CloudPushService pushService;
    private static final String CHAT_SERVER_URL = "https://mns.wonlycloud.com:10500";

    public static BaseApplication getInstance() {
        if (null == instance)
            throw new NullPointerException();
        return instance;
    }

    public Socket getSocket() {
        if (mSocket == null) {
            try {
//                IO.Options options=new IO.Options();
//                options.timeout=5000;
//                options.reconnection=true;
//                options.reconnectionDelay=500;
//                options.reconnectionAttempts=2;
//                mSocket = IO.socket(CHAT_SERVER_URL,options);
                mSocket = IO.socket(CHAT_SERVER_URL);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return mSocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(getApplicationContext());
        pushService = Api.getCloudPushService();
        instance = this;

        //初始化极光
//        JPushInterface.setDebugMode(BuildConfig.DEBUG);    // 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);            // 初始化 JPush
        initCloudChannel(this);
        init();
        // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        setupDatabase();
    }

    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e("MyMessageReceiver+register", response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                LogUtil.e("MyMessageReceiver", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        MiPushRegister.register(applicationContext, "2882303761517514040", "5131751434040"); // 初始化小米辅助推送
//        HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
//        GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
    }

    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "smarthome.db", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
        // 在 QueryBuilder 类中内置两个 Flag 用于方便输出执行的 SQL 语句与传递参数的值
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return db;
    }

    public Context getContext() {
        return context;
    }

    private void init() {
        AppContext.init(this);
        //全局异常捕获
//        CrashHandler.create(this);
    }

    /**
     * 用户表
     *
     * @return
     */
    public static UserDao getUserDao() {
        return mDaoSession.getUserDao();
    }

    /**
     * 门锁记录表
     *
     * @return
     */
    public static DoorlockrecordDao getDoorlockrecordDao() {
        return mDaoSession.getDoorlockrecordDao();
    }

    public static IconDao getIconDao() {
        return mDaoSession.getIconDao();
    }


}
