package com.fbee.smarthome_wl.ui.personaccount;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.request.UpdateUserinfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.MessgaeCode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/4/27.
 */

public class ModifyPersonAccountPresenter extends BaseCommonPresenter<ModifyPersonAccountContract.View> implements ModifyPersonAccountContract.Presenter {
    public ModifyPersonAccountPresenter(ModifyPersonAccountContract.View view) {
        super(view);
    }

    @Override
    public void reqModifyPersonAccount(UpdateUserinfoReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resModifyPersonAccount(res);
                    }
                }
            }

        });
        Ums ums =getUms("MSG_PERSONAL_DATA_UPDATE_REQ",body);
        Subscription subscription=mApiWrapper.updateUserInfo(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);

    }
    /**
     * 请求验证码
     */
    @Override
    public void sendMessageCode(SmsReq body) {

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse resCode = new Gson().fromJson(jsonObj.toString(), MessgaeCode.class);

                        view.resCode(resCode);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                view.resCodeFail();
            }
        });

        Ums ums=getUms("MSG_SMS_CODE_REQ",body);
        Subscription subscription=mApiWrapper.sendMessage(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);

    }

}
