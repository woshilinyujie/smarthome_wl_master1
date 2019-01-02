package com.fbee.smarthome_wl.ui.equesdevice.videocall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatRecordEvent;
import com.bairuitech.anychat.AnyChatTransDataEvent;
import com.bairuitech.anychat.AnyChatUserInfoEvent;
import com.bairuitech.anychat.AnyChatVideoCallEvent;
import com.bairuitech.anychat.config.ConfigHelper;
import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.DoorLockDeviceAdapter;
import com.fbee.smarthome_wl.adapter.itemdecoration.SpaceItemDecoration;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.GateWayInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.constant.DoorLockGlobal;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.event.ControlTimeEvent;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceListResponse;
import com.fbee.smarthome_wl.service.ControlTimeService;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverContract;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverPresenter;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TimerCount;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by wl on 2017/1/9.
 */
public class JiucaiVideoActivity extends BaseActivity<AddOrDelDevicesToSeverContract.Presenter> implements AnyChatVideoCallEvent,
        AnyChatUserInfoEvent, AnyChatRecordEvent, AnyChatTransDataEvent, AnyChatBaseEvent, BaseRecylerAdapter.OnItemClickLitener, AddOrDelDevicesToSeverContract.View {
    private SurfaceView videoSurface;
    private GridView gv;
    private String[] texts;
    private List<CommonAdapter.GvData> datas = new ArrayList<CommonAdapter.GvData>();
    private int[] imgs = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
    private DoorLockDeviceAdapter adapter;
    private int videoIndex = 0;
    private AnyChatCoreSDK anychat;
    private static int dwTargetUserId;
    private int roomId;
    private String version;
    private boolean isMicMute;
    private boolean bSelfVideoOpened = false;
    private boolean isSpeakerMute = false;
    boolean bOtherVideoOpened = false;
    boolean isVideoIng = false;
    private static final int COUNTTIMES = 5;
    GateWayInfo gateWayInfo;
    private TextView countTimes;
    private TextView name;
    private LinearLayout device;
    private RecyclerView doorlockRecycler;
    private ImageView mBtnControl;
    private TextView DeviceName;
    private TextView mTextLockHint;
    private RelativeLayout doorLock;
    private int screenWidth;
    private int size;
    private int itemWidth;
    private LinearLayout.LayoutParams params;
    private List<DeviceInfo> mSwitchinfos;
    private DeviceInfo selectDevice;
    private ControlTimeService.TimeCount timer;
    private HashMap<Integer, String> mControlStateHashMap = new HashMap<Integer, String>();
    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.RECORD_AUDIO};
    private LinearLayout ima;
    private String path;
    private RelativeLayout rlEquesCall;
    private CommonAdapter adapter1;
    private String devicesNum;
    private String context_uuid;
    private List<DeviceInfo> deviceInfos;
    private String deviceName;
    private DeviceInfo selectDeviceInfo;
    private RelativeLayout isJihuoLayoutLiner;
    private TextView tvDeviceName;
    private TimerCount controlTimeTimer;
    private LinearLayout doorlockLayoutLinear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.jiucai_video_call);
    }

    /**
     * 手动获取权限
     * Created by ZhaoLi.Wang on 2016/12/14 19:13
     */
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

    private void initSdk() {
        try {
            anychat = AnyChatCoreSDK.getInstance();
            anychat.SetBaseEvent(this);
            anychat.SetVideoCallEvent(this);
            anychat.SetUserInfoEvent(this);
            anychat.SetRecordSnapShotEvent(this);
            //anychat.SetStateChgEvent();
            anychat.mSensorHelper.InitSensor(this);
            anychat.SetTransDataEvent(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextView Cancle;

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

    @Override
    protected void initView() {
        initSdk();
        initApi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            verifyRecordPermissions(this);
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int voice = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (voice == manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, voice - 2, 0);
        }
        dwTargetUserId = getIntent().getIntExtra("dwUserId", 0);
        version = getIntent().getStringExtra("DeviceVer");
        path = getIntent().getStringExtra("path");
        if (path == null) {
            path = Api.getCamPath() + "/JiuCai";
        }
        devicesNum = getIntent().getStringExtra("devicesNum");

        if (version != null && version.compareTo("null") != 0 && version.compareTo("1.0.1") > 0) {
            isMicMute = true;
        } else {
            isMicMute = false;
        }
        roomId = getIntent().getIntExtra("room", 0);
        anychat.EnterRoom(roomId, "");
        mSwitchinfos = AppContext.getmDoorLockDevices();
        size = mSwitchinfos.size();
        if (size == 1) {
            selectDevice = mSwitchinfos.get(0);
        }

        int length = 110;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        itemWidth = (int) (length * density);
        params = new LinearLayout.LayoutParams(gridviewWidth,
                LinearLayout.LayoutParams.MATCH_PARENT);

//        initTimerCheckAv();
        mHandler.sendEmptyMessage(COUNTTIMES);
        videoSurface = (SurfaceView) findViewById(R.id.surface_view);
        countTimes = (TextView) findViewById(R.id.call_time);
        name = (TextView) findViewById(R.id.name);
        name.setText(getIntent().getStringExtra("alias"));
        isJihuoLayoutLiner = (RelativeLayout) findViewById(R.id.is_jihuo_layout_liner);
        device = (LinearLayout) findViewById(R.id.doorlock_layout_linear);
        Cancle = (TextView) findViewById(R.id.quxiao_text);
        doorlockRecycler = (RecyclerView) findViewById(R.id.doorlock_recycler);
        mBtnControl = (ImageView) findViewById(R.id.is_jihuo_doorlock_icon);
        doorlockLayoutLinear = (LinearLayout) findViewById(R.id.doorlock_layout_linear);
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        rlEquesCall = (RelativeLayout) findViewById(R.id.surface_remote);
        doorLock = (RelativeLayout) findViewById(R.id.door_lock);
        mTextLockHint = (TextView) findViewById(R.id.is_jihuo_doorlock_text_state);
        ima = (LinearLayout) findViewById(R.id.linear_CaptureDefault);
        TextView quxiao_lock = (TextView) findViewById(R.id.is_jihuo_quxiao_text);
        quxiao_lock.setOnClickListener(this);
        Cancle.setOnClickListener(this);
//        quxiao.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                doorLock.setVisibility(View.GONE);
//                gv.setEnabled(true);
//            }
//        });
//        mBtnControl.setOnClickListener(new View.OnClickListener() {
//
//            @SuppressLint("NewApi")
//            @Override
//            public void onClick(View v) {
//                if (!isControlAble) {// 如果门锁不可控，未激活
//                    mTextLockHint.setText("门锁未激活");
//                    mBtnControl.setImageAlpha(100);
//                    mTextLockHint.setTextColor(Color.BLACK);// 显示黑色字体
//                } else {
//                    unlock();// 已经激活，输入密码开锁
//                }
//            }
//        });

        ConfigHelper.getConfigHelper().LoadConfig(this);
        // 判断是否显示本地摄像头切换图标
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
            if (AnyChatCoreSDK.mCameraHelper.GetCameraNumber() > 1) {
                // 默认打开前置后置摄像头
                AnyChatCoreSDK.mCameraHelper
                        .SelectVideoCapture(AnyChatCoreSDK.mCameraHelper.CAMERA_FACING_BACK);
            }
        } else {
            String[] strVideoCaptures = anychat.EnumVideoCapture();
            if (strVideoCaptures != null && strVideoCaptures.length > 1) {
                //				mImgSwitch.setVisibility(View.VISIBLE);
                // 默认打开前置摄像头
                for (int i = 0; i < strVideoCaptures.length; i++) {
                    String strDevices = strVideoCaptures[i];
                    if (strDevices.indexOf("Front") >= 0) {
                        anychat.SelectVideoCapture(strDevices);
                        break;
                    }
                }
            }
        }
        gv = (GridView) findViewById(R.id.video_gv);
//        mSwitchListAdapter = new DeviceListAdapter(this,
//                mSwitchinfos);
//        mSwitchListAdapter.setOnDeviceClickListener(this);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        rlEquesCall.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                        videoSurface.getHolder().setFixedSize(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                        break;
                    case 1:
                        //截屏
                        if (path != null) {
                            anychat.SetSDKOptionString(
                                    AnyChatDefine.BRAC_SO_SNAPSHOT_TMPDIR,
                                    path + "/");
                            Log.e("九彩视频抓拍地址", path);
                        }
                        int dwFlags1 = AnyChatDefine.ANYCHAT_RECORD_FLAGS_SNAPSHOT;
                        anychat.SnapShot(dwTargetUserId, dwFlags1, 0);

                        break;
                    case 2:
                        //录制
                        if (!isVideoIng) {
                            AnyChatCoreSDK.SetSDKOptionInt(
                                    AnyChatDefine.BRAC_SO_RECORD_FILETYPE, 0);
                            if (adapter1 != null) {
                                datas.get(2).text = "结束录制";
                                adapter1.notifyDataSetChanged();
                            }
                            if (path != null) {
                                anychat.SetSDKOptionString(
                                        AnyChatDefine.BRAC_SO_RECORD_TMPDIR,
                                        path + "/");

                                int dwFlags = AnyChatDefine.ANYCHAT_RECORD_FLAGS_VIDEO
                                        + AnyChatDefine.ANYCHAT_RECORD_FLAGS_AUDIO;
                                anychat.StreamRecordCtrlEx(dwTargetUserId, 1, dwFlags, 0, "");
                                showToast("开始录制！");
                            }
                            isVideoIng = !isVideoIng;
                        } else {
                            if (adapter1 != null) {
                                datas.get(2).text = "开始录制";
                                adapter1.notifyDataSetChanged();
                            }
                            anychat.StreamRecordCtrlEx(dwTargetUserId, 0, 0, 0, "");
                            showToast("录制成功！" + path);
                            isVideoIng = !isVideoIng;
                        }
                        break;
                    case 3:
                        if ((anychat.GetSpeakState(-1) == 1)) {
                            if (adapter1 != null) {
                                datas.get(3).text = "打开麦克";
                                adapter1.notifyDataSetChanged();
                                anychat.UserSpeakControl(-1, 0);
                                showToast("麦克已关闭");
                            }
                        } else {
                            if (adapter1 != null) {
                                datas.get(3).text = "关闭麦克";
                                adapter1.notifyDataSetChanged();
                                anychat.UserSpeakControl(-1, 1);
                                showToast("麦克风已打开");
                            }
                        }
                        break;
                    case 4:
                        finish();
                        break;
                    case 5:
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
                                gv.setVisibility(View.GONE);
                                //列表层显示
                                doorlockLayoutLinear.setVisibility(View.VISIBLE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                }
            }
        });
        if (AnyChatCoreSDK
                .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
            videoIndex = anychat.mVideoHelper.bindVideo(videoSurface
                    .getHolder());
            anychat.mVideoHelper.SetVideoUser(videoIndex, dwTargetUserId);
        }
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

    private void unlock() {
//        setDoorLockState(DoorLockActivity.S_DOOR_UNLOCK);
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

    private EditText et_password;
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
    private LinearLayout mPswLayout;
    private LinearLayout linear_passwordDialog;

//    private void setDoorLockState(final int state) {
//        linear_passwordDialog = (LinearLayout) findViewById(R.id.linear_passwordDialog);
//        mPswLayout = (LinearLayout) findViewById(R.id.include_psw_layout);
//        LinearLayout.LayoutParams pswlayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.6));
//        mPswLayout.setLayoutParams(pswlayoutparams);
//        linear_passwordDialog.setVisibility(View.VISIBLE);
//        //mPswLayout.setVisibility(View.VISIBLE);
//
//        et_password = (EditText) findViewById(R.id.et_password);
//        btn_1 = (Button) findViewById(R.id.btn_1);
//        btn_2 = (Button) findViewById(R.id.btn_2);
//        btn_3 = (Button) findViewById(R.id.btn_3);
//        btn_4 = (Button) findViewById(R.id.btn_4);
//        btn_5 = (Button) findViewById(R.id.btn_5);
//        btn_6 = (Button) findViewById(R.id.btn_6);
//        btn_7 = (Button) findViewById(R.id.btn_7);
//        btn_8 = (Button) findViewById(R.id.btn_8);
//        btn_9 = (Button) findViewById(R.id.btn_9);
//        btn_10 = (Button) findViewById(R.id.btn_10);
//
//
//        int[] a = {4, 3, 1, 0, 5, 9, 2, 6, 8, 7};
//        Button[] btn = {btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7,
//                btn_8, btn_9, btn_10};
//        int[] b = new int[10];// 乱序键盘值数组
//        int c = (int) (Math.random() * 10);// 0-9的随机数
//        // 赋值于乱序键盘值数组
//        for (int i = 0; i < b.length; i++) {
//            if (c + i < b.length) {
//                b[i] = a[c + i];
//            } else {
//
//                b[i] = a[i - b.length + c];// 比如随机数为7，把a[7]赋给b[0],a[]赋完以后，再从a【0】开始赋值给b【】
//            }
//
//        }
//        // ========================================================================给按钮赋值===================
//        for (int i = 0; i < btn.length; i++) {
//            btn[i].setText(String.valueOf(b[i]));
//        }
//
//        // ========================================================================关闭键盘====================
//        findViewById(R.id.btn_close).setOnClickListener(
//                new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        linear_passwordDialog.setVisibility(View.GONE);
//                        //mPswLayout.setVisibility(View.GONE);
//                        //设置grivdview可点击
//                        if (!gv.isEnabled()) {
//                            gv.setEnabled(true);
//                        }
//
//                    }
//                });
//
//        findViewById(R.id.iv_delete).setOnClickListener(
//                new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        int index = et_password.getSelectionStart();
//                        Editable editable = et_password.getText();
//                        if (editable != null && index > 0) {
//                            editable.delete(index - 1, index);
//                        }
//
//                    }
//                });
//        findViewById(R.id.btn_positive).setOnClickListener(
//                new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // 对话框确定按钮
//                        String password = et_password.getText().toString();
//                        if (password.length() == 6) {
//                            final byte[] passwd = password.getBytes();
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//
////                                    if (gateWayInfo != null && selectDeviceInfo != null) {
////                                        String ver = new String(gateWayInfo.getVer());
////                                        int ret = ver.compareTo("2.3.3");
////                                        if (ret >= 0) {
////                                            try {
////
////                                                byte[] aesByte = aes256encrypt(passwd);
////                                                AppContext.getInstance().getSerialInstance().setGatedoorStateAES(selectDeviceInfo,
////                                                        state, 6, aesByte);// 发送密文
////                                            } catch (Exception e) {
////                                                e.printStackTrace();
////                                            }
////                                        } else {
////                                            AppContext.getInstance().getSerialInstance().setGatedoorState(selectDeviceInfo,
////                                                    state, encrypt(passwd));// 发送密文
////                                        }
////                                    }
//
//                                }
//                            }).start();
////                            dialog.dismiss();
//                            // //-----------------------------------------------华丽分割线------5.7
//                        } else if (TextUtils.isEmpty(password)) {
//                            ToastUtils.showShort("密码不能为空!");
//                        } else if (password.length() < 7) {
//                            ToastUtils.showShort("请输入6位密码!");
//                        }
//                    }
//                });
//
//        btn_1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第1个按钮
//                et_password.append(btn_1.getText().toString());
//
//            }
//        });
//        btn_2.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第2个按钮
//                et_password.append(btn_2.getText().toString());
//
//            }
//        });
//        btn_3.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第3个按钮
//                et_password.append(btn_3.getText().toString());
//
//            }
//        });
//        btn_4.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第4个按钮
//                et_password.append(btn_4.getText().toString());
//            }
//        });
//        btn_5.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第5个按钮
//                et_password.append(btn_5.getText().toString());
//
//            }
//        });
//        btn_6.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第6个按钮
//                et_password.append(btn_6.getText().toString());
//
//            }
//        });
//        btn_7.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第7个按钮
//                et_password.append(btn_7.getText().toString());
//
//            }
//        });
//        btn_8.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第8个按钮
//                et_password.append(btn_8.getText().toString());
//
//            }
//        });
//        btn_9.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第9个按钮
//                et_password.append(btn_9.getText().toString());
//            }
//        });
//        btn_10.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第10个按钮
//                et_password.append(btn_10.getText().toString());
//
//            }
//        });
//
//    }

    public static byte[] encrypt(byte[] password) {
        byte key[] = {0x46, 0x45, 0x49, 0x42, 0x49, 0x47};// 加密密钥

        byte newPaString[] = new byte[6];// 密文
        for (int i = 0; i < newPaString.length; i++) {
            newPaString[i] = (byte) (key[i] ^ password[i]);

        }

        return newPaString;

    }

    @Override
    protected void initData() {
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
        createPresenter(new AddOrDelDevicesToSeverPresenter(this));
        texts = new String[]{"全屏", "截屏", "开始录制", "打开麦克", "结束通话", "开锁"};
        receiveGateWayInfo();
        for (int i = 0; i < texts.length; i++) {
            datas.add(new CommonAdapter.GvData(texts[i], imgs[i]));
        }
        adapter1 = new CommonAdapter(this, datas);
        gv.setAdapter(adapter1);
        //网络请求

        QueryDevicesListInfo bodyEntity = new QueryDevicesListInfo();
        bodyEntity.setUuid(devicesNum);
        bodyEntity.setVendor_name(FactoryType.EQUES);
        presenter.queryDevices(bodyEntity);


    }

    public static final int MSG_CHECKAV = 1;
    public static final int MSG_FORCE_FINISH = 4;//防止无网络时，无法退出界面的问题，设置超时退出
    private Timer mTimerCheckAv;
    private TimerTask mTimerTask;
    private int timeJianGe = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CHECKAV:
                    CheckVideoStatus();
                    break;
                case COUNTTIMES:
                    String timeFormat = null;
                    ++timeJianGe;
                    timeFormat = getTime(timeJianGe);
                    countTimes.setText(timeFormat);
                    /*if ((timeJianGe==30)&&(!status.equals(Method.METHOD_VIDEOPLAY_STATUS_PLAYING))){
                        ToastUtil.showCenterToast(VideoCallActivity.this,"当前网络环境差,请返回重试！");
                    }*/
                    mHandler.sendEmptyMessageDelayed(COUNTTIMES, 1000);
                    break;
            }

        }
    };

    // --------===========开始获取门锁允许被控制的状态
    private Handler videoHandler = new Handler();

    private void beginGetAbleState() {
        videoHandler.postDelayed(runable, 0);
        videoHandler.postDelayed(runable, 3000); // 防止网络差丢包
        //	videoHandler.postDelayed(check_if_reply_ready, 5000); //4秒钟后，检查网关是否回复激活
    }

    // --------===========结束获取门锁允许被控制的状态

    // ==============创建Runnable对象

    private Runnable runable = new Runnable() {

        @Override
        public void run() {
//            mSerial.GetControlableState(selectDevice);// 获取门锁允许被控制的状态
        }
    };


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

//    private void initTimerCheckAv() {
//        if (mTimerCheckAv == null)
//            mTimerCheckAv = new Timer();
//        mTimerTask = new TimerTask() {
//            @Override
//            public void run() {
//                mHandler.sendEmptyMessage(MSG_CHECKAV);
//            }
//        };
//        mTimerCheckAv.schedule(mTimerTask, 1000, 100);
//    }

    // 判断视频是否已打开
    private void CheckVideoStatus() {
        try {
            if (!bOtherVideoOpened) {
                // 摄像头的状态：0，没有摄像头；1,有摄像头但没打开；2，摄像头已打开
                int camerastate = anychat.GetCameraState(dwTargetUserId);
                int myselfcamer = anychat.GetCameraState(-1);
                int videowidth = anychat.GetUserVideoWidth(dwTargetUserId);
                if (camerastate == 0) {// camerastate == 0 没有摄像头
                    Log.e("ee", "没有找到摄像头");
                    SurfaceHolder holder = videoSurface.getHolder();
                    // 如果是采用内核视频显示（非Java驱动），则需要设置Surface的参数
                    if (AnyChatCoreSDK
                            .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                        holder.setFormat(PixelFormat.RGB_565);
                        holder.setFixedSize(anychat.GetUserVideoWidth(-1),
                                anychat.GetUserVideoHeight(-1));
                    }
                    Surface s = holder.getSurface();
                    if (AnyChatCoreSDK
                            .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                        anychat.mVideoHelper.SetVideoUser(videoIndex,
                                dwTargetUserId);
                    } else {
                        anychat.SetVideoPos(dwTargetUserId, s, 0, 0, 0, 0);
                    }
                    videoSurface.setBackgroundColor(Color.TRANSPARENT);
                } else if (camerastate == 2 && videowidth != 0) {
                /*
                 * camerastate == 2 摄像头打开 videowidth == 0 打开视频设备失败
				 */
                    SurfaceHolder holder = videoSurface.getHolder();
                    // 如果是采用内核视频显示（非Java驱动），则需要设置Surface的参数
                    if (AnyChatCoreSDK
                            .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                        holder.setFormat(PixelFormat.RGB_565);
                        holder.setFixedSize(anychat.GetUserVideoWidth(-1),
                                anychat.GetUserVideoHeight(-1));
                    }
                    Surface s = holder.getSurface();
                    if (AnyChatCoreSDK
                            .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                        anychat.mVideoHelper.SetVideoUser(videoIndex,
                                dwTargetUserId);
                    } else {
                        anychat.SetVideoPos(dwTargetUserId, s, 0, 0, 0, 0);
                    }

                    videoSurface.setBackgroundColor(Color.TRANSPARENT);

                    bOtherVideoOpened = true;
                } else {
                    SurfaceHolder holder = videoSurface.getHolder();
                    if (AnyChatCoreSDK
                            .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) != AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                        holder.setFormat(PixelFormat.RGB_565);
                        holder.setFixedSize(anychat.GetUserVideoWidth(-1), anychat.GetUserVideoHeight(-1));
                    }
                    Surface s = holder.getSurface();
                    if (AnyChatCoreSDK
                            .GetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL) == AnyChatDefine.VIDEOSHOW_DRIVER_JAVA) {
                        anychat.mVideoHelper.SetVideoUser(videoIndex,
                                dwTargetUserId);
                    } else {
                        anychat.SetVideoPos(dwTargetUserId, s, 0, 0, 0, 0);
                    }
                    videoSurface.setBackgroundColor(Color.TRANSPARENT);
                    bOtherVideoOpened = true;
                }
            } else {
                //    Log.e(TAG, "bOtherVideoOpened:" + bOtherVideoOpened);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quxiao_text:
                device.setVisibility(View.GONE);
                gv.setVisibility(View.VISIBLE);
                gv.setEnabled(true);
                break;
            case R.id.is_jihuo_quxiao_text:
                //门锁激活界面消失
                isJihuoLayoutLiner.setVisibility(View.GONE);
                //底层出现
                gv.setVisibility(View.VISIBLE);
                //列表层消失
                doorlockLayoutLinear.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //forceClose();
    }


    @Override
    public void onBackPressed() {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            finish();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onRestart() {
        mHandler.sendEmptyMessage(MSG_FORCE_FINISH);
        super.onRestart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            name.setVisibility(View.VISIBLE);
            gv.setVisibility(View.VISIBLE);

            rlEquesCall.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, AppUtil.dp2px(JiucaiVideoActivity.this, 300)));
//            videoSurface.getHolder().setFixedSize(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight() * 3 / 7);
            videoSurface.getHolder().setFixedSize(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight() - gv.getHeight());
        } else {
            name.setVisibility(View.GONE);
            gv.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        super.onConfigurationChanged(newConfig);
    }


    private void speakCameraControl(int user, int type) {
        try {
            if (user == -1) {
                //不需要打开本地摄像头，只需要打开speaker
                anychat.UserSpeakControl(user, type);
                anychat.UserCameraControl(user, 0);
            } else {
                anychat.UserSpeakControl(user, type);
                anychat.UserCameraControl(user, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            if (isMicMute == true) {
                speakCameraControl(-1, 0);
                Log.i("jiucaivideoactivity", "MIC 静音");
            } else {
                speakCameraControl(-1, 1);
                Log.i("jiucaivideoactivity", "MIC 打开");
            }
            bSelfVideoOpened = false;
        }
    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        try {
            if (isSpeakerMute == true) {
                anychat.UserSpeakControl(dwTargetUserId, 0);
                anychat.UserCameraControl(dwTargetUserId, 1);
            } else {
                speakCameraControl(dwTargetUserId, 1);
            }
            bOtherVideoOpened = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        try {
            if (isSpeakerMute == true) {
                anychat.UserSpeakControl(dwTargetUserId, 0);
                anychat.UserCameraControl(dwTargetUserId, 1);
            } else {
                speakCameraControl(dwTargetUserId, 1);
            }

            bOtherVideoOpened = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        if (dwErrorCode == 0) {
            finish();
        }
    }

    @Override
    public void OnAnyChatRecordEvent(int dwUserId, int dwErrorCode, String lpFileName, int dwElapse, int dwFlags, int dwParam, String lpUserStr) {
    }

    @Override
    public void OnAnyChatSnapShotEvent(int dwUserId, int dwErrorCode, String lpFileName, int dwFlags, int dwParam, String lpUserStr) {
        LogUtil.e("OnAnyChatSnapShotEvent", "调用了");
        showToast("截屏成功");
    }

    @Override
    public void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen, int wparam, int lparam, int taskid) {

    }

    @Override
    public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {

    }

    @Override
    public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {

    }

    @Override
    public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {

    }

    @Override
    public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId, int dwErrorCode, int dwFlags, int dwParam, String userStr) {
        try {
            LogUtil.e("123456", "执行");
            if (dwEventType == AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH) {
                speakCameraControl(-1, 0);
                speakCameraControl(dwTargetUserId, 0);
                anychat.LeaveRoom(-1);
                this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
//            unregisterReceiver(receiver);
            long VideoTm = System.currentTimeMillis();
//            PreferencesUtils.saveLong("time", VideoTm);

            if (mTimerCheckAv != null)
                mTimerCheckAv.cancel();
            anychat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH, dwTargetUserId, 0, 0, 0, "");
            anychat.LeaveRoom(-1);
            speakCameraControl(-1, 0);
            speakCameraControl(dwTargetUserId, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isStartService = false;
    private boolean isControlAble = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void OnSwitchIconListener(DeviceInfo deviceInfo, boolean isPermission, ToggleButton iconSwitch, ImageView iconWarn) {

//        DeviceName.setText(deviceInfo.getDeviceName());
        switch (deviceInfo.getDeviceId()) {
            case DeviceList.DEVICE_ID_DOOR_LOCK:// 启动DoorLockActivity
                doorLock.setVisibility(View.VISIBLE);
                device.setVisibility(View.GONE);
                if (gv.isEnabled()) {
                    gv.setEnabled(false);
                }
                selectDevice = deviceInfo;

                // 判断服务是否在进行计时
//                if (ControlTimeService.timerMap != null) {
////                    timer = ControlTimeService.timerMap.get(selectDevice.getUId());
//                    if (null != timer) {
//                        if (timer.isRuning()) {
//                            isStartService = true;
//                            isControlAble = true;
//                            mTextLockHint.setText("门锁已激活");
//                            mTextLockHint.setTextColor(Color.BLACK);
//                            mBtnControl.setImageAlpha(255);
//
//                        } else {
//                            isStartService = false;
//                            isControlAble = false;
//                            mTextLockHint.setText("门锁未激活");
//                            mTextLockHint.setTextColor(Color.BLACK);
//                            mBtnControl.setImageAlpha(100);
//                        }
//
//                    } else {
//                        isStartService = false;
//                        isControlAble = false;
//                        mTextLockHint.setText("门锁未激活");
//                        mTextLockHint.setTextColor(Color.BLACK);
//                        mBtnControl.setImageAlpha(100);
//                    }
//                } else {
//                    isStartService = false;
//                    isControlAble = false;
//                    mTextLockHint.setText("门锁未激活");
//                    mTextLockHint.setTextColor(Color.BLACK);
//                    mBtnControl.setImageAlpha(100);
//
//                }
//                beginGetAbleState();// --------===========开始获取门锁允许被控制的状态
                break;
        }
    }

//    /**
//     * 重置图标
//     */
//    private void setReset(int time, final boolean flag, final int deviceId) {
//        mHandler.postDelayed(new Runnable() {
//
//            @SuppressLint("NewApi")
//            @Override
//            public void run() {
//
//                if (flag) {
//                    mTextLockHint.setText("门锁已激活");// 清空文字
//                    mBtnControl.setClickable(true);
//                    mTextLockHint.setTextColor(Color.BLACK);
//                    mBtnControl.setImageAlpha(255);
//                } else {
//                    //ControlTimeService.time = 1;
////                    stopTimer(deviceId);
//                    mBtnControl.setClickable(false);// 门锁图标不可以再次点击
////                    mBtnControl.setImageResource(R.drawable.ic_device_door_lock);
//                    mTextLockHint.setText("门锁未激活");// 清空文字
//                    mBtnControl.setClickable(false);
//                    mTextLockHint.setTextColor(Color.BLACK);
//                    mBtnControl.setImageAlpha(100);
//
//                }
//            }
//        }, time);
//    }

    /**
     * 停止定时器
     *
     * @author ZhaoLi.Wang
     * @date 2016-11-25 下午3:57:55
     */
//    private void stopTimer(int mDeviceUid) {
//        if (ControlTimeService.timerMap != null) {
//            timer = ControlTimeService.timerMap.get(mDeviceUid);
//            if (null != timer) {
//                timer.cancel();
//                ControlTimeService.timerMap.remove(mDeviceUid);
//                // 没有定时器运行 服务停止
//                if (ControlTimeService.timerMap.size() == 0) {
//                    if (null == intentTime) {
//                        intentTime = new Intent(this,
//                                ControlTimeService.class);
//                    }
//                    stopService(intentTime);
//                }
//
//            }
//        }
//
//    }

    /**
     * 显示门锁激活状态
     *
     * @param data
     */
//    @SuppressLint("NewApi")
//    private void showDoorLockControlState(int data) {
//        String str = null;
//        try {
//            str = mControlStateHashMap.get(data);
//        } catch (Exception e) {
//        }
//        if (TextUtils.isEmpty(str)) {
//            return;
//        }
//        if (data == 1) {
//            isControlAble = true;
//            if (!isStartService) {
//                intentTime = new Intent(this,
//                        ControlTimeService.class);
//                //设备
//                intentTime.putExtra("deviceUid", selectDevice.getUId());
//                startService(intentTime);
//                isStartService = true;
//
//            }
//        } else {
//            isControlAble = false;
//        }

//        mBtnControl.setClickable(true);
//        mTextLockHint.setText(str);// 显示门锁已激活
//        mTextLockHint.setTextColor(Color.BLACK);// 显示黑色字体
//        mBtnControl.setImageAlpha(255);

//    }

    public String status = " ";
    private Intent intentTime;
    public static final String ACTION_UPDATE_DOOR_LOCK_CONTROL_STATE = "com.fbee.smarthome_wl.activity.DoorLockActivity.updateControl";
    // ====================广播接收者
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//
//
//        private String extras;
//
//        @SuppressLint("NewApi")
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(CommonBaseActivity.ACTION_NAME)) {
//                extras = intent.getExtras().getString("json");
//                JSONObject json = null;
//                try {
//                    json = new JSONObject(extras);
//                    String method = json.optString(Method.METHOD);
//                    if (method.equals(Method.METHOD_VIDEOPLAY_STATUS_PLAYING)) {
//                        status = method;
////                        btnCapture.setEnabled(true);
////                        btnMute.setEnabled(true);
////                        btnSoundSwitch.setEnabled(true);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (intent.getAction().equals("fromServicetobroadcast")) {
//
//                //计时器结束，门锁显示未激活
//                int deviceUid = intent.getIntExtra("mDeviceUid", 0);
//                if (deviceUid == selectDevice.getUId()) {
//                    isControlAble = false;
//                    isStartService = false;
//                    mTextLockHint.setText("门锁未激活");
//                    mBtnControl.setImageAlpha(100);
//                    mTextLockHint.setTextColor(Color.BLACK);
//                    mBtnControl.setImageResource(R.drawable.ic_device_door_lock);// 改变锁的图标
//                }
//            } else if (intent.getAction().equals(MainActivity.ACTION_DOOR_LOCK_ALARM)) {// ==========更新数据
//                byte[] doorLockData = intent.getByteArrayExtra("doorlockDATA");
//                int uid = intent.getIntExtra("doorlockuid", 0); // 得到uid
//                if (selectDevice == null || (selectDevice != null && uid != selectDevice.getUId())) {
//                    return;
//                }
//                StringBuffer text = new StringBuffer();
//                int way = intent.getIntExtra("doorlockway", 0); // 开锁方式
//                int Flag = intent.getIntExtra("doorlockfalg", 0);
//                if (Flag == 0) {
//                    if (doorLockData != null && (doorLockData.length == 12 || doorLockData.length == 13)) {
//                        text.append("远程开锁成功");
//                        mTextLockHint.setText(text.toString());
//                        setReset(3000, false, selectDevice.getUId());
//                    }
//
//                } else if (Flag == 1) {
//                    text.append("密码错误");
//                    mTextLockHint.setText(text.toString());
//                    setReset(3000, true, selectDevice.getUId());
//                } else if (Flag == 2 || Flag == 0x7f) {
//                    text.append("远程开锁未允许");
//                    mTextLockHint.setText(text.toString());
//                    setReset(3000, false, selectDevice.getUId());
//                }
//                if (way == DoorLockGlobal.R_DOOR_LOCK_ALARM_FLAG) { // 防拆 报警各种报警
//                }
//
//                // ================================更新激活状态
//            } else if (intent.getAction().equals(
//                    ACTION_UPDATE_DOOR_LOCK_CONTROL_STATE)) {
//                DeviceInfo deviceInfo = (DeviceInfo) intent
//                        .getSerializableExtra("updateData");
//                int uid = deviceInfo.getUId();// 得到uid
//                if (selectDevice == null || (selectDevice != null && uid != selectDevice.getUId())) {
//                    return;
//                }
//                int clusterId = deviceInfo.getClusterId();
//                int attribId = deviceInfo.getAttribID();
//                int date = deviceInfo.getSensordata();
//                if (clusterId == 0x0a && attribId == 0x00) {
//                    // 门锁时间
//
//                } else if (clusterId == 0x00 && attribId == 0x12) { // 门锁可以控制
//                    // 可控状态
//                    showDoorLockControlState(date);
//                } else if (clusterId == 0x0101 && attribId == 0x0000) {
//                }
//
//            }
//
//        }
//    };

    @Override
    public void onItemClick(View view, int position) {
//      门锁激活界面出现
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
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

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
    }

    @Override
    public void resReqGateWayInfo(QueryGateWayInfoReq body) {

    }

    @Override
    public void loginSuccess(Object obj) {

    }
}
