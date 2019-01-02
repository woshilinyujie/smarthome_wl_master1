package com.fbee.smarthome_wl.ui.login;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.RegReq;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * @class name：com.fbee.smarthome_wl.ui.login
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/31 10:33
 */
public class RegisterPresenter extends BaseCommonPresenter<RegisterContract.View> implements  RegisterContract.Presenter{


    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    /**
     * 请求验证码
     */
    @Override
    public void sendMessageCode(SmsReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                view.resCodeFail();
            }
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        CodeResponse resCode = new Gson().fromJson(jsonObj.toString(), CodeResponse.class);

                        view.resCode(resCode);
                    }
                }
            }
        });


        Ums ums= getUms("MSG_SMS_CODE_REQ",body);
        Subscription subscription=mApiWrapper.sendMessage(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);

    }

    /**
     * 注册
     */
    @Override
    public void register(RegReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JsonObject json) {
                if (null != json) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0){
                        ToastUtils.showShort("数据返回错误!");
                        return;
                    }
                    BaseResponse info = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                    view.resRegister(info);
                }
            }
        });
        Ums ums = getUms("MSG_USER_REG_REQ",body);
        Subscription subscription = mApiWrapper.register(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);

    }


}
