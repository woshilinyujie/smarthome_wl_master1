package com.fbee.smarthome_wl.ui.videodoorlock;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.CMSBean;
import com.fbee.smarthome_wl.bean.Cms;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.AMS;
import com.fbee.smarthome_wl.request.AMSBean;
import com.fbee.smarthome_wl.request.ServiceConfigureReq;
import com.fbee.smarthome_wl.response.ServiceConfigureRes;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.Method;

import rx.Subscriber;
import rx.Subscription;

public class VideoLockDMSFirstActivity extends BaseActivity {
    private ImageView back;
    private TextView title;
    private Button btPeiwang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lock_dmsfirst);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        btPeiwang = (Button) findViewById(R.id.bt_peiwang);
        btPeiwang.setOnClickListener(this);
        back.setOnClickListener(this);
        //如果当前手机开启了数据流量将其关闭
        if (getMobileDataState(this, null)) {
            showErrorDialog();
        }
    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
        initApi();
        showLoadingDialog(null);
        reqServiceConfig();
    }

    private String serviceIp;
    private String servicePort;

    /**
     * 请求DAS服务配置
     */
    private void reqServiceConfig() {
        final ServiceConfigureReq body = new ServiceConfigureReq();
        body.setService_type("DAS");
        body.setSecret_key(AppContext.getInstance().getBodyBean().getSecret_key());
        body.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        body.setVendor_name(FactoryType.GENERAL);
        final Cms cms = getCms("MSG_GET_SERVICE_CONFIG_REQ", body);

//        final AddTokenReq addTokenReq=new AddTokenReq();
//        addTokenReq.setAttitude("read");
//        addTokenReq.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
//        addTokenReq.setSecret_key(AppContext.getInstance().getBodyBean().getSecret_key());
//        final AMS ams = getAMS("MSG_TOKEN_ADD_REQ", addTokenReq);


        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    hideLoadingDialog();
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("CMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        if (jsonObj.has("body")) {
                            ServiceConfigureRes bean = new Gson().fromJson(jsonObj.toString(), ServiceConfigureRes.class);
                            if (bean == null) return;
                            if ("200".equals(bean.getHeader().getHttp_code())) {
                                if (bean.getBody() != null && bean.getBody().getServer_ip() != null && bean.getBody().getServer_port() != null) {
                                    serviceIp = bean.getBody().getServer_ip();
                                    servicePort = bean.getBody().getServer_port();
                                }
                            } else {
                                ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
                            }
                        }
                    }
                }
            }
        });
        Subscription subscription = mApiWrapper.getServerConfigure(cms).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
//        Subscription subscription =mApiWrapper.getServerConfigure(cms).filter(new Func1<JsonObject, Boolean>() {
//            @Override
//            public Boolean call(JsonObject json) {
//                boolean isSuccess = false;
//                if (!mCompositeSubscription.isUnsubscribed()) {
//                    if (null != json) {
//                        JsonObject jsonObj = json.getAsJsonObject("CMS");
//                        if (null == jsonObj || jsonObj.size() == 0)
//                            return false;
//                        ServiceConfigureRes bean=new Gson().fromJson(jsonObj.toString(), ServiceConfigureRes.class);
//                        if ("200".equals(bean.getHeader().getHttp_code())){
//                            if(bean.getBody()!=null&&bean.getBody().getServer_ip()!=null&&bean.getBody().getServer_port()!=null){
//                                serviceIp=bean.getBody().getServer_ip();
//                                servicePort=bean.getBody().getServer_port();
//                            }
//                            isSuccess = false;
//                        }else{
//                            if("RETURN_INVALID_TOKEN_STRING".equals(bean.getHeader().getReturn_string())){
//                                isSuccess = true;
//                            }
//                        }
//                    }
//                }
//                return isSuccess;
//            }
//        }).flatMap(new Func1<JsonObject, Observable<JsonObject>>() {
//            @Override
//            public Observable<JsonObject > call(JsonObject jsonObject) {
//
//                return mApiWrapper.addToken(ams);
//            }
//        }).filter(new Func1<JsonObject, Boolean>() {
//            @Override
//            public Boolean call(JsonObject bean) {
//                    boolean isSus=false;
//                    if (null != bean) {
//                        JsonObject jsonObj = bean.getAsJsonObject("AMS");
//                        if (null == jsonObj || jsonObj.size() == 0)
//                            return false;
//                        AddTokenResponse res = new Gson().fromJson(jsonObj.toString(), AddTokenResponse.class);
//                        if ("200".equals(res.getHeader().getHttp_code())){
//                            if(res.getBody()!=null&&res.getBody().getToken()!=null){
//                                LogUtil.e("服务器发送","token返回"+res.getBody().getToken());
//                                AppContext.setToken(res.getBody().getToken());
//                                isSus=true;
//                            }
//                        }else{
//                            return false;
//                        }
//                    }else{
//                        return false;
//                    }
//
//                    return isSus;
//            }
//        }).flatMap(new Func1<JsonObject, Observable<JsonObject>>() {
//            @Override
//            public Observable<JsonObject> call(JsonObject s) {
//
//                body.setToken(AppContext.getToken());
//                Cms mCms=getCms("MSG_GET_SERVICE_CONFIG_REQ",body);
//                return   mApiWrapper.getServerConfigure(mCms);
//            }
//        }).subscribe(subscriber);;
//        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bt_peiwang:
                if (serviceIp != null && servicePort != null) {
                    if (getMobileDataState(this, null)) {
                        showErrorDialog();
                        break;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("serviceIp", serviceIp);
                    bundle.putString("servicePort", servicePort);
                    skipAct(VideoDoorlockWifisActivity.class, bundle);
                    finish();
                } else {
                    showToast("请求出错,请返回重试");
                }

                break;
        }
    }

    protected <E> Cms getCms(String type, E bodyBean) {
        CMSBean.HeaderBean header = new CMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type(type);
        header.setSeq_id("2");
        Cms cms = new Cms();
        CMSBean cmsbean = new CMSBean();
        cmsbean.setHeader(header);
        cmsbean.setBody(bodyBean);
        cms.setCms(cmsbean);

        return cms;
    }

    protected <E> AMS getAMS(String type, E bodyBean) {
        AMSBean.HeaderBean header = new AMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type(type);
        header.setSeq_id("3");
        AMS ams = new AMS();
        AMSBean amsbean = new AMSBean();
        amsbean.setHeader(header);
        amsbean.setBody(bodyBean);
        ams.setAMS(amsbean);
        return ams;
    }

    /**
     * 返回手机移动数据的状态
     *
     * @param pContext
     * @param arg      默认填null
     * @return true 连接 false 未连接
     */
    public static boolean getMobileDataState(Context pContext, Object[] arg) {
        ConnectivityManager mConnectivity = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) pContext.getSystemService(TELEPHONY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        int netType = info.getType();
        int netSubtype = info.getSubtype();

        if (netType == ConnectivityManager.TYPE_WIFI) {  //WIFI
            return false;
        } else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS && !mTelephony.isNetworkRoaming()) {   //MOBILE
            return info.isConnected();
        } else {
            return false;
        }

//        try {
//
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            Class ownerClass = mConnectivityManager.getClass();
//
//            Class[] argsClass = null;
//            if (arg != null) {
//                argsClass = new Class[1];
//                argsClass[0] = arg.getClass();
//            }
//
//            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
//
//            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
//
//            return isOpen;
//
//        } catch (Exception e) {
//            // TODO: handle exception
//
//            System.out.println("得到移动数据状态出错");
//            return false;
//        }

    }

    private AlertDialog checkAlertDialog;

    private void showErrorDialog() {

        checkAlertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("请手动关闭手机数据流量,否则配网将会出错").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkAlertDialog.dismiss();
            }
        }).setCancelable(false).show();
    }
}
