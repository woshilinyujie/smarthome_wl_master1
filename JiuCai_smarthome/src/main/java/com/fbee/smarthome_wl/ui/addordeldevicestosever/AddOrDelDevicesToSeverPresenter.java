package com.fbee.smarthome_wl.ui.addordeldevicestosever;

import android.support.v4.util.ArrayMap;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.LoginReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseNetBean;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceListResponse;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
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
 * Created by WLPC on 2017/5/5.
 */

public class AddOrDelDevicesToSeverPresenter extends BaseCommonPresenter<AddOrDelDevicesToSeverContract.View> implements AddOrDelDevicesToSeverContract.Presenter {

    public AddOrDelDevicesToSeverPresenter(AddOrDelDevicesToSeverContract.View view) {
        super(view);
    }

    /**
     * 添加设备到服务器
     *
     * @param body
     */
    @Override
    public void reqAddDevices(AddDevicesReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resAddDevices(res);
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_ADD_REQ", body);
        Subscription subscription = mApiWrapper.addDevices(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /***
     * 查询猫眼是否已经绑定门锁
     *
     * @param bodyEntity
     */
    @Override
    public void queryDevices(QueryDevicesListInfo bodyEntity) {
        Subscriber subscriber = new Subscriber<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        QueryDeviceListResponse res = new Gson().fromJson(jsonObj.toString(), QueryDeviceListResponse.class);
                        view.queryDevicesResult(res);
                    }
                }
            }
        };

        Ums ums = getUms("MSG_DEVICE_QUERY_REQ", bodyEntity);
        Subscription subscription = mApiWrapper.queryDevicesList(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 删除设备到服务器
     *
     * @param body
     */
    @Override
    public void reqDeleteDevices(DeleteDevicesReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resDeleteDevices(res);
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_DEL_REQ", body);
        Subscription subscription = mApiWrapper.deleteDevices(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 查询网关设备列表
     *
     * @param bodyEntity
     */
    @Override
    public void reqGateWayInfo(QueryDevicesListInfo bodyEntity) {
        Subscriber subscriber = new Subscriber<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        QueryGateWayInfoReq res = new Gson().fromJson(jsonObj.toString(), QueryGateWayInfoReq.class);
                        view.resReqGateWayInfo(res);
                    }
                }
            }
        };

        Ums ums = getUms("MSG_GATEWAY_QUERY_REQ", bodyEntity);
        Subscription subscription = mApiWrapper.queryDevicesList(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 九彩登录注册，预留代码后面有变动
     */

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
