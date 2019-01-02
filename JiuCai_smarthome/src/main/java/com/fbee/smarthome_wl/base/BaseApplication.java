package com.fbee.smarthome_wl.base;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.fbee.JiuCai_smarthome.greendao.DaoMaster;
import com.fbee.JiuCai_smarthome.greendao.DaoSession;
import com.fbee.JiuCai_smarthome.greendao.IconDao;
import com.fbee.JiuCai_smarthome.greendao.UserDao;
import com.fbee.smarthome_wl.BuildConfig;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.greendao.DoorlockrecordDao;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by ZhaoLi.Wang on 2016/9/22.
 */
public class BaseApplication extends Application {
    private Context context;
    private static BaseApplication instance;
    private static DaoSession mDaoSession;
    private SQLiteDatabase db;
    public static BaseApplication getInstance() {
        if(null == instance)
            throw new NullPointerException();
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(getApplicationContext());
        instance = this;

        //初始化极光
        JPushInterface.setDebugMode(BuildConfig.DEBUG); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        initSDK();
        init();
        // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        setupDatabase();
    }

    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"smarthome.db", null);
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
        MultiDex.install(this) ;
    }

    public  void setContext(Context context) {
        this.context = context;
    }

    public SQLiteDatabase getSQLiteDatabase(){
       return db;
    }

    public  Context getContext() {
        return context;
    }

    private void init() {
        AppContext.init(this);
        //全局异常捕获
//        CrashHandler.create(this);
    }

    /**
     * 用户表
     * @return
     */
    public static UserDao getUserDao(){
        return mDaoSession.getUserDao();
    }

    /**
     * 门锁记录表
     * @return
     */
    public static DoorlockrecordDao getDoorlockrecordDao(){
        return mDaoSession.getDoorlockrecordDao();
    }

    public static IconDao getIconDao(){
        return mDaoSession.getIconDao();
    }

    //==============anychat=============
    public static AnyChatCoreSDK anyChatSDK;

    /**
     * anychat init
     */
    private void initSDK(){
        try
        {
            if (anyChatSDK==null) {
                anyChatSDK= AnyChatCoreSDK.getInstance();
                anyChatSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
