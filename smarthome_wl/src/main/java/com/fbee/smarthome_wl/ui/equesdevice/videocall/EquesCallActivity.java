package com.fbee.smarthome_wl.ui.equesdevice.videocall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.DoorLockDeviceAdapter;
import com.fbee.smarthome_wl.adapter.itemdecoration.SpaceItemDecoration;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.DoorLockStateInfo;
import com.fbee.smarthome_wl.bean.GateWayInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.event.ControlTimeEvent;
import com.fbee.smarthome_wl.event.VideoTime;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceListResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverContract;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverPresenter;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TimerCount;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.Tool;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.WeakHandler;
import com.fbee.zllctl.DeviceInfo;
import com.jaeger.library.StatusBarUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.ui.doorlock.DoorLockActivity.S_DOOR_UNLOCK;

public class EquesCallActivity extends BaseActivity<AddOrDelDevicesToSeverContract.Presenter> implements SurfaceHolder.Callback, BaseRecylerAdapter.OnItemClickLitener, AddOrDelDevicesToSeverContract.View {

    private SurfaceView surfaceView;
    private String uid;
    private ImageView btnSpeak;
    private LinearLayout llFunction;
    private RelativeLayout rlEquesCall;
    private ImageView fullScreen;
    private RelativeLayout rl_call_button;
    private ImageView btnHangupCall;
    private ImageView btnMute;
    private AudioManager audioManager;
    private int current;
    private String callId;
    private boolean isMuteFlag;
    private LinearLayout right_linear_eques_call;
    private ImageView tvScreenCapture;
    private TextView txTime;
    private Timer timer;
    private RelativeLayout screenCapture;
    private RelativeLayout iv_hangupCall;
    private RelativeLayout iVspeak;
    private RelativeLayout ivMute;
    private LinearLayout doorlockLayoutLinear;
    private RecyclerView doorlockRecycler;
    private ImageView linerSpeakLock;
    private ImageView btnSpeakLock;
    private ImageView doorlockImageicon;
    private DoorLockDeviceAdapter adapter;
    private TextView quxiaoText;
    private RelativeLayout isJihuoLayoutLiner;
    private ImageView mBtnControl;
    private TextView mTextLockHint;
    private TextView isJihuoQuxiaoText;
    private DeviceInfo selectDeviceInfo;
    private TimerCount controlTimeTimer;
    private boolean isControlAble = false;// 默认不可控
    private WeakHandler mHandler;
    private GateWayInfo gateWayInfo;
    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.RECORD_AUDIO};
    private List<DeviceInfo> deviceInfos;
    private String uuid;
    private String gwSnid;
    private Button speak;
    private String deviceName;
    private TextView tvDeviceName;
    private ImageView imageFullscreenMute;
    private ImageView imageFullscreenVoice;
    private HashMap<String, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean>> userEntityMap;
    private TextView tvSpeak;
    private TextView tvSpeak1;
    private String context_uuid;

    //private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_call);
        StatusBarUtil.setColor(this, Color.parseColor("#00000000"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            verifyRecordPermissions(this);
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString(Method.ATTR_BUDDY_UID);
        rl_call_button = (RelativeLayout) findViewById(R.id.rl_call_button);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        btnSpeak = (ImageView) findViewById(R.id.btn_speak);
        llFunction = (LinearLayout) findViewById(R.id.ll_function);
        rlEquesCall = (RelativeLayout) findViewById(R.id.rl_eques_call);
        fullScreen = (ImageView) findViewById(R.id.iv_full_screen);
        btnHangupCall = (ImageView) findViewById(R.id.btn_hangupCall);
        btnMute = (ImageView) findViewById(R.id.btn_mute);
        tvScreenCapture = (ImageView) findViewById(R.id.tv_screen_capture);
        right_linear_eques_call = (LinearLayout) findViewById(R.id.right_linear_eques_call);
        ivMute = (RelativeLayout) findViewById(R.id.break_icon_eques_call);
        iVspeak = (RelativeLayout) findViewById(R.id.iv_speak);
        txTime = (TextView) findViewById(R.id.call_time);
        screenCapture = (RelativeLayout) findViewById(R.id.iv_screen_capture);
        iv_hangupCall = (RelativeLayout) findViewById(R.id.iv_hangupCall);
        doorlockLayoutLinear = (LinearLayout) findViewById(R.id.doorlock_layout_linear);
        doorlockRecycler = (RecyclerView) findViewById(R.id.doorlock_recycler);
        linerSpeakLock = (ImageView) findViewById(R.id.btn_speak_lock);
        doorlockImageicon = (ImageView) findViewById(R.id.doorlock_imageicon);
        quxiaoText = (TextView) findViewById(R.id.quxiao_text);
        isJihuoLayoutLiner = (RelativeLayout) findViewById(R.id.is_jihuo_layout_liner);
        mBtnControl = (ImageView) findViewById(R.id.is_jihuo_doorlock_icon);
        speak = (Button) findViewById(R.id.speak);
        mTextLockHint = (TextView) findViewById(R.id.is_jihuo_doorlock_text_state);
        isJihuoQuxiaoText = (TextView) findViewById(R.id.is_jihuo_quxiao_text);
        tvSpeak = (TextView) findViewById(R.id.tv_speak);
        tvSpeak1 = (TextView) findViewById(R.id.tv_speak1);
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        imageFullscreenMute = (ImageView) findViewById(R.id.image_fullscreen_mute);
        imageFullscreenVoice = (ImageView) findViewById(R.id.image_fullscreen_voice);
        speak.setOnClickListener(this);
        iv_hangupCall.setOnClickListener(this);
        surfaceView.getHolder().addCallback(this);
        btnHangupCall.setOnClickListener(this);
        screenCapture.setOnClickListener(this);
        btnMute.setOnClickListener(this);
        ivMute.setOnClickListener(this);
        tvScreenCapture.setOnClickListener(this);
        fullScreen.setOnClickListener(this);
        doorlockImageicon.setOnClickListener(this);
        quxiaoText.setOnClickListener(this);
        mBtnControl.setOnClickListener(this);
        isJihuoQuxiaoText.setOnClickListener(this);

    }

    private void initTime() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
            }
        };
        RxBus.getInstance()
                .toObservable(VideoTime.class)
                .compose(TransformUtils.<VideoTime>defaultSchedulers())
                .subscribe(new Action1<VideoTime>() {

                    @Override
                    public void call(VideoTime videoTime) {
                        if(timer !=null)
                        timer.schedule(task, 1000, 1000);
                    }
                },onErrorAction);
    }

    public static void verifyRecordPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_RECORD,
                    1);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void initData() {
        timer = new Timer();
        createPresenter(new AddOrDelDevicesToSeverPresenter(this));
        mCompositeSubscription = getCompositeSubscription();
        //接收网关信息
        receiveGateWayInfo();
        //请求网关信息
        AppContext.getInstance().getSerialInstance().getGateWayInfo();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
        //判断是否插入耳机
        boolean bo = audioManager.isWiredHeadsetOn();
        if (!bo) {
            openSpeaker();
        }
        //phone = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        //初始化门锁列表
        //门锁计时intent
        mHandler = new WeakHandler();
        speak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        callSpeakerSetting(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        callSpeakerSetting(false);
                        break;
                }
                return false;
            }
        });
        //按住说话
        btnSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        callSpeakerSetting(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        callSpeakerSetting(false);
                        break;
                }
                return false;
            }
        });
        //按住说话
        linerSpeakLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        callSpeakerSetting(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        callSpeakerSetting(false);
                        break;
                }
                return false;
            }
        });
        iVspeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageFullscreenVoice.setImageResource(R.mipmap.full_screen_voice_press);
                        callSpeakerSetting(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        imageFullscreenVoice.setImageResource(R.mipmap.full_screen_voice);
                        callSpeakerSetting(false);
                        break;
                }
                return false;
            }
        });
        deviceInfos = AppContext.getmDoorLockDevices();
        adapter = new DoorLockDeviceAdapter(this, this.deviceInfos);
        GridLayoutManager gm = new GridLayoutManager(this, 1);
        gm.setOrientation(GridLayoutManager.HORIZONTAL);
        doorlockRecycler.setLayoutManager(gm);
        doorlockRecycler.setItemAnimator(new DefaultItemAnimator());
        SpaceItemDecoration sd = new SpaceItemDecoration(AppUtil.dip2px(this, 10));
        sd.setLine(deviceInfos.size());
        doorlockRecycler.addItemDecoration(sd);
        adapter.setOnItemClickLitener(this);
        doorlockRecycler.setAdapter(adapter);

        //接收飞比设备刷新页面
        receiveFbeeDevice();

        //接接收门锁设备开锁上报
        //receiveDoorLockPowerInfo();

        //接收门锁可控状态
        receiveDoorLockStateInfo();


        //接收门锁激活服务计时结束时的数据
        //receiveControlTimeServiceInfo();

        //请求列表查询当前猫眼是否绑定门锁
        QueryDevicesListInfo bodyEntity = new QueryDevicesListInfo();
        bodyEntity.setUuid(uid);
        bodyEntity.setVendor_name(FactoryType.EQUES);
        presenter.queryDevices(bodyEntity);

        initTime();
    }


    /**
     * 接收网关信息
     */
    private void receiveGateWayInfo() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                receiveGateWayInfo();
            }
        };
        //注册RXbus接收arriveReport_CallBack（）回调中的数据
        Subscription gateWaySubscription = RxBus.getInstance().toObservable(GateWayInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GateWayInfo>() {
                    @Override
                    public void call(GateWayInfo event) {
                        if (event == null) return;
                        if (gateWayInfo != null) {
                            if (event.toString().equals(gateWayInfo.toString())) {
                                return;
                            }
                        }
                        gateWayInfo = event;
                    }
                }, onErrorAction);
        mCompositeSubscription.add(gateWaySubscription);
    }


    /**
     * 注册RXbus接收来自Serial中arriveReportgatedoor_CallBack(int uId, byte[] data,
     * char clusterId, char attribID)的数据
     */
    public void receiveDoorLockStateInfo() {
        Subscription stateInfoSubscription = RxBus.getInstance().toObservable(DoorLockStateInfo.class)
                .compose(TransformUtils.<DoorLockStateInfo>defaultSchedulers())
                .subscribe(new Action1<DoorLockStateInfo>() {
                    @Override
                    public void call(DoorLockStateInfo event) {
                        //处理门锁状态数据
                        if (event == null) return;
                        if (selectDeviceInfo == null) return;
                        handleReceiveDoorLockState(event);
                    }
                });
        mCompositeSubscription.add(stateInfoSubscription);
    }


    /**
     * 接收服务时间控制门锁时间进度
     */
    private void receiveControlTimeEvent() {

        Subscription subHint = RxBus.getInstance().toObservable(ControlTimeEvent.class)
                .compose(TransformUtils.<ControlTimeEvent>defaultSchedulers())
                .subscribe(new Action1<ControlTimeEvent>() {
                    @Override
                    public void call(ControlTimeEvent event) {
                        if (event == null) return;
                        if (selectDeviceInfo == null) return;
                        if (event.getDeviceId() == selectDeviceInfo.getUId()) {
                            final int nowSecond = event.getNowTime();

                            if (mTextLockHint.getText().equals("门锁未激活")) {
                                mTextLockHint.setText("门锁已激活");// 清空文字
                            }
                            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                            if (!mBtnControl.isClickable()) {
                                mBtnControl.setClickable(true);
                            }
                            if (isControlAble == false) {
                                isControlAble = true;
                            }

                            if (nowSecond == 1) {
                                isControlAble = false;
                                mBtnControl.setClickable(false);// 门锁图标不可以再次点击
                                mTextLockHint.setText("门锁未激活");// 清空文字
                                mBtnControl.setImageResource(R.mipmap.doorlock_icon);
                                mBtnControl.setClickable(false);
                            }
                        }
                    }
                });
        mCompositeSubscription.add(subHint);
    }


    /**
     * 处理门锁状态数据
     *
     * @param event
     */
    private void handleReceiveDoorLockState(DoorLockStateInfo event) {
        //初始化门锁通知
        int uid = event.getDoorLockUid(); // 得到uid
        if (uid != selectDeviceInfo.getUId()) {
            return;
        }
        int data = event.getDoorLockSensorData(); // 开锁时间
        byte[] bytes = Tool.IntToBytes(data);
        int newmodle = event.getNewDoorLockState();// 获得胁迫报警位
        StringBuffer text = new StringBuffer();
        int cardNum = -1;
        cardNum = event.getDoorLockCareNum();// 获取门卡编号
        int way = event.getDoorLockWay(); // 开锁方式
        int Flag = event.getFalg();
        if (Flag == 1) {
            text.append("密码错误");
            mTextLockHint.setText(text.toString());
            setReset(3000, true);
            ToastUtils.showShort("密码错误");
        } else if (Flag == 2 || Flag == 0x7f) {
            text.append("远程开锁未允许");
            mTextLockHint.setText(text.toString());
            ToastUtils.showShort("远程开锁未允许!");
            setReset(3000, false);
        }
        // 进入管理员菜单
        if ((newmodle & 0x08) == 8) {
            // 按键进入管理员菜单
            if (way == 0) {
                Enter_Menu(newmodle, text, cardNum, "密码");
            }// 指纹操作进入管理员菜单
            else if (way == 2) {
                Enter_Menu(newmodle, text, cardNum, "指纹");
            } else if (way == 3) {
                Enter_Menu(newmodle, text, cardNum, "刷卡");
            } else if (way == 5) {
                Enter_Menu(newmodle, text, cardNum, "多重");
            }
            if ((newmodle & 0xbf) == 0x88) {
                text.append("胁迫报警进入菜单");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);

            }// 胁迫指纹进入菜单（双人模式）
            else if ((newmodle & 0xbf) == 0x98) {
                text.append("胁迫报警进入菜单(双人模式)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }// 胁迫报警启用常开（菜单）
            else if ((newmodle & 0xbf) == 0x89) {
                text.append("胁迫报警启用常开(菜单)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }// 胁迫报警取消常开（菜单）
            else if ((newmodle & 0xbf) == 0x8a) {
                text.append("胁迫报警取消常开(菜单)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }
            // 胁迫指纹进入菜单取消常开（双人模式）
            else if ((newmodle & 0xbf) == 0x9a) {
                text.append("胁迫报警取消常开(双人模式)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }// 胁迫指纹进入菜单启用常开（双人模式）
            else if ((newmodle & 0xbf) == 0x99) {
                text.append("胁迫报警启用常开(双人模式)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }
        } else {
            if (bytes[3] == 0x02) { // 开锁
                // 刷卡
                if (way == 3) {
                    Unlocking(newmodle, text, cardNum, "刷卡");
                }
                // 多重验证
                else if (way == 5) {
                    Unlocking(newmodle, text, cardNum, "多重验证");
                }
                // 密码
                else if (way == 0) {
                    Unlocking(newmodle, text, cardNum, "密码");
                }
                // 指纹
                else if (way == 2) {
                    Unlocking(newmodle, text, cardNum, "指纹");
                }// 远程
                else if (way == 4) {
                    text.append("远程开锁成功");
                    mTextLockHint.setText(text.toString());
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    setReset(3000, false);
                }
                if ((newmodle & 0xbf) == 0x80) {
                    text.append("胁迫报警");
                    mTextLockHint.setText(text.toString());
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x90) {
                    text.append("胁迫报警 (双人模式)");
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x82) {
                    text.append("胁迫报警取消常开");
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x81) {
                    text.append("胁迫报警启用常开");
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x92) {
                    text.append("胁迫报警取消常开(双人模式)");
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x91) {
                    text.append("胁迫报警启用常开(双人模式)");
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                }
            }// 非法操作报警
            else if (bytes[3] == 0x03) {
                text.append("非法操作");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }// 非法卡
            else if (bytes[3] == 0x05) {
                text.append("门锁未关");
                mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }
        }
    }
    /**
     * 判断是否是绑定设备用户
     *
     * @param userNum
     * @return
     *//*
    public boolean isAssociateDevicesUser(int uid, int userNum) {
        userEntityMap = AppContext.getMap();
        if (userEntityMap != null) {
            if (userEntityMap.size() > 0) {
                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = userEntityMap.get(String.valueOf(uid));
                if (bodyEntities != null) {
                    for (int i = 0; i < bodyEntities.size(); i++) {
                        if (bodyEntities.get(i).getId().equals(String.valueOf(userNum))) {
                            List<String> userList = bodyEntities.get(i).getWithout_notice_user_list();
                            if (userList != null && userList.size() > 0) {
                                for (int j = 0; j < userList.size(); j++) {
                                    if (phone.equals(userList.get(j))) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }*/

    /**
     * 重置图标
     */
    private void setReset(int time, final boolean flag) {
        mHandler.postDelayed(new Runnable() {

            @SuppressLint("NewApi")
            @Override
            public void run() {

                if (flag) {
                    mTextLockHint.setText("门锁已激活");// 清空文字
                    mBtnControl.setClickable(true);
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                } else {
                    stopTimer();
                    isControlAble = false;
                    mBtnControl.setClickable(false);// 门锁图标不可以再次点击
                    mTextLockHint.setText("门锁未激活");// 清空文字
                    mBtnControl.setImageResource(R.mipmap.doorlock_icon);
                    mBtnControl.setClickable(false);
                }
            }
        }, time);
    }

    //进入菜单
    private void Enter_Menu(int newmodle, StringBuffer text, int cardNum,
                            String str) {
        if (newmodle == 8) {
            text.append(str + "验证进入菜单");
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if (newmodle == 0x18) {
            text.append(str + "验证进入菜单(双人模式)");
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if (newmodle == 10) {
            text.append(str + "取消常开(菜单)");
            mTextLockHint.setText(text.toString());
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            setReset(3000, false);
        } else if (newmodle == 9) {
            text.append(str + "启用常开(菜单)");
            mTextLockHint.setText(text.toString());
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            setReset(3000, false);
        } else if (newmodle == 26) {
            text.append(str + "取消常开(菜单)(双人)");
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x19) {
            text.append(str + "启用常开(菜单)(双人)");
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        }
    }

    private void Unlocking(int newmodle, StringBuffer text, int cardNum,
                           String str) {
        if (newmodle == 0) {
            text.append(str + "开锁");
            mTextLockHint.setText(text.toString());
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x10) {
            text.append(str + "开锁(双人模式)");
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x02) {
            text.append(str + "开锁取消常开");
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x01) {
            text.append(str + "开锁启用常开");
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x12) {
            text.append(str + "开锁取消常开(双人模式)");
            mTextLockHint.setText(text.toString());
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x11) {
            text.append(str + "开锁启用常开(双人模式)");
            mTextLockHint.setText(text.toString());
            mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
            setReset(3000, false);
        } else {
            if ((newmodle & 0xbf) != 0x80 && (newmodle & 0xbf) != 0x90 && (newmodle & 0xbf) != 0x82 && (newmodle & 0xbf) != 0x81 && (newmodle & 0xbf) != 0x91 && (newmodle & 0xbf) != 0x92) {
                text.append(str + "开锁");
                mTextLockHint.setText(text.toString());
                mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                setReset(3000, false);
            }
        }
    }

    /**
     * 停止定时器
     *
     * @author ZhaoLi.Wang
     * @date 2016-11-25 下午3:57:55
     */
    private void stopTimer() {
        if (AppContext.timerMap != null) {
            if (selectDeviceInfo != null) {
                controlTimeTimer = AppContext.timerMap.get(selectDeviceInfo.getUId());
            }
            if (null != controlTimeTimer) {
                AppContext.timerMap.remove(selectDeviceInfo.getUId());
                controlTimeTimer.cancel();

            }
        }

    }


    private void callSpeakerSetting(boolean f) {
        if (f) {
            tvSpeak.setText("松开结束");
            tvSpeak1.setText("松开结束");
            speak.setText("松开结束");
            if (callId != null) {
                icvss.equesAudioRecordEnable(true, callId);
                icvss.equesAudioPlayEnable(false, callId);
            }
            closeSpeaker();
        } else {
            tvSpeak.setText("按住说话");
            tvSpeak1.setText("按住说话");
            speak.setText("按住说话");
            if (callId != null) {
                icvss.equesAudioPlayEnable(true, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }
            openSpeaker();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_full_screen:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                rlEquesCall.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                surfaceView.getHolder().setFixedSize(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                llFunction.setVisibility(View.GONE);
                rl_call_button.setVisibility(View.GONE);
                right_linear_eques_call.setVisibility(View.VISIBLE);
                if (isMuteFlag) {
                    imageFullscreenMute.setImageResource(R.mipmap.full_screen_mute_press);
                } else {
                    imageFullscreenMute.setImageResource(R.mipmap.fullscreen_mute);
                }
                break;
            case R.id.btn_hangupCall:
            case R.id.iv_hangupCall:
                hangUpCall();
                break;

            case R.id.break_icon_eques_call:
                isMuteFlag = !isMuteFlag;
                if (isMuteFlag) {
                    imageFullscreenMute.setImageResource(R.mipmap.full_screen_mute_press);
                    btnMute.setImageResource(R.mipmap.eques_mute_selected);
                } else {
                    imageFullscreenMute.setImageResource(R.mipmap.fullscreen_mute);
                    btnMute.setImageResource(R.mipmap.eques_mute);
                }
                setAudioMute();// 设置静音
                break;
            case R.id.btn_mute:
                isMuteFlag = !isMuteFlag;
                if (isMuteFlag) {
                    imageFullscreenMute.setImageResource(R.mipmap.full_screen_mute_press);
                    btnMute.setImageResource(R.mipmap.eques_mute_selected);
                } else {
                    imageFullscreenMute.setImageResource(R.mipmap.fullscreen_mute);
                    btnMute.setImageResource(R.mipmap.eques_mute);
                }
                setAudioMute();// 设置静音
                break;
            case R.id.tv_screen_capture:
            case R.id.iv_screen_capture:
                String path = Api.getCamPath() + uid + File.separator;
                boolean isCreateOk = createDirectory(path);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = df.format(new Date());
                if (isCreateOk) {
                    path = StringUtils.join(path, format, ".jpg");
                    icvss.equesSnapCapture(BuddyType.TYPE_WIFI_DOOR_R22, path);
                    showToast("抓拍成功\n图片保存地址:" + path);
                } else {
                    showToast("抓拍失败");
                }
                break;

            //底层门锁图标
            case R.id.doorlock_imageicon:
                if (deviceInfos == null || deviceInfos.size() == 0) {
                    showToast("当前没有门锁");
                } else {
                    if (context_uuid != null && !context_uuid.isEmpty()) {
                        //说明有门锁绑定
                        int lockUid = 0;
                        for (int i = 0; i < deviceInfos.size(); i++) {
                            String Ieee = ByteStringUtil.bytesToHexString(deviceInfos.get(i).getIEEE()).toUpperCase();
                            if (context_uuid.equals(Ieee)) {
                                selectDeviceInfo = deviceInfos.get(i);
                                deviceName = deviceInfos.get(i).getDeviceName();
                                lockUid = deviceInfos.get(i).getUId();
                            } else {

                            }
                        }
                        //说明有门锁绑定
                        isJihuoLayoutLiner.setVisibility(View.VISIBLE);
                        tvDeviceName.setText(deviceName);

                        isControlTimeRunning(lockUid);

                        //收服务时间控制门锁时间进度
                        receiveControlTimeEvent();
                    } else {
                        //底层消失
                        llFunction.setVisibility(View.GONE);
                        //列表层显示
                        doorlockLayoutLinear.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;

            //门锁列表 取消按钮
            case R.id.quxiao_text:
                //底层出现
                llFunction.setVisibility(View.VISIBLE);
                //列表层消失
                doorlockLayoutLinear.setVisibility(View.GONE);

                break;

            //门锁激活界面 门锁图标
            case R.id.is_jihuo_doorlock_icon:

                //门锁图标
                if (!isControlAble) {// 如果门锁不可控，未激活
                    mTextLockHint.setText("门锁未激活");
                    mBtnControl.setClickable(false);
                    mBtnControl.setImageResource(R.mipmap.doorlock_icon);
                    break;
                }
                unlock();// 已经激活，输入密码开锁

                break;

            //门锁激活界面 取消按钮
            case R.id.is_jihuo_quxiao_text:
                //门锁激活界面消失
                isJihuoLayoutLiner.setVisibility(View.GONE);
                //底层出现
                llFunction.setVisibility(View.VISIBLE);
                //列表层消失
                doorlockLayoutLinear.setVisibility(View.GONE);
                break;

        }

    }


    /**
     * 判断服务是否在进行计时
     *
     * @param deviceUid
     */
    private void isControlTimeRunning(int deviceUid) {
        if (deviceUid == 0) return;
        if (AppContext.timerMap != null) {
            controlTimeTimer = AppContext.timerMap.get(deviceUid);
            if (null != controlTimeTimer) {
                if (controlTimeTimer.isRuning()) {
                    isControlAble = true;
                    mTextLockHint.setText("门锁已激活");
                    mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    mBtnControl.setClickable(true);

                } else {
                    isControlAble = false;
                    mTextLockHint.setText("门锁未激活");
                    mBtnControl.setImageResource(R.mipmap.doorlock_icon);
                    mBtnControl.setClickable(false);

                }

            } else {
                isControlAble = false;
                mTextLockHint.setText("门锁未激活");
                mBtnControl.setImageResource(R.mipmap.doorlock_icon);
                mBtnControl.setClickable(false);
            }

        } else {
            isControlAble = false;
            mTextLockHint.setText("门锁未激活");
            mBtnControl.setClickable(false);
            mBtnControl.setImageResource(R.mipmap.doorlock_icon);
        }
    }

    /**
     * 开锁
     */
    private void unlock() {
        setDoorLockState(S_DOOR_UNLOCK);
    }


    private Dialog dialog;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_10;
    private EditText et_password;

    /**
     * 设置锁的状态
     *
     * @param state
     */
    private void setDoorLockState(final int state) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除对话框的标题
        dialog.setContentView(R.layout.unlock_password_dialog);
        // 在代码中设置界面大小的方法:
        Display display = getWindowManager().getDefaultDisplay();
        // 获取屏幕宽、高
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);// 对话框底对齐
        window.setBackgroundDrawableResource(R.drawable.news_home_dialog_border);
        ViewGroup.LayoutParams windowLayoutParams = window.getAttributes(); // 获取对话框当前的参数值
        windowLayoutParams.width = (int) (display.getWidth()); // 宽度设置为屏幕的0.85
        windowLayoutParams.height = (int) (display.getHeight() * 0.6); // 高度设置为屏幕的0.24
        dialog.show();
        dialog.setCancelable(false);// 点击外面和返回建无法隐藏
        et_password = (EditText) dialog.findViewById(R.id.et_password);
        btn_1 = (Button) dialog.findViewById(R.id.btn_1);
        btn_1.setText("1");
        btn_2 = (Button) dialog.findViewById(R.id.btn_2);
        btn_2.setText("2");
        btn_3 = (Button) dialog.findViewById(R.id.btn_3);
        btn_3.setText("3");
        btn_4 = (Button) dialog.findViewById(R.id.btn_4);
        btn_4.setText("4");
        btn_5 = (Button) dialog.findViewById(R.id.btn_5);
        btn_5.setText("5");
        btn_6 = (Button) dialog.findViewById(R.id.btn_6);
        btn_6.setText("6");
        btn_7 = (Button) dialog.findViewById(R.id.btn_7);
        btn_7.setText("7");
        btn_8 = (Button) dialog.findViewById(R.id.btn_8);
        btn_8.setText("8");
        btn_9 = (Button) dialog.findViewById(R.id.btn_9);
        btn_9.setText("9");
        btn_10 = (Button) dialog.findViewById(R.id.btn_10);
        btn_10.setText("0");

        // ========================================================================关闭键盘====================
        dialog.findViewById(R.id.btn_close).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.findViewById(R.id.iv_delete).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = et_password.getSelectionStart();
                        Editable editable = et_password.getText();
                        if (editable != null && index > 0) {
                            editable.delete(index - 1, index);
                        }
                    }
                });
        dialog.findViewById(R.id.btn_positive).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 对话框确定按钮
                        String password = et_password.getText().toString();
                        if (password.length() == 6) {
                            final byte[] passwd = password.getBytes();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    if (gateWayInfo != null && selectDeviceInfo != null) {
                                        String ver = new String(gateWayInfo.getVer());
                                        int ret = ver.compareTo("2.3.3");
                                        if (ret >= 0) {
                                            try {

                                                byte[] aesByte = aes256encrypt(passwd);
                                                AppContext.getInstance().getSerialInstance().setGatedoorStateAES(selectDeviceInfo,
                                                        state, 6, aesByte);// 发送密文
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            AppContext.getInstance().getSerialInstance().setGatedoorState(selectDeviceInfo,
                                                    state, encrypt(passwd));// 发送密文
                                        }
                                    }

                                }
                            }).start();
                            dialog.dismiss();
                            // //-----------------------------------------------华丽分割线------5.7
                        } else if (TextUtils.isEmpty(password)) {
                            ToastUtils.showShort("密码不能为空!");
                        } else if (password.length() < 7) {
                            ToastUtils.showShort("请输入6位密码!");
                        }
                    }
                });
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第1个按钮
                et_password.append(btn_1.getText().toString());
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第2个按钮
                et_password.append(btn_2.getText().toString());
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第3个按钮
                et_password.append(btn_3.getText().toString());
            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第4个按钮
                et_password.append(btn_4.getText().toString());
            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 对话框第5个按钮
                et_password.append(btn_5.getText().toString());
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第6个按钮
                et_password.append(btn_6.getText().toString());
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第7个按钮
                et_password.append(btn_7.getText().toString());
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第8个按钮
                et_password.append(btn_8.getText().toString());
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第9个按钮
                et_password.append(btn_9.getText().toString());
            }
        });
        btn_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第10个按钮
                et_password.append(btn_10.getText().toString());
            }
        });
    }

   /* */

    /**
     * 设置锁的状态
     *//*
    private void setDoorLockState(final int state) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除对话框的标题
        dialog.setContentView(R.layout.unlock_password_dialog);
        // 在代码中设置界面大小的方法:
        Display display = getWindowManager().getDefaultDisplay();
        // 获取屏幕宽、高
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);// 对话框底对齐
        window.setBackgroundDrawableResource(R.drawable.news_home_dialog_border);
        ViewGroup.LayoutParams windowLayoutParams = window.getAttributes(); // 获取对话框当前的参数值
        windowLayoutParams.width = (int) (display.getWidth()); // 宽度设置为屏幕的0.85
        windowLayoutParams.height = (int) (display.getHeight() * 0.6); // 高度设置为屏幕的0.24
        dialog.show();
        dialog.setCancelable(false);// 点击外面和返回建无法隐藏
        et_password = (EditText) dialog.findViewById(R.id.et_password);
        btn_1 = (Button) dialog.findViewById(R.id.btn_1);
        btn_2 = (Button) dialog.findViewById(R.id.btn_2);
        btn_3 = (Button) dialog.findViewById(R.id.btn_3);
        btn_4 = (Button) dialog.findViewById(R.id.btn_4);
        btn_5 = (Button) dialog.findViewById(R.id.btn_5);
        btn_6 = (Button) dialog.findViewById(R.id.btn_6);
        btn_7 = (Button) dialog.findViewById(R.id.btn_7);
        btn_8 = (Button) dialog.findViewById(R.id.btn_8);
        btn_9 = (Button) dialog.findViewById(R.id.btn_9);
        btn_10 = (Button) dialog.findViewById(R.id.btn_10);

        int[] a = {4, 3, 1, 0, 5, 9, 2, 6, 8, 7};
        Button[] btn = {btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7,
                btn_8, btn_9, btn_10};
        int[] b = new int[10];// 乱序键盘值数组
        int c = (int) (Math.random() * 10);// 0-9的随机数
        // 赋值于乱序键盘值数组
        for (int i = 0; i < b.length; i++) {
            if (c + i < b.length) {
                b[i] = a[c + i];
            } else {

                b[i] = a[i - b.length + c];// 比如随机数为7，把a[7]赋给b[0],a[]赋完以后，再从a【0】开始赋值给b【】
            }
        }
        // ========================================================================给按钮赋值===================
        for (int i = 0; i < btn.length; i++) {
            btn[i].setText(String.valueOf(b[i]));
        }
        // ========================================================================关闭键盘====================
        dialog.findViewById(R.id.btn_close).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.findViewById(R.id.iv_delete).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = et_password.getSelectionStart();
                        Editable editable = et_password.getText();
                        if (editable != null && index > 0) {
                            editable.delete(index - 1, index);
                        }
                    }
                });
        dialog.findViewById(R.id.btn_positive).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 对话框确定按钮
                        String password = et_password.getText().toString();
                        if (password.length() == 6) {
                            final byte[] passwd = password.getBytes();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    if (gateWayInfo != null && selectDeviceInfo != null) {
                                        String ver = new String(gateWayInfo.getVer());
                                        int ret = ver.compareTo("2.3.3");
                                        if (ret >= 0) {
                                            try {

                                                byte[] aesByte = aes256encrypt(passwd);
                                                AppContext.getInstance().getSerialInstance().setGatedoorStateAES(selectDeviceInfo,
                                                        state, 6, aesByte);// 发送密文
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            AppContext.getInstance().getSerialInstance().setGatedoorState(selectDeviceInfo,
                                                    state, encrypt(passwd));// 发送密文
                                        }
                                    }

                                }
                            }).start();
                            dialog.dismiss();
                            // //-----------------------------------------------华丽分割线------5.7
                        } else if (TextUtils.isEmpty(password)) {
                            ToastUtils.showShort("密码不能为空!");
                        } else if (password.length() < 7) {
                            ToastUtils.showShort("请输入6位密码!");
                        }
                    }
                });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第1个按钮
                et_password.append(btn_1.getText().toString());
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第2个按钮
                et_password.append(btn_2.getText().toString());
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第3个按钮
                et_password.append(btn_3.getText().toString());
            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第4个按钮
                et_password.append(btn_4.getText().toString());
            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 对话框第5个按钮
                et_password.append(btn_5.getText().toString());
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第6个按钮
                et_password.append(btn_6.getText().toString());
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第7个按钮
                et_password.append(btn_7.getText().toString());
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第8个按钮
                et_password.append(btn_8.getText().toString());
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第9个按钮
                et_password.append(btn_9.getText().toString());
            }
        });
        btn_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第10个按钮
                et_password.append(btn_10.getText().toString());
            }
        });
    }*/

    //得到有效字节长度
    public int getVirtualValueLength(byte[] buf) {
        int i = 0;
        for (; i < buf.length; i++) {
            if (buf[i] == (byte) 0) {
                break;
            }
        }
        return i;
    }

    public byte[] aes256encrypt(byte[] password) {

        byte[] snid;
        byte[] name;
        byte[] psd;
        snid = ByteStringUtil.hexStringToBytes(gateWayInfo.getSnid());
        byte[] name1 = gateWayInfo.getUname();
        byte[] psd1 = gateWayInfo.getPasswd();
        //由于返回回来的name和psd存在0x00，所以要先获取有效字节长度
        int length = getVirtualValueLength(name1);
        int length1 = getVirtualValueLength(psd1);
        name = new byte[length];
        psd = new byte[length1];
        System.arraycopy(name1, 0, name, 0, length);
        System.arraycopy(psd1, 0, psd, 0, length1);

        byte[] key = new byte[32];
        byte key1[] = {0x46, 0x45, 0x49, 0x42, 0x49, 0x47};// 加密密钥
        byte newPaString[] = new byte[6];// 异或后的密码
        //  password[i]&0x0f  此处密码需要先转化，再和密钥异或
        for (int i = 0; i < newPaString.length; i++) {
            newPaString[i] = (byte) (key1[i] ^ (password[i] & 0x0f));
        }

        int nameLength = name.length;
        int psdLength = psd.length;
        int snidLength = snid.length;
        //拼接key 密钥
        System.arraycopy(name, 0, key, 0, nameLength);
        System.arraycopy(psd, 0, key, nameLength, psdLength);
        System.arraycopy(snid, 0, key, nameLength + psdLength, snidLength);

        //获取长地址
        byte[] ieee = selectDeviceInfo.getIEEE();
        //获取endpoint
        byte ep = (byte) ((selectDeviceInfo.getUId() >> 16) & 0x00ff);
        //拼接成一个数组
        byte[] longAdd = new byte[9];
        System.arraycopy(ieee, 0, longAdd, 0, 8);
        longAdd[8] = ep;

        //将长地址加入到key数组中
        System.arraycopy(longAdd, 0, key, nameLength
                + psdLength + snidLength, 9);

        byte[] ssss = "WANGLI2016".getBytes();
        int  myLen=32-(nameLength
                + psdLength + snidLength + 9);
        //将补位字节加入
        System.arraycopy(ssss, 0, key, nameLength
                + psdLength + snidLength + 9, myLen);
        //时间戳，加入28800（8个小时），与网关时间同步
        int time = (int) (System.currentTimeMillis() / 1000 + 28800);
        String timeString = Integer.toHexString(time);
        byte[] times = ByteStringUtil.hexStringToBytes(timeString);
        // byte[] times = intToBytes2(time);
        byte[] doorPsd = new byte[32];
        //拼接加密前明文
        System.arraycopy(newPaString, 0, doorPsd, 0, 6);
        System.arraycopy(times, 0, doorPsd, 28, 4);
        byte[] aesPsd = null;
        try {
            //AES 256加密，具体操作看加密文件夹，需要替换jdk下的包
            aesPsd = Aes256EncodeUtil.encrypt(doorPsd, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aesPsd;
    }

    /**
     * 开锁密码解密
     *
     * @param password
     * @return
     */
    public byte[] encrypt(byte[] password) {
        byte key[] = {0x46, 0x45, 0x49, 0x42, 0x49, 0x47};// 加密密钥
        byte newPaString[] = new byte[6];// 密文
        for (int i = 0; i < newPaString.length; i++) {
            newPaString[i] = (byte) (key[i] ^ password[i]);
        }

        return newPaString;
    }

    /**
     * 接收飞比设备刷新页面
     */
    private void receiveFbeeDevice() {
        //注册RXbus接收飞比设备
//        mSubscription = RxBus.getInstance().toObservable(DeviceInfo.class)
//                .compose(TransformUtils.<DeviceInfo>defaultSchedulers())
//                .subscribe(new Action1<DeviceInfo>() {
//                    @Override
//                    public void call(DeviceInfo event) {
//                        LogUtil.e("deviceinfo",event.toString());
//                        //获取门锁列表
//                        if(null != event&&event.getDeviceId()==10){
//                            for (int i = 0; i < deviceInfos.size(); i++) {
//                                if (deviceInfos.get(i).getUId() == event.getUId()) {
//                                    return;
//                                }
//                            }
//                            deviceInfos.add(event);
//                            //adapter.notifyDataSetChanged();
//                        }
//                    }
//                });
//        mCompositeSubscription.add(mSubscription);

        AppContext.getInstance().getSerialInstance().getDevices();
    }

    private void hangUpCall() {
        if (callId != null) {
            icvss.equesCloseCall(callId);
        }
        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (uid != null) {
            callId = icvss.equesOpenCall(uid, holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            holder.setFixedSize(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        } else {
            holder.setFixedSize(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            finish();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            rlEquesCall.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, AppUtil.dp2px(this, 300)));
            surfaceView.getHolder().setFixedSize(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            llFunction.setVisibility(View.VISIBLE);
            rl_call_button.setVisibility(View.VISIBLE);
            right_linear_eques_call.setVisibility(View.GONE);
        }
    }

    int timeJianGe = 0;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    ++timeJianGe;
                    String timeFormat = getTime(timeJianGe);
                    txTime.setText(timeFormat);
                }
            });
        }
    };


    public void closeSpeaker() {
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openSpeaker() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        current, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAudioMute() {
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, isMuteFlag);
        if (isMuteFlag) {
            if (callId != null) {
                icvss.equesAudioPlayEnable(false, callId);
                icvss.equesAudioRecordEnable(false, callId);
            }
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
            callSpeakerSetting(false);
        }
    }

    public boolean createDirectory(String filePath) {
        if (null == filePath) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();

    }


    /**
     * 猫眼视频计时
     */
    public String getTime(int time) {
        String hFormat = null;
        String minutesFormat = null;
        String secondsFormat = null;
        int hour = time / 3600;
        if (hour < 10) {
            hFormat = "0" + String.format("%d", hour);
        } else {
            hFormat = String.format("%d", hour);
        }
        int minutes = time % 3600 / 60;
        if (minutes < 10) {
            minutesFormat = "0" + String.format("%d", minutes);
        } else {
            minutesFormat = String.format("%d", minutes);
        }
        int seconds = time % 60;
        if (seconds < 10) {
            secondsFormat = "0" + String.format("%d", seconds);
        } else {
            secondsFormat = String.format("%d", seconds);
        }

        String timeFormat = hFormat + ":" + minutesFormat + ":" + secondsFormat;
        return timeFormat;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (callId != null) {
            icvss.equesCloseCall(callId);
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        //门锁激活界面出现
        isJihuoLayoutLiner.setVisibility(View.VISIBLE);
        selectDeviceInfo = deviceInfos.get(position);
        if (selectDeviceInfo == null) return;
        //判断服务是否在进行计时
        int deviceUid = selectDeviceInfo.getUId();
        tvDeviceName.setText(selectDeviceInfo.getDeviceName());
        isControlTimeRunning(deviceUid);

        //收服务时间控制门锁时间进度
        receiveControlTimeEvent();

    }

    @Override
    public void resAddDevices(BaseResponse bean) {

    }

    @Override
    public void resDeleteDevices(BaseResponse bean) {

    }

    @Override
    public void queryDevicesResult(QueryDeviceListResponse bean) {
        QueryDeviceListResponse.BodyBean body = bean.getBody();
        //isControlTimeRunning(lockUid);
        context_uuid = body.getContext_uuid();
        for (int i = 0; i < deviceInfos.size(); i++) {
            String ieee = ByteStringUtil.bytesToHexString(deviceInfos.get(i).getIEEE()).toUpperCase();
            if (ieee.equals(context_uuid)) {
                selectDeviceInfo = deviceInfos.get(i);
                return;
            } else {
                String uId = deviceInfos.get(i).getUId() + "";
                if (context_uuid.equals(uId)) {
                    selectDeviceInfo = deviceInfos.get(i);
                    return;
                }
            }
        }
        // 开始获取门锁允许被控制的状态
        /*if (selectDeviceInfo != null) {
            beginGetAbleState(selectDeviceInfo);
        }*/
    }

    @Override
    public void resReqGateWayInfo(QueryGateWayInfoReq body) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
