package com.fbee.smarthome_wl.ui.equesdevice.settinglist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.AddModifyDeviceReq;
import com.fbee.smarthome_wl.bean.DelDeviceReq;
import com.fbee.smarthome_wl.bean.JiuCaiSettingInfo;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.JiuCaiDeleteEvent;
import com.fbee.smarthome_wl.response.BaseNetBean;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by jija on 2017/1/5.
 */
public class JiucaiSettingListActivity extends BaseActivity implements View.OnClickListener {

    private CheckBox NetWork, Message, Surveillance, Alarm;
    private Button Delete;
    private ResAnychatLogin.UserBean.UserDevicesBean UserDevicesBean;
    private AlertDialog.Builder builder;
    private HashMap<String, String> deviceInfo;
    private AnyChatCoreSDK anychat;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiucai_device_setting);
    }

    public void initData() {
        NetWork.setOnClickListener(this);
        Message.setOnClickListener(this);
        Surveillance.setOnClickListener(this);
        Alarm.setOnClickListener(this);
        Delete.setOnClickListener(this);
    }

    public void initView() {
        initApi();
        anychat = AnyChatCoreSDK.getInstance();
        deviceInfo = new HashMap<>();
        UserDevicesBean = (ResAnychatLogin.UserBean.UserDevicesBean) getIntent().getExtras().getSerializable("UserDevicesBean");
        NetWork = (CheckBox) findViewById(R.id.cb_network);
        Message = (CheckBox) findViewById(R.id.cb_message);
        Surveillance = (CheckBox) findViewById(R.id.cb_surveillance);
        Alarm = (CheckBox) findViewById(R.id.cb_alarm);
        Delete = (Button) findViewById(R.id.btn_delete);
//        PreferencesUtils.saveString("JIUCAI_ID",resLoginbean.getUser().getId());
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
//        stringStringHashMap.put("imei", UserDevicesBean.getDevice_name());
//        stringStringHashMap.put("user_id", PreferencesUtils.getString("JIUCAI_ID"));
        //noinspection unchecked
        mApiWrapper.RequestJiuCaiSetting(UserDevicesBean.getDevice_name(), PreferencesUtils.getString("JIUCAI_ID"))
                .subscribe(newMySubscriber(new SimpleMyCallBack<JiuCaiSettingInfo>() {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(JiuCaiSettingInfo jiuCaiSettingInfo) {
                        if (jiuCaiSettingInfo.getMode_push() == 1) {
                            NetWork.setChecked(true);
                        }
                        if (jiuCaiSettingInfo.getMode_message() == 1) {
                            Message.setChecked(true);
                        }
                        if (jiuCaiSettingInfo.getMonitor() == 1) {
                            Surveillance.setChecked(true);
                        }
                        if (jiuCaiSettingInfo.getSensor_alarm() == 1) {
                            Alarm.setChecked(true);
                        }
                    }
                }));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_bt_back:
                finish();
                break;
            case R.id.btn_delete:
                dialogshow();
                break;
            case R.id.cb_network:
            case R.id.cb_message:

                StringBuilder sb = new StringBuilder();
                if (NetWork.isChecked()) {
                    sb.append("1");
                }
                if (Message.isChecked()) {
                    sb.append("5");
                }
                if (sb.toString().length() == 0) {
                    sb.append("0");
                }
                deviceInfo.put("type", "mode");
                deviceInfo.put("imei", UserDevicesBean.getDevice_name());
                deviceInfo.put("value", sb.toString());
                mApiWrapper.JiuCaiSetting(deviceInfo).subscribe(newMySubscriber(new SimpleMyCallBack() {
                    @Override
                    public void onNext(Object o) {
                        if (o.toString().contains("success"))
                            showToast("设置成功");
                    }
                }));
                break;
            case R.id.cb_surveillance:
                deviceInfo.put("type", "monitor");
                deviceInfo.put("imei", UserDevicesBean.getDevice_name());
                deviceInfo.put("value", Surveillance.isChecked() ? "1" : "0");
                mApiWrapper.JiuCaiSetting(deviceInfo).subscribe(newMySubscriber(new SimpleMyCallBack() {
                    @Override
                    public void onNext(Object o) {
                        if (o.toString().contains("success"))
                            showToast("设置成功");
                    }
                }));
                break;
            case R.id.cb_alarm:
                deviceInfo.put("type", "sensor");
                deviceInfo.put("imei", UserDevicesBean.getDevice_name());
                deviceInfo.put("value", Alarm.isChecked() ? "5" : "0");
                mApiWrapper.JiuCaiSetting(deviceInfo).subscribe(newMySubscriber(new SimpleMyCallBack() {
                    @Override
                    public void onNext(Object o) {
                        if (o.toString().contains("success"))
                            showToast("设置成功");
                    }
                }));
                break;

        }
    }

    private void dialogshow() {
        AlertDialog.Builder alter1 = new AlertDialog.Builder(JiucaiSettingListActivity.this);
        alter1.setMessage("确定要删除设备?")
                .setTitle("叮咚")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }
                        }).show();
    }

    /**
     * 删除anychat
     */
    private void delete() {

        try {

//            Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<BaseNetBean>() {
//                @Override
//                public void onError(Throwable mHttpExceptionBean) {
//                    super.onError(mHttpExceptionBean);
//                    showToast("删除失败");
//                }
//
//                @Override
//                public void onNext(BaseNetBean info) {
//                    if (info.status.equals("1")) {
//                        List<ResAnychatLogin.UserBean.UserDevicesBean> devices = AppContext.getDevices();
//                        AppContext.DeleteList(UserDevicesBean);
//                        List<ResAnychatLogin.UserBean.UserDevicesBean> aaa = AppContext.getDevices();
//                        showToast("删除成功");
//
//                    } else if (info.status.equals("0")) {
//                        showToast(info.msg);
//                    }
//                }
//
//                @Override
//                public void onCompleted() {
//                    super.onCompleted();
//                }
//
//
//            });
            //anychat 删除
            final Map<String, String> params = new HashMap<String, String>();
            params.put("device", UserDevicesBean.getDevice_name());
            params.put("alias", UserDevicesBean.getAlias());
//            Observable<BaseNetBean> observableAnychat = mApiWrapper.delAnychat(params);
//            //九彩删除
//            DelDeviceReq delbean = new DelDeviceReq();
//            delbean.device = UserDevicesBean.getDevice_name();
//            delbean.alias = UserDevicesBean.getAlias();
//            final Map<String, String> headers = new HashMap<>();
//            String token = PreferencesUtils.getString("JIUCAI_TOKEN");
//            if (token == null) {
//                token = "";
//            }
//            headers.put("Token", token);
//            headers.put("Content-Type", "application/json; charset=utf-8");
//            mApiWrapper.delJiu(params).subscribe(newMySubscriber(new SimpleMyCallBack() {
//                @Override
//                public void onNext(Object o) {
////                    showToast("弹出");
//                }
//            }));
            mApiWrapper.delJiu(params).subscribe(new Action1<BaseNetBean>() {


                private List<ResAnychatLogin.UserBean.UserDevicesBean> devices;

                @Override
                public void call(BaseNetBean baseNetBean) {
                    if (baseNetBean.status.equals("success")) {
                        String s = "delfriend:" + UserDevicesBean.getDevice_name();
                        byte buf[] = s.getBytes();
                        int i = anychat.TransBuffer(0, buf, 1024);
                        devices = AppContext.getDevices();
                        for (ResAnychatLogin.UserBean.UserDevicesBean DevicesBean : devices) {
                            if (UserDevicesBean.getDevice_name().equals(DevicesBean.getDevice_name())) {
                                RxBus.getInstance().post(new JiuCaiDeleteEvent());
                                devices.remove(DevicesBean);
                                showToast("删除成功");
                                skipAct(MainActivity.class);
                                finish();
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
