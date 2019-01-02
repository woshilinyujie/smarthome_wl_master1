/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fbee.smarthome_wl.ui.corecode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.event.GateSnidEvent;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.service.LocationService;
import com.fbee.smarthome_wl.ui.corecode.camera.CameraManager;
import com.fbee.smarthome_wl.ui.corecode.decode.DecodeThread;
import com.fbee.smarthome_wl.ui.corecode.utils.CaptureActivityHandler;
import com.fbee.smarthome_wl.ui.corecode.utils.InactivityTimer;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.AES256Encryption;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RGBLuminanceSource;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.UriToPathUtil;
import com.fbee.smarthome_wl.widget.pop.PopwindowChoose;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

import static com.fbee.smarthome_wl.base.BaseCommonPresenter.mSeqid;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;


/**
 * @author Created by ZhaoLi.Wang
 * @Description: TODO 二维码扫描界面
 * @date 2016-11-29 下午2:37:24
 */
public final class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener, BaseRecylerAdapter.OnItemClickLitener {


    public static final int CAPTURE = 32151;
    private static final String TAG = CaptureActivity.class.getSimpleName();

    private LinearLayout linearLayout;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;


    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;
    private ImageView ivScanSwitch;
    private TextView tvScanSwitch;

    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private AlertDialog alertDialog;

    private Rect mCropRect = null;
    private String passwd;
    private String user;
    private PopwindowChoose popwindow;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    private boolean isHasSurface = false;
    private final int REQUEST_CODE = 1001;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_capture_code);
        boolean isPermission = getIntent().getBooleanExtra("isPermission", false);
        if (isPermission) {
            showToast("请在手机应用权限管理中打开王力智能的摄像头权限。");
        }
        initGPS();
        initApi();
        initPopup();
        addGateWay();
        Intent intent = new Intent();
        intent.setClass(this, LocationService.class);
        startService(intent);
        initview();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(CaptureActivity.this, "请打开GPS",
                    Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
//			 弹出Toast
//            Toast.makeText(CaptureActivity.this, "GPS is ready",
//                    Toast.LENGTH_LONG).show();
//          // 弹出对话框
//          new AlertDialog.Builder(this).setMessage("GPS is ready")
//                  .setPositiveButton("OK", null).show();
        }
    }

    public void initview() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        title.setText("二维码扫描");
        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setOnClickListener(this);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        linearLayout = (LinearLayout) findViewById(R.id.layout_scan_switch);
        ivScanSwitch = (ImageView) findViewById(R.id.iv_scan_switch);
        tvScanSwitch = (TextView) findViewById(R.id.tv_scan_switch);
        ImageView mButtonBack = (ImageView) findViewById(R.id.back);
        mButtonBack.setVisibility(View.VISIBLE);
        mButtonBack.setOnClickListener(this);
        inactivityTimer = new InactivityTimer(this);
        linearLayout.setOnClickListener(this);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.capture_anim);
        scanLine.startAnimation(animation);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
//        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     * 获取扫描结果
     *
     * @param rawResult The contents of the barcode. 条形码的内容
     * @param bundle    The extras 额外信息，二维码的缩略图
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();

        playBeepSoundAndVibrate();
        String resultString = rawResult.getText();
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(CaptureActivity.this, "扫描失败!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (getIntent().hasExtra("ISADD")) {
            try {
                user = resultString.split("GT")[1].split("pass")[0];
                if (null != user) {
                    while (user.startsWith("0")) {
                        user = user.substring(1);
                    }
                } else {
                    ToastUtils.showShort("扫描失败");
                }
                passwd = resultString.split("GT")[1].split("pass")[1];
                if (user != null && passwd.length() > 0) {
                    loginFbee(user, passwd);

                } else {
                    ToastUtils.showShort("扫描失败");
                }
            } catch (Exception e) {
                ToastUtils.showShort("扫描失败");
            }
        }else if (getIntent().hasExtra(AppContext.JIUCAI_NAME)){
            Intent resultIntent = new Intent();
            Bundle setbundle = new Bundle();
            setbundle.putString("result", resultString);
            resultIntent.putExtras(setbundle);
            this.setResult(77, resultIntent);
            CaptureActivity.this.finish();
        }else {
            Intent resultIntent = new Intent();
            Bundle setbundle = new Bundle();
            setbundle.putString("result", resultString);
            resultIntent.putExtras(setbundle);
            this.setResult(88, resultIntent);
            CaptureActivity.this.finish();
        }


    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }
            initCrop();
        } catch (Exception e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        Toast.makeText(CaptureActivity.this, "请在手机应用权限管理中打开王力智能的摄像头权限。", Toast.LENGTH_LONG).show();
    }

    /**
     * 重新启动二维码扫描功能。参数为秒值
     */
    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //打开或关闭闪关灯
            case R.id.layout_scan_switch:
                if (cameraManager != null)
                    cameraManager.switchFlash(ivScanSwitch, tvScanSwitch);
                break;
            case R.id.back:
                this.finish();
                break;
            //打开相册
            case R.id.iv_right_menu:
                if (popwindow != null && popwindow.isShowing()) {
                    colsePop();
                } else {
                    showPop();
                }
                break;


        }
    }

    private void initPopup() {
        if (popwindow == null) {
            ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();

            PopwindowChoose.Menu pop = new PopwindowChoose.Menu(R.mipmap.add, "相册获取");
            menulist.add(pop);
            if(!getIntent().hasExtra(AppContext.JIUCAI_NAME)){
                PopwindowChoose.Menu pop1 = new PopwindowChoose.Menu(R.mipmap.add, "手动输入");
                menulist.add(pop1);
            }
            popwindow = new PopwindowChoose(this, menulist, this, this, true);
            popwindow.setAnimationStyle(R.style.popwin_anim_style);
        }
//        tvRightMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (popwindow != null && popwindow.isShowing()) {
//                    colsePop();
//                } else {
//                    showPop();
//                }
//
//            }
//
//        });

    }

    //解析二维码图片,返回结果封装在Result对象中
    private com.google.zxing.Result parseQRcodeBitmap(String bitmapPath) {
        try {
            //解析转换类型UTF-8
            Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            //获取到待解析的图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            //如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
            //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
            options.inJustDecodeBounds = true;
            //此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
            Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
            //我们现在想取出来的图片的边长（二维码图片是正方形的）设置为400像素
            /**
             options.outHeight = 400;
             options.outWidth = 400;
             options.inJustDecodeBounds = false;
             bitmap = BitmapFactory.decodeFile(bitmapPath, options);
             */
            //以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
            options.inSampleSize = options.outHeight / 400;
            if (options.inSampleSize <= 0) {
                options.inSampleSize = 1; //防止其值小于或等于0
            }
            /**
             * 辅助节约内存设置
             * options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_8888
             * options.inPurgeable = true;
             * options.inInputShareable = true;
             */
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(bitmapPath, options);
            //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
            RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
            //将图片转换成二进制图片
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
            //初始化解析对象
            QRCodeReader reader = new QRCodeReader();
            //开始解析
            Result result = null;
            result = reader.decode(binaryBitmap, hints);

            return result;
        } catch (Exception e) {
            return null;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imgPath = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
//                    String[] proj = new String[]{MediaStore.Images.Media.DATA};
//                    Cursor cursor = CaptureActivity.this.getContentResolver().query(data.getData(), proj, null, null, null);
//
//                    if(cursor.moveToFirst()){
//                        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                        System.out.println(columnIndex);
//                        //获取到用户选择的二维码图片的绝对路径
//                        imgPath = cursor.getString(columnIndex);
//                    }
//                    cursor.close();

                    Uri uri = data.getData();
                    if ("Xiaomi".equals(Build.MANUFACTURER)) {
                        uri = UriToPathUtil.geturi(data, CaptureActivity.this);
                    }
                    imgPath = UriToPathUtil.getImageAbsolutePath(this, uri);

                    //获取解析结果
                    Result ret = parseQRcodeBitmap(imgPath);
                    if (ret == null) {
                        Toast.makeText(getApplicationContext(), "图片解析出错，请重新拍摄", Toast.LENGTH_LONG).show();
                    } else if (getIntent().hasExtra("ISADD")) {
                        user = ret.toString().split("GT")[1].split("pass")[0];
                        if (null != user) {
                            while (user.startsWith("0")) {
                                user = user.substring(1);
                            }
                        } else {
                            ToastUtils.showShort("扫描失败");
                        }
                        String passwd = ret.toString().split("GT")[1].split("pass")[1];
                        if (user != null && passwd.length() > 0) {
                            loginFbee(user, passwd);

                        } else {
                            ToastUtils.showShort("扫描失败");
                        }

                    } else {
                        Intent resultIntent = new Intent();
                        Bundle setbundle = new Bundle();
                        setbundle.putString("result", ret.toString());
                        resultIntent.putExtras(setbundle);
                        this.setResult(88, resultIntent);
                        CaptureActivity.this.finish();
                    }

                    break;

                default:
                    break;
            }
        }

    }

    private Serial mSerial;

    public void loginFbee(final String username, final String psw) {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
//                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                mSerial = AppContext.getInstance().getSerialInstance();
                //释放资源
                mSerial.releaseSource();
                //登录
                int ret = mSerial.connectRemoteZll(username, psw);
                //请求网关
                mSerial.getGateWayInfo();
                subscriber.onNext(ret);
                subscriber.onCompleted();
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("登录失败");
                    }

                    @Override
                    public void onNext(Integer ret) {

                        switch (ret) {
                            case 1:
                                ToastUtils.showShort("登录成功");
                                //请求网关
                                List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                                for (int i = 0; i < gateway_list.size(); i++) {
                                    if (username.equals(gateway_list.get(i).getUsername())) {
                                        return;
                                    }
                                }
                                Api.jpushSetting(CaptureActivity.this, user);
                                icvss.equesLogin(CaptureActivity.this, EquesConfig.SERVER_ADDRESS, username, EquesConfig.APPKEY);
                                mSerial.getGateWayInfo();
                                break;
                            case -4:
                                showDialog("网关登录人数已上限!");
                                break;
                            case -2:
                                showDialog("网关账号或密码错误!");
                                break;
                            case -3:
                                showDialog("网关登录超时！");
                                break;

                        }

                    }
                });

    }

    private void addGateWay() {
        Subscription gateWaySubscription = RxBus.getInstance().toObservable(GateSnidEvent.class)
                .compose(TransformUtils.<GateSnidEvent>defaultSchedulers())
                .subscribe(new Action1<GateSnidEvent>() {
                    @Override
                    public void call(GateSnidEvent event) {
                        if (null == user) return;
                        AddGateWayReq bodyBean = new AddGateWayReq();
                        bodyBean.setVendor_name(FactoryType.FBEE);
                        bodyBean.setUuid(event.snid.toLowerCase());
                        bodyBean.setUsername(user);
                        bodyBean.setPassword(AES256Encryption.encrypt(passwd, event.snid.toLowerCase()));
                        bodyBean.setAuthorization("admin");
                        bodyBean.setNote(user);
                        bodyBean.setVersion(AppContext.getVer());

                        //存储当前网关
                        LoginResult.BodyBean.GatewayListBean gw = new LoginResult.BodyBean.GatewayListBean();
                        LoginResult.BodyBean bean = new LoginResult.BodyBean();
                        gw.setUsername(user);
                        gw.setPassword(bodyBean.getPassword());
                        gw.setAuthorization("admin");
                        gw.setVendor_name(FactoryType.FBEE);
                        gw.setUuid(AppContext.getGwSnid());
                        gw.setVersion(AppContext.getVer());
                        //保存最后登录的网关信息
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                        List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                        if (gateway_list == null) {
                            gateway_list = new ArrayList<LoginResult.BodyBean.GatewayListBean>();
                        }
                        gateway_list.add(gw);
                        bean.setGateway_list(gateway_list);
                        AppContext.getInstance().setBodyBean(bean);


                        AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();
                        location.setCountries(AppContext.getMcountryName());
                        location.setProvince(AppContext.getMadminArea());
                        location.setCity(AppContext.getMlocality());
                        location.setPartition(AppContext.getMsubLocality());
                        location.setStreet(AppContext.getMfeatureName());
                        bodyBean.setLocation(location);
                        addGateway(bodyBean);

                    }
                });
        mCompositeSubscription.add(gateWaySubscription);
    }

    //添加网关
    public void addGateway(AddGateWayReq bodyBean) {

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showToast("添加网关失败，请重新扫描添加");
            }

            @Override
            public void onCompleted() {
                super.onCompleted();

            }

            @Override
            public void onNext(JsonObject json) {
                hideLoadingDialog();
                if (null != json) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    BaseResponse info = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                    if ("200".equals(info.getHeader().getHttp_code())) {
                        startActivity(new Intent(CaptureActivity.this, MainActivity.class));
                        finish();
                    } else {
                        showToast("添加网关失败，请重新扫描添加");
                    }
                }
            }
        });


        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_GATEWAY_ADD_REQ");
        header.setSeq_id(mSeqid.getAndIncrement() + "");
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyBean);
        ums.setUMS(umsbean);
        Subscription sub = mApiWrapper.addGateWay(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 0:
                Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
                if (Build.VERSION.SDK_INT < 19) {
                    innerIntent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                CaptureActivity.this.startActivityForResult(wrapperIntent, REQUEST_CODE);
                colsePop();
                break;
            case 1:
                showLoginDialog();
                colsePop();
                break;
        }

    }


    private void showLoginDialog() {
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_login_user_psd, null);
        TextView nameTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        nameTitle.setText("请输入网关账号及密码");
        final EditText usernameEdit = (EditText) dialogView.findViewById(R.id.username_edit_login);
        final EditText passWordEdit = (EditText) dialogView.findViewById(R.id.password_edit_login);
        TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);

        confirmText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                user = usernameEdit.getText().toString();
                passwd = passWordEdit.getText().toString();
                if (!user.isEmpty() && !passwd.isEmpty()) {
                    Intent resultIntent = new Intent();
                    Bundle setbundle = new Bundle();
                    setbundle.putString("username", user);
                    setbundle.putString("password", passwd);
                    resultIntent.putExtras(setbundle);
                    setResult(66, resultIntent);
                    user = null;
                    CaptureActivity.this.finish();
                } else {
                    showToast("请输入帐号或密码");
                }
            }
        });
        cancleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        alertDialog = customizeDialog.show();
    }


    private void colsePop() {
        if (popwindow != null && popwindow.isShowing())
            popwindow.dismiss();
    }

    private void showPop() {
        if (popwindow != null && !popwindow.isShowing()) {

            View view = popwindow.mContentView;
            //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
            int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
            int[] location = new int[2];

            // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
            ivRightMenu.getLocationOnScreen(location);
            //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
            popwindow.showAtLocation(ivRightMenu, Gravity.NO_GRAVITY, (location[0] + ivRightMenu.getWidth() / 2) - popupWidth / 2, location[1] + ivRightMenu.getHeight());

        }

    }
}