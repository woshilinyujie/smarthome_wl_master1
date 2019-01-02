package com.fbee.smarthome_wl.ui.doorlock;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.QueryDeleteDoorlockAlarm;
import com.fbee.smarthome_wl.request.QueryDoorlockAlarm;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.DoorLockAlarmResponse;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;

/**
 * @class name：com.fbee.smarthome_wl.activity.doorlock
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/2 15:41
 */
public class DoorlockPresenter extends BaseCommonPresenter<DoorLockContract.View> implements DoorLockContract.Presenter {
    public DoorlockPresenter(DoorLockContract.View view) {
        super(view);
    }


    /**
     * 获取电量信息
     *
     * @param prams
     */
//    @Override
//    public void getStatus(Map prams) {
//        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<DoorlockpowerInfo>() {
//            @Override
//            public void onError(Throwable mHttpExceptionBean) {
//                super.onError(mHttpExceptionBean);
//            }
//
//            @Override
//            public void onNext(DoorlockpowerInfo info) {
//                view.responseDoorInfo(info);
//            }
//
//        });
//
//        Subscription subscription = mApiWrapper.getDoorInfo(prams)
//                .subscribe(subscriber);
//
//        mCompositeSubscription.add(subscription);
//    }

    @Override
    public void getStatus(Map prams,final String type) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<DoorlockpowerInfo>() {
            @Override
            public void onError(Throwable mHttpExceptionBean) {
                super.onError(mHttpExceptionBean);
            }

            @Override
            public void onNext(DoorlockpowerInfo info) {
                if("6".equals(type)){
                    view.responseDoorInfo(info);
                }else if("13".equals(type)){
                    view.responseDeviceModel(info);
                }

            }

        });

        Subscription subscription = mApiWrapper.getDoorInfo(prams)
                .subscribe(subscriber);

        mCompositeSubscription.add(subscription);

    }

    @Override
    public void getDoorlockAlarm(QueryDoorlockAlarm bodyBean) {

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
                    DoorLockAlarmResponse info = new Gson().fromJson(jsonObj.toString(), DoorLockAlarmResponse.class);
                    view.responseDoorAlarm(info);

                }
            }
        });

        Ums ums = getUms("MSG_ALARM_DATA_QUERY_REQ", bodyBean);
        Subscription sub = mApiWrapper.getDoorlockAlarm(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }

    @Override
    public void deleteDoorlockAlarm(QueryDeleteDoorlockAlarm body) {
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
                    view.responseDeleteDoorAlarm(info);

                }
            }
        });

        Ums ums = getUms("MSG_ALARM_DATA_DEL_REQ", body);
        Subscription sub = mApiWrapper.deleteDoorAlarm(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }


}
