package com.fbee.smarthome_wl.ui.equesdevice;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.EquesDeviceInfo;
import com.fbee.smarthome_wl.event.UpdateEquesNameEvent;
import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.EquesAlarmActivity;
import com.fbee.smarthome_wl.ui.equesdevice.equesaddlock.EquesAddlockActivity;
import com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.EquesFlashShotActivity;
import com.fbee.smarthome_wl.ui.equesdevice.settinglist.EquesSettingListActivity;
import com.fbee.smarthome_wl.ui.equesdevice.videocall.EquesCallActivity;
import com.fbee.smarthome_wl.ui.equesdevice.visitorlist.EquesVisitorActivity;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EquesDeviceInfoActivity extends BaseActivity {

    private String Nick;
    private String Bid;
    private String Uid;
    private String Name;
    private int Status;
    private TextView DeviceName;
    private TextView DeviceState;
    private String sw_version;
    /**
     * 人体检测开关
     */
    private int alarm_enable;
    /**
     * 门铃灯开关
     */
    private int db_light_enable;
    /**
     * 门铃声
     */
    private int doorbell_ring;
    private ImageView back;
    private ImageView videoView;
    private ImageView ivLinkSpeed;
    private TextView tvBatteryLevel;
    private ImageView ivAlarmMessage;
    private ImageView btnConfigure;
    private ImageView ivVisitor;
    private ImageView ivCaptured;
    private int wifi_level;
    private String wifi_config;
    private ImageView closRing;
    private boolean aBoolean;
    private ImageView batteryImage;
    private ImageView gateLock;
    private int battery_level;
    private TextView tvRing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_device_info);
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        Nick = bundle.getString(Method.ATTR_BUDDY_NICK);
        Bid = bundle.getString(Method.ATTR_BUDDY_BID);
        Uid = bundle.getString(Method.ATTR_BUDDY_UID);
        Name = bundle.getString(Method.ATTR_BUDDY_NAME);
        Status = bundle.getInt(Method.ATTR_BUDDY_STATUS);
        //获取设备详情
        icvss.equesGetDeviceInfo(Uid);

        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        DeviceName = (TextView) findViewById(R.id.title);
        videoView = (ImageView) findViewById(R.id.video_view);
        DeviceState = (TextView) findViewById(R.id.dev_state);
        ivLinkSpeed = (ImageView) findViewById(R.id.iv_LinkSpeed);
        tvBatteryLevel = (TextView) findViewById(R.id.tv_batteryLevel);
        ivAlarmMessage = (ImageView) findViewById(R.id.iv_alarm_message);
        gateLock = (ImageView) findViewById(R.id.btn_gate_lock);
        btnConfigure = (ImageView) findViewById(R.id.btn_configure);
        ivVisitor = (ImageView) findViewById(R.id.iv_visitor);
        ivCaptured = (ImageView) findViewById(R.id.iv_captured);
        closRing = (ImageView) findViewById(R.id.btn_close_ring);
        tvRing = (TextView) findViewById(R.id.tv_ring);
        batteryImage = (ImageView) findViewById(R.id.iv_batteryImage_1);
        closRing.setOnClickListener(this);
        ivCaptured.setOnClickListener(this);
        btnConfigure.setOnClickListener(this);
        videoView.setOnClickListener(this);
        gateLock.setOnClickListener(this);
        back.setOnClickListener(this);
        ivVisitor.setOnClickListener(this);
        ivAlarmMessage.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        initApi();
        aBoolean = PreferencesUtils.getBoolean(Uid);
        if (aBoolean) {
            closRing.setImageResource(R.mipmap.cloes_ring);
            tvRing.setText("关闭铃声");
        } else {
            closRing.setImageResource(R.mipmap.cloes_ring_selector);
            tvRing.setText("开启铃声");
        }
        if (Nick == null) {
            DeviceName.setText(Name);
        } else {
            DeviceName.setText(Nick);
        }
        if (Status == 0) {
            DeviceState.setText("离线");
            DeviceState.setTextColor(getResources().getColor(R.color.red));
        } else if (Status == 1) {
            DeviceState.setText("在线");
            DeviceState.setTextColor(getResources().getColor(R.color.colorAccent));
        }

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
                        LogUtil.e("三秒", "请求前");
                        if (sw_version == null) {
                            icvss.equesGetDeviceInfo(Uid);
                            LogUtil.e("三秒", "请求中");
                        }

                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);

        mSubscription = RxBus.getInstance().toObservable(UpdateEquesNameEvent.class)
                .compose(TransformUtils.<UpdateEquesNameEvent>defaultSchedulers())
                .subscribe(new Action1<UpdateEquesNameEvent>() {
                    @Override
                    public void call(UpdateEquesNameEvent event) {
                        DeviceName.setText(event.getName());
                    }
                });

        mSubscription = RxBus.getInstance().toObservable(EquesDeviceInfo.class)
                .compose(TransformUtils.<EquesDeviceInfo>defaultSchedulers())
                .subscribe(new Action1<EquesDeviceInfo>() {

                    @Override
                    public void call(EquesDeviceInfo equesDeviceInfo) {
                        sw_version = equesDeviceInfo.getSw_version();
                        wifi_level = equesDeviceInfo.getWifi_level();
                        wifi_config = equesDeviceInfo.getWifi_config();
                        alarm_enable = equesDeviceInfo.getAlarm_enable();
                        db_light_enable = equesDeviceInfo.getDb_light_enable();
                        doorbell_ring = equesDeviceInfo.getDoorbell_ring();
                        battery_level = equesDeviceInfo.getBattery_level();
                        if (battery_level <= 10) {
                            batteryImage.setImageResource(R.mipmap.battery_0);
                        } else if (battery_level > 10 && battery_level <= 25) {
                            batteryImage.setImageResource(R.mipmap.battery_25);
                        } else if (battery_level > 25 && battery_level <= 50) {
                            batteryImage.setImageResource(R.mipmap.battery_50);
                        } else if (battery_level > 50 && battery_level <= 75) {
                            batteryImage.setImageResource(R.mipmap.battery_75);
                        } else if (battery_level > 75) {
                            batteryImage.setImageResource(R.mipmap.battery_100);
                        }
//                        else {
//                            batteryImage.setImageResource(R.mipmap.battery_0);
//                        }
                        tvBatteryLevel.setText(battery_level + "%");
                        switch (wifi_level) {
                            case 0:
                                ivLinkSpeed.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_signal_0));
                                break;
                            case 1:
                                ivLinkSpeed.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_signal_1));
                                break;
                            case 2:
                            case 3:
                                ivLinkSpeed.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_signal_2));
                                break;
                            case 4:
                                ivLinkSpeed.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_signal_3));

                                break;

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_configure:
                Bundle bundle = new Bundle();
                bundle.putString(Method.ATTR_BUDDY_UID, Uid);
                bundle.putString(Method.ATTR_433_DEVICE_NICK, Nick);
                bundle.putString(Method.ATTR_BUDDY_BID, Bid);
                bundle.putString(Method.ATTR_BUDDY_NAME, Name);
                if (sw_version != null) {
                    //版本号
                    bundle.putString(Method.ATTR_BUDDY_VERSION, sw_version);
                    //报警开关
                    bundle.putInt(Method.METHOD_ALARM_ENABLE, alarm_enable);
                    //门铃灯开关
                    bundle.putInt(Method.METHOD_DB_LIGHT_ENABLE, db_light_enable);
                    //门铃声
                    bundle.putInt("doorbell_ring", doorbell_ring);
                    //当前连接的网络名
                    bundle.putString(Method.METHOD_WIFI_STATUS, wifi_config);
                    skipAct(EquesSettingListActivity.class, bundle);
                } else {
                    showToast("配置获取失败");
                    icvss.equesGetDeviceInfo(Uid);
                }
                break;
            case R.id.video_view:
                //bundle1.putString(Method.ATTR_BUDDY_NICK, Nick);
//                if (Nick == null) {
//                    Nick = Name;
//                }
//                bundle1.putString(Method.ATTR_BUDDY_NICK, Nick);
                if (Uid != null) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(Method.ATTR_BUDDY_UID, Uid);
                    skipAct(EquesCallActivity.class, bundle1);
                }
                break;
            case R.id.iv_alarm_message:
                Bundle bundle2 = new Bundle();
                if (Nick == null) {
                    Nick = Name;
                }
                bundle2.putString(Method.ATTR_BUDDY_NICK, Nick);
                bundle2.putString(Method.ATTR_BUDDY_BID, Bid);
                skipAct(EquesAlarmActivity.class, bundle2);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.iv_visitor:
                Bundle bundle3 = new Bundle();
                if (Nick == null) {
                    Nick = Name;
                }
                bundle3.putString(Method.ATTR_BUDDY_NICK, Nick);
                bundle3.putString(Method.ATTR_BUDDY_BID, Bid);
                skipAct(EquesVisitorActivity.class, bundle3);
                break;
            case R.id.iv_captured:
                Bundle bundle4 = new Bundle();
                bundle4.putString(Method.ATTR_BUDDY_UID, Uid);
                bundle4.putString(Method.ATTR_BUDDY_NICK, Nick);
                skipAct(EquesFlashShotActivity.class, bundle4);
                break;
            case R.id.btn_close_ring:
                if (!aBoolean) {
                    DialogManager.Builder builder = new DialogManager.Builder()
                            .msg("智能猫眼的呼叫铃音和报警铃音都将开启").title("")
                            .cancelable(false)
                            .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                            .rightBtnText("确定");

                    DialogManager.showDialog(this, builder, new DialogManager.ConfirmDialogListener() {
                        @Override
                        public void onLeftClick() {

                        }

                        @Override
                        public void onRightClick() {
                            closRing.setImageResource(R.mipmap.cloes_ring);
                            aBoolean = true;
                            PreferencesUtils.saveBoolean(Uid, aBoolean);
                            tvRing.setText("关闭铃声");
                        }
                    });
                }else{
                    DialogManager.Builder builder = new DialogManager.Builder()
                            .msg("智能猫眼的呼叫铃音和报警铃音都将关闭").title("")
                            .cancelable(false)
                            .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                            .rightBtnText("确定");

                    DialogManager.showDialog(this, builder, new DialogManager.ConfirmDialogListener() {
                        @Override
                        public void onLeftClick() {

                        }

                        @Override
                        public void onRightClick() {
                            closRing.setImageResource(R.mipmap.cloes_ring_selector);
                            aBoolean = false;
                            PreferencesUtils.saveBoolean(Uid, aBoolean);
                            tvRing.setText("开启铃声");
                        }
                    });
                }
//                AlertDialog.Builder builder1 = new AlertDialog.Builder(EquesDeviceInfoActivity.this);
//                builder1.setMessage("智能猫眼的呼叫铃音和报警铃音都将关闭/开启")
//                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setPositiveButton("开启",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                }).show();
                break;
            case R.id.btn_gate_lock:
                Bundle bundle5 = new Bundle();
                bundle5.putString(Method.ATTR_BUDDY_BID, Bid);
                skipAct(EquesAddlockActivity.class, bundle5);
                break;
        }
    }
}
