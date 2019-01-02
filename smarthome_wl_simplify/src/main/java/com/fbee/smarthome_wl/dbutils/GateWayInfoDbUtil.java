package com.fbee.smarthome_wl.dbutils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.greendao.DaoMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取数据库上次登录的账号
 * @class name：com.fbee.smarthome_wl.dbutils
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/21 17:26
 */
public class GateWayInfoDbUtil {

    private static GateWayInfoDbUtil dbutil;

    private GateWayInfoDbUtil(){};

    public static GateWayInfoDbUtil getIns(){
        if(dbutil == null) {
            synchronized (UserDbUtil.class) {
                if (dbutil == null) {
                    dbutil = new GateWayInfoDbUtil();
                }
            }
        }
        return dbutil;
    }

    public List<GateWayInfo> getAllGateWayInfo() {

        SQLiteDatabase sqLiteDatabase = BaseApplication.getInstance().getSQLiteDatabase();
        if (sqLiteDatabase == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(BaseApplication.getInstance(),"smarthome.db", null);
            sqLiteDatabase = helper.getWritableDatabase();
        }

        ArrayList<GateWayInfo> gateWayInfos = new ArrayList<GateWayInfo>();

        try {
            sqLiteDatabase.beginTransaction();
            Cursor e = sqLiteDatabase.query("gatewayinfo",
                    (String[]) null, (String) null, (String[]) null,
                    (String) null, (String) null, "data", (String) null);
            if (e.moveToFirst()) {
                do {
                    gateWayInfos.add(new GateWayInfo(e.getString(e
                            .getColumnIndex("username")), e.getString(e
                            .getColumnIndex("passwd")), e.getString(e
                            .getColumnIndex("gateway_ip")), e.getString(e
                            .getColumnIndex("snid")), e.getString(e
                            .getColumnIndex("remark"))));
                } while (e.moveToNext());
            }

            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception var6) {
        } finally {
            sqLiteDatabase.endTransaction();
            if (sqLiteDatabase.isOpen()) {
                sqLiteDatabase.close();
            }
        }

        return gateWayInfos;
    }

}
