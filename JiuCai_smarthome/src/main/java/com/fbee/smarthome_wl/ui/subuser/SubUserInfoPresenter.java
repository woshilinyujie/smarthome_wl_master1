package com.fbee.smarthome_wl.ui.subuser;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QuerySubUserInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QuerySubUserInfoResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/4/19.
 */

public class SubUserInfoPresenter extends BaseCommonPresenter<SubUserInfoContract.View> implements SubUserInfoContract.Presenter {


    public SubUserInfoPresenter(SubUserInfoContract.View view) {
        super(view);
    }

    /**
     * 请求用户信息
     * @param body
     */
    @Override
    public void reqQuerySubUser(QuerySubUserInfoReq body) {

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
                        QuerySubUserInfoResponse res=new Gson().fromJson(jsonObj.toString(), QuerySubUserInfoResponse.class);
                        view.resQuerySubUser(res);
                    }
                }
            }
        });

        Ums ums=getUms("MSG_CHILD_USER_QUERY_REQ",body);
        Subscription subscription=mApiWrapper.querySubUserInfo(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 删除网关请求
     * @param body
     * @param id
     */
    @Override
    public void reqDeleteGateWay(DeleteDevicesReq body, final int postion) {
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
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resDeleteGateWay(res,postion);
                    }
                }
            }
        };

        Ums ums = getUms("MSG_DEVICE_DEL_REQ", body);
        Subscription subscription=mApiWrapper.deleteGateWay(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
}
