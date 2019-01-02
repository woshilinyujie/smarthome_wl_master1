package com.fbee.smarthome_wl.ui.YsEye;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.ui.YsPlay.YsplayActivity;
import com.videogo.constant.Constant;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.realplay.RealPlayStatus;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by linyujie on 18/12/21.
 */

public class YsEyeActivity extends BaseActivity {

    private SurfaceHolder mRealPlaySh = null;
    private SurfaceView surfaceView;
    private EZPlayer player;
    private ImageView ys_video_view;
    private String random;
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys_layout);

    }

    @Override
    protected void initView() {
        ys_video_view = (ImageView) findViewById(R.id.ys_video_view);
        ys_video_view.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        uuid = getIntent().getStringExtra("uuid");
        random = getIntent().getStringExtra("random");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ys_video_view:
                Intent intent=new Intent(this, YsplayActivity.class);
                intent.putExtra("uuid",uuid);
                intent.putExtra("random",random);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
