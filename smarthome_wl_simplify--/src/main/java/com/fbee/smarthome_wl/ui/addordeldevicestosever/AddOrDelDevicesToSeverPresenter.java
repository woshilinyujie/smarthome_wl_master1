package com.fbee.smarthome_wl.ui.addordeldevicestosever;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceListResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/5/5.
 */

public class AddOrDelDevicesToSeverPresenter extends BaseCommonPresenter<AddOrDelDevicesToSeverContract.View> implements AddOrDelDevicesToSeverContract.Presenter {

    public AddOrDelDevicesToSeverPresenter(AddOrDelDevicesToSeverContract.View view) {
        super(view);
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
                        view.resAddDevices(res);
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_ADD_REQ", body);
        Subscription subscription = mApiWrapper.addDevices(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /***
     * 查询猫眼是否已经绑定门锁
     *
     * @param bodyEntity
     */
    @Override
    public void queryDevices(QueryDevicesListInfo bodyEntity) {
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
                        QueryDeviceListResponse res = new Gson().fromJson(jsonObj.toString(), QueryDeviceListResponse.class);
                        view.queryDevicesResult(res);
                    }
                }
            }
        };

        Ums ums = getUms("MSG_DEVICE_QUERY_REQ", bodyEntity);
        Subscription subscription = mApiWrapper.queryDevicesList(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 删除设备到服务器
     *
     * @param body
     */
    @Override
    public void reqDeleteDevices(DeleteDevicesReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resDeleteDevices(res);
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_DEL_REQ", body);
        Subscription subscription = mApiWrapper.deleteDevices(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 查询网关设备列表
     * @param bodyEntity
     */
    @Override
    public void reqGateWayInfo(QueryDevicesListInfo bodyEntity) {
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
                        QueryGateWayInfoReq res = new Gson().fromJson(jsonObj.toString(), QueryGateWayInfoReq.class);
                        view.resReqGateWayInfo(res);
                    }
                }
            }
        };

        Ums ums = getUms("MSG_GATEWAY_QUERY_REQ", bodyEntity);
        Subscription subscription = mApiWrapper.queryDevicesList(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

}
