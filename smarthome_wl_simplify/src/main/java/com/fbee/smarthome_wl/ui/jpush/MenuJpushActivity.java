package com.fbee.smarthome_wl.ui.jpush;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

public class MenuJpushActivity extends BaseActivity {
    private CheckBox doorLockCb;
    private CheckBox smartCatCb;
    private CheckBox smartArlamCb;
    private LoginResult.BodyBean.GatewayListBean gw;
    private String Alarm;
    private String Call;
    private HashSet<String> stringSet;
    private boolean jpushCall;
    private boolean jpushAlarm;
    private boolean checked;
    private TextView title;
    private ImageView back;
    private boolean jpushLock;
    private String lock;
    private String localUsername;
    private CloudPushService cloudPushService;
    private String[] aliTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_jpush);
    }

    @Override
    protected void initView() {

        doorLockCb = (CheckBox) findViewById(R.id.door_lock_cb);
        smartCatCb = (CheckBox) findViewById(R.id.smart_cat_cb);
        smartArlamCb = (CheckBox) findViewById(R.id.smart_arlam_cb);
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.back);
    }

    @Override
    protected void initData() {
        title.setText("推送设置");
        back.setVisibility(View.VISIBLE);
        gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        aliTag = new String[]{gw.getUsername()};
        if (gw != null) {
            stringSet = (HashSet<String>) PreferencesUtils.getObject(gw.getUsername());
            jpushCall = PreferencesUtils.getBoolean(gw.getUsername() + PreferencesUtils.JPUSH_CALL);
            jpushAlarm = PreferencesUtils.getBoolean(gw.getUsername() + PreferencesUtils.JPUSH_ALARM);
            jpushLock = PreferencesUtils.getBoolean(gw.getUsername() + PreferencesUtils.JPUSH_LOCK);
        }
        if (stringSet == null) {
            stringSet = new HashSet<>();
        }
        cloudPushService = Api.getCloudPushService();
        doorLockCb.setChecked(jpushLock);
        smartCatCb.setChecked(jpushCall);
        smartArlamCb.setChecked(jpushAlarm);
        back.setOnClickListener(this);
        doorLockCb.setOnClickListener(this);
        smartCatCb.setOnClickListener(this);
        smartArlamCb.setOnClickListener(this);
        if (jpushAlarm == true || jpushLock == true) {
            openJpush(stringSet);
        }
//        if (jpushLock == true) {
//            cloudPushService.bindTag(CloudPushService.DEVICE_TARGET, aliTag, null, new CommonCallback() {
//                @Override
//                public void onSuccess(String s) {
//                    LogUtil.e("MyMessageReceiver+register+bindTag+onSuccess 1", s+"1");
//                }
//
//                @Override
//                public void onFailed(String s, String s1) {
//                    LogUtil.e("MyMessageReceiver+register+bindTag+onFailed 1", s + "---" + s1);
//                }
//            });
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.door_lock_cb:
                if (doorLockCb.isChecked()) {
                    cloudPushService.bindTag(CloudPushService.DEVICE_TARGET, aliTag, null, new CommonCallback() {
                        @Override
                        public void onSuccess(String s) {
                            LogUtil.e("MyMessageReceiver+register+bindTag+onSuccess 2", s+"2");
                        }

                        @Override
                        public void onFailed(String s, String s1) {
                            LogUtil.e("MyMessageReceiver+register+bindTag+onFailed 2", s + "---" + s1);
                        }
                    });
                } else {
                    cloudPushService.unbindTag(CloudPushService.DEVICE_TARGET, aliTag, null, new CommonCallback() {
                        @Override
                        public void onSuccess(String s) {
                            LogUtil.e("MyMessageReceiver+register+bindTag+onSuccess 3", s+"3");
                        }

                        @Override
                        public void onFailed(String s, String s1) {
                            LogUtil.e("MyMessageReceiver+register+bindTag+onFailed 3", s + "---" + s1);
                        }
                    });
                }
                if (gw != null) {
                    PreferencesUtils.saveBoolean(gw.getUsername() + PreferencesUtils.JPUSH_LOCK, doorLockCb.isChecked());
                }

                break;
            case R.id.smart_cat_cb:
                if (gw == null) {
                    String localUsername = PreferencesUtils.getString(LOCAL_USERNAME);
                    lock = localUsername + "__DBCall";
                } else {
                    Call = gw.getUsername() + "__DBCall";
                }
                checked = smartCatCb.isChecked();
                if (checked) {
                    if (stringSet.contains(Call)) {
                    } else {
                        stringSet.add(Call);
                    }
                    if (gw == null) {
                        PreferencesUtils.saveObject(localUsername, stringSet);
                    } else {
                        PreferencesUtils.saveObject(gw.getUsername(), stringSet);
                    }
                    openJpush(stringSet);
                } else {
                    closeJpush(Call);
                }
                if (gw != null)
                    PreferencesUtils.saveBoolean(gw.getUsername() + PreferencesUtils.JPUSH_CALL, smartCatCb.isChecked());
                break;
            case R.id.smart_arlam_cb:
                if (gw == null) {
                    String localUsername = PreferencesUtils.getString(LOCAL_USERNAME);
                    lock = localUsername + "__DBAlarm";
                } else {
                    Alarm = gw.getUsername() + "__DBAlarm";
                }
                boolean checked = smartArlamCb.isChecked();
                if (checked) {
                    if (stringSet.contains(Alarm)) {

                    } else {
                        stringSet.add(Alarm);
                    }
                    if (gw == null) {
                        PreferencesUtils.saveObject(localUsername, stringSet);
                    } else {
                        PreferencesUtils.saveObject(gw.getUsername(), stringSet);
                    }
                    openJpush(stringSet);
                } else {
                    closeJpush(Alarm);
                }
                if (gw != null)
                    PreferencesUtils.saveBoolean(gw.getUsername() + PreferencesUtils.JPUSH_ALARM, smartArlamCb.isChecked());
                break;
            case R.id.back:
                finish();
                break;
        }

    }

    private void openJpush(HashSet<String> stringSet) {
        Set<String> tags = JPushInterface.filterValidTags(stringSet);

        JPushInterface.setTags(this, tags,
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int arg0, String arg1, Set<String> arg2) {
                    }
                });
    }

    public void closeJpush(String username) {
        stringSet.remove(username);
        JPushInterface.setTags(this, stringSet,
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int arg0, String arg1, Set<String> arg2) {
                    }
                });
    }
}
