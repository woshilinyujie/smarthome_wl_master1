package com.fbee.smarthome_wl.ui.modifypassword;

import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.ModifyPasswordReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/4/17.
 */

public class Modifypresenter extends BaseCommonPresenter<ModifyContract.View> implements ModifyContract.Presenter {
    public Modifypresenter(ModifyContract.View view) {
        super(view);
    }

    @Override
    public void sendModifyPassCode(ModifyPasswordReq body) {
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
                        BaseResponse  res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resModifyPass(res);
                    }
                }
            }
        };

        Ums ums=getUms("MSG_PASSWD_CHANGE_REQ",body);
        Subscription subscription=mApiWrapper.modifyPassWord(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

}
