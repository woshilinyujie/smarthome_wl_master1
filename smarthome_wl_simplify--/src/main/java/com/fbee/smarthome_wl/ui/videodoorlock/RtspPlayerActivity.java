package com.fbee.smarthome_wl.ui.videodoorlock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.VideoTime;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.request.videolockreq.DeviceStartPushflowRequest;
import com.fbee.smarthome_wl.request.videolockreq.DeviceStopPushflowRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserAuthRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserInitiateIntercomRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserStartPushflowRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserTransportRequest;
import com.fbee.smarthome_wl.request.videolockreq.transportreq.DistanceOpenDoorLock;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.response.videolockres.DeviceTransportResponse;
import com.fbee.smarthome_wl.response.videolockres.MnsBaseResponse;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.Base64Util;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.FileUtils;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.vlcutil.streaming.RtpPacket;
import com.fbee.vlcutil.streaming.RtspClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

import static com.fbee.smarthome_wl.utils.ByteStringUtil.hexStringToBytes;
import static com.fbee.smarthome_wl.utils.ByteStringUtil.string2HexString;
import static com.fbee.vlcutil.streaming.RtspClient.TRANSPORT_UDP;


public class RtspPlayerActivity extends BaseActivity<DoorLockVideoCallContract.Presenter> implements DoorLockVideoCallContract.View {
    private static final String TAG = RtspPlayerActivity.class.getSimpleName();
    private FrameLayout frameSurfaceview;
    private TextView callTime;
    private LinearLayout llFunction;
    private ImageView doorlockImageicon;
    private ImageView tvScreenCapture;
    private ImageView btnSpeak;
    private TextView tvSpeak;
    private ImageView btnMute;
    private LinearLayout linearCaptureDefault;
    private ImageView btnHangupCall;
    private RelativeLayout glSurfaceView;
    private RtspSurfaceRender mRender;
    private ImageView backImg;

    private boolean isMuteFlag;
    private int volume;
    private io.socket.client.Socket socket;
    protected RtspClient mClient;
    private String uuid;
    private String deviceSSID;
    private String deviceVersion;
    private long time;
    //截屏，录屏相关
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private static final int REQUEST_MEDIA_PROJECTION = 1001;
    private MediaProjectionManager mMediaProjectionManager;

    private Timer timer;
    private AudioManager mAudioManager;
    private int currentVolume;
    private String authMode;
    private View mLoadingView;

    private ExecutorService mThreadPool;
    public static boolean printOptionEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏
        setContentView(R.layout.activity_videolock_call);
    }

    @Override
    protected void initView() {
        backImg = (ImageView) findViewById(R.id.back_img);
        glSurfaceView = (RelativeLayout) findViewById(R.id.surface);
        frameSurfaceview = (FrameLayout) findViewById(R.id.frame_surfaceview);
        callTime = (TextView) findViewById(R.id.call_time);
        llFunction = (LinearLayout) findViewById(R.id.ll_function);
        linearCaptureDefault = (LinearLayout) findViewById(R.id.linear_CaptureDefault);
        btnHangupCall = (ImageView) findViewById(R.id.btn_hangupCall);
        tvScreenCapture = (ImageView) findViewById(R.id.tv_screen_capture);
        btnSpeak = (ImageView) findViewById(R.id.btn_speak);
        tvSpeak = (TextView) findViewById(R.id.tv_speak);
        btnMute = (ImageView) findViewById(R.id.btn_mute);
        doorlockImageicon = (ImageView) findViewById(R.id.doorlock_imageicon);
        mLoadingView = findViewById(R.id.video_loading);
        //挂断
        btnHangupCall.setOnClickListener(this);
        //静音
        btnMute.setOnClickListener(this);
        //截图
        tvScreenCapture.setOnClickListener(this);
        //开锁
        doorlockImageicon.setOnClickListener(this);

        backImg.setOnClickListener(this);


    }

    @Override
    protected void initData() {
        uuid = getIntent().getStringExtra("deviceUuid");
        if (uuid == null) return;
        deviceSSID = getIntent().getStringExtra("networkSSID");
        deviceVersion = getIntent().getStringExtra("deviceVersion");
        authMode = getIntent().getStringExtra("authmode");
        createPresenter(new DoorLockVideoCallPresenter(this));
        if (deviceSSID == null || deviceVersion == null || authMode == null) {
            reqQueryDevice();
        }

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        try {
            socket = BaseApplication.getInstance().getSocket();
            socket.on("MSG_USER_START_PUSHFLOW_RSP", userStartPushflow);
            socket.on("MSG_USER_INITIATE_INTERCOM_RSP", userStopSpeaking);
            socket.on("MSG_DEVICE_START_PUSHFLOW_REQ", deviceStartPushflow);
            socket.on("MSG_USER_TRANSPORT_RSP", userTransportRsp);
            socket.on("MSG_USER_AUTH_RSP", userConfirmation);
            socket.on("MSG_DEVICE_TRANSPORT_REQ", deviceTransportRsp);
        } catch (Exception e) {
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager)
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }


        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //当前音量
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


        timer = new Timer();
        initTime();
        receiveDeviceStopPushflow();
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        long time = System.currentTimeMillis() / 1000;
        String phone = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        sTime = "/" + phone + time + ".sdp";
        startPushStream();

        //按住说话
        btnSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mClient.getStatus() != 200) {
                            startPushStream();
                        } else {
                            if (mSubsend == null && mClient != null && mClient.getRtpport() != 0) {
                                callSpeakerSetting();
                                userStartPushgflowRequest();
                            } else {
                                m_keep_running = true;
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        m_keep_running = false;

                        break;
                }
                return false;
            }
        });


    }


    protected AudioRecord m_in_rec;
    protected int m_in_buf_size;
    protected byte[] m_in_bytes;
    protected boolean m_keep_running = true;
    protected int mFrameSize = 160;
    private final int RtpheadSize = 12;
    long fristtime = 0;
    int seqn = 0;
    Subscription mSubsend;

    private byte[] processedData = new byte[mFrameSize / 2];

    private void callSpeakerSetting() {
        if (null != mClient) {
            mSubsend = Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    try {

                        Process.setThreadPriority(
                                Process.THREAD_PRIORITY_URGENT_AUDIO);
                        m_in_buf_size = AudioRecord.getMinBufferSize(8000,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT);
                        if (null == m_in_rec) {
                            m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                                    AudioFormat.CHANNEL_IN_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT,
                                    m_in_buf_size);
                        }

                        byte[] buffer = new byte[mFrameSize / 2 + RtpheadSize];
                        RtpPacket rtp_packet = new RtpPacket(buffer, 0);
                        m_in_bytes = new byte[mFrameSize];
                        Log.e("Saudioclient", "m_in_buf_size" + mFrameSize);
                        rtp_packet.setPayloadType(97);
                        if (0 == fristtime) {
                            fristtime = System.currentTimeMillis();
                        }
                        m_in_rec.startRecording();

                        while (true) {
                            byte[] packet = rtp_packet.getPacket();
                            int bytesRecord = m_in_rec.read(m_in_bytes, 0, mFrameSize);
                            long nowtime = System.currentTimeMillis();
                            if (bytesRecord < 0) {
                                continue;
                            }

                            if (bytesRecord != 0) {
                                short[] rawdata = Bytes2Shorts(m_in_bytes);
                                G711.linear2ulaw(rawdata, 0, processedData, mFrameSize / 2);
                                System.arraycopy(processedData, 0, packet, RtpheadSize, processedData.length);
                                rtp_packet.setSequenceNumber(seqn++);
                                rtp_packet.setTimestamp((nowtime - fristtime) * 8);
                                //发送数据
//                                    byte[] mTcpHeader = new byte[]{'$', 0, 0, 0};
//                                    mTcpHeader[1] = 0;
//                                    int len = packet.length;
//                                    mTcpHeader[2] = (byte) (len >> 8);
//                                    mTcpHeader[3] = (byte) (len & 0xFF);
//                                    Log.e("Saudioclient", "Saudioclient" + len);
                                if (m_keep_running) {
//                                        mClient.getOutputStream().write(mTcpHeader);
                                    DatagramPacket packaget = new DatagramPacket(packet, packet.length, mClient.getAddress(), mClient.getRtpport());
                                    mClient.getUdpSocket().send(packaget);
                                    Log.e("Saudioclient", "packaget");
//                                        mClient.getmOutputStream().write(packet,0,len);
//                                        mClient.getmOutputStream().flush();
                                } else {
//                                        byte[] mHeader = new byte[]{'$', 0, 0, 0};
//                                        mHeader[1] = 0;
//                                        int headerlen = 12;
//                                        mHeader[2] = (byte) (headerlen >> 8);
//                                        mHeader[3] = (byte) (headerlen & 0xFF);
//                                        mClient.getOutputStream().write(mHeader);
                                    byte[] header = new byte[12];
                                    System.arraycopy(packet, 0, header, 0, 12);
                                    DatagramPacket packaget = new DatagramPacket(header, header.length, mClient.getAddress(), mClient.getRtpport());
                                    mClient.getUdpSocket().send(packaget);
                                    Log.e("Saudioclient", "header");
//                                        mClient.getOutputStream().write(header);
//                                        mClient.getOutputStream().flush();
                                }

                            }


                        }


//                            if (null != m_in_rec) {
//                                m_in_rec.release();
//                                m_in_rec = null;
//                            }

                    } catch (Exception e) {
                        Log.e("Saudioclient", "Exception" + e.getMessage());
                        e.printStackTrace();
                    }

                }
            }).compose(TransformUtils.<Boolean>defaultSchedulers())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onNext(Boolean drawable) {

                        }

                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Saudioclient", "Exception" + e.getMessage());
                        }
                    });

        }
    }


    public short[] Bytes2Shorts(byte[] buf) {
        byte bLength = 2;
        short[] s = new short[buf.length / bLength];
        for (int iLoop = 0; iLoop < s.length; iLoop++) {
            byte[] temp = new byte[bLength];
            for (int jLoop = 0; jLoop < bLength; jLoop++) {
                temp[jLoop] = buf[iLoop * bLength + jLoop];
            }
            s[iLoop] = getShort(temp);
        }
        return s;
    }

    public short getShort(byte[] buf) {
        return getShort(buf, testCPU());
    }

    public boolean testCPU() {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            // System.out.println("is big ending");
            return true;
        } else {
            // System.out.println("is little ending");
            return false;
        }
    }


    public short getShort(byte[] buf, boolean bBigEnding) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 2) {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        short r = 0;
        if (bBigEnding) {
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        } else {
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        }

        return r;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                userStopSpeaking();
                break;

            //挂断
            case R.id.btn_hangupCall:
                userStopSpeaking();
                break;
            //静音
            case R.id.btn_mute:
                if (mAudioManager != null) {
                    isMuteFlag = !isMuteFlag;
                    if (isMuteFlag) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                        btnMute.setImageResource(R.mipmap.eques_mute_selected);
                    } else {
                        if (currentVolume != 0) {
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                        } else {
                            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                                    AudioManager.FX_FOCUS_NAVIGATION_UP);
                        }
                        btnMute.setImageResource(R.mipmap.eques_mute);
                    }
                }
                break;
            case R.id.break_icon_eques_call:
                isMuteFlag = !isMuteFlag;
                if (isMuteFlag) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    btnMute.setImageResource(R.mipmap.eques_mute_selected);
                } else {
                    if (currentVolume != 0) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    } else {
                        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                                AudioManager.FX_FOCUS_NAVIGATION_UP);
                    }
                    btnMute.setImageResource(R.mipmap.eques_mute);
                }
                break;

            //截屏
            case R.id.tv_screen_capture:
                RtspPlayerActivity.printOptionEnable = true;
//                Observable.create(new Observable.OnSubscribe<Boolean>() {
//                    @Override
//                    public void call(Subscriber<? super Boolean> subscriber) {
//                        if (null != mRender) {
//                            mRender.capture(new RtspSurfaceRender.CaptureCallback() {
//                                @Override
//                                public void onCapture(final File path) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            showToast("截图保存到" + path.getPath());
//                                        }
//                                    });
//
//                                }
//                            });
//
//                        }
//                    }
//                }).throttleFirst(1, TimeUnit.SECONDS) //防止重复点击
//                        .compose(TransformUtils.<Boolean>defaultSchedulers())
//                        .subscribe(new Observer<Boolean>() {
//                            @Override
//                            public void onNext(Boolean b) {
//
//                            }
//
//                            @Override
//                            public void onCompleted() {
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                ToastUtils.showShort("保存失败！");
//                            }
//                        });


//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    catchBitmap();
//                }else{
//                    showToast("手机版本过低，不支持截屏");
//                }
                break;
            //开锁
            case R.id.doorlock_imageicon:
                if (authMode != null) {
                    //单人模式
                    if (authMode.equals("0")) {
                        openDoorLock(null);
                    }
                    //双人模式
                    else if (authMode.equals("1")) {
                        showToast("双人模式,请输入第一位管理员密码!");
                        openDoorLock(null);
                    }
                }
                break;

        }
    }


    /**
     * 开始推音频流
     */
    private void startPushStream() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (null == mClient) {
                    mClient = new RtspClient();
                    mClient.setTransportMode(TRANSPORT_UDP);
                }
                mClient.setServerAddress("139.196.221.163", 10700);
                mClient.setStreamPath(sTime);
                mClient.startStream();

            }
        });
    }

    /**
     * 停止推送音频流
     */
    private void releasePushStream() {
        if (null != mClient) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mClient.stopStream();
                    mClient = null;
                }
            });
        }
    }

    private Subscription timerSub;

    private void initTime() {
        if (timerSub != null && !timerSub.isUnsubscribed()) {
            timerSub.unsubscribe();
        }
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
            }
        };
        timerSub = RxBus.getInstance()
                .toObservable(VideoTime.class)
                .compose(TransformUtils.<VideoTime>defaultSchedulers())
                .subscribe(new Action1<VideoTime>() {

                    @Override
                    public void call(VideoTime videoTime) {
                        if (timer != null)
                            timer.schedule(task, 1000, 1000);
                    }
                }, onErrorAction);
    }


    private int timeJianGe = 0;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    ++timeJianGe;
                    String timeFormat = getTime(timeJianGe);
                    callTime.setText(timeFormat);
                }
            });
        }
    };

    /**
     * 猫眼视频计时
     */
    public String getTime(int time) {
        String hFormat = null;
        String minutesFormat = null;
        String secondsFormat = null;
        int hour = time / 3600;
        if (hour < 10) {
            hFormat = "0" + String.format("%d", hour);
        } else {
            hFormat = String.format("%d", hour);
        }
        int minutes = time % 3600 / 60;
        if (minutes < 10) {
            minutesFormat = "0" + String.format("%d", minutes);
        } else {
            minutesFormat = String.format("%d", minutes);
        }
        int seconds = time % 60;
        if (seconds < 10) {
            secondsFormat = "0" + String.format("%d", seconds);
        } else {
            secondsFormat = String.format("%d", seconds);
        }

        String timeFormat = hFormat + ":" + minutesFormat + ":" + secondsFormat;
        return timeFormat;

    }

    /**
     * 请求查询设备信息
     */
    private void reqQueryDevice() {
        QueryDeviceuserlistReq body = new QueryDeviceuserlistReq();
        body.setVendor_name(FactoryType.GENERAL);
        body.setUuid(uuid);
        body.setShort_id(uuid);
        presenter.reqQueryDevice(body);
    }

    private void catchBitmap() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                takeScreenShot(RtspPlayerActivity.this);
            }
        }).throttleFirst(1, TimeUnit.SECONDS) //防止重复点击
                .compose(TransformUtils.<Boolean>defaultSchedulers())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(Boolean b) {

                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("保存失败！");
                    }
                });

    }

    private String savePath;

    public boolean saveBitmap(Bitmap bm) {
        String path = Api.getCamPath() + uuid + File.separator;
        boolean issuccess = FileUtils.makeDirs(path);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = df.format(new Date());
        File f = null;
        FileOutputStream out = null;
        if (issuccess) {
            savePath = StringUtils.join(path, format, ".jpg");
            f = new File(savePath);
        }
        if (f.exists()) {
            f.delete();
        }
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
        super.onConfigurationChanged(newConfig);
    }


    /**
     * 截屏
     */
    private void takeScreenShot(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
            LogUtil.e("RtspPlayerActivity", "截屏");

        } else {
            ToastUtils.showShort("当前手机系统版本低不支持");
        }

    }


    private Subscription DeviceStopPushflowsubscription;

    /**
     * 接收设备停止推流
     */
    private void receiveDeviceStopPushflow() {
        DeviceStopPushflowsubscription = RxBus.getInstance().toObservable(DeviceStopPushflowRequest.class).compose(TransformUtils.<DeviceStopPushflowRequest>defaultSchedulers()).subscribe(new Action1<DeviceStopPushflowRequest>() {
            @Override
            public void call(DeviceStopPushflowRequest response) {
                Log.e("接收设备", "设备停止推流");
                if (response == null) return;
                if (uuid.equals(response.getUuid())) {
                    Toast.makeText(RtspPlayerActivity.this, "设备停止通话", Toast.LENGTH_SHORT);
                    finish();
                }
            }
        });
    }

    private String sTime;

    /**
     * 用户开始推流
     */
    private void userStartPushgflowRequest() {
        UserStartPushflowRequest bean = new UserStartPushflowRequest();
        bean.setToken(AppContext.getToken());
        bean.setApi_version("1.0");
        bean.setVendor_name(FactoryType.GENERAL);
        bean.setUuid(uuid);
        String url = "rtsp://139.196.221.163:10700" + sTime;
        UserStartPushflowRequest.DataBean dataBean = new UserStartPushflowRequest.DataBean();
        dataBean.setApp_rtsp_url(url);
        bean.setData(dataBean);
        JSONObject jsonObject = null;
        try {
            String req = new Gson().toJson(bean);
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_START_PUSHFLOW_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户开始推流发送成功");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 用户挂断对讲
     */
    private void userStopSpeaking() {
        UserInitiateIntercomRequest bean = new UserInitiateIntercomRequest();
        bean.setToken(AppContext.getToken());
        bean.setApi_version("1.0");
        bean.setUuid(uuid);
        bean.setVendor_name(FactoryType.GENERAL);
        UserInitiateIntercomRequest.DataBean dataBean = new UserInitiateIntercomRequest.DataBean();
        dataBean.setAction("hangup");
        bean.setData(dataBean);
        JSONObject jsonObject = null;
        try {
            String req = new Gson().toJson(bean);
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_INITIATE_INTERCOM_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户挂断对讲发送成功");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finish();
    }


    private boolean transportTag = false;

    /**
     * 用户透传
     */
    private void userTransportReq(String pass) {
        transportTag = false;
        UserTransportRequest bean = new UserTransportRequest();
        bean.setUuid(uuid);
        bean.setApi_version("1.0");
        bean.setVendor_name(FactoryType.GENERAL);
        bean.setToken(AppContext.getToken());
        DistanceOpenDoorLock dodl = new DistanceOpenDoorLock();
        dodl.setTimestamp(String.valueOf(time));
        dodl.setCmd("REMOTE_UNLOCK");
        dodl.setUnlock_pwd(pass);
        String dodlreq = new Gson().toJson(dodl);
        String dodlreqresult = Base64Util.encode(dodlreq.getBytes());
        bean.setData(dodlreqresult);
        String req = new Gson().toJson(bean);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_TRANSPORT_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户透传发送成功");
                    transportTag = true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int needAddToken = 0;
    private Emitter.Listener userStartPushflow = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            final MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
            if (response != null && response.getReturn_string() != null) {
                Log.e("服务器发送", response.getReturn_string().contains("SUCCESS") + "");
                if (response.getReturn_string().contains("SUCCESS")) {
                    Log.e("服务器发送", "开始推流");
                    // startPushStream();
                }//需要验证
                else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                    comfirmTag = 1;
                    userConfirmationReq();
                }
                //token失效
                else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING") || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                    needAddToken = 1;
                    reqAddToken();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("服务器发送", "用户开始推流返回" + response.getReturn_string());
                        Toast.makeText(RtspPlayerActivity.this, "用户开始推流返回" + response.getReturn_string(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };


    private Emitter.Listener userStopSpeaking = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                    if (response != null && response.getReturn_string() != null) {
                        if (response.getReturn_string().contains("SUCCESS")) {
                            Log.e("服务器发送", "用户挂断对讲返回" + response.getReturn_string());
                            Toast.makeText(RtspPlayerActivity.this, "用户挂断对讲返回" + response.getReturn_string(), Toast.LENGTH_SHORT).show();
                            if (null != mRender)
                                mRender.onSurfaceDestoryed();
                            finish();
                        }
                        //需要验证
                        else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                            comfirmTag = 3;
                            userConfirmationReq();
                        }//token失效
                        else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING") || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                            needAddToken = 3;
                            reqAddToken();
                        }

                    } else {
                        finish();
                    }
                }
            });
        }
    };

    private String stsp_url;
    private Emitter.Listener deviceStartPushflow = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    DeviceStartPushflowRequest response = new Gson().fromJson(data.toString(), DeviceStartPushflowRequest.class);
                    if (response != null && uuid.equals(response.getUuid()) && response.getData() != null && response.getData().getDevice_rtsp_url() != null) {
                        if (stsp_url == null) {
                            stsp_url = response.getData().getDevice_rtsp_url();
                            play(stsp_url);
                            if (mClient.getStatus() == 200)
                                userStartPushgflowRequest();
                            VideoTime videoTime = new VideoTime();
                            RxBus.getInstance().post(videoTime);
                        } else if (stsp_url != null && !stsp_url.equals(response.getData().getDevice_rtsp_url())) {
                            stsp_url = response.getData().getDevice_rtsp_url();
                            play(stsp_url);
                            if (mClient.getStatus() == 200)
                                userStartPushgflowRequest();
                        }

                        Log.e("服务器发送", "播放地址:" + response.getData().getDevice_rtsp_url());

                    }
                    Toast.makeText(RtspPlayerActivity.this, "设备开始推流返回", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    private Emitter.Listener userTransportRsp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (transportTag) {
                        transportTag = false;
                        JSONObject data = (JSONObject) args[0];
                        MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                        if (response != null && "REMOTE_UNLOCK".equals(response.getCmd())) {
                            showToast("用户透传返回:" + response.getReturn_string());
                            if (response.getReturn_string().contains("SUCCESS")) {
                                Log.e("服务器发送", "用户透传返回:" + response.getReturn_string());
                            }
                            //需要验证
                            else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                                comfirmTag = 2;
                                userConfirmationReq();
                            }//token失效
                            else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING") || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                                needAddToken = 2;
                                reqAddToken();
                            }
                        }
                    }
                }
            });
        }
    };


    /**
     * 用户验证
     */
    private void userConfirmationReq() {
        UserAuthRequest request = new UserAuthRequest();
        request.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        request.setApi_version("1.0");
        request.setToken(AppContext.getToken());

        JSONObject jsonObject = null;
        try {
            String req = new Gson().toJson(request);
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_AUTH_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户验证发送成功");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private int comfirmTag = 0;
    private Emitter.Listener userConfirmation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                    Log.e("服务器发送", "用户验证返回:" + response.getReturn_string());
                    if (response.getReturn_string().contains("SUCCESS")) {
                        switch (comfirmTag) {
                            case 1:
                                comfirmTag = 0;
                                userStartPushgflowRequest();
                                break;
                            case 2:
                                comfirmTag = 0;
                                userTransportReq(aespasStr);
                                break;
                            case 3:
                                comfirmTag = 0;
                                userStopSpeaking();
                                break;
                        }
                    }
                }
            });
        }
    };


    private Emitter.Listener deviceTransportRsp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    DeviceTransportResponse response = new Gson().fromJson(data.toString(), DeviceTransportResponse.class);
                    if (response != null) {
                        if (response.getUuid() != null && response.getUuid().equals(uuid)) {
                            if (response.getData() != null) {
                                byte[] resData = Base64Util.decode(response.getData());
                                String resStrData = new String(resData);
                                JsonObject jsonData = new Gson().fromJson(resStrData, JsonObject.class);
                                if (jsonData != null) {
                                    String returnStr = jsonData.get("return_string").getAsString();
                                    String returnCmd = jsonData.get("cmd").getAsString();
                                    if ("RETURN_SUCCESS_OK_STRING".equals(returnStr)) {
                                        if ("REMOTE_UNLOCK".equals(returnCmd)) {
                                            showToast("开锁成功!");
                                        }
                                    } else {
                                        showToast("开锁失败!");
                                    }
                                } else {
                                    showToast("开锁失败!");
                                }
                            }
                        }
                    } else {
                        showToast("开锁失败!");
                    }
                }
            });
        }
    };


    GLSurfaceView mgl;

    void play(String url) {
        if (null == mgl) {
            mgl = new GLSurfaceView(this);
            mgl.setEGLContextClientVersion(3);
            mRender = new RtspSurfaceRender(mgl, this);
            mRender.setRtspUrl(url, Api.getCamPath() + uuid);
            mgl.setRenderer(mRender);
            mgl.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            glSurfaceView.addView(mgl);
        }
        llFunction.setVisibility(View.VISIBLE);

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if(null != mgl)
//            mgl.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(null != mgl)
//            mgl.onResume();
//    }


    @Override
    public void onBackPressed() {
        userStopSpeaking();

//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            rlEquesCall.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, AppUtil.dp2px(this, 250)));
//            surfaceView.getHolder().setFixedSize(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//            llFunction.setVisibility(View.VISIBLE);
//            rlCallButton.setVisibility(View.VISIBLE);
//            rightLinearEquesCall.setVisibility(View.GONE);
//        }

    }


    /**
     * 查询设备信息返回
     *
     * @param bean
     */
    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {
        if (bean == null) return;
        if (bean.getHeader().getHttp_code().equals("200")) {
            deviceSSID = bean.getBody().getNetwork_ssid();
            deviceVersion = bean.getBody().getVersion();
            authMode = bean.getBody().getAuth_mode();
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    @Override
    public void resAddDevice(BaseResponse bean) {

    }

    @Override
    public void resDeleteGateWay(BaseResponse bean) {

    }


    /**
     * 请求token
     */
    private void reqAddToken() {
        AddTokenReq addTokenReq = new AddTokenReq();
        addTokenReq.setAttitude("read");
        addTokenReq.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        addTokenReq.setSecret_key(AppContext.getInstance().getBodyBean().getSecret_key());
        presenter.reqAddToken(addTokenReq);
    }

    /**
     * token返回
     *
     * @param bean
     */
    @Override
    public void resAddToken(AddTokenResponse bean) {
        LogUtil.e("token", "" + bean.getBody().getToken());
        if ("200".equals(bean.getHeader().getHttp_code())) {
            if (bean.getBody() != null && bean.getBody().getToken() != null) {
                LogUtil.e("token", "" + bean.getBody().getToken());
                AppContext.setToken(bean.getBody().getToken());
                switch (needAddToken) {
                    case 1:
                        needAddToken = 0;
                        userStartPushgflowRequest();
                        break;
                    case 2:
                        needAddToken = 0;
                        userTransportReq(aespasStr);
                        break;
                    case 3:
                        needAddToken = 0;
                        userStopSpeaking();
                        break;
                }
            }
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    private Dialog dialog;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_10;
    private EditText et_password;

    /**
     * 开启门锁
     */
    private void openDoorLock(final String firstPass) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除对话框的标题
        dialog.setContentView(R.layout.unlock_password_dialog);
        // 在代码中设置界面大小的方法:
        Display display = getWindowManager().getDefaultDisplay();
        // 获取屏幕宽、高
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);// 对话框底对齐
        window.setBackgroundDrawableResource(R.drawable.news_home_dialog_border);
        ViewGroup.LayoutParams windowLayoutParams = window.getAttributes(); // 获取对话框当前的参数值
        windowLayoutParams.width = (int) (display.getWidth()); // 宽度设置为屏幕的0.85
        windowLayoutParams.height = (int) (display.getHeight() * 0.6); // 高度设置为屏幕的0.24
        dialog.show();
        dialog.setCancelable(false);// 点击外面和返回建无法隐藏
        et_password = (EditText) dialog.findViewById(R.id.et_password);
        btn_1 = (Button) dialog.findViewById(R.id.btn_1);
        btn_1.setText("1");
        btn_2 = (Button) dialog.findViewById(R.id.btn_2);
        btn_2.setText("2");
        btn_3 = (Button) dialog.findViewById(R.id.btn_3);
        btn_3.setText("3");
        btn_4 = (Button) dialog.findViewById(R.id.btn_4);
        btn_4.setText("4");
        btn_5 = (Button) dialog.findViewById(R.id.btn_5);
        btn_5.setText("5");
        btn_6 = (Button) dialog.findViewById(R.id.btn_6);
        btn_6.setText("6");
        btn_7 = (Button) dialog.findViewById(R.id.btn_7);
        btn_7.setText("7");
        btn_8 = (Button) dialog.findViewById(R.id.btn_8);
        btn_8.setText("8");
        btn_9 = (Button) dialog.findViewById(R.id.btn_9);
        btn_9.setText("9");
        btn_10 = (Button) dialog.findViewById(R.id.btn_10);
        btn_10.setText("0");

        // ========================================================================关闭键盘====================
        dialog.findViewById(R.id.btn_close).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.findViewById(R.id.iv_delete).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = et_password.getSelectionStart();
                        Editable editable = et_password.getText();
                        if (editable != null && index > 0) {
                            editable.delete(index - 1, index);
                        }
                    }
                });
        dialog.findViewById(R.id.btn_positive).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        singleOrMoreTypeOpenVideoDoor(firstPass);

                    }
                });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第1个按钮
                et_password.append(btn_1.getText().toString());
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第2个按钮
                et_password.append(btn_2.getText().toString());
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第3个按钮
                et_password.append(btn_3.getText().toString());
            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第4个按钮
                et_password.append(btn_4.getText().toString());
            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 对话框第5个按钮
                et_password.append(btn_5.getText().toString());
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第6个按钮
                et_password.append(btn_6.getText().toString());
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第7个按钮
                et_password.append(btn_7.getText().toString());
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第8个按钮
                et_password.append(btn_8.getText().toString());
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第9个按钮
                et_password.append(btn_9.getText().toString());
            }
        });
        btn_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第10个按钮
                et_password.append(btn_10.getText().toString());
            }
        });
    }

    private String aespasStr;

    private void singleOrMoreTypeOpenVideoDoor(String firstPass) {
        if (authMode != null) {
            //单人模式
            if (authMode.equals("0")) {
                // 对话框确定按钮
                final String password = et_password.getText().toString();
                LogUtil.e("加密", "输入密码str：" + password);
                if (password.length() == 6) {
                    String passStr = ByteStringUtil.myStringToString(password);
                    final byte[] passwd = ByteStringUtil.hexStringToBytes(passStr);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] key = aes256VideoDoorKey();
                            if (key == null) return;
                            String keyStr = ByteStringUtil.bytesToHexString(key);
                            LogUtil.e("加密", "秘钥：" + keyStr);
                            byte[] aesPas = aes256VideoDoorPass(passwd, key);
                            aespasStr = ByteStringUtil.bytesToHexString(aesPas);
                            LogUtil.e("加密", "密文：" + aespasStr);
                            if (aespasStr == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("开锁失败,请重试!");
                                    }
                                });
                                return;
                            }
                            userTransportReq(aespasStr);
                        }
                    }).start();
                    dialog.dismiss();
                    // //-----------------------------------------------华丽分割线------5.7
                } else if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort("密码不能为空!");
                } else if (password.length() < 7) {
                    ToastUtils.showShort("请输入6位密码!");
                }
            }
            //双人模式
            else if (authMode.equals("1")) {

                // 对话框确定按钮
                final String password = et_password.getText().toString();
                LogUtil.e("加密", "输入密码str：" + password);
                if (password.length() == 6) {
                    if (firstPass == null) {
                        String passStr = ByteStringUtil.myStringToString(password);
                        dialog.dismiss();
                        showToast("双人模式,请输入第二位管理员密码!");
                        openDoorLock(passStr);
                    } else {

                        String passStr = ByteStringUtil.myStringToString(password);
                        String resultPass = firstPass + "2c" + passStr;
                        final byte[] passwd = ByteStringUtil.hexStringToBytes(resultPass);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] key = aes256VideoDoorKey();
                                if (key == null) return;
                                String keyStr = ByteStringUtil.bytesToHexString(key);
                                LogUtil.e("加密", "秘钥：" + keyStr);
                                byte[] aesPas = aes256VideoDoorPass(passwd, key);
                                aespasStr = ByteStringUtil.bytesToHexString(aesPas);
                                LogUtil.e("加密", "密文：" + aespasStr);
                                if (aespasStr == null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast("开锁失败,请重试!");
                                        }
                                    });
                                    return;
                                }
                                userTransportReq(aespasStr);
                            }
                        }).start();
                        dialog.dismiss();
                    }

                    // //-----------------------------------------------华丽分割线------5.7
                } else if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort("密码不能为空!");
                } else if (password.length() < 7) {
                    ToastUtils.showShort("请输入6位密码!");
                }
            }
        }
    }

    private byte[] aes256VideoDoorKey() {
        byte[] aes = null;
        if (deviceSSID != null && deviceVersion != null) {

            String content = uuid + deviceSSID;
            if (content.length() < 32) {
                for (int i = content.length(); i < 32; i++) {
                    content = content + "0";
                }
            }
            byte[] contentByte;
            String content16Str = string2HexString(content).toUpperCase();
            LogUtil.e("加密", "第一次加密内容：" + content16Str);
            contentByte = hexStringToBytes(content16Str);
            int len = contentByte.length;
            byte[] doorPsd = new byte[32];
            //拼接加密前明文
            if (len > 32) {
                System.arraycopy(contentByte, 0, doorPsd, 0, 32);
            } else {
                System.arraycopy(contentByte, 0, doorPsd, 0, len);
            }

            //加密秘钥
            String key = "WONLYAPPOPENSMARTLOCKKEY@@@@2017";
            String key16Str = string2HexString(key).toUpperCase();
            LogUtil.e("加密", "第一次加密秘钥：" + key16Str);
            byte[] keyByte = hexStringToBytes(key16Str);

            try {
                aes = Aes256EncodeUtil.encrypt(doorPsd, keyByte);
            } catch (Exception e) {
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("参数不足,开锁失败!");
                }
            });
        }
        return aes;
    }

    private byte[] aes256VideoDoorPass(byte[] pas, byte[] key) {

        byte[] doorPsd = new byte[32];
        int pasLen = pas.length;
        time = System.currentTimeMillis() / 1000;
        String time16Str = Long.toHexString(time);
        byte[] timeByte = ByteStringUtil.hexStringToBytes(time16Str);
        //拼接加密前明文
        System.arraycopy(pas, 0, doorPsd, 0, pasLen);
        System.arraycopy(timeByte, 0, doorPsd, 28, 4);
        LogUtil.e("加密", "第二次加密内容：" + ByteStringUtil.bytesToHexString(doorPsd).toUpperCase());
        byte[] aes = null;
        try {
            aes = Aes256EncodeUtil.encrypt(doorPsd, key);
        } catch (Exception e) {
        }
        return aes;
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "用户取消了", Toast.LENGTH_SHORT).show();
                return;
            }

            int width = this.getWindowManager().getDefaultDisplay().getWidth();
            int height = this.getWindowManager().getDefaultDisplay()
                    .getHeight();

            final ImageReader mImageReader = ImageReader.newInstance(width, height, 0x1, 2);
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                    width, height, getResources().getDisplayMetrics().densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Image image = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                image = mImageReader.acquireLatestImage();
                            }
                            if (image == null) {
                                LogUtil.e("RtspPlayerActivity", "image == null");
                                return;
                            }
                            int width = 0;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                width = image.getWidth();
                                int height = image.getHeight();
                                final Image.Plane[] planes = image.getPlanes();
                                final ByteBuffer buffer = planes[0].getBuffer();
                                int pixelStride = planes[0].getPixelStride();
                                int rowStride = planes[0].getRowStride();
                                int rowPadding = rowStride - pixelStride * width;
                                Bitmap mBitmap;
                                mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                                mBitmap.copyPixelsFromBuffer(buffer);

                                // 获取状态栏高度
                                Rect frame = new Rect();
                                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

                                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height);

                                image.close();
                                if (mBitmap != null) {
                                    //拿到mitmap
                                    final Bitmap finalMBitmap = mBitmap;
                                    if (saveBitmap(finalMBitmap)) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtils.showShort("图片保存到" + savePath);
                                            }
                                        });

                                    }
                                    if (null != mBitmap) {
                                        mBitmap.recycle();
                                    }
                                    if (mVirtualDisplay != null)
                                        mVirtualDisplay.release();
                                    if (null != mMediaProjection)
                                        mMediaProjection.stop();

                                }
                            }

                        }
                    }).start();

                }
            }, 500);


        }
    }


    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void hide() {
        mLoadingView.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mRender)
            mRender.onSurfaceDestoryed();
        releasePushStream();
        if (socket != null) {
            socket.off("MSG_USER_START_PUSHFLOW_RSP", userStartPushflow);
            socket.off("MSG_USER_INITIATE_INTERCOM_RSP", userStopSpeaking);
            socket.off("MSG_DEVICE_START_PUSHFLOW_REQ", deviceStartPushflow);
            socket.off("MSG_USER_TRANSPORT_RSP", userTransportRsp);
            socket.off("MSG_USER_AUTH_RSP", userConfirmation);
            socket.off("MSG_DEVICE_TRANSPORT_REQ", deviceTransportRsp);
        }

        if (DeviceStopPushflowsubscription != null && !DeviceStopPushflowsubscription.isUnsubscribed()) {
            DeviceStopPushflowsubscription.unsubscribe();
        }

        if (timerSub != null && !timerSub.isUnsubscribed()) {
            timerSub.unsubscribe();
        }

        if (mSubsend != null && !mSubsend.isUnsubscribed()) {
            if (null != m_in_rec) {
                m_in_rec.release();
                m_in_rec = null;
            }
            mSubsend.unsubscribe();
        }

        if (timer != null) {
            timer.cancel();
        }
    }


}
