package com.fbee.smarthome_wl.ui.mainsimplify.home.doorlock;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.bean.ArriveReportCBInfo;
import com.fbee.smarthome_wl.bean.DoorLockStateInfo;
import com.fbee.smarthome_wl.bean.GateWayInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DoorLockGlobal;
import com.fbee.smarthome_wl.event.ControlTimeEvent;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.DoorLockAlarmResponse;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockActivity;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockContract;
import com.fbee.smarthome_wl.ui.doorlock.DoorlockPresenter;
import com.fbee.smarthome_wl.ui.doorlocklog.DoorLockLogActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.ui.usermanage.UserManageActivity;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.BadgeUtil;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.DateUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TimerCount;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.Tool;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.R.id.tv_online_state;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;


/**
 * 门锁设备
 */
public class DlFragment extends BaseFragment<DoorLockContract.Presenter> implements View.OnClickListener,DoorLockContract.View{

    private ImageView imageOpenLock;
    private TextView tvOnlineState;
    private ImageView imageAccountNum;
    private TextView tvPowerState;
    private ImageView imageLockRecord;
    private TextView tvDeviceInfo;
    private TimerCount timer;
    private boolean isControlAble = false;// 默认不可控
    private int mDeviceUid;
    private DeviceInfo deviceInfo;
    private GateWayInfo gateWayInfo;
    private String phone;
    private HashMap<String, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean>> userEntityMap;
    private String mUserName;
    private String mPassword;
    private Long mStartTime;
    private Long mEndTime;
    private String deviceName = "devicename";
    private String deviceId = "deviceid";
    private String deviceIeee="deviceIeee";
    private String mDeviceIeee;
    private String mDeviceName;
    private String itemDviceInfo = "itemDviceInfo";
    private int requestCode = 500;


    public DlFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_dl;
    }

    @Override
    public void initView() {
        imageOpenLock = (ImageView) mContentView.findViewById(R.id.image_open_lock);
        tvOnlineState = (TextView) mContentView.findViewById(tv_online_state);
        imageAccountNum = (ImageView) mContentView.findViewById(R.id.image_account_num);
        tvPowerState = (TextView) mContentView.findViewById(R.id.tv_power_state);
        imageLockRecord = (ImageView) mContentView.findViewById(R.id.image_lock_record);
        tvDeviceInfo = (TextView) mContentView.findViewById(R.id.tv_device_info);
        imageOpenLock.setOnClickListener(this);
        imageAccountNum.setOnClickListener(this);
        imageLockRecord.setOnClickListener(this);
        tvDeviceInfo.setOnClickListener(this);
        tvPowerState.setOnClickListener(this);
        tvOnlineState.setOnClickListener(this);
    }

    @Override
    public void bindEvent() {
        initApi();
        createPresenter(new DoorlockPresenter(this));
        deviceInfo= (DeviceInfo) getArguments().getSerializable("Dl");
        phone = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        if(deviceInfo!=null){
            mDeviceUid = deviceInfo.getUId();
            mDeviceIeee=ByteStringUtil.bytesToHexString(deviceInfo.getIEEE()).toUpperCase();
            mDeviceName = deviceInfo.getDeviceName();
            byte state=deviceInfo.getDeviceStatus();
            if(state>0){
                tvOnlineState.setText("在线");
                tvOnlineState.setTextColor(getResources().getColor(R.color.colorAccent));
            }else{
                tvOnlineState.setText("离线");
                tvOnlineState.setTextColor(getResources().getColor(R.color.red));
            }
        }
        //获取当前网关
        LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        if (gw == null) {
            return;
        }
        mUserName = gw.getUsername();
        mPassword = gw.getPassword();
        isControlTimeRunning();
        receiveControlTimeEvent();
        receiveGateWayInfo();
        //设置缓存最新电量
        setNativePower();
        mStartTime = DateUtil.getLastThreeDayTime();
        mEndTime = DateUtil.getCurrentTime();
        //获取门锁第一条电量展示
        getNetPowerFirstRecordPersent(String.valueOf(mStartTime / 1000), String.valueOf(mEndTime / 1000));
        //请求网关信息
        AppContext.getInstance().getSerialInstance().getGateWayInfo();
        receiveDoorLockPowerInfo();
        receiveDoorLockStateInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //远程开锁
            case R.id.image_open_lock:
                if (!isControlAble) {// 如果门锁不可控，未激活
                    showToast("门锁未激活");
                    break;
                }
                unlock();// 已经激活，输入密码开锁
                break;

            //门锁账号
            case R.id.image_account_num:
                //用户
                Bundle bun = new Bundle();
                bun.putInt(deviceId, mDeviceUid);
                bun.putString(deviceIeee,mDeviceIeee);
                skipAct(UserManageActivity.class, bun);
                break;

            //门锁记录
            case R.id.image_lock_record:
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
                                jsonObject.put("messageCount", 0);
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
                notifyBadgeNumber();

                Bundle bundle = new Bundle();
                bundle.putString(deviceName, mDeviceName);
                bundle.putString(deviceIeee,mDeviceIeee);
                bundle.putInt(deviceId, mDeviceUid);
                skipAct(DoorLockLogActivity.class, bundle);
                break;

            //门锁详情
            case R.id.tv_device_info:
                Intent intent = new Intent(getActivity(), DoorLockActivity.class);
                intent.putExtra(itemDviceInfo, deviceInfo);
                startActivityForResult(intent, requestCode);

                break;
            case R.id.tv_online_state:
                Intent intent1 = new Intent(getActivity(), DoorLockActivity.class);
                intent1.putExtra(itemDviceInfo, deviceInfo);
                startActivityForResult(intent1, requestCode);
                break;
            case R.id.tv_power_state:
                Intent intent2 = new Intent(getActivity(), DoorLockActivity.class);
                intent2.putExtra(itemDviceInfo, deviceInfo);
                startActivityForResult(intent2, requestCode);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isControlTimeRunning();
    }

    //设置缓存电量
    private void setNativePower(){
        if(AppContext.getDoorLockPowerMap().get(mDeviceUid)!=null){
            int power=AppContext.getDoorLockPowerMap().get(mDeviceUid)/2;
            tvPowerState.setText(power+"%");
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
            BadgeUtil.setBadgeCount(getActivity(), count,
                    Build.MANUFACTURER);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        presenter.getStatus(paramsMap,"6");

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
                            int nowSecond=event.getNowTime();
                            if(nowSecond>1){
                                isControlAble=true;
                            }else{
                                isControlAble = false;
                            }
                        }
                    }
                });
        mCompositeSubscription.add(subHint);
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
                                        //showDoorLockDvcState(100);
                                    } else if (event.getData() == DoorLockGlobal.BATTERY_STATE_LOW_POWER_ALARM) {
                                        //可以设置低电量状态图片
                                        tvPowerState.setText("5%");
                                    }
                                    break;
                                case DoorLockGlobal.BATTERY_ATTRID_POWER:
                                    //设置电量百分比
                                    int powerPerent = event.getData() / 2;
                                    tvPowerState.setText(powerPerent+"%");
                                    break;
                            }
                        }
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
     * 判断服务是否在进行计时
     */
    private void isControlTimeRunning() {
        if (AppContext.timerMap != null) {
            timer = AppContext.timerMap.get(mDeviceUid);
            if (null != timer) {
                if (timer.isRuning()) {
                    isControlAble = true;
                } else {
                    isControlAble = false;
                }
            } else {
                isControlAble = false;
            }
        } else {
            isControlAble = false;
        }
    }

    /**
     * 开锁
     */
    private void unlock() {
        setDoorLockState(1);
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
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除对话框的标题
        dialog.setContentView(R.layout.unlock_password_dialog);
        // 在代码中设置界面大小的方法:
        Display display = getActivity().getWindowManager().getDefaultDisplay();
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
                                        int res=ver.compareTo("3.0.0");
                                        if (ret >=0) {
                                            try {
                                                if(res>=0){
                                                    AppContext.getInstance().getSerialInstance().setGatedoorStateID2(deviceInfo,
                                                            state, encrypt(passwd));
                                                }else{
                                                    byte[] aesByte = aes256encrypt(passwd);
                                                    AppContext.getInstance().getSerialInstance().setGatedoorStateAES(deviceInfo,
                                                            state, 6, aesByte);// 发送密文
                                                }
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
            showToast(text.toString());
            setReset(3000, true);
        } else if (Flag == 2 || Flag == 0x7f) {
            text.append("远程开锁未允许");
            showToast(text.toString());
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
                showToast(text.toString());
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
                showToast(text.toString());
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
                showToast(text.toString());
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
                showToast(text.toString());
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
                showToast(text.toString());
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
                showToast(text.toString());
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
                    showToast(text.toString());
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
                    showToast(text.toString());
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

                    showToast(text.toString());
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

                    showToast(text.toString());
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

                    showToast(text.toString());
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

                    showToast(text.toString());
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

                    showToast(text.toString());
                    setReset(3000, false);
                }
            }// 非法操作报警
            else if (bytes[3] == 0x03) {
                text.append("非法操作");
                showToast(text.toString());
                setReset(3000, false);
            }// 非法卡
            else if (bytes[3] == 0x05) {
                text.append("门锁未关");
                showToast(text.toString());
                setReset(3000, false);
            }
        }
    }
    /**
     * 重置图标
     */
    private void setReset(int time, final boolean flag) {
        if (flag) {
            isControlAble = true;
        } else {
            stopTimer();
            isControlAble = false;
        }
//        mHandler.postDelayed(new Runnable() {
//
//            @SuppressLint("NewApi")
//            @Override
//            public void run() {
//                if (flag) {
//                    isControlAble = true;
//                } else {
//                    stopTimer();
//                    isControlAble = false;
//                }
//            }
//        }, time);
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
            }
        }

    }

    //进入菜单
    private void Enter_Menu(int newmodle, StringBuffer text, int cardNum,
                            String str) {
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
        showToast(text.toString());
        setReset(3000, false);
    }

    private void Unlocking(int newmodle, StringBuffer text, int cardNum,
                           String str) {
        if (newmodle == 0) {
            text.append(str + "开锁");
            showToast(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x10) {
            text.append(str + "开锁(双人模式)");
            showToast(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x02) {
            text.append(str + "开锁取消常开");
            showToast(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x01) {
            text.append(str + "开锁启用常开");
            showToast(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x12) {
            text.append(str + "开锁取消常开(双人模式)");
            showToast(text.toString());
            setReset(3000, false);
        } else if ((newmodle & 0xbf) == 0x11) {
            text.append(str + "开锁启用常开(双人模式)");
            showToast(text.toString());
            setReset(3000, false);
        } else {
            if ((newmodle & 0xbf) != 0x80 && (newmodle & 0xbf) != 0x90 && (newmodle & 0xbf) != 0x82 && (newmodle & 0xbf) != 0x81 && (newmodle & 0xbf) != 0x91 && (newmodle & 0xbf) != 0x92) {
                text.append(str + "开锁");
                showToast(text.toString());
                setReset(3000, false);
            }
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

    @Override
    public void responseDoorInfo(DoorlockpowerInfo info) {
        if (info != null) {
            if (info.getValue().size() == 0) {
                return;
            }
            String powerInfo = info.getValue().get(0);
            int firstPower = Integer.parseInt(powerInfo) / 2;
            tvPowerState.setText(firstPower+"%");
            AppContext.getDoorLockPowerMap().put(mDeviceUid,Integer.parseInt(powerInfo));
        }
    }

    @Override
    public void responseDoorAlarm(DoorLockAlarmResponse info) {

    }

    @Override
    public void responseDeleteDoorAlarm(BaseResponse info) {

    }

    @Override
    public void responseDeviceModel(DoorlockpowerInfo info) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
