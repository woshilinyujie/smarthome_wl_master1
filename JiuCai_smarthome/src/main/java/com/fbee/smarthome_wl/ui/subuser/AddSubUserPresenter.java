package com.fbee.smarthome_wl.ui.subuser;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.AddChildUserReq;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/4/20.
 */

public class AddSubUserPresenter extends BaseCommonPresenter<AddSubUserContract.View> implements AddSubUserContract.Presenter {


    public AddSubUserPresenter(AddSubUserContract.View view) {
        super(view);
    }


    //添加子用户请求
    @Override
    public void reqAddSubUser(AddChildUserReq body) {

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resAddSubUser(res);
                    }
                }
            }
        });

        Ums ums=getUms("MSG_CHILD_USER_ADD_REQ",body);
        Subscription subscription=mApiWrapper.addChildUser(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
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
}
