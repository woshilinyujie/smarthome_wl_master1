package com.fbee.smarthome_wl.ui.videodoorlock.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.bean.Pus;
import com.fbee.smarthome_wl.bean.SettingPramasInfo;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.UpdateDoorLockName;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.VideolockDelEvent;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.PusBodyBean;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.request.videolockreq.UserAuthRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserDeviceStatusQueryRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserTransportRequest;
import com.fbee.smarthome_wl.request.videolockreq.transportreq.RestartDevice;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.response.UpdateVideoLockResponse;
import com.fbee.smarthome_wl.response.videolockres.DeviceTransportResponse;
import com.fbee.smarthome_wl.response.videolockres.FileDownloadResponse;
import com.fbee.smarthome_wl.response.videolockres.MnsBaseResponse;
import com.fbee.smarthome_wl.response.videolockres.UserDeviceStatusQueryResponse;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.DoorLockVideoCallContract;
import com.fbee.smarthome_wl.ui.videodoorlock.DoorLockVideoCallPresenter;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.Base64Util;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Subscriber;
import rx.Subscription;

import static com.fbee.smarthome_wl.utils.ByteStringUtil.hexStringToBytes;
import static com.fbee.smarthome_wl.utils.ByteStringUtil.string2HexString;

public class VideoLockSettingActivity extends BaseActivity<DoorLockVideoCallContract.Presenter> implements DoorLockVideoCallContract.View, View.OnClickListener {
    private String uuid;
    private String note;
    /*** 报警数据类型*/
    public static final int ALARM = 1;
    /*** 音量等级*/
    public static final int LEVEL = 2;
    /*** 验证模式*/
    public static final int AUTH = 3;
    /*** 警报灵敏度*/
    public static final int SENSITIVE = 4;
    /**
     * 红外报警检测的间隔时间
     */
    public static final int TIME = 5;
    private QueryDeviceUserResponse deviceInfo;
    private Socket socket;
    private SettingPramasInfo info;
    private String infrared_sensitivity;
    private TextView tvDevName;
    private LinearLayout linearSetDevName;
    private TextView tvDevType;
    private CheckBox checkDevHumanDetection;
    private CheckBox checkDevHumanDetectionArlm;
    private TextView tvDevArlmTime;
    private LinearLayout linearArlmTime;
    private TextView tvArlmtype;
    private LinearLayout linearArlmtype;
    private TextView linearInfraredSense;
    private LinearLayout llInfraredSense;
    private TextView tvAuthMode;
    private LinearLayout linearAuthMode;
    private TextView tvVoicelevel;
    private LinearLayout linearVoicelevel;
    private CheckBox checkPassValidate;
    private CheckBox checkNetworkModle;
    private CheckBox checkDevNotCloseArlm;
    private CheckBox checkDevFalseArlm;
    private TextView tvVersion;
    private TextView tvStrcodeInfo;
    private TextView tvDevMac;
    private TextView tvDevSsid;
    private TextView tvDevTelecontrol;
    private Button btnVideolockRestartDevice;
    private Button btnDevDelete;
    private ImageView back;
    private TextView title;
    private TextView tvRightMenu;


    private AlertDialog alertDialog;
    private Map<Integer, Integer> OnOffMap;
    private String infrared_alarm_interval_time;
    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lock_setting);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText("门锁配置");
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("完成");
        tvRightMenu.setOnClickListener(this);
        tvDevName = (TextView) findViewById(R.id.tv_devName);
        linearSetDevName = (LinearLayout) findViewById(R.id.linear_setDevName);
        linearSetDevName.setOnClickListener(this);
        tvDevType = (TextView) findViewById(R.id.tv_devType);
        checkDevHumanDetection = (CheckBox) findViewById(R.id.check_devHuman_detection);
        checkDevHumanDetectionArlm = (CheckBox) findViewById(R.id.check_devHuman_detection_arlm);
        tvDevArlmTime = (TextView) findViewById(R.id.tv_dev_arlmTime);
        linearArlmTime = (LinearLayout) findViewById(R.id.linear_arlmTime);
        linearArlmTime.setOnClickListener(this);
        tvArlmtype = (TextView) findViewById(R.id.tv_arlmtype);
        linearArlmtype = (LinearLayout) findViewById(R.id.linear_arlmtype);
        linearArlmtype.setOnClickListener(this);
        linearInfraredSense = (TextView) findViewById(R.id.linear_infrared_sense);
        llInfraredSense = (LinearLayout) findViewById(R.id.ll_infrared_sense);
        llInfraredSense.setOnClickListener(this);
        tvAuthMode = (TextView) findViewById(R.id.tv_auth_mode);
        linearAuthMode = (LinearLayout) findViewById(R.id.linear_auth_mode);
        linearAuthMode.setOnClickListener(this);
        tvVoicelevel = (TextView) findViewById(R.id.tv_voicelevel);
        linearVoicelevel = (LinearLayout) findViewById(R.id.linear_voicelevel);
        linearVoicelevel.setOnClickListener(this);
        tvDevTelecontrol= (TextView) findViewById(R.id.tv_dev_telecontrol);
        findViewById(R.id.layout_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUpdate();
            }
        });
        //0,1,1,0,1,0,1,0 对应 关闭红外报警，开启门锁常开，开启门未关报警，关闭假锁报警，开启密码功能，关闭红外检测，开启网络常在线，关闭远程控制
        //智能人体侦测报警
        checkDevHumanDetectionArlm = (CheckBox) findViewById(R.id.check_devHuman_detection_arlm);
        //门锁常开
        //门未关报警
        checkDevNotCloseArlm = (CheckBox) findViewById(R.id.check_dev_notClose_arlm);
        //假锁报警
        checkDevFalseArlm = (CheckBox) findViewById(R.id.check_dev_false_arlm);
        //门锁密码验证功能
        checkPassValidate = (CheckBox) findViewById(R.id.check_passValidate);
        //智能人体侦测
        checkDevHumanDetection = (CheckBox) findViewById(R.id.check_devHuman_detection);
        //门锁常联网模式
        checkNetworkModle = (CheckBox) findViewById(R.id.check_network_modle);
        //远程控制

        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setOnClickListener(this);
        tvStrcodeInfo = (TextView) findViewById(R.id.tv_strcode_info);
        tvDevMac = (TextView) findViewById(R.id.tv_dev_mac);
        tvDevSsid = (TextView) findViewById(R.id.tv_dev_ssid);
        tvDevSsid.setOnClickListener(this);
        btnVideolockRestartDevice = (Button) findViewById(R.id.btn_videolock_restartDevice);
        btnVideolockRestartDevice.setOnClickListener(this);
        btnDevDelete = (Button) findViewById(R.id.btn_dev_delete);
        btnDevDelete.setOnClickListener(this);
    }

    private void setOnCheckListener() {
        //智能人体侦测报警
        checkDevHumanDetectionArlm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    OnOffMap.put(0, 1);
                } else {
                    OnOffMap.put(0, 0);
                }

            }
        });
        //门锁常开 是个button
        //门未关报警
        checkDevNotCloseArlm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    OnOffMap.put(2,1);
                }else{
                    OnOffMap.put(2,0);
                }
            }
        });

        //假锁报警
        checkDevFalseArlm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    OnOffMap.put(3,1);
                }else{
                    OnOffMap.put(3,0);
                }
            }
        });
        //门锁密码验证功能
        checkPassValidate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    OnOffMap.put(4,1);
                }else{
                    OnOffMap.put(4,0);
                }
            }
        });
        //智能人体侦测
        checkDevHumanDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    OnOffMap.put(5,1);
                }else{
                    OnOffMap.put(5,0);
                }
            }
        });
        //门锁常联网模式
        checkNetworkModle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    OnOffMap.put(6,1);
                }else{
                    OnOffMap.put(6,0);
                }
            }
        });

        //远程控制  仅展示
    }

    @Override
    protected void initData() {
        initApi();
        uuid = getIntent().getStringExtra("deviceUuid");
        LogUtil.e("服务器发送", "设备uuid:" + uuid);
        note = getIntent().getStringExtra("note");
        if (uuid == null) return;
        if (note == null) return;
        createPresenter(new DoorLockVideoCallPresenter(this));
        OnOffMap=new HashMap<>();
        setOnCheckListener();
        for (int i = 0; i <8 ; i++) {
            OnOffMap.put(i,0);
        }
        reqQueryDevice();
        showLoadingDialog(null);

        try {
            socket = BaseApplication.getInstance().getSocket();
            socket.on("MSG_USER_TRANSPORT_RSP", userTransportRsp);
            socket.on("MSG_DEVICE_TRANSPORT_REQ",deviceTransportRsp);
            socket.on("MSG_USER_AUTH_RSP", userConfirmation);
            socket.on("MSG_USER_DEVICE_STATUS_QUERY_RSP", checkDeviceState);
        } catch (Exception e) {
        }

        checkDeviceState();
        info = new SettingPramasInfo();

        //设置名字
        tvDevName.setText(note);
    }

    /**
     * 用户验证
     */
    private void userConfirmationReq(){
        UserAuthRequest request=new UserAuthRequest();
        request.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        request.setApi_version("1.0");
        request.setToken(AppContext.getToken());

        JSONObject jsonObject=null;
        try {
            String req=new Gson().toJson(request);
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_AUTH_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送","用户验证发送成功");
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
                    MnsBaseResponse response= new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                    Log.e("服务器发送","用户验证返回:"+response.getReturn_string());
                    if(response.getReturn_string().contains("SUCCESS")){
                        switch (needConfirmTag){
                            //参数设置
                            case 1:
                                needConfirmTag=0;
                                userTransportReq(aespasStr);
                                break;
                            //重启设备
                            case 2:
                                needConfirmTag=0;
                                userRestartReq(aespasStr);
                                break;
                            //检查设备状态
                            case 3:
                                needConfirmTag=0;
                                checkDeviceState();
                                break;
                        }

                    }
                }
            });
        }
    };


    private int needConfirmTag=0;
   // private int transportTag = 0;
    private int needAddToken=0;
    private Emitter.Listener userTransportRsp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                        JSONObject data = (JSONObject) args[0];
                        MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                        if (response != null) {
                            if("DEVICE_CONFIG".equals(response.getCmd())){
                                showToast("用户透传返回:" + response.getReturn_string());
                                if (response.getReturn_string().contains("SUCCESS")) {
                                    Log.e("服务器发送", "用户透传返回:" + response.getReturn_string());
                                    // showToast("设备参数设置成功!");
                                    //finish();
                                }
                                //需要验证
                                else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                                    needConfirmTag=1;
                                    userConfirmationReq();
                                } else if(response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING")||response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")){
                                    needAddToken=1;
                                    reqAddToken();
                                }
                            }else if("REMOTE_RESTART_DEVICE".equals(response.getCmd())){
                                showToast("用户透传返回:" + response.getReturn_string());
                                if (response.getReturn_string().contains("SUCCESS")) {
                                    Log.e("服务器发送", "用户透传返回:" + response.getReturn_string());
                                    showToast("远程重启设备设置成功!");
                                    finish();
                                }
                                //需要验证
                                else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                                    needConfirmTag=2;
                                    userConfirmationReq();
                                } else if(response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING")||response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")){
                                    needAddToken=2;
                                    reqAddToken();
                                }
                            }
                            //文件下载透传返回
                            else if("FILE_DOWNLOAD".equals(response.getCmd())){
                                DialogManager.Builder builder = new DialogManager.Builder()
                                        .msg("设备开始升级，设备指示灯将闪烁，升级过程中切勿断电；设备升级过程中，命令响应不正常，请等待升级成功再操作；").cancelable(false).title("更新提示")
                                        .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                                        .rightBtnText("确定");
                                DialogManager.showDialog(VideoLockSettingActivity.this, builder, new DialogManager.ConfirmDialogListener() {

                                    @Override
                                    public void onLeftClick() {

                                    }

                                    @Override
                                    public void onRightClick() {
                                    }
                                });

                            }

                        } else {
                            showToast("操作失败!");
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
                    showToast("收到设备透传!");
                    JSONObject data = (JSONObject) args[0];
                    DeviceTransportResponse response = new Gson().fromJson(data.toString(), DeviceTransportResponse.class);
                    if (response != null) {
                        if (response.getUuid() != null && response.getUuid().equals(uuid)) {
                            if (response.getData() != null) {
                                byte[] resData = Base64Util.decode(response.getData());
                                String resStrData = new String(resData);
                                JsonObject jsonData = new Gson().fromJson(resStrData, JsonObject.class);
                                if (jsonData != null) {
                                    String returnStr = jsonData.get("return_string").getAsString();
                                    String returnCmd = jsonData.get("cmd").getAsString();
                                    showToast("设备透传:"+returnStr);
                                    if ("RETURN_SUCCESS_OK_STRING".equals(returnStr)) {
                                        if ("DEVICE_CONFIG".equals(returnCmd)) {
                                            showToast("设备参数设置成功!");
                                            finish();
                                        }
                                    }else{
                                        showToast("操作失败!");
                                    }
                                }else{
                                    showToast("操作失败!");
                                }
                            }
                        }
                    }else{
                        showToast("操作失败!");
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
                    if(deviceStateTag){
                        deviceStateTag=false;

                        JSONObject data = (JSONObject) args[0];
                        UserDeviceStatusQueryResponse response = new Gson().fromJson(data.toString(), UserDeviceStatusQueryResponse.class);
                        if (response != null && response.getReturn_string() != null) {
                            Log.e("服务器发送", "用户设备状态返回:" + response.getReturn_string());
                            showToast("用户设备状态返回" + response.getReturn_string());
                            if (response.getReturn_string().contains("SUCCESS")) {
                                List<UserDeviceStatusQueryResponse.StatusListBean> statusList = response.getStatus_list();
                                if (statusList != null) {
                                    String status = statusList.get(0).getStatus();
                                    String power = statusList.get(0).getPower();
                                    if (status != null) {
                                        if (status.equals("online")) {
                                            LogUtil.e("服务器发送", "在线状态:在线");
                                            isOnline = true;

                                        }
                                    }

                                }
                            }

                            //需要验证
                            else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                                needConfirmTag=3;
                                userConfirmationReq();
                            }

                            //token失效
                            else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING")||response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                                needAddToken=3;
                                reqAddToken();
                            }
                        }
                    }

                }
            });
        }
    };
    private boolean deviceStateTag=false;
    /**
     * 用户设备状态
     */
    private void checkDeviceState() {
        deviceStateTag=false;
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
                    deviceStateTag=true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存用户设备配置
     *
     * @param pass
     */
    private void userTransportReq(String pass) {
       // transportTag = 0;
        UserTransportRequest bean = new UserTransportRequest();
        bean.setUuid(uuid);
        bean.setApi_version("1.0");
        bean.setVendor_name(FactoryType.GENERAL);
        bean.setToken(AppContext.getToken());
        bean.setCmd("DEVICE_CONFIG");
        info.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000l));
        info.setUnlock_adm_pwd(pass);
        info.setOn_off(getOnOffResult());
        String dodlreq = new Gson().toJson(info);
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
//                    transportTag = 1;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 远程重启设备
     */
    private void userRestartReq(String pass) {
        //transportTag = 0;
        UserTransportRequest bean = new UserTransportRequest();
        bean.setUuid(uuid);
        bean.setApi_version("1.0");
        bean.setVendor_name(FactoryType.GENERAL);
        bean.setToken(AppContext.getToken());
        bean.setCmd("REMOTE_RESTART_DEVICE");
        RestartDevice data = new RestartDevice();
        data.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000l));
        data.setCmd("REMOTE_RESTART_DEVICE");
        data.setUnlock_adm_pwd(pass);
        String dodlreq = new Gson().toJson(data);
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
                   // transportTag = 2;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //视频锁升级
    private void userUpdateReq(String part,String url,String md5){
        UserTransportRequest bean = new UserTransportRequest();
        bean.setUuid(uuid);
        bean.setApi_version("1.0");
        bean.setVendor_name(FactoryType.GENERAL);
        bean.setToken(AppContext.getToken());
        bean.setCmd("FILE_DOWNLOAD");

        FileDownloadResponse filedown = new FileDownloadResponse();
        filedown.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000l));
        filedown.setCmd("FILE_DOWNLOAD");
        filedown.setPart(part);
        filedown.setFile_url(url);
        filedown.setMd5(md5);
        String data = new Gson().toJson(filedown);
        String result = Base64Util.encode(data.getBytes());
        bean.setData(result);
        String req = new Gson().toJson(bean);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_TRANSPORT_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户透传发送成功");
                    // transportTag = 2;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    private int right_menuOrRestartDeviceType = 0;
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, VLParametersettingActivity.class);
        switch (v.getId()) {

            case R.id.back:
                finish();
                break;

            //完成
            case R.id.tv_right_menu:
                if(isOnline){
                    if (deviceInfo != null && deviceInfo.getBody().getAuth_mode() != null) {
                        right_menuOrRestartDeviceType = 1;
                        showCustomizeDialog();
                    } else {
                        showToast("获取信息失败!");
                    }
                }else{
                   showToast("设备不在线");
                }
                break;

            //设备名设置
            case R.id.linear_setDevName:
                modifyDevName();
                break;
            //报警数据类型
            case R.id.linear_arlmtype:
                intent.putExtra("prama", ALARM);
                String mtype = tvArlmtype.getText().toString();
                if (mtype.length()>0){
                    if(mtype.contains("pic")){
                        mtype="pic";
                    }else if(mtype.contains("av")){
                        mtype="av";
                    }
                }
                intent.putExtra("data",mtype);
                startActivityForResult(intent, ALARM);
                break;

            //音量等级
            case R.id.linear_voicelevel:
                intent.putExtra("prama", LEVEL);
                intent.putExtra("data", tvVoicelevel.getText().toString());
                startActivityForResult(intent, LEVEL);
                break;

            //红外灵敏度
            case R.id.ll_infrared_sense:
                intent.putExtra("prama", SENSITIVE);
                intent.putExtra("data", linearInfraredSense.getText().toString());
                startActivityForResult(intent, SENSITIVE);
                break;

            //验证模式
            case R.id.linear_auth_mode:
                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> devicelist = deviceInfo.getBody().getDevice_user_list();
                int number = 0;
                if (null != devicelist) {
                    for (int i = 0; i < devicelist.size(); i++) {
                        if ("adm".equals(devicelist.get(i).getLevel())) {
                            number++;
                        }
                    }
                }
                intent.putExtra("prama", AUTH);
                String mAuth = tvAuthMode.getText().toString();
                if(mAuth.length()>0){
                    mAuth = mAuth.substring(0,1);
                }
                intent.putExtra("data", mAuth);
                intent.putExtra("number", number);
                startActivityForResult(intent, AUTH);
                break;

            case R.id.linear_arlmTime:
                intent.putExtra("prama", TIME);
                intent.putExtra("data", tvDevArlmTime.getText().toString());
                startActivityForResult(intent, TIME);
                break;

            //重启设备
            case R.id.btn_videolock_restartDevice:
                if(isOnline){
                    if (deviceInfo != null && deviceInfo.getBody().getAuth_mode() != null) {
                        right_menuOrRestartDeviceType = 2;
                        showCustomizeDialog();
                    } else {
                        showToast("获取信息失败!");
                    }
                }else{
                    showToast("设备不在线");
                }

                break;
            //删除设备
            case R.id.btn_dev_delete:
                DeleteDevicesReq body = new DeleteDevicesReq();
                DeleteDevicesReq.DeviceBean bean = new DeleteDevicesReq.DeviceBean();
                bean.setUuid(uuid);
                bean.setVendor_name(FactoryType.GENERAL);
                body.setGateway_vendor_name(FactoryType.FBEE);
                body.setGateway_uuid(AppContext.getGwSnid());
                body.setDevice(bean);
                presenter.reqDeleteGateWay(body);
                break;
        }
    }
    private EditText editText;
    private void modifyDevName(){
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_title);
        title.setText("修改设备名称");
        editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        editText.setText(tvDevName.getText());
        Selection.setSelection(editText.getText(), editText.getText().length());
        TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText() == null) {
                    showToast("请输入设备名称");
                } else {
                    reqAddDevice();
                    //tvDevName.setText(editText.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });
        cancleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        alertDialog = customizeDialog.show();
    }

    private void reqAddDevice() {
        AddDevicesReq addDevicesReq = new AddDevicesReq();
        AddDevicesReq.DeviceBean deviceBean = new AddDevicesReq.DeviceBean();
        addDevicesReq.setGateway_vendor_name(FactoryType.FBEE);
        addDevicesReq.setGateway_uuid(AppContext.getGwSnid());
        deviceBean.setVendor_name(FactoryType.GENERAL);
        deviceBean.setUuid(uuid);
        deviceBean.setShort_id(uuid);
        deviceBean.setType(FactoryType.VIDEO_DOORLOCK_TYPE);
//        deviceBean.setUsername("hkq");
//        deviceBean.setPassword("123456");
//        deviceBean.setRoom_id("111");
//        deviceBean.setRoom_name("卧室");
        deviceBean.setNote(editText.getText().toString().trim());
//        deviceBean.setContext_uuid("test");
        if(version!=null){
            deviceBean.setVersion(version);
            addDevicesReq.setDevice(deviceBean);
            LogUtil.e("服务器发送","添加设备:"+addDevicesReq.toString());
            presenter.reqAddDevices(addDevicesReq);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            //报警数据类型
            case 1001:
                String alarm = data.getStringExtra("resultstr");
                info.setInfrared_alarm_type(alarm);
                if(null != alarm){
                    if(alarm.equals("pic")){
                        tvArlmtype.setText(alarm+"(图片)");
                    }else if(alarm.equals("av")){
                        tvArlmtype.setText(alarm+"(视频)");
                    }
                }

                break;
            //音量等级
            case 1002:
                String level = data.getStringExtra("resultstr");
                switch (level){
                    case "低":
                        info.setVolume_level("1");
                    break;
                    case "中":
                        info.setVolume_level("2");
                        break;
                    case "高":
                        info.setVolume_level("3");
                        break;

                }
                tvVoicelevel.setText(level);
                break;
            //验证模式
            case 1003:
                String auth = data.getStringExtra("resultstr");
                info.setAuth_mode(auth);
                if(null != auth){
                    if("0".equals(auth)){
                        tvAuthMode.setText(auth+"(单人验证)");
                    }else if("1".equals(auth)){
                        tvAuthMode.setText(auth+"(双人验证)");
                    }
                }
                break;
            //智能人体侦测灵敏度
            case 1004:
                String onff = data.getStringExtra("resultstr");
                linearInfraredSense.setText(onff);
                if (onff.equals("低")) {
                    info.setInfrared_sensitivity("30");
                } else if (onff.equals("中")) {
                    info.setInfrared_sensitivity("70");
                } else if (onff.equals("高")) {
                    info.setInfrared_sensitivity("100");
                }
                break;
            //自动报警时间
            case 1005:
                String time = data.getStringExtra("resultstr");
                tvDevArlmTime.setText(time + "秒");
                info.setInfrared_alarm_interval_time(time);
                break;
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

    //查询设备信息返回
    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {
        hideLoadingDialog();
        if (bean == null) return;
        if (bean.getHeader().getHttp_code().equals("200")) {
            deviceInfo = bean;
            if (bean.getBody() != null) {

                info.setInfrared_alarm_interval_time(bean.getBody().getInfrared_alarm_interval_time());
                info.setActivation_code(bean.getBody().getActivation_code());
                info.setAuth_mode(bean.getBody().getAuth_mode());
                info.setCmd("DEVICE_CONFIG");
                info.setInfrared_alarm_type(bean.getBody().getInfrared_alarm_type());
                info.setInfrared_sensitivity(bean.getBody().getInfrared_sensitivity());
                info.setOn_off(bean.getBody().getOn_off());
                info.setVolume_level(bean.getBody().getVolume_level());

                if (bean.getBody().getInfrared_alarm_interval_time() != null) {
                    //红外报警时间
                    infrared_alarm_interval_time = bean.getBody().getInfrared_alarm_interval_time();
                    int interval_time = Integer.parseInt(infrared_alarm_interval_time);
                    if (interval_time >= 0 && interval_time <= 60) {
                        tvDevArlmTime.setText("60秒");
                    } else if (interval_time > 60 && interval_time <= 90) {
                        tvDevArlmTime.setText("90秒");
                    } else if (interval_time > 90) {
                        tvDevArlmTime.setText("120秒");
                    }

                }
                if (bean.getBody().getInfrared_alarm_type() != null) {
                    switch (bean.getBody().getInfrared_alarm_type()) {
//                        case "txt":
//                            //报警数据类型
//                            tvArlmtype.setText("txt" + "(文本)");
//                            break;
                        case "pic":
                            //报警数据类型
                            tvArlmtype.setText("pic" + "(图片)");
                            break;
                        case "av":
                            //报警数据类型
                            tvArlmtype.setText("av" + "(视频)");
                            break;
                    }
                }
                if (bean.getBody().getVolume_level() != null) {
                    //音量等级
                    switch (bean.getBody().getVolume_level()){
                        case "1":
                            tvVoicelevel.setText("低");
                        break;
                        case "2":
                            tvVoicelevel.setText("中");
                            break;
                        case "3":
                            tvVoicelevel.setText("高");
                            break;
                    }
                }
                if (bean.getBody().getInfrared_sensitivity() != null) {
                    //红外灵敏度
                    infrared_sensitivity = bean.getBody().getInfrared_sensitivity();
                    int sensitivity = Integer.parseInt(infrared_sensitivity);
                    if (0 <= sensitivity && sensitivity <= 30) {
                        linearInfraredSense.setText("低");
                    } else if (30 < sensitivity && sensitivity <= 70) {
                        linearInfraredSense.setText("中");
                    } else if (sensitivity <= 100) {
                        linearInfraredSense.setText("高");
                    }
                }

                if (bean.getBody().getAuth_mode() != null) {
                    if (bean.getBody().getAuth_mode().equals("0")) {
                        //验证模式
                        tvAuthMode.setText("0 (单人验证)");
                    } else {
                        tvAuthMode.setText("1 (双人验证)");
                    }
                }
                if (bean.getBody().getOn_off() != null) {
                    //功能开关
                    String On_Off=bean.getBody().getOn_off();
                    String[] onOffList=On_Off.split(",");
                    initOnOff(onOffList);
                }
                version = bean.getBody().getVersion();
                if(version !=null){
                    //门锁版本信息
                    tvVersion.setText(version);
                }

                if(bean.getBody().getMac_addr()!=null){
                    //门锁mac地址
                    tvDevMac.setText(bean.getBody().getMac_addr());
                }else{
                    tvDevMac.setText(uuid);
                }

                if(bean.getBody().getNetwork_ssid()!=null){
                    //门锁wifi ssid
                    tvDevSsid.setText(bean.getBody().getNetwork_ssid());
                }
                if(bean.getBody().getDev_id()!=null){
                    //门锁串码信息
                    tvStrcodeInfo.setText(bean.getBody().getDev_id());
                }
                if(bean.getBody().getDev_model()!=null){
                    //设备型号
                    tvDevType.setText(bean.getBody().getDev_model());
                }


            }
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    /**
     * 初始化功能开关
     * @param onOffList
     */
    private void initOnOff(String[] onOffList) {
        if ("0".equals(onOffList[0])) {
            //智能人体侦测报警
            checkDevHumanDetectionArlm.setChecked(false);
        } else if ("1".equals(onOffList[0])) {
            checkDevHumanDetectionArlm.setChecked(true);
        }

        if ("0".equals(onOffList[1])) {
            //开启门锁常开  是个button
            OnOffMap.put(1, 0);
        } else if ("1".equals(onOffList[1])) {
            OnOffMap.put(1, 1);
        }

        if ("0".equals(onOffList[2])) {
            //门未关报警
            checkDevNotCloseArlm.setChecked(false);
        } else if ("1".equals(onOffList[2])) {
            checkDevNotCloseArlm.setChecked(true);
        }

        if ("0".equals(onOffList[3])) {
            //假锁报警
            checkDevFalseArlm.setChecked(false);
        } else if ("1".equals(onOffList[3])) {
            checkDevFalseArlm.setChecked(true);
        }

        if ("0".equals(onOffList[4])) {
            //门锁密码验证功能
            checkPassValidate.setChecked(false);
        } else if ("1".equals(onOffList[4])) {
            checkPassValidate.setChecked(true);
        }

        if ("0".equals(onOffList[5])) {
            //智能人体侦测
            checkDevHumanDetection.setChecked(false);
        } else if ("1".equals(onOffList[5])) {
            checkDevHumanDetection.setChecked(true);
        }

        if ("0".equals(onOffList[6])) {
            //门锁常联网模式
            checkNetworkModle.setChecked(false);
        } else if ("1".equals(onOffList[6])) {
            checkNetworkModle.setChecked(true);
        }

        if ("0".equals(onOffList[7])) {
            //远程控制  仅展示用
            tvDevTelecontrol.setText("已关闭");
            OnOffMap.put(7, 0);
        } else if ("1".equals(onOffList[7])) {
            tvDevTelecontrol.setText("已开启");
            OnOffMap.put(7, 1);
        }
    }

    /**
     * 获取功能开关最后结果
     * @return
     */
    private String getOnOffResult(){
        StringBuffer str=new StringBuffer();
        for (int i = 0; i <8 ; i++) {
            if(i<7){
                str.append(OnOffMap.get(i)+",");
            }else{
                str.append(OnOffMap.get(i));
            }
        }
        LogUtil.e("功能开关结果",str.toString());
        return str.toString();
    }
    @Override
    public void resAddDevice(BaseResponse bean) {
        if (bean.getHeader().getHttp_code().equals("200")) {
            tvDevName.setText(editText.getText().toString());
            RxBus.getInstance().post(new UpdateDoorLockName(uuid,tvDevName.getText().toString()));
            showToast("修改成功!");
        }
    }

    @Override
    public void resDeleteGateWay(BaseResponse bean) {
        if (bean.getHeader().getHttp_code().equals("200")) {
            showToast("删除成功");
            RxBus.getInstance().post(new VideolockDelEvent(uuid));
            skipAct(MainActivity.class);
            finish();
        }
    }

    /**
     * 请求token
     */
    private void reqAddToken(){
        AddTokenReq addTokenReq=new AddTokenReq();
        addTokenReq.setAttitude("read");
        addTokenReq.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        addTokenReq.setSecret_key(AppContext.getInstance().getBodyBean().getSecret_key());
        presenter.reqAddToken(addTokenReq);
    }

    /**
     * token返回
     * @param bean
     */
    @Override
    public void resAddToken(AddTokenResponse bean) {
        LogUtil.e("token",""+bean.getBody().getToken());
        if ("200".equals(bean.getHeader().getHttp_code())){
            if(bean.getBody()!=null&&bean.getBody().getToken()!=null){
                LogUtil.e("token",""+bean.getBody().getToken());
                AppContext.setToken(bean.getBody().getToken());
                switch (needAddToken){
                    //参数设置
                    case 1:
                        needAddToken=0;
                        userTransportReq(aespasStr);
                        break;
                    //重启设备
                    case 2:
                        needAddToken=0;
                        userRestartReq(aespasStr);
                        break;
                    //检查设备状态
                    case 3:
                        needAddToken=0;
                        checkDeviceState();
                        break;
                }
            }
        }else{
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }


    private void showCustomizeDialog(){
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_input_password,null);
        TextView tvTitleWifidialog = (TextView) dialogView.findViewById(R.id.tv_title_wifidialog);
        tvTitleWifidialog.setText("输入密码");
        final EditText editText= (EditText) dialogView.findViewById(R.id.et_first);
        final EditText etSecond = (EditText)dialogView.findViewById(R.id.et_second);
        if (deviceInfo.getBody().getAuth_mode().equals("0")) {
            etSecond.setVisibility(View.GONE);
        } else if(deviceInfo.getBody().getAuth_mode().equals("1")){
            etSecond.setVisibility(View.VISIBLE);
        }
        TextView tvLeftCancelBtnWifidialog = (TextView)dialogView.findViewById(R.id.tv_left_cancel_btn_wifidialog);
        TextView tvRightConfirmBtnWifidialog = (TextView)dialogView.findViewById(R.id.tv_right_confirm_btn_wifidialog);
        //确定
        tvRightConfirmBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password= editText.getText().toString().trim();
                //单人
                if(deviceInfo.getBody().getAuth_mode().equals("0")){
                    LogUtil.e("加密", "输入密码str：" + password);
                    if (password.length() == 6) {
                        String resultPass = ByteStringUtil.myStringToString(password);
                        commonEncrypt(resultPass);
                        // //-----------------------------------------------华丽分割线------5.7
                    } else if (TextUtils.isEmpty(password)) {
                        ToastUtils.showShort("密码不能为空!");
                    } else if (password.length() < 6) {
                        ToastUtils.showShort("请输入6位密码!");
                    }
                }
                //双人
                else if(deviceInfo.getBody().getAuth_mode().equals("1")){
                    String secondPass= etSecond.getText().toString().trim();
                    if(password.length()==6&&secondPass.length()==6){
                        String passStr = ByteStringUtil.myStringToString(password);
                        String secpassStr = ByteStringUtil.myStringToString(secondPass);
                        String resultPass = passStr + "2c" + secpassStr;
                        commonEncrypt(resultPass);
                    }else if(TextUtils.isEmpty(password)||TextUtils.isEmpty(secondPass)){
                        ToastUtils.showShort("密码不能为空!");
                    }else if(password.length() < 6||secondPass.length()<6){
                        ToastUtils.showShort("请输入6位密码!");
                    }
                }
            }
        });
        //取消
        tvLeftCancelBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        alertDialog=customizeDialog.show();
    }

    private String aespasStr;
    private void commonEncrypt(String resultPass){
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
                            if (right_menuOrRestartDeviceType == 1) {
                                showToast("开锁失败,请重试!");
                            } else if (right_menuOrRestartDeviceType == 2) {
                                showToast("远程重启失败,请重试!");
                            }
                        }
                    });
                    return;
                }
                if (right_menuOrRestartDeviceType == 1) {
                    userTransportReq(aespasStr);
                } else if (right_menuOrRestartDeviceType == 2) {
                    userRestartReq(aespasStr);
                }
            }
        }).start();
        alertDialog.dismiss();
    }

    private byte[] aes256VideoDoorKey() {
        byte[] aes = null;
        if (deviceInfo != null && deviceInfo.getBody() != null && deviceInfo.getBody().getNetwork_ssid() != null ) {

            String lockSSID = deviceInfo.getBody().getNetwork_ssid();
//            String version = deviceInfo.getBody().getVersion();
            // String lockSSID="jija-yanfa";
            // String version="1.0.0-1.0.0";
            String content = uuid + lockSSID;
            if(content.length() <32){
                for (int i = content.length(); i <32 ; i++) {
                    content =content+"0";
                }
            }
            byte[] contentByte;
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
        long time =  System.currentTimeMillis() / 1000;
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



    private void requestUpdate() {
        showLoadingDialog(null);
        Subscriber subscriber = new Subscriber<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                hideLoadingDialog();
            }

            @Override
            public void onNext(JsonObject json) {
                try {
                    if (null != json) {
                        hideLoadingDialog();
                        JsonObject jsonObj = json.getAsJsonObject("PUS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        UpdateVideoLockResponse updateinfo = new Gson().fromJson(jsonObj.toString(), UpdateVideoLockResponse.class);
                        String responseCode = updateinfo.getHeader().getHttp_code();
                        if ("200".equals(responseCode)) {
                            String newVersion = updateinfo.getBody().getNew_version();
                            String[] newVersions = newVersion.split(",");
                            if(newVersions.length<3){
                                return;
                            }
                            String[] md5s = updateinfo.getBody().getMd5().split(",");


                            String mcu=newVersions[0];
                            String wifi = newVersions[1];
                            String video = newVersions[2];
                            String[] nowversions = version.split(",");
                            if(nowversions.length<3){
                                return;
                            }
                            String[] urls = updateinfo.getBody().getUrl().split(",");

                            StringBuffer sb = new StringBuffer();
                            StringBuffer urlbuffer = new StringBuffer();
                            StringBuffer partbuffer = new StringBuffer();
                            StringBuffer md5buffer = new StringBuffer();
                            if(mcu.compareTo(nowversions[0])>0){
                                partbuffer.append("mcu,");
                                urlbuffer.append(urls[0]+",");
                                md5buffer.append(md5s[0]+",");
                                sb.append("“门锁”");
                            }
                            if(wifi.compareTo(nowversions[1])>0){
                                partbuffer.append("wifi,");
                                urlbuffer.append(urls[1]+",");
                                md5buffer.append(md5s[1]+",");
                                sb.append("“WIFI”");
                            }
                            if(video.compareTo(nowversions[2])>0){
                                partbuffer.append("video,");
                                urlbuffer.append(urls[2]+",");
                                md5buffer.append(md5s[2]+",");
                                sb.append("“视频”");
                            }


                            if(partbuffer.length()>0){
                                showUpdateDialog(partbuffer.substring(0,partbuffer.length()-1),
                                        urlbuffer.substring(0,urlbuffer.length()-1),
                                        md5buffer.substring(0,md5buffer.length()-1),
                                        sb.toString()
                                        );
                            }else{
                                showToast("已是最新版本");
                            }

                        } else {
                            showToast(updateinfo.getHeader().getReturn_string());
                        }

                    }
                } catch (Exception e) {

                }
            }
        };

        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        header.setSeq_id("2");
        Pus pus = new Pus();
        PusBodyBean bodyBean = new PusBodyBean();
        bodyBean.setToken(AppContext.getToken());
        bodyBean.setProduct_name("general");
        bodyBean.setPlatform("device");
        bodyBean.setEndpoint_type(FactoryType.Z501);
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyBean);
        pus.setPUS(umsbean);

        Subscription subscription = mApiWrapper.updateVersion(pus).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    /**
     * 提示更新
     */
    private void  showUpdateDialog(final String parts,final String urls, final String md5,final String tilte){
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("有新版本("+tilte+")是否更新？").cancelable(false).title("更新提示")
                .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("更新");
        DialogManager.showDialog(VideoLockSettingActivity.this, builder, new DialogManager.ConfirmDialogListener() {

            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                userUpdateReq(parts,urls,md5);
            }
        });
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket!=null){
            socket.off("MSG_USER_TRANSPORT_RSP", userTransportRsp);
            socket.off("MSG_DEVICE_TRANSPORT_REQ",deviceTransportRsp);
            socket.off("MSG_USER_AUTH_RSP", userConfirmation);
            socket.off("MSG_USER_DEVICE_STATUS_QUERY_RSP", checkDeviceState);
        }
    }
}
