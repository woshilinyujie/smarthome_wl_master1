package com.fbee.smarthome_wl.ui.gateway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.GatewayListAdapter;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.GateSnidEvent;
import com.fbee.smarthome_wl.event.SwitchDataEvent;
import com.fbee.smarthome_wl.event.UpDataGwName;
import com.fbee.smarthome_wl.event.UpdateGwEvent;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.request.DeleteGateWayReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.corecode.CaptureActivity;
import com.fbee.smarthome_wl.utils.AES256Encryption;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.common.AppContext.getmOurDevices;
import static com.fbee.smarthome_wl.ui.main.homepage.HomeFragment.adduser;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;


public class GatewayListActicity extends BaseActivity<GateWayManagerContract.Presenter> implements GateWayManagerContract.View {

    private SwipeMenuListView gateways;
    private List<LoginResult.BodyBean.GatewayListBean> gatewayList;
    private GatewayListAdapter gatewayListAdapter;
    private TextView title;
    private LoginResult.BodyBean.GatewayListBean deleteItem;
    private TextView addGateway;
    private AlertDialog alertDialog;
    private String user;
    private String passwd;
    private ImageView back;
    private String name;
    private LoginResult.BodyBean bodyBean;
    private AddGateWayReq gwBean;
    private String username;
    private Subscription mAddSub;
    private String gwSnid;
    private LoginResult.BodyBean.GatewayListBean gw;
    private String username1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_list_acticity);
    }

    @Override
    protected void initView() {
        initApi();
        createPresenter(new GateWayManagerPresenter(this));
        gateways = (SwipeMenuListView) findViewById(R.id.ls_gateway);
        title = (TextView) findViewById(R.id.title);
        addGateway = (TextView) findViewById(R.id.tv_right_menu);
        back = (ImageView) findViewById(R.id.back);
    }

    @Override
    protected void initData() {
        addGateWay();
        receiveGateWayInfo();
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("网关管理");
        addGateway.setText("添加");
        addGateway.setVisibility(View.VISIBLE);
        addGateway.setOnClickListener(this);
        bodyBean = AppContext.getInstance().getBodyBean();
        if (bodyBean != null) {
            gatewayList = AppContext.getInstance().getBodyBean().getGateway_list();
            if (gatewayList == null) {
                gatewayList = new ArrayList<LoginResult.BodyBean.GatewayListBean>();
            } else {
                gatewayList = AppContext.getInstance().getBodyBean().getGateway_list();
            }
            if (gatewayList != null) {
                gatewayListAdapter = new GatewayListAdapter(GatewayListActicity.this, gatewayList);
                gateways.setAdapter(gatewayListAdapter);
            }
        }
        gateways.setMenuCreator(creator);
        gateways.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 1:
                        showDelateDialog(GatewayListActicity.this, position);
                        break;
                    case 0:
                        showUpdataName(position);
                        break;


                }
                return false;
            }
        });

        mSubscription = RxBus.getInstance().toObservable(UpdateGwEvent.class)
                .compose(TransformUtils.<UpdateGwEvent>defaultSchedulers())
                .subscribe(new Action1<UpdateGwEvent>() {

                    @Override
                    public void call(UpdateGwEvent upDataGwName) {
                        gatewayList = AppContext.getInstance().getBodyBean().getGateway_list();
                        if (null != gatewayListAdapter)
                            gatewayListAdapter.notifyDataSetChanged();
                    }

                });

        gateways.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                showUpdataName(position);
                Intent intent = new Intent(GatewayListActicity.this, GateWayQrcodeActivity.class);
                intent.putExtra("Bean", gatewayList.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * 接收网关信息
     */
    private void receiveGateWayInfo() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                receiveGateWayInfo();
            }
        };

        //注册RXbus接收arriveReport_CallBack（）回调中的数据
//        Subscription gateWaySubscription = RxBus.getInstance().toObservable(GateWayInfo.class)
//                .onBackpressureBuffer(10000)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<GateWayInfo>() {
//
//                    @Override
//                    public void call(GateWayInfo event) {
//                        if (event == null) return;
//                        int virtualValueLength = ByteStringUtil.getVirtualValueLength(event.getUname());
//                        byte[] virtualname = new byte[virtualValueLength];
//                        for (int i = 0; i < virtualValueLength; i++) {
//                            virtualname[i] = event.getUname()[i];
//                        }
//                        String gwuname = new String(virtualname);
//                        for (int i = 0; i < gatewayList.size(); i++) {
//                            if (gwuname.equals(gatewayList.get(i).getUsername())) {
//                                gatewayList.get(i).setVersion(new String(event.getVer()));
//                                gatewayListAdapter.notifyDataSetChanged();
//                            }
//                        }
//
//                    }
//                }, onErrorAction);
//        mCompositeSubscription.add(gateWaySubscription);
    }

    public void showUpdataName(final int position) {
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name, null);
        TextView nameTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        nameTitle.setText("修改设备名称");
        final EditText editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        editText.setFilters(new InputFilter[]{Api.filter});
        editText.setText(gatewayList.get(position).getNote());
        Selection.setSelection(editText.getText(), editText.getText().length());
        TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {

            private String psw;

            @Override
            public void onClick(View v) {
                name = editText.getText().toString().trim();
                if (name != null && name.isEmpty()) {
                    showToast("请输入设备名称");
                    return;
                } else {
                    for (int i = 0; i < gatewayList.size(); i++) {
                        if (name.equals(gatewayList.get(i).getNote())) {
                            showToast("该昵称已存在");
                            return;
                        }
                    }
                    username = gatewayList.get(position).getUsername();
                    if (AppUtil.isMobileNO(username)) {
                        psw = "123456";
                    } else {
                        psw = gatewayList.get(position).getPassword();
                    }
                    gatewayList.get(position).setNote(name);
                    gatewayListAdapter.notifyDataSetChanged();
                    if (!TextUtils.isEmpty(psw))
                        addgateway(username, name, psw);
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

    }

    private void addGateWay() {
        Subscription gateWaySubscription = RxBus.getInstance().toObservable(GateSnidEvent.class)
                .compose(TransformUtils.<GateSnidEvent>defaultSchedulers())
                .subscribe(new Action1<GateSnidEvent>() {
                    @Override
                    public void call(GateSnidEvent event) {
                        if (null == user) return;
                        boolean isAdd = true;
                        if (gatewayList != null && gatewayList.size() > 0) {
                            for (int i = 0; i < gatewayList.size(); i++) {
                                String snid = gatewayList.get(i).getUuid();
                                if (event.snid.equals(snid)) {
                                    isAdd = false;
                                    break;
                                }
                            }
                        }
                        if (isAdd) {
                            AddGateWayReq bodyBean = new AddGateWayReq();
                            bodyBean.setVendor_name(FactoryType.FBEE);
                            bodyBean.setUuid(event.snid);
                            bodyBean.setUsername(user);
                            try {
                                bodyBean.setPassword(AES256Encryption.encrypt(passwd, AppContext.getGwSnid()));
                            } catch (Exception e) {
                            }
                            bodyBean.setAuthorization("admin");
                            bodyBean.setNote(user);
                            bodyBean.setVersion(AppContext.getVer());
                            //网关地理信息
                            AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();
                            location.setCountries(AppContext.getMcountryName());
                            location.setProvince(AppContext.getMadminArea());
                            location.setCity(AppContext.getMlocality());
                            location.setPartition(AppContext.getMsubLocality());
                            location.setStreet(AppContext.getMfeatureName());
                            bodyBean.setLocation(location);
                            LoginResult.BodyBean.GatewayListBean gw = new LoginResult.BodyBean.GatewayListBean();
                            //存储当前网关
                            gw.setUsername(user);
                            gw.setPassword(AES256Encryption.encrypt(passwd, AppContext.getGwSnid()));
                            gw.setAuthorization("admin");
                            gw.setVendor_name(FactoryType.FBEE);
                            gw.setUuid(event.snid);
                            gw.setVersion(AppContext.getVer());
                            gw.setNote(user);
                            //保存最后登录的网关信息
                            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                            presenter.reqUpdateGateWay(bodyBean, username);
                            RxBus.getInstance().post(new UpDataGwName(user, true));
                        }

                    }
                });
        mCompositeSubscription.add(gateWaySubscription);
    }

    private void showDelateDialog(final Context context, final int position) {
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("是否确定删除？").cancelable(false).title("提示")
                .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("删除");

        DialogManager.showDialog(context, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                deleteItem = gatewayList.get(position);
                gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                if (gw == null) {
                    String local_username = PreferencesUtils.getString(LOCAL_USERNAME);
                    if (deleteItem.getUsername().equals(local_username)) {
                        hideLoadingDialog();
                        showToast("当前网关不能删除");
                        return;
                    }
                } else {
                    if (deleteItem.getUsername().equals(gw.getUsername())) {
                        hideLoadingDialog();
                        showToast("当前网关不能删除");
                        return;
                    }
                }
                if (AppUtil.isMobileNO(gatewayList.get(position).getUsername())) {
                    QueryDevicesListInfo body = new QueryDevicesListInfo();
                    body.setUuid(gatewayList.get(position).getUuid());
                    body.setVendor_name("virtual");
                    presenter.reqGateWayInfo(body);
                } else {
                    detele(position);
                }
            }
        });
    }

    private void detele(final int position) {
        showLoadingDialog("正在删除设备");
        DeleteGateWayReq body = new DeleteGateWayReq();
        body.setUuid(deleteItem.getUuid());
        body.setVendor_name(deleteItem.getVendor_name());
        presenter.reqDeleteGateWay(body);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_menu:
                if (AppContext.getInstance().getBodyBean().getGateway_list() != null && AppContext.getInstance().getBodyBean().getGateway_list().size() > 9) {
                    showToast("最多添加10个网关");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyRecordPermissions(this);
                } else {
                    startActivityForResult(new Intent(this, CaptureActivity.class), 88);
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private static String[] PERMISSIONS_RECORD = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};

    private void verifyRecordPermissions(GatewayListActicity captureActivity) {
        //摄像头权限
        if (ContextCompat.checkSelfPermission(captureActivity,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //先判断有没有权限 ，没有就在这里进行权限的申请
            ActivityCompat.requestPermissions(this, PERMISSIONS_RECORD, 2);
        } else {
            startActivityForResult(new Intent(this, CaptureActivity.class), 88);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(GatewayListActicity.this, CaptureActivity.class), 88);
            } else {
                Intent intent = new Intent(GatewayListActicity.this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isPermission", true);
                startActivityForResult(intent, 88, bundle);
            }
        }
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {

            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                    0xCE)));
            // set item width
            openItem.setWidth(AppUtil.dp2px(GatewayListActicity.this, 75));
            // set item title
            openItem.setTitle("编辑");
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);


            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(AppUtil.dp2px(GatewayListActicity.this, 75));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);


        }
    };

    //飞比登录返回
    @Override
    public void loginFbeeResult(int result) {
        switch (result) {
            case 1:
                //清空飞比数据缓存
                getmOurDevices().clear();
                AppContext.getmOurScenes().clear();
                AppContext.getmOurGroups().clear();
                AppContext.getmDoorLockDevices().clear();
                //通知页面更新数据
                RxBus.getInstance().post(new SwitchDataEvent());
                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppContext.getInstance().getSerialInstance().getDevices();
                        AppContext.getInstance().getSerialInstance().getSences();
                    }
                });
                //移康退出
                icvss.equesUserLogOut();
                Api.jpushSetting(this, user);
                //移康登录
                icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, user, EquesConfig.APPKEY);
                AppContext.getInstance().getSerialInstance().getGateWayInfo();
                //是否是新网关
                boolean flag = true;
                List<LoginResult.BodyBean.GatewayListBean> mGateWaylist = AppContext.getInstance().getBodyBean().getGateway_list();
                if (mGateWaylist != null && mGateWaylist.size() > 0) {
                    for (int i = 0; i < mGateWaylist.size(); i++) {
                        String username = mGateWaylist.get(i).getUsername();
                        if (user.equals(username)) {
                            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), mGateWaylist.get(i));
                            hideLoadingDialog();
                            RxBus.getInstance().post(new UpDataGwName(user, true));
                            flag = false;
                            break;
                        }
                    }
                }

                if (null != user && flag) {
                    showLoadingDialog("网关登录成功，添加中...");
                    mAddSub = Observable.timer(5000, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    showToast("添加网关失败");
                                    hideLoadingDialog();
                                    if (AppUtil.isNetworkAvailable(GatewayListActicity.this)) {
                                        user = null;
                                        LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                                        if (null != gw)
                                            presenter.loginFbee(gw.getUsername(), gw.getPassword());
                                    } else {
                                        showToast("当前无网络连接");
                                    }
                                }
                            });
                }


                break;
            case -4:
                hideLoadingDialog();
                if (null != mAddSub)
                    mAddSub.unsubscribe();
                showToast("网关登录人数已达到上限!");
                break;
            case -2:
                hideLoadingDialog();
                if (null != mAddSub)
                    mAddSub.unsubscribe();
                showToast("网关账号或密码错误!");
                break;
            case -3:
                hideLoadingDialog();
                if (null != mAddSub)
                    mAddSub.unsubscribe();
                showToast("网关登录超时！");
                break;

        }
    }

    /***
     * 这个地方是修改网关名称
     */
    private void addgateway(String username, String name, String psw) {
        String uuid = null;
        List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
        for (int i = 0; i < gateway_list.size(); i++) {
            String username1 = gateway_list.get(i).getUsername();
            if (username1.equals(username)) {
                uuid = gateway_list.get(i).getUuid();
                break;
            }
        }
        if (AppUtil.isMobileNO(username)) {
            gwBean = new AddGateWayReq();
            gwBean.setVendor_name("virtual");
            gwBean.setUuid(username);
            gwBean.setAuthorization("admin");
            gwBean.setUsername(username);
            gwBean.setPassword("123456");
            gwBean.setNote(name);
            gwBean.setVersion("1.0.0");
        } else {
            gwBean = new AddGateWayReq();
            gwBean.setVendor_name(FactoryType.FBEE);
            gwBean.setUuid(uuid);
            gwBean.setAuthorization("admin");
            gwBean.setUsername(username);
            if (uuid == null) {
                return;
            }
            try {
                gwBean.setPassword(AES256Encryption.encrypt(psw, uuid));
            } catch (Exception e) {
            }
            if (TextUtils.isEmpty(name)) {
                gwBean.setNote(username);
            } else {
                gwBean.setNote(name);
            }
            gwBean.setVersion(AppContext.getVer());
        }
        AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();
        String mcountryName = AppContext.getMcountryName();
        location.setCountries(mcountryName);
        location.setProvince(AppContext.getMadminArea());
        location.setCity(AppContext.getMlocality());
        location.setPartition(AppContext.getMsubLocality());
        location.setStreet(AppContext.getMfeatureName());
        gwBean.setLocation(location);
        presenter.reqUpdateGateWay(gwBean,username);
    }

    @Override
    public void resDeleteGateWay(BaseResponse bean) {
        hideLoadingDialog();
        if (bean != null && deleteItem != null) {
            if ("200".equals(bean.getHeader().getHttp_code())) {
                ToastUtils.showShort("删除成功!");
                hideLoadingDialog();
                gatewayList.remove(deleteItem);
                RxBus.getInstance().post(new UpDataGwName());
                gatewayListAdapter.notifyDataSetChanged();
            } else {
                ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
            }
        }
    }

    @Override
    public void resReqGateWayInfo(QueryGateWayInfoReq body) {
        List<QueryGateWayInfoReq.BodyEntity.DeviceListEntity> device_list = body.getBody().getDevice_list();
        if (device_list != null && device_list.size() > 0) {
            showToast("请删除已绑定的设备");
        } else {
            gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
            if (body.getBody().getUsername().equals(gw.getUsername())) {
                hideLoadingDialog();
                showToast("当前网关不能删除");
                return;
            } else {
                DeleteGateWayReq bean = new DeleteGateWayReq();
                bean.setUuid(body.getBody().getUsername());
                bean.setVendor_name("virtual");
                presenter.reqDeleteGateWay(bean);
            }
        }
    }

    //网关添加成功后页面修改
    @Override
    public void resAddGateWay(BaseResponse bean, String username) {
        hideLoadingDialog();
        //修改昵称
        if (bean.getHeader().getHttp_code().equals("200") && name != null) {
            if (null != mAddSub)
                mAddSub.unsubscribe();
            if(user==null){
                KeepGwInfo(username, name);
            }else{
                KeepGwInfo(user, name);
            }
            RxBus.getInstance().post(new UpDataGwName(name, false));
            //添加网关
        } else if (bean.getHeader().getHttp_code().equals("200") && user != null) {
            if (null != mAddSub)
                mAddSub.unsubscribe();
            //通知页面更新数据
            KeepGwInfo(user, user);
            RxBus.getInstance().post(new UpDataGwName(user, true));

        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }

    }

    private void KeepGwInfo(String user, String note) {
        //存储当前网关
        LoginResult.BodyBean.GatewayListBean gw = new LoginResult.BodyBean.GatewayListBean();
        if (AppUtil.isMobileNO(user)) {
            gw.setUsername(user);
            gw.setNote(note);
            gw.setPassword("123456");
            gw.setAuthorization("admin");
            gw.setVendor_name(FactoryType.FBEE);
            gw.setUuid(user);
            gw.setVersion("1.0.0");
        } else {
            gw.setUsername(user);
            gw.setNote(note);
            gw.setPassword(AES256Encryption.encrypt(passwd, AppContext.getGwSnid()));
            gw.setAuthorization("admin");
            gw.setVendor_name(FactoryType.FBEE);
            gw.setUuid(AppContext.getGwSnid());
            gw.setVersion(AppContext.getVer());
        }
        boolean isexist = true;
        for (int i = 0; i < gatewayList.size(); i++) {
            gwSnid = AppContext.getGwSnid();
            if (gwSnid == null)
                gwSnid = PreferencesUtils.getString(LOCAL_USERNAME);
            if (gatewayList.get(i).getUuid().equals(gwSnid)) {
                isexist = false;
                break;
            }
        }
        if (isexist) {
            gatewayList.add(gw);
            gatewayListAdapter.notifyDataSetChanged();
            AppContext.getInstance().getBodyBean().setGateway_list(gatewayList);
            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 88) {
            adduser = null;
            try {
                String result = data.getExtras().getString("result");
                user = result.split("GT")[1].split("pass")[0];
                if (null != user) {
                    while (user.startsWith("0")) {
                        user = user.substring(1);
                    }
                } else {
                    showToast("扫描失败");
                }

                //虚拟
                if (AppUtil.isMobileNO(user)) {
                    //移康退出
                    icvss.equesUserLogOut();
                    icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, user, EquesConfig.APPKEY);
                    LoginResult.BodyBean.GatewayListBean bean = null;
                    List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                    boolean flag = false;
                    String note = user;
                    for (int i = 0; i < gateway_list.size(); i++) {
                        if (gateway_list.get(i).getUsername().equals(user)) {
                            flag = true;
                            note = gateway_list.get(i).getNote();
                            bean = gateway_list.get(i);
                            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gateway_list.get(i));
                        }
                    }

                    if (!flag) {
                        //添加到服务器
                        AddGateWayReq bodyBean = new AddGateWayReq();
                        bodyBean.setVendor_name("virtual");
                        bodyBean.setUuid(user);
                        bodyBean.setUsername(user);
                        bodyBean.setPassword("123456");
                        bodyBean.setAuthorization("admin");
                        bodyBean.setNote(user);
                        bodyBean.setVersion("1.0.0");

                        AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();
                        String mcountryName = AppContext.getMcountryName();
                        location.setCountries(mcountryName);
                        location.setProvince(AppContext.getMadminArea());
                        location.setCity(AppContext.getMlocality());
                        location.setPartition(AppContext.getMsubLocality());
                        location.setStreet(AppContext.getMfeatureName());
                        bodyBean.setLocation(location);
                        presenter.reqUpdateGateWay(bodyBean, username);

                    }


                    AppContext.clearAllDatas();
                    LoginResult.BodyBean mbodyBean = new LoginResult.BodyBean();
                    mbodyBean.setGateway_list(gateway_list);
                    AppContext.getInstance().setBodyBean(mbodyBean);
                    AppContext.getInstance().getSerialInstance().releaseSource();

                    AppContext.getInstance().setmHomebody(null);
                    //通知页面更新数据
                    RxBus.getInstance().post(new SwitchDataEvent());
                    RxBus.getInstance().post(new UpDataGwName(note, true));
                    return;

                }

                passwd = result.split("GT")[1].split("pass")[1];
                if (user != null && passwd.length() > 0) {
                    presenter.loginFbee(user, passwd);
                    showLoadingDialog("正在加载...");
                } else {
                    showToast("扫描失败");
                }

            } catch (Exception e) {
                showToast("扫描失败");
            }

        } else if (resultCode == 66) {
            adduser = null;
            user = data.getExtras().getString("username");
            passwd = data.getExtras().getString("password");
            if (!user.isEmpty() && !passwd.isEmpty()) {
                presenter.loginFbee(user, passwd);
                showLoadingDialog("正在加载...");
            }
        }
    }
}
