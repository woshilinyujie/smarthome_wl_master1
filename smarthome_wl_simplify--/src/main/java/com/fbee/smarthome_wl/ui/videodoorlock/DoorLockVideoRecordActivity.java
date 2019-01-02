package com.fbee.smarthome_wl.ui.videodoorlock;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.alarmlist.DoorlockAlarmActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.locklog.VideoDoorLockLogActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.operationrecord.OperationRecordActivity;

public class DoorLockVideoRecordActivity extends BaseActivity {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RelativeLayout relativeOpenDoorRecord;
    private RelativeLayout relativeOperateDoorRecord;
    private RelativeLayout relativeVisitorDoorRecord;
    String deviceUuid;
    String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_lock_video_record);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        relativeOpenDoorRecord = (RelativeLayout) findViewById(R.id.relative_open_door_record);
        relativeOperateDoorRecord = (RelativeLayout) findViewById(R.id.relative_operate_door_record);
        relativeVisitorDoorRecord = (RelativeLayout) findViewById(R.id.relative_visitor_door_record);
        back.setOnClickListener(this);
        relativeOpenDoorRecord.setOnClickListener(this);
        relativeOperateDoorRecord.setOnClickListener(this);
        relativeVisitorDoorRecord.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        title.setText("门锁记录");
        deviceUuid = getIntent().getExtras().getString("deviceUuid");
        deviceName = getIntent().getExtras().getString("deviceName");
        if(deviceUuid==null)return;
        if(deviceName==null)return;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;

            //开锁记录
            case R.id.relative_open_door_record:
                Bundle bundle1 = new Bundle();
                bundle1.putString("deviceUuid", deviceUuid);
                skipAct(VideoDoorLockLogActivity.class, bundle1);
                break;

            //操作记录
            case R.id.relative_operate_door_record:
                Bundle bundle5 = new Bundle();
                bundle5.putString(Method.ATTR_BUDDY_UID, deviceUuid);
                bundle5.putString(Method.ATTR_BUDDY_NICK, deviceUuid);
                skipAct(OperationRecordActivity.class, bundle5);
                break;

            //访客记录
            case R.id.relative_visitor_door_record:
                Bundle bundle2 = new Bundle();
                bundle2.putString("deviceUuid", deviceUuid);
                bundle2.putString("type", "visitor");
                bundle2.putString("deviceName", deviceName);
                skipAct(DoorlockAlarmActivity.class, bundle2);
                break;
        }
    }
}
