package com.fbee.smarthome_wl.ui.usermanage;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WLPC on 2017/4/24.
 */

public class DeviceAssociateUserPresenter extends BaseCommonPresenter<DeviceAssociateUserContract.View> implements DeviceAssociateUserContract.Presenter {
    public DeviceAssociateUserPresenter(DeviceAssociateUserContract.View view) {
        super(view);
    }

    @Override
    public void reqAddDeviceUser(AddDeviceUser body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        if(res.getHeader().getSeq_id().equals("1003")) {
                            view.resAddDeviceUser(res);
                        }
                    }
                }
            }

        });

        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_DEVICE_USER_ADD_REQ");
        header.setSeq_id("1003");
        Ums ums = new Ums();
        UMSBean umsbean=new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(body);
        ums.setUMS(umsbean);

        Subscription subscription=mApiWrapper.addDeviceUsre(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void reqModifyDeviceUser(AddDeviceUser body1,AddDeviceUser body2) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resModifyDeviceUser(res);
                    }
                }
            }

        });

        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_DEVICE_USER_ADD_REQ");
        header.setSeq_id("1001");
        Ums ums = new Ums();
        UMSBean umsbean=new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(body1);
        ums.setUMS(umsbean);


        UMSBean.HeaderBean header01 = new UMSBean.HeaderBean();
        header01.setApi_version("1.0");
        header01.setMessage_type("MSG_DEVICE_USER_ADD_REQ");
        header01.setSeq_id("1002");
        Ums ums01 = new Ums();
        UMSBean umsbean01=new UMSBean();
        umsbean01.setHeader(header01);
        umsbean01.setBody(body2);
        ums01.setUMS(umsbean01);
        Observable<JsonObject> record=mApiWrapper.addDeviceUsre(ums);
        Observable<JsonObject> powerRecord=mApiWrapper.addDeviceUsre(ums01);


        Subscription subscription = Observable.merge(record, powerRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        mCompositeSubscription.add(subscription);

    }

    /**
     * 查询设备信息
     * @param body
     */
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
                        QueryDeviceUserResponse res=null;
                        if(jsonObj.has("body")){
                            res=new Gson().fromJson(jsonObj.toString(), QueryDeviceUserResponse.class);
                        }
                        view.resQueryDevice(res);

                    }
                }
            }

        });
        Ums ums=getUms("MSG_DEVICE_QUERY_REQ",body);
        Subscription subscription=mApiWrapper.getUserEquipmentlist(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
/*

    */
/**
     * 删除设备用户
     * @param body
     *//*

    @Override
    public void reqDelDeviceUser(DeleteDeviceUserInfo body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resDelDeviceUser(res);
                    }
                }
            }

        });
        Ums ums=getUms("MSG_DEVICE_USER_DEL_REQ",body);
        Subscription subscription=mApiWrapper.deleteDeviceUsre(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
*/





}
