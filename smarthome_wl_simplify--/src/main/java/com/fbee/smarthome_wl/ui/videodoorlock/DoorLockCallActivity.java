package com.fbee.smarthome_wl.ui.videodoorlock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.request.videolockreq.DeviceStopPushflowRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserAuthRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserInitiateIntercomRequest;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.response.videolockres.MnsBaseResponse;
import com.fbee.smarthome_wl.ui.videodoorlock.setting.VideoLockSettingActivity;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Subscription;
import rx.functions.Action1;

public class DoorLockCallActivity extends BaseActivity<DoorLockVideoCallContract.Presenter> implements DoorLockVideoCallContract.View {
    private TextView tvEquesName;
    private LinearLayout hangUp;
    private LinearLayout videoCall;
    private LinearLayout videoSetting;
    private io.socket.client.Socket socket;
    private String uuid;
    private int tag = 0;
    private String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_lock_call);
    }

    @Override
    protected void initView() {
        tvEquesName = (TextView) findViewById(R.id.tv_eques_name);
        hangUp = (LinearLayout) findViewById(R.id.hang_up);
        videoCall = (LinearLayout) findViewById(R.id.video_call);
        videoSetting = (LinearLayout) findViewById(R.id.video_setting);
    }

    @Override
    protected void initData() {
        tvEquesName.setText("视频锁");
        uuid = getIntent().getStringExtra("deviceUuid");
        if (uuid == null) return;
        createPresenter(new DoorLockVideoCallPresenter(this));
        showLoadingDialog("正在获取信息,请稍后...");
        reqQueryDevice();
        hangUp.setOnClickListener(this);
        videoCall.setOnClickListener(this);
        videoSetting.setOnClickListener(this);
        try {
            socket = BaseApplication.getInstance().getSocket();
            socket.on("MSG_USER_INITIATE_INTERCOM_RSP", startAndEndSpeaking);
            socket.on("MSG_USER_AUTH_RSP", userConfirmation);
        } catch (Exception e) {

        }
        receiveDeviceStopPushflow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_up:
                userStopSpeaking();
                break;
            case R.id.video_call:
                userStartSpeaking();
                break;
            case R.id.video_setting:
                if (note != null) {
                    Intent intent = new Intent(this, VideoLockSettingActivity.class);
                    intent.putExtra("deviceUuid", uuid);
                    intent.putExtra("note", note);
                    startActivity(intent);
                } else {
                    showToast("数据获取失败!");
                }

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

    private Subscription DeviceEndPushflowsubscription;

    /**
     * 接收设备停止推流
     */
    private void receiveDeviceStopPushflow() {
        DeviceEndPushflowsubscription = RxBus.getInstance().toObservable(DeviceStopPushflowRequest.class).compose(TransformUtils.<DeviceStopPushflowRequest>defaultSchedulers()).subscribe(new Action1<DeviceStopPushflowRequest>() {
            @Override
            public void call(DeviceStopPushflowRequest response) {
                Log.e("接收设备", "设备停止推流");
                Toast.makeText(DoorLockCallActivity.this, "设备停止通话", Toast.LENGTH_SHORT);
                finish();
            }
        });
    }

    /**
     * 用户发起对讲
     */
    private void userStartSpeaking() {
        tag = 0;
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
                    tag = 1;
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 用户挂断对讲
     */
    private void userStopSpeaking() {
        tag = 0;
        UserInitiateIntercomRequest bean = new UserInitiateIntercomRequest();
        bean.setToken(AppContext.getToken());
        bean.setApi_version("1.0");
        bean.setUuid(uuid);
        bean.setVendor_name(FactoryType.GENERAL);
        UserInitiateIntercomRequest.DataBean dataBean = new UserInitiateIntercomRequest.DataBean();
        dataBean.setAction("hangup");
        bean.setData(dataBean);
        String req = new Gson().toJson(bean);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_INITIATE_INTERCOM_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户挂断对讲发送成功");
                    tag = 2;
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener startAndEndSpeaking = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                    if (response != null) {
                        if (response.getReturn_string().contains("SUCCESS")) {
                            if (tag == 2) {
                                Toast.makeText(DoorLockCallActivity.this, "用户挂断对讲返回" + response.getReturn_string(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (tag == 1) {
                                Toast.makeText(DoorLockCallActivity.this, "用户发起对讲返回" + response.getReturn_string(), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(DoorLockCallActivity.this, RtspPlayerActivity.class);
                                i.putExtra("deviceUuid", uuid);
                                startActivity(i);
                                finish();
                            }
                        }
                        //需要验证
                        else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                            userConfirmationReq();
                        }
                        //token失效
                        else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING") || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                            reqAddToken();
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
                        switch (tag) {
                            case 1:
                                userStartSpeaking();
                                break;
                            case 2:
                                userStopSpeaking();
                                break;
                        }

                    }
                }
            });
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        userStopSpeaking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.off("MSG_USER_INITIATE_INTERCOM_RSP", startAndEndSpeaking);
            socket.off("MSG_USER_AUTH_RSP", userConfirmation);
        }
        if (DeviceEndPushflowsubscription != null && !DeviceEndPushflowsubscription.isUnsubscribed()) {
            DeviceEndPushflowsubscription.unsubscribe();
        }
    }

    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {
        hideLoadingDialog();
        if (bean == null) return;
        if (bean.getHeader().getHttp_code().equals("200")) {
            if (bean.getBody() != null && bean.getBody().getNote() != null) {
                tvEquesName.setText(bean.getBody().getNote());
                note = bean.getBody().getNote();
            }
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

    @Override
    public void resAddToken(AddTokenResponse bean) {
        LogUtil.e("token", "" + bean.getBody().getToken());
        if ("200".equals(bean.getHeader().getHttp_code())) {
            if (bean.getBody() != null && bean.getBody().getToken() != null) {
                LogUtil.e("token", "" + bean.getBody().getToken());
                AppContext.setToken(bean.getBody().getToken());
                switch (tag) {
                    case 1:
                        userStartSpeaking();
                        break;
                    case 2:
                        userStopSpeaking();
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
