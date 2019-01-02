package com.fbee.smarthome_wl.ui.equesdevice.videocall;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.AnyChatVideoCallEvent;
import com.bairuitech.anychat.config.ConfigHelper;
import com.bairuitech.anychat.config.VideoCallContrlHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.event.EquesAlarmDialogEvent;
import com.fbee.smarthome_wl.event.EquesVideoCallEvent;
import com.fbee.smarthome_wl.event.JiuCaiAlarmImage;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import rx.functions.Action1;

public class EquesVideoCallActivity extends BaseActivity implements AnyChatVideoCallEvent, VideoCallContrlHandler {

    private LinearLayout hangUp;
    private String sid;
    private String from;
    private LinearLayout videoCall;
    private ImageView headOrtrait;
    private JSONObject jpushData;
    private TextView equesName;
    private EquesListInfo.bdylistEntity bdylistEntity;
    private MediaPlayer mediaplayer;
    private ResAnychatLogin.UserBean.UserDevicesBean deviceBean;
    private int dwUserId;
    private String path;
    private AnyChatCoreSDK anychat;
    private int eventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_video_call);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setAnyChat();
    }

    @Override
    protected void initView() {
        hangUp = (LinearLayout) findViewById(R.id.hang_up);
        videoCall = (LinearLayout) findViewById(R.id.video_call);
        headOrtrait = (ImageView) findViewById(R.id.head_ortrait);
        equesName = (TextView) findViewById(R.id.tv_eques_name);
    }

    protected void setAnyChat() {
        try {
            ConfigHelper.getConfigHelper().ApplyVideoConfig(this);
            anychat = AnyChatCoreSDK.getInstance();
            anychat.SetBaseEvent(anyChatBaseEvent);
            anychat.SetVideoCallEvent(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initData() {
        mediaplayer = MediaPlayer.create(this, R.raw.alarm);
        mediaplayer.setLooping(true);
        mediaplayer.start();
//        if (devicesBean != null) {
//            it.putExtra("deviceBean", devicesBean);
//        }
//        it.putExtra("id", dwUserid);
//        it.putExtra("path", TempFilePath);
        deviceBean = (ResAnychatLogin.UserBean.UserDevicesBean) getIntent().getExtras().get("deviceBean");
        if (deviceBean != null) {
            dwUserId = getIntent().getExtras().getInt("id");
        }
        sid = getIntent().getExtras().getString(Method.ATTR_CALL_SID);
        from = getIntent().getExtras().getString(Method.ATTR_FROM);
        String name = getIntent().getExtras().getString("name");
        String json = getIntent().getExtras().getString("json");
        List<EquesListInfo.bdylistEntity> bdylist = (List<EquesListInfo.bdylistEntity>) PreferencesUtils.getObject(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME) + "JpushArlam");
        for (int i = 0; i < bdylist.size(); i++) {
            bdylistEntity = bdylist.get(i);
            if (bdylistEntity.getBid().equals(from)) {
                if (bdylistEntity.getNick() != null) {
                    equesName.setText(bdylist.get(i).getNick() + "来电");
                } else {
                    equesName.setText(bdylist.get(i).getName() + "来电");
                }
            }
        }
        try {
            if (from == null && json != null) {
                jpushData = new JSONObject(json);
                from = jpushData.optString("bid");
                name = jpushData.optString("name");
                for (int i = 0; i < bdylist.size(); i++) {
                    bdylistEntity = bdylist.get(i);
                    if (bdylistEntity.getBid().equals(from)) {
                        if (bdylistEntity.getNick() != null) {
                            equesName.setText(bdylist.get(i).getNick() + "来电");
                        } else {
                            equesName.setText(bdylist.get(i).getName() + "来电");
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (name != null) {
            icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, name, EquesConfig.APPKEY);
        }
        hangUp.setOnClickListener(this);
        videoCall.setOnClickListener(this);

        mSubscription = RxBus.getInstance().toObservable(EquesVideoCallEvent.class)
                .compose(TransformUtils.<EquesVideoCallEvent>defaultSchedulers())
                .subscribe(new Action1<EquesVideoCallEvent>() {
                    @Override
                    public void call(EquesVideoCallEvent event) {
                        String fid = event.getFid();
                        URL url = icvss.equesGetRingPicture(fid, from);
                        ImageLoader.load(EquesVideoCallActivity.this, Uri.parse(url.toString()), headOrtrait);
                    }
                });
        mSubscription = RxBus.getInstance().toObservable(JiuCaiAlarmImage.class)
                .compose(TransformUtils.<JiuCaiAlarmImage>defaultSchedulers())
                .subscribe(new Action1<JiuCaiAlarmImage>() {
            @Override
            public void call(JiuCaiAlarmImage jiuCaiAlarmImage) {
                ImageLoader.load(EquesVideoCallActivity.this, jiuCaiAlarmImage.tempFilePath, headOrtrait);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_up:
                icvss.equesCloseCall(sid);
                mediaplayer.pause();
                mSubscription.unsubscribe();
                finish();
                break;
            case R.id.video_call:

                if (deviceBean != null) {
                    comeToVideoActivity();
                } else {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(Method.ATTR_BUDDY_UID, from);
                    skipAct(EquesCallActivity.class, bundle1);
                }
                mediaplayer.pause();
                finish();
                break;
        }
    }

    public void comeToVideoActivity() {
        try {
            if (eventType != 0 && eventType != AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH) {
                //发起视频通话后未结束，先挂断在请求。
                anychat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH, Integer.valueOf(deviceBean.getDevice_anychat_id()), 0, 0, 0, "");

            }
            int result = anychat.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST, dwUserId, 0, 0,
                    0, "");

            LogUtil.e("result", result + "");
            //   showLoadingDialog("正在加载视频...");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        finish();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mediaplayer != null) {
                mediaplayer.stop();
                mediaplayer.release();//释放资源
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId, int dwErrorCode, int dwFlags, int dwParam, String userStr) {
        LogUtil.e("OnAnyChatVideoCallEvent", "+++++++++++++");

        try {
            eventType = dwEventType;
            switch (dwEventType) {
                case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST:// < 呼叫请求
                    VideoCall_SessionRequest(dwUserId, dwFlags,
                            dwParam, userStr);
                    break;
                case AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY:// < 呼叫请求回复 开始向设备端发送视频请求
                    VideoCall_SessionReply(dwUserId, dwErrorCode, dwFlags, dwParam, userStr);
                    break;
                //视频
                case AnyChatDefine.BRAC_VIDEOCALL_EVENT_START:// 视频呼叫会话开始事件
                    LogUtil.e("1111", "OnAnyChatVideoCallEvent");
                    VideoCall_SessionStart(dwUserId,
                            dwFlags, dwParam, userStr);
                    Log.i("INFO", "dwUserId+dwFlags+dwParam+userStr:" + dwUserId + "***" + dwFlags + "****" + dwParam + "*****" + userStr);
                    break;
                case AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH:// < 挂断（结束）呼叫会话
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

    }

    @Override
    public void VideoCall_SessionStart(int dwUserId, int dwFlags, int dwParam, String szUserStr) {
        LogUtil.e("1111", "VideoCall_SessionStart");
        // hideLoadingDialog();
        Intent intent = new Intent();
        intent.setClass(this, JiucaiVideoActivity.class);
        intent.putExtra("DeviceVer", deviceBean.getVersion());
        intent.putExtra("devicesNum", deviceBean.getDevice_name());
        intent.putExtra("dwUserId", dwUserId);
        intent.putExtra("room", dwParam);
        intent.putExtra("alias", deviceBean.getAlias());
        startActivity(intent);
    }


    @Override
    public void VideoCall_SessionEnd(int dwUserId, int dwFlags, int dwParam, String szUserStr) {

    }
}
