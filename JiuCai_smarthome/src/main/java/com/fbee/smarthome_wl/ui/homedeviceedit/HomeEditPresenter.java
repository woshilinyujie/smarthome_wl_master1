package com.fbee.smarthome_wl.ui.homedeviceedit;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.request.UpdateUserConfigurationReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * @class name：com.fbee.smarthome_wl.ui.homedeviceedit
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/20 9:57
 */
public class HomeEditPresenter extends BaseCommonPresenter<HomeEditContract.View> implements HomeEditContract.Presenter {

    public HomeEditPresenter(HomeEditContract.View view) {
        super(view);
    }


    @Override
    public void setUserConfig(UpdateUserConfigurationReq body) {
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
                    view.setCallBack(info);
                }
            }
        });

        Subscription sub = mApiWrapper.updateHomeConfiguration(getUms("MSG_USER_CONFIG_UPDATE_REQ",body)).subscribe(subscriber);
        mCompositeSubscription.add(sub);

    }


}
