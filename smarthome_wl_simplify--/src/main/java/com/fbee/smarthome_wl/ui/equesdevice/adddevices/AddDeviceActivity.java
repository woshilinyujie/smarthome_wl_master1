package com.fbee.smarthome_wl.ui.equesdevice.adddevices;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.AddDeviceInfo;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.event.EquesAddLockEvent;
import com.fbee.smarthome_wl.event.UpDataGwName;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.UpdataApkResponse;
import com.fbee.smarthome_wl.response.UpdateModel;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverPresenter;
import com.fbee.smarthome_wl.ui.corecode.CaptureActivity;
import com.fbee.smarthome_wl.ui.equesdevice.equesaddlock.EquesAddlockActivity;
import com.fbee.smarthome_wl.ui.main.MainContract;
import com.fbee.smarthome_wl.ui.main.MainPresenter;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

import static com.fbee.smarthome_wl.base.BaseCommonPresenter.mSeqid;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * 添加猫眼设备
 */
public class AddDeviceActivity extends BaseActivity<MainContract.Presenter> implements RadioGroup.OnCheckedChangeListener, MainContract.View {

    private TextView AddDevice;
    private String wifiSsid;
    private ImageView QRCode;
    private TextView title;
    private ImageView back;
    private EditText passWord;
    private LinearLayout dialog;
    private LinearLayout greatest;
    private TextView cancel;
    private TextView wifiName;
    private LoginResult.BodyBean.GatewayListBean gw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
    }

    @Override
    protected void initView() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiSsid = getWifiInfoSSid(wifiManager);
        dialog = (LinearLayout) findViewById(R.id.ll_dialog);
        AddDevice = (TextView) findViewById(R.id.add_device);
        wifiName = (TextView) findViewById(R.id.tv_wifi);
        QRCode = (ImageView) findViewById(R.id.qr_code);
        passWord = (EditText) findViewById(R.id.wifi_password);
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.back);
        greatest = (LinearLayout) findViewById(R.id.ll_greatest);
        cancel = (TextView) findViewById(R.id.tv_cancel);
        back.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(this);
        back.setOnClickListener(this);
        title.setText("添加猫眼");
        Bitmap bitmap = icvss.equesCreateQrcode(null, null,
                EquesConfig.KEYID, null,
                BuddyType.TYPE_WIFI_DOOR_R22, 500);
        QRCode.setImageBitmap(bitmap);
        wifiName.setText("当前WiFi:" + wifiSsid + "请");
    }

    @Override
    protected void initData() {
        gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        createPresenter(new MainPresenter(this));
        initApi();
        AddDevice.setOnClickListener(this);
        mSubscription = RxBus.getInstance().toObservable(AddDeviceInfo.class)
                .compose(TransformUtils.<AddDeviceInfo>defaultSchedulers())
                .subscribe(new Action1<AddDeviceInfo>() {

                    public void call(AddDeviceInfo event) {
                        if (event.getReqid() != null) {
                            LoginResult.BodyBean.GatewayListBean wangguan = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                            if (wangguan == null) {
                                LoginResult.BodyBean.GatewayListBean gw = new LoginResult.BodyBean.GatewayListBean();
                                gw.setUsername(PreferencesUtils.getString(LOCAL_USERNAME));
                                gw.setNote(PreferencesUtils.getString(LOCAL_USERNAME));
                                gw.setPassword("123456");
                                gw.setAuthorization("admin");
                                gw.setVendor_name("virtual");
                                gw.setUuid(PreferencesUtils.getString(LOCAL_USERNAME));
                                gw.setVersion("1.0.0");
                                //保存最后登录的网关信息
                                List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                                if (gateway_list == null) {
                                    gateway_list = new ArrayList<LoginResult.BodyBean.GatewayListBean>();
                                }
                                PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                                gateway_list.add(gw);
                                AppContext.getInstance().getBodyBean().setGateway_list(gateway_list);
                                AddGateWayReq bodyBean = new AddGateWayReq();
                                bodyBean.setVendor_name("virtual");
                                bodyBean.setUuid(PreferencesUtils.getString(LOCAL_USERNAME));
                                bodyBean.setUsername(PreferencesUtils.getString(LOCAL_USERNAME));
                                bodyBean.setPassword("123456");
                                bodyBean.setAuthorization("admin");
                                bodyBean.setNote(PreferencesUtils.getString(LOCAL_USERNAME));
                                bodyBean.setVersion("1.0.0");
                                addGateway(bodyBean, event);
                            } else {
                                icvss.equesAckAddResponse(event.getReqid(), 1);
                            }
                        }
                    }
                });
        //猫眼确认添加到网关后报上来的数据
        receiveEquessAddLockEvent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_device:
                String wifiPwd = passWord.getText().toString();
                //添加猫眼时没有网关
                if (null == gw) {
                    showDelateDialog(this);
                } else {
                    if (null == wifiSsid || wifiSsid.equals("<unknown ssid>")) {
                        showToast("请确保已连接Wi-Fi");
                    } else {
                        Bitmap bitmap = icvss.equesCreateQrcode(wifiSsid, wifiPwd,
                                EquesConfig.KEYID, gw.getUsername(),
                                BuddyType.TYPE_WIFI_DOOR_R22, 500);
                        QRCode.setImageBitmap(bitmap);
                        dialog.setVisibility(View.GONE);
                        greatest.setBackgroundColor(getResources().getColor(R.color.gray_bg));
                    }
                }

                break;
            case R.id.tv_cancel:
            case R.id.back:
                finish();
                break;
        }
    }


    private void showDelateDialog(final Context context) {
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("添加猫眼设备前，请先添加网关！").cancelable(true).title("提示")
                .leftBtnText("跳过").Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("添加");
        DialogManager.showDialog(context, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onLeftClick() {
                String wifiPwd = passWord.getText().toString();
                if (wifiSsid.equals("<unknown ssid>")) {
                    showToast("请确保已连接Wi-Fi");
                } else {
                    icvss.equesLogin(AddDeviceActivity.this, EquesConfig.SERVER_ADDRESS, PreferencesUtils.getString(LOCAL_USERNAME), EquesConfig.APPKEY);
                    Bitmap bitmap = icvss.equesCreateQrcode(wifiSsid, wifiPwd,
                            EquesConfig.KEYID, PreferencesUtils.getString(LOCAL_USERNAME),
                            BuddyType.TYPE_WIFI_DOOR_R22, 500);
                    QRCode.setImageBitmap(bitmap);
                    dialog.setVisibility(View.GONE);
                    greatest.setBackgroundColor(getResources().getColor(R.color.gray_bg));
                }

            }

            @Override
            public void onRightClick() {
                Intent intent = new Intent(AddDeviceActivity.this, CaptureActivity.class);
                intent.putExtra("ISADD", "addGw");
                startActivity(intent);
            }


        });
    }


    //添加网关
    public void addGateway(AddGateWayReq bodyBean, final AddDeviceInfo event) {

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);

            }

            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onNext(JsonObject json) {
                if (null != json) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    BaseResponse info = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                    if (info.getHeader().getHttp_code().equals("200")) {
                        RxBus.getInstance().post(new UpDataGwName(PreferencesUtils.getString(LOCAL_USERNAME)));
                        icvss.equesAckAddResponse(event.getReqid(), 1);
                    } else {
                        icvss.equesUserLogOut();
                        showToast("网关添加失败...");
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


    // 获取WI-FI信息
    public String getWifiInfoSSid(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            String ssid = wifiInfo.getSSID();
            if (ssid != null) {
                int i = ssid.indexOf("\"");
                if (i != -1) {
                    ssid = ssid.replaceAll("\"", "");
                }
            }
            return ssid;
        }
        return null;
    }

    /***
     * 添加猫眼设备到服务器
     *
     * @param bid
     */
    public void addEquesToserver(String bid) {
        AddDevicesReq addDevicesReq = new AddDevicesReq();
        AddDevicesReq.DeviceBean deviceBean = new AddDevicesReq.DeviceBean();
        addDevicesReq.setGateway_vendor_name(FactoryType.FBEE);
        addDevicesReq.setGateway_uuid(AppContext.getGwSnid());
        deviceBean.setVendor_name(FactoryType.EQUES);
        deviceBean.setUuid(bid);  //猫眼的bid
        deviceBean.setType(FactoryType.EQUESCATEYE);
        addDevicesReq.setDevice(deviceBean);
        presenter.reqAddDevices(addDevicesReq);

    }

    /**
     * 猫眼确认添加到网关后报上来的数据
     */
    private void receiveEquessAddLockEvent() {

        Subscription mSubscriptionState = RxBus.getInstance().toObservable(EquesAddLockEvent.class)
                .compose(TransformUtils.<EquesAddLockEvent>defaultSchedulers())
                .subscribe(new Action1<EquesAddLockEvent>() {
                    private String gwSnid;

                    @Override
                    public void call(EquesAddLockEvent event) {
                        String topActivity = AppUtil.getTopActivity(AddDeviceActivity.this);
                        if (topActivity.equals("com.fbee.smarthome_wl.ui.equesdevice.adddevices.AddDeviceActivity")) {
                            icvss.equesGetDeviceList();
                            String bid = event.getAdded_bdy().getBid();
                            AddDevicesReq addDevicesReq = new AddDevicesReq();
                            AddDevicesReq.DeviceBean deviceBean = new AddDevicesReq.DeviceBean();
                            gwSnid = AppContext.getGwSnid();
                            if (gwSnid == null) {
                                gwSnid = PreferencesUtils.getString(LOCAL_USERNAME);
                                addDevicesReq.setGateway_vendor_name("virtual");
                            } else {
                                addDevicesReq.setGateway_vendor_name(FactoryType.FBEE);
                            }
                            addDevicesReq.setGateway_uuid(gwSnid);
                            deviceBean.setVendor_name(FactoryType.EQUES);
                            deviceBean.setUuid(bid);  //猫眼的bid
                            deviceBean.setType(FactoryType.EQUESCATEYE);
                            addDevicesReq.setDevice(deviceBean);
                            presenter.reqAddDevices(addDevicesReq);
                            addEquesToserver(bid);
                            if (AppContext.getmDoorLockDevices() != null && AppContext.getmDoorLockDevices().size() > 0) {
                                Bundle bundle = new Bundle();
                                bundle.putString(Method.ATTR_BUDDY_BID, bid);
                                skipAct(EquesAddlockActivity.class, bundle);
                                finish();
                            } else {
                                showToast("当前没有门锁可绑定");
                                finish();
                            }
                        }
                    }
                });
        mCompositeSubscription.add(mSubscriptionState);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void resUpdate(UpdateModel updateinfo) {

    }

    @Override
    public void resGwCallback(UpdataApkResponse updateinfo) {

    }

    @Override
    public void resAddDeviceUser(BaseResponse bean) {

    }

    @Override
    public void resAddToken(AddTokenResponse bean) {

    }
}
