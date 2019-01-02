package com.fbee.smarthome_wl.ui.gateway;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.request.DeleteGateWayReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/4/19.
 */

public class GateWayManagerPresenter extends BaseCommonPresenter<GateWayManagerContract.View> implements GateWayManagerContract.Presenter {

    public GateWayManagerPresenter(GateWayManagerContract.View view) {
        super(view);
    }

    /**
     * 更新添加网关请求
     * @param body
     * @param username
     */
    @Override
    public void reqUpdateGateWay(AddGateWayReq body, final String username) {


        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resAddGateWay(res,username);
                    }
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
            @Override
            public void onCompleted() {
            }
        });



        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_GATEWAY_ADD_REQ");
        header.setSeq_id(mSeqid.getAndIncrement()+"");
        Ums ums = new Ums();
        UMSBean umsbean=new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(body);
        ums.setUMS(umsbean);
        Subscription subscription=mApiWrapper.addGateWay(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 删除网关请求
     * @param body
     */
    @Override
    public void reqDeleteGateWay(DeleteGateWayReq body) {

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resDeleteGateWay(res);
                    }
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
            @Override
            public void onCompleted() {
            }
        });
        Ums ums =getUms("MSG_GATEWAY_DEL_REQ",body);
        Subscription subscription=mApiWrapper.deleteGateWay(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }



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
                        QueryGateWayInfoReq res=new Gson().fromJson(jsonObj.toString(), QueryGateWayInfoReq.class);
                        view.resReqGateWayInfo(res);
                    }
                }
            }
        };

        Ums ums = getUms("MSG_GATEWAY_QUERY_REQ",bodyEntity);
        Subscription subscription=mApiWrapper.queryDevicesList(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }





    @Override
    public void loginFbee(final String username, final String psw) {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
//                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                //释放资源
                mSerial.releaseSource();
                //登录
                int ret = mSerial.connectRemoteZll(username, psw);

                subscriber.onNext(ret);
                subscriber.onCompleted();
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(Integer ret) {
                        view.loginFbeeResult(ret);

                    }
                });

        mCompositeSubscription.add(sub);
    }
}
