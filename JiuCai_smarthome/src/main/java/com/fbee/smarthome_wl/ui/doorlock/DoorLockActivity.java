package com.fbee.smarthome_wl.ui.doorlock;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.ArriveReportCBInfo;
import com.fbee.smarthome_wl.bean.DoorLockStateInfo;
import com.fbee.smarthome_wl.bean.GateWayInfo;
import com.fbee.smarthome_wl.bean.HintCountInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DoorLockGlobal;
import com.fbee.smarthome_wl.event.ChangeDevicenameEvent;
import com.fbee.smarthome_wl.event.ControlTimeEvent;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.ui.doorlocklog.DoorLockLogActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.ui.usermanage.UserManageActivity;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.BadgeUtil;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.DateUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.TimerCount;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.Tool;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.WeakHandler;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.Serial;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;


/**
 * 门锁详情页面
 */
public class DoorLockActivity extends BaseActivity<DoorLockContract.Presenter> implements DoorLockContract.View {

    private ImageView back;
    private TextView title;
    private TextView mTextLockHint;
    private TextView doorlockName;
    private ImageView editIconDoorlock;
    private ImageView batteryIconDoorlock;
    private ImageView mBtnControl;
    private RelativeLayout recordLinearDoorlock;
    private RelativeLayout accountLinearDoorlock;
    private RelativeLayout relDoorlockIcon;
    private CircleProgressView circleview;
    private ImageView doorlockKey;


    private String itemDviceInfo = "itemDviceInfo";
    private DeviceInfo deviceInfo;
    private int mDeviceUid;
    private String mUserName;
    private String mPassword;
    private String mDeviceName;
    private String deviceName = "devicename";
    private String deviceId = "deviceid";
    private String deviceIeee="deviceIeee";
    private String doorLockmaps = "doorLockMaps";
    private Long mStartTime;
    private Long mEndTime;
    private int resultCode = 600;
    private Intent intentTime;
    private TimerCount timer;

    private WeakHandler mHandler;
    private boolean isControlAble = false;// 默认不可控
    private int reply_ready_flag = 0;
    public static final int S_DOOR_UNLOCK = 1;
    private int hintCount = 0;
    private TextView mTextRecordHint;
    private AlertDialog alertDialog;
    private String soVer;
    private GateWayInfo gateWayInfo;
    private HashMap<String, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean>> userEntityMap;
    private String phone;
    private String mDeviceIeee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorlock);
    }

    @Override
    protected void initView() {
        initApi();
        createPresenter(new DoorlockPresenter(this));
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        title = (TextView) findViewById(R.id.title);
        doorlockName = (TextView) findViewById(R.id.doorlock_name);
        editIconDoorlock = (ImageView) findViewById(R.id.edit_icon_doorlock);
        batteryIconDoorlock = (ImageView) findViewById(R.id.battery_icon_doorlock);
        mBtnControl = (ImageView) findViewById(R.id.doorlock_icon);
        doorlockKey = (ImageView) findViewById(R.id.doorlock_key);
        recordLinearDoorlock = (RelativeLayout) findViewById(R.id.record_linear_doorlock);
        accountLinearDoorlock = (RelativeLayout) findViewById(R.id.account_linear_doorlock);
        mTextLockHint = (TextView) findViewById(R.id.doorlock_text_state);
        mTextRecordHint = (TextView) findViewById(R.id.btn_lock_record_hint);
        relDoorlockIcon = (RelativeLayout) findViewById(R.id.rel_doorlock_icon);
        circleview = (CircleProgressView) findViewById(R.id.circleview);
        circleview.setProgress(50);

    }

    @Override
    protected void initData() {
        title.setText("门锁详情");
        //获取所有门锁uid和name集合
        //1.2.1
        back.setOnClickListener(this);
        editIconDoorlock.setOnClickListener(this);
        recordLinearDoorlock.setOnClickListener(this);
        accountLinearDoorlock.setOnClickListener(this);
        relDoorlockIcon.setOnClickListener(this);
        phone = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        soVer = AppContext.getInstance().getSerialInstance().getSoVer();
        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra(itemDviceInfo);
        mDeviceUid = deviceInfo.getUId();
        mDeviceIeee=ByteStringUtil.bytesToHexString(deviceInfo.getIEEE()).toUpperCase();
        mDeviceName = deviceInfo.getDeviceName();
        doorlockName.setText(mDeviceName);
        //门锁计时intent
        mHandler = new WeakHandler();
        //获取当前网关
        LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        if (gw == null) {
            return;
        }
        mUserName = gw.getUsername();
        mPassword = gw.getPassword();
        mStartTime = DateUtil.getLastThreeDayTime();
        mEndTime = DateUtil.getCurrentTime();

        //接收网关信息
        receiveGateWayInfo();

        //请求网关信息
        AppContext.getInstance().getSerialInstance().getGateWayInfo();

        // 判断服务是否在进行计时
        isControlTimeRunning();

        //接收服务时间控制门锁时间进度
        receiveControlTimeEvent();

        // 开始获取门锁允许被控制的状态
       // beginGetAbleState();

        //设置缓存最新电量
        setNativePower();

        //获取门锁第一条电量展示
        getNetPowerFirstRecordPersent(String.valueOf(mStartTime / 1000), String.valueOf(mEndTime / 1000));

        //接收门锁电量及门锁是否可控
        receiveDoorLockPowerInfo();

        //接收门锁可控状态
        receiveDoorLockStateInfo();

        //接收门锁激活服务计时结束时的数据
        //receiveControlTimeServiceInfo();

        //接收小红点改变
        receiveHintCount();

    }

    //设置缓存电量
    private void setNativePower(){
        if(AppContext.getDoorLockPowerMap().get(mDeviceUid)!=null){
            int power=AppContext.getDoorLockPowerMap().get(mDeviceUid)/2;
            showDoorLockDvcState(power);
        }
    }
    /**
     *
     * 接收服务时间控制门锁时间进度
     */
    private void receiveControlTimeEvent(){

        Subscription subHint = RxBus.getInstance().toObservable(ControlTimeEvent.class)
                .compose(TransformUtils.<ControlTimeEvent>defaultSchedulers())
                .subscribe(new Action1<ControlTimeEvent>() {
                    @Override
                    public void call(ControlTimeEvent event) {
                        if (event==null) return;
                        if(event.getDeviceId()==mDeviceUid){
                           final int nowSecond=event.getNowTime();
                            mBtnControl.setImageResource(R.mipmap.empty_door);
                            doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                            if(mTextLockHint.getText().equals("门锁未激活")){
                                mTextLockHint.setText("门锁已激活");
                            }
                            if(!relDoorlockIcon.isClickable()){
                                relDoorlockIcon.setClickable(true);
                            }
                            if(isControlAble ==false){
                                isControlAble=true;
                            }
                            //动画显示
                            showAnimation();
                            //进度开始
                            controlProgress(50-nowSecond);

                            if(nowSecond ==1){
                                isControlAble = false;
                                mTextLockHint.setText("门锁未激活");
                                relDoorlockIcon.setClickable(false);
                                //设置灰色
                                mBtnControl.setImageResource(R.mipmap.empty_door_gray);
                                doorlockKey.setImageResource(R.drawable.fanzhuan_gray_lockimg);

                                //动画停掉
                                //mBtnControl.clearAnimation();
                                doorlockKey.clearAnimation();
                                //progress置位
                                controlProgress(50);
                            }
                        }
                    }
                });
        mCompositeSubscription.add(subHint);
    }


    /**
     * 控制门锁计时进度
     * @param progress
     */
    private void controlProgress(final int  progress){
        ThreadPoolUtils.getInstance().getSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                circleview.setProgressNotInUiThread(progress);

            }
        });
    }

    /**
     * 设置动画
     */
    private void showAnimation() {
        // 获取自定义动画实例
        CustomRotateAnim rotateAnim = CustomRotateAnim.getCustomRotateAnim();
        // 一次动画执行1秒
        rotateAnim.setDuration(1000);
        // 设置为循环播放
        rotateAnim.setRepeatCount(-1);
        // 设置为匀速
        rotateAnim.setInterpolator(new LinearInterpolator());
        // 开始播放动画
        //mBtnControl.startAnimation(rotateAnim);
        doorlockKey.startAnimation(rotateAnim);
    }

    /**
     * 接收小红点改变
     */
    private void receiveHintCount() {
        //接收小红点改变
        Subscription subHint = RxBus.getInstance().toObservable(HintCountInfo.class)
                .compose(TransformUtils.<HintCountInfo>defaultSchedulers())
                .subscribe(new Action1<HintCountInfo>() {
                    @Override
                    public void call(HintCountInfo event) {
                        if(event.getUid()==mDeviceUid){
                            getHintCount();
                        }
                    }
                });
        mCompositeSubscription.add(subHint);
    }

    private void getHintCount() {
        try {
            String jsString = PreferencesUtils.getString(MainActivity.DOORMESSAGECOUNT);
            // ===============如果jsonArray不为空，有消息的门锁，在设备详情页显示红点
            if (jsString != null) {
                JSONArray jsonArrayGet = new JSONArray(jsString);
                for (int i = 0; i < jsonArrayGet.length(); i++) {
                    JSONObject jsonObjectGet = jsonArrayGet.getJSONObject(i);
                    if (jsonObjectGet.optInt("uid") == mDeviceUid) {
                        hintCount = jsonObjectGet.optInt("messageCount");

                        if (hintCount != 0) {
                            if (mTextRecordHint.getVisibility() != View.VISIBLE) {
                                mTextRecordHint
                                        .setVisibility(View.VISIBLE);// 显示红点TextView
                            }
                            mTextRecordHint.setText(String
                                    .valueOf(hintCount));

                        } else {
                            if (mTextRecordHint.getVisibility() != View.GONE) {
                                mTextRecordHint
                                        .setVisibility(View.GONE);// 显示红点TextView
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断服务是否在进行计时
     */
    private void isControlTimeRunning() {

        if (AppContext.timerMap != null) {
            timer = AppContext.timerMap.get(mDeviceUid);
            if (null != timer) {
                if (timer.isRuning()) {
                    isControlAble = true;
                    mTextLockHint.setText("门锁已激活");
                    //mBtnControl.setImageResource(R.mipmap.doorlock_activeicon);
                    //设置绿色
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                    relDoorlockIcon.setClickable(true);

                } else {
                    isControlAble = false;
                    mTextLockHint.setText("门锁未激活");
                    //设置灰色
                    mBtnControl.setImageResource(R.mipmap.empty_door_gray);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_gray_lockimg);
                    relDoorlockIcon.setClickable(false);
                    //动画停掉
                    //mBtnControl.clearAnimation();
                    doorlockKey.clearAnimation();
                    //progress置位
                    controlProgress(50);
                }

            } else {
                isControlAble = false;
                mTextLockHint.setText("门锁未激活");
                //设置灰色
                mBtnControl.setImageResource(R.mipmap.empty_door_gray);
                doorlockKey.setImageResource(R.drawable.fanzhuan_gray_lockimg);
                //doorlockKey.setImageResource();
                relDoorlockIcon.setClickable(false);
                //动画停掉
                //mBtnControl.clearAnimation();
                doorlockKey.clearAnimation();
                //progress置位
                controlProgress(50);
            }

        } else {
            isControlAble = false;
            mTextLockHint.setText("门锁未激活");
            relDoorlockIcon.setClickable(false);
            //设置灰色
            mBtnControl.setImageResource(R.mipmap.empty_door_gray);
            doorlockKey.setImageResource(R.drawable.fanzhuan_gray_lockimg);

            //动画停掉
            doorlockKey.clearAnimation();

            //progress置位
            controlProgress(50);
        }
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
                },onErrorAction);
        mCompositeSubscription.add(gateWaySubscription);
    }

    /**
     * 接收门锁电量及门锁是否可控
     */
    private void receiveDoorLockPowerInfo() {
        //注册RXbus接收arriveReport_CallBack（）回调中的数据
        mSubscription = RxBus.getInstance().toObservable(ArriveReportCBInfo.class)
                .compose(TransformUtils.<ArriveReportCBInfo>defaultSchedulers())
                .subscribe(new Action1<ArriveReportCBInfo>() {
                    @Override
                    public void call(ArriveReportCBInfo event) {
                        if (event == null) return;
                        if (event.getuId() != mDeviceUid) return;
                        //门锁电量相关
                        if (event.getClusterId() == DoorLockGlobal.CLUSTER_ID_POWER_REPORT) {
                            switch (event.getAttribID()) {
                                case DoorLockGlobal.BATTERY_ATTRID_STATE:
                                    if (event.getData() == DoorLockGlobal.BATTERY_STATE_NORMAL) {
                                        //可以设置电量正常状态图片
                                        showDoorLockDvcState(100);
                                    } else if (event.getData() == DoorLockGlobal.BATTERY_STATE_LOW_POWER_ALARM) {
                                        //可以设置低电量状态图片
                                        showDoorLockDvcState(25);
                                    }
                                    break;
                                case DoorLockGlobal.BATTERY_ATTRID_POWER:
                                    //设置电量百分比
                                    int powerPerent = event.getData() / 2;
                                    showDoorLockDvcState(powerPerent);
                                    break;
                            }
                        }
                       /* //门锁可控状态上报
                        int clusterId = event.getClusterId();
                        LogUtil.e("门锁上报clusterId","==="+clusterId);
                        int attribId = event.getAttribID();
                        LogUtil.e("门锁上报attribId","==="+attribId);
                        int data = event.getData();
                        LogUtil.e("门锁上报data","data="+data);
                        if (clusterId == 0x00 && attribId == 0x12) { // 门锁可以控制
                            //可控状态，开启倒计时
                            showDoorLockControlState(data);
                        }*/
                    }
                });
        mCompositeSubscription.add(mSubscription);
    }

    /**
     * 注册RXbus接收来自Serial中arriveReportgatedoor_CallBack(int uId, byte[] data,
     * char clusterId, char attribID)的数据
     */
    public void receiveDoorLockStateInfo() {
        mSubscription = RxBus.getInstance().toObservable(DoorLockStateInfo.class)
                .compose(TransformUtils.<DoorLockStateInfo>defaultSchedulers())
                .subscribe(new Action1<DoorLockStateInfo>() {
                    @Override
                    public void call(DoorLockStateInfo event) {
                        //处理门锁状态数据
                        if (event == null) return;
                        handleReceiveDoorLockState(event);
                    }
                });
        mCompositeSubscription.add(mSubscription);
    }





    /**
     * 显示门锁激活状态
     *
     * @param data
     */
    private void showDoorLockControlState(int data) {
        //激活状态 ，开启服务倒计时
        if (data == 1) {
            //激活状态 ，开启服务倒计时
            reply_ready_flag = 1;
            isControlAble = true;

            mTextLockHint.setText("门锁已激活");// 显示门锁已激活
            relDoorlockIcon.setClickable(true);
            //设置绿色
            mBtnControl.setImageResource(R.mipmap.empty_door);
            doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);

        } else {
            //未激活状态
            isControlAble = false;
            mTextLockHint.setText("门锁未激活");// 显示门锁未激活
            //设置灰色
            mBtnControl.setImageResource(R.mipmap.empty_door_gray);
            doorlockKey.setImageResource(R.drawable.fanzhuan_gray_lockimg);
            //doorlockKey.setImageResource();
            relDoorlockIcon.setClickable(false);
            //动画停掉
            //mBtnControl.clearAnimation();
            doorlockKey.clearAnimation();
            //progress置位
            controlProgress(50);
        }
    }

    /**
     * 判断是否是绑定设备用户
     *
     * @param userNum
     * @return
     */
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
    }

    /**
     * 处理门锁状态数据
     *
     * @param event
     */
    private void handleReceiveDoorLockState(DoorLockStateInfo event) {
        //初始化门锁通知
        int uid = event.getDoorLockUid(); // 得到uid
        int data = event.getDoorLockSensorData(); // 开锁时间
        byte[] bytes = Tool.IntToBytes(data);
        int newmodle = event.getNewDoorLockState();// 获得胁迫报警位
        if (uid != mDeviceUid) {
            return;
        }
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

                if (isAssociateDevicesUser(uid, cardNum)) {
                    //密码
                    if (way == 0) {
                        text.append("密码进入菜单");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹进入菜单");
                    }
                    else if (way == 3) {
                        text.append("刷卡进入菜单");
                    } else if (way == 5) {
                        text.append("多重验证进入菜单");
                    }

                } else {
                    //密码
                    if (way == 0) {
                        text.append("密码胁迫报警进入菜单");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹胁迫报警进入菜单");
                    }
                    else if (way == 3) {
                        text.append("刷卡胁迫报警进入菜单");
                    } else if (way == 5) {
                        text.append("多重验证胁迫报警进入菜单");
                    }
                }


                //text.append("胁迫报警进入菜单");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);

            }// 胁迫指纹进入菜单（双人模式）
            else if ((newmodle & 0xbf) == 0x98) {

                if (isAssociateDevicesUser(uid, cardNum)) {
                    //密码
                    if (way == 0) {
                        text.append("密码进入菜单(双人模式)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹进入菜单(双人模式)");
                    }
                    else if (way == 3) {
                        text.append("刷卡进入菜单(双人模式)");
                    } else if (way == 5) {
                        text.append("多重验证进入菜单(双人模式)");
                    }
                } else {
                    //密码
                    if (way == 0) {
                        text.append("密码胁迫报警进入菜单(双人模式)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹胁迫报警进入菜单(双人模式)");
                    }
                    else if (way == 3) {
                        text.append("刷卡胁迫报警进入菜单(双人模式)");
                    } else if (way == 5) {
                        text.append("多重验证胁迫报警进入菜单(双人模式)");
                    }
                }


                //text.append("胁迫报警进入菜单(双人模式)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }// 胁迫报警启用常开（菜单）
            else if ((newmodle & 0xbf) == 0x89) {

                if (isAssociateDevicesUser(uid, cardNum)) {
                    //密码
                    if (way == 0) {
                        text.append("密码启用常开(菜单)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹启用常开(菜单)");
                    }
                    else if (way == 3) {
                        text.append("刷卡启用常开(菜单)");
                    } else if (way == 5) {
                        text.append("多重验证启用常开(菜单)");
                    }

                } else {
                    //密码
                    if (way == 0) {
                        text.append("密码胁迫报警启用常开(菜单)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹胁迫报警启用常开(菜单)");
                    }
                    else if (way == 3) {
                        text.append("刷卡胁迫报警启用常开(菜单)");
                    } else if (way == 5) {
                        text.append("多重验证胁迫报警启用常开(菜单)");
                    }
                }


                //text.append("胁迫报警启用常开(菜单)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }// 胁迫报警取消常开（菜单）
            else if ((newmodle & 0xbf) == 0x8a) {

                if (isAssociateDevicesUser(uid, cardNum)) {
                    //密码
                    if (way == 0) {
                        text.append("密码取消常开(菜单)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹取消常开(菜单)");
                    }
                    else if (way == 3) {
                        text.append("刷卡取消常开(菜单)");
                    } else if (way == 5) {
                        text.append("多重验证取消常开(菜单)");
                    }
                } else {
                    //密码
                    if (way == 0) {
                        text.append("密码胁迫报警取消常开(菜单)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹胁迫报警取消常开(菜单)");
                    }
                    else if (way == 3) {
                        text.append("刷卡胁迫报警取消常开(菜单)");
                    } else if (way == 5) {
                        text.append("多重验证胁迫报警取消常开(菜单)");
                    }
                }


                //text.append("胁迫报警取消常开(菜单)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }
            // 胁迫指纹进入菜单取消常开（双人模式）
            else if ((newmodle & 0xbf) == 0x9a) {


                if (isAssociateDevicesUser(uid, cardNum)) {
                    //密码
                    if (way == 0) {
                        text.append("密码取消常开(双人模式)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹取消常开(双人模式)");
                    }
                    else if (way == 3) {
                        text.append("刷卡取消常开(双人模式)");
                    } else if (way == 5) {
                        text.append("多重验证取消常开(双人模式)");
                    }
                } else {
                    //密码
                    if (way == 0) {
                        text.append("密码胁迫报警取消常开(双人模式)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹胁迫报警取消常开(双人模式)");
                    }
                    else if (way == 3) {
                        text.append("刷卡胁迫报警取消常开(双人模式)");
                    } else if (way == 5) {
                        text.append("多重验证胁迫报警取消常开(双人模式)");
                    }
                }


                // text.append("胁迫报警取消常开(双人模式)");
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }// 胁迫指纹进入菜单启用常开（双人模式）
            else if ((newmodle & 0xbf) == 0x99) {

                if (isAssociateDevicesUser(uid, cardNum)) {
                    //密码
                    if (way == 0) {
                        text.append("密码启用常开(双人模式)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹启用常开(双人模式)");
                    }
                    else if (way == 3) {
                        text.append("刷卡启用常开(双人模式)");
                    } else if (way == 5) {
                        text.append("多重验证启用常开(双人模式)");
                    }
                } else {
                    //密码
                    if (way == 0) {
                        text.append("密码胁迫报警启用常开(双人模式)");
                    }
                    //指纹
                    else if (way == 2) {
                        text.append("指纹胁迫报警启用常开(双人模式)");
                    }
                    else if (way == 3) {
                        text.append("刷卡胁迫报警启用常开(双人模式)");
                    } else if (way == 5) {
                        text.append("多重验证胁迫报警启用常开(双人模式)");
                    }
                }


                //text.append("胁迫报警启用常开(双人模式)");
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
                    //设置绿色
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                    setReset(3000, false);
                }
                if ((newmodle & 0xbf) == 0x80) {
                    if (isAssociateDevicesUser(uid, cardNum)) {
                        //密码
                        if (way == 0) {
                            text.append("密码开锁成功");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹开锁成功");
                        }
                        else if(way == 3){
                            text.append("刷卡开锁成功");
                        }
                        else if(way == 5){
                            text.append("多重验证开锁成功");
                        }
                    } else {
                        //密码
                        if (way == 0) {
                            text.append("密码胁迫报警");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹胁迫报警");
                        }
                        else if(way == 3){
                            text.append("刷卡胁迫报警");
                        }
                        else if(way == 5){
                            text.append("多重验证胁迫报警");
                        }
                    }


                    //text.append("胁迫报警");
                    mTextLockHint.setText(text.toString());
                    //设置绿色
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x90) {

                    if (isAssociateDevicesUser(uid, cardNum)) {
                        //密码
                        if (way == 0) {
                            text.append("密码开锁成功");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹开锁成功");
                        }
                        else if(way == 3){
                            text.append("刷卡开锁成功");
                        }
                        else if(way == 5){
                            text.append("多重验证开锁成功");
                        }
                    } else {
                        //密码
                        if (way == 0) {
                            text.append("密码胁迫报警 (双人模式)");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹胁迫报警 (双人模式)");
                        }
                        else if(way == 3){
                            text.append("刷卡胁迫报警(双人模式)");
                        }
                        else if(way == 5){
                            text.append("多重验证胁迫报警(双人模式)");
                        }
                    }


                    //text.append("胁迫报警 (双人模式)");
                    //设置绿色
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x82) {

                    if (isAssociateDevicesUser(uid, cardNum)) {
                        //密码
                        if (way == 0) {
                            text.append("密码开锁成功");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹开锁成功");
                        }
                        else if(way == 3){
                            text.append("刷卡开锁成功");
                        }
                        else if(way == 5){
                            text.append("多重验证开锁成功");
                        }
                    } else {
                        //密码
                        if (way == 0) {
                            text.append("密码胁迫报警取消常开");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹胁迫报警取消常开");
                        }
                        else if(way == 3){
                            text.append("刷卡胁迫报警取消常开");
                        }
                        else if(way == 5){
                            text.append("多重验证胁迫报警取消常开");
                        }
                    }


                    //text.append("胁迫报警取消常开");
                    //设置绿色
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x81) {

                    if (isAssociateDevicesUser(uid, cardNum)) {
                        //密码
                        if (way == 0) {
                            text.append("密码开锁成功");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹开锁成功");
                        }
                        else if(way == 3){
                            text.append("刷卡开锁成功");
                        }
                        else if(way == 5){
                            text.append("多重验证开锁成功");
                        }
                    } else {
                        //密码
                        if (way == 0) {
                            text.append("密码胁迫报警启用常开");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹胁迫报警启用常开");
                        }
                        else if(way == 3){
                            text.append("刷卡胁迫报警启用常开");
                        }
                        else if(way == 5){
                            text.append("多重验证胁迫报警启用常开");
                        }
                    }


                    //text.append("胁迫报警启用常开");
                    //设置绿色
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x92) {

                    if (isAssociateDevicesUser(uid, cardNum)) {
                        //密码
                        if (way == 0) {
                            text.append("密码开锁成功");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹开锁成功");
                        }
                        else if(way == 3){
                            text.append("刷卡开锁成功");
                        }
                        else if(way == 5){
                            text.append("多重验证开锁成功");
                        }
                    } else {
                        //密码
                        if (way == 0) {
                            text.append("密码胁迫报警取消常开(双人模式)");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹胁迫报警取消常开(双人模式)");
                        }
                        else if(way == 3){
                            text.append("刷卡胁迫报警取消常开(双人模式)");
                        }
                        else if(way == 5){
                            text.append("多重验证胁迫报警取消常开(双人模式)");
                        }
                    }


                    //text.append("胁迫报警取消常开(双人模式)");
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                    mTextLockHint.setText(text.toString());
                    setReset(3000, false);
                } else if ((newmodle & 0xbf) == 0x91) {

                    if (isAssociateDevicesUser(uid, cardNum)) {
                        //密码
                        if (way == 0) {
                            text.append("密码开锁成功");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹开锁成功");
                        }
                        else if(way == 3){
                            text.append("刷卡开锁成功");
                        }
                        else if(way == 5){
                            text.append("多重验证开锁成功");
                        }
                    } else {
                        //密码
                        if (way == 0) {
                            text.append("密码胁迫报警启用常开(双人模式)");
                        }
                        //指纹
                        else if (way == 2) {
                            text.append("指纹胁迫报警启用常开(双人模式)");
                        }
                        else if(way == 3){
                            text.append("刷卡胁迫报警启用常开(双人模式)");
                        }
                        else if(way == 5){
                            text.append("多重验证胁迫报警启用常开(双人模式)");
                        }
                    }


                    //text.append("胁迫报警启用常开(双人模式)");
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
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
                mBtnControl.setImageResource(R.mipmap.empty_door);
                doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                mTextLockHint.setText(text.toString());
                setReset(3000, false);
            }
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
                resId = R.mipmap.power_25;
                break;
        }
        batteryIconDoorlock.setImageResource(resId);
    }


    /**
     * 获取互联网电量数据第一条百分比
     */

    public void getNetPowerFirstRecordPersent(final String mStartTime, final String mEndTime) {
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

    @Override
    protected void onResume() {
        super.onResume();
        //获取小红点
        getHintCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_icon_doorlock:
                //编辑门锁名称
                showCustomizeDialog();
                //showPopupWindow(parentView);
                break;
            case R.id.rel_doorlock_icon:
                //门锁图标
                if (!isControlAble) {// 如果门锁不可控，未激活
                    mTextLockHint.setText("门锁未激活");
                    relDoorlockIcon.setClickable(false);
                    //设置灰色
                    mBtnControl.setImageResource(R.mipmap.empty_door_gray);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_gray_lockimg);
                    //动画停掉
                    //mBtnControl.clearAnimation();
                    doorlockKey.clearAnimation();
                    //progress置位
                    controlProgress(50);
                    break;
                }
                unlock();// 已经激活，输入密码开锁
                break;
            case R.id.record_linear_doorlock:
                //记录
                hintCount = 0;

                //消息红点置位为零
                try {
                    String jsString = PreferencesUtils.getString(MainActivity.DOORMESSAGECOUNT);
                    // ===============如果jsonArray不为空，有消息的门锁，在设备详情页不再显示红点
                    if (!jsString.equals("[]")) {
                        JSONArray jsonArrayGet = new JSONArray(jsString);
                        for (int i = 0; i < jsonArrayGet.length(); i++) {
                            JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
                            if (jsonObjectGet.optInt("uid") == deviceInfo.getUId()) {
                                // ==============更新该门锁记录
                                remove(i, jsonArrayGet);// 必须先删除,防止得到的消息数量一直为0
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("uid", deviceInfo.getUId());
                                jsonObject.put("messageCount", hintCount);
                                jsonArrayGet.put(jsonObject);
                                PreferencesUtils.saveString(MainActivity.DOORMESSAGECOUNT,
                                        jsonArrayGet.toString());
                                break;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mTextRecordHint.setVisibility(View.GONE);// 红点消失
                notifyBadgeNumber();

                Bundle bundle = new Bundle();
                bundle.putString(deviceName, mDeviceName);
                bundle.putString(deviceIeee,mDeviceIeee);
                bundle.putInt(deviceId, mDeviceUid);
                skipAct(DoorLockLogActivity.class, bundle);
                break;
            case R.id.account_linear_doorlock:
                //用户
                Bundle bun = new Bundle();
                bun.putInt(deviceId, mDeviceUid);
                bun.putString(deviceIeee,mDeviceIeee);
                skipAct(UserManageActivity.class, bun);
                break;
            case R.id.back:
                Intent intent = new Intent();
                intent.putExtra("UID", mDeviceUid);
                intent.putExtra("devieceName", doorlockName.getText().toString().trim());
                setResult(resultCode, intent);
                finish();
                break;
        }
    }

    //JSONArray删除条目
    public void remove(int positon, JSONArray array) throws Exception {
        if (positon < 0)
            return;
        Field valuesField = JSONArray.class.getDeclaredField("values");
        valuesField.setAccessible(true);
        List<Object> values = (List<Object>) valuesField.get(array);
        if (positon >= values.size())
            return;
        values.remove(positon);
    }


    // 消息数量显示
    public void notifyBadgeNumber() {
        try {
            String jsString = PreferencesUtils.getString(MainActivity.DOORMESSAGECOUNT);
            int count = 0;
            if (jsString != null) {
                JSONArray jsonArrayGet = new JSONArray(jsString);
                for (int i = 0; i < jsonArrayGet.length(); i++) {
                    JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
                    count = count + jsonObjectGet.optInt("messageCount");
                }
            }
            BadgeUtil.setBadgeCount(this, count,
                    Build.MANUFACTURER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 开锁
     */
    private void unlock() {
        setDoorLockState(S_DOOR_UNLOCK);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("UID", mDeviceUid);
        intent.putExtra("devieceName", doorlockName.getText().toString().trim());
        setResult(resultCode, intent);
        finish();
    }

    private void beginGetAbleState() {
        AppContext.getInstance().getSerialInstance().GetControlableState(deviceInfo);
        Subscription subscription01 = Observable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                AppContext.getInstance().getSerialInstance().GetControlableState(deviceInfo);
            }
        });

        Subscription subscription = Observable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {

                if (reply_ready_flag == 0) {
                    mTextLockHint.setText("门锁未激活");
                    //设置灰色
                    mBtnControl.setImageResource(R.mipmap.empty_door_gray);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_gray_lockimg);
                    //doorlockKey.setImageResource();
                    relDoorlockIcon.setClickable(false);
                    isControlAble = false;
                    stopTimer();
                } else {
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                    mTextLockHint.setText("门锁已激活");
                    relDoorlockIcon.setClickable(true);
                    isControlAble = true;
                }

            }
        });
        mCompositeSubscription.add(subscription01);
        mCompositeSubscription.add(subscription);

    }



    /**
     * 停止定时器
     *
     * @author ZhaoLi.Wang
     * @date 2016-11-25 下午3:57:55
     */
    private void stopTimer() {
        if (AppContext.timerMap != null) {
            timer = AppContext.timerMap.get(mDeviceUid);
            if (null != timer) {
                AppContext.timerMap.remove(mDeviceUid);
                timer.cancel();
                //动画停掉
                doorlockKey.clearAnimation();
                //progress置位
                controlProgress(50);

            }
        }

    }


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
                    relDoorlockIcon.setClickable(true);
                    mBtnControl.setImageResource(R.mipmap.empty_door);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                } else {
                    stopTimer();
                    isControlAble = false;
                    relDoorlockIcon.setClickable(false);// 门锁图标不可以再次点击
                    mTextLockHint.setText("门锁未激活");// 清空文字
                    //设置灰色
                    mBtnControl.setImageResource(R.mipmap.empty_door_gray);
                    doorlockKey.setImageResource(R.drawable.fanzhuan_gray_lockimg);
                    //动画停掉
                    doorlockKey.clearAnimation();
                    //progress置位
                    controlProgress(50);
                }
            }
        }, time);
    }


    //进入菜单
    private void Enter_Menu(int newmodle, StringBuffer text, int cardNum,
                            String str) {
        mBtnControl.setImageResource(R.mipmap.empty_door);
        doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
        if (newmodle == 8) {
            text.append(str + "验证进入菜单");
        } else if (newmodle == 0x18) {
            text.append(str + "验证进入菜单(双人模式)");
        } else if (newmodle == 10) {
            text.append(str + "取消常开(菜单)");
        } else if (newmodle == 9) {
            text.append(str + "启用常开(菜单)");
        } else if (newmodle == 26) {
            text.append(str + "取消常开(菜单)(双人)");
        } else if ((newmodle & 0xbf) == 0x19) {
            text.append(str + "启用常开(菜单)(双人)");
        }
        mTextLockHint.setText(text.toString());
        setReset(3000, false);
    }


    private void Unlocking(int newmodle, StringBuffer text, int cardNum,
                           String str) {
        if (newmodle == 0) {
            text.append(str + "开锁");
            mTextLockHint.setText(text.toString());
            mBtnControl.setImageResource(R.mipmap.empty_door);
            doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x10) {
            text.append(str + "开锁(双人模式)");
            mBtnControl.setImageResource(R.mipmap.empty_door);
            doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x02) {
            text.append(str + "开锁取消常开");
            mBtnControl.setImageResource(R.mipmap.empty_door);
            doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x01) {
            text.append(str + "开锁启用常开");
            mBtnControl.setImageResource(R.mipmap.empty_door);
            doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
            mTextLockHint.setText(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x12) {
            text.append(str + "开锁取消常开(双人模式)");
            mTextLockHint.setText(text.toString());
            mBtnControl.setImageResource(R.mipmap.empty_door);
            doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x11) {
            text.append(str + "开锁启用常开(双人模式)");
            mTextLockHint.setText(text.toString());
            mBtnControl.setImageResource(R.mipmap.empty_door);
            doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
            setReset(3000, false);
        } else {
            if ((newmodle & 0xbf) != 0x80 && (newmodle & 0xbf) != 0x90 && (newmodle & 0xbf) != 0x82 && (newmodle & 0xbf) != 0x81 && (newmodle & 0xbf) != 0x91 && (newmodle & 0xbf) != 0x92) {
                text.append(str + "开锁");
                mTextLockHint.setText(text.toString());
                mBtnControl.setImageResource(R.mipmap.empty_door);
                doorlockKey.setImageResource(R.drawable.fanzhuan_lockimg);
                setReset(3000, false);
            }
        }
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

                List<DeviceInfo> deviceInfos = AppContext.getmOurDevices();

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
                                        doorlockName.setText(editName);
                                        //修改成功，改对应缓存中的门锁名称
                                        List<DeviceInfo> list = AppContext.getmDoorLockDevices();
                                        for (int i = 0; i < list.size(); i++) {
                                            if (list.get(i).getUId() == deviceInfo.getUId()) {
                                                AppContext.getmDoorLockDevices().get(i).setDeviceName(editName);
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
     * 电量信息回调
     *
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
            AppContext.getDoorLockPowerMap().put(mDeviceUid,Integer.parseInt(powerInfo));
        }
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog(null);
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
                        final String password = et_password.getText().toString();
                        if (password.length() == 6) {
                            final byte[] passwd = password.getBytes();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (gateWayInfo != null) {
                                        String ver = new String(gateWayInfo.getVer());
                                        int ret = ver.compareTo("2.3.3");
                                        if (ret >=0) {
                                            try {
                                                byte[] aesByte = aes256encrypt(passwd);
                                                AppContext.getInstance().getSerialInstance().setGatedoorStateAES(deviceInfo,
                                                        state, 6, aesByte);// 发送密文
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            AppContext.getInstance().getSerialInstance().setGatedoorState(deviceInfo,
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

    /**
     * 设置锁的状态
     *
     *
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
                        final String password = et_password.getText().toString();
                        if (password.length() == 6) {
                            final byte[] passwd = password.getBytes();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (gateWayInfo != null) {
                                        String ver = new String(gateWayInfo.getVer());
                                        int ret = ver.compareTo("2.3.3");
                                        if (ret >=0) {
                                            try {
                                                byte[] aesByte = aes256encrypt(passwd);
                                                AppContext.getInstance().getSerialInstance().setGatedoorStateAES(deviceInfo,
                                                        state, 6, aesByte);// 发送密文
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            AppContext.getInstance().getSerialInstance().setGatedoorState(deviceInfo,
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

    public byte[] aes256encrypt(byte[] password) {

        byte[] snid;
        byte[] name;
        byte[] psd;
        snid = ByteStringUtil.hexStringToBytes(gateWayInfo.getSnid());
        byte[] name1 = gateWayInfo.getUname();
        byte[] psd1 = gateWayInfo.getPasswd();
        //由于返回回来的name和psd存在0x00，所以要先获取有效字节长度
        int length = ByteStringUtil.getVirtualValueLength(name1);
        int length1 = ByteStringUtil.getVirtualValueLength(psd1);
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
        byte[] ieee = deviceInfo.getIEEE();
        //获取endpoint
        byte ep = (byte) ((deviceInfo.getUId() >> 16) & 0x00ff);
        //拼接成一个数组
        byte[] longAdd = new byte[9];
        System.arraycopy(ieee, 0, longAdd, 0, 8);
        longAdd[8] = ep;

        //将长地址加入到key数组中
        System.arraycopy(longAdd, 0, key, nameLength
                + psdLength + snidLength, 9);

        byte[] ssss = "WANGLI2016".getBytes();

        //将补位字节加入
        System.arraycopy(ssss, 0, key, nameLength
                + psdLength + snidLength + 9, 10);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        endGetAbleState();// --------===========结束获取门锁允许被控制的状态
        //hideLoading();
        doorlockKey.clearAnimation();
    }


}
