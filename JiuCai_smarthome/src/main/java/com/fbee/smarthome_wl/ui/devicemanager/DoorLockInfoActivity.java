package com.fbee.smarthome_wl.ui.devicemanager;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
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
import com.fbee.smarthome_wl.event.ChangeDevicenameEvent;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockContract;
import com.fbee.smarthome_wl.ui.doorlock.DoorlockPresenter;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.DateUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.Serial;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
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
        presenter.getStatus(paramsMap);

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
                resId = R.mipmap.power_25;
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
