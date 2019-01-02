package com.fbee.smarthome_wl.ui.videodoorlock.operationrecord;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.QueryVideoLockRecordReq;
import com.fbee.smarthome_wl.request.RecordReq;
import com.fbee.smarthome_wl.response.RecordResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wl on 2017/10/30.
 */

public class OperationRecordPresenter extends BaseCommonPresenter<OperationRecordContract.view> implements OperationRecordContract.Presenter {
    public OperationRecordPresenter(OperationRecordContract.view view) {
        super(view);
    }

    @Override
    public void getOperationRecord(RecordReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    RecordResponse res = new Gson().fromJson(jsonObj.toString(), RecordResponse.class);
                    view.responseRecord(res);
                }
            }
        });
        Ums ums = getUms("MSG_OPERATED_RECORD_QUERY_REQ", body);
        Subscription subscription=mApiWrapper.qureyRecordlist(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscriber);
    }
}
