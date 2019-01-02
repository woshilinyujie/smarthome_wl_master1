package com.fbee.smarthome_wl.ui.equesdevice;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatTransDataEvent;
import com.bairuitech.anychat.AnyChatUserInfoEvent;
import com.bairuitech.anychat.AnyChatVideoCallEvent;
import com.bairuitech.anychat.config.BaseConst;
import com.bairuitech.anychat.config.ConfigEntity;
import com.bairuitech.anychat.config.ConfigHelper;
import com.bairuitech.anychat.config.SessionItem;
import com.bairuitech.anychat.config.VideoCallContrlHandler;
import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.anychatutils.ControlCenter;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.JiucaiNetPicMessageActivity;
import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.JiucaiPicMessageActivity;
import com.fbee.smarthome_wl.ui.equesdevice.equesaddlock.EquesAddlockActivity;
import com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.JiucaiLocalVideoActivity;
import com.fbee.smarthome_wl.ui.equesdevice.settinglist.JiucaiSettingListActivity;
import com.fbee.smarthome_wl.ui.equesdevice.videocall.JiucaiVideoActivity;
import com.fbee.smarthome_wl.ui.equesdevice.visitorlist.JiucaiVisitorPicActivity;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;

import java.util.Random;
import java.util.Scanner;

import static com.fbee.smarthome_wl.anychatutils.ControlCenter.anychat;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;


/**
 * Created by jija on 2017/1/4.
 */
public class JiucaiDevicesInfoActivity extends BaseActivity implements AnyChatVideoCallEvent, VideoCallContrlHandler, AnyChatUserInfoEvent {

    private TextView dev_name;
    private ImageView VideoView;
    private ImageView Preferences;
    private ImageView videoRecording;
    private TextView state;
    private ImageView PictureMessage;
    //电量
    private ImageView disturb;
    //    private Button btn_back;
//    private Button Setting;
    private int jiucaiId;
    private int deviceStatus;
    private ResAnychatLogin.UserBean.UserDevicesBean userDevicesBean;
    private SessionItem sessionItem;
    private int eventType;
    private ConfigEntity configEntity;
    private String sdCardPath;
    private LoginResult.BodyBean.GatewayListBean gw;
    private ImageView gateLock;
    private TextView rightMenu;
    private ImageView ivAlarmMessage;
    private ImageView visitor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiucai_deviceinfo);
    }

    protected void startBackServce() {
        Intent intent = new Intent();
        intent.setAction(BaseConst.ACTION_BACKSERVICE);
        intent.setPackage(getPackageName());//这里你需要设置你应用的包名（5.0开始需要显式调用）
        this.startService(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ConfigHelper.getConfigHelper().ApplyVideoConfig(this);
        anychat.SetBaseEvent(anyChatBaseEvent);
        anychat.SetVideoCallEvent(this);
        anychat.SetUserInfoEvent(this);
        ControlCenter.getControlCenter().initFriendDatas(2);
    }

    @Override
    protected void initData() {
        sdCardPath = Api.getCamPath() + "/JiuCai";
        jiucaiId = userDevicesBean.getDevice_anychat_id();
        deviceStatus = anychat.GetFriendStatus(jiucaiId);
        LogUtil.e("九彩状态", deviceStatus + "");
        if (deviceStatus == 1) {
            state.setText("在线");
        }
        if (deviceStatus == 0) {
            state.setText("离线");
        }
        VideoView.setOnClickListener(this);
        Preferences.setOnClickListener(this);
        videoRecording.setOnClickListener(this);
        PictureMessage.setOnClickListener(this);
        disturb.setOnClickListener(this);
        gateLock.setOnClickListener(this);
        rightMenu.setOnClickListener(this);
        ivAlarmMessage.setOnClickListener(this);
        visitor.setOnClickListener(this);
//        Setting.setOnClickListener(this);
//        btn_back.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        initApi();
        userDevicesBean = (ResAnychatLogin.UserBean.UserDevicesBean) getIntent().getExtras().getSerializable("UserDevicesBean");
        dev_name = (TextView) findViewById(R.id.title);
        dev_name.setText(userDevicesBean.getAlias());
        VideoView = (ImageView) findViewById(R.id.video_view);
        TextView videoRecord = (TextView) findViewById(R.id.tv_ring);
        TextView PictureMsg = (TextView) findViewById(R.id.tv_captured);
        videoRecord.setText("视频");
        PictureMsg.setText("抓拍");
        gateLock = (ImageView) findViewById(R.id.btn_gate_lock);
        rightMenu = (TextView) findViewById(R.id.tv_right_menu);
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText("设备图片");
        Preferences = (ImageView) findViewById(R.id.btn_configure);
        videoRecording = (ImageView) findViewById(R.id.btn_close_ring);
        state = (TextView) findViewById(R.id.dev_state);
        PictureMessage = (ImageView) findViewById(R.id.iv_captured);
        disturb = (ImageView) findViewById(R.id.iv_batteryImage_1);
        ivAlarmMessage = (ImageView) findViewById(R.id.iv_alarm_message);
        visitor = (ImageView) findViewById(R.id.iv_visitor);
        gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        setAnyChat();
    }

    private void setAnyChat() {
        ConfigHelper.getConfigHelper().ApplyVideoConfig(this);
        anychat.SetBaseEvent(anyChatBaseEvent);
        anychat.SetTransDataEvent(this);
        anychat.SetUserInfoEvent(this);
        anychat.Connect("ihomecn.rollupcn.com", 8906);
        anychat.Login(gw.getUsername(), "123456");
        anychat.InitSDK(android.os.Build.VERSION.SDK_INT, 0);

        configEntity = ConfigHelper.getConfigHelper().LoadConfig(this);
        ControlCenter.getControlCenter();

        ConfigHelper.getConfigHelper().ApplyVideoConfig(this);
        startBackServce();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_view:
                //打开视频页面
                comeToVideoActivity();
                break;
            case R.id.btn_close_ring:
                //视频录制
                Intent intent2 = new Intent(JiucaiDevicesInfoActivity.this, JiucaiLocalVideoActivity.class);
                intent2.putExtra("path", sdCardPath + userDevicesBean.getDevice_name());
                intent2.putExtra("bool", true);
                LogUtil.e("跳转", "开始");
                startActivity(intent2);
                break;
            //绑定门锁
            case R.id.btn_gate_lock:
                Bundle bundle5 = new Bundle();
                bundle5.putString(Method.ATTR_BUDDY_BID, userDevicesBean.getDevice_name());
                skipAct(EquesAddlockActivity.class, bundle5);
                break;
            case R.id.iv_captured:
                //抓拍信息
                Intent intent = new Intent(this, JiucaiPicMessageActivity.class);
                intent.putExtra("path", sdCardPath + userDevicesBean.getDevice_name());
                startActivity(intent);
                break;
            case R.id.btn_configure:
                //参数设置
                //设置
                Intent intent1 = new Intent(this, JiucaiSettingListActivity.class);
                intent1.putExtra("UserDevicesBean", userDevicesBean);
                startActivity(intent1);
                break;
            case R.id.tv_right_menu:
                //报警图片
                Intent intent3 = new Intent(this, JiucaiNetPicMessageActivity.class);
                intent3.putExtra("UserDevicesBean", userDevicesBean.getDevice_anychat_id());
                startActivity(intent3);
                break;
            case R.id.iv_alarm_message:

                break;
            case R.id.iv_visitor:
                Intent intent4 = new Intent(this, JiucaiVisitorPicActivity.class);
                startActivity(intent4);
                break;
        }
    }

    public void comeToVideoActivity() {
        try {
            if (deviceStatus == 1) {
                if (eventType != 0 && eventType != AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH) {
                    //发起视频通话后未结束，先挂断在请求。
                    anychat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH, jiucaiId, 0, 0, 0, "");

                }
                int result = anychat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST, jiucaiId, 0, 0,
                        0, "");

                LogUtil.e("result", result + "");
                showLoadingDialog("正在加载视频...");
            } else {
                Toast.makeText(this, getString(R.string.device_offline), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId, int dwErrorCode, int dwFlags, int dwParam, String userStr) {
        try {
            LogUtil.e("视频", "dwEventType:" + dwEventType + "dwUserId" + dwUserId + "dwErrorCode:" + dwErrorCode + "dwFlags:" + dwFlags + "dwParam:" + dwParam + "userStr:" + userStr);
            eventType = dwEventType;
            switch (dwEventType) {
                case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST:// < 呼叫请求
                    LogUtil.e("视频", "呼叫请求");
                    VideoCall_SessionRequest(dwUserId, dwFlags,
                            dwParam, userStr);
                    break;
                case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY:// < 呼叫请求回复 开始向设备端发送视频请求
                    LogUtil.e("视频 开始向设备端发送视频请求", "呼叫请求回复 开始向设备端发送视频请求");
                    VideoCall_SessionReply(dwUserId, dwErrorCode, dwFlags, dwParam, userStr);
                    break;
                //视频
                case AnyChatDefine.BRAC_VIDEOCALL_EVENT_START:// 视频呼叫会话开始事件
                    LogUtil.e("视频", "视频呼叫会话开始事件");
                    VideoCall_SessionStart(dwUserId,
                            dwFlags, dwParam, userStr);
                    break;
                case AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH:// < 挂断（结束）呼叫会话
                    LogUtil.e("视频", "挂断（结束）呼叫会话");
                    VideoCall_SessionEnd(dwUserId, dwFlags, dwParam, userStr);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void VideoCall_SessionRequest(int dwUserId, int dwFlags, int dwParam, String szUserStr) {
    }

    @Override
    public void VideoCall_SessionReply(int dwUserId, int dwErrorCode, int dwFlags, int dwParam, String szUserStr) {
        String strMessage = null;
        switch (dwErrorCode) {
            case VideoCallContrlHandler.ERRORCODE_SESSION_BUSY:
                strMessage = getString(com.bairuitech.anychat.R.string.str_returncode_bussiness);
                break;
            case VideoCallContrlHandler.ERRORCODE_SESSION_REFUSE:
                strMessage = getString(com.bairuitech.anychat.R.string.str_returncode_requestrefuse);
                break;
            case VideoCallContrlHandler.ERRORCODE_SESSION_OFFLINE:
                strMessage = getString(com.bairuitech.anychat.R.string.str_returncode_offline);
                break;
            case VideoCallContrlHandler.ERRORCODE_SESSION_QUIT:
                strMessage = getString(com.bairuitech.anychat.R.string.str_returncode_requestcancel);
                break;
            case VideoCallContrlHandler.ERRORCODE_SESSION_TIMEOUT:
                strMessage = getString(com.bairuitech.anychat.R.string.str_returncode_timeout);
                break;
            case VideoCallContrlHandler.ERRORCODE_SESSION_DISCONNECT:
                strMessage = getString(com.bairuitech.anychat.R.string.str_returncode_disconnect);
                break;
            case VideoCallContrlHandler.ERRORCODE_SUCCESS:
                break;
            default:
                break;
        }
        if (strMessage != null) {
            showToast(strMessage);
        }
    }

    @Override
    public void VideoCall_SessionStart(int dwUserId, int dwFlags, int dwParam, String szUserStr) {
        LogUtil.e("1111", "VideoCall_SessionStart");
        // sessionItem = new SessionItem(dwFlags, 0, dwUserId);
        // sessionItem.setRoomId(dwParam);
        hideLoadingDialog();
        Intent intent = new Intent(this, JiucaiVideoActivity.class);
        intent.putExtra("DeviceVer", userDevicesBean.getVersion());
        intent.putExtra("devicesNum", userDevicesBean.getDevice_name());
        intent.putExtra("dwUserId", dwUserId);
        intent.putExtra("room", dwParam);
        intent.putExtra("alias", userDevicesBean.getAlias());
        intent.putExtra("path", sdCardPath + userDevicesBean.getDevice_name());
        startActivity(intent);
    }

    @Override
    public void VideoCall_SessionEnd(int dwUserId, int dwFlags, int dwParam, String szUserStr) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventType != 0 && eventType != AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH) {
            //发起视频通话后未结束，先挂断在请求。
            anychat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH, jiucaiId, 0, 0, 0, "");
        }
//        releaseAnyChat();
    }

    /**
     * 释放anychat资源
     */
    protected void releaseAnyChat() {
        try {
            Intent intent = new Intent();
            intent.setAction(BaseConst.ACTION_BACKSERVICE);
            intent.setPackage(getPackageName());
            stopService(intent);
            anychat.Logout();
            anychat.Release();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {

    }

    @Override
    public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {
        if (userDevicesBean.getDevice_anychat_id() == dwUserId) {
            LogUtil.e("九彩状态", dwStatus + "++" + dwUserId);
            deviceStatus = dwStatus;
            if (dwStatus == 1) {
                state.setText("在线");
            }
            if (dwStatus == 0) {
                state.setText("离线");
            }
        }
    }

    @Override
    public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {

    }
}
