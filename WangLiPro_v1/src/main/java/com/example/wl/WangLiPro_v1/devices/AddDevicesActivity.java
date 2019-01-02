package com.example.wl.WangLiPro_v1.devices;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
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

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.adapter.MySpinnerSdapter;
import com.example.wl.WangLiPro_v1.adapter.VideoLockAdapter;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.example.wl.WangLiPro_v1.utils.DialogUtils;
import com.example.wl.WangLiPro_v1.utils.WifiAutoConnectManager;
import com.jwl.android.jwlandroidlib.Exception.HttpException;
import com.jwl.android.jwlandroidlib.bean.BaseBean;
import com.jwl.android.jwlandroidlib.http.HttpManager;
import com.jwl.android.jwlandroidlib.httpInter.HttpDataCallBack;
import com.jwl.android.jwlandroidlib.tcp.TCPSocketCallback;
import com.jwl.android.jwlandroidlib.tcp.WifiTcpSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddDevicesActivity extends BaseActivity implements View.OnClickListener {

    private static final int WIFI_SCAN_PERMISSION_CODE = 100;
    private WifiManager wifiManager;
    private String firstLocalSSID;
    private Subscription checkSubscription;
    private WifiAutoConnectManager wifiAutoConnectManager;
    private ArrayList<ScanResult> scanResult;
    private ArrayList wifiScanResult;
    private MySpinnerSdapter spinnerSdapter;
    private LinearLayout linerSearch;
    private TextView tvSearchTitle;
    private LinearLayout linerSearchIn;
    private LinearLayout linerNetIn;
    private boolean isSearch = true;
    private Subscription subscription;
    private ListView lv;
    private VideoLockAdapter adapter;
    private ScanResult doorLockItemData;
    private Dialog dialog;
    private String randomStr;
    private String versionStr;
    private String deviceIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_devices);
    }

    @Override
    protected void initData() {
        scanResult = new ArrayList();
        wifiScanResult = new ArrayList();
        dialog = DialogUtils.getDialog(this);
        try {
            showCloseDialog();
            //获取定位权限
            if (!checkPermission()) {
                requestPermission();
            }
            //获得系统wifi服务
            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            firstLocalSSID = getLocalWifiSSID(wifiManager);
            //开始扫描
            wifiManager.startScan();
            registerBroadcast();
            //showLoadingDialog("正在搜索设备,请稍后...");
            checkWifiIsExist();
            wifiAutoConnectManager = new WifiAutoConnectManager(wifiManager);
        } catch (Exception e) {
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
                changeWifi(doorLockItemData.SSID, "", "", "");
            }

        });
        adapter = new VideoLockAdapter(this, scanResult);
        lv.setAdapter(adapter);
    }

    private void changeWifi(final String itemSSID, final String deviceIdStr, final String versionStr, final String pass) {
        if (!pass.equals("")) {
            wifiAutoConnectManager.connect(pass, "", WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS);
        }
        wifiAutoConnectManager.connect(itemSSID, "", WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS);
        Toast.makeText(this, "手机正在切换到设备WIFI,请稍后...", Toast.LENGTH_SHORT).show();
        checkNetConnection(itemSSID, pass);
    }

    private void sendMsg(final String itemSSID, final String pass) {
        final int[] tempIp = new int[]{114, 215, 81, 40};
        int random = (int) (Math.random() * 9000 + 1000);
        randomStr = String.valueOf(random);
        WifiTcpSocket.getInstance().setDataCallBack(new TCPSocketCallback() {
            @Override
            public void connected() {
                WifiTcpSocket.getInstance().sendWifiData(itemSSID, pass, randomStr, tempIp);
            }

            @Override
            public void receive(byte[] buffer) {

                //将数据发送给服务器
                DialogUtils.showErroMsg(AddDevicesActivity.this, new String(buffer));
                //0-16 表示deviceId, 16-21 表示version
                byte[] deviceIdBts = new byte[16];
                byte[] verSionBts = new byte[5];
                System.arraycopy(buffer, 0, deviceIdBts, 0, 16);
                System.arraycopy(buffer, 16, verSionBts, 0, 5);
                deviceIdStr = new String(deviceIdBts);
                versionStr = new String(verSionBts);
                changeWifi(itemSSID, deviceIdStr, versionStr, pass);
            }

            @Override
            public void disconnect() {
                dialog.dismiss();
                Toast.makeText(AddDevicesActivity.this, "断开TCP连接", Toast.LENGTH_SHORT).show();

            }
        });
        WifiTcpSocket.getInstance().startTcpConnect("192.168.40.61", 14320);
    }

    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    //(String deviceIdStr, final String versionStr, final String pass)
    public void sendDataToServer(final String deviceIdStr, final String versionStr, final String itemSSID) {
        if (TextUtils.isEmpty(versionStr)) {
            Toast.makeText(AddDevicesActivity.this, "TCP 没有建立不能发送数据，不能进行绑定", Toast.LENGTH_SHORT).show();
            return;
        }
        HttpManager.getInstance(this).bindLock(AppContext.getUSERID(), AppContext.getTOKEN(), deviceIdStr, randomStr,
                itemSSID, versionStr, "家", new HttpDataCallBack<BaseBean>() {
                    @Override
                    public void httpDateCallback(BaseBean b) {
                        Log.e("服务器", "成功" + "deviceIdStr+" + deviceIdStr + "randomStr+" + randomStr + "itemSSID+" + itemSSID + "versionStr+" + versionStr);
                        if (b.getResponse().getCode() == 200) {
                            finish();
                            Toast.makeText(AddDevicesActivity.this, "消息已经到服务器", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddDevicesActivity.this, b.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void httpException(HttpException e) {
                        Log.e("服务器", e.toString() + "deviceIdStr+" + deviceIdStr + "randomStr+" + randomStr + "itemSSID+" + itemSSID + "versionStr+" + versionStr);
                    }

                    @Override
                    public void complet() {
                        Log.e("服务器", "为止" + "deviceIdStr+" + deviceIdStr + "randomStr+" + randomStr + "itemSSID+" + itemSSID + "versionStr+" + versionStr);
                    }
                });
    }


    /**
     * 检测门锁wifi是否连接成功
     */
    private void checkNetConnection(final String itemSSID, final String pass) {
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
//                        nowWifiIp != null &&
                        Log.e("轮训", "111");
                        if (itemSSID.contains("WangLi") && pass.equals("")) {
                            showCustomizeDialog();
                            if (subscription != null && !subscription.isUnsubscribed()) {
                                subscription.unsubscribe();
                            }
                        } else if (deviceIdStr != null && isNetworkAvailable(AddDevicesActivity.this)) {
                            sendDataToServer(deviceIdStr, versionStr, itemSSID);
                            if (subscription != null && !subscription.isUnsubscribed()) {
                                subscription.unsubscribe();
                            }
                        }
                    }
                });
    }

    /**
     * 判断当前wifi是否 门锁的wifi
     *
     * @return
     */
    private boolean nowWifiIsDoorLock() {
        boolean is = false;
        String localSSID = getLocalWifiSSID(wifiManager);
        if (localSSID != null) {
            if (localSSID.contains("WangLi")) {
                is = true;
            }
        }
        return is;
    }

    @Override
    protected void initView() {
        linerSearch = (LinearLayout) findViewById(R.id.liner_search);
        tvSearchTitle = (TextView) findViewById(R.id.tv_search_title);
        linerSearchIn = (LinearLayout) findViewById(R.id.liner_search_in);
        linerNetIn = (LinearLayout) findViewById(R.id.liner_net_in);
        TextView title = (TextView) findViewById(R.id.title);
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.lv);
        title.setText("设备添加");
    }

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    /**
     * 检查是否已经授予权限
     *
     * @return
     */
    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSIONS, WIFI_SCAN_PERMISSION_CODE);
    }

    /**
     * 注册广播，接收wifi网络状态变化
     */
    private void registerBroadcast() {

//        IntentFilter intentfilter = new IntentFilter();
//        intentfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(checkWifiIsConnectReceiver, intentfilter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
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
//                            getNowWifiIp();         //257

                        }
                    } else {
                        Log.e("wifi", "断开");
                    }
                }
            }
        }
    };

    private String getLocalWifiSSID(WifiManager wifimg) {
        WifiInfo wifiInfo = wifimg.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    /**
     * 获取当前链接门锁wifi的Ip地址
     */
//    private String getNowWifiIp() {
//        String localSSID = getLocalWifiSSID(wifiManager);//277
//        if (localSSID != null) {
//            if (localSSID.contains("WangLi")) {
//                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
//                nowWifiIp = intToIp(dhcpInfo.gateway);
//            }
//        }
//        return nowWifiIp;
//    }
    public String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    private void checkWifiIsExist() {
        checkSubscription = Observable.interval(1, TimeUnit.SECONDS) //292
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
                        Log.e("轮训", aLong + "");
                        if (aLong > 10) {       //307
                            if (scanResult.size() == 0) {
                                //hideLoadingDialog();
                                if (checkSubscription != null && !checkSubscription.isUnsubscribed()) {
                                    checkSubscription.unsubscribe();
                                }
                                Toast.makeText(AddDevicesActivity.this, "未找到设备，请确认设备已打开配网模式", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            if (scanResult.size() > 0) {
                                adapter.notifyDataSetChanged();
//                                hideLoadingDialog();
                                if (checkSubscription != null && !checkSubscription.isUnsubscribed()) {
                                    checkSubscription.unsubscribe();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WIFI_SCAN_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以做你要做的事情了。
                } else {
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
                    finish();
                }
                break;
        }
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
                        Log.e("wifi", data.get(i).SSID);
                        if (!TextUtils.isEmpty(data.get(i).SSID)) {
                            if (data.get(i).SSID.startsWith("WangLi")) {
                                scanResult.add(data.get(i));
                            } else {
                                wifiScanResult.add(data.get(i).SSID);
                            }
                        }
                    }

                    if (alertDialog != null && alertDialog.isShowing()) {
                        if (spinnerWifidialog != null && spinnerSdapter != null) {
                            String selectItem = spinnerWifidialog.getSelectedItem().toString();
                            spinnerSdapter.notifyDataSetChanged();
                            if (selectItem != null) {
                                for (int i = 0; i < wifiScanResult.size(); i++) {
                                    if (selectItem.equals(wifiScanResult.get(i))) {
                                        spinnerWifidialog.setSelection(i);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (isSearch && linerSearch.getVisibility() == View.VISIBLE && scanResult.size() > 0) {
                        linerSearch.setVisibility(View.GONE);
                        isSearch = false;
                    }
//                    if (adapter != null) {
//                        adapter.notifyDataSetChanged();
//                    }
                }
            }
            //系统wifi的状态
            else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.e("扫描", "开启扫描");
                        wifiManager.startScan();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.e("扫描", action);
                        break;
                }
            }
        }
    };

    /**
     * 将搜索到的wifi根据信号强度从强到时弱进行排序
     *
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

    private AlertDialog alertDialog;

    private void showCloseDialog() {
        alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("如果您的手机移动网络处于开启状态,请手动关闭手机移动网络连接,否则配网肯能会出错").setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        }).setCancelable(false).show();
    }


    /**
     * 选择wifi，将账号密码发送给视频锁
     */
    private Spinner spinnerWifidialog;
    private String itemSSID;
    private String pass;

    private void showCustomizeDialog() {
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.wifi_select_dialog, null);
        customizeDialog.setCancelable(false);
        spinnerWifidialog = (Spinner) dialogView.findViewById(R.id.spinner_wifidialog);
        final EditText tvContentWifidialog = (EditText) dialogView.findViewById(R.id.tv_content_wifidialog);
        TextView tvLeftCancelBtnWifidialog = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn_wifidialog);
        TextView tvRightConfirmBtnWifidialog = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn_wifidialog);
        spinnerSdapter = new MySpinnerSdapter(this, wifiScanResult);
        if (firstLocalSSID != null && !"<unknown ssid>".equals(firstLocalSSID)) {
            for (int i = 0; i < wifiScanResult.size(); i++) {
                if (firstLocalSSID.equals(wifiScanResult.get(i))) {
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
                    Toast.makeText(AddDevicesActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                itemSSID = spinnerWifidialog.getSelectedItem().toString();
                sendMsg(itemSSID, pass);
                if (alertDialog != null)
                    alertDialog.dismiss();
                if (linerSearch.getVisibility() == View.GONE) {
                    linerSearch.setVisibility(View.VISIBLE);
                    tvSearchTitle.setText("设备正在联网");
                    linerSearchIn.setVisibility(View.GONE);
                    linerNetIn.setVisibility(View.VISIBLE);
                }
//                sendMsg(itemSSID, pass);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
//                unregisterReceiver(checkWifiIsConnectReceiver);
                unregisterReceiver(mReceiver);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(checkWifiIsConnectReceiver);
        unregisterReceiver(mReceiver);
        if (dialog != null) {
            dialog.dismiss();
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
