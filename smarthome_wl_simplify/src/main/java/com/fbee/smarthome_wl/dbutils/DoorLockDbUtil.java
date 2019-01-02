package com.fbee.smarthome_wl.dbutils;

/**
 * Created by WLPC on 2017/3/6.
 */

import com.fbee.smarthome_wl.greendao.Doorlockrecord;
import com.fbee.smarthome_wl.greendao.DoorlockrecordDao;

import java.util.List;

import static com.fbee.smarthome_wl.base.BaseApplication.getDoorlockrecordDao;

/**
 * 门锁记录数据库操作
 */
public class DoorLockDbUtil {


    private static DoorLockDbUtil doorLockDbUtil;

    private DoorLockDbUtil(){};

    public static DoorLockDbUtil getIns(){
        if(doorLockDbUtil == null) {
            synchronized (DoorLockDbUtil.class) {
                if (doorLockDbUtil == null) {
                    doorLockDbUtil = new DoorLockDbUtil();
                }
            }
            return doorLockDbUtil;
        }
        return doorLockDbUtil;
    }

    /**
     *
     * 插入门锁记录单条数据
     * @param doorlockrecord
     * @return
     */
    public boolean insertDoorLockRecord(Doorlockrecord doorlockrecord){
        try {
            if(doorlockrecord==null)return false;
            long count = getDoorlockrecordDao().count();
            if(count>=200){
                List<Doorlockrecord> beans = getDoorlockrecordDao().loadAll();
                getDoorlockrecordDao().delete(beans.get(0));
            }
            long rowId=  getDoorlockrecordDao().insertOrReplace(doorlockrecord);
        }catch (Exception e){
            return false;
        }
        return true;
    };

    /**
     * 插入门锁记录列表
     * @param doorlockrecordList
     * @return
     */
    public boolean insertDoorLockRecordList(final List<Doorlockrecord> doorlockrecordList){
        try {
            if(doorlockrecordList == null || doorlockrecordList.isEmpty()){
                return false;
            }
            getDoorlockrecordDao().getSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i<doorlockrecordList.size(); i++){
                        Doorlockrecord doorlockrecord = doorlockrecordList.get(i);
                        insertDoorLockRecord(doorlockrecord);
                    }
                }
            });

        }catch (Exception e){
            return false;
        }
        return true;
    };

    /**
     *
     * 删除所有门锁记录
     */
    public void  deleteAllDoorLockRecord(){
        getDoorlockrecordDao().deleteAll();
    }

    /**
     * 通过门锁id 删除所有记录
     * @param uid
     */
    public void  deleteAllDoorLockRecordByUid(int uid){
        List<Doorlockrecord> mlist = getDoorlockrecordDao().queryBuilder().where(DoorlockrecordDao.Properties.DeviceID.eq(uid)).build().list();
        if(mlist!=null&&mlist.size()>0){
            for (int i = 0; i < mlist.size(); i++) {
                getDoorlockrecordDao().delete(mlist.get(i));
            }
        }


    }

    /**
     * 获取所有门锁数据列表
     * @return
     */
    public List<Doorlockrecord> getAllDoorLockRecord(){
       return getDoorlockrecordDao().loadAll();
    }


    /**
     * 根据设备id uid查询记录并按时间降序排列
     * @param uid
     * @return
     */
    public List<Doorlockrecord> getDoorLockRecordByUid(int  uid){

        return getDoorlockrecordDao().queryBuilder().where(DoorlockrecordDao.Properties.DeviceID.eq(uid)).orderDesc(DoorlockrecordDao.Properties.Time).build().list();
    }


    
    /**
     * 获取门锁记录数量
     * @return
     */
    public long getDoorLockRecordCounts(){

        return  getDoorlockrecordDao().count();
    }


    /**
     * 通过门锁id获取限制条数和偏移量的门锁记录并按时间升序排列
     * @return
     */
    public List<Doorlockrecord> getCustomDoorLockRecords(int uid,int lim,int offs ){
        List<Doorlockrecord> recordList = getDoorlockrecordDao().queryBuilder()
                .where(DoorlockrecordDao.Properties.DeviceID.eq(uid))
                .offset(offs)
                .limit(lim)
                .orderAsc(DoorlockrecordDao.Properties.Time)
                .build().list();
        return recordList;
    }

    /**
     * 根据时间删除时间点之前的数据
     * @param uid
     * @param time
     */
    public void deleteDoorLockRecordsTimeAgo(int uid,String time ){
        List<Doorlockrecord> recordList = getDoorlockrecordDao().queryBuilder()
                .where(DoorlockrecordDao.Properties.DeviceID.eq(uid),DoorlockrecordDao.Properties.Time.le(time))
                .build().list();
        if(recordList!=null&&recordList.size()>0){
            for (int i = 0; i <recordList.size() ; i++) {
                getDoorlockrecordDao().delete(recordList.get(i));
            }
        }

    }

    /**
     * 删除单个数据
     * @param d
     */
    public void deleteDoorLockRecord(Doorlockrecord d){
        getDoorlockrecordDao().delete(d);
    }
}
