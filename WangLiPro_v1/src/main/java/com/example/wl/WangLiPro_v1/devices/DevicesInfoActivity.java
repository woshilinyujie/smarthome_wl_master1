package com.example.wl.WangLiPro_v1.devices;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.base.BaseActivity;

public class DevicesInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back;
    private ImageView imageRecord;
    private ImageView userRecord;
    private TextView deviceInfo;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_info);
    }

    @Override
    protected void initData() {
        deviceId = getIntent().getStringExtra("ID");
        back.setOnClickListener(this);
        imageRecord.setOnClickListener(this);
        userRecord.setOnClickListener(this);
        deviceInfo.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ImageView imageview = (ImageView) findViewById(R.id.imageview);
        TextView title = (TextView) findViewById(R.id.title);
        imageRecord = (ImageView) findViewById(R.id.image_log_record);
        userRecord = (ImageView) findViewById(R.id.image_user_record);
        deviceInfo = (TextView) findViewById(R.id.tv_eq_device_info);
        back = (ImageView) findViewById(R.id.back);
        title.setText("门锁详情");
        Glide.with(this).load(Uri.parse(AppContext.getMapList().get(0))).into(imageview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.image_log_record:
                Intent intent = new Intent(this, DoorLockLogActivity.class);
                intent.putExtra("MSG", "LOG");
                startActivity(intent);
                break;
            case R.id.image_user_record:
                Intent intent1 = new Intent(this, DoorLockLogActivity.class);
                intent1.putExtra("MSG", "USER");
                intent1.putExtra("ID", deviceId);
                startActivity(intent1);
                break;
            case R.id.tv_eq_device_info:
                Intent intent2 = new Intent(this, CommConfigActivity.class);
                intent2.putExtra("ID", deviceId);
                startActivity(intent2);
                break;
        }
    }
}
