package com.fbee.smarthome_wl.api;

import com.fbee.smarthome_wl.bean.AddModifyDeviceReq;
import com.fbee.smarthome_wl.bean.DelDeviceReq;
import com.fbee.smarthome_wl.bean.DeviceListInfo;
import com.fbee.smarthome_wl.bean.JiuCaiSettingInfo;
import com.fbee.smarthome_wl.bean.Pus;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.LoginReq;
import com.fbee.smarthome_wl.request.RegisterReq;
import com.fbee.smarthome_wl.response.BaseNetBean;
import com.fbee.smarthome_wl.response.DoorAlarmRecordinfo;
import com.fbee.smarthome_wl.response.DoorRecordInfo;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by ZhaoLi.Wang on 2016/9/26.
 */
public interface ApiService {

    /**
     * 获取门锁设备电量信息
     *
     * @return
     */
    @FormUrlEncoded
    @POST("datahistory.php")
    Observable<DoorlockpowerInfo> getDoorLockStaus(@FieldMap Map<String, String> fields);

    /**
     * 获取门锁开锁
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("datahistory.php")
    Observable<DoorRecordInfo> getDoorLockRecord(@FieldMap Map<String, String> fields);

    /**
     * 删除历史记录
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("datadelhistory.php")
    Observable<JsonObject> deteleRecord(@FieldMap Map<String, String> fields);

    /**
     * anychat删除设备
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("http://ihomecn.rollupcn.com/app/remove")
    Observable<BaseNetBean> delAnyChat(@FieldMap Map<String, String> fields);

    /**
     * 九彩删除
     *
     * @return
     */
    @FormUrlEncoded
    @POST("http://mysmart.9cyh.cn/app/catremove")
    Observable<BaseNetBean> delJiu(@FieldMap Map<String, String> deletDevice);

    /**
     * 获取警报信息
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("datahistory.php")
    Observable<DoorAlarmRecordinfo> getAlarmRecord(@FieldMap Map<String, String> fields);

    /**
     * 获取设备列表
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("jpush.php")
    Observable<DeviceListInfo> getDeviceList(@FieldMap Map<String, String> fields);

    /**
     * 设置设备白名单
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("jpush.php")
    Observable<JsonObject> addDeviceList(@FieldMap Map<String, String> fields);

    /**
     * 版本更新
     *
     * @return http://cloud.bmob.cn/8e775204bbfedf14/login
     * http://pus.wonlycloud.com:12100
     */
    @Headers("Connection:close")
    @POST("https://pus.wonlycloud.com:10400")
    Observable<JsonObject> updateVersion(@Body Pus pus);

    /**
     * 九彩登录
     *
     * @param loginReq
     * @return
     */
    @FormUrlEncoded
    @POST("http://mysmart.9cyh.cn/app/login")
    Observable<JsonObject> loginJiu(@FieldMap Map<String, String> loginReq);

    /**
     * anyChat 登录
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("http://ihomecn.rollupcn.com/app/login")
    Observable<ResAnychatLogin> loginAnyChat(@FieldMap Map<String, String> fields);

    /**
     * anyChat 注册
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("http://ihomecn.rollupcn.com/app/signup")
    Observable<ResAnychatLogin> registerAnyChat(@FieldMap Map<String, String> fields);

    /**
     * 九彩添加设备
     *
     * @return
     */
    @FormUrlEncoded
    @POST("http://mysmart.9cyh.cn/app/catdevices")
    Observable<BaseNetBean> addJiu(@FieldMap Map<String, String> addModifyDeviceReq);

    /**
     * 获取九彩猫眼设备设置参数
     *
     * @return
     */
    @GET("http://mysmart.9cyh.cn/app/device_setting")
    Observable<JiuCaiSettingInfo> RequestJiuCaiSetting(@Query("imei") String imei,
                                                       @Query("user_id") String userId);

    /***
     * 设置九彩猫眼同步设备
     */
    @FormUrlEncoded
    @POST("http://mysmart.9cyh.cn/app/setting")
    Observable<Object> JiuCaiSetting(@FieldMap Map<String, String> deviceInfo);

    /**
     * anychat添加设备
     *
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("http://ihomecn.rollupcn.com/app/devices")
    Observable<BaseNetBean> addAnyChat(@FieldMap Map<String, String> fields);

    /**
     * 九彩猫眼注册
     *
     * @return
     */
    @FormUrlEncoded
    @POST("http://mysmart.9cyh.cn/app/signup")
    Observable<BaseNetBean> registerJiu(@FieldMap Map<String, String> registerReq);

    /**
     * 服务器获取验证码
     * http://139.196.221.163:10300
     * https://ums.wonlycloud.com:10300
     *http://139.196.221.163:10300/
     * @return
     */
    @Headers("Connection:close")
    @POST("https://ums.wonlycloud.com:10300")
    Observable<JsonObject> umsService(@Body Ums ums);

    /**
     * 服务器获取验证码
     * http://139.196.221.163:10300
     * https://ums.wonlycloud.com:10300
     *
     * @return
     */
    @Headers("Connection:close")
    @POST("https://ums.wonlycloud.com:10300")
    Observable<Response<JsonObject>> umsLoginService(@Body Ums ums);

    /**
     * 下载文件
     *
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
