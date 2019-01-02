package com.fbee.smarthome_wl.dbutils;

import com.fbee.JiuCai_smarthome.greendao.IconDao;
import com.fbee.smarthome_wl.greendao.Icon;
import com.fbee.smarthome_wl.utils.LogUtil;

import static com.fbee.smarthome_wl.base.BaseApplication.getIconDao;

/**
 * @class name：com.fbee.smarthome_wl.dbutils
 * @anthor create by Zhaoli.Wang
 * @time 2017/6/14 14:36
 */
public class IconDbUtil {

    private static IconDbUtil iconDbUtil;
    public  static final int AREA = 1;
    public static final int SENCE = 2;
    private IconDbUtil(){}

    public static IconDbUtil getIns(){
        if(iconDbUtil == null) {
            synchronized (UserDbUtil.class) {
                if (iconDbUtil == null) {
                    iconDbUtil = new IconDbUtil();
                }
            }
        }
        return iconDbUtil;
    }


    public Icon getIcon(String account,String gateway,int type,String  name){
        Icon icon =null;
        try{
            icon =getIconDao().queryBuilder().where(IconDao.Properties.Account.eq(account),
                    IconDao.Properties.Gateway.eq(gateway),
                    IconDao.Properties.Type.eq(type),IconDao.Properties.Name.eq(name)).unique();

        }catch (Exception e){
            e.printStackTrace();
        }

       return icon;
    }

    public long getIconid(String account,String gateway,int type,String  name){
        Icon icon=getIconDao().queryBuilder().where(IconDao.Properties.Account.eq(account),
                IconDao.Properties.Gateway.eq(gateway),
                IconDao.Properties.Type.eq(type),IconDao.Properties.Name.eq(name)).unique();
        if(icon == null){
            return -1;
        }
        return icon.getId();
    }



    public void  addIcon(Icon icon){
//        Long id=getIconid(icon.getAccount(),icon.getGateway(),icon.getType(),icon.getName());
//        if(id != null &&id >0)
//        icon.setId(id);
        getIconDao().insertOrReplaceInTx(icon);
    }


    public void detele(String account,String gateway,int type,String name){
        try{
            Icon icon=getIconDao().queryBuilder().where(
                    IconDao.Properties.Account.eq(account),
                    IconDao.Properties.Gateway.eq(gateway),
                    IconDao.Properties.Type.eq(type),IconDao.Properties.Name.eq(name)).unique();
            if(null != icon){
                getIconDao().deleteByKey(icon.getId());
            }
        }catch (Exception e){

        }


    }



}
