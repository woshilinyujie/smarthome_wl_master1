package com.fbee.smarthome_wl.ui.doorlock;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;

import java.util.Map;

import rx.Subscriber;
import rx.Subscription;

/**
 * @class name：com.fbee.smarthome_wl.activity.doorlock
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/2 15:41
 */
public class DoorlockPresenter extends BaseCommonPresenter<DoorLockContract.View>  implements DoorLockContract.Presenter {
    public DoorlockPresenter(DoorLockContract.View view) {
        super(view);
    }


    /**
     * 获取电量信息
     * @param prams
     */
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

        Subscription subscription =  mApiWrapper.getDoorInfo(prams)
                .subscribe(subscriber);

        mCompositeSubscription.add(subscription);
    }



}
