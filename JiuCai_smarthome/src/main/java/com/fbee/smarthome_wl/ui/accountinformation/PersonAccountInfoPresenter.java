package com.fbee.smarthome_wl.ui.accountinformation;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.UpdateUserinfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/4/27.
 */

public class PersonAccountInfoPresenter extends BaseCommonPresenter<PersonAccountInfoContract.View> implements PersonAccountInfoContract.Presenter {
    public PersonAccountInfoPresenter(PersonAccountInfoContract.View view) {
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
        Ums ums=getUms("MSG_PERSONAL_DATA_UPDATE_REQ",body);
        Subscription subscription=mApiWrapper.updateUserInfo(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void reqDestroyUser() {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                EZOpenSDK.getInstance().logout();
            }

        });
        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_USER_LOGOUT_REQ");
        header.setSeq_id(mSeqid.getAndIncrement()+"");
        Ums ums = new Ums();
        UMSBean umsbean=new UMSBean();
        umsbean.setHeader(header);
        ums.setUMS(umsbean);
        Subscription subscription=mApiWrapper.destroyUser(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);

    }
}
