package com.fbee.smarthome_wl.ui.usermanage;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by WLPC on 2017/6/15.
 */

public class UserManagePresenter extends BaseCommonPresenter<UserManageContract.View> implements UserManageContract.Presenter {
    public UserManagePresenter(UserManageContract.View view) {
        super(view);
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

    /**
     * 添加设备用户请求
     * @param body
     */
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
                        view.resAddDeviceUser(res);
                    }
                }
            }

        });
        Ums ums=getUms("MSG_DEVICE_USER_ADD_REQ",body);
        Subscription subscription=mApiWrapper.addDeviceUsre(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
}
