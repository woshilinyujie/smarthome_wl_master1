package com.fbee.smarthome_wl.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.bean.ResLoginJiuCaibean;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.LocalLoginReq;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.corecode.AddGatewayActivity;
import com.fbee.smarthome_wl.ui.login.LoginActivity;
import com.fbee.smarthome_wl.ui.login.LoginContract;
import com.fbee.smarthome_wl.ui.login.LoginPresenter;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.base.BaseCommonPresenter.mSeqid;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

public class SplashActivity extends BaseActivity<LoginContract.Presenter> implements LoginContract.View {
    LoginResult.BodyBean.GatewayListBean gw;
    private String jsonobj;
    private Bundle extras;
    private long queryTime;
    private List<LoginResult.BodyBean.GatewayListBean> gateway_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void initView() {
        createPresenter(new LoginPresenter(this));
        //如果是自动登录状态
        if (PreferencesUtils.getBoolean(PreferencesUtils.ISAUTOLOGIN)) {
            initApi();
            if (!TextUtils.isEmpty(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME)) &&
                    !TextUtils.isEmpty(PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW))) {
                String userName = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
                String passWord = PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW);
                LocalLoginReq body = new LocalLoginReq();
                body.setUsername(userName);
                //登录密码
                String psw = Aes256EncodeUtil.SHAEncrypt(passWord);
                Date dt = new Date();
                long second = dt.getTime() / 1000;
                queryTime = second;
                String secondStr = String.valueOf(second);
                String subsecond = secondStr.substring(0, secondStr.length() - 2);
                body.setPassword(Aes256EncodeUtil.SHAEncrypt(psw.substring(psw.length() - 24) + subsecond));
                //            body.setPassword(MD5Tools.MD5(passWord));
                LocalLoginReq.PhoneBean phoneBean = new LocalLoginReq.PhoneBean();
                phoneBean.setUuid(AppUtil.getIMEI(this));
                phoneBean.setPlatform("android");
                phoneBean.setPlatform_version(android.os.Build.VERSION.RELEASE);
                phoneBean.setNetwork_type(AppUtil.getNetworkType(this));
                phoneBean.setEndpoint_type(android.os.Build.MODEL);
                body.setPhone(phoneBean);
                loginLocal(body);
            } else {
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                overridePendingTransition(0, android.R.anim.fade_out);
                                finish();
                            }
                        });
            }
        }
        //如果不是自动登录状态
        else {
            skipAct(LoginActivity.class);
            finish();
        }


    }

    @Override
    protected void initData() {
        extras = getIntent().getExtras();
        if (extras != null) {
            jsonobj = extras.getString("json");
        }
    }


    /**
     * 本地服务器登录
     *
     * @param bodyBean
     */
    public void loginLocal(LocalLoginReq bodyBean) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<Response<JsonObject>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(Response<JsonObject> response) {

                try {
                    Headers header = response.headers();
                    Date date = header.getDate("Date");
                    Long time = date.getTime();
                    JsonObject json = response.body();
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        LoginResult info = new Gson().fromJson(jsonObj.toString(), LoginResult.class);

                        loginLocalsuccess(info, time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                if (null != json) {
//                    JsonObject jsonObj = json.getAsJsonObject("UMS");
//                    if (null == jsonObj || jsonObj.size() == 0)
//                        return;
//                    LoginResult info = new Gson().fromJson(jsonObj.toString(), LoginResult.class);
//                    loginLocalsuccess(info);
//
//                }
            }
        });

        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_USER_LOGIN_REQ");
        header.setSeq_id(mSeqid.getAndIncrement() + "");
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyBean);
        ums.setUMS(umsbean);
        Subscription sub = mApiWrapper.loginLocal(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }


    /**
     * 本地服务器登录返回
     *
     * @param result
     */
    public void loginLocalsuccess(LoginResult result, Long time) {
        if (null == result)
            return;
        //本地登录成功
        if ("200".equals(result.getHeader().getHttp_code())) {
            if (result.getBody() == null) {
                //账号下没有网关
                skipAct(AddGatewayActivity.class);
                finish();
                return;
            }
            AppContext.getInstance().setBodyBean(result.getBody());
            //保存用户昵称
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_ALAIRS, result.getBody().getUser_alias());
            //本地服务器下没有网关
            gateway_list = result.getBody().getGateway_list();
            if (gateway_list != null && gateway_list.size() > 0) {
                gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                if (gw == null) {
                    gw = result.getBody().getGateway_list().get(0);
                    if (AppUtil.isMobileNO(gw.getUsername())) {
                        skipAct(MainActivity.class);
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                        finish();
                    } else {
                        loginFbee(gw.getUsername(), gw.getPassword());
                    }
                    presenter.registerAndlogin(gw.getUsername(), "123456");
                } else if (AppUtil.isMobileNO(gw.getUsername())) {
                    boolean falg = true;
                    String gwUuid = gw.getUuid();
                    for (int i = 0; i < gateway_list.size(); i++) {
                        String uuid = gateway_list.get(i).getUuid();
                        if (gwUuid.equals(uuid)) {
                            skipAct(MainActivity.class);
                            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                            finish();
                            return;
                        }
                    }
                    if (falg) {
                        gw = result.getBody().getGateway_list().get(0);
                        if (AppUtil.isMobileNO(gw.getUsername())) {
                            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                            skipAct(MainActivity.class);
                            presenter.registerAndlogin(gw.getUsername(), "123456");
                            finish();
                        } else {
                            presenter.registerAndlogin(gw.getUsername(), "123456");
                            loginFbee(gw.getUsername(), gw.getPassword());
                        }
                    }
                } else {
                    boolean falg = true;
                    String gwUuid = gw.getUuid();
                    for (int i = 0; i < gateway_list.size(); i++) {
                        String uuid = gateway_list.get(i).getUuid();
                        if (gwUuid.equals(uuid)) {
                            loginFbee(gw.getUsername(), gw.getPassword());
                            presenter.registerAndlogin(gw.getUsername(), "123456");
                            icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
                            return;
                        }
                    }
                    if (falg) {
                        gw = result.getBody().getGateway_list().get(0);
                        if (AppUtil.isMobileNO(gw.getUsername())) {
                            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                            presenter.registerAndlogin(gw.getUsername(), "123456");
                            skipAct(MainActivity.class);
                            finish();
                        } else {
                            loginFbee(gw.getUsername(), gw.getPassword());
                            presenter.registerAndlogin(gw.getUsername(), "123456");
                            icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
                        }
                    }
                }
            } else {
                //账号下没有网关
                PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), null);
                skipAct(AddGatewayActivity.class);
                finish();
                return;
            }
            icvss.equesUserLogOut();
            //移康猫眼登录
            if (gw != null && gw.getUsername() != null) {
                presenter.registerAndlogin(gw.getUsername(), "123456");
                icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
                Api.jpushSetting(this, gw.getUsername());
            }
        } else {
            if (Math.abs(queryTime - time / 1000) > 3 * 60) {
                ToastUtils.showShort("登录失败，请检查手机时间是否已校准");
            } else {
                ToastUtils.showShort(RequestCode.getRequestCode(result.getHeader().getReturn_string()));
            }


            skipAct(LoginActivity.class);
            finish();
        }

    }

    @Override
    public void onClick(View v) {

    }


    public void loginFbee(final String username, final String psw) {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                if (username != null && psw != null) {
                    int ret = mSerial.connectRemoteZll(username, psw);
                    subscriber.onNext(ret);
                    subscriber.onCompleted();
                }
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer ret) {
                        loginFbeeResult(ret);

                    }
                });

        mCompositeSubscription.add(sub);


    }

    //九彩登录回调
    @Override
    public void loginSuccess(Object obj) {
        if(obj instanceof ResAnychatLogin){
            LogUtil.e("登录成功","Anychat");
            ResAnychatLogin resAnychatLogin= (ResAnychatLogin) obj;
            if(!TextUtils.isEmpty(resAnychatLogin.getUser().getName())){
                PreferencesUtils.saveString("ANYCHAT_USERNAME",resAnychatLogin.getUser().getName());
            }
            //78978978978
            PreferencesUtils.saveString("ANYCHAT_PHONE","85285285285");
            PreferencesUtils.saveString("ANYCHAT_PASSWORD","111111");
//            PreferencesUtils.saveInt("ANYCHAT_USERID",resAnychatLogin.getUser().getId());
            //PreferencesUtils.saveObject("ANYCHAT_USERDEVICES",resAnychatLogin.getUser().getUser_devices());
            AppContext.setDevices(resAnychatLogin.getUser().getUser_devices());
            RxBus.getInstance().post(resAnychatLogin);
        }else if(obj instanceof JSONObject){
            LogUtil.e("登录成功","九彩");
            ResLoginJiuCaibean resLoginbean = new Gson().fromJson(obj.toString(), ResLoginJiuCaibean.class);
            //LogUtil.e("jiucailogin",obj.toString()+"");
            //LogUtil.e("id",resLoginbean.getData().getId());
            PreferencesUtils.saveString("JIUCAI_ID",resLoginbean.getUser().getId());
        }
    }
    public void loginFbeeResult(int result) {
        switch (result) {
            case 1:
                //保存最后登录的网关信息
                if (null != gw)
                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                Bundle bundle = new Bundle();
                if (jsonobj != null) {
                    bundle.putString("json", jsonobj);
                }
                skipAct(MainActivity.class, bundle);
                finish();
                break;
            case -1:
                skipAct(LoginActivity.class);
                finish();
                break;
            case -2:
                //虚拟网关
                if (gw != null && gw.getUsername().equals(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME))) {
                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                    skipAct(MainActivity.class);
                    finish();
                } else {
                    showToast("网关账号或密码错误!");
                    skipAct(LoginActivity.class);
                    finish();
                }
                break;
            case -3:
                showToast("网关登录超时！");
                skipAct(LoginActivity.class);
                finish();
                break;
            case -4:
                showToast("网关登录人数已上限!");
                skipAct(LoginActivity.class);
                finish();
                break;


        }
    }

    @Override
    public void loginLocalsuccess(LoginResult result, long time) {

    }


    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
