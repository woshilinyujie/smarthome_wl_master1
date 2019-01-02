package com.fbee.smarthome_wl.ui.subuser;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.DeteleChildUser;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/4/19.
 */

public class SubUserListPresenter extends BaseCommonPresenter<SubUserListContract.View> implements SubUserListContract.Presenter {

    public SubUserListPresenter(SubUserListContract.View view) {
        super(view);
    }

    //删除子用户请求
    @Override
    public void reqDeleteSubUser(DeteleChildUser body) {
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
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resDeleteSubUser(res);
                    }
                }
            }
        });

        Ums ums=getUms("MSG_CHILD_USER_DEL_REQ",body);
        Subscription subscription=mApiWrapper.deleteChildUser(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
}
