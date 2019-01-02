package com.fbee.smarthome_wl.ui.main.equipment;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.request.AMS;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryGateWayReq;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryGateWayInfoResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.equipment
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/11 9:19
 */
public class EquipmentPresenter extends BaseCommonPresenter<EquipmentContract.View> implements EquipmentContract.Presenter{
    public EquipmentPresenter(EquipmentContract.View view) {
        super(view);
    }


    /**
     * 获取网关信息
     * @param req
     */
    @Override
    public void getGateWayInfo(QueryGateWayReq req, final String factorytype) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<List<QueryGateWayInfoResponse.BodyBean.DeviceListBean>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> list) {
                if (null != list) {
                    if(FactoryType.EQUES.equals(factorytype)){
                        view.equesDeviceListCallback(list);
                    }else if(FactoryType.GENERAL.equals(factorytype)){
                        view.getGateWayResult(list);
                    }
                }
            }
        });
        Ums ums = getUms("MSG_GATEWAY_QUERY_REQ", req);
        Subscription sub = mApiWrapper.seclectGatewayinfo(ums).map(new Func1<JsonObject, List<QueryGateWayInfoResponse.BodyBean.DeviceListBean>>() {
            @Override
            public List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> call(JsonObject jsonObject) {
                if(null != jsonObject){
                    JsonObject jsonObj = jsonObject.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return null;
                    QueryGateWayInfoResponse info = new Gson().fromJson(jsonObj.toString(), QueryGateWayInfoResponse.class);

                    List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> mVideoLockList = new ArrayList<QueryGateWayInfoResponse.BodyBean.DeviceListBean>();

                   if(info.getHeader().getHttp_code().equals("200")){
                       List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> mDevicelist = info.getBody().getDevice_list();
                       if(mDevicelist != null){
                           //过滤出wifi设备
                           for (int i = 0; i <mDevicelist.size() ; i++) {
                               if( mDevicelist.get(i).getVendor_name().equals(FactoryType.EQUES)){
                                   mVideoLockList.add(mDevicelist.get(i));
                               }else if(mDevicelist.get(i).getVendor_name().equals(FactoryType.GENERAL)){
                                   String devicetype = mDevicelist.get(i).getType();
                                   if("WonlyVideoLock".equals(devicetype)){
                                       mVideoLockList.add(mDevicelist.get(i));
                                   }

                               }
                           }

                       }

                   }
                    return mVideoLockList;
                }else{
                    return null;
                }
            }
        }).subscribe(subscriber);
        mCompositeSubscription.add(sub);


    }

    /**
     * 服务器设备删除
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
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_DEL_REQ", body);
        Subscription subscription = mApiWrapper.deleteDevices(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }


    /**
     * 请求token  当token验证失效时
     * @param body
     */
    @Override
    public void reqAddToken(AddTokenReq body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("AMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        AddTokenResponse res = new Gson().fromJson(jsonObj.toString(), AddTokenResponse.class);
                        view.resAddTokenCallback(res);
                    }
                }
            }

        });
        AMS ams = getAMS("MSG_TOKEN_ADD_REQ", body);
        Subscription subscription = mApiWrapper.addToken(ams).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }


}
