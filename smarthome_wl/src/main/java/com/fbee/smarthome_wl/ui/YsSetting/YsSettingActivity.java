package com.fbee.smarthome_wl.ui.YsSetting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.ielse.view.SwitchView;

/**
 * Created by Administrator on 2019/1/6.
 */

public class YsSettingActivity extends BaseActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.iv_right_menu)
    ImageView ivRightMenu;
    @BindView(R.id.tv_right_menu)
    TextView tvRightMenu;
    @BindView(R.id.header_rl)
    RelativeLayout headerRl;
    @BindView(R.id.ys_set_name)
    RelativeLayout ysSetName;
    @BindView(R.id.ys_set_modle)
    TextView ysSetModle;
    @BindView(R.id.ys_set_serial)
    TextView ysSetSerial;
    @BindView(R.id.ys_set_wifi)
    TextView ysSetWifi;
    @BindView(R.id.ys_set_version)
    TextView ysSetVersion;
    @BindView(R.id.ys_set_help)
    SwitchView ysSetHelp;
    @BindView(R.id.ys_set_detection)
    SwitchView ysSetDetection;
    @BindView(R.id.ys_set_encode)
    SwitchView ysSetEncode;
    @BindView(R.id.ys_set_update)
    RelativeLayout ysSetUpdate;
    @BindView(R.id.ys_set_delete)
    Button ysSetDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys_setting);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        back.setOnClickListener(this);
        ysSetName.setOnClickListener(this);
        ysSetUpdate.setOnClickListener(this);
        ysSetDelete.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                break;
            case R.id.ys_set_name:
                break;
            case R.id.ys_set_update:
                break;
            case R.id.ys_set_delete:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
