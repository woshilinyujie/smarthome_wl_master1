package com.fbee.smarthome_wl.ui.videodoorlock.operationrecord;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.response.RecordResponse;

import java.text.SimpleDateFormat;

public class OperationRecordInfoActivity extends BaseActivity {

    private RecordResponse.BodyEntity bodyEntity;
    private ListView list;
    private TextView operatingTime;
    private TextView operatingType;
    private TextView userName;
    private TextView userLevel;
    private TextView attribute;
    private TextView adminUser;
    private TextView adminUser2;
    private StringBuffer stringBuffer;
    private String mode;
    private ImageView back;
    private String device_user_id;
    private LinearLayout llUsername;
    private String[] split1;
    private String object_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_record_info);
        initView();
    }

    @Override
    protected void initView() {
        operatingTime = (TextView) findViewById(R.id.operating_time);
        operatingType = (TextView) findViewById(R.id.operating_type);
        userName = (TextView) findViewById(R.id.user_name);
        userLevel = (TextView) findViewById(R.id.user_level);
        attribute = (TextView) findViewById(R.id.attribute);
        adminUser = (TextView) findViewById(R.id.admin_user);
        adminUser2 = (TextView) findViewById(R.id.admin_user2);
        llUsername = (LinearLayout) findViewById(R.id.ll_username);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        stringBuffer = new StringBuffer();
        bodyEntity = (RecordResponse.BodyEntity) getIntent().getSerializableExtra("bean");
        //设备的UNIX时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String hms = formatter.format(Long.parseLong(bodyEntity.getTimestamp()) * 1000);
        operatingTime.setText(hms);
        //操作的方式
        mode = bodyEntity.getBe_operated().getMode();
        String modemsg = SelectCharacter(mode);
        operatingType.setText(modemsg);
        //开锁的方式
        StringBuffer selectmode = Selectmode(bodyEntity.getBe_operated().getUnlock_mode());
        String substring = selectmode.substring(1, selectmode.length());
        attribute.setText(substring);
        //被操作的对象
        object_note = bodyEntity.getBe_operated().getObject_note();
        if (object_note != null && !object_note.isEmpty()) {
            userName.setText(bodyEntity.getBe_operated().getObject() + "/" + object_note);
        } else {
            userName.setText(bodyEntity.getBe_operated().getObject());
        }
        //被操作用户的等级
        userLevel.setText(bodyEntity.getBe_operated().getObject_level());
        //授权管理员
        device_user_id = bodyEntity.getDevice_user_id();
        String device_user_note = bodyEntity.getDevice_user_note();
        if (device_user_id.contains(",")) {
            if (device_user_note.contains(",")) {
                split1 = device_user_note.split(",");
            }
            String[] split = device_user_id.split(",");
            llUsername.setVisibility(View.VISIBLE);
            if (split1 != null && split1.length > 0 && split1[0] != null && !split1[0].isEmpty()) {
                adminUser.setText(split[0] + "/" + split1[0]);
            } else {
                adminUser.setText(split[0]);
            }
            if (split1 != null && split1.length > 0 && split1[1] != null && !split1[1].isEmpty()) {
                adminUser2.setText(split[1] + "/" + split1[1]);
            } else {
                adminUser2.setText(split[1]);
            }
        } else {
            if (device_user_note != null && !device_user_note.isEmpty()) {
                adminUser.setText(device_user_id + "/" + device_user_note);
            } else {
                adminUser.setText(device_user_id);
            }
        }
    }

    private String SelectCharacter(String content) {
        String stringmsg = null;
        switch (content) {
            case "del":
                stringmsg = ("删除");
                break;
            case "update":
                stringmsg = ("修改");
                break;
            case "add":
            case "create":
                stringmsg = ("新增");
                break;
        }
        return stringmsg;
    }

    private StringBuffer Selectmode(String mode) {
        if (mode.contains("pwd")) {
            stringBuffer.append("/密码");
        }
        if (mode.contains("fp")) {
            stringBuffer.append("/指纹");
        }
        if (mode.contains("card")) {
            stringBuffer.append("/刷卡");
        }
        if (mode.contains("face")) {
            stringBuffer.append("/人脸");
        }
        if (mode.contains("rf")) {
            stringBuffer.append("/感应");
        }
        if (mode.contains("eye")) {
            stringBuffer.append("/虹膜");
        }
        if (mode.contains("vena")) {
            stringBuffer.append("/指静脉");
        }
        if (mode.contains("remote")) {
            stringBuffer.append("/远程");
        }
        if (mode.contains("timeslot")) {
            stringBuffer.append("/时间段");
        } else {
            stringBuffer.append("/所有");
        }
        return stringBuffer;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
