package com.fbee.smarthome_wl.ui.videodoorlock;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/9/11.
 */

public class VideoDoorlockWifisPresenter extends BaseCommonPresenter<VideoDoorlockWifisContract.View> implements VideoDoorlockWifisContract.Presenter{
    public VideoDoorlockWifisPresenter(VideoDoorlockWifisContract.View view) {
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
                        view.resAddDevice(res);
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_ADD_REQ", body);
        Subscription subscription = mApiWrapper.addDevices(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
}
