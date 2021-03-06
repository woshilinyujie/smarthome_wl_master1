package com.fbee.smarthome_wl.api;

import com.fbee.smarthome_wl.bean.Ams;
import com.fbee.smarthome_wl.bean.AmsR;
import com.fbee.smarthome_wl.bean.DeviceListInfo;
import com.fbee.smarthome_wl.bean.Pus;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.LoginReq;
import com.fbee.smarthome_wl.request.RegisterReq;
import com.fbee.smarthome_wl.response.BaseNetBean;
import com.fbee.smarthome_wl.response.DoorAlarmRecordinfo;
import com.fbee.smarthome_wl.response.DoorRecordInfo;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * Created by ZhaoLi.Wang on 2016/9/26.
 */
public class ApiWrapper extends Api {


    /**
     * 获取门锁设备电量信息
     * @param mDoorLockParams
     * @return
     */
    public Observable<DoorlockpowerInfo> getDoorInfo(Map mDoorLockParams) {
        return applySchedulers(getService().getDoorLockStaus(mDoorLockParams));
    }

    /**
     * 门锁开锁消息
     * @param params
     * @return
     */
    public Observable<DoorRecordInfo> getDoorRecord(Map params){
        return applySchedulers(getService().getDoorLockRecord(params));

    }


    /**
     * 删除开锁历史记录
     * @param params
     * @return
     */
    public Observable<JsonObject>  deteleDoorRecord(Map params){
        return applySchedulers(getService().deteleRecord(params));
    }


    /**
     * 门锁警报消息
     * @param params
     * @return
     */
    public Observable<DoorAlarmRecordinfo> getAlarmRecord(Map params){
        return applySchedulers(getService().getAlarmRecord(params));
    }


    /**
     * 获取白名单
     * @param params
     * @return
     */
    public Observable<DeviceListInfo> getDeviceList(Map params){
        return applySchedulers(getService().getDeviceList(params));
    }

    /**
     * 添加白名单
     * @param params
     * @return
     */
    public  Observable<JsonObject>  addDevice(Map params){
        return applySchedulers(getService().addDeviceList(params));
    }
    /**
     * 获取token
     * @param params
     * @return
     */
    public  Observable<JsonObject>  getToken(Ams params){
        String cookie = PreferencesUtils.getString(PreferencesUtils.COOKIE + PreferencesUtils.getString(LOCAL_USERNAME));
        Map<String, String> map = new HashMap<String, String>();
        map.clear();
        map.put("cookie",cookie);
        return applySchedulers(getService().amsTokenService(map,params));
    }


    /**
     * 升级接口
     * @param params
     * @return
     */
    public Observable<JsonObject>  updateVersion(Pus params){
        return applySchedulers(getService().updateVersion(params));
    }


    /**
     * 登录九彩
     * @param loginReq
     * @return
     */
    public  Observable<JsonObject> loginJiu(Map<String, String> headers, LoginReq loginReq){
        return applySchedulers(getService().loginJiu(headers,loginReq));
    }

    /**
     * 九彩注册
     * @param registerReq
     * @return
     */
    public  Observable<BaseNetBean> registerJiu(Map<String, String> headers, RegisterReq registerReq){
        return applySchedulers(getService().registerJiu(headers,registerReq));
    }


    /**
     * 本地服务器，验证码获取
     * @return
     */
    public Observable<JsonObject>  sendMessage(Ums  ums){
        return applySchedulers(getService().umsService(ums));
    }


    /**
     * 账号注册
     * @return
     */
    public Observable<JsonObject> register(Ums  ums){
        return  applySchedulers(getService().umsService(ums));
    }


    /**
     * 本地服务器登录
     * @return
     */
    public Observable<Response<JsonObject>> loginLocal(Ums  ums) {

        return applySchedulers(getService().umsLoginService(ums));
       // return applySchedulers(getService().umsService(ums));
        //return  applySchedulers(getService().loginLocal());
    }


    /**
     * 获取用户首页配置
     * @return
     */
    public Observable<JsonObject>  userConfig(Ums  ums){
        return   applySchedulers(getService().umsService(ums));
    }


    /**
     * 查询设备用户列表
     * @return
     */
    public Observable<JsonObject>  getUserEquipmentlist(Ums  ums){
        return  applySchedulers(getService().umsService(ums));
    }

    /**
     * 更新用户配置
     * @return
     */
    public  Observable<JsonObject> updateHomeConfiguration(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }
    /**
     * 修改密码
     * @return
     */
    public  Observable<JsonObject> modifyPassWord(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }
    /**
     * 忘记密码
     * @return
     */
    public  Observable<JsonObject> forgetPassWord(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }

    /**
     * 更新个人资料
     * @return
     */
    public  Observable<JsonObject> updateUserInfo(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }

    /**
     * 注销用户
     * @param ums
     * @return
     */
    public  Observable<JsonObject> destroyUser(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }


    /**
     * 添加子用户
     * @return
     */
    public  Observable<JsonObject> addChildUser(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }
    /**
     * 查询设备列表
     * @return
     */
    public  Observable<JsonObject> queryDevicesList(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }
    /**
     * 查询子用户信息
     * @return
     */
    public  Observable<JsonObject> querySubUserInfo(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }
    /**
     * 查询ys消息
     * @return
     */
    public  Observable<JsonObject> queryYsInfo(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }


    /**
     * 删除子用户
     * @return
     */
    public  Observable<JsonObject> deleteChildUser(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }

    /**
     * 添加网关
     * @return
     */
    public  Observable<JsonObject> addGateWay(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }

    /**
     * 删除网关
     * @return
     */
    public  Observable<JsonObject> deleteGateWay(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }


    /**
     * 添加设备
     * @return
     */
    public  Observable<JsonObject> addDevices(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }

    /**
     * 删除设备
     * @return
     */
    public  Observable<JsonObject> deleteDevices(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }

    /**
     * 添加设备用户
     * @return
     */
    public  Observable<JsonObject> addDeviceUsre(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }

    /**
     * 删除设备用户
     * @return
     */
    public  Observable<JsonObject> deleteDeviceUsre(Ums ums){
        return  applySchedulers(getService().umsService(ums));
    }


    /**
     * 下载文件
     * @param url
     * @return
     */
    public Observable<ResponseBody> downloadFile(String url){
        return  applySchedulers(getDownloadService().download(url));
    }



}
