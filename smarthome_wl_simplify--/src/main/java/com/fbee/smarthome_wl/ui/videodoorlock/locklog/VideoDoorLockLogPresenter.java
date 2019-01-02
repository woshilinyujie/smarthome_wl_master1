package com.fbee.smarthome_wl.ui.videodoorlock.locklog;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.QueryVideoLockRecordReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.VideolockRecordResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.ResponseBody;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wl on 2017/9/12.
 */

public class VideoDoorLockLogPresenter extends BaseCommonPresenter<VideoDoorLockLogContract.view> implements VideoDoorLockLogContract.Presenter{
    public VideoDoorLockLogPresenter(VideoDoorLockLogContract.view view) {
        super(view);
    }

    @Override
    public void getVideoDoorLocklog(QueryVideoLockRecordReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if(!mCompositeSubscription.isUnsubscribed()){
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    VideolockRecordResponse res=new Gson().fromJson(jsonObj.toString(), VideolockRecordResponse.class);
                    view.responseDoorLog(res);
                }
            }
        });
        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_UNLOCK_RECORD_QUERY_REQ");
        header.setSeq_id(mSeqid.getAndIncrement()+"");
        Ums ums = new Ums();
        UMSBean umsbean=new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(body);
        ums.setUMS(umsbean);
        Subscription subscription=mApiWrapper.qureyRecordlist(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscriber);
    }

    @Override
    public void deleteDoorLocklog(QueryVideoLockRecordReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if(!mCompositeSubscription.isUnsubscribed()){
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                    view.responseDeleteDoor(res);
                }
            }
        });
        Ums ums = getUms("MSG_UNLOCK_RECORD_DEL_REQ", body);
        Subscription subscription=mApiWrapper.deleteRecordlist(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscriber);
    }
}
