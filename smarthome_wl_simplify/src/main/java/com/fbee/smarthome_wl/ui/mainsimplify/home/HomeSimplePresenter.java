package com.fbee.smarthome_wl.ui.mainsimplify.home;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * @class name：com.fbee.smarthome_wl.ui.mainsimplify.home
 * @anthor create by Zhaoli.Wang
 * @time 2017/11/8 13:32
 */
public class HomeSimplePresenter extends BaseCommonPresenter<HomeSimpleContract.View> implements HomeSimpleContract.Presenter{


    public HomeSimplePresenter(HomeSimpleContract.View view) {
        super(view);
    }

    @Override
    public void loginFbee(final String username, final String psw) {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
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
     * 添加新网关到服务器
     * @param bodyBean
     */
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
