package com.fbee.smarthome_wl.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.fbee.smarthome_wl.greendao.Doorlockrecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DOORLOCKRECORD.
*/
public class DoorlockrecordDao extends AbstractDao<Doorlockrecord, Long> {

    public static final String TABLENAME = "DOORLOCKRECORD";

    /**
     * Properties of entity Doorlockrecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public final static Property UserID = new Property(2, int.class, "userID", false, "USER_ID");
        public final static Property DeviceID = new Property(3, int.class, "deviceID", false, "DEVICE_ID");
        public final static Property Msg = new Property(4, String.class, "msg", false, "MSG");
        public final static Property MsgType = new Property(5, String.class, "msgType", false, "MSG_TYPE");
        public final static Property Time = new Property(6, String.class, "time", false, "TIME");
        public final static Property Remark = new Property(7, String.class, "remark", false, "REMARK");
    };


    public DoorlockrecordDao(DaoConfig config) {
        super(config);
    }
    
    public DoorlockrecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DOORLOCKRECORD' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'USER_NAME' TEXT," + // 1: userName
                "'USER_ID' INTEGER NOT NULL ," + // 2: userID
                "'DEVICE_ID' INTEGER NOT NULL ," + // 3: deviceID
                "'MSG' TEXT," + // 4: msg
                "'MSG_TYPE' TEXT," + // 5: msgType
                "'TIME' TEXT," + // 6: time
                "'REMARK' TEXT);"); // 7: remark
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DOORLOCKRECORD'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Doorlockrecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(2, userName);
        }
        stmt.bindLong(3, entity.getUserID());
        stmt.bindLong(4, entity.getDeviceID());
 
        String msg = entity.getMsg();
        if (msg != null) {
            stmt.bindString(5, msg);
        }
 
        String msgType = entity.getMsgType();
        if (msgType != null) {
            stmt.bindString(6, msgType);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(7, time);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(8, remark);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Doorlockrecord readEntity(Cursor cursor, int offset) {
        Doorlockrecord entity = new Doorlockrecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userName
            cursor.getInt(offset + 2), // userID
            cursor.getInt(offset + 3), // deviceID
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // msg
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // msgType
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // time
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // remark
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Doorlockrecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserID(cursor.getInt(offset + 2));
        entity.setDeviceID(cursor.getInt(offset + 3));
        entity.setMsg(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMsgType(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setTime(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setRemark(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Doorlockrecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Doorlockrecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
