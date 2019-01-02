package com.fbee.smarthome_wl.dbutils;

import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.greendao.User;
import com.fbee.smarthome_wl.greendao.UserDao;

import java.util.List;

import static com.fbee.smarthome_wl.base.BaseApplication.getUserDao;

/**
 * 用户表
 * @class name：com.fbee.smarthome_wl.dbutils
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/3 11:18
 */
public class UserDbUtil {

    private static UserDbUtil userDbUtil;

    private UserDbUtil(){};

    public static UserDbUtil getIns(){
        if(userDbUtil == null) {
            synchronized (UserDbUtil.class) {
                if (userDbUtil == null) {
                    userDbUtil = new UserDbUtil();
                }
            }
        }
        return userDbUtil;
    }


    /**
     * 插入单条数据
     * @param user
     * @return
     */
    public boolean insert(User user){
        try {
            long rowId=  getUserDao().insertOrReplace(user);
        }catch (Exception e){
            return false;
        }
        return true;
    };

    /**
     * 插入列表
     * @param userList
     * @return
     */
    public boolean insertList(final List<User> userList){
        try {
            if(userList == null || userList.isEmpty()){
                return false;
            }
            getUserDao().getSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i<userList.size(); i++){
                        User user = userList.get(i);
                        getUserDao().insertOrReplace(user);
                    }
                }
            });

        }catch (Exception e){
            return false;
        }
        return true;
    };

    /**
     * 删除所有数据
     */
    public void deleteAllUser(){
        getUserDao().deleteAll();
    }

    /**
     * 根据id,删除数据
     * @param id      用户id
     */
    public void deleteNote(long id){
        getUserDao().deleteByKey(id);
    }

    /**
     * 根据用户类,删除信息
     * @param user    用户信息类
     */
    public void deleteNote(User user){
        getUserDao().delete(user);
    }

    /**
     * 根据用户编号，设备uid查询是否存在该用户
     * @param cardNum
     * @param uid
     * @return
     */
    public boolean queryUserIsExist(int cardNum,int uid){
        User user=getUserDao().queryBuilder().where(UserDao.Properties.Deviceid.eq(uid),UserDao.Properties.Userid.eq(cardNum)).unique();
        if(user!=null){
            return true;
        }
        return false;
    }

    /**
     * 根据uid查找该设备下的所有用户
     * @param uid
     * @return
     */
    public List<User> queryAllUsersByUid(int uid){
        List<User> users=getUserDao().queryBuilder().where(UserDao.Properties.Deviceid.eq(uid)).list();
        return users;
    }

    /**
     * 根据用户id查找别名
     * @param
     * @return
     */
    public String getUserAliasByID(long cardNum,int uid ){
        User user=getUserDao().queryBuilder().where(UserDao.Properties.Deviceid.eq(uid),UserDao.Properties.Userid.eq(cardNum)).unique();
        if(user==null){
            return null;
        }
        return  user.getUseralias();
    }

    /**
     * 根据用户id,替换当前用户
     * @param id
     * @param user
     */
    public void replaceUserById(long id,User user){
        deleteNote(id);
        insert(user);
    }
    /**
     * 所有用户
     * @return
     */
    public List<User>  getAlluser(){
       return getUserDao().loadAll();
    }

    /**
     * 获取用户数量
     * @return
     */
    public long getCounts(){
        return  BaseApplication.getUserDao().count();
    }




}
