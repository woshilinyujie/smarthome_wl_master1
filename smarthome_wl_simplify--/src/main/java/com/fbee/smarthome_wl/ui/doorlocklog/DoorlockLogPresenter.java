package com.fbee.smarthome_wl.ui.doorlocklog;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.greendao.Doorlockrecord;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.DoorAlarmRecordinfo;
import com.fbee.smarthome_wl.response.DoorRecordInfo;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.common.AppContext.getMap;

/**
 * @class name：com.fbee.smarthome_wl.activity.doorlocklog
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/4 9:12
 */
public class DoorlockLogPresenter extends BaseCommonPresenter<DoorLockLogContract.View> implements DoorLockLogContract.Presenter {
    private List<Doorlockrecord>  mlist ;
    public DoorlockLogPresenter(DoorLockLogContract.View view) {
        super(view);
    }


    @Override
    public void getDoorInfo(Map prams,final int mdeviceUid,final String mdeviceName,final String ieee) {
        {
            final DoorlockinfoParse parse = new DoorlockinfoParse(this);
            mlist = new ArrayList<Doorlockrecord>();
            //开锁
            Observable<DoorRecordInfo> doorlockRecord = mApiWrapper.getDoorRecord(prams);
            //电量
            Map powerprams = new HashMap();
            powerprams.putAll(prams);
            powerprams.put("type","6");
            Observable<DoorlockpowerInfo> powerRecord = mApiWrapper.getDoorInfo(powerprams);
            //报警
            Map alarmprams = new HashMap();
            alarmprams.putAll(prams);
            alarmprams.put("type","9");
            Observable<DoorAlarmRecordinfo> aramRecord = mApiWrapper.getAlarmRecord(alarmprams);

            Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<Object>() {
                @Override
                public void onError(Throwable mHttpExceptionBean) {
                    super.onError(mHttpExceptionBean);
                    view.resDatafail();
                }
                @Override
                public void onNext(Object obj) {
                    try {
                        //开锁
                        if (obj instanceof DoorRecordInfo) {
                            List<Doorlockrecord> pList = parse.getDoorLockRecord((DoorRecordInfo) obj, mdeviceUid, mdeviceName,ieee);
                            if(pList.size() >0 ){
                                mlist.addAll(pList);
                            }
                        }
                        //电量
                        else if (obj instanceof DoorlockpowerInfo) {
                            List<Doorlockrecord> pList = parse.getDoorLockPowerRecord((DoorlockpowerInfo) obj, mdeviceUid);
                            if(pList.size() > 0){
                                mlist.addAll(pList);
                            }

                        }
                        //报警
                        else if(obj instanceof DoorAlarmRecordinfo){
                            List<Doorlockrecord> pList =parse.getDoorLockArmRecord((DoorAlarmRecordinfo)obj,mdeviceUid);
                            if(pList.size() > 0){
                                mlist.addAll(pList);
                            }
                        }
                    }catch (Exception e){

                    }

                }

                @Override
                public void onCompleted() {
                    super.onCompleted();
                    view.resDoorInfoData(mlist);
                }


            });

            Subscription subscription = Observable.merge(doorlockRecord, powerRecord, aramRecord)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);

            mCompositeSubscription.add(subscription);


        }
    }

    /**
     * 删除门锁记录
     * @param params
     */
    @Override
    public void deteleDoorinfo(Map params) {
        //开锁记录
        Observable<JsonObject> record = mApiWrapper.deteleDoorRecord(params);
        //电量
        Map powerprams = new HashMap();
        powerprams.putAll(params);
        powerprams.put("type","6");
        Observable<JsonObject> powerRecord = mApiWrapper.deteleDoorRecord(powerprams);
        //警报
        Map alarmprams = new HashMap();
        alarmprams.putAll(params);
        alarmprams.put("type","9");
        Observable<JsonObject> aramRecord = mApiWrapper.deteleDoorRecord(alarmprams);

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable mHttpExceptionBean) {
                super.onError(mHttpExceptionBean);
                view.deteleFail();
            }

            @Override
            public void onNext(JsonObject info) {

            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                view.deteleSuccess();
            }

        });
        Subscription subscription = Observable.merge(record, powerRecord, aramRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        mCompositeSubscription.add(subscription);
    }

    @Override
    public void reqAddDeviceUser(final AddDeviceUser body) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        if("200".equals(res.getHeader().getHttp_code())){
                            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums= getMap().get(body.getUuid());
                            if(userNums==null){
                                userNums=new ArrayList<>();
                                QueryDeviceUserResponse.BodyBean.DeviceUserListBean info = new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                                info.setId(body.getDevice_user().getId());
                                info.setNote(body.getDevice_user().getNote());
                                /*List<String> without_notice_user_list=new ArrayList<>();
                                info.setWithout_notice_user_list(without_notice_user_list);*/
                                userNums.add(info);
                                AppContext.getMap().put(body.getUuid(), userNums);

                            }else {
                                boolean tag=false;
                                for (int i = 0; i <userNums.size() ; i++) {
                                    if(userNums.get(i).getId().equals(body.getDevice_user().getId())){
                                        tag=true;
                                        break;
                                    }
                                }
                                if(!tag){
                                    QueryDeviceUserResponse.BodyBean.DeviceUserListBean info = new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                                    info.setId(body.getDevice_user().getId());
                                    info.setNote(body.getDevice_user().getNote());
                                    /*List<String> without_notice_user_list=new ArrayList<>();
                                    info.setWithout_notice_user_list(without_notice_user_list);*/
                                    userNums.add(info);
                                }
                            }

                        }else {
                            ToastUtils.showShort(RequestCode.getRequestCode(res.getHeader().getReturn_string()));
                        }
                    }
                }
            }

        });
        Ums ums=getUms("MSG_DEVICE_USER_ADD_REQ",body);
        Subscription subscription=mApiWrapper.addDeviceUsre(ums).subscribe(subscriber);
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
}
