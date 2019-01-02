package com.fbee.smarthome_wl.ui.main.homepage;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.request.HomePageInfoReq;
import com.fbee.smarthome_wl.request.UpdateUserConfigurationReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.homepage
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/28 13:43
 */
public class HomePresenter extends BaseCommonPresenter<HomeContract.View> implements HomeContract.Presenter{
    public HomePresenter(HomeContract.View view) {
        super(view);
    }

    @Override
    public void loginFbee(final String username,final String psw) {
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
                    if (jsonObj.has("body")){
                        HomePageResponse info = new Gson().fromJson(jsonObj.toString(), HomePageResponse.class);
                        view.userConfigResult(info);
                    }else{
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

        Subscription sub = mApiWrapper.updateHomeConfiguration(getUms("MSG_USER_CONFIG_UPDATE_REQ",body)).subscribe(subscriber);
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


        Ums ums =  getUms("MSG_GATEWAY_ADD_REQ",bodyBean);
        Subscription sub = mApiWrapper.addGateWay(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);

    }


}
