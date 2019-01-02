package com.fbee.smarthome_wl.ui.main;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.DeviceListInfo;
import com.fbee.smarthome_wl.bean.Pus;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.dbutils.UserDbUtil;
import com.fbee.smarthome_wl.greendao.User;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.PusBodyBean;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.response.UpdataApkResponse;
import com.fbee.smarthome_wl.response.UpdateModel;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.zllctl.DeviceInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * @class name：com.fbee.smarthome_wl.activity.mian
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/4 11:16
 */
public class MainPresenter extends BaseCommonPresenter<MainContract.View> implements MainContract.Presenter {
    public MainPresenter(MainContract.View view) {
        super(view);
    }


    @Override
    public void requestDeviceList(final String username, final String psw) {
        HashMap paramsMap = new HashMap<String, String>();
        paramsMap.put("userNo", username);
        paramsMap.put("userPass", psw);
        paramsMap.put("uid", "");// 设备uid(可选)
        paramsMap.put("aID", "");//设备aID(可选)

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JsonObject obj) {
            }

        });

        Observable<DeviceListInfo> observableList = mApiWrapper.getDeviceList(paramsMap);

        Subscription subscription = observableList.flatMap(new Func1<DeviceListInfo, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(DeviceListInfo deviceListInfo) {

                List<DeviceListInfo.ItemBean> items = deviceListInfo.getItem();
                //设备白名单
                List<Integer> uids = new ArrayList<Integer>();
                if (null != items) {
                    for (int i = 0; i < items.size(); i++) {
                        uids.add(items.get(i).getUid());
                    }
                }
                //本地设备列表
                List<DeviceInfo> devices = AppContext.getmOurDevices();
                List<Integer> deviceuids = new ArrayList<Integer>();

                if (null != devices) {
                    for (int i = 0; i < devices.size(); i++) {
                        if (uids.contains(devices.get(i).getUId())) {
                            continue;
                        }
                        deviceuids.add(devices.get(i).getUId());
                    }
                }

                return Observable.from(deviceuids);
            }
        }).flatMap(new Func1<Integer, Observable<JsonObject>>() {
            @Override
            public Observable<JsonObject> call(Integer uid) {

                HashMap paramsMap = new HashMap<String, String>();
                paramsMap.put("userNo", username);
                paramsMap.put("userPass", psw);
                paramsMap.put("uid", uid.toString());
                paramsMap.put("pushoption", "1");
                paramsMap.put("AppSN", "3");
                paramsMap.put("AppTAG", "");
                paramsMap.put("AppLan", "0");
                paramsMap.put("aID", "");//设备aID(可选)

                return mApiWrapper.addDevice(paramsMap);
            }
        }).subscribe(subscriber);

        mCompositeSubscription.add(subscription);
    }

    /**
     * 升级接口，是否有新版本
     */
    @Override
    public void updateVersion(PusBodyBean bodyEntity) {

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
                        JsonObject jsonObj = json.getAsJsonObject("PUS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        UpdateModel updateinfo = new Gson().fromJson(jsonObj.toString(), UpdateModel.class);
                        view.resUpdate(updateinfo);

                    }
                }
            }
        };
        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        header.setSeq_id("1");
        Pus pus = new Pus();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyEntity);
        pus.setPUS(umsbean);
        Subscription subscription = mApiWrapper.updateVersion(pus)
                .subscribe(subscriber);
        mCompositeSubscription.add(subscription);

    }


    /**
     * 网关升级查询
     *
     * @param bodyEntity
     */
    @Override
    public void updateGateWay(PusBodyBean bodyEntity) {
        Subscriber subscriber = new Subscriber<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(JsonObject json) {
                try {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("PUS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        UpdataApkResponse updateinfo = new Gson().fromJson(jsonObj.toString(), UpdataApkResponse.class);
                        view.resGwCallback(updateinfo);

                    }
                } catch (Exception e) {

                }
            }
        };

        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        header.setSeq_id("1");
        Pus pus = new Pus();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyEntity);
        pus.setPUS(umsbean);


        Subscription subscription = mApiWrapper.updateVersion(pus).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 查询设备的用户----5.19接口，修改为查询设备信息
     */
    @Override
    public void getUserEquipmentlist(final int uid, QueryDeviceuserlistReq body) {

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
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
                        JsonObject header = jsonObj.getAsJsonObject("header");
                        String code = header.get("http_code").getAsString();
                        if (jsonObj.has("body")) {
                            QueryDeviceUserResponse info = new Gson().fromJson(jsonObj.toString(), QueryDeviceUserResponse.class);
                            if (info != null && info.getBody() != null) {
                                if (info.getBody().getDevice_user_list() != null) {
                                    for (int i = 0; i < info.getBody().getDevice_user_list().size(); i++) {

                                        QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean = info.getBody().getDevice_user_list().get(i);
                                        User newUser = new User(Long.parseLong(deviceUserListBean.getId()), (int) uid, deviceUserListBean.getNote());
                                        UserDbUtil.getIns().insert(newUser);
                                    }
                                    AppContext.getMap().put(String.valueOf(uid), info.getBody().getDevice_user_list());
                                }
                            }


                        }

                    }
                }
            }
        });

        UMSBean.HeaderBean headerBean = new UMSBean.HeaderBean();
        headerBean.setApi_version("1.0");
        headerBean.setMessage_type("MSG_DEVICE_QUERY_REQ");
        headerBean.setSeq_id(String.valueOf(uid));
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(headerBean);
        umsbean.setBody(body);
        ums.setUMS(umsbean);
        Subscription subscription = mApiWrapper.getUserEquipmentlist(ums)
                .subscribe(subscriber);
        mCompositeSubscription.add(subscription);


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
                        if (res.getHeader().getMessage_type().equals("200")) {
                        }
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_ADD_REQ", body);
        Subscription subscription = mApiWrapper.addDevices(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
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
                        BaseResponse res = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        view.resAddDeviceUser(res);
                    }
                }
            }

        });
        Ums ums = getUms("MSG_DEVICE_USER_ADD_REQ", body);
        Subscription subscription = mApiWrapper.addDeviceUsre(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void reqDestroyUser() {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                EZOpenSDK.getInstance().logout();
            }

        });
        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_USER_LOGOUT_REQ");
        header.setSeq_id(mSeqid.getAndIncrement() + "");
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        ums.setUMS(umsbean);
        Subscription subscription = mApiWrapper.destroyUser(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }
}