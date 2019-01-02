package com.fbee.smarthome_wl.ui.videodoorlock;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.UpdateDoorLockName;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.request.videolockreq.DeviceInfoUpdateRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserAuthRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserDeviceStatusQueryRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserInitiateIntercomRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserTransportRequest;
import com.fbee.smarthome_wl.request.videolockreq.transportreq.DistanceOpenDoorLock;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.response.videolockres.DeviceTransportResponse;
import com.fbee.smarthome_wl.response.videolockres.MnsBaseResponse;
import com.fbee.smarthome_wl.response.videolockres.UserDeviceStatusQueryResponse;
import com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.EquesFlashShotActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.alarmlist.DoorlockAlarmActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.doorlockuser.DeviceUserListActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.setting.VideoLockSettingActivity;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.Base64Util;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.fbee.smarthome_wl.widget.pop.PopwindowChoose;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Subscription;
import rx.functions.Action1;

import static com.fbee.smarthome_wl.utils.ByteStringUtil.hexStringToBytes;
import static com.fbee.smarthome_wl.utils.ByteStringUtil.string2HexString;

public class DoorLockVideoInfoActivity extends BaseActivity<DoorLockVideoCallContract.Presenter> implements BaseRecylerAdapter.OnItemClickLitener, DoorLockVideoCallContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private ImageView videoView;
    private Socket socket;
    private String uuid;
    private String note;
    private PopwindowChoose popwindow;
    private ImageView video_view;
    private TextView dev_state;
    private ImageView iv_LinkSpeed;
    private ImageView iv_batteryImage_1;
    private TextView tv_batteryLevel;
    private ImageView btn_gate_lock;
    private ImageView iv_alarm_message;
    private ImageView btn_deviceusers;
    private ImageView iv_visitor;
    private ImageView btn_close_ring;
    private TextView tv_ring;
    private ImageView iv_captured;
    private boolean aBoolean;
    private QueryDeviceUserResponse deviceInfo;
    private long time;
    private int comfirmTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_lock_video_call);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        videoView = (ImageView) findViewById(R.id.iv_video_view);
        videoView.setOnClickListener(this);
        dev_state = (TextView) findViewById(R.id.tv_dev_state);
        dev_state.setOnClickListener(this);
        iv_LinkSpeed = (ImageView) findViewById(R.id.iv_LinkSpeed);
        iv_LinkSpeed.setOnClickListener(this);
        iv_batteryImage_1 = (ImageView) findViewById(R.id.iv_batteryImage_1);
        tv_batteryLevel = (TextView) findViewById(R.id.tv_batteryLevel);
        btn_gate_lock = (ImageView) findViewById(R.id.btn_gate_lock);
        btn_gate_lock.setOnClickListener(this);
        iv_alarm_message = (ImageView) findViewById(R.id.iv_alarm_message);
        iv_alarm_message.setOnClickListener(this);
        btn_deviceusers = (ImageView) findViewById(R.id.btn_deviceusers);
        btn_deviceusers.setOnClickListener(this);
        iv_visitor = (ImageView) findViewById(R.id.iv_visitor);
        iv_visitor.setOnClickListener(this);
        btn_close_ring = (ImageView) findViewById(R.id.btn_close_ring);
        btn_close_ring.setOnClickListener(this);
        tv_ring = (TextView) findViewById(R.id.tv_ring);
        tv_ring.setOnClickListener(this);
        iv_captured = (ImageView) findViewById(R.id.iv_captured);
        iv_captured.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        uuid = getIntent().getStringExtra("deviceUuid");
        LogUtil.e("服务器发送", "设备uuid:" + uuid);
        note = getIntent().getStringExtra("note");
        if (uuid == null) return;
        if (note == null) return;
        back.setVisibility(View.VISIBLE);
        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        back.setOnClickListener(this);
        ivRightMenu.setOnClickListener(this);
        initApi();
        createPresenter(new DoorLockVideoCallPresenter(this));

        //查询设备信息
        reqQueryDevice();


        initPopup();
        try {
            socket = BaseApplication.getInstance().getSocket();
            socket.on("MSG_USER_INITIATE_INTERCOM_RSP", startSpeaking);
            socket.on("MSG_USER_TRANSPORT_RSP", userTransportRsp);
            socket.on("MSG_USER_DEVICE_STATUS_QUERY_RSP", checkDeviceState);
            socket.on("MSG_USER_AUTH_RSP", userConfirmation);
            socket.on("MSG_DEVICE_TRANSPORT_REQ", deviceTransportRsp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //查询设备状态
        checkDeviceState();

        //接收视频锁状态改变
        receiveVideoLockStateChange();

        aBoolean = PreferencesUtils.getBoolean(uuid);
        if (aBoolean) {
            btn_close_ring.setImageResource(R.mipmap.cloes_ring);
            tv_ring.setText("免打扰(已关)");
        } else {
            btn_close_ring.setImageResource(R.mipmap.cloes_ring_selector);
            tv_ring.setText("免打扰(已开)");
        }
        title.setText(note);
        RxBus.getInstance().toObservable(UpdateDoorLockName.class).compose(TransformUtils.<UpdateDoorLockName>defaultSchedulers()).subscribe(new Action1<UpdateDoorLockName>() {
            @Override
            public void call(UpdateDoorLockName upDataGwName) {
                if(upDataGwName.getUuid()!=null&&upDataGwName.getUuid().equals(uuid)){
                    note = upDataGwName.UpdateName;
                    title.setText(upDataGwName.UpdateName);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.iv_video_view:
                if (isOnline) {
                    userStartSpeaking();
                } else {
                    showToast("设备不在线");
                }

                break;
            case R.id.iv_right_menu:
                if (popwindow != null && popwindow.isShowing()) {
                    colsePop();
                } else {
                    showPop();
                }
                break;
            case R.id.iv_visitor:
                Bundle bundle2 = new Bundle();
                bundle2.putString("deviceUuid", uuid);
                bundle2.putString("type", "visitor");
                bundle2.putString("deviceName", note);
                skipAct(DoorLockVideoRecordActivity.class, bundle2);
                break;
            case R.id.iv_alarm_message:
                Bundle bundle = new Bundle();
                bundle.putString("deviceUuid", uuid);
                bundle.putString("deviceName", note);
                skipAct(DoorlockAlarmActivity.class, bundle);
                break;
            case R.id.iv_captured:
                Bundle bundle4 = new Bundle();
                bundle4.putString(Method.ATTR_BUDDY_UID, uuid);
                bundle4.putString(Method.ATTR_BUDDY_NICK, note);
                skipAct(EquesFlashShotActivity.class, bundle4);
                break;
            case R.id.btn_deviceusers:
                Intent intent = new Intent(this, DeviceUserListActivity.class);
                intent.putExtra("deviceUuid", uuid);
                intent.putExtra("deviceInfo", deviceInfo);
                intent.putExtra("isOnline", isOnline);
                startActivity(intent);
                break;
            //远程开锁
            case R.id.btn_gate_lock:
                if (isOnline) {
                    if (deviceInfo != null && deviceInfo.getBody() != null && deviceInfo.getBody().getAuth_mode() != null) {
                        //单人模式
                        if (deviceInfo.getBody().getAuth_mode().equals("0")) {
                            openDoorLock(null);
                        }
                        //双人模式
                        else if (deviceInfo.getBody().getAuth_mode().equals("1")) {
                            showToast("双人模式,请输入第一位管理员密码!");
                            openDoorLock(null);
                        }
                    }
                } else {
                    showToast("设备不在线");
                }


                break;
            case R.id.btn_close_ring:
                isCloseRing();
                break;

        }
    }

    private void isCloseRing() {
        if (!aBoolean) {
            DialogManager.Builder builder = new DialogManager.Builder()
                    .msg("门锁呼叫提示和报警提示都将开启").title("")
                    .cancelable(false)
                    .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                    .rightBtnText("确定");

            DialogManager.showDialog(this, builder, new DialogManager.ConfirmDialogListener() {
                @Override
                public void onLeftClick() {

                }

                @Override
                public void onRightClick() {
                    btn_close_ring.setImageResource(R.mipmap.cloes_ring);
                    aBoolean = true;
                    PreferencesUtils.saveBoolean(uuid, aBoolean);
                    tv_ring.setText("免打扰(已关)");
                }
            });
        } else {
            DialogManager.Builder builder = new DialogManager.Builder()
                    .msg("门锁呼叫提示和报警提示都将关闭").title("")
                    .cancelable(false)
                    .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                    .rightBtnText("确定");

            DialogManager.showDialog(this, builder, new DialogManager.ConfirmDialogListener() {
                @Override
                public void onLeftClick() {

                }

                @Override
                public void onRightClick() {
                    btn_close_ring.setImageResource(R.mipmap.cloes_ring_selector);
                    aBoolean = false;
                    PreferencesUtils.saveBoolean(uuid, aBoolean);
                    tv_ring.setText("免打扰(已开)");
                }
            });
        }
    }

    /**
     * 请求查询设备信息
     */
    private void reqQueryDevice() {
        QueryDeviceuserlistReq body = new QueryDeviceuserlistReq();
        body.setVendor_name(FactoryType.GENERAL);
        body.setUuid(uuid);
        body.setShort_id(uuid);
        presenter.reqQueryDevice(body);
    }

    private boolean tag = false;

    /**
     * 用户发起对讲
     */
    private void userStartSpeaking() {
        tag = false;
        UserInitiateIntercomRequest bean = new UserInitiateIntercomRequest();
        bean.setToken(AppContext.getToken());
        bean.setApi_version("1.0");
        bean.setUuid(uuid);
        bean.setVendor_name(FactoryType.GENERAL);
        UserInitiateIntercomRequest.DataBean dataBean = new UserInitiateIntercomRequest.DataBean();
        dataBean.setAction("call");
        bean.setData(dataBean);
        String req = new Gson().toJson(bean);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_INITIATE_INTERCOM_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户开始对讲发送成功");
                    tag = true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean transportTag = false;
    private boolean deviceTransportTag = false;

    /**
     * 用户透传
     */
    private void userTransportReq(String pass) {
        transportTag = false;
        deviceTransportTag = false;
        UserTransportRequest bean = new UserTransportRequest();
        bean.setUuid(uuid);
        bean.setApi_version("1.0");
        bean.setVendor_name(FactoryType.GENERAL);
        bean.setToken(AppContext.getToken());
        bean.setCmd("REMOTE_UNLOCK");
        DistanceOpenDoorLock dodl = new DistanceOpenDoorLock();
        LogUtil.e("获取时间", "" + time);
        dodl.setTimestamp(String.valueOf(time));
        dodl.setCmd("REMOTE_UNLOCK");
        dodl.setUnlock_pwd(pass);
        String dodlreq = new Gson().toJson(dodl);
        String dodlreqresult = Base64Util.encode(dodlreq.getBytes());
        bean.setData(dodlreqresult);
        String req = new Gson().toJson(bean);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_TRANSPORT_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户透传发送成功");
                    transportTag = true;
                    deviceTransportTag = true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean deviceStatusTag = false;

    /**
     * 用户设备状态
     */
    private void checkDeviceState() {
        deviceStatusTag = false;
        UserDeviceStatusQueryRequest bean = new UserDeviceStatusQueryRequest();
        bean.setToken(AppContext.getToken());
        bean.setApi_version("1.0");
        List<UserDeviceStatusQueryRequest.DeviceListBean> deviceListBean = new ArrayList<>();
        UserDeviceStatusQueryRequest.DeviceListBean listBean = new UserDeviceStatusQueryRequest.DeviceListBean();
        listBean.setUuid(uuid);
        listBean.setVendor_name(FactoryType.GENERAL);
        deviceListBean.add(listBean);
        bean.setDevice_list(deviceListBean);
        String req = new Gson().toJson(bean);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_DEVICE_STATUS_QUERY_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户查询设备状态发送成功");
                    deviceStatusTag = true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Emitter.Listener startSpeaking = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (tag) {
                        tag = false;
                        JSONObject data = (JSONObject) args[0];
                        MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                        if (response != null && response.getReturn_string() != null) {
                            Log.e("服务器发送", "用户发起对讲返回:" + response.getReturn_string());
                            Toast.makeText(DoorLockVideoInfoActivity.this, "用户发起对讲返回" + response.getReturn_string(), Toast.LENGTH_SHORT).show();
                            if (response.getReturn_string().contains("SUCCESS")) {
                                Intent i = new Intent(DoorLockVideoInfoActivity.this, RtspPlayerActivity.class);
//                                Intent i = new Intent(DoorLockVideoInfoActivity.this, GainSpanVlcActivity.class);
                                i.putExtra("deviceUuid", uuid);
                                if (deviceInfo != null && deviceInfo.getBody() != null) {
                                    i.putExtra("networkSSID", deviceInfo.getBody().getNetwork_ssid());
                                    i.putExtra("deviceVersion", deviceInfo.getBody().getVersion());
                                    i.putExtra("authmode", deviceInfo.getBody().getAuth_mode());
                                }
                                startActivity(i);
                            }
                            //需要验证
                            else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                                comfirmTag = 1;
                                userConfirmationReq();
                            }
                            //token失效
                            else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING") || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                                needAddToken = 1;
                                reqAddToken();
                            }
                        }
                    }
                }
            });
        }
    };


    private Emitter.Listener userTransportRsp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (transportTag) {
                        transportTag = false;
                        JSONObject data = (JSONObject) args[0];
                        MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                        if (response != null && "REMOTE_UNLOCK".equals(response.getCmd())) {
                            showToast("用户透传返回:" + response.getReturn_string());
                            if (response.getReturn_string().contains("SUCCESS")) {
                                Log.e("服务器发送", "用户透传返回:" + response.getReturn_string());
                            }
                            //需要验证
                            else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                                comfirmTag = 3;
                                userConfirmationReq();
                            }

                            //token失效
                            else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING") || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                                needAddToken = 2;
                                reqAddToken();
                            }
                        }
                    }

                }
            });
        }
    };
    private boolean isOnline;
    private Emitter.Listener checkDeviceState = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (deviceStatusTag) {
                        deviceStatusTag = false;

                        JSONObject data = (JSONObject) args[0];
                        UserDeviceStatusQueryResponse response = new Gson().fromJson(data.toString(), UserDeviceStatusQueryResponse.class);
                        if (response != null && response.getReturn_string() != null) {
                            Log.e("服务器发送", "用户设备状态返回:" + response.getReturn_string());
                            Toast.makeText(DoorLockVideoInfoActivity.this, "用户设备状态返回" + response.getReturn_string(), Toast.LENGTH_SHORT).show();
                            if (response.getReturn_string().contains("SUCCESS")) {
                                List<UserDeviceStatusQueryResponse.StatusListBean> statusList = response.getStatus_list();
                                if (statusList != null) {
                                    String status = statusList.get(0).getStatus();
                                    String power = statusList.get(0).getPower();
                                    if (status != null) {
                                        if (status.equals("online")) {
                                            LogUtil.e("服务器发送", "在线状态:在线");
                                            isOnline = true;
                                            dev_state.setText("在线");
                                        }
                                    }
                                    if (power != null) {
                                        // iv_batteryImage_1
                                        int powerInt = Integer.parseInt(power);
                                        tv_batteryLevel.setText(power + "%");
                                        if (powerInt <= 10) {
                                            iv_batteryImage_1.setImageResource(R.mipmap.battery_0);
                                        } else if (powerInt <= 25) {
                                            iv_batteryImage_1.setImageResource(R.mipmap.battery_25);
                                        } else if (powerInt <= 50) {
                                            iv_batteryImage_1.setImageResource(R.mipmap.battery_50);
                                        } else if (powerInt <= 75) {
                                            iv_batteryImage_1.setImageResource(R.mipmap.battery_75);
                                        } else if (powerInt > 75) {
                                            iv_batteryImage_1.setImageResource(R.mipmap.battery_100);
                                        }
                                    }
                                }
                            }

                            //需要验证
                            else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                                comfirmTag = 2;
                                userConfirmationReq();
                            }

                            //token失效
                            else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING") || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                                needAddToken = 3;
                                reqAddToken();
                            }
                        }
                    }

                }
            });
        }
    };


    /**
     * 用户验证
     */
    private void userConfirmationReq() {
        UserAuthRequest request = new UserAuthRequest();
        request.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        request.setApi_version("1.0");
        request.setToken(AppContext.getToken());

        JSONObject jsonObject = null;
        try {
            String req = new Gson().toJson(request);
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_AUTH_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户验证发送成功");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Emitter.Listener userConfirmation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                    Log.e("服务器发送", "用户验证返回:" + response.getReturn_string());
                    if (response.getReturn_string().contains("SUCCESS")) {
                        switch (comfirmTag) {
                            case 1:
                                comfirmTag = 0;
                                userStartSpeaking();
                                break;
                            case 2:
                                comfirmTag = 0;
                                checkDeviceState();
                                break;
                            case 3:
                                comfirmTag = 0;
                                userTransportReq(aespasStr);
                                break;
                        }

                    }
                }
            });
        }
    };


    private Emitter.Listener deviceTransportRsp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (deviceTransportTag) {
                        deviceTransportTag = false;
                        JSONObject data = (JSONObject) args[0];
                        LogUtil.e("服务器发送", "开锁返回0" + data.toString());
                        DeviceTransportResponse response = new Gson().fromJson(data.toString(), DeviceTransportResponse.class);
                        if (response != null) {
                            if (response.getUuid() != null && response.getUuid().equals(uuid)) {
                                if (response.getData() != null) {
                                    byte[] resData = Base64Util.decode(response.getData());
                                    String resStrData = new String(resData);
                                    JsonObject jsonData = new Gson().fromJson(resStrData, JsonObject.class);
                                    LogUtil.e("服务器发送", "开锁返回1" + jsonData.toString());
                                    if (jsonData != null) {
                                        String returnStr = jsonData.get("return_string").getAsString();
                                        String returnCmd = jsonData.get("cmd").getAsString();
                                        if ("RETURN_SUCCESS_OK_STRING".equals(returnStr)) {
                                            if ("REMOTE_UNLOCK".equals(returnCmd)) {
                                                showToast("开锁成功!");
                                            }
                                        } else {
                                            showToast("开锁失败!");
                                        }
                                    } else {
                                        showToast("开锁失败!");
                                    }
                                }
                            }
                        } else {
                            showToast("开锁失败!");
                        }
                    }

                }
            });
        }
    };


    /**
     * 接收视频锁状态改变
     */
    private void receiveVideoLockStateChange(){
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                LogUtil.e("DoorLockVideoInfoActivity", throwable.getMessage() + "");
            }
        };
        Subscription state = RxBus.getInstance().toObservable(DeviceInfoUpdateRequest.class)
                .compose(TransformUtils.<DeviceInfoUpdateRequest>defaultSchedulers())
                .subscribe(new Action1<DeviceInfoUpdateRequest>() {
                    @Override
                    public void call(DeviceInfoUpdateRequest event) {
                        if(event==null)return;
                        if(uuid.equals(event.getUuid())&&event.getData()!=null&&event.getData().getStatus()!=null){
                            if("online".equals(event.getData().getStatus())||"bind".equals(event.getData().getStatus())){
                                LogUtil.e("服务器发送", "在线状态:在线");
                                isOnline = true;
                                dev_state.setText("在线");

                            }else{
                                isOnline = false;
                                dev_state.setText("离线");
                            }
                            String power=event.getData().getPower();
                            if (power != null) {
                                // iv_batteryImage_1
                                int powerInt = Integer.parseInt(power);
                                tv_batteryLevel.setText(power + "%");
                                if (powerInt <= 10) {
                                    iv_batteryImage_1.setImageResource(R.mipmap.battery_0);
                                } else if (powerInt <= 25) {
                                    iv_batteryImage_1.setImageResource(R.mipmap.battery_25);
                                } else if (powerInt <= 50) {
                                    iv_batteryImage_1.setImageResource(R.mipmap.battery_50);
                                } else if (powerInt <= 75) {
                                    iv_batteryImage_1.setImageResource(R.mipmap.battery_75);
                                } else if (powerInt > 75) {
                                    iv_batteryImage_1.setImageResource(R.mipmap.battery_100);
                                }
                            }
                        }

                    }
                },onErrorAction);
        mCompositeSubscription.add(state);
    }




    private void initPopup() {

        if (popwindow == null) {
            ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();

            PopwindowChoose.Menu pop = new PopwindowChoose.Menu(R.mipmap.add, "门锁配置");
            menulist.add(pop);
//            PopwindowChoose.Menu pop1 = new PopwindowChoose.Menu(R.mipmap.add, "抓拍图像");
//            menulist.add(pop1);
//            PopwindowChoose.Menu pop2 = new PopwindowChoose.Menu(R.mipmap.add, "操作记录");
//            menulist.add(pop2);
            popwindow = new PopwindowChoose(this, menulist, this, this, true);
            popwindow.setAnimationStyle(R.style.popwin_anim_style);
        }

    }

    private void colsePop() {
        if (popwindow != null && popwindow.isShowing())
            popwindow.dismiss();
    }

    private void showPop() {
        if (popwindow != null && !popwindow.isShowing()) {

            View view = popwindow.mContentView;
            //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
            int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
            int[] location = new int[2];

            // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
            ivRightMenu.getLocationOnScreen(location);
            //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
            popwindow.showAtLocation(ivRightMenu, Gravity.NO_GRAVITY, (location[0] + ivRightMenu.getWidth() / 2) - popupWidth / 2, location[1] + ivRightMenu.getHeight());

        }

    }

    @Override
    public void onItemClick(View view, int position) {
        //配置
        if (position == 0) {
            if (isOnline) {
                Intent intent = new Intent(this, VideoLockSettingActivity.class);
                intent.putExtra("deviceUuid", uuid);
                intent.putExtra("note", note);
                startActivity(intent);
            } else {
                showToast("设备不在线!");
            }
            colsePop();
        }
//        //抓拍截图
//        else if (position == 1) {
//            Bundle bundle4 = new Bundle();
//            bundle4.putString(Method.ATTR_BUDDY_UID, uuid);
//            bundle4.putString(Method.ATTR_BUDDY_NICK, note);
//            skipAct(EquesFlashShotActivity.class, bundle4);
//            colsePop();
//        } else if (position == 2) {
//            Bundle bundle5 = new Bundle();
//            bundle5.putString(Method.ATTR_BUDDY_UID, uuid);
//            bundle5.putString(Method.ATTR_BUDDY_NICK, note);
//            skipAct(OperationRecordActivity.class, bundle5);
//            colsePop();
//        }
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
     * 开启门锁
     */
    private void openDoorLock(final String firstPass) {
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
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
                        singleOrMoreTypeOpenVideoDoor(firstPass);
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

    private String aespasStr;

    private void singleOrMoreTypeOpenVideoDoor(String firstPass) {
        if (deviceInfo != null && deviceInfo.getBody() != null && deviceInfo.getBody().getAuth_mode() != null) {
            //单人模式
            if (deviceInfo.getBody().getAuth_mode().equals("0")) {
                // 对话框确定按钮
                final String password = et_password.getText().toString();
                LogUtil.e("加密", "输入密码str：" + password);
                if (password.length() == 6) {
                    String passStr = ByteStringUtil.myStringToString(password);
                    final byte[] passwd = ByteStringUtil.hexStringToBytes(passStr);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] key = aes256VideoDoorKey();
                            if (key == null) return;
                            String keyStr = ByteStringUtil.bytesToHexString(key);
                            LogUtil.e("加密", "秘钥：" + keyStr);
                            byte[] aesPas = aes256VideoDoorPass(passwd, key);
                            aespasStr = ByteStringUtil.bytesToHexString(aesPas);
                            LogUtil.e("加密", "密文：" + aespasStr);
                            if (aespasStr == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("开锁失败,请重试!");
                                    }
                                });
                                return;
                            }
                            userTransportReq(aespasStr);
                        }
                    }).start();
                    dialog.dismiss();
                    // //-----------------------------------------------华丽分割线------5.7
                } else if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort("密码不能为空!");
                } else if (password.length() < 6) {
                    ToastUtils.showShort("请输入6位密码!");
                }
            }
            //双人模式
            else if (deviceInfo.getBody().getAuth_mode().equals("1")) {
                // 对话框确定按钮
                final String password = et_password.getText().toString();
                LogUtil.e("加密", "输入密码str：" + password);
                if (password.length() == 6) {
                    if (firstPass == null) {
                        String passStr = ByteStringUtil.myStringToString(password);
                        dialog.dismiss();
                        openDoorLock(passStr);
                        showToast("双人模式,请输入第二位管理员密码!");
                    } else {

                        String passStr = ByteStringUtil.myStringToString(password);
                        String resultPass = firstPass + "2c" + passStr;
                        final byte[] passwd = ByteStringUtil.hexStringToBytes(resultPass);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] key = aes256VideoDoorKey();
                                if (key == null) return;
                                String keyStr = ByteStringUtil.bytesToHexString(key);
                                LogUtil.e("加密", "秘钥：" + keyStr);
                                byte[] aesPas = aes256VideoDoorPass(passwd, key);
                                aespasStr = ByteStringUtil.bytesToHexString(aesPas);
                                LogUtil.e("加密", "密文：" + aespasStr);
                                if (aespasStr == null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast("开锁失败,请重试!");
                                        }
                                    });
                                    return;
                                }
                                userTransportReq(aespasStr);
                            }
                        }).start();
                        dialog.dismiss();
                    }
                    // //-----------------------------------------------华丽分割线------5.7
                } else if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort("密码不能为空!");
                } else if (password.length() < 6) {
                    ToastUtils.showShort("请输入6位密码!");
                }
            }
        }
    }


    private byte[] aes256VideoDoorKey() {
        byte[] aes = null;
        if (deviceInfo != null && deviceInfo.getBody() != null && deviceInfo.getBody().getNetwork_ssid() != null ) {

            String lockSSID = deviceInfo.getBody().getNetwork_ssid();
//            String version = deviceInfo.getBody().getVersion();
            // String lockSSID="jija-yanfa";
            // String version="1.0.0-1.0.0";
            String content = uuid + lockSSID ;
            byte[] contentByte;
            if(content.length() <32){
                for (int i = content.length(); i <32 ; i++) {
                    content =content+"0";
                }
            }
            String content16Str = string2HexString(content);
            LogUtil.e("加密", "第一次加密内容：" + content16Str);
            contentByte = hexStringToBytes(content16Str);
            int len = contentByte.length;
            byte[] doorPsd = new byte[32];
            //拼接加密前明文
            if (len > 32) {
                System.arraycopy(contentByte, 0, doorPsd, 0, 32);
            } else {
                System.arraycopy(contentByte, 0, doorPsd, 0, len);
            }

            //加密秘钥
            String key = "WONLYAPPOPENSMARTLOCKKEY@@@@2017";
            String key16Str = string2HexString(key);
            LogUtil.e("加密", "第一次加密秘钥：" + key16Str);
            byte[] keyByte = hexStringToBytes(key16Str);

            try {
                aes = Aes256EncodeUtil.encrypt(doorPsd, keyByte);
            } catch (Exception e) {
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("参数不足,开锁失败!");
                }
            });
        }
        return aes;
    }

    private byte[] aes256VideoDoorPass(byte[] pas, byte[] key) {
        byte[] doorPsd = new byte[32];
        int pasLen = pas.length;
        time = System.currentTimeMillis() / 1000;
        String time16Str = Long.toHexString(time);
        byte[] timeByte = ByteStringUtil.hexStringToBytes(time16Str);
        //拼接加密前明文
        System.arraycopy(pas, 0, doorPsd, 0, pasLen);
        System.arraycopy(timeByte, 0, doorPsd, 28, 4);
        LogUtil.e("加密", "第二次加密内容：" + ByteStringUtil.bytesToHexString(doorPsd).toUpperCase());
        byte[] aes = null;
        try {
            aes = Aes256EncodeUtil.encrypt(doorPsd, key);
        } catch (Exception e) {
        }
        return aes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.off("MSG_USER_INITIATE_INTERCOM_RSP", startSpeaking);
            socket.off("MSG_USER_TRANSPORT_RSP", userTransportRsp);
            socket.off("MSG_USER_DEVICE_STATUS_QUERY_RSP", checkDeviceState);
            socket.off("MSG_USER_AUTH_RSP", userConfirmation);
            socket.off("MSG_DEVICE_TRANSPORT_REQ", deviceTransportRsp);
        }

    }

    //请求设备信息返回
    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {
        if (bean == null) return;
        if (bean.getHeader().getHttp_code().equals("200")) {
            deviceInfo = bean;
//            deviceInfo.getBody().setNetwork_ssid("jija-yanfa");
//            deviceInfo.getBody().setAuth_mode("1");
//            deviceInfo.getBody().setVersion("1.0.0-1.0.0");
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    @Override
    public void resAddDevice(BaseResponse bean) {

    }


    @Override
    public void resDeleteGateWay(BaseResponse bean) {

    }


    /**
     * 请求token
     */
    private void reqAddToken() {
        AddTokenReq addTokenReq = new AddTokenReq();
        addTokenReq.setAttitude("read");
        addTokenReq.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        addTokenReq.setSecret_key(AppContext.getInstance().getBodyBean().getSecret_key());
        presenter.reqAddToken(addTokenReq);
    }

    private int needAddToken = 0;

    /**
     * 添加token返回
     *
     * @param bean
     */
    @Override
    public void resAddToken(AddTokenResponse bean) {
        if ("200".equals(bean.getHeader().getHttp_code())) {
            if (bean.getBody() != null && bean.getBody().getToken() != null) {
                LogUtil.e("token", "" + bean.getBody().getToken());
                AppContext.setToken(bean.getBody().getToken());
                switch (needAddToken) {
                    //用户发起对讲
                    case 1:
                        needAddToken = 0;
                        userStartSpeaking();
                        break;
                    //用户透传
                    case 2:
                        needAddToken = 0;
                        userTransportReq(aespasStr);
                        break;
                    //检查设备状态
                    case 3:
                        needAddToken = 0;
                        checkDeviceState();
                        break;
                }
            }
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
