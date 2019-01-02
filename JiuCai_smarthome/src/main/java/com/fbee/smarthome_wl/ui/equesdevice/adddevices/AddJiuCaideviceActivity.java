package com.fbee.smarthome_wl.ui.equesdevice.adddevices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bairuitech.anychat.AnyChatTransDataEvent;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.response.BaseNetBean;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.corecode.CaptureActivity;
import com.fbee.smarthome_wl.ui.main.homepage.HomeFragment;
import com.fbee.smarthome_wl.utils.PreferencesUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.type;
import static com.fbee.smarthome_wl.anychatutils.ControlCenter.anychat;
import static com.fbee.smarthome_wl.base.BaseApplication.anyChatSDK;

public class AddJiuCaideviceActivity extends BaseActivity implements AnyChatTransDataEvent {

    private TextView textView;
    private EditText jiucai_nub;
    private ImageView add_jiucai;
    private EditText jiucai_name;
    private String dn;
    private String dname;
    private Timer time;
    private TimerTask task;
    private String type = "0";
    int dwTargetUserId;
    private List<ResAnychatLogin.UserBean.UserDevicesBean> jiuCaiDevices;
    private LoginResult.BodyBean.GatewayListBean gw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jiu_caidevice);
    }

    public void initView() {
        initApi();
        textView = (TextView) findViewById(R.id.textView);
        jiucai_nub = (EditText) findViewById(R.id.jiucai_nub);
        add_jiucai = (ImageView) findViewById(R.id.add_jiucai);
        jiucai_name = (EditText) findViewById(R.id.jiucai_name);
        TextView rightMenu = (TextView) findViewById(R.id.tv_right_menu);
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText("完成");
        rightMenu.setOnClickListener(this);
        add_jiucai.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        jiuCaiDevices = AppContext.getDevices();
        gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        if (gw != null) {
            setAnyChat();
        }
    }

    protected void setAnyChat() {
        try {
            anyChatSDK.SetBaseEvent(anyChatBaseEvent);
            anyChatSDK.SetTransDataEvent(this);
            anyChatSDK.EnterRoom(1, "");
            anychat.Connect("ihomecn.rollupcn.com", 8906);
            anychat.Login(gw.getUsername(), "123456");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_jiucai:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyRecordPermissions(AddJiuCaideviceActivity.this);
                } else {
                    Intent intent = new Intent(this, CaptureActivity.class);
                    intent.putExtra(AppContext.JIUCAI_NAME, "JIUCAI_NAME");
                    startActivityForResult(intent, 77);
                }


                break;
            case R.id.tv_right_menu:
                performAdd();
                break;
        }
    }

    private static String[] PERMISSIONS_RECORD = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};

    private void verifyRecordPermissions(BaseActivity captureActivity) {
        //摄像头权限
        if (ContextCompat.checkSelfPermission(captureActivity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //先判断有没有权限 ，没有就在这里进行权限的申请
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS_RECORD, 2);
            }
        } else {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra(AppContext.JIUCAI_NAME, "JIUCAI_NAME");
            startActivityForResult(intent, 77);
        }
    }

    /***
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 2) {
//            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(this, CaptureActivity.class);
//                intent.putExtra(AppContext.JIUCAI_NAME, "JIUCAI_NAME");
//                intent.putExtra("isPermission", true);
//                startActivityForResult(intent, 77);
//            }
//        }
        if (requestCode == 2) {
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, CaptureActivity.class);
                intent.putExtra(AppContext.JIUCAI_NAME, "JIUCAI_NAME");
                startActivityForResult(intent, 77);
            } else {
                Intent intent = new Intent(this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isPermission", true);
                startActivityForResult(intent, 77, bundle);
            }
        }
    }

    private void performAdd() {
        try {
            dn = jiucai_nub.getText().toString();
            dname = jiucai_name.getText().toString();
            if (TextUtils.isEmpty(dn)) {
                showToast("设备编号不能为空");
                return;
            }
            if (TextUtils.isEmpty(dname)) {
                showToast("设备名称不能为空");
                return;
            }
            for (ResAnychatLogin.UserBean.UserDevicesBean device : jiuCaiDevices) {
                if (device.getDevice_name().equals(dn)) {
                    showToast("设备已经绑定");
                    return;
                }
            }

            //发送透传数据（设备编号）
            byte buf[] = dn.getBytes();
            int flag = anyChatSDK.TransBuffer(-1, buf, 1024);//0是正常的  208 未登录
            if (flag == 0) {
                showToast("请在设备端确认");
                recordRunTime();
            }
            if (flag == 208) {
                showToast("请重新登录");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 绑定计时
     */
    private void recordRunTime() {
        time = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (time != null) {
                    time.cancel();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("绑定超时");
                    }
                });
            }
        };
        time.schedule(task, 40 * 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 77) {
            jiucai_nub.setText(data.getExtras().getString("result"));
        }

    }

    @Override
    public void OnAnyChatTransBuffer(int dwUserid, byte[] lpBuf, int dwLen) {
        if (time != null) {
            time.cancel();
            String string = new String(lpBuf);


            if (dn.equals(string)) {
                dwTargetUserId = dwUserid;
                doAdd();
            } else if (string.equals("13")) {// 拒绝绑定
                showToast("拒绝绑定");
            } else if (string.equals("14")) {// 设备端已经绑定
                dwTargetUserId = dwUserid;
                showToast("设备端已绑定");
            } else {
            }
        }
    }

    private void doAdd() {
        try {

            Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<BaseNetBean>() {
                @Override
                public void onError(Throwable mHttpExceptionBean) {
                    super.onError(mHttpExceptionBean);
                    showToast("添加失败");
                }

                @Override
                public void onNext(BaseNetBean info) {
                    if ("success".equals(info.status)) {
                        //透明通道传输告知服务器有新的绑定关系建立
                        if (dwTargetUserId != 0) {
                            String s = "addfriend:" + dwTargetUserId;
                            byte buf[] = s.getBytes();
                            anyChatSDK.TransBuffer(0, buf, 1024);
                            showToast("添加成功");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showToast("添加失败");
                        }

                    } else {
                        showToast("添加失败");
                    }

                }
            });

            //anychat
            Map<String, String> params = new HashMap<String, String>();
            params.put("device", dn);
            params.put("alias", dname);
            Observable<BaseNetBean> anychatObs = mApiWrapper.addAnyChat(params);

            //九彩
            ArrayMap<String, String> addModifyDeviceReq = new ArrayMap<String, String>();
            addModifyDeviceReq.put("device", dn);
            addModifyDeviceReq.put("alias", dname);
            final Map<String, String> headers = new HashMap<>();
            String token = PreferencesUtils.getString("JIUCAI_TOKEN");
            if (token == null) {
                token = "";
            }
            headers.put("Token", token);
            headers.put("Content-Type", "application/json; charset=utf-8");

            Observable<BaseNetBean> jiuObs = mApiWrapper.addJiu(addModifyDeviceReq);
            Subscription subscription = Observable.merge(anychatObs, jiuObs)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);

            mCompositeSubscription.add(subscription);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnAnyChatTransBufferEx(int dwUserid, byte[] lpBuf, int dwLen, int wparam, int lparam, int taskid) {

    }

    @Override
    public void OnAnyChatSDKFilterData(byte[] lpBuf, int dwLen) {

    }
}
