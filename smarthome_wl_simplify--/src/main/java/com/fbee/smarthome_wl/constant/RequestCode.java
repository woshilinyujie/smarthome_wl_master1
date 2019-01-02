package com.fbee.smarthome_wl.constant;

import com.fbee.smarthome_wl.api.ApiWrapper;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.ActivityPageManager;
import com.fbee.smarthome_wl.request.LocalLoginReq;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.google.gson.JsonObject;

import java.util.Date;

import retrofit2.Response;
import rx.Observer;

/**
 * Created by wl on 2017/5/16.
 */

public class RequestCode {
    public static final String RETURN_UNKNOWN_USERNAME_STRING = "RETURN_UNKNOWN_USERNAME_STRING";
    public static final String RETURN_UNKNOWN_GLOBAL_ROAMING_STRING = "RETURN_UNKNOWN_GLOBAL_ROAMING_STRING";
    public static final String RETURN_SMS_TIMEOUT_STRING = "RETURN_SMS_TIMEOUT_STRING";
    public static final String RETURN_SMS_NOT_ALLOWED_STRING = "RETURN_SMS_NOT_ALLOWED_STRING";
    public static final String RETRUN_USER_IS_EXISTS_STRING = "RETRUN_USER_IS_EXISTS_STRING";
    public static final String RETRUN_USER_NOT_EXISTS_STRING = "RETRUN_USER_NOT_EXISTS_STRING";
    public static final String RETRUN_USER_PASSWORD_ERROR_STRING = "RETRUN_USER_PASSWORD_ERROR_STRING";
    public static final String RETRUN_NEED_LOGIN_STRING = "RETRUN_NEED_LOGIN_STRING";
    public static final String RETRUN_INVALID_GATEWAY_BODY_STRING = "RETRUN_INVALID_GATEWAY_BODY_STRING";
    public static final String RETRUN_PARENT_USER_HAS_BINDING_STRING = "RETRUN_PARENT_USER_HAS_BINDING_STRING";
    public static final String RETRUN_CHILD_USER_HAS_BINDING_STRING = "RETRUN_CHILD_USER_HAS_BINDING_STRING";
    public static final String RETRUN_PARENT_USER_NOT_EXISTS_STRING = "RETRUN_PARENT_USER_NOT_EXISTS_STRING";
    public static final String RETRUN_CHILD_USER_NOT_EXISTS_STRING = "RETRUN_CHILD_USER_NOT_EXISTS_STRING";
    public static final String RETRUN_USER_IS_PARENT_STRING = "RETRUN_USER_IS_PARENT_STRING";
    public static final String RETRUN_GATEWAY_NOT_EXISTS_STRING = "RETRUN_GATEWAY_NOT_EXISTS_STRING";
    public static final String RETRUN_DEVICE_NOT_EXISTS_STRING = "RETRUN_DEVICE_NOT_EXISTS_STRING";
    public static final String RETRUN_KICKED_USER_STRING = "RETRUN_KICKED_USER_STRING";
    public static final String RETURN_SMS_SEND_FREQUENT_STRING = "RETURN_SMS_SEND_FREQUENT_STRING";
    public static final String RETURN_INVALID_BODY_STRING ="RETURN_INVALID_BODY_STRING";

    public static String getRequestCode(String code) {
        String msg = "";
        switch (code) {
            case RETURN_UNKNOWN_USERNAME_STRING:
                msg = "用户名格式错误";
                break;

            case RETURN_UNKNOWN_GLOBAL_ROAMING_STRING:
                msg = "国际区号格式错误";
                break;
            case RETURN_SMS_TIMEOUT_STRING:
                msg = "用户名格式错误";
                break;
            case RETURN_SMS_NOT_ALLOWED_STRING:
                msg = "短信验证码错误";
                break;
            case RETRUN_USER_IS_EXISTS_STRING:
                msg = "用户已存在";
                break;
            case RETRUN_USER_NOT_EXISTS_STRING:
                msg = "用户不存在";
                break;
            case RETRUN_USER_PASSWORD_ERROR_STRING:
                msg = "用户密码错误";
                break;

            case RETRUN_NEED_LOGIN_STRING:
                msg = "连接超时，操作失败";
                login();
                break;
            case RETRUN_INVALID_GATEWAY_BODY_STRING:
                msg = "未知的网关信息";
                break;
            case RETRUN_PARENT_USER_HAS_BINDING_STRING:
                msg = "父用户已被绑定";
                break;
            case RETRUN_CHILD_USER_HAS_BINDING_STRING:
                msg = "子用户已被绑定";
                break;
            case RETRUN_PARENT_USER_NOT_EXISTS_STRING:
                msg = "父用户不存在";
                break;
            case RETRUN_CHILD_USER_NOT_EXISTS_STRING:
                msg = "子用户不存在";
                break;

            case RETRUN_USER_IS_PARENT_STRING:
                msg = "不能同时为父子用户";
                break;

            case RETRUN_GATEWAY_NOT_EXISTS_STRING:
                msg = "网关不存在";
                break;

            case RETRUN_DEVICE_NOT_EXISTS_STRING:
                msg = "设备不存在";
                break;
            case RETURN_SMS_SEND_FREQUENT_STRING:
                msg = "短信验证码发送过于频繁";
                break;
            case RETRUN_KICKED_USER_STRING:
                ToastUtils.showShort("用户已在别处登陆");
                ActivityPageManager.finishAllActivity();
                break;
            case RETURN_INVALID_BODY_STRING:
                msg ="未知的消息格式";
                break;
            default:
                msg ="未知的服务器错误";
                break;
        }
        return msg;
    }



    private static void login(){
        LocalLoginReq body = new LocalLoginReq();
        body.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        String psw = Aes256EncodeUtil.SHAEncrypt(PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW));
        Date dt = new Date();
        long second = dt.getTime() / 1000;
        String secondStr = String.valueOf(second);
        String subsecond = secondStr.substring(0, secondStr.length() - 2);
        body.setPassword(Aes256EncodeUtil.SHAEncrypt(psw.substring(psw.length() - 24) + subsecond));
        LocalLoginReq.PhoneBean phoneBean = new LocalLoginReq.PhoneBean();
        phoneBean.setUuid(AppUtil.getIMEI(BaseApplication.getInstance().getContext()));
        phoneBean.setPlatform("android");
        phoneBean.setPlatform_version(android.os.Build.VERSION.RELEASE);
        phoneBean.setNetwork_type(AppUtil.getNetworkType(BaseApplication.getInstance().getContext()));
        phoneBean.setEndpoint_type(android.os.Build.MODEL);
        body.setPhone(phoneBean);

        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_USER_LOGIN_REQ");
        header.setSeq_id("1");
        Ums ums = new Ums();
        UMSBean umsbean=new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(body);
        ums.setUMS(umsbean);

        Observer<Response<JsonObject>> receiver = new Observer<Response<JsonObject>>() {
            @Override
            public void onCompleted() {
                //数据接收完成时调用
            }

            @Override
            public void onError(Throwable e) {
                //发生错误调用
            }

            @Override
            public void onNext(Response<JsonObject> s) {

            }
        };
        new ApiWrapper().loginLocal(ums).subscribe(receiver);

    }




}
