//package com.fbee.smarthome_wl.ui.videodoorlock;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.graphics.Rect;
//import android.hardware.display.DisplayManager;
//import android.hardware.display.VirtualDisplay;
//import android.media.AudioManager;
//import android.media.Image;
//import android.media.ImageReader;
//import android.media.projection.MediaProjection;
//import android.media.projection.MediaProjectionManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.RequiresApi;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Display;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.fbee.smarthome_wl.R;
//import com.fbee.smarthome_wl.api.Api;
//import com.fbee.smarthome_wl.base.BaseActivity;
//import com.fbee.smarthome_wl.base.BaseApplication;
//import com.fbee.smarthome_wl.common.AppContext;
//import com.fbee.smarthome_wl.constant.FactoryType;
//import com.fbee.smarthome_wl.constant.RequestCode;
//import com.fbee.smarthome_wl.event.VideoTime;
//import com.fbee.smarthome_wl.request.AddTokenReq;
//import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
//import com.fbee.smarthome_wl.request.videolockreq.DeviceStartPushflowRequest;
//import com.fbee.smarthome_wl.request.videolockreq.DeviceStopPushflowRequest;
//import com.fbee.smarthome_wl.request.videolockreq.UserAuthRequest;
//import com.fbee.smarthome_wl.request.videolockreq.UserInitiateIntercomRequest;
//import com.fbee.smarthome_wl.request.videolockreq.UserStartPushflowRequest;
//import com.fbee.smarthome_wl.request.videolockreq.UserTransportRequest;
//import com.fbee.smarthome_wl.request.videolockreq.transportreq.DistanceOpenDoorLock;
//import com.fbee.smarthome_wl.response.AddTokenResponse;
//import com.fbee.smarthome_wl.response.BaseResponse;
//import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
//import com.fbee.smarthome_wl.response.videolockres.DeviceTransportResponse;
//import com.fbee.smarthome_wl.response.videolockres.MnsBaseResponse;
//import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
//import com.fbee.smarthome_wl.utils.AppUtil;
//import com.fbee.smarthome_wl.utils.Base64Util;
//import com.fbee.smarthome_wl.utils.ByteStringUtil;
//import com.fbee.smarthome_wl.utils.FileUtils;
//import com.fbee.smarthome_wl.utils.LogUtil;
//import com.fbee.smarthome_wl.utils.PreferencesUtils;
//import com.fbee.smarthome_wl.utils.RxBus;
//import com.fbee.smarthome_wl.utils.ToastUtils;
//import com.fbee.smarthome_wl.utils.TransformUtils;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.videolan.libvlc.IVLCVout;
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
//import org.videolan.libvlc.streaming.RtspClient;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.TimeUnit;
//
//import io.socket.client.Ack;
//import io.socket.emitter.Emitter;
//import rx.Observable;
//import rx.Observer;
//import rx.Subscriber;
//import rx.Subscription;
//import rx.functions.Action1;
//
//import static com.fbee.smarthome_wl.R.id.right_linear_eques_call;
//import static com.fbee.smarthome_wl.R.id.rl_call_button;
//import static com.fbee.smarthome_wl.utils.ByteStringUtil.hexStringToBytes;
//import static com.fbee.smarthome_wl.utils.ByteStringUtil.string2HexString;
//import static org.videolan.libvlc.streaming.RtspClient.TRANSPORT_TCP;
//
//public class VideoPlayerActivity extends BaseActivity<DoorLockVideoCallContract.Presenter> implements DoorLockVideoCallContract.View {
//    private static final String TAG = VideoPlayerActivity.class.getSimpleName();
//    private SurfaceHolder surfaceHolder;
//    private LibVLC libvlc = null;
//    private MediaPlayer mediaPlayer = null;
//    private IVLCVout ivlcVout;
//    private Media media;
//
//
//    private LinearLayout relativeVideocall;
//    private FrameLayout frameSurfaceview;
//    private RelativeLayout rlEquesCall;
//    private SurfaceView surfaceView;
//    private LinearLayout rightLinearEquesCall;
//    private RelativeLayout breakIconEquesCall;
//    private ImageView imageFullscreenMute;
//    private RelativeLayout ivSpeak;
//    private ImageView imageFullscreenVoice;
//    private RelativeLayout ivScreenCapture;
//    private RelativeLayout ivHangupCall;
//    private RelativeLayout rlCallButton;
//    private TextView callTime;
//    private ImageView ivFullScreen;
//    private RelativeLayout isJihuoLayoutLiner;
//    private TextView isJihuoQuxiaoText;
//    private ImageView isJihuoDoorlockIcon;
//    private TextView isJihuoDoorlockTextState;
//    private Button speak;
//    private TextView tvDeviceName;
//    private LinearLayout doorlockLayoutLinear;
//    private TextView quxiaoText;
//    private RecyclerView doorlockRecycler;
//    private ImageView btnSpeakLock;
//    private TextView tvSpeak1;
//    private LinearLayout llFunction;
//    private LinearLayout linearCaptureDefault;
//    private ImageView btnHangupCall;
//    private ImageView tvScreenCapture;
//    private ImageView btnSpeak;
//    private TextView tvSpeak;
//    private ImageView btnMute;
//    private ImageView doorlockImageicon;
//    private boolean isMuteFlag;
//    private int volume;
//
//    private io.socket.client.Socket socket;
//    protected RtspClient mClient;
//    private String uuid;
//    private String deviceSSID;
//    private String deviceVersion;
//    private long time;
//    //截屏，录屏相关
//    private MediaProjection mMediaProjection;
//    private VirtualDisplay mVirtualDisplay;
//    private static final int REQUEST_MEDIA_PROJECTION = 1001;
//    private MediaProjectionManager mMediaProjectionManager;
//
//    private Timer timer;
//    private AudioManager mAudioManager;
//    private int currentVolume;
//    private String authMode;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_eques_call);
//    }
//
//    @Override
//    protected void initView() {
//        relativeVideocall = (LinearLayout) findViewById(R.id.relative_videocall);
//        frameSurfaceview = (FrameLayout) findViewById(R.id.frame_surfaceview);
//        rlEquesCall = (RelativeLayout) findViewById(R.id.rl_eques_call);
//        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
//        rightLinearEquesCall = (LinearLayout) findViewById(right_linear_eques_call);
//        breakIconEquesCall = (RelativeLayout) findViewById(R.id.break_icon_eques_call);
//        imageFullscreenMute = (ImageView) findViewById(R.id.image_fullscreen_mute);
//        ivSpeak = (RelativeLayout) findViewById(R.id.iv_speak);
//        imageFullscreenVoice = (ImageView) findViewById(R.id.image_fullscreen_voice);
//        ivScreenCapture = (RelativeLayout) findViewById(R.id.iv_screen_capture);
//        ivHangupCall = (RelativeLayout) findViewById(R.id.iv_hangupCall);
//        rlCallButton = (RelativeLayout) findViewById(rl_call_button);
//        callTime = (TextView) findViewById(R.id.call_time);
//        ivFullScreen = (ImageView) findViewById(R.id.iv_full_screen);
//        isJihuoLayoutLiner = (RelativeLayout) findViewById(R.id.is_jihuo_layout_liner);
//        isJihuoQuxiaoText = (TextView) findViewById(R.id.is_jihuo_quxiao_text);
//        isJihuoDoorlockIcon = (ImageView) findViewById(R.id.is_jihuo_doorlock_icon);
//        isJihuoDoorlockTextState = (TextView) findViewById(R.id.is_jihuo_doorlock_text_state);
//        speak = (Button) findViewById(R.id.speak);
//        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
//        doorlockLayoutLinear = (LinearLayout) findViewById(R.id.doorlock_layout_linear);
//        quxiaoText = (TextView) findViewById(R.id.quxiao_text);
//        doorlockRecycler = (RecyclerView) findViewById(R.id.doorlock_recycler);
//        btnSpeakLock = (ImageView) findViewById(R.id.btn_speak_lock);
//        tvSpeak1 = (TextView) findViewById(R.id.tv_speak1);
//        llFunction = (LinearLayout) findViewById(R.id.ll_function);
//        linearCaptureDefault = (LinearLayout) findViewById(R.id.linear_CaptureDefault);
//        btnHangupCall = (ImageView) findViewById(R.id.btn_hangupCall);
//        tvScreenCapture = (ImageView) findViewById(R.id.tv_screen_capture);
//        btnSpeak = (ImageView) findViewById(R.id.btn_speak);
//        tvSpeak = (TextView) findViewById(R.id.tv_speak);
//        btnMute = (ImageView) findViewById(R.id.btn_mute);
//        doorlockImageicon = (ImageView) findViewById(R.id.doorlock_imageicon);
//        //挂断
//        btnHangupCall.setOnClickListener(this);
//        ivHangupCall.setOnClickListener(this);
//        //静音
//        btnMute.setOnClickListener(this);
//        //截图
//        tvScreenCapture.setOnClickListener(this);
//        ivScreenCapture.setOnClickListener(this);
//        //开锁
//        doorlockImageicon.setOnClickListener(this);
//        ivFullScreen.setOnClickListener(this);
//
//
//    }
//
//    @Override
//    protected void initData() {
//        uuid=getIntent().getStringExtra("deviceUuid");
//        if(uuid==null)return;
//        deviceSSID=getIntent().getStringExtra("networkSSID");
//        deviceVersion=getIntent().getStringExtra("deviceVersion");
//        authMode=getIntent().getStringExtra("authmode");
//        createPresenter(new DoorLockVideoCallPresenter(this));
//        if(deviceSSID==null||deviceVersion==null||authMode==null){
//            reqQueryDevice();
//        }
//        try{
//            socket= BaseApplication.getInstance().getSocket();
//            socket.on("MSG_USER_START_PUSHFLOW_RSP",userStartPushflow);
//            socket.on("MSG_USER_INITIATE_INTERCOM_RSP",userStopSpeaking);
//            socket.on("MSG_DEVICE_START_PUSHFLOW_REQ",deviceStartPushflow);
//            socket.on("MSG_USER_TRANSPORT_RSP",userTransportRsp);
//            socket.on("MSG_USER_AUTH_RSP", userConfirmation);
//            socket.on("MSG_DEVICE_TRANSPORT_REQ",deviceTransportRsp);
//        }catch (Exception e){
//        }
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            mMediaProjectionManager = (MediaProjectionManager)
//                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        }
//        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        //当前音量
//        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//
//
//        timer = new Timer();
//        initTime();
//        receiveDeviceStopPushflow();
//
//        speak.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        callSpeakerSetting(true);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        callSpeakerSetting(false);
//                        break;
//                }
//                return false;
//            }
//        });
//
//        //按住说话
//        btnSpeak.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        callSpeakerSetting(true);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        callSpeakerSetting(false);
//                        break;
//                }
//                return false;
//            }
//        });
//
//        //按住说话
//        btnSpeakLock.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        callSpeakerSetting(true);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        callSpeakerSetting(false);
//                        break;
//                }
//                return false;
//            }
//        });
//        ivSpeak.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        imageFullscreenVoice.setImageResource(R.mipmap.full_screen_voice_press);
//                        callSpeakerSetting(true);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        imageFullscreenVoice.setImageResource(R.mipmap.full_screen_voice);
//                        callSpeakerSetting(false);
//                        break;
//                }
//                return false;
//            }
//        });
//
//
//    }
//
//    private void callSpeakerSetting(boolean b) {
//        if(null != mClient){
//            mClient.changeSpeak(b);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            //挂断
//            case R.id.btn_hangupCall:
//            case R.id.iv_hangupCall:
//                userStopSpeaking();
//                break;
//            //静音
//            case R.id.btn_mute:
//                if(mAudioManager !=null){
//                    isMuteFlag = !isMuteFlag;
//                    if(isMuteFlag){
//                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
//                        btnMute.setImageResource(R.mipmap.eques_mute_selected);
//                    }else{
//                        if(currentVolume != 0){
//                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
//                        }else{
//                            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
//                                    AudioManager.FX_FOCUS_NAVIGATION_UP);
//                        }
//                        btnMute.setImageResource(R.mipmap.eques_mute);
//                    }
//                }
//                break;
//            //截屏
//            case R.id.iv_screen_capture:
//            case R.id.tv_screen_capture:
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    catchBitmap();
//                }else{
//                    showToast("手机版本过低，不支持截屏");
//                }
//                break;
//            //开锁
//            case R.id.doorlock_imageicon:
//                if(authMode!=null){
//                    //单人模式
//                    if(authMode.equals("0")){
//                        openDoorLock(null);
//                    }
//                    //双人模式
//                    else if(authMode.equals("1")){
//                        showToast("双人模式,请输入第一位管理员密码!");
//                        openDoorLock(null);
//                    }
//                }
//                break;
//            case R.id.iv_full_screen:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                rlEquesCall.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
//                surfaceView.getHolder().setFixedSize(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                llFunction.setVisibility(View.GONE);
//                rlCallButton.setVisibility(View.GONE);
//                rightLinearEquesCall.setVisibility(View.VISIBLE);
//                if (isMuteFlag) {
//                    imageFullscreenMute.setImageResource(R.mipmap.full_screen_mute_press);
//                } else {
//                    imageFullscreenMute.setImageResource(R.mipmap.fullscreen_mute);
//                }
//                break;
//
//
//        }
//    }
//
//
//
//
//    /**
//     * 开始推音频流
//      */
//    private void startPushStream(){
//        if(null == mClient){
//            mClient = new RtspClient();
//            mClient.setTransportMode(TRANSPORT_TCP);
//        }
//        mClient.setServerAddress("192.168.1.12", 10700);
//        mClient.setStreamPath(sTime);
//        mClient.startStream();
//    }
//
//    /**
//     * 停止推送音频流
//     */
//    private void releasePushStream(){
//        if(null != mClient){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    mClient.stopStream();
//                    mClient=null;
//                }
//            }).start();
//        }
//    }
//
//    private Subscription timerSub;
//    private void initTime() {
//        if(timerSub!=null&&!timerSub.isUnsubscribed()){
//            timerSub.unsubscribe();
//        }
//        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
//            // onError()
//            @Override
//            public void call(Throwable throwable) {
//            }
//        };
//        timerSub=RxBus.getInstance()
//                .toObservable(VideoTime.class)
//                .compose(TransformUtils.<VideoTime>defaultSchedulers())
//                .subscribe(new Action1<VideoTime>() {
//
//                    @Override
//                    public void call(VideoTime videoTime) {
//                        if(timer !=null)
//                            timer.schedule(task, 1000, 1000);
//                    }
//                },onErrorAction);
//    }
//
//
//    private int timeJianGe = 0;
//    TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//
//            runOnUiThread(new Runnable() {      // UI thread
//                @Override
//                public void run() {
//                    ++timeJianGe;
//                    String timeFormat = getTime(timeJianGe);
//                    callTime.setText(timeFormat);
//                }
//            });
//        }
//    };
//
//    /**
//     * 猫眼视频计时
//     */
//    public String getTime(int time) {
//        String hFormat = null;
//        String minutesFormat = null;
//        String secondsFormat = null;
//        int hour = time / 3600;
//        if (hour < 10) {
//            hFormat = "0" + String.format("%d", hour);
//        } else {
//            hFormat = String.format("%d", hour);
//        }
//        int minutes = time % 3600 / 60;
//        if (minutes < 10) {
//            minutesFormat = "0" + String.format("%d", minutes);
//        } else {
//            minutesFormat = String.format("%d", minutes);
//        }
//        int seconds = time % 60;
//        if (seconds < 10) {
//            secondsFormat = "0" + String.format("%d", seconds);
//        } else {
//            secondsFormat = String.format("%d", seconds);
//        }
//
//        String timeFormat = hFormat + ":" + minutesFormat + ":" + secondsFormat;
//        return timeFormat;
//
//    }
//    /**
//     * 请求查询设备信息
//     */
//    private void reqQueryDevice(){
//        QueryDeviceuserlistReq body=new QueryDeviceuserlistReq();
//        body.setVendor_name(FactoryType.GENERAL);
//        body.setUuid(uuid);
//        body.setShort_id(uuid);
//        presenter.reqQueryDevice(body);
//    }
//
//    private void catchBitmap() {
//        Observable.create(new Observable.OnSubscribe<Boolean>() {
//            @Override
//            public void call(Subscriber<? super Boolean> subscriber) {
//                takeScreenShot(VideoPlayerActivity.this);
//            }
//        }).throttleFirst(1, TimeUnit.SECONDS) //防止重复点击
//                .compose(TransformUtils.<Boolean>defaultSchedulers())
//                .subscribe(new Observer<Boolean>() {
//            @Override
//            public void onNext(Boolean b) {
//
//            }
//            @Override
//            public void onCompleted() {
//            }
//            @Override
//            public void onError(Throwable e) {
//                ToastUtils.showShort("保存失败！");
//            }
//        });
//
//    }
//
//    private String savePath;
//    public boolean saveBitmap(Bitmap bm) {
//        String path = Api.getCamPath() + uuid + File.separator;
//        boolean issuccess = FileUtils.makeDirs(path);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String format = df.format(new Date());
//        File f =null;
//        FileOutputStream out =null;
//        if(issuccess){
//            savePath = StringUtils.join(path, format, ".jpg");
//            f= new File(savePath);
//        }
//        if (f.exists()) {
//            f.delete();
//        }
//        try {
//            out= new FileOutputStream(f);
//            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//            return true;
//        } catch (FileNotFoundException e) {
//            return false;
//        } catch (IOException e) {
//            return false;
//        }finally {
//            try{
//                if(out != null){
//                    out.close();
//                }
//            }catch(IOException e){
//            }
//        }
//
//    }
//
//
//
//
//    private void initPlayer(String url) {
//        ArrayList<String> options = new ArrayList<>();
//        options.add("--aout=opensles");
//        options.add("--audio-time-stretch");
//        options.add("-vvv");
//        //延时调试
////        options.add(":file-caching=300"); //文件缓存
////        options.add(":network-caching=1000"); //网络缓存
////        options.add(":sout-mux-caching=300");//输出缓存
////        options.add(":codec=mediacodec,iomx,all");
////        options.add(":demux=h264");
//        libvlc = new LibVLC(VideoPlayerActivity.this, options);
//        surfaceHolder = surfaceView.getHolder();
//        surfaceHolder.setKeepScreenOn(true);
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//            }
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                //竖屏
//                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    int sw = getWindow().getDecorView().getWidth();
//                    int sh = getWindow().getDecorView().getHeight();
//                    if (sw * sh == 0) {
//                        Log.e(TAG, "Invalid surface size");
//                        return;
//                    }
//                    mediaPlayer.getVLCVout().setWindowSize(sw, dip2px(VideoPlayerActivity.this, 300));
//                    mediaPlayer.setAspectRatio(sw + ":" + dip2px(VideoPlayerActivity.this, 300));
//                    mediaPlayer.setScale(0);
//                    holder.setFixedSize(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//                }
//                //横屏
//                else {
//                    int sw = getWindow().getDecorView().getWidth();
//                    int sh = getWindow().getDecorView().getHeight();
//                    if (sw * sh == 0) {
//                        Log.e(TAG, "Invalid surface size");
//                        return;
//                    }
//                    mediaPlayer.getVLCVout().setWindowSize(sw, sh);
//                    mediaPlayer.setAspectRatio(sw+":"+sh);
//                    mediaPlayer.setScale(0);
//                    holder.setFixedSize(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                }
//            }
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//            }
//        });
//        mediaPlayer = new MediaPlayer(libvlc);
//        mediaPlayer.setEventListener(new MediaPlayer.EventListener() {
//            @Override
//            public void onEvent(MediaPlayer.Event event) {
//                switch (event.type) {
//                    case MediaPlayer.Event.Buffering:
//                        break;
//                    case MediaPlayer.Event.Playing:
////                        Log.e(TAG, "onEvent: playing...");
//                        //videoLoading.setVisibility(View.GONE);
//                        break;
//                    case MediaPlayer.Event.EncounteredError:
//                        Log.e(TAG, "onEvent: error...");
//                        //videoLoading.setVisibility(View.GONE);
//                        mediaPlayer.stop();
//                        Toast.makeText(VideoPlayerActivity.this, "播放出错！", Toast.LENGTH_LONG).show();
//                        break;
//                }
//            }
//        });
//        // take live555 as RTSP server
//        media = new Media(libvlc, Uri.parse(url));
//
//        int cache = 1000;
//        media.addOption(":network-caching=1000");
//        media.addOption(":file-caching=300" );
//        media.addOption(":live-cacheing=300" );
//        media.addOption(":sout-mux-caching" + cache);
//        media.addOption(":codec=mediacodec,iomx,all");
//
//        mediaPlayer.setMedia(media);
//        ivlcVout = mediaPlayer.getVLCVout();
//        ivlcVout.setVideoView(surfaceView);
//        ivlcVout.attachViews();
//        ivlcVout.addCallback(new IVLCVout.Callback() {
//            @Override
//            public void onSurfacesCreated(IVLCVout vlcVout) {
//                int sw = getWindow().getDecorView().getWidth();
//                int sh = getWindow().getDecorView().getHeight();
//                if (sw * sh == 0) {
//                    Log.e(TAG, "Invalid surface size");
//                    return;
//                }
//                mediaPlayer.getVLCVout().setWindowSize(sw, dip2px(VideoPlayerActivity.this, 300));
//                mediaPlayer.setAspectRatio(sw + ":" + dip2px(VideoPlayerActivity.this, 300));
//                mediaPlayer.setScale(0);
//
//            }
//
//            @Override
//            public void onSurfacesDestroyed(IVLCVout vlcVout) {
//            }
//        });
//        mediaPlayer.play();
//        volume=mediaPlayer.getVolume();
//    }
//
//
//
//    /**
//     * 截屏
//     */
//    private  void takeScreenShot(Activity activity) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(),
//                    REQUEST_MEDIA_PROJECTION);
//            LogUtil.e("VideoPlayerActivity","截屏");
//
//        }else{
//            ToastUtils.showShort("当前手机系统版本低不支持");
//        }
//
//    }
//
//
//
//    private Subscription DeviceStopPushflowsubscription;
//    /**
//     * 接收设备停止推流
//     */
//    private void receiveDeviceStopPushflow(){
//        DeviceStopPushflowsubscription = RxBus.getInstance().toObservable(DeviceStopPushflowRequest.class).compose(TransformUtils.<DeviceStopPushflowRequest>defaultSchedulers()).subscribe(new Action1<DeviceStopPushflowRequest>() {
//            @Override
//            public void call(DeviceStopPushflowRequest response) {
//                Log.e("接收设备","设备停止推流");
//                Toast.makeText(VideoPlayerActivity.this,"设备停止通话",Toast.LENGTH_SHORT);
//                finish();
//            }
//        });
//    }
//
//    private String sTime;
//    /**
//     * 用户开始推流
//     */
//    private void  userStartPushgflowRequest(){
//        UserStartPushflowRequest bean=new UserStartPushflowRequest();
//        bean.setToken(AppContext.getToken());
//        bean.setApi_version("1.0");
//        bean.setVendor_name(FactoryType.GENERAL);
//        bean.setUuid(uuid);
//        long time=System.currentTimeMillis()/1000;
//        String phone= PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
//        sTime="/"+ phone+time+".sdp";
//        String url="rtsp://192.168.1.12:10700"+sTime;
//        UserStartPushflowRequest.DataBean dataBean=new UserStartPushflowRequest.DataBean();
//        dataBean.setApp_rtsp_url(url);
//        bean.setData(dataBean);
//        JSONObject jsonObject=null;
//        try {
//            String req=new Gson().toJson(bean);
//            jsonObject = new JSONObject(req);
//            socket.emit("MSG_USER_START_PUSHFLOW_REQ", jsonObject, new Ack() {
//                @Override
//                public void call(Object... objects) {
//                    Log.e("服务器发送","用户开始推流发送成功");
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 用户挂断对讲
//     */
//    private void userStopSpeaking(){
//        UserInitiateIntercomRequest bean=new UserInitiateIntercomRequest();
//        bean.setToken(AppContext.getToken());
//        bean.setApi_version("1.0");
//        bean.setUuid(uuid);
//        bean.setVendor_name(FactoryType.GENERAL);
//        UserInitiateIntercomRequest.DataBean dataBean=new UserInitiateIntercomRequest.DataBean();
//        dataBean.setAction("hangup");
//        bean.setData(dataBean);
//        JSONObject jsonObject=null;
//        try {
//            String req=new Gson().toJson(bean);
//            jsonObject = new JSONObject(req);
//            socket.emit("MSG_USER_INITIATE_INTERCOM_REQ", jsonObject, new Ack() {
//                @Override
//                public void call(Object... objects) {
//                    Log.e("服务器发送","用户挂断对讲发送成功");
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private boolean transportTag =false;
//    /**
//     * 用户透传
//     */
//    private void userTransportReq(String pass){
//        transportTag =false;
//        UserTransportRequest bean=new UserTransportRequest();
//        bean.setUuid(uuid);
//        bean.setApi_version("1.0");
//        bean.setVendor_name(FactoryType.GENERAL);
//        bean.setToken(AppContext.getToken());
//        DistanceOpenDoorLock dodl=new DistanceOpenDoorLock();
//        dodl.setTimestamp(String.valueOf(time));
//        dodl.setCmd("REMOTE_UNLOCK");
//        dodl.setUnlock_pwd(pass);
//        String dodlreq=new Gson().toJson(dodl);
//        String dodlreqresult= Base64Util.encode(dodlreq.getBytes());
//        bean.setData(dodlreqresult);
//        String req=new Gson().toJson(bean);
//        JSONObject jsonObject=null;
//        try {
//            jsonObject = new JSONObject(req);
//            socket.emit("MSG_USER_TRANSPORT_REQ", jsonObject, new Ack() {
//                @Override
//                public void call(Object... objects) {
//                    Log.e("服务器发送","用户透传发送成功");
//                    transportTag=true;
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//    private int needAddToken=0;
//    private Emitter.Listener userStartPushflow = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            JSONObject data = (JSONObject) args[0];
//            final MnsBaseResponse response= new Gson().fromJson(data.toString(), MnsBaseResponse.class);
//            if(response!=null&&response.getReturn_string()!=null){
//                Log.e("服务器发送",response.getReturn_string().contains("SUCCESS")+"");
//                if(response.getReturn_string().contains("SUCCESS")){
//                    Log.e("服务器发送","开始推流");
//                    startPushStream();
//                }//需要验证
//                else if(response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")){
//                    comfirmTag=1;
//                    userConfirmationReq();
//                }
//                //token失效
//                else if(response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING")||response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")){
//                    needAddToken=1;
//                    reqAddToken();
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("服务器发送","用户开始推流返回"+response.getReturn_string());
//                        Toast.makeText(VideoPlayerActivity.this,"用户开始推流返回"+response.getReturn_string(),Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }
//    };
//
//
//    private Emitter.Listener userStopSpeaking = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    MnsBaseResponse response= new Gson().fromJson(data.toString(), MnsBaseResponse.class);
//                    if(response!=null&&response.getReturn_string()!=null){
//                       if(response.getReturn_string().contains("SUCCESS")){
//                           finish();
//                           Log.e("服务器发送","用户挂断对讲返回"+response.getReturn_string());
//                           Toast.makeText(VideoPlayerActivity.this,"用户挂断对讲返回"+response.getReturn_string(),Toast.LENGTH_SHORT).show();
//                       }
//                       //需要验证
//                       else if(response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")){
//                           comfirmTag=3;
//                           userConfirmationReq();
//                       }//token失效
//                       else if(response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING")||response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")){
//                           needAddToken=3;
//                           reqAddToken();
//                       }
//
//                    }else{
//                        finish();
//                    }
//                }
//            });
//        }
//    };
//
//
//    private Emitter.Listener deviceStartPushflow = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    DeviceStartPushflowRequest response= new Gson().fromJson(data.toString(), DeviceStartPushflowRequest.class);
//                    if(response!=null&&response.getData()!=null&&response.getData().getDevice_rtsp_url()!=null){
//                        Log.e("服务器发送","播放地址:"+response.getData().getDevice_rtsp_url());
//                        VideoTime videoTime = new VideoTime();
//                        RxBus.getInstance().post(videoTime);
//
////                        initPlayer("rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov");
//                        initPlayer(response.getData().getDevice_rtsp_url());
//                        userStartPushgflowRequest();
//                    }
//                    Log.e("服务器发送","设备开始推流返回:"+response.toString());
//                    Toast.makeText(VideoPlayerActivity.this,"设备开始推流返回",Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    };
//
//
//    private Emitter.Listener userTransportRsp = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(transportTag){
//                        transportTag=false;
//                        JSONObject data = (JSONObject) args[0];
//                        MnsBaseResponse response= new Gson().fromJson(data.toString(), MnsBaseResponse.class);
//                        if(response!=null){
//                            showToast("用户透传返回:"+response.getReturn_string());
//                            if(response.getReturn_string().contains("SUCCESS")){
//                                Log.e("服务器发送","用户透传返回:"+response.getReturn_string());
//                            }
//                            //需要验证
//                            else if(response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")){
//                                comfirmTag=2;
//                                userConfirmationReq();
//                            }//token失效
//                            else if(response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING")||response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")){
//                                needAddToken=2;
//                                reqAddToken();
//                            }
//                        }else{
//                            showToast("开锁失败!");
//                        }
//                    }
//                }
//            });
//        }
//    };
//
//
//
//    /**
//     * 用户验证
//     */
//    private void userConfirmationReq(){
//        UserAuthRequest request=new UserAuthRequest();
//        request.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
//        request.setApi_version("1.0");
//        request.setToken(AppContext.getToken());
//
//        JSONObject jsonObject=null;
//        try {
//            String req=new Gson().toJson(request);
//            jsonObject = new JSONObject(req);
//            socket.emit("MSG_USER_AUTH_REQ", jsonObject, new Ack() {
//                @Override
//                public void call(Object... objects) {
//                    Log.e("服务器发送","用户验证发送成功");
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//    private int comfirmTag=0;
//    private Emitter.Listener userConfirmation = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    MnsBaseResponse response= new Gson().fromJson(data.toString(), MnsBaseResponse.class);
//                    Log.e("服务器发送","用户验证返回:"+response.getReturn_string());
//                    if(response.getReturn_string().contains("SUCCESS")){
//                        switch (comfirmTag){
//                            case 1:
//                                comfirmTag=0;
//                                userStartPushgflowRequest();
//                                break;
//                            case 2:
//                                comfirmTag=0;
//                                userTransportReq(aespasStr);
//                                break;
//                            case 3:
//                                comfirmTag=0;
//                                userStopSpeaking();
//                                break;
//                        }
//                    }
//                }
//            });
//        }
//    };
//
//
//    private Emitter.Listener deviceTransportRsp = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                    JSONObject data = (JSONObject) args[0];
//                    DeviceTransportResponse response= new Gson().fromJson(data.toString(), DeviceTransportResponse.class);
//                    if(response!=null){
//                        if(response.getUuid()!=null&&response.getUuid().equals(uuid)){
//                            if(response.getData()!=null){
//                                byte[] resData= Base64Util.decode(response.getData());
//                                String resStrData= new String(resData);
//                                JsonObject jsonData= new Gson().fromJson(resStrData,JsonObject.class);
//                                if(jsonData!=null){
//                                    String returnStr=  jsonData.get("return_string").getAsString();
//                                    String returnCmd=jsonData.get("cmd").getAsString();
//                                    if("RETURN_SUCCESS_OK_STRING".equals(returnStr)){
//                                        if("REMOTE_UNLOCK".equals(returnCmd)){
//                                            showToast("开锁成功!");
//                                        }
//                                    }else{
//                                        showToast("开锁失败!");
//                                    }
//                                }else{
//                                    showToast("开锁失败!");
//                                }
//                            }
//                        }
//                    }else{
//                        showToast("开锁失败!");
//                    }
//                }
//            });
//        }
//    };
//
//
//    /**
//     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
//     */
//    private  int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if(mediaPlayer!=null){
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.pause();
//            }
//        }
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(null != mediaPlayer)
//            mediaPlayer.play();
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if(null != ivlcVout){
//            ivlcVout.setVideoView(surfaceView);
//            ivlcVout.attachViews();
//        }
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mediaPlayer != null){
//            mediaPlayer.stop();
//            mediaPlayer.getVLCVout().detachViews();
//        }
//
//    }
//    @Override
//    public void onBackPressed() {
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            userStopSpeaking();
//            finish();
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            rlEquesCall.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, AppUtil.dp2px(this, 300)));
//            surfaceView.getHolder().setFixedSize(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//            llFunction.setVisibility(View.VISIBLE);
//            rlCallButton.setVisibility(View.VISIBLE);
//            rightLinearEquesCall.setVisibility(View.GONE);
//        }
//
//    }
//
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            if(libvlc!=null){
//                libvlc.release();
//            }
//
//        }
//
//        releasePushStream();
//        if(socket!=null){
//            socket.off("MSG_USER_START_PUSHFLOW_RSP",userStartPushflow);
//            socket.off("MSG_USER_INITIATE_INTERCOM_RSP",userStopSpeaking);
//            socket.off("MSG_DEVICE_START_PUSHFLOW_REQ",deviceStartPushflow);
//            socket.off("MSG_USER_TRANSPORT_RSP",userTransportRsp);
//            socket.off("MSG_USER_AUTH_RSP", userConfirmation);
//            socket.off("MSG_DEVICE_TRANSPORT_REQ",deviceTransportRsp);
//        }
//
//        if(DeviceStopPushflowsubscription!=null&&!DeviceStopPushflowsubscription.isUnsubscribed()){
//            DeviceStopPushflowsubscription.unsubscribe();
//        }
//
//        if(timerSub!=null&&!timerSub.isUnsubscribed()){
//            timerSub.unsubscribe();
//        }
//        if (timer != null) {
//            timer.cancel();
//        }
//    }
//
//    /**
//     * 查询设备信息返回
//     * @param bean
//     */
//    @Override
//    public void resQueryDevice(QueryDeviceUserResponse bean) {
//        if(bean==null)return;
//        if(bean.getHeader().getHttp_code().equals("200")){
//            deviceSSID=bean.getBody().getNetwork_ssid();
//            deviceVersion=bean.getBody().getVersion();
//            authMode=bean.getBody().getAuth_mode();
//        }else{
//            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
//        }
//    }
//
//    @Override
//    public void resAddDevice(BaseResponse bean) {
//
//    }
//
//    @Override
//    public void resDeleteGateWay(BaseResponse bean) {
//
//    }
//
//
//    /**
//     * 请求token
//     */
//    private void reqAddToken(){
//        AddTokenReq addTokenReq=new AddTokenReq();
//        addTokenReq.setAttitude("read");
//        addTokenReq.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
//        addTokenReq.setSecret_key(AppContext.getInstance().getBodyBean().getSecret_key());
//        presenter.reqAddToken(addTokenReq);
//    }
//
//    /**
//     * token返回
//     * @param bean
//     */
//    @Override
//    public void resAddToken(AddTokenResponse bean) {
//        LogUtil.e("token",""+bean.getBody().getToken());
//        if ("200".equals(bean.getHeader().getHttp_code())){
//            if(bean.getBody()!=null&&bean.getBody().getToken()!=null){
//                LogUtil.e("token",""+bean.getBody().getToken());
//                AppContext.setToken(bean.getBody().getToken());
//                switch (needAddToken){
//                    case 1:
//                        needAddToken=0;
//                        userStartPushgflowRequest();
//                        break;
//                    case 2:
//                        needAddToken=0;
//                        userTransportReq(aespasStr);
//                        break;
//                    case 3:
//                        needAddToken=0;
//                        userStopSpeaking();
//                        break;
//                }
//            }
//        }else{
//            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
//        }
//    }
//
//    private Dialog dialog;
//    private Button btn_1;
//    private Button btn_2;
//    private Button btn_3;
//    private Button btn_4;
//    private Button btn_5;
//    private Button btn_6;
//    private Button btn_7;
//    private Button btn_8;
//    private Button btn_9;
//    private Button btn_10;
//    private EditText et_password;
//    /**
//     * 开启门锁
//     */
//    private void openDoorLock(final String firstPass) {
//        dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除对话框的标题
//        dialog.setContentView(R.layout.unlock_password_dialog);
//        // 在代码中设置界面大小的方法:
//        Display display = getWindowManager().getDefaultDisplay();
//        // 获取屏幕宽、高
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.BOTTOM);// 对话框底对齐
//        window.setBackgroundDrawableResource(R.drawable.news_home_dialog_border);
//        ViewGroup.LayoutParams windowLayoutParams = window.getAttributes(); // 获取对话框当前的参数值
//        windowLayoutParams.width = (int) (display.getWidth()); // 宽度设置为屏幕的0.85
//        windowLayoutParams.height = (int) (display.getHeight() * 0.6); // 高度设置为屏幕的0.24
//        dialog.show();
//        dialog.setCancelable(false);// 点击外面和返回建无法隐藏
//        et_password = (EditText) dialog.findViewById(R.id.et_password);
//        btn_1 = (Button) dialog.findViewById(R.id.btn_1);
//        btn_1.setText("1");
//        btn_2 = (Button) dialog.findViewById(R.id.btn_2);
//        btn_2.setText("2");
//        btn_3 = (Button) dialog.findViewById(R.id.btn_3);
//        btn_3.setText("3");
//        btn_4 = (Button) dialog.findViewById(R.id.btn_4);
//        btn_4.setText("4");
//        btn_5 = (Button) dialog.findViewById(R.id.btn_5);
//        btn_5.setText("5");
//        btn_6 = (Button) dialog.findViewById(R.id.btn_6);
//        btn_6.setText("6");
//        btn_7 = (Button) dialog.findViewById(R.id.btn_7);
//        btn_7.setText("7");
//        btn_8 = (Button) dialog.findViewById(R.id.btn_8);
//        btn_8.setText("8");
//        btn_9 = (Button) dialog.findViewById(R.id.btn_9);
//        btn_9.setText("9");
//        btn_10 = (Button) dialog.findViewById(R.id.btn_10);
//        btn_10.setText("0");
//
//        // ========================================================================关闭键盘====================
//        dialog.findViewById(R.id.btn_close).setOnClickListener(
//                new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//        dialog.findViewById(R.id.iv_delete).setOnClickListener(
//                new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        int index = et_password.getSelectionStart();
//                        Editable editable = et_password.getText();
//                        if (editable != null && index > 0) {
//                            editable.delete(index - 1, index);
//                        }
//                    }
//                });
//        dialog.findViewById(R.id.btn_positive).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        singleOrMoreTypeOpenVideoDoor(firstPass);
//
//                    }
//                });
//
//        btn_1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第1个按钮
//                et_password.append(btn_1.getText().toString());
//            }
//        });
//        btn_2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第2个按钮
//                et_password.append(btn_2.getText().toString());
//            }
//        });
//        btn_3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第3个按钮
//                et_password.append(btn_3.getText().toString());
//            }
//        });
//        btn_4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第4个按钮
//                et_password.append(btn_4.getText().toString());
//            }
//        });
//        btn_5.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 对话框第5个按钮
//                et_password.append(btn_5.getText().toString());
//            }
//        });
//        btn_6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第6个按钮
//                et_password.append(btn_6.getText().toString());
//            }
//        });
//        btn_7.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第7个按钮
//                et_password.append(btn_7.getText().toString());
//            }
//        });
//        btn_8.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第8个按钮
//                et_password.append(btn_8.getText().toString());
//            }
//        });
//        btn_9.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第9个按钮
//                et_password.append(btn_9.getText().toString());
//            }
//        });
//        btn_10.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 对话框第10个按钮
//                et_password.append(btn_10.getText().toString());
//            }
//        });
//    }
//
//    private String aespasStr;
//    private void singleOrMoreTypeOpenVideoDoor(String firstPass){
//        if(authMode!=null){
//            //单人模式
//            if(authMode.equals("0")){
//                // 对话框确定按钮
//                final String password = et_password.getText().toString();
//                LogUtil.e("加密","输入密码str："+password);
//                if (password.length() == 6) {
//                    String passStr=ByteStringUtil.myStringToString(password);
//                    final byte[] passwd = ByteStringUtil.hexStringToBytes(passStr);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            byte[] key=aes256VideoDoorKey();
//                            if(key==null) return;
//                            String keyStr=ByteStringUtil.bytesToHexString(key);
//                            LogUtil.e("加密","秘钥："+keyStr);
//                            byte[] aesPas=aes256VideoDoorPass(passwd,key);
//                            aespasStr=ByteStringUtil.bytesToHexString(aesPas);
//                            LogUtil.e("加密","密文："+aespasStr);
//                            if(aespasStr==null){
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        showToast("开锁失败,请重试!");
//                                    }
//                                });
//                                return;
//                            }
//                            userTransportReq(aespasStr);
//                        }
//                    }).start();
//                    dialog.dismiss();
//                    // //-----------------------------------------------华丽分割线------5.7
//                } else if (TextUtils.isEmpty(password)) {
//                    ToastUtils.showShort("密码不能为空!");
//                } else if (password.length() < 7) {
//                    ToastUtils.showShort("请输入6位密码!");
//                }
//            }
//            //双人模式
//            else if(authMode.equals("1")){
//
//                // 对话框确定按钮
//                final String password = et_password.getText().toString();
//                LogUtil.e("加密","输入密码str："+password);
//                if (password.length() == 6) {
//                    if(firstPass==null){
//                        String passStr=ByteStringUtil.myStringToString(password);
//                        dialog.dismiss();
//                        showToast("双人模式,请输入第二位管理员密码!");
//                        openDoorLock(passStr);
//                    }else{
//
//                        String passStr=ByteStringUtil.myStringToString(password);
//                        String resultPass=firstPass+"2c"+passStr;
//                        final byte[] passwd = ByteStringUtil.hexStringToBytes(resultPass);
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                byte[] key=aes256VideoDoorKey();
//                                if(key==null) return;
//                                String keyStr=ByteStringUtil.bytesToHexString(key);
//                                LogUtil.e("加密","秘钥："+keyStr);
//                                byte[] aesPas=aes256VideoDoorPass(passwd,key);
//                                aespasStr=ByteStringUtil.bytesToHexString(aesPas);
//                                LogUtil.e("加密","密文："+aespasStr);
//                                if(aespasStr==null){
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            showToast("开锁失败,请重试!");
//                                        }
//                                    });
//                                    return;
//                                }
//                                userTransportReq(aespasStr);
//                            }
//                        }).start();
//                        dialog.dismiss();
//                    }
//
//                    // //-----------------------------------------------华丽分割线------5.7
//                } else if (TextUtils.isEmpty(password)) {
//                    ToastUtils.showShort("密码不能为空!");
//                } else if (password.length() < 7) {
//                    ToastUtils.showShort("请输入6位密码!");
//                }
//            }
//        }
//    }
//
//    private byte[] aes256VideoDoorKey(){
//        byte[] aes=null;
//        if(deviceSSID!=null&&deviceVersion!=null){
//
//            String content=uuid+deviceSSID+deviceVersion;
//            byte[] contentByte;
//            String content16Str= string2HexString(content).toUpperCase();
//            LogUtil.e("加密","第一次加密内容："+content16Str);
//            contentByte = hexStringToBytes(content16Str);
//            int len=contentByte.length;
//            byte[] doorPsd = new byte[32];
//            //拼接加密前明文
//            if(len>32){
//                System.arraycopy(contentByte, 0, doorPsd, 0, 32);
//            }else {
//                System.arraycopy(contentByte, 0, doorPsd, 0, len);
//            }
//
//            //加密秘钥
//            String key="WONLYAPPOPENSMARTLOCKKEY@@@@2017";
//            String key16Str= string2HexString(key).toUpperCase();
//            LogUtil.e("加密","第一次加密秘钥："+key16Str);
//            byte[] keyByte= hexStringToBytes(key16Str);
//
//            try{
//                aes= Aes256EncodeUtil.encrypt(doorPsd, keyByte);
//            }catch (Exception e){
//            }
//        }else{
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    showToast("参数不足,开锁失败!");
//                }
//            });
//        }
//        return aes;
//    }
//
//    private byte[] aes256VideoDoorPass(byte[] pas,byte[] key){
//
//        byte[] doorPsd = new byte[32];
//        int pasLen=pas.length;
//        time=System.currentTimeMillis()/1000;
//        String time16Str=Long.toHexString(time);
//        byte[] timeByte=ByteStringUtil.hexStringToBytes(time16Str);
//        //拼接加密前明文
//        System.arraycopy(pas, 0, doorPsd, 0, pasLen);
//        System.arraycopy(timeByte, 0, doorPsd, 28, 4);
//        LogUtil.e("加密","第二次加密内容："+ByteStringUtil.bytesToHexString(doorPsd).toUpperCase());
//        byte[] aes=null;
//        try{
//            aes= Aes256EncodeUtil.encrypt(doorPsd, key);
//        }catch (Exception e){
//        }
//        return aes;
//    }
//    @Override
//    public void hideLoading() {
//
//    }
//
//    @Override
//    public void showLoadingDialog() {
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == REQUEST_MEDIA_PROJECTION) {
//            if (resultCode != Activity.RESULT_OK) {
//                Toast.makeText(this, "用户取消了", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            int width = this.getWindowManager().getDefaultDisplay().getWidth();
//            int height = this.getWindowManager().getDefaultDisplay()
//                    .getHeight();
//
//            final ImageReader mImageReader = ImageReader.newInstance(width,height, 0x1, 2);
//            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
//            mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
//                    width, height, getResources().getDisplayMetrics().densityDpi,
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                    mImageReader.getSurface(), null, null);
//
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Image image = null;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                                image = mImageReader.acquireLatestImage();
//                            }
//                            if (image == null) {
//                                LogUtil.e("VideoPlayerActivity","image == null");
//                                return;
//                            }
//                            int width = 0;
//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                                width = image.getWidth();
//                                int height = image.getHeight();
//                                final Image.Plane[] planes = image.getPlanes();
//                                final ByteBuffer buffer = planes[0].getBuffer();
//                                int pixelStride = planes[0].getPixelStride();
//                                int rowStride = planes[0].getRowStride();
//                                int rowPadding = rowStride - pixelStride * width;
//                                Bitmap mBitmap;
//                                mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
//                                mBitmap.copyPixelsFromBuffer(buffer);
//
//                                // 获取状态栏高度
//                                Rect frame = new Rect();
//                                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//                                int statusBarHeight = frame.top;
//
//                                int buttomH = rlCallButton.getHeight();
//                                int titleH = AppUtil.dp2px(VideoPlayerActivity.this, 300);
//                                // 去掉标题栏
//                                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//                                    mBitmap = Bitmap.createBitmap(mBitmap, 0, statusBarHeight, width,titleH - buttomH);
//                                }else{
//                                    mBitmap = Bitmap.createBitmap(mBitmap, 0, statusBarHeight,width ,height-statusBarHeight );
//                                }
//
//                                image.close();
//                                if (mBitmap != null) {
//                                    //拿到mitmap
//                                    final Bitmap finalMBitmap = mBitmap;
//                                    if(saveBitmap(finalMBitmap)){
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                ToastUtils.showShort("图片保存到"+savePath);
//                                            }
//                                        });
//
//                                    }
//                                    if(null !=mBitmap ){
//                                        mBitmap.recycle();
//                                    }
//                                    if(mVirtualDisplay  !=null)
//                                        mVirtualDisplay.release();
//                                    if(null != mMediaProjection)
//                                        mMediaProjection.stop();
//
//                                }
//                            }
//
//                        }
//                    }).start();
//
//                }
//            },500);
//
//
//        }
//    }
//
//
//
//
//
//
//
//}
