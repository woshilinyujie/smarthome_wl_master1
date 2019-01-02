package com.fbee.smarthome_wl.ui.equesdevice.settinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.EquesDeviceDelete;
import com.fbee.smarthome_wl.bean.EquesDeviceInfo;
import com.fbee.smarthome_wl.bean.EquesDevicePIRInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.event.UpdateEquesNameEvent;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceListResponse;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverContract;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverPresenter;
import com.fbee.smarthome_wl.ui.equesdevice.settinglist.settingalarmtime.EquesAlarmTimeActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;

import rx.Subscription;
import rx.functions.Action1;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

public class EquesSettingListActivity extends BaseActivity<AddOrDelDevicesToSeverContract.Presenter> implements AddOrDelDevicesToSeverContract.View {

    public static final int TAG = 1;
    private String uid;
    private String swVersion;
    private String nick;
    private String bid;
    private String name;
    private TextView deviceName;
    private TextView devicePirtime;
    private TextView deviceSensitivity;
    private int alarmEnable;
    private int dbLightEnable;
    private int doorbellRing;
    private LinearLayout linearUpdateDevName;
    private TextView tvDevDetailsName;
    private LinearLayout linearPirTime;
    private TextView tvPirTime;
    private LinearLayout linearSensitivity;
    private TextView tvSensitivity;
    private LinearLayout lineraPirMode;
    private LinearLayout linearSetAlarm;
    private CheckBox cbAlarm;
    private LinearLayout linearSetLight;
    private CheckBox cbLight;
    private LinearLayout linearDoorbellRing;
    private TextView tvDoorbellRing;
    private LinearLayout linearPirMode;
    private TextView tvPirMode;
    private LinearLayout linearContinuousCapture;
    private TextView tvContinuousCapture;
    private LinearLayout linearPirRingtone;
    private TextView tvPirRingtone;
    private SeekBar seekbarAlarmSettingVolume;
    private TextView tvWifiConfig;
    private TextView tvDevId;
    private TextView version;
    private Button btnRestartDevice;
    private Button btnDeleteDevice;
    private AlertDialog.Builder alter;
    private String alarmtime;
    private Intent intent;
    private int sense_sensitivity;
    /**
     * 灵敏度标记
     */
    private int sensibility;
    /**
     * 警报方式
     */
    private int alarmMode;
    private String wifiStatus;
    private LinearLayout llSeeker;
    private TextView tvOk;
    private int alarmTime;
    private int pirringtone1 = -1;
    private int senseSensitivity;
    private AlertDialog alertDialog;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_setting_list);
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString(Method.ATTR_BUDDY_UID);
        swVersion = bundle.getString(Method.ATTR_BUDDY_VERSION);
        nick = bundle.getString(Method.ATTR_433_DEVICE_NICK);
        bid = bundle.getString(Method.ATTR_BUDDY_BID);
        alarmEnable = bundle.getInt(Method.METHOD_ALARM_ENABLE);
        name = bundle.getString(Method.ATTR_BUDDY_NAME);
        dbLightEnable = bundle.getInt(Method.METHOD_DB_LIGHT_ENABLE);
        wifiStatus = bundle.getString(Method.METHOD_WIFI_STATUS);
        doorbellRing = bundle.getInt("doorbell_ring");
        //获取PRI详情
        icvss.equesGetDevicePirInfo(uid);
        icvss.equesGetDeviceInfo(uid);
        //人体侦测开关
        linearUpdateDevName = (LinearLayout) findViewById(R.id.linear_updateDevName);
        deviceName = (TextView) findViewById(R.id.tv_dev_detailsName);
        linearPirTime = (LinearLayout) findViewById(R.id.linear_pirTime);
        devicePirtime = (TextView) findViewById(R.id.tv_pirTime);
        linearSensitivity = (LinearLayout) findViewById(R.id.linear_sensitivity);
        deviceSensitivity = (TextView) findViewById(R.id.tv_sensitivity);
        lineraPirMode = (LinearLayout) findViewById(R.id.linera_pirMode);
        linearSetAlarm = (LinearLayout) findViewById(R.id.linear_setAlarm);
        cbAlarm = (CheckBox) findViewById(R.id.cb_alarm);
        linearSetLight = (LinearLayout) findViewById(R.id.linear_setLight);
        cbLight = (CheckBox) findViewById(R.id.cb_light);
        back = (ImageView) findViewById(R.id.back);
        linearDoorbellRing = (LinearLayout) findViewById(R.id.linear_doorbell_ring);
        tvDoorbellRing = (TextView) findViewById(R.id.tv_doorbell_ring);
        linearPirMode = (LinearLayout) findViewById(R.id.linear_pirMode);
        tvPirMode = (TextView) findViewById(R.id.tv_pirMode);
        linearContinuousCapture = (LinearLayout) findViewById(R.id.linear_continuousCapture);
        tvContinuousCapture = (TextView) findViewById(R.id.tv_continuousCapture);
        linearPirRingtone = (LinearLayout) findViewById(R.id.linear_pirRingtone);
        tvPirRingtone = (TextView) findViewById(R.id.tv_pirRingtone);
        TextView title = (TextView) findViewById(R.id.title);
        seekbarAlarmSettingVolume = (SeekBar) findViewById(R.id.seekbar_alarmSettingVolume);
        tvOk = (TextView) findViewById(R.id.tv_right_menu);
        llSeeker = (LinearLayout) findViewById(R.id.ll_seekber);
        tvWifiConfig = (TextView) findViewById(R.id.tv_wifiConfig);
        tvDevId = (TextView) findViewById(R.id.tv_devId);
        version = (TextView) findViewById(R.id.version);
        btnRestartDevice = (Button) findViewById(R.id.btn_restartDevice);
        btnDeleteDevice = (Button) findViewById(R.id.btn_delete_device);
        back.setVisibility(View.VISIBLE);
        tvOk.setVisibility(View.VISIBLE);
        title.setText("猫眼配置");
        tvOk.setText("提交");
        tvDevId.setText(name);
        version.setText(swVersion);
        tvWifiConfig.setText(wifiStatus);
        tvOk.setOnClickListener(this);
        back.setOnClickListener(this);
        linearPirTime.setOnClickListener(this);
        btnRestartDevice.setOnClickListener(this);
        linearDoorbellRing.setOnClickListener(this);
        btnDeleteDevice.setOnClickListener(this);
        linearUpdateDevName.setOnClickListener(this);
        linearSensitivity.setOnClickListener(this);
        linearPirMode.setOnClickListener(this);
        linearContinuousCapture.setOnClickListener(this);
        linearPirRingtone.setOnClickListener(this);
        cbAlarm.setOnClickListener(this);
        cbLight.setOnClickListener(this);

    }

    private int sense_time;
    private int rngtone;

    @Override
    protected void initData() {
        initApi();
        createPresenter(new AddOrDelDevicesToSeverPresenter(this));
        showLoadingDialog(null);
        if (dbLightEnable == 1) {
            cbLight.setChecked(true);
        } else {
            cbLight.setChecked(false);
        }
        if (alarmEnable == 0) {
            cbAlarm.setChecked(false);
        } else {
            cbAlarm.setChecked(true);
        }
        if (nick == null) {
            deviceName.setText(name);
        } else {
            deviceName.setText(nick);
        }
        selectDoorRing(doorbellRing);
        mSubscription = RxBus.getInstance()
                .toObservable(EquesDevicePIRInfo.class)
                .compose(TransformUtils.<EquesDevicePIRInfo>defaultSchedulers())
                .subscribe(new Action1<EquesDevicePIRInfo>() {

                    @Override
                    public void call(EquesDevicePIRInfo equesDevicePIRInfo) {
                        hideLoadingDialog();
                        sense_time = equesDevicePIRInfo.getSense_time();
                        devicePirtime.setText(sense_time + "秒");
                        sense_sensitivity = equesDevicePIRInfo.getSense_sensitivity();
                        if (sense_sensitivity == 1) {
                            deviceSensitivity.setText("高");
                        } else if (sense_sensitivity == 2) {
                            deviceSensitivity.setText("低");
                        }
                        if (equesDevicePIRInfo.getFormat() == 0) {
                            tvPirMode.setText("拍照");
                        } else {
                            tvPirMode.setText("录像");
                            linearContinuousCapture.setEnabled(false);
                        }
                        seekbarAlarmSettingVolume.setProgress(equesDevicePIRInfo.getVolume() - 1);
                        tvContinuousCapture.setText(equesDevicePIRInfo.getCapture_num() + "张");
                        rngtone = equesDevicePIRInfo.getRngtone();
                        switch (rngtone) {
                            case 1:
                                tvPirRingtone.setText("你是谁呀");
                                break;
                            case 2:
                                tvPirRingtone.setText("嘟嘟声");
                                break;
                            case 3:
                                tvPirRingtone.setText("警报声");
                                break;
                            case 4:
                                tvPirRingtone.setText("尖啸声");
                                break;
                            case 5:
                                tvPirRingtone.setText("静音(默认)");
                                seekbarAlarmSettingVolume.setEnabled(false);
                                break;
                            default:
                                break;
                        }
                    }
                });
        mSubscription = RxBus.getInstance().toObservable(EquesDeviceInfo.class)
                .compose(TransformUtils.<EquesDeviceInfo>defaultSchedulers())
                .subscribe(new Action1<EquesDeviceInfo>() {
                    @Override
                    public void call(EquesDeviceInfo equesDeviceInfo) {
                        hideLoadingDialog();
                        int alarm_enable = equesDeviceInfo.getAlarm_enable();
                        if (alarm_enable == 0) {
                            cbAlarm.setChecked(false);
                        } else {
                            cbAlarm.setChecked(true);
                        }
                        int db_light_enable = equesDeviceInfo.getDb_light_enable();
                        if (db_light_enable == 0) {
                            cbLight.setChecked(false);
                        } else {
                            cbLight.setChecked(true);
                        }
                    }
                });

        mSubscription= RxBus.getInstance().toObservable(EquesDeviceDelete.class)
                .compose(TransformUtils.<EquesDeviceDelete>defaultSchedulers())
                .subscribe(new Action1<EquesDeviceDelete>() {
                    @Override
                    public void call(EquesDeviceDelete equesDeviceDelete) {
                        hideLoadingDialog();
                        showToast("删除成功");
                        skipAct(MainActivity.class);
                        finish();
                    }
                });
    }

    private void selectDoorRing(int doorRing) {
        switch (doorRing) {
            case 0:
                tvDoorbellRing.setText("铃声一");
                break;
            case 1:
                tvDoorbellRing.setText("铃声二");
                break;
            case 2:
                tvDoorbellRing.setText("铃声三");
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_restartDevice:
                AlertDialog.Builder builder = creatDialog();
                builder.setMessage("确定要重新启动设备?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        icvss.equesRestartDevice(uid);
                                    }
                                }).show();
                break;
            case R.id.btn_delete_device:
                AlertDialog.Builder builder1 = creatDialog();
                builder1.setMessage("确定要删除设备?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {

                                    private String gwSnid;

                                    public void onClick(DialogInterface dialog, int which) {
                                        DeleteDevicesReq body = new DeleteDevicesReq();
                                        DeleteDevicesReq.DeviceBean deviceBean = new DeleteDevicesReq.DeviceBean();
                                        deviceBean.setUuid(uid);
                                        deviceBean.setVendor_name(FactoryType.EQUES);
                                        gwSnid = AppContext.getGwSnid();
                                        if (gwSnid == null) {
                                            gwSnid = PreferencesUtils.getString(LOCAL_USERNAME);
                                            body.setGateway_vendor_name("virtual");
                                        } else {
                                            body.setGateway_vendor_name(FactoryType.FBEE);
                                        }
                                        body.setGateway_uuid(gwSnid);
                                        body.setDevice(deviceBean);
                                        presenter.reqDeleteDevices(body);
                                    }
                                }).show();

                break;
            case R.id.linear_updateDevName:
                final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
                final View dialogView = LayoutInflater.from(this)
                        .inflate(R.layout.dialog_modify_doolock_name, null);
                TextView title = (TextView) dialogView.findViewById(R.id.tv_title);
                title.setText("修改设备名称");
                final EditText editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
                editText.setText(deviceName.getText());
                Selection.setSelection(editText.getText(), editText.getText().length());
                TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
                TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
                confirmText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText() == null) {
                            showToast("请输入设备名称");
                        } else {
                            icvss.equesSetDeviceNick(bid, editText.getText().toString());
                            deviceName.setText(editText.getText().toString());
                            UpdateEquesNameEvent updateEquesNameEvent = new UpdateEquesNameEvent();
                            updateEquesNameEvent.setName(editText.getText().toString());
                            updateEquesNameEvent.setId(bid);
                            RxBus.getInstance().post(updateEquesNameEvent);
                            alertDialog.dismiss();
                        }
                    }
                });
                cancleText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                    }
                });
                customizeDialog.setView(dialogView);
                alertDialog = customizeDialog.show();
                break;
            case R.id.linear_pirTime:
                intent = new Intent(this, EquesAlarmTimeActivity.class);
                if (!TextUtils.isEmpty(devicePirtime.getText())) {
                    String substring = devicePirtime.getText().toString().substring(0, devicePirtime.getText().length() - 1);
                    int time = Integer.parseInt(substring);
                    intent.putExtra("sense_time", time);
                    startActivityForResult(intent, TAG);
                }
                break;
            case R.id.linear_sensitivity:

                if (deviceSensitivity.getText().equals("高")) {
                    sensibility = 0;
                } else {
                    sensibility = 1;
                }
                final String[] str = {"高", "低"};
                AlertDialog.Builder builder2 = creatDialog();
                builder2.setTitle("感应报警");
                builder2.setSingleChoiceItems(str, sensibility, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sensibility = which;
                                deviceSensitivity.setText(str[which]);
                                dialog.dismiss();
                            }
                        }
                );
                builder2.show();
                break;
            case R.id.linear_doorbell_ring:
                intent = new Intent(this, EquesAlarmTimeActivity.class);
                intent.putExtra("Uid", uid);
                intent.putExtra("doorbellRing", tvDoorbellRing.getText());
                startActivityForResult(intent, TAG);
                break;
            case R.id.linear_pirMode:
                if (tvPirMode.getText().equals("拍照")) {
                    alarmMode = 0;
                } else {
                    alarmMode = 1;
                }
                final String[] mode = {"拍照", "录像"};
                AlertDialog.Builder builder3 = creatDialog();
                builder3.setTitle("感应报警");
                builder3.setSingleChoiceItems(mode, alarmMode, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alarmMode = which;
                                tvPirMode.setText(mode[which]);
                                if (tvPirMode.getText().toString().equals("录像")) {
                                    linearContinuousCapture.setEnabled(false);
                                } else {
                                    linearContinuousCapture.setEnabled(true);
                                }
                                dialog.dismiss();
                            }
                        }
                );
                builder3.show();

                break;
            case R.id.linear_continuousCapture:
                intent = new Intent(this, EquesAlarmTimeActivity.class);
                if (!TextUtils.isEmpty(devicePirtime.getText())) {
                    String pager = tvContinuousCapture.getText().toString().substring(0, devicePirtime.getText().length() - 1);
                    int pagers = Integer.parseInt(pager);
                    intent.putExtra("capture_num", pagers);
                    startActivityForResult(intent, TAG);
                }
                break;
            case R.id.linear_pirRingtone:
                intent = new Intent(this, EquesAlarmTimeActivity.class);
                String PirRingtone = tvPirRingtone.getText().toString();
                intent.putExtra("tvPirRingtone", PirRingtone);
                startActivityForResult(intent, TAG);
                break;
            case R.id.cb_alarm:
                if (cbAlarm.isChecked()) {
                    icvss.equesSetPirEnable(uid, 1);
                } else {
                    icvss.equesSetPirEnable(uid, 0);
                }
                break;
            case R.id.cb_light:
                if (cbLight.isChecked()) {
                    icvss.equesSetDoorbellLight(uid, 1);
                } else {
                    icvss.equesSetDoorbellLight(uid, 0);
                }
                break;
            case R.id.tv_right_menu:
                //获取到的报警时间
                String substring1 = devicePirtime.getText().toString().substring(0, devicePirtime.getText().length() - 1);
                int senseTime = Integer.parseInt(substring1);
                //获取灵敏度
                if (deviceSensitivity.getText().toString().equals("高")) {
                    senseSensitivity = 1;
                } else {
                    senseSensitivity = 2;
                }
                //报警铃声
                if (tvPirRingtone.getText().equals("你是谁呀")) {
                    pirringtone1 = 1;
                } else if (tvPirRingtone.getText().equals("嘟嘟声")) {
                    pirringtone1 = 2;
                } else if (tvPirRingtone.getText().equals("警报声")) {
                    pirringtone1 = 3;
                } else if (tvPirRingtone.getText().equals("尖啸声")) {
                    pirringtone1 = 4;
                } else if (tvPirRingtone.getText().equals("静音(默认)")) {
                    pirringtone1 = 5;
                }
                //抓拍数量
                String substring = tvContinuousCapture.getText().toString().substring(0, devicePirtime.getText().length() - 1);
                int capture_num = Integer.parseInt(substring);
                icvss.equesSetDevicePirInfo(uid, senseTime, senseSensitivity, pirringtone1, seekbarAlarmSettingVolume.getProgress(), capture_num, alarmMode);
                finish();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private AlertDialog.Builder creatDialog() {
        if (alter == null) {
            alter = new AlertDialog.Builder(EquesSettingListActivity.this);
        }
        return alter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                alarmtime = data.getStringExtra("ALARMTIME");
                alarmTime = Integer.parseInt(alarmtime);
                devicePirtime.setText(alarmtime + "秒");
                break;
            case 2:
                int index = data.getIntExtra("INDEX", -1);
                selectDoorRing(index);
                break;
            case 3:
                int capturenum = data.getIntExtra("CAPTURENUM", -1);
                switch (capturenum) {
                    case 0:
                        tvContinuousCapture.setText(1 + "张");
                        break;
                    case 1:
                        tvContinuousCapture.setText(3 + "张");
                        break;
                    case 2:
                        tvContinuousCapture.setText(5 + "张");
                        break;
                }

                break;
            case 4:
                pirringtone1 = data.getIntExtra("PIRRINGTONE", -1);
                if (pirringtone1 != -1) {
                    switch (pirringtone1) {
                        case 0:
                            tvPirRingtone.setText("你是谁呀");
                            seekbarAlarmSettingVolume.setEnabled(true);
                            break;
                        case 1:
                            tvPirRingtone.setText("嘟嘟声");
                            seekbarAlarmSettingVolume.setEnabled(true);
                            break;
                        case 2:
                            tvPirRingtone.setText("警报声");
                            seekbarAlarmSettingVolume.setEnabled(true);
                            break;
                        case 3:
                            tvPirRingtone.setText("尖啸声");
                            seekbarAlarmSettingVolume.setEnabled(true);
                            break;
                        case 4:
                            tvPirRingtone.setText("静音(默认)");
                            seekbarAlarmSettingVolume.setEnabled(false);
                            break;
                    }
                }
                break;
        }

    }

    @Override
    public void resAddDevices(BaseResponse bean) {

    }

    @Override
    public void resDeleteDevices(BaseResponse bean) {
//        if (bean.getHeader().getHttp_code().equals("200")) {
            icvss.equesDelDevice(bid);
//        }
    }

    @Override
    public void queryDevicesResult(QueryDeviceListResponse bean) {

    }

    @Override
    public void resReqGateWayInfo(QueryGateWayInfoReq body) {
        LogUtil.e("123456","3333");
    }

    @Override
    public void loginSuccess(Object obj) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
