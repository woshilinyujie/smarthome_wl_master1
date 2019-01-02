package com.fbee.smarthome_wl.ui.videodoorlock;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.AMS;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/9/13.
 */

public class DoorLockVideoCallPresenter extends BaseCommonPresenter<DoorLockVideoCallContract.View> implements DoorLockVideoCallContract.Presenter {
    public DoorLockVideoCallPresenter(DoorLockVideoCallContract.View view) {
        super(view);
    }

    @Override
    public void reqQueryDevice(QueryDeviceuserlistReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        QueryDeviceUserResponse res = null;
                        if (jsonObj.has("body")) {
                            res = new Gson().fromJson(jsonObj.toString(), QueryDeviceUserResponse.class);
                        }
                        view.resQueryDevice(res);

                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_QUERY_REQ", body);
        Subscription subscription = mApiWrapper.getUserEquipmentlist(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
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
                        view.resAddDevice(res);
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_ADD_REQ", body);
        Subscription subscription = mApiWrapper.addDevices(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 删除网关请求
     *
     * @param body
     */
    @Override
    public void reqDeleteGateWay(DeleteDevicesReq body) {
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
                        BaseResponse res = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resDeleteGateWay(res);
                    }
                }
            }
        };

        Ums ums = getUms("MSG_DEVICE_DEL_REQ", body);
        Subscription subscription = mApiWrapper.deleteGateWay(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void reqAddToken(AddTokenReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("AMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        AddTokenResponse res = new Gson().fromJson(jsonObj.toString(), AddTokenResponse.class);
                        view.resAddToken(res);
                    }
                }
            }

        });
        AMS ams = getAMS("MSG_TOKEN_ADD_REQ", body);
        Subscription subscription = mApiWrapper.addToken(ams).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
}
