package com.example.wl.WangLiPro_v1.devices;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.jwl.android.jwlandroidlib.udp.UdpManager;
import com.jwl.android.jwlandroidlib.udp.inter.CommInter;
import com.jwl.android.jwlandroidlib.udpbean.BaseUdpBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import rx.functions.Action1;

public class WangliCallActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout hangUp;
    private String sid;
    private String from;
    private LinearLayout videoCall;
    private ImageView headOrtrait;
    private JSONObject jpushData;
    private TextView equesName;
    private MediaPlayer mediaplayer;
    private String deviceId;
    private String incallId;
    private String ip;
    public static boolean isAlive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wangli_video_call);
        isAlive = true;
    }

    @Override
    protected void initView() {
        hangUp = (LinearLayout) findViewById(R.id.hang_up);
        videoCall = (LinearLayout) findViewById(R.id.video_call);
        headOrtrait = (ImageView) findViewById(R.id.head_ortrait);
        equesName = (TextView) findViewById(R.id.tv_eques_name);
    }


    @Override
    protected void initData() {
        mediaplayer = MediaPlayer.create(this, R.raw.alarm);
        mediaplayer.setLooping(true);
        mediaplayer.start();
        deviceId = getIntent().getExtras().getString("deviceId");
        incallId = getIntent().getExtras().getString("incall");
        ip = getIntent().getExtras().getString("ip");
        hangUp.setOnClickListener(this);
        videoCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_up:
                eventsmart();
                mediaplayer.pause();
                finish();
                break;
            case R.id.video_call:
                Intent intent = new Intent(WangliCallActivity.this, LockVideoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("incall", incallId);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("ip", ip);
                startActivity(intent);
                mediaplayer.pause();
                finish();
                break;
        }
    }

    private void eventsmart() {
        UdpManager.getinstance().tearDown(new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                if (baseUdpBean.getErroCode() == 200) {
                    eventsmart();
                }
            }

            @Override
            public void erro(String msg) {
            }

            @Override
            public void complet() {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UdpManager.getinstance().close();
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAlive = false;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
        try {
            if (mediaplayer != null) {
                mediaplayer.stop();
                mediaplayer.release();//释放资源
            }
        } catch (Exception e) {

        }
    }
}
