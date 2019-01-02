package com.fbee.smarthome_wl.ui.videodoorlock;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.videodoorlock.MySpinnerSdapter;
import com.fbee.smarthome_wl.adapter.videodoorlock.VideoLockAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.bean.AddVideoLockInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.videolockreq.DeviceInfoUpdateRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserBindDeviceRequest;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.videolockres.MnsBaseResponse;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.WifiAutoConnectManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoDoorlockWifisActivity extends BaseActivity<VideoDoorlockWifisContract.Presenter> implements VideoDoorlockWifisContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ListView lv;
    private LinearLayout linerSearch;
    private TextView tvSearchTitle;
    private LinearLayout linerSearchIn;
    private LinearLayout linerNetIn;

    private List<ScanResult> scanResult;
    private List<String> wifiScanResult;
    private VideoLockAdapter adapter;
    // 线程池
    private ExecutorService mThreadPool;
    private static final int WIFI_SCAN_PERMISSION_CODE = 100;
    private WifiManager wifiManager;
    private WifiAutoConnectManager wifiAutoConnectManager;
    private io.socket.client.Socket socket;
    private String nowWifiIp;
    private Subscription subscription;
    private String pass;
    private String itemSSID = null;
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;
    //139.196.221.163
    //211.140.24.250
    private String das_addr;
    private String das_port;
    private String result03;
    private String result01;
    private String result02;
    private int portSk = 10000;
    private String chanel = "6";
    private int Port = 80;
    private DhcpInfo dhcpInfo;
    private MySpinnerSdapter spinnerSdapter;
    private ScanResult doorLockItemData;
    private String firstLocalSSID;
    private boolean isSearch=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_doorlock_wifis);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        lv = (ListView) findViewById(R.id.lv);
        linerSearch = (LinearLayout) findViewById(R.id.liner_search);
        tvSearchTitle = (TextView) findViewById(R.id.tv_search_title);
        linerSearchIn = (LinearLayout) findViewById(R.id.liner_search_in);
        linerNetIn = (LinearLayout) findViewById(R.id.liner_net_in);
        createPresenter(new VideoDoorlockWifisPresenter(this));

    }

    @Override
    protected void initData() {
        das_addr=getIntent().getStringExtra("serviceIp");
        das_port=getIntent().getStringExtra("servicePort");
        if(das_addr==null||das_port==null)return;
        showCloseDialog();
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("门锁wifi");
        scanResult = new ArrayList<>();
        wifiScanResult = new ArrayList<>();
        try{
            //获取定位权限
            getLocationPermission();

            // 初始化线程池
            mThreadPool = Executors.newCachedThreadPool();
            registerBroadcast();
            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);    //获得系统wifi服务
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            firstLocalSSID = AppUtil.getLocalWifiSSID(wifiManager);
            LogUtil.e("当前wifi名称", "ssid: " + firstLocalSSID);
            //<unknown ssid>
            //开始扫描
            wifiManager.startScan();

            //showLoadingDialog("正在搜索设备,请稍后...");
            checkWifiIsExist();
            wifiAutoConnectManager = new WifiAutoConnectManager(wifiManager);
        }catch (Exception e){
            showToast("请允许打开位置权限!");
            finish();
            e.printStackTrace();
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doorLockItemData = scanResult.get(position);
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                    return;
                }

                changeWifi();
            }
        });
        adapter = new VideoLockAdapter(this, scanResult);
        lv.setAdapter(adapter);


        try {
            socket = BaseApplication.getInstance().getSocket();
            socket.on("MSG_USER_AUTH_RSP", userConfirmation);
            socket.on("MSG_USER_BIND_DEVICE_RSP", userBindDevice);
            socket.on("MSG_DEVICE_INFO_UPDATE_REQ", deviceInfoUpdate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (result01 != null) {
                            Toast.makeText(VideoDoorlockWifisActivity.this, "一次返回成功" + result01, Toast.LENGTH_SHORT).show();
                            //version:%s,mac_addr:%s,
                            Log.e("发送返回", "1:" + result01);
                            LogUtil.e("发送返回","itemSSID:"+itemSSID+"  pass:"+pass);
                            sendMsg02(itemSSID, pass);
                        } else {
                            showErrorDialog(null);
                        }
                        break;
                    case 2:
                        if (result02 != null) {
                            if (result02.contains("200") || result02.contains("OK")) {
                                Toast.makeText(VideoDoorlockWifisActivity.this, "二次返回成功" + result02, Toast.LENGTH_SHORT).show();
                                sentMsg03();
                            } else {
                                showErrorDialog(null);
                            }
                            Log.e("发送返回", "2:" + result02);

                        } else {
                            showErrorDialog(null);
                        }
                        break;
                    case 3:
                        if (result03 != null) {
                            if (result03.contains("200") || result03.contains("OK")) {
                                Toast.makeText(VideoDoorlockWifisActivity.this, "三次返回成功" + result03, Toast.LENGTH_SHORT).show();
                                checkNetIsAvilable();
                            } else {
                                showErrorDialog(null);
                            }
                            Log.e("发送返回", "3:" + result03);
                        } else {
                            showErrorDialog(null);
                        }
                        break;
                }
            }
        };
    }
    private AlertDialog alertDialog;
    private void showCloseDialog() {
        alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("如果您的手机移动网络处于开启状态,请手动关闭手机移动网络连接,否则配网肯能会出错").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
    private void changeWifi(){
        if (nowWifiIsDoorLock()) {
            String nowWifiSSID = getNowWifiSSID();
            if (doorLockItemData.SSID.equals(nowWifiSSID)) {
                getNowWifiIp();
            } else {
                wifiAutoConnectManager.connect(doorLockItemData.SSID, "", WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS);
            }
        } else {
            wifiAutoConnectManager.connect(doorLockItemData.SSID, "", WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS);
        }
        showLoadingDialog("手机正在切换到设备WIFI,请稍后...");
        checkNetConnection(doorLockItemData.SSID);
    }



    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 获取wifi连接需要定位权限,没有获取权限
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                }, WIFI_SCAN_PERMISSION_CODE);

            }
        }

    }


    private Subscription checkNetSubscription;
    /**
     * 判断当前手机是否连接网络
     */
    private void checkNetIsAvilable(){
        if (checkNetSubscription != null && !checkNetSubscription.isUnsubscribed()) {
            checkNetSubscription.unsubscribe();
        }
        checkNetSubscription = Observable.interval(8, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {

                       if(!AppUtil.isNetworkAvailable(VideoDoorlockWifisActivity.this)){
                            ToastUtils.showShort("请检查网络,确保网络连接");
                       }else{
                           if(nowWifiIsDoorLock()){
                               wifiManager.setWifiEnabled(false);
                               wifiManager.setWifiEnabled(true);
                           }else {
                               if (checkNetSubscription != null && !checkNetSubscription.isUnsubscribed()) {
                                   checkNetSubscription.unsubscribe();
                               }
                           }
                       }
                    }
                });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case WIFI_SCAN_PERMISSION_CODE:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // 权限被用户同意，可以做你要做的事情了。
//                } else {
//                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
//                    showToast("请允许打开位置权限!");
//                    finish();
//                }
//                break;
//        }
//    }


//    /**
//     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
//     * @param context
//     * @return true 表示开启
//     */
//    public static final boolean isOPen(final Context context) {
//        LocationManager locationManager
//                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (gps || network) {
//            return true;
//        }
//
//        return false;
//    }

    /**
     * 注册广播，接收wifi网络状态变化
     */
    private void registerBroadcast() {

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkWifiIsConnectReceiver, intentfilter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);


    }

    private Subscription checkSubscription;
    private AlertDialog checkAlertDialog;
    /**
     * 十秒钟计时确定是否有门锁wifi
     */
    private void checkWifiIsExist() {

//        checkAlertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("未找到设备，请确认设备已打开配网模式，全新设备上电后会自动处于配网模式3分钟").setNegativeButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                checkAlertDialog.dismiss();
//            }
//        }).setCancelable(false).create();

        checkSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong >10) {
                            if (scanResult.size() == 0) {
                                //hideLoadingDialog();
                                if (checkSubscription != null && !checkSubscription.isUnsubscribed()) {
                                    checkSubscription.unsubscribe();
                                }
                                showErrorDialog("未找到设备，请确认设备已打开配网模式");
//                                checkAlertDialog.show();
                            }
                        }else{
                            if(scanResult.size()>0){
                                hideLoadingDialog();
                                if (checkSubscription != null && !checkSubscription.isUnsubscribed()) {
                                    checkSubscription.unsubscribe();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 检测门锁wifi是否连接成功
     */
    private void checkNetConnection(final String ssid) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.e("wifi", "nowWifiIp" + nowWifiIp + "wifi列表数量" + wifiScanResult.size());
                        if (nowWifiIp != null && wifiScanResult.size() > 0) {
                            hideLoadingDialog();
                            showCustomizeDialog();
                            if (subscription != null && !subscription.isUnsubscribed()) {
                                subscription.unsubscribe();
                            }
                        }
                    }
                });
    }

    /**
     * 选择wifi，将账号密码发送给视频锁
     */
    private Spinner spinnerWifidialog;
    private void showCustomizeDialog() {
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.wifi_select_dialog, null);
        customizeDialog.setCancelable(false);
        spinnerWifidialog = (Spinner) dialogView.findViewById(R.id.spinner_wifidialog);
        final EditText tvContentWifidialog = (EditText) dialogView.findViewById(R.id.tv_content_wifidialog);
        TextView tvLeftCancelBtnWifidialog = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn_wifidialog);
        TextView tvRightConfirmBtnWifidialog = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn_wifidialog);
        spinnerSdapter = new MySpinnerSdapter(this, wifiScanResult);
        LogUtil.e("服务器发送","firstLocalSSID:"+firstLocalSSID);
        if(firstLocalSSID!=null&&!"<unknown ssid>".equals(firstLocalSSID)){
            for (int i = 0; i <wifiScanResult.size() ; i++) {
                if(firstLocalSSID.equals(wifiScanResult.get(i))){
                    spinnerWifidialog.setSelection(i);
                    break;
                }
            }
        }
        spinnerWifidialog.setAdapter(spinnerSdapter);
        tvRightConfirmBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass = tvContentWifidialog.getText().toString().trim();
                if (pass == null || pass.isEmpty()) {
                    Toast.makeText(VideoDoorlockWifisActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                itemSSID = spinnerWifidialog.getSelectedItem().toString();
                LogUtil.e("服务器发送", "选中条目" + itemSSID);
                sendMsg01(itemSSID, pass);
                if (alertDialog != null)
                    alertDialog.dismiss();
                if(linerSearch.getVisibility()==View.GONE){
                    linerSearch.setVisibility(View.VISIBLE);
                    tvSearchTitle.setText("设备正在联网");
                    linerSearchIn.setVisibility(View.GONE);
                    linerNetIn.setVisibility(View.VISIBLE);
                }
//                showLoadingDialog("请稍后...");
            }
        });
        tvLeftCancelBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        alertDialog = customizeDialog.show();
    }

    private int random = -1;

    public void sendMsg01(final String itemData, final String pass) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Socket sk = null;
                OutputStream outputStreamSk = null;
                OutputStreamWriter outputStreamWriterSk = null;
                InputStream isSk = null;
                InputStreamReader isRSk = null;
                BufferedWriter bwSk = null;
                BufferedReader brSk = null;
                try {
                    LogUtil.e("wifi", "第一次发送ip" + nowWifiIp);
                    sk = new Socket(nowWifiIp, portSk);
                    outputStreamSk = sk.getOutputStream();
                    LogUtil.e("sockt连接发送","outputStreamSk:"+outputStreamSk);
                    outputStreamWriterSk = new OutputStreamWriter(outputStreamSk, "utf-8");
                    LogUtil.e("sockt连接发送","outputStreamWriterSk:"+outputStreamWriterSk);
                    bwSk = new BufferedWriter(outputStreamWriterSk);
                    random=getRandomInt();
                   // String contentSk = "DAS_ADDR:" + das_addr + ",DAS_PORT:" + das_port + ",RANDOM:" + "1234567890" + ",SSID:" + itemData + ",PASSWD:" + pass + ",CHANNEL:" + chanel;
                   // LogUtil.e("服务器发送1",  contentSk);
                    String contentSk="DAS_ADDR:"+das_addr+",DAS_PORT:"+das_port+",RANDOM:"+random+",SSID:"+itemData+",PASSWD:"+pass+",CHANNEL:"+chanel;
                    LogUtil.e("服务器发送1",  contentSk);
                    bwSk.write(contentSk + "\n");// 必须要加换行符号,不然数据发不出去
                    bwSk.flush();

                    isSk = sk.getInputStream();
                    isRSk = new InputStreamReader(isSk);
                    brSk = new BufferedReader(isRSk);
                    result01 = brSk.readLine();
                    Message msgSk = Message.obtain();
                    msgSk.what = 1;
                    mMainHandler.sendMessage(msgSk);
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                    showErrorDialog("socket连接失败");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStreamSk != null) {
                            outputStreamSk.close();
                            outputStreamSk = null;
                        }
                        if (outputStreamWriterSk != null) {
                            outputStreamWriterSk.close();
                            outputStreamWriterSk = null;
                        }
                        if (bwSk != null) {
                            bwSk.close();
                            bwSk = null;
                        }
                        if (isSk != null) {
                            isSk.close();
                            isSk = null;
                        }
                        if (isRSk != null) {
                            isRSk.close();
                            isRSk = null;
                        }
                        if (brSk != null) {
                            brSk.close();
                            brSk = null;
                        }
                        if (sk != null) {
                            sk.close();
                            sk = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 发送信息
    public void sendMsg02(final String itemData, final String pass) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                Socket socket = null;
                InputStream is = null;
                InputStreamReader isr = null;
                OutputStream outputStream = null;
                OutputStreamWriter outputStreamWriter = null;
                BufferedWriter bw = null;
                BufferedReader br = null;

                try {
                    //jija-yanfa   wl88888888
                    // 创建socket对象，指定服务器端地址和端口号
                    socket = new Socket(nowWifiIp, Port);
                    outputStream = socket.getOutputStream();
                    outputStreamWriter = new OutputStreamWriter(outputStream, "utf-8");
                    bw = new BufferedWriter(outputStreamWriter);
                    String body = "<network><client><wireless><ssid>" + itemData + "</ssid><password>" + pass + "</password><channel>" + chanel + "</channel><security>wpa-personal</security></wireless><ip><ip_type>dhcp</ip_type></ip></client></network>";
                    long lenth = body.length();
                    String content = "POST /gainspan/system/config/network?t=1502432851784 HTTP/1.1\r\n"
                            + "Host: " + nowWifiIp + "\r\n"
                            + "Content-Type: text/xml\r\n"
                            + "Referer: http://" + nowWifiIp + "/gsprov.html\r\n"
                            + "Content-Length: " + lenth + "\r\n\r\n"
                            + body;
                    LogUtil.e("服务器发送2",content);
                    bw.write(content + "\n");// 必须要加换行符号,不然数据发不出去
                    bw.flush();
                    is = socket.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    // 通过输入流读取器对象 接收服务器发送过来的数据
                    result02 = br.readLine();
                    Message msg = Message.obtain();
                    msg.what = 2;
                    mMainHandler.sendMessage(msg);

                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    try {

                        if (outputStream != null) {
                            outputStream.close();
                            outputStream = null;
                        }
                        if (outputStreamWriter != null) {
                            outputStreamWriter.close();
                            outputStreamWriter = null;
                        }
                        if (bw != null) {
                            bw.close();
                            bw = null;
                        }
                        if (is != null) {
                            is.close();
                            is = null;
                        }
                        if (isr != null) {
                            isr.close();
                            isr = null;
                        }
                        if (br != null) {
                            br.close();
                            br = null;
                        }
                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sentMsg03() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                InputStreamReader isr = null;
                OutputStream outputStream = null;
                OutputStreamWriter outputStreamWriter = null;
                BufferedWriter bw = null;
                BufferedReader br = null;
                Socket socket = null;
                try {
                    // 创建socket对象，指定服务器端地址和端口号
                    socket = new Socket(nowWifiIp, Port);
                    outputStream = socket.getOutputStream();
                    outputStreamWriter = new OutputStreamWriter(outputStream, "utf-8");
                    bw = new BufferedWriter(outputStreamWriter);
                    String body = "<network><mode>client</mode></network>";
                    long lenth = body.length();

                    String content = "POST /gainspan/system/config/network?t=1502432854735 HTTP/1.1\r\n"
                            + "Host: " + nowWifiIp + "\r\n"
                            + "Content-Length: " + lenth + "\r\n"
                            + "Content-Type: text/xml\r\n"
                            + "Referer: http://" + nowWifiIp + "/gsprov.html\r\n\r\n"
                            + body;
                    LogUtil.e("服务器发送3",content);
                    bw.write(content + "\n");// 必须要加换行符号,不然数据发不出去
                    bw.flush();
                    is = socket.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    // 通过输入流读取器对象 接收服务器发送过来的数据
                    result03 = br.readLine();
                    Message msg = Message.obtain();
                    msg.what = 3;
                    mMainHandler.sendMessage(msg);

                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                            outputStream = null;
                        }
                        if (outputStreamWriter != null) {
                            outputStreamWriter.close();
                            outputStreamWriter = null;
                        }
                        if (bw != null) {
                            bw.close();
                            bw = null;
                        }
                        if (is != null) {
                            is.close();
                            is = null;
                        }
                        if (isr != null) {
                            isr.close();
                            isr = null;
                        }
                        if (br != null) {
                            br.close();
                            br = null;
                        }
                        if (socket != null) {
                            socket.close();
                            socket = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 获取随机数
     */
    private int getRandomInt() {
        //2147483647
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        buffer.append(random.nextInt(3));
        buffer.append(random.nextInt(2));
        buffer.append(random.nextInt(5));
        buffer.append(random.nextInt(8));
        buffer.append(random.nextInt(5));
        buffer.append(random.nextInt(9));
        buffer.append(random.nextInt(4));
        buffer.append(random.nextInt(7));
        buffer.append(random.nextInt(5));
        buffer.append(random.nextInt(8));
        String ss = buffer.toString();
        int i = Integer.valueOf(ss);
        return i;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // wifi已成功扫描到可用wifi。
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> data = sortByLevel(wifiManager.getScanResults());
                scanResult.clear();
                wifiScanResult.clear();
                if (data != null && data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        if (!TextUtils.isEmpty(data.get(i).SSID)) {
                            if (data.get(i).SSID.startsWith("WONLY")) {
                                scanResult.add(data.get(i));
                            } else {
                                wifiScanResult.add(data.get(i).SSID);
                            }
                        }
                    }
//                    if(scanResult.size()==1){
//                        doorLockItemData = scanResult.get(0);
//                        changeWifi();
//                    }else if(scanResult.size()>1){
//                        adapter.notifyDataSetChanged();
//                    }


                    if (alertDialog != null && alertDialog.isShowing()) {
                        if (spinnerWifidialog!=null&&spinnerSdapter != null) {
                            String selectItem=spinnerWifidialog.getSelectedItem().toString();
                            spinnerSdapter.notifyDataSetChanged();
                            if(selectItem!=null){
                                for (int i = 0; i <wifiScanResult.size() ; i++) {
                                    if(selectItem.equals(wifiScanResult.get(i))){
                                        spinnerWifidialog.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if(isSearch&&linerSearch.getVisibility()==View.VISIBLE&&scanResult.size()>0){
                        linerSearch.setVisibility(View.GONE);
                        isSearch=false;
                    }
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            //系统wifi的状态
            else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.e("扫描", "开启扫描");
                        wifiManager.startScan();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        break;
                }
            }
        }
    };

    /**
     * 将搜索到的wifi根据信号强度从强到时弱进行排序
     * @param list 存放周围wifi热点对象的列表
     */
    private List<ScanResult> sortByLevel(List<ScanResult> list) {

        Collections.sort(list, new Comparator<ScanResult>() {

            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level - lhs.level;
            }
        });
        return list;
    }

    private BroadcastReceiver checkWifiIsConnectReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                //获取联网状态的NetworkInfo对象
                NetworkInfo info = intent
                        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI
                                ) {
                            getNowWifiIp();

                        }
                    } else {
                        Log.e("wifi", "断开");
                    }
                }
            }
        }
    };


    /**
     * 获取当前链接门锁wifi的Ip地址
     */
    private void getNowWifiIp() {
        String localSSID = AppUtil.getLocalWifiSSID(wifiManager);
        LogUtil.e("wifi", "ssid: " + localSSID);
        if (localSSID != null) {
            boolean aaa = localSSID.contains("WONLY");
            LogUtil.e("wifi", "是否以WONLY开头" + aaa);
            if (localSSID.contains("WONLY")) {
                dhcpInfo = wifiManager.getDhcpInfo();
                nowWifiIp = AppUtil.getGateWayIp(dhcpInfo);
                LogUtil.e("wifi", "ip: " + nowWifiIp);
            }
        }
    }

    /**
     * 判断当前wifi是否 门锁的wifi
     * @return
     */
    private boolean nowWifiIsDoorLock() {
        boolean is = false;
        String localSSID = AppUtil.getLocalWifiSSID(wifiManager);
        LogUtil.e("wifi", "ssid: " + localSSID);
        if (localSSID != null) {
            boolean aaa = localSSID.contains("WONLY");
            LogUtil.e("wifi", "是否以WONLY开头" + aaa);
            if (localSSID.contains("WONLY")) {
                is = true;
            }
        }
        return is;
    }

    /**
     * 获取当前wifi的SSid
     * @return
     */
    private String getNowWifiSSID() {
        String localSSID = AppUtil.getLocalWifiSSID(wifiManager);
        return localSSID;
    }
    private String mac;
//    private String version;
    /**
     * 用户绑定设备
     */
    private void userBindDeviceReq() {
        hideLoadingDialog();
        //version:%s,mac_addr:%s,
        int index01 = result01.indexOf(",");
        int index02 = result01.indexOf(":");
//        version = result01.substring(index02 + 1, index01);
        int last01 = result01.lastIndexOf(":");
        int last02 = result01.lastIndexOf(",");
        mac = result01.substring(last01 + 1, last02);
        UserBindDeviceRequest request = new UserBindDeviceRequest();
        request.setToken(AppContext.getToken());
        request.setApi_version("1.0");
        request.setVendor_name(FactoryType.GENERAL);
        //request.setUuid("ccb0da102ce4");
        request.setUuid(mac);
        request.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        //request.setRandom_key("1234567890");
        request.setRandom_key(String.valueOf(random));
        //request.setNetwork_ssid("jija-yanfa");
        request.setNetwork_ssid(itemSSID);
        //request.setVersion("1.0.0-1.0.0");
//        request.setVersion(version);
        //request.setMac_addr("ccb0da102ce4");
        request.setMac_addr(mac);
        LogUtil.e("服务器发送",request.toString());
        JSONObject jsonObject = null;
        try {
            String req = new Gson().toJson(request);
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_BIND_DEVICE_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户绑定设备发送成功");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        checkUndateTiming();
    }


    /**
     * 请求添加视频锁设备到服务器UMS
     */
    private void reqDddDevice() {
        //添加设备到服务器
        AddDevicesReq addDevicesReq = new AddDevicesReq();
        AddDevicesReq.DeviceBean deviceBean = new AddDevicesReq.DeviceBean();
        addDevicesReq.setGateway_vendor_name(FactoryType.FBEE);
        addDevicesReq.setGateway_uuid(AppContext.getGwSnid());
        deviceBean.setVendor_name(FactoryType.GENERAL);
        deviceBean.setUuid(mac);
        deviceBean.setShort_id(mac);
        deviceBean.setNote("视频锁");
        deviceBean.setType(FactoryType.VIDEO_DOORLOCK_TYPE);
//        deviceBean.setVersion(version);
        addDevicesReq.setDevice(deviceBean);
        LogUtil.e("服务器发送ums",addDevicesReq.toString());
        presenter.reqAddDevices(addDevicesReq);
    }

    private Subscription subscriptionUpdateTiming;

    /**
     * 设备信息更新通知检测
     */
    private void deviceInfoUpdateTiming() {
        if (checkSubscriptionUpdateTiming != null && !checkSubscriptionUpdateTiming.isUnsubscribed()) {
            checkSubscriptionUpdateTiming.unsubscribe();
        }
        if (subscriptionUpdateTiming != null && !subscriptionUpdateTiming.isUnsubscribed()) {
            subscriptionUpdateTiming.unsubscribe();
        }
        subscriptionUpdateTiming = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong <= 180) {
                            LogUtil.e("服务器发送","计时:"+aLong);
                            if(mac!=null&&response!=null&&mac.equals(response.getUuid())&&response.getData()!=null&&response.getData().getStatus()!=null){
                                if("bind".equals(response.getData().getStatus())){
                                    if (subscriptionUpdateTiming != null && !subscriptionUpdateTiming.isUnsubscribed()) {
                                        subscriptionUpdateTiming.unsubscribe();
                                    }
                                    LogUtil.e("服务器发送添加","===========");
                                    reqDddDevice();
                                    LogUtil.e("服务器发送添加","+++++++++++");
                                }
                            }

                        } else {
                            hideLoadingDialog();
                            showErrorDialog(null);
                            if (subscriptionUpdateTiming != null && !subscriptionUpdateTiming.isUnsubscribed()) {
                                subscriptionUpdateTiming.unsubscribe();
                            }
                        }
                    }
                });
    }

    private Subscription checkSubscriptionUpdateTiming;
    private void checkUndateTiming(){
        if (checkSubscriptionUpdateTiming != null && !checkSubscriptionUpdateTiming.isUnsubscribed()) {
            checkSubscriptionUpdateTiming.unsubscribe();
        }
        checkSubscriptionUpdateTiming = Observable.interval(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong == 185) {
                            hideLoadingDialog();
                            showErrorDialog(null);
                            if (checkSubscriptionUpdateTiming != null && !checkSubscriptionUpdateTiming.isUnsubscribed()) {
                                checkSubscriptionUpdateTiming.unsubscribe();
                            }
                        }
                    }
                });
    }

    private void showErrorDialog(String  msg){
        if(msg==null){
            msg="操作失败请返回重试";
        }
        checkAlertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage(msg).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkAlertDialog.dismiss();
                skipAct(VideoLockDMSFirstActivity.class);
                finish();
            }
        }).setCancelable(false).show();
    }
    private Emitter.Listener userConfirmation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result03 != null) {
                        if (result03.contains("200") || result03.contains("OK")) {
                            JSONObject data = (JSONObject) args[0];
                            MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                            if (response.getReturn_string().contains("SUCCESS")) {
                                result03 = null;
                                userBindDeviceReq();
                            }
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener userBindDevice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    MnsBaseResponse response01 = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                    Log.e("服务器发送", "绑定设备返回:" + response01.getReturn_string());
                    Toast.makeText(VideoDoorlockWifisActivity.this, "绑定设备返回:" + response01.getReturn_string(), Toast.LENGTH_SHORT).show();
                    if (response01.getReturn_string().contains("WAIT_BIND")) {
                        deviceInfoUpdateTiming();
                    }
                }
            });
        }
    };

    private DeviceInfoUpdateRequest response;
    private Emitter.Listener deviceInfoUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    response = new Gson().fromJson(data.toString(), DeviceInfoUpdateRequest.class);
                    showToast("设备信息更新:"+response.toString());
                    Log.e("服务器发送", "设备信息更新返回:" + response.toString());
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alertDialog!=null&&alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        unregisterReceiver(mReceiver);
        unregisterReceiver(checkWifiIsConnectReceiver);
        if (checkNetSubscription != null && !checkNetSubscription.isUnsubscribed()) {
            checkNetSubscription.unsubscribe();
        }

        if (checkSubscriptionUpdateTiming != null && !checkSubscriptionUpdateTiming.isUnsubscribed()) {
            checkSubscriptionUpdateTiming.unsubscribe();
        }

        if (subscriptionUpdateTiming != null && !subscriptionUpdateTiming.isUnsubscribed()) {
            subscriptionUpdateTiming.unsubscribe();
        }
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (checkSubscription != null && !checkSubscription.isUnsubscribed()) {
            checkSubscription.unsubscribe();
        }
        if (socket != null) {
            socket.off("MSG_USER_AUTH_RSP", userConfirmation);
            socket.off("MSG_USER_BIND_DEVICE_RSP", userBindDevice);
            socket.off("MSG_DEVICE_INFO_UPDATE_REQ", deviceInfoUpdate);
        }
    }

    /**
     * 添加设备返回
     *
     * @param bean
     */
    @Override
    public void resAddDevice(BaseResponse bean) {
        hideLoadingDialog();
        if (bean == null) {
            showErrorDialog(null);
            return;
        }
        if (bean.getHeader().getHttp_code().equals("200")) {
            AddVideoLockInfo myDeviceInfo = new AddVideoLockInfo();
            myDeviceInfo.setName("视频锁");
            myDeviceInfo.setId(response.getUuid());
            myDeviceInfo.setDeviceType(FactoryType.VIDEO_DOORLOCK_TYPE);
            myDeviceInfo.setStatus("online");
            RxBus.getInstance().post(myDeviceInfo);
            LogUtil.e("服务器发送","设备上线绑定成功");
            Toast.makeText(VideoDoorlockWifisActivity.this, "设备上线绑定成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showErrorDialog(null);
        }
    }


    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }


}
