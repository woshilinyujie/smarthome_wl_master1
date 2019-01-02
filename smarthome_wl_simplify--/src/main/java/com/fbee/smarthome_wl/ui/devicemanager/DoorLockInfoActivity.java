package com.fbee.smarthome_wl.ui.devicemanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.event.BindDevSenceEvent;
import com.fbee.smarthome_wl.event.BindDeviceInfoEvent;
import com.fbee.smarthome_wl.event.BindSenceInfoEvent;
import com.fbee.smarthome_wl.event.ChangeDevicenameEvent;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.DoorLockAlarmResponse;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockContract;
import com.fbee.smarthome_wl.ui.doorlock.DoorlockPresenter;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.DateUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.Serial;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.common.AppContext.getmOurDevices;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

public class DoorLockInfoActivity extends BaseActivity<DoorLockContract.Presenter> implements DoorLockContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RelativeLayout linearUpdateDevName;
    private TextView doorlockNameInfo;
    private ImageView doorlockPowerInfo;
    private TextView doorlockIsonlineInfo;
    private TextView doorlockUuidInfo;
    private TextView doorlockIeeeInfo;
    private TextView doorlockModelSoftVer;
    private RelativeLayout rlPower;
    private RelativeLayout rlDeviceModel;
    private TextView doorlockModelInfo;
    private RelativeLayout rlDeviceCode;
    private TextView doorlockCode;
    private RelativeLayout rlScencePanel;
    private TextView tvScencePanel;
    private TextView tvDeviceName;



    private DeviceInfo deviceInfo;
    private int mDeviceUid;
    private String mDeviceName;
    private byte mDeviceStatus;
    private byte[] mDeviceIeee;
    private String mUserName;
    private String mPassword;
    private Long mStartTime;
    private Long mEndTime;
    private AlertDialog alertDialog;
    private String mDeviceSnid;
    private Serial serial;
    private Subscription mSubscription1;
    private int bindTag;
    private int bindSenceUid=-1;
    private int[] bindDeviceUids;
    private int bindDeviceUid=-1;
    private DeviceInfo devInfo;
    private List<SenceInfo>  datas;
    private DeviceInfo bindDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_lock_info);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        linearUpdateDevName = (RelativeLayout) findViewById(R.id.linear_updateDevName);
        doorlockNameInfo = (TextView) findViewById(R.id.doorlock_Name_info);
        doorlockPowerInfo = (ImageView) findViewById(R.id.doorlock_power_info);
        doorlockIsonlineInfo = (TextView) findViewById(R.id.doorlock_isonline_info);
        doorlockUuidInfo = (TextView) findViewById(R.id.doorlock_duuid_info);
        doorlockIeeeInfo = (TextView) findViewById(R.id.doorlock_ieee_info);
        doorlockModelSoftVer = (TextView) findViewById(R.id.doorlock_model_soft_ver);
        rlPower = (RelativeLayout) findViewById(R.id.rl_power);

        rlDeviceModel = (RelativeLayout) findViewById(R.id.rl_device_model);
        doorlockModelInfo = (TextView) findViewById(R.id.doorlock_model_info);
        rlDeviceCode = (RelativeLayout) findViewById(R.id.rl_device_code);
        doorlockCode = (TextView) findViewById(R.id.doorlock_code);

        rlScencePanel = (RelativeLayout) findViewById(R.id.rl_scence_panel);
        tvScencePanel = (TextView) findViewById(R.id.tv_scence_panel);
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);

    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new DoorlockPresenter(this));
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("设备信息");
        linearUpdateDevName.setOnClickListener(this);
        mDeviceUid =  getIntent().getIntExtra("uuid",-1);
        if(mDeviceUid==-1)return;
        List<DeviceInfo> deviceList=AppContext.getmOurDevices();
        for (int i = 0; i <deviceList.size() ; i++) {
            if(mDeviceUid==deviceList.get(i).getUId()){
                deviceInfo=deviceList.get(i);
            }
        }
        if(deviceInfo==null)return;
        mDeviceName = deviceInfo.getDeviceName();
        mDeviceStatus=deviceInfo.getDeviceStatus();
        mDeviceIeee=deviceInfo.getIEEE();
        mDeviceSnid = deviceInfo.getDeviceSnid();
        //门锁
        if(deviceInfo.getDeviceId()== DeviceList.DEVICE_ID_DOOR_LOCK){
            title.setText("门锁信息");
            rlPower.setVisibility(View.VISIBLE);
            rlDeviceModel.setVisibility(View.VISIBLE);
            rlDeviceCode.setVisibility(View.VISIBLE);
            //获取当前网关
            LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
            if (gw == null) {
                return;
            }
            mUserName = gw.getUsername();
            mPassword = gw.getPassword();
            mEndTime = DateUtil.getCurrentTime();
            showLoadingDialog(null);

            //设置门锁缓存最新电量
            setNativePower();
            //三秒钟后do
            secondsLaterDo();
            //获取门锁第一条电量展示
            getNetPowerFirstRecordPersent(String.valueOf(mEndTime / 1000));
            //门锁类型
            getDeviceType();

        }
        //插座
        else if(deviceInfo.getDeviceId()==DeviceList.DEVICE_ID_SOCKET){
            title.setText("插座信息");
        }
        //色温灯
        else if(deviceInfo.getDeviceId()==DeviceList.DEVICE_ID_COLOR_TEMP1 ||
                deviceInfo.getDeviceId()== DeviceList.DEVICE_ID_COLOR_TEMP2){
            title.setText("色温灯信息");
        }
        //彩灯
        else if(deviceInfo.getDeviceId()==DeviceList.DEVICE_ID_COLOR_PHILIPS){
            title.setText("彩灯信息");
        }
        //窗帘
        else if(deviceInfo.getDeviceId()==DeviceList.DEVICE_ID_CURTAIN){
            title.setText("窗帘信息");
        }
        //智能开关
        else if(deviceInfo.getDeviceId()==DeviceList.DEVICE_ID_SWITCH){
            title.setText("智能开关信息");
        }
        //场景面板
        else if(deviceInfo.getDeviceId()==DeviceList.DEVICE_ID_SWITCH_SCENE){
            datas=new ArrayList<>();
            title.setText("场景面板信息");
            rlScencePanel.setVisibility(View.VISIBLE);
            rlScencePanel.setOnClickListener(this);
            tvScencePanel.setText("绑定设备/场景");
            Action1<Throwable> onErrorAction = new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    // Error handling
                }
            };
            //接收到Serial中
            Subscription subscription= RxBus.getInstance().toObservable(SenceInfo.class)
                    .onBackpressureBuffer(10000)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SenceInfo>() {
                        @Override
                        public void call(SenceInfo senceInfo) {
                            if (senceInfo==null)return;

                            for (int i = 0; i <datas.size() ; i++) {
                                if (datas.get(i)==null)
                                    continue;
                                if(datas.get(i).getSenceId()==senceInfo.getSenceId()){
                                    if (datas.get(i).getSenceName().equals(senceInfo.getSenceName())){
                                        return;
                                    }else{
                                        datas.remove(i);
                                        datas.add(senceInfo);
                                        return;
                                    }
                                }
                            }
                            datas.add(senceInfo);
                            LogUtil.e("绑定列表",""+senceInfo.getSenceId());
                        }
                    },onErrorAction);
            mCompositeSubscription.add(subscription);
            //获取场景
            AppContext.getInstance().getSerialInstance().getSences();

            mSubscription = RxBus.getInstance().toObservable(BindDeviceInfoEvent.class)
                    .compose(TransformUtils.<BindDeviceInfoEvent>defaultSchedulers())
                    .subscribe(new Action1<BindDeviceInfoEvent>() {
                        @Override
                        public void call(BindDeviceInfoEvent event) {
                           //接收绑定设备信息
                            if(event==null)return;
                            if(event.getUid()==mDeviceUid){
                                tvScencePanel.setText("绑定设备/场景/解绑");
                                int[] bindDeviceUids=event.getTargetDeviceIds();
                                if(bindDeviceUids!=null&&bindDeviceUids.length>0){
                                    List<DeviceInfo> devices = AppContext.getmOurDevices();
                                    for (int i = 0; i <devices.size() ; i++) {
                                        if(bindDeviceUids[0]==devices.get(i).getUId()){
                                            bindDevice=devices.get(i);
                                            tvDeviceName.setText(devices.get(i).getDeviceName());
                                        }
                                    }
                                }
                            }
                            LogUtil.e("返回","绑定设备信息返回");
                            hideLoadingDialog();
                        }
                    });
            mSubscription1 = RxBus.getInstance().toObservable(BindSenceInfoEvent.class)
                    .compose(TransformUtils.<BindSenceInfoEvent>defaultSchedulers())
                    .subscribe(new Action1<BindSenceInfoEvent>() {
                        @Override
                        public void call(BindSenceInfoEvent event) {
                            //接收绑定场景信息
                            if(event==null)return;
                            if(event.getUid()==mDeviceUid){
                                tvScencePanel.setText("绑定设备/场景");
                                bindSenceUid=event.getSceneId();
                            }
                            LogUtil.e("返回","绑定场景信息返回");
                            hideLoadingDialog();
                        }
                    });

           Subscription mSubscription2 = RxBus.getInstance().toObservable(BindDevSenceEvent.class)
                    .compose(TransformUtils.<BindDevSenceEvent>defaultSchedulers())
                    .subscribe(new Action1<BindDevSenceEvent>() {
                        @Override
                        public void call(BindDevSenceEvent event) {

                            if(event==null)return;
                            //设备
                            if(event.getBindTag()==1){
                                if("".equals(event.getName())){
                                    tvDeviceName.setText("无");
                                    tvScencePanel.setText("绑定设备/场景");
                                    bindDevice=null;
                                }else{
                                    tvDeviceName.setText(event.getName());
                                    tvScencePanel.setText("绑定设备/场景/解绑");
                                    List<DeviceInfo> devices = AppContext.getmOurDevices();
                                    for (int i = 0; i <devices.size() ; i++) {
                                        if(event.getBindSenceId()==devices.get(i).getUId()){
                                           bindDevice=devices.get(i);
                                        }
                                    }
                                }
                            }
                            //场景
                            else if(event.getBindTag()==2){
                                tvDeviceName.setText(event.getName());
                                tvScencePanel.setText("绑定设备/场景");
                                bindDevice=null;
                            }
                        }
                    });
            mCompositeSubscription.add(mSubscription);
            mCompositeSubscription.add(mSubscription1);
            mCompositeSubscription.add(mSubscription2);
            serial=AppContext.getInstance().getSerialInstance();
            checkTimer();
//            if(serial!=null){
//                serial.getBindInfo();
//            }
        }

        doorlockNameInfo.setText(mDeviceName);

        if(mDeviceStatus>0){
            doorlockIsonlineInfo.setText("在线");
        }else{
            doorlockIsonlineInfo.setText("离线");
        }
        if(mDeviceUid>0){
            doorlockUuidInfo.setText(String.valueOf(mDeviceUid));
        }


        String ieee=ByteStringUtil.bytesToHexString(mDeviceIeee);
        if(ieee!=null){
            doorlockIeeeInfo.setText(ieee.toUpperCase());
        }

        if(mDeviceSnid!=null){
            int index=mDeviceSnid.indexOf('.');
            String deviceSnid=mDeviceSnid.substring(0,index+2);
            doorlockModelSoftVer.setText(deviceSnid);
        }

    }

    private Subscription subscription33;
    private void checkTimer() {

       subscription33 = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {

                        if(datas.size()>0){
                            for (int i = 0; i <datas.size() ; i++) {
                                if(bindSenceUid==datas.get(i).getSenceId()){
                                    tvDeviceName.setText(datas.get(i).getSenceName());
                                    if (subscription33 != null && !subscription33.isUnsubscribed()) {
                                        subscription33.unsubscribe();
                                    }
                                    hideLoadingDialog();
                                    break;
                                }
                            }

                        }
                    }
                });
        mCompositeSubscription.add(subscription33);
    }
    @Override
    protected void onResume() {
        super.onResume();
       if(deviceInfo.getDeviceId()==DeviceList.DEVICE_ID_SWITCH_SCENE){
           if(serial!=null){
               tvScencePanel.setText("绑定设备/场景");
               serial.getBindInfo();
           }
       }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.linear_updateDevName:
                //编辑设备名称
                showCustomizeDialog();

                break;
            //关联/解绑
            case R.id.rl_scence_panel:
                showAssociationbindDialog();
                break;
        }
    }

    //设置缓存电量
    private void setNativePower(){
        if(AppContext.getDoorLockPowerMap().get(mDeviceUid)!=null){
            int power=AppContext.getDoorLockPowerMap().get(mDeviceUid)/2;
            showDoorLockDvcState(power);
        }

    }

    private void secondsLaterDo(){
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        Subscription subscription1 = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        hideLoadingDialog();

                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);
    }

    private void showAssociationbindDialog(){
        /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_associatebind, null);
        TextView tvAssociateDevice = (TextView) dialogView.findViewById(R.id.tv_associate_device);
        TextView tvAssociateScence = (TextView) dialogView.findViewById(R.id.tv_associate_scence);
        TextView tvUnbind = (TextView) dialogView.findViewById(R.id.tv_unbind);
        if(tvScencePanel.getText().toString().equals("绑定设备/场景")){
            tvUnbind.setVisibility(View.GONE);
        }
        tvAssociateDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //绑定设备
                if (alertDialog != null)
                    alertDialog.dismiss();
                Intent intent=new Intent(DoorLockInfoActivity.this,BindDeviceActivity.class);
                intent.putExtra("deviceUid",mDeviceUid);

                startActivity(intent);
            }
        });

        tvAssociateScence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //绑定场景
                if (alertDialog != null)
                    alertDialog.dismiss();
                Intent intent=new Intent(DoorLockInfoActivity.this,BindSenceActivity.class);
                //intent.putExtra("senceUid",bindSenceUid);
                intent.putExtra("deviceUid",mDeviceUid);
                startActivity(intent);

            }
        });

        tvUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解绑
                if (alertDialog != null)
                    alertDialog.dismiss();
                if(bindDevice!=null){
                    showLoadingDialog(null);
                    unBandDevice(bindDevice);
                }
            }

        });
        customizeDialog.setView(dialogView);
        alertDialog = customizeDialog.show();
    }

    private void unBandDevice(final DeviceInfo mbindDevice) {
        Subscription sub=Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret=serial.unBindDevice(deviceInfo,mbindDevice);
                subscriber.onNext(ret);
                subscriber.onCompleted();
            }
        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(Integer ret) {
                        if(ret>=0){
                            ToastUtils.showShort("解绑成功");
                            tvDeviceName.setText("无");
                            tvScencePanel.setText("绑定设备/场景");
                            bindDevice=null;

                        }else{

                            ToastUtils.showShort("解绑失败请重试");
                        }
                        hideLoadingDialog();
                    }
                });

        mCompositeSubscription.add(sub);

    }

    /**
     * 修改门锁名称弹出对话框
     */
    private void showCustomizeDialog() {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_title);
        title.setText("修改设备名称");
        final EditText editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        if(mDeviceName!=null){
            editText.setText(mDeviceName);
            editText.setSelection(mDeviceName.length());
        }
        TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String editName = editText.getText().toString().trim();
                if (editName == null || editName.isEmpty()) {
                    ToastUtils.showShort("设备名不能为空!");
                    return;
                }

                List<DeviceInfo> deviceInfos = getmOurDevices();

                if (null == deviceInfos){
                    ToastUtils.showShort("修改失败!");
                    return;
                }


                for (int i = 0; i < deviceInfos.size(); i++) {
                    if (deviceInfos.get(i).getDeviceName().equals(editName)) {
                        ToastUtils.showShort("设备名已存在，请重新输入!");
                        return;
                    }

                }

                try {
                    final byte[] temp = editName.getBytes("utf-8");
                    if (temp.length > 16) {
                        ToastUtils.showShort("设备名过长!");
                        return;
                    }

                    Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            Serial mSerial = AppContext.getInstance().getSerialInstance();
                            int ret = mSerial.ChangeDeviceName(deviceInfo, temp);
                            subscriber.onNext(ret);
                        }

                    }).compose(TransformUtils.<Integer>defaultSchedulers())
                            .subscribe(new Subscriber<Integer>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Integer ret) {
                                    if (alertDialog != null)
                                        alertDialog.dismiss();
                                    if (ret >= 0) {
                                        doorlockNameInfo.setText(editName);
                                        //修改成功，改对应缓存中的设备名称
                                        List<DeviceInfo> list = AppContext.getmOurDevices();
                                        for (int i = 0; i < list.size(); i++) {
                                            if (list.get(i).getUId() == deviceInfo.getUId()) {
                                                AppContext.getmOurDevices().get(i).setDeviceName(editName);
                                            }
                                        }

                                        RxBus.getInstance().post(new ChangeDevicenameEvent(deviceInfo.getUId(), editName));
                                        ToastUtils.showShort("修改成功!");
                                    } else {
                                        ToastUtils.showShort("修改失败!");
                                    }

                                }
                            });
                    mCompositeSubscription.add(sub);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        cancleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        alertDialog = customizeDialog.show();
    }
    /**
     * 获取互联网电量数据第一条百分比
     */

    public void getNetPowerFirstRecordPersent( final String mEndTime) {
        ArrayMap paramsMap = new ArrayMap<String, String>();
        paramsMap.put("userNo", mUserName);
        paramsMap.put("userPass", mPassword);
        paramsMap.put("type", "6");
        paramsMap.put("uid", mDeviceUid + "");
        paramsMap.put("start", "100");
        paramsMap.put("end", mEndTime);
        paramsMap.put("limit", "1");
        presenter.getStatus(paramsMap,"6");

    }

    /**
     * 获取门锁型号
     */
    public void getDeviceType(){
        ArrayMap paramsMap = new ArrayMap<String, String>();
        paramsMap.put("userNo", mUserName);
        paramsMap.put("userPass", mPassword);
        paramsMap.put("type", "13");
        paramsMap.put("uid", mDeviceUid + "");
        paramsMap.put("limit", "1");
        presenter.getStatus(paramsMap,"13");
    }



    /**
     * 电量信息回调
     * @param info
     */
    @Override
    public void responseDoorInfo(DoorlockpowerInfo info) {
        if (info != null) {
            if (info.getValue().size() == 0) {
                return;
            }
            String powerInfo = info.getValue().get(0);
            int firstPower = Integer.parseInt(powerInfo) / 2;
            showDoorLockDvcState(firstPower);
        }
        hideLoadingDialog();
    }

    @Override
    public void responseDoorAlarm(DoorLockAlarmResponse info) {

    }

    @Override
    public void responseDeleteDoorAlarm(BaseResponse info) {

    }


    /**
     * 门锁型号返回
     * @param info
     */
    @Override
    public void responseDeviceModel(DoorlockpowerInfo info) {
        if(null != info){
            if(info.getValue().size()==0){
                doorlockModelInfo.setText("无");
                doorlockCode.setText("无");
                return;
            }
            String code = info.getValue().get(0);
            long time= Long.parseLong(info.getTime().get(0))*1000l;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date  dt = new Date(time);
            String timeStr = "上报时间："+sdf.format(dt);

            String model=code.substring(2,3);
            String modelnum = code.substring(3,6);
            if(model.equals("2")){
                doorlockModelInfo.setText("Z"+modelnum);
            }else if("3".equals(model)){
                doorlockModelInfo.setText("G"+modelnum);
            }else if("6".equals(model)){
                doorlockModelInfo.setText("R"+modelnum);
            }else if("9".equals(model)){
                doorlockModelInfo.setText("H"+modelnum);
            }else{
                doorlockModelInfo.setText("智能锁"+modelnum);
            }
            //串码
            String deviceCode = code.substring(6,28);
            String strCode=new String(ByteStringUtil.hexStringToBytes(deviceCode));
            SpannableString msp = new SpannableString(strCode+"\n"+timeStr);
            msp.setSpan(new AbsoluteSizeSpan(12,true), strCode.length()+1,
                    strCode.length()+1+timeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置字体前景色
             msp.setSpan(new ForegroundColorSpan(Color.GRAY), strCode.length()+1,
                     strCode.length()+1+timeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            doorlockCode.setText(msp);
        }else{
            doorlockModelInfo.setText("无");
            doorlockCode.setText("无");
        }
    }

    /**
     * 电量上报图片设置
     *
     * @param
     */
    private void showDoorLockDvcState(int persent) {
        int resId = 0;
        switch (persent) {
            case 100:
                resId = R.mipmap.power_100;
                break;
            case 75:
                resId = R.mipmap.power_75;
                break;
            case 50:
                resId = R.mipmap.power_50;
                break;
            case 25:
                resId = R.mipmap.power_25;
                break;
            case 0:
                resId = R.mipmap.power_0;
                break;
        }
        doorlockPowerInfo.setImageResource(resId);
    }
    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
