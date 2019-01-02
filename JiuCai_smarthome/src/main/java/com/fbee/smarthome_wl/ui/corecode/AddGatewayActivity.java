package com.fbee.smarthome_wl.ui.corecode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.ui.login.LoginActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.ui.main.homepage.HomeFragment;
import com.fbee.smarthome_wl.utils.PreferencesUtils;

public class AddGatewayActivity extends BaseActivity {
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RelativeLayout captureCropView;
    private Button btnAdd;
    public static AddGatewayActivity addGatewayActivity;
    private Button btnSkip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gateway);
        addGatewayActivity = this;
    }

    @Override
    protected void initView() {
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        captureCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnSkip = (Button) findViewById(R.id.btn_skip);
    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("添加网关");
        btnAdd.setOnClickListener(this);
        btnSkip.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.btn_add:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyRecordPermissions(this);
                } else {
                    Intent intent = new Intent(this, CaptureActivity.class);
                    intent.putExtra("ISADD", "addGw");
                    startActivity(intent);
                }
                break;
            case R.id.btn_skip:
                Intent intent01 = new Intent(this, MainActivity.class);
                startActivity(intent01);
                finish();
                break;
        }
    }

    private static String[] PERMISSIONS_RECORD = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};

    private void verifyRecordPermissions(BaseActivity captureActivity) {
        //摄像头权限
        if (ContextCompat.checkSelfPermission(captureActivity,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //先判断有没有权限 ，没有就在这里进行权限的申请
            ActivityCompat.requestPermissions(this, PERMISSIONS_RECORD, 2);
        } else {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra("ISADD", "addGw");
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, CaptureActivity.class);
                intent.putExtra("ISADD", "addGw");
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("ISADD", "addGw");
                bundle.putBoolean("isPermission", true);
                startActivity(intent, bundle);
            }
        }
    }
}
