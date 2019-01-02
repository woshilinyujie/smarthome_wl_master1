package com.fbee.smarthome_wl.ui.main.homepage;

import android.support.v4.util.ArrayMap;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.request.HomePageInfoReq;
import com.fbee.smarthome_wl.request.LoginReq;
import com.fbee.smarthome_wl.request.UpdateUserConfigurationReq;
import com.fbee.smarthome_wl.response.BaseNetBean;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.homepage
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/28 13:43
 */
public class HomePresenter extends BaseCommonPresenter<HomeContract.View> implements HomeContract.Presenter {
    public HomePresenter(HomeContract.View view) {
        super(view);
    }

    @Override
    public void loginFbee(final String username, final String psw) {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
//                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                //释放资源
                mSerial.releaseSource();
                //登录
                int ret = mSerial.connectRemoteZll(username, psw);
                mSerial.getGateWayInfo();
                subscriber.onNext(ret);
                subscriber.onCompleted();
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(Integer ret) {

                        view.loginFbeeResult(ret);

                    }
                });

        mCompositeSubscription.add(sub);

    }

    /**
     * 查询首页配置
     *
     * @param bodyBean
     */
    @Override
    public void getHomePageSetting(HomePageInfoReq bodyBean) {

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JsonObject json) {
                if (null != json) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    if (jsonObj.has("body")) {
                        HomePageResponse info = new Gson().fromJson(jsonObj.toString(), HomePageResponse.class);
                        view.userConfigResult(info);
                    } else {
                        BaseResponse info = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.userConfigResultNoBody(info);
                    }

                }
            }
        });

        Ums ums = getUms("MSG_USER_CONFIG_QUERY_REQ", bodyBean);
        Subscription sub = mApiWrapper.userConfig(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);

    }

    /**
     * 设置用户配置
     *
     * @param body
     */
    @Override
    public void setUserConfig(UpdateUserConfigurationReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JsonObject json) {
                if (null != json) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    BaseResponse info = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                    view.setCallBack(info);
                }
            }
        });

        Subscription sub = mApiWrapper.updateHomeConfiguration(getUms("MSG_USER_CONFIG_UPDATE_REQ", body)).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }

    //添加网关
    @Override
    public void addGateway(AddGateWayReq bodyBean) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JsonObject json) {
                if (null != json) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    BaseResponse info = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                    view.setCallBack(info);
                }
            }
        });


        Ums ums = getUms("MSG_GATEWAY_ADD_REQ", bodyBean);
        Subscription sub = mApiWrapper.addGateWay(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);

    }

    @Override
    public void registerAndlogin(final String username, final String psw) {

        final Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", username);
        map.put("password", psw);

        final Map<String, String> regmap = new HashMap<String, String>();
        regmap.put("name", username);
        regmap.put("mobile", username);
        regmap.put("password", psw);

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<Object>() {
            @Override
            public void onNext(Object obj) {
                //  LogUtil.e("onNext","=============");
                view.loginSuccess(obj);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

        final Observable<ResAnychatLogin> anychat = mApiWrapper.loginAnychat(map).filter(new Func1<ResAnychatLogin, Boolean>() {
            @Override
            public Boolean call(ResAnychatLogin resAnychatLogin) {
                String status = resAnychatLogin.getStatus();
                boolean isneedReg = false;
                if (null != status) {
                    if ("failure".equals(status)) {
                        isneedReg = true;
                    } else if ("success".equals(status)) {

                    }
                }
                if (!isneedReg)
                    view.loginSuccess(resAnychatLogin);
                return isneedReg;
            }
        }).flatMap(new Func1<ResAnychatLogin, Observable<ResAnychatLogin>>() {
            @Override
            public Observable<ResAnychatLogin> call(ResAnychatLogin resAnychatLogin) {

                return mApiWrapper.registerAnychat(regmap);
            }
        }).filter(new Func1<ResAnychatLogin, Boolean>() {
            @Override
            public Boolean call(ResAnychatLogin resAnychatLogin) {
                String status = resAnychatLogin.getStatus();
                boolean isSuccess = true;
                String remoteId = resAnychatLogin.getUser().getId() + "";
                if (null != status) {
                    if ("001".equals(status)) {

                    } else if ("002".equals(status)) {
                        isSuccess = false;
                    }
                }
                return isSuccess;
            }
        }).flatMap(new Func1<ResAnychatLogin, Observable<ResAnychatLogin>>() {
            @Override
            public Observable<ResAnychatLogin> call(ResAnychatLogin resAnychatLogin) {
                return mApiWrapper.loginAnychat(map);
            }
        });

        //九彩

        final Map<String, String> headers = new HashMap<>();
        String token = PreferencesUtils.getString("JIUCAI_TOKEN");
        if (token == null) {
            token = "";
        }
        headers.put("Content-Type", "application/json; charset=utf-8");
        ArrayMap loginReq = new ArrayMap();
        loginReq.put("mobile", username);
        loginReq.put("password", psw);
        Observable<JsonObject> jiu = mApiWrapper.loginJiu(loginReq).filter(new Func1<JsonObject, Boolean>() {
            @Override
            public Boolean call(JsonObject resLoginbean) {
                boolean isSuccess = true;
                // LogUtil.e("jiucaiJson",resLoginbean.toString());
                try {
                    if ("success".equals(resLoginbean.get("status").getAsString())) {
                        JSONObject js = new JSONObject(resLoginbean.toString());
                        view.loginSuccess(js);
                        isSuccess = false;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return isSuccess;
            }
        }).flatMap(new Func1<JsonObject, Observable<BaseNetBean>>() {
            @Override
            public Observable<BaseNetBean> call(JsonObject resLoginbean) {
                ArrayMap registerreq = new ArrayMap<String, String>();
                registerreq.put("mobile", username);
                registerreq.put("password", psw);
                registerreq.put("name", username);
                registerreq.put("city", AppContext.getLocation());
                registerreq.put("device", username);
                registerreq.put("email", "56464545@qq.com");
                return mApiWrapper.registerJiu(registerreq);
            }
        }).filter(new Func1<BaseNetBean, Boolean>() {
            @Override
            public Boolean call(BaseNetBean baseNetBean) {
                boolean isSuccess = false;
                LogUtil.e("zhuce", baseNetBean.msg);
                try {
                    if ("1".equals(baseNetBean.status)) {
                        isSuccess = true;
                    }
                } catch (JsonIOException e) {
                    e.printStackTrace();
                }
                return isSuccess;
            }
        }).flatMap(new Func1<BaseNetBean, Observable<JsonObject>>() {

            private LoginReq loginReq;

            @Override
            public Observable<JsonObject> call(BaseNetBean baseNetBean) {
                ArrayMap loginReq = new ArrayMap();
                loginReq.put("mobile", username);
                loginReq.put("password", psw);
                return mApiWrapper.loginJiu(loginReq);
            }
        });

        Subscription subscription = Observable.merge(anychat, jiu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        //  mApiWrapper.registerJiu(headers,new RegisterReq(username, psw, username, remoteId)) .subscribe(subscriber);

        mCompositeSubscription.add(subscription);
    }

}
