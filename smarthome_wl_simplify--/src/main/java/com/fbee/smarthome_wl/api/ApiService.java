package com.fbee.smarthome_wl.api;

import com.fbee.smarthome_wl.bean.Cms;
import com.fbee.smarthome_wl.bean.DeviceListInfo;
import com.fbee.smarthome_wl.bean.Pus;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.AMS;
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
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by ZhaoLi.Wang on 2016/9/26.
 */
public interface ApiService {

    /**
     * 获取门锁设备电量信息
     * @return
     */
    @FormUrlEncoded
    @POST("datahistory.php")
    Observable<DoorlockpowerInfo> getDoorLockStaus(@FieldMap Map<String, String> fields);

    /**
     * 获取门锁开锁
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("datahistory.php")
    Observable<DoorRecordInfo> getDoorLockRecord(@FieldMap Map<String, String> fields);



    /**
     * 删除历史记录
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("datadelhistory.php")
    Observable<JsonObject> deteleRecord(@FieldMap Map<String, String> fields);


    /**
     * 获取警报信息
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("datahistory.php")
    Observable<DoorAlarmRecordinfo> getAlarmRecord(@FieldMap Map<String, String> fields);


    /**
     * 获取设备列表
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("jpush.php")
    Observable<DeviceListInfo> getDeviceList(@FieldMap Map<String, String> fields);


    /**
     * 设置设备白名单
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST("jpush.php")
    Observable<JsonObject> addDeviceList(@FieldMap Map<String, String> fields);

    /**
     * 版本更新
     * @return http://cloud.bmob.cn/8e775204bbfedf14/login
     * http://pus.wonlycloud.com:12100
     */
    @Headers("Connection:close")
    @POST("https://pus.wonlycloud.com:10400")
    Observable<JsonObject>  updateVersion(@Body Pus pus);

    /**
     *  九彩登录
     * @param loginReq
     * @return
     */
    @POST("http://120.25.77.121/user/login")
    Observable<JsonObject> loginJiu(@HeaderMap Map<String, String> headers, @Body LoginReq loginReq);


    /**
     * 九彩猫眼注册
     * @return
     */
    @POST("http://120.25.77.121/user/register")
    Observable<BaseNetBean> registerJiu(@HeaderMap Map<String, String> headers, @Body RegisterReq registerReq);


    /**
     * 服务器获取验证码
     * http://139.196.221.163:10300
     * https://ums.wonlycloud.com:10300
     * http://139.196.221.163:10300
     * @return
     */
    @Headers("Connection:close")
    @POST("http://139.196.221.163:10300")
    Observable<JsonObject>  umsService(@Body Ums ums);

    /**
     * http://192.168.1.91:10200/
     * https://ams.wonlycloud.com:10200
     * @param ams
     * @return
     */
    @Headers("Connection:close")
    @POST("https://ams.wonlycloud.com:10200")
    Observable<JsonObject>  amsService(@Body AMS ams);

    /**
     * http://192.168.1.188:10100
     * https://cms.wonlycloud.com:10100
     * @param cms
     * @return
     */
    @Headers("Connection:close")
    @POST("https://cms.wonlycloud.com:10100")
    Observable<JsonObject>  cmsService(@Body Cms cms);


    /**
     * 服务器获取验证码
     * http://139.196.221.163:10300
     * https://ums.wonlycloud.com:10300
     * @return
     */
    @Headers("Connection:close")
    @POST("http://139.196.221.163:10300")
    Observable<Response<JsonObject>>  umsLoginService(@Body Ums ums);


    /**
     * 下载文件
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);


}
