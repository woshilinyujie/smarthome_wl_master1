package com.fbee.smarthome_wl.ui.YsPlay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.utils.DateUtils;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZDeviceRecordFile;
import com.videogo.util.Utils;
import com.xw.repo.BubbleSeekBar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by linyujie on 19/1/3.
 */

public class YsVideoActivity extends AppCompatActivity {
    @BindView(R.id.ys_video_sv)
    SurfaceView ysVideoSv;
    @BindView(R.id.ys_video_back)
    ImageView ysVideoBack;
    @BindView(R.id.ys_video_play1)
    ImageView ysVideoPlay1;
    @BindView(R.id.ys_video_time1)
    TextView ysVideoTime1;
    @BindView(R.id.ys_video_bar)
    BubbleSeekBar ysVideoBar;
    @BindView(R.id.ys_video_time2)
    TextView ysVideoTime2;
    @BindView(R.id.ys_video_jietu)
    ImageView ysVideoJietu;
    @BindView(R.id.ys_video_play)
    ImageView ysVideoPlay;
    @BindView(R.id.ys_loading)
    ImageView ysLoading;
    private String random;
    private EZPlayer player;
    // 录像时间
    private int mRecordSecond = 0;
    private boolean isPlay = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 205://成功
                    isPlay = true;
                    ysVideoTime1.setText("00:00:00");
                    ysLoading.setVisibility(View.GONE);
                    ysVideoPlay1.setBackgroundResource(R.mipmap.ys_video_kaibo);
                    startUpdateTimer();
                    break;
                case 201://结束
                    isPlay = false;
                    ysVideoPlay.setVisibility(View.VISIBLE);
                    ysVideoPlay1.setBackgroundResource(R.mipmap.ys_video_play1);
                    ysVideoBar.setProgress(10);
                    ysVideoTime1.setText("00:00:10");
                    stopUpdateTimer();
                    player.stopPlayback();
                    break;
                case 221://停止
                    isPlay = false;
                    ysVideoPlay.setVisibility(View.VISIBLE);
                    ysVideoPlay1.setBackgroundResource(R.mipmap.ys_video_play1);
                    stopUpdateTimer();
                    player.stopPlayback();
                    break;
                case 5000:
                    if (player != null) {
                        osd = player.getOSDTime();
                        if (osd != null)
                            RefreshUi(osd);
                    }
                    break;
                case 1000:
                    ysLoading.setVisibility(View.VISIBLE);
                    ysVideoPlay.setVisibility(View.GONE);
                    player.setHandler(handler);
                    holder = ysVideoSv.getHolder();
                    player.setSurfaceHold(holder);
                    player.startPlayback(ezDeviceRecordFile);
                    break;
            }
        }
    };

    private String mRecordTime = null;
    private SurfaceHolder holder;
    private String time;
    private String UUIDd;
    private EZDeviceRecordFile ezDeviceRecordFile;
    private Timer mUpdateTimer;
    private TimerTask mUpdateTimerTask;
    private boolean bIsRecording = false;
    private int progress;
    private Calendar osd;
    private Calendar instanceC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys_video_layout);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Glide.with(this).load(R.drawable.ys_loading).asGif().into(ysLoading);
        random = getIntent().getStringExtra("random");
        UUIDd = getIntent().getStringExtra("uuid");
        time = getIntent().getStringExtra("time");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        player = EZOpenSDK.getInstance().createPlayer(UUIDd, 1);
        player.setPlayVerifyCode(random);
        final Calendar startTime = Calendar.getInstance();
        final Calendar endTime = Calendar.getInstance();
        Date date = new Date(Long.parseLong(time) * 1000);
        startTime.setTime(date);
        endTime.setTime(date);
        String s = DateUtils.getInstance().dateFormat6(Long.parseLong(time) * 1000 - 10000);
        String s1 = DateUtils.getInstance().dateFormat6(Long.parseLong(time) * 1000 + 10000);
        String[] split = s.split(":");
        String[] split1 = s1.split(":");

        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
        startTime.set(Calendar.MINUTE, Integer.parseInt(split[0]));
        startTime.set(Calendar.SECOND, Integer.parseInt(split[0]));
        endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split1[0]));
        endTime.set(Calendar.MINUTE, Integer.parseInt(split1[1]));
        endTime.set(Calendar.SECOND, Integer.parseInt(split1[2]));


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EZDeviceRecordFile> ezDeviceRecordFiles = EZOpenSDK.getInstance().searchRecordFileFromDevice(UUIDd, 1, startTime, endTime);
                    if (ezDeviceRecordFiles.size() > 0) {
                        ezDeviceRecordFile = ezDeviceRecordFiles.get(0);
                        handler.sendEmptyMessage(1000);
                    }
                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        ysVideoBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stopUpdateTimer();
                        break;

                }
                return false;
            }
        });
        ysVideoBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int i, float v) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int i, float v) {
                if(isPlay){
                    if(instanceC==null)
                    instanceC = Calendar.getInstance();
                    Date date1=new Date();
                    date1.setTime(osd.getTime().getTime()+i*1000);
                    instanceC.setTime(date1);
                    player.pausePlayback();
                    player.seekPlayback(instanceC);
                    bubbleSeekBar.setProgress(i);
                }
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int i, float v) {

            }
        });
    }

    @OnClick({R.id.ys_video_back, R.id.ys_video_play1, R.id.ys_video_jietu, R.id.ys_video_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ys_video_back:
                finish();
                break;
            case R.id.ys_video_play1:
                if (isPlay) {
                    if (ysVideoPlay.getVisibility() == View.GONE) {
                        player.pausePlayback();
                        stopUpdateTimer();
                        ysVideoPlay.setVisibility(View.VISIBLE);
                        ysVideoPlay1.setBackgroundResource(R.mipmap.ys_video_play1);
                    } else {
                        player.resumePlayback();
                        ysVideoPlay.setVisibility(View.GONE);
                        ysVideoPlay1.setBackgroundResource(R.mipmap.ys_video_kaibo);
                        startUpdateTimer();
                    }
                } else {
                    if (ysLoading.getVisibility() == View.VISIBLE) {
                        return;
                    }
                    handler.sendEmptyMessage(1000);
                }

                break;
            case R.id.ys_video_play:
                if (isPlay) {
                    if (ysVideoPlay.getVisibility() == View.GONE) {
                        player.pausePlayback();
                        stopUpdateTimer();
                        ysVideoPlay.setVisibility(View.VISIBLE);
                        ysVideoPlay1.setBackgroundResource(R.mipmap.ys_video_play1);
                    } else {
                        player.resumePlayback();
                        ysVideoPlay.setVisibility(View.GONE);
                        ysVideoPlay1.setBackgroundResource(R.mipmap.ys_video_kaibo);
                        startUpdateTimer();
                    }
                } else {
                    if (ysLoading.getVisibility() == View.VISIBLE) {
                        return;
                    }
                    handler.sendEmptyMessage(1000);
                }

                break;
            case R.id.ys_video_jietu:
                break;
        }
    }


    private void RefreshUi(Calendar osdTime) {
        long osd = osdTime.getTimeInMillis();
        long begin = ezDeviceRecordFile.getStartTime().getTime().getTime();
        long end = ezDeviceRecordFile.getStopTime().getTime().getTime();
        double x = ((osd - begin)) / 1000;
        progress = (int) x;
        ysVideoBar.setProgress(progress);
        if (progress < 10) {
            ysVideoTime1.setText("00:00:0" + progress);
        } else {
            ysVideoTime1.setText("00:00:10");
        }
    }


    /**
     * 启动定时器
     *
     * @see
     * @since V1.0
     */
    private void startUpdateTimer() {
        stopUpdateTimer();
        // 开始录像计时
        if (mUpdateTimer == null)
            mUpdateTimer = new Timer();
        if (mUpdateTimerTask == null)
            mUpdateTimerTask = new TimerTask() {
                @Override
                public void run() {

                    // 录像显示
                    if (bIsRecording) {
                        // 更新录像时间
                        Calendar OSDTime = null;
                        if (player != null)
                            OSDTime = player.getOSDTime();
                        if (OSDTime != null) {
                            String playtime = Utils.OSD2Time(OSDTime);
                            if (!playtime.equals(mRecordTime)) {
                                mRecordSecond++;
                                mRecordTime = playtime;
                            }
                        }
                    }
                    sendMessage(5000, 0, 0);
                }
            };
        // 延时1000ms后执行，1000ms执行一次

        mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000);
    }


    // 停止定时器
    private void stopUpdateTimer() {
        // 停止录像计时
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }

    private void sendMessage(int message, int arg1, int arg2) {
        if (handler != null) {
            Message msg = handler.obtainMessage();
            msg.what = message;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stopPlayback();
            player.release();
        }

        stopUpdateTimer();

    }

}
