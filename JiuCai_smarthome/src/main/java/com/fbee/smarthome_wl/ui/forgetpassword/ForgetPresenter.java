package com.fbee.smarthome_wl.ui.forgetpassword;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.ForgetPwd;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/4/17.
 */

public class ForgetPresenter extends BaseCommonPresenter<ForgetContract.View> implements ForgetContract.Presenter {
    public ForgetPresenter(ForgetContract.View view) {
        super(view);
    }

    @Override
    public void sendMessageCode(SmsReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                //view.resCodeFail();
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

    @Override
    public void findPassWord(ForgetPwd body) {
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
                    view.resForgetPassWord(info);
                }
            }
        });

        Ums ums= getUms("MSG_PASSWD_FORGET_REQ",body);
        Subscription subscription = mApiWrapper.forgetPassWord(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
}
