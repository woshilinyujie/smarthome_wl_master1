package com.fbee.smarthome_wl.ui.YsPlay;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.ui.ys.ScreenOrientationHelper;
import com.fbee.smarthome_wl.utils.DateUtils;
import com.videogo.constant.Constant;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.realplay.RealPlayStatus;
import com.videogo.util.LocalInfo;
import com.videogo.util.Utils;
import com.videogo.widget.CheckTextButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by linyujie on 18/12/28.
 */

public class YsplayActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.ys_sv)
    SurfaceView ysSv;
    @BindView(R.id.ys_play)
    ImageView ysPlay;
    @BindView(R.id.ys_time)
    TextView ysTime;
    @BindView(R.id.ys_screen)
    CheckTextButton ysScreen;
    @BindView(R.id.ys_connection)
    ImageView ysConnection;
    @BindView(R.id.ys_guaduan)
    ImageView ysGuaduan;
    @BindView(R.id.ys_jinyin)
    ImageView ysJinyin;
    @BindView(R.id.ys_kaisuo)
    ImageView ysKaisuo;
    @BindView(R.id.ys_loading)
    ImageView ysLoading;
    @BindView(R.id.ys_group)
    RelativeLayout ysGroup;
    @BindView(R.id.header_rl)
    RelativeLayout headerRl;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.iv_right_menu)
    ImageView ivRightMenu;
    @BindView(R.id.tv_right_menu)
    TextView tvRightMenu;
    @BindView(R.id.quanpin_lock)
    ImageView quanpinLock;
    @BindView(R.id.quanpin_jinyin)
    ImageView quanpinJinyin;
    @BindView(R.id.quanpin_guaduan)
    ImageView quanpinGuaduan;
    @BindView(R.id.quanpin_connection)
    ImageView quanpinConnection;
    @BindView(R.id.quanpin_ll)
    LinearLayout quanpinLl;
    @BindView(R.id.ys_time_rl)
    RelativeLayout ysTimeRl;
    private EZPlayer player;
    private long time = 16 * 60 * 60 * 1000;
    private SurfaceHolder mRealPlaySh;
    private boolean isOpenSound = true;
    private ScreenOrientationHelper mScreenOrientationHelper;
    /**
     * 标识是否正在播放
     */
    private int mStatus = RealPlayStatus.STATUS_INIT;
    private boolean mIsOnStop = false;
    /**
     * 屏幕当前方向
     */
    private int mOrientation = Configuration.ORIENTATION_PORTRAIT;
    private float mRealRatio = Constant.LIVE_VIEW_RATIO;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ysLoading.setVisibility(View.GONE);
            switch (msg.what) {
                case 102://成功
                    mStatus = RealPlayStatus.STATUS_PLAY;
                    ysPlay.setVisibility(View.GONE);
                    countDownTimer.start();
                    updateOrientation();
                    mRealRatio = Constant.LIVE_VIEW_RATIO;
                    break;
                case 103://错误
                    ysPlay.setVisibility(View.VISIBLE);
                    ysTime.setText("00:00:00");
                    countDownTimer.cancel();
                    time = 16 * 60 * 60 * 1000;
                    break;
                case 100://暂停
                    ysPlay.setVisibility(View.VISIBLE);
                    ysTime.setText("00:00:00");
                    time = 16 * 60 * 60 * 1000;
                    countDownTimer.cancel();
                    break;
                case 1000:

                    break;
            }
        }
    };
    private CountDownTimer countDownTimer;
    private String uuid;
    private String random;
    private LocalInfo mLocalInfo;
    private SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys_play_layout);
        ButterKnife.bind(this);
        initData();
    }


    protected void initData() {
        uuid = getIntent().getStringExtra("uuid");
        random = getIntent().getStringExtra("random");
        player = EZOpenSDK.getInstance().createPlayer(uuid, 1);
        player.setHandler(handler);
        holder = ysSv.getHolder();
        player.setSurfaceHold(holder);
        holder.addCallback(this);
        player.setPlayVerifyCode(random);
        player.startRealPlay();
        player.openSound();
// 获取配置信息操作对象
        mLocalInfo = LocalInfo.getInstance();// 获取屏幕参数
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mLocalInfo.setScreenWidthHeight(metric.widthPixels, metric.heightPixels);
        mLocalInfo.setNavigationBarHeight((int) Math.ceil(25 * getResources().getDisplayMetrics().density));
        mScreenOrientationHelper = new ScreenOrientationHelper(this, ysScreen, null,ysSv);
        updateOrientation();
        Glide.with(this).load(R.drawable.ys_loading).asGif().into(ysLoading);
        countDownTimer = new CountDownTimer(24 * 60 * 60 * 1000, 1000 * 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                time = time + 1000;
                ysTime.setText(DateUtils.getInstance().dateFormat6(time));
            }

            @Override
            public void onFinish() {
            }
        };

    }


    @OnClick({R.id.quanpin_lock, R.id.quanpin_jinyin, R.id.quanpin_guaduan, R.id.quanpin_connection, R.id.ys_sv, R.id.ys_time, R.id.ys_connection, R.id.ys_guaduan, R.id.ys_jinyin, R.id.ys_kaisuo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ys_sv:
                break;
            case R.id.ys_time:
                break;

            case R.id.ys_connection:
                if( ysPlay.getVisibility()==View.GONE)
                    return;
                player = EZOpenSDK.getInstance().createPlayer(uuid, 1);
                player.setHandler(handler);
                holder = ysSv.getHolder();
                player.setSurfaceHold(holder);
                holder.addCallback(this);
                player.setPlayVerifyCode(random);
                player.startRealPlay();
                ysPlay.setVisibility(View.GONE);
                ysLoading.setVisibility(View.VISIBLE);

                break;
            case R.id.ys_guaduan:
                //停止直播

                player.stopRealPlay();
                ysTime.setText("00:00:00");
                ysPlay.setVisibility(View.VISIBLE);
                //释放资源
                player.release();
                countDownTimer.cancel();
                time = 16 * 60 * 60 * 1000;
                ysLoading.setVisibility(View.GONE);
                break;
            case R.id.ys_jinyin:
                if (isOpenSound) {
                    player.closeSound();
                    isOpenSound = false;
                } else {
                    player.openSound();
                    isOpenSound = true;
                }
                break;
            case R.id.ys_kaisuo:
                break;
            case R.id.quanpin_lock:
                break;
            case R.id.quanpin_jinyin:
                if (isOpenSound) {
                    player.closeSound();
                    isOpenSound = false;
                } else {
                    player.openSound();
                    isOpenSound = true;
                }
                break;
            case R.id.quanpin_guaduan:
                //停止直播

                player.stopRealPlay();
                ysTime.setText("00:00:00");
                ysPlay.setVisibility(View.VISIBLE);
                //释放资源
                player.release();
                countDownTimer.cancel();
                time = 16 * 60 * 60 * 1000;
                ysLoading.setVisibility(View.GONE);
                break;
            case R.id.quanpin_connection:
                if(ysPlay.getVisibility()==View.GONE)
                    return;
                player = EZOpenSDK.getInstance().createPlayer(uuid, 1);
                player.setHandler(handler);
                holder = ysSv.getHolder();
                player.setSurfaceHold(holder);
                holder.addCallback(this);
                player.setPlayVerifyCode(random);
                player.startRealPlay();
                ysPlay.setVisibility(View.GONE);
                ysLoading.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        player.release();
        countDownTimer.cancel();
        time = 16 * 60 * 60 * 1000;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurfaceHold(holder);
        }
        mRealPlaySh = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.setSurfaceHold(null);
        }
        mRealPlaySh = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            mOrientation = newConfig.orientation;
            setRealPlaySvLayout();
            quanpinLl.setVisibility(View.VISIBLE);
            ysTimeRl.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRealPlaySvLayout();
            quanpinLl.setVisibility(View.GONE);
            ysTimeRl.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onConfigurationChanged(newConfig);
    }


    private void updateOrientation() {
        if (mStatus == RealPlayStatus.STATUS_PLAY) {
            setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        }
    }

    private void setOrientation(int sensor) {
        if (sensor == ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            mScreenOrientationHelper.enableSensorOrientation();
        else
            mScreenOrientationHelper.disableSensorOrientation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mScreenOrientationHelper != null) {
            mScreenOrientationHelper.postOnStop();
        }
    }


    private void setRealPlaySvLayout() {
        final int screenWidth;
        final int screenHeight;

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            // 设置播放窗口位置
            screenWidth = mLocalInfo.getScreenWidth();
            screenHeight = (mOrientation == Configuration.ORIENTATION_PORTRAIT) ? (mLocalInfo.getScreenHeight() - mLocalInfo
                    .getNavigationBarHeight()) : mLocalInfo.getScreenHeight();
            final RelativeLayout.LayoutParams realPlaySvlp = Utils.getPlayViewLp(mRealRatio, mOrientation,
                    mLocalInfo.getScreenWidth(), (int) (mLocalInfo.getScreenWidth() * Constant.LIVE_VIEW_RATIO),
                    screenWidth, screenHeight);
            RelativeLayout.LayoutParams svLp = new RelativeLayout.LayoutParams(realPlaySvlp.width, realPlaySvlp.height);
            LinearLayout.LayoutParams svgruop = new LinearLayout.LayoutParams(realPlaySvlp.width, realPlaySvlp.height);
            ysGroup.setLayoutParams(svgruop);
            ysSv.setLayoutParams(svLp);
            //title影藏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else{
            screenWidth = mLocalInfo.getScreenWidth();
            screenHeight=mScreenOrientationHelper.height;
            final RelativeLayout.LayoutParams realPlaySvlp = Utils.getPlayViewLp(mRealRatio, mOrientation,
                    mLocalInfo.getScreenWidth(), (int) (mLocalInfo.getScreenWidth() * Constant.LIVE_VIEW_RATIO),
                    screenWidth, screenHeight);
            RelativeLayout.LayoutParams svLp = new RelativeLayout.LayoutParams(realPlaySvlp.height,realPlaySvlp.width);
            LinearLayout.LayoutParams svgruop = new LinearLayout.LayoutParams(realPlaySvlp.height,realPlaySvlp.width);
            ysGroup.setLayoutParams(svgruop);
            ysSv.setLayoutParams(svLp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        headerRl.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            mScreenOrientationHelper.portrait();
            return;
        }
        finish();
    }
}
