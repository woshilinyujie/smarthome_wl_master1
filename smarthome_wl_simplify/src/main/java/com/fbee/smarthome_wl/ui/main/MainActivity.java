package com.fbee.smarthome_wl.ui.main;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.bean.ArriveReportCBInfo;
import com.fbee.smarthome_wl.bean.DoorLockStateInfo;
import com.fbee.smarthome_wl.bean.GateWayInfo;
import com.fbee.smarthome_wl.bean.HintCountInfo;
import com.fbee.smarthome_wl.bean.ModifyAccountInfo;
import com.fbee.smarthome_wl.bean.ModifyAliars;
import com.fbee.smarthome_wl.common.ActivityPageManager;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.constant.DoorLockGlobal;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.dbutils.DoorLockDbUtil;
import com.fbee.smarthome_wl.dbutils.UserDbUtil;
import com.fbee.smarthome_wl.event.HomePagerUpMain;
import com.fbee.smarthome_wl.event.SwitchFragmentEvent;
import com.fbee.smarthome_wl.event.UpdateProgressEvevt;
import com.fbee.smarthome_wl.event.UserHaderIconChange;
import com.fbee.smarthome_wl.greendao.Doorlockrecord;
import com.fbee.smarthome_wl.greendao.User;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.PusBodyBean;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.response.UpdataApkResponse;
import com.fbee.smarthome_wl.response.UpdateModel;
import com.fbee.smarthome_wl.ui.aboutinfo.AboutInfoActivity;
import com.fbee.smarthome_wl.ui.accountinformation.PersonAccountInfoActivity;
import com.fbee.smarthome_wl.ui.areamanager.AreaManagerActivity;
import com.fbee.smarthome_wl.ui.corecode.AddGatewayActivity;
import com.fbee.smarthome_wl.ui.devicemanager.DeviceManagerActivity;
import com.fbee.smarthome_wl.ui.doorlocklog.DoorLockLogActivity;
import com.fbee.smarthome_wl.ui.gateway.GatewayListActicity;
import com.fbee.smarthome_wl.ui.jpush.MenuJpushActivity;
import com.fbee.smarthome_wl.ui.main.area.AreaFragment;
import com.fbee.smarthome_wl.ui.main.equipment.EquipmentFragment;
import com.fbee.smarthome_wl.ui.main.homepage.HomeFragment;
import com.fbee.smarthome_wl.ui.main.scenario.ScenarioFragment;
import com.fbee.smarthome_wl.ui.mainsimplify.find.FindFragment;
import com.fbee.smarthome_wl.ui.mainsimplify.home.HomeSimpleFragment;
import com.fbee.smarthome_wl.ui.mainsimplify.my.MyFragment;
import com.fbee.smarthome_wl.ui.plan.PlanListActivity;
import com.fbee.smarthome_wl.ui.rule.RuleListActivity;
import com.fbee.smarthome_wl.ui.scenario.ScenaManagerActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.BadgeUtil;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.DownloadProgressUtil;
import com.fbee.smarthome_wl.utils.FileUtils;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.MessageUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.TimerCount;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.Tool;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.WeakHandler;
import com.fbee.smarthome_wl.view.NoScrollViewPager;
import com.fbee.smarthome_wl.view.loopswitch.AutoSwitchAdapter;
import com.fbee.smarthome_wl.view.loopswitch.AutoSwitchView;
import com.fbee.smarthome_wl.view.loopswitch.LoopModel;
import com.fbee.smarthome_wl.widget.dialog.AlamDialogActivity;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.Serial;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.R.id.main_equipment_management;
import static com.fbee.smarthome_wl.common.AppContext.timerMap;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

public class MainActivity extends BaseActivity<MainContract.Presenter> implements RadioGroup.OnCheckedChangeListener, MainContract.View, Handler.Callback {
    private static final String TAG = "JPUSH";
    private DrawerLayout activityMain;
    private NoScrollViewPager mainFragment;
    private RadioGroup mainTab;
    private RadioButton mainHomeBtn;
    private RadioButton mainEquipmentBtn;
    private RadioButton mainAreaBtn;
    private RadioButton mainScenarioBtn;
    private RelativeLayout rlUserPicture;
    private ImageView userPicture;
    private LinearLayout alreadyLogin;
    private TextView userNick;
    private TextView userPhone;
    private LinearLayout mainRules;
    private LinearLayout mainPlans;
    private LinearLayout mainEquipmentManagement;
    private LinearLayout mainSceneManagement;
    private LinearLayout mainAreaManagement;
    private LinearLayout mainPush;
    private LinearLayout mainAboutInfo;
    private View rl_pb;
    private ProgressBar pb;
    private View leftMenuBottomLine;
    private TextView tvSetting;
    private String deviceName;
    private String alamName;
    private boolean isAlarm = false;// 用于控制是否播放报警声音
    private NotificationCompat.Builder builder;
    private MediaPlayer mediaplayer;
    public static final String DOORMESSAGECOUNT = "doorMessageCount";
    //首页
    private HomeFragment homeFragment;
    //设备
    private EquipmentFragment equipmentFragment;
    //区域
    private AreaFragment areaFragment;
    //场景
    private ScenarioFragment scenarioFragment;
    private TextView tvGateway;
    private LinearLayout gatewayList;
    private HashMap<String, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean>> userEntityMap;
    private String phone;
    private LoginResult.BodyBean.GatewayListBean gw;
    private List<BaseFragment> fragmentList;
    private long mExitTime = 0;
    private WeakHandler handler;
    private String json;
    private AudioManager audioManager = null; // 音频

    private ProgressDialog mDialog;
    private String ver = null;
    private boolean flag = true;
    private UpdataApkResponse updateinfo = null;
    private Subscription subInterval;
    ProgressDialog pdialog;
    private DoorLockDbUtil doorLockDbUtil;
    private AutoSwitchView loopswitch;
    MyFragmentPagerAdapter fadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AddGatewayActivity.addGatewayActivity != null) {
            AddGatewayActivity.addGatewayActivity.finish();
        }
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    @Override
    protected void initView() {
        fragmentList = new ArrayList<>();
        String model = PreferencesUtils.getString(PreferencesUtils.MODEL);


        createPresenter(new MainPresenter(this));
        activityMain = (DrawerLayout) findViewById(R.id.activity_main);
        mainFragment = (NoScrollViewPager) findViewById(R.id.main_fragment);
        mainTab = (RadioGroup) findViewById(R.id.main_tab);
        gatewayList = (LinearLayout) findViewById(R.id.gatewaylist);
        mainHomeBtn = (RadioButton) findViewById(R.id.main_home_btn);
        mainEquipmentBtn = (RadioButton) findViewById(R.id.main_equipment_btn);
        mainAreaBtn = (RadioButton) findViewById(R.id.main_area_btn);
        mainScenarioBtn = (RadioButton) findViewById(R.id.main_scenario_btn);
        rlUserPicture = (RelativeLayout) findViewById(R.id.rl_user_picture);
        userPicture = (ImageView) findViewById(R.id.user_picture);
        alreadyLogin = (LinearLayout) findViewById(R.id.already_login);
        userNick = (TextView) findViewById(R.id.user_nick);
        userPhone = (TextView) findViewById(R.id.user_phone);
        mainRules = (LinearLayout) findViewById(R.id.main_rules);
        mainPlans = (LinearLayout) findViewById(R.id.main_plans);
        mainEquipmentManagement = (LinearLayout) findViewById(main_equipment_management);
        mainSceneManagement = (LinearLayout) findViewById(R.id.main_scene_management);
        mainAreaManagement = (LinearLayout) findViewById(R.id.main_area_management);
        mainPush = (LinearLayout) findViewById(R.id.main_push);
        mainAboutInfo = (LinearLayout) findViewById(R.id.main_about_info);
        tvGateway = (TextView) findViewById(R.id.tv_gateway);
        //切换模式
        findViewById(R.id.main_change).setOnClickListener(this);
        if ("household".equals(model)) {
            homeFragment = new HomeFragment();
            equipmentFragment = new EquipmentFragment();
            areaFragment = new AreaFragment();
            scenarioFragment = new ScenarioFragment();
            fragmentList.add(homeFragment);
            fragmentList.add(equipmentFragment);
            fragmentList.add(areaFragment);
            fragmentList.add(scenarioFragment);

        } else {
            Drawable top = getResources().getDrawable(R.drawable.main_tab_find_selector);
            mainEquipmentBtn.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            mainScenarioBtn.setVisibility(View.GONE);
            Drawable mytop = getResources().getDrawable(R.drawable.main_tab_my_selector);
            mainAreaBtn.setCompoundDrawablesWithIntrinsicBounds(null, mytop, null, null);

            mainEquipmentBtn.setText("发现");
            mainAreaBtn.setText("我的");
            //关闭手势侧滑
            activityMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            HomeSimpleFragment simple = new HomeSimpleFragment();
            FindFragment find = new FindFragment();
            MyFragment my = new MyFragment();

            fragmentList.clear();
            fragmentList.add(simple);
            fragmentList.add(find);
            fragmentList.add(my);
            if (null == fadapter) {
                fadapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
                mainFragment.setAdapter(fadapter);
            } else {
                fadapter.notifyDataSetChanged();
            }
            mainHomeBtn.setChecked(true);
        }

        rl_pb = findViewById(R.id.rl_pb);
        pb = (ProgressBar) findViewById(R.id.pb);
        //轮播效果
        loopswitch = (AutoSwitchView) findViewById(R.id.loopswitch);
        fadapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mainFragment.setOffscreenPageLimit(3);
        mainFragment.setAdapter(fadapter);
        mainHomeBtn.setChecked(true);


        mDialog = new ProgressDialog(this);
        mDialog.setMax(100);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            verifyRecordPermissions(MainActivity.this);
        initApi();
        //接收模式切换
        receiveModelChange();
        initHandler();
    }

    private void initHandler() {

        if (AppContext.getInstance().getBodyBean() != null) {
            List<LoginResult.BodyBean.GatewayListBean> mGateWaylist = AppContext.getInstance().getBodyBean().getGateway_list();
            if (mGateWaylist != null && mGateWaylist.size() == 0) {
                //轮播图
                ArrayList<LoopModel> datas = new ArrayList<LoopModel>();
                LoopModel model = null;
                List<String> slides = AppContext.getInstance().getSlideshow();
                if (null != slides) {
                    for (int i = 0; i < slides.size(); i++) {
                        model = new LoopModel(slides.get(i), R.mipmap.loop);
                        datas.add(model);
                    }
                } else {
                    model = new LoopModel(null, R.mipmap.loop);
                    datas.add(model);
                    model = new LoopModel(null, R.mipmap.loop);
                    datas.add(model);
                    model = new LoopModel(null, R.mipmap.loop);
                    datas.add(model);
                }
                AutoSwitchAdapter mAdapter = new AutoSwitchAdapter(this, datas);
                mAdapter.setListener(new AutoSwitchAdapter.OnIitemClickListener() {
                    @Override
                    public void onIitemClickListener(int positon) {
                        //showToast(datas.get(positon).getTitle());
                    }
                });
                loopswitch.setAdapter(mAdapter);
                loopswitch.setVisibility(View.VISIBLE);
            } else {
                loopswitch.setVisibility(View.GONE);
            }
        } else {
            loopswitch.setVisibility(View.GONE);
        }

        handler = new WeakHandler(this);
        receiveGateWayInfo();
        //检测网关升级
        PusBodyBean bodyBean = new PusBodyBean();
        bodyBean.setToken("");
        bodyBean.setProduct_name("wonly");
        bodyBean.setPlatform("gateway");
        bodyBean.setEndpoint_type(FactoryType.FBEE);
        presenter.updateGateWay(bodyBean);

        handler.sendEmptyMessageAtTime(1, 1000l);
        //升级回调
        Subscription subscription = RxBus.getInstance().toObservable(UpdateProgressEvevt.class).compose(TransformUtils.<UpdateProgressEvevt>defaultSchedulers()).subscribe(new Action1<UpdateProgressEvevt>() {
            @Override
            public void call(final UpdateProgressEvevt groupInfo) {
                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        showProgressDialog(groupInfo.getUpgradedata());
                    }
                });
            }
        });
        mCompositeSubscription.add(subscription);
    }

    @Override
    protected void initData() {
        initApi();
        updateVersion(); //检查是否有新版本
        if (PreferencesUtils.getString(PreferencesUtils.LOCAL_ALAIRS) != null) {
            userNick.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_ALAIRS));
        }
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                json = getIntent().getExtras().getString("json");
            }
        }
        if (json != null) {
            skipAct(DoorLockLogActivity.class);
        }
        userPhone.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        mainTab.setOnCheckedChangeListener(this);
        String registrationID = JPushInterface.getRegistrationID(this);
        mainPush.setOnClickListener(this);
        rlUserPicture.setOnClickListener(this);
        phone = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        mainSceneManagement.setOnClickListener(this);
        mainEquipmentManagement.setOnClickListener(this);
        mainAreaManagement.setOnClickListener(this);
        gatewayList.setOnClickListener(this);
        mainAboutInfo.setOnClickListener(this);
        mainPlans.setOnClickListener(this);
        mainRules.setOnClickListener(this);

        doorLockDbUtil = DoorLockDbUtil.getIns();
        //初始化消息数量
        notifyBadgeNumber();
        //初始化头像
        initHeaderIcon();
        gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        //接收飞比设备存储到AppContext中mOurDevices内
        receiveFbeeDevicesInfo();
        //注册RXbus接收来自Serial中arriveReportgatedoor_CallBack(int uId, byte[] data,
        //char clusterId, char attribID)的数据
        receiveDoorLockStateInfo();

        //接收门锁电量arriveReport_CallBack(int uId, int data, char clusterId,
        //char attribID)的数据
        receiveDoorLockPowerInfo();
        //接收昵称改变
        receiveAliarsChange();
        //接收用户名改变
        receiveUserNameChange();
        //获取区域列表
        receiveGroups();
        //获取场景列表
        receiveSencess();
        //获取网关信息
        receiveGateWays();
        //获取白名单设备列表
        requestDeviceList();
        //接收头像改变
        receiveHeaderIconChange();
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
        Subscription gateWaySubscription = RxBus.getInstance().toObservable(GateWayInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GateWayInfo>() {
                    @Override
                    public void call(GateWayInfo event) {
                        if (event == null) return;
                        if (loopswitch.getVisibility() == View.VISIBLE)
                            loopswitch.setVisibility(View.GONE);
                        if (flag && updateinfo != null) {
                            ver = new String(event.getVer());
                            int falg = updateinfo.getBody().getNew_version().compareTo(ver);
                            final String url = updateinfo.getBody().getUrl();
                            //有新版本
                            if (falg > 0) {
                                boolean isMust = false;
                                //是否为强制更新  true 为强制更新,版本为2.2.5及以下 强制升级
                                if (ver.compareTo("2.2.5") <= 0) {
                                    ThreadPoolUtils.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            int number = AppContext.getInstance().getSerialInstance().connectLANZll();
                                            if (number > 0) {
                                                final String[] ips = AppContext.getInstance().getSerialInstance().getGatewayIps(number);
                                                final String[] snids = AppContext.getInstance().getSerialInstance().getBoxSnids(number);
                                                final LoginResult.BodyBean.GatewayListBean currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                                                for (int i = 0; i < ips.length; i++) {
                                                    //当前网关与手机在同局域网内
                                                    if (currentGw.getUuid().equals(snids[i])) {
                                                        //提示网关升级
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                showupdateDialog(true, url);
                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                    });

                                }
                                //2.2.5以上版本升级
                                else {
                                    if (updateinfo.getBody().getForce_upgrade().equals("true")) {
                                        isMust = true;
                                    }
                                    showupdateDialog(isMust, updateinfo.getBody().getUrl());
                                }

                            }
                            flag = false;
                            updateinfo = null;

                        }

                        if (subInterval != null && !subInterval.isUnsubscribed()) {
                            subInterval.unsubscribe();
                            if (progressDialog != null)
                                progressDialog.dismiss();
                            if (mDialog != null)
                                mDialog.dismiss();
                            showToast("网关升级成功！");
                        }

                    }
                }, onErrorAction);
        mCompositeSubscription.add(gateWaySubscription);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String json = extras.getString("json");
            if (json != null) {
                Bundle bundle = new Bundle();
                bundle.putString("json", json);
                skipAct(DoorLockLogActivity.class, bundle);
            }
        }
    }

    /**
     * 设备白名单列表
     */
    private void requestDeviceList() {
        if (null != gw) {
            String mUserName = gw.getUsername();
            String mPassWord = gw.getPassword();
            presenter.requestDeviceList(mUserName, mPassWord);
        }
    }

    /**
     * 初始化头像
     */
    private void initHeaderIcon() {
        HashMap<String, String> map = (HashMap<String, String>) PreferencesUtils.getObject(PreferencesUtils.HEAD_ICON);
        if (map != null) {
            String uri = map.get(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
            if (uri != null) {
                ImageLoader.loadCropCircle(MainActivity.this, uri, userPicture, R.mipmap.default_user_picture);
            }
        }
    }

    /**
     * 接收头像改变
     */
    private void receiveHeaderIconChange() {
        //获取区域列表
        Subscription subscription = RxBus.getInstance().toObservable(UserHaderIconChange.class).compose(TransformUtils.<UserHaderIconChange>defaultSchedulers()).subscribe(new Action1<UserHaderIconChange>() {
            @Override
            public void call(UserHaderIconChange userHaderIconChange) {
                // userPicture
                if (userHaderIconChange.getUri() != null) {
                    ImageLoader.loadCropCircle(MainActivity.this, userHaderIconChange.getUri(), userPicture, R.mipmap.default_user_picture);
                }
            }
        });
        mCompositeSubscription.add(subscription);
    }

    /**
     * 获取区域
     */
    private void receiveGroups() {

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                LogUtil.e("groupError", throwable.getMessage() + "");
            }
        };
        //获取区域列表
        Subscription subscription = RxBus.getInstance().toObservable(GroupInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<GroupInfo>() {
                    @Override
                    public void call(GroupInfo groupInfo) {
                        if (groupInfo == null) return;
                        for (int i = 0; i < AppContext.getmOurGroups().size(); i++) {
                            if (AppContext.getmOurGroups().get(i).getGroupName().equals(groupInfo.getGroupName())) {
                                return;
                            }
                        }
                        AppContext.getmOurGroups().add(0, groupInfo);
                    }
                }, onErrorAction);
        AppContext.getInstance().getSerialInstance().getGroups();
        mCompositeSubscription.add(subscription);
    }

    /**
     * 获取场景
     */
    private void receiveSencess() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                receiveSencess();
            }
        };
        //场景缓存
        Subscription subscriptionSence = RxBus.getInstance().toObservable(SenceInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SenceInfo>() {
                    @Override
                    public void call(SenceInfo senceInfo) {
                        if (senceInfo == null) return;
                        for (int i = 0; i < AppContext.getmOurScenes().size(); i++) {
                            if (AppContext.getmOurScenes().get(i) == null) continue;
                            if (AppContext.getmOurScenes().get(i).getSenceId() == senceInfo.getSenceId()) {
                                if (AppContext.getmOurScenes().get(i).getSenceName().equals(senceInfo.getSenceName())) {
                                    return;
                                } else {
                                    AppContext.getmOurScenes().remove(i);
                                    AppContext.getmOurScenes().add(0, senceInfo);
                                    return;
                                }
                            }
                        }
                        AppContext.getmOurScenes().add(0, senceInfo);
                    }
                }, onErrorAction);
        AppContext.getInstance().getSerialInstance().getSences();
        mCompositeSubscription.add(subscriptionSence);
    }

    /**
     * 获取网关
     */
    private void receiveGateWays() {
        mSubscription = RxBus.getInstance().toObservable(LoginResult.BodyBean.GatewayListBean.class)
                .compose(TransformUtils.<LoginResult.BodyBean.GatewayListBean>defaultSchedulers())
                .subscribe(new Action1<LoginResult.BodyBean.GatewayListBean>() {
                    @Override
                    public void call(LoginResult.BodyBean.GatewayListBean event) {
                        if (event.getNote() != null && event.getNote().length() > 0) {
                            tvGateway.setText(event.getNote());
                        } else {
                            tvGateway.setText(event.getUsername());
                        }
                    }
                });

        mSubscription = RxBus.getInstance().toObservable(HomePagerUpMain.class)
                .compose(TransformUtils.<HomePagerUpMain>defaultSchedulers())
                .subscribe(new Action1<HomePagerUpMain>() {
                               private LoginResult.BodyBean.GatewayListBean gatewayListBean;

                               @Override
                               public void call(HomePagerUpMain updataName) {
                                   tvGateway.setText(updataName.getName());
                               }
                           }
                );
        mCompositeSubscription.add(mSubscription);
    }

    /**
     * 接收昵称改变
     */
    private void receiveAliarsChange() {
        Subscription aliarSucription = RxBus.getInstance().toObservable(ModifyAliars.class)
                .compose(TransformUtils.<ModifyAliars>defaultSchedulers())
                .subscribe(new Action1<ModifyAliars>() {
                    @Override
                    public void call(ModifyAliars event) {
                        userNick.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_ALAIRS));
                    }
                });
        mCompositeSubscription.add(aliarSucription);
    }

    /**
     * 接用户名称改变
     */
    private void receiveUserNameChange() {
        Subscription accountSucription = RxBus.getInstance().toObservable(ModifyAccountInfo.class)
                .compose(TransformUtils.<ModifyAccountInfo>defaultSchedulers())
                .subscribe(new Action1<ModifyAccountInfo>() {
                    @Override
                    public void call(ModifyAccountInfo event) {
                        userPhone.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                    }
                });
        mCompositeSubscription.add(accountSucription);
    }

    //接收飞比设备
    private void receiveFbeeDevicesInfo() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        //注册RXbus接收飞比设备
        Subscription fbeeScription = RxBus.getInstance().toObservable(DeviceInfo.class)
                .compose(TransformUtils.<DeviceInfo>defaultSchedulers())
                .subscribe(new Action1<DeviceInfo>() {
                    @Override
                    public void call(DeviceInfo event) {

                        if (null != event) {
                            //存储到AppContext.getmOurDevices()中
                            for (int i = 0; i < AppContext.getmOurDevices().size(); i++) {
                                if (AppContext.getmOurDevices().get(i).getUId() == event.getUId()) {
                                    return;
                                }
                            }
                            AppContext.getmOurDevices().add(event);
                            //查询设备的用户
                            QueryDeviceuserlistReq body = new QueryDeviceuserlistReq();
                            String ieee = ByteStringUtil.bytesToHexString(event.getIEEE()).toUpperCase();
                            body.setUuid(ieee);
                            body.setShort_id(String.valueOf(event.getUId()));
                            body.setVendor_name(FactoryType.FBEE);
                            //如果是门锁查询设备信息
                            if (event.getDeviceId() == DeviceList.DEVICE_ID_DOOR_LOCK) {
                                presenter.getUserEquipmentlist(event.getUId(), body);
                            }
                            //添加设备到服务器
                            AddDevicesReq addDevicesReq = new AddDevicesReq();
                            AddDevicesReq.DeviceBean deviceBean = new AddDevicesReq.DeviceBean();
                            addDevicesReq.setGateway_vendor_name(FactoryType.FBEE);
                            addDevicesReq.setGateway_uuid(AppContext.getGwSnid());
                            deviceBean.setVendor_name(FactoryType.FBEE);
                            deviceBean.setUuid(ieee);
                            deviceBean.setShort_id(String.valueOf(event.getUId()));
                            deviceBean.setType(String.valueOf(event.getDeviceId()));
                            addDevicesReq.setDevice(deviceBean);
                            presenter.reqAddDevices(addDevicesReq);
                        }
                    }
                }, onErrorAction);
        mCompositeSubscription.add(fbeeScription);
        AppContext.getInstance().getSerialInstance().getDevices();
    }

    /**
     * 接收门锁电量
     */
    private void receiveDoorLockPowerInfo() {
        //注册RXbus接收arriveReport_CallBack（）回调中的数据
        Subscription subscrip = RxBus.getInstance().toObservable(ArriveReportCBInfo.class)
                .compose(TransformUtils.<ArriveReportCBInfo>defaultSchedulers())
                .subscribe(new Action1<ArriveReportCBInfo>() {
                    @Override
                    public void call(ArriveReportCBInfo event) {
                        if (event == null) return;
                        //传感器报警
                        if(event.getAttribID() == DoorLockGlobal.SENSOR_ALARM){
                            List<DeviceInfo> devices = AppContext.getmOurDevices();
                            for (int i = 0; i <devices.size() ; i++) {
                                if(devices.get(i).getUId() ==event.getuId() ){
                                    if(devices.get(i).getDeviceId() == DeviceList.DEVICE_ID_SENSOR){
                                        isAlarm = true;
                                        if((event.getData() & 0x08) == 0x08){
                                            StringBuffer strb = new StringBuffer();
                                            strb.append("低电量报警");
                                            if((event.getData() & 0x01) == 0x01 || (event.getData() & 0x02) == 0x02){
                                                strb.append("/报警");
                                            }
                                            if((event.getData() & 0x04) == 0x04){
                                                strb.append("/防拆报警");
                                            }
                                            dialogShow(devices.get(i).getDeviceName(),"低电量报警");
                                        }else if((event.getData() & 0x01) == 0x01 || (event.getData() & 0x02) == 0x02){
                                            if(event.getIsDenfense() == 1)
                                            dialogShow(devices.get(i).getDeviceName(),"报警");
                                        }else if((event.getData() & 0x04) == 0x04){
                                            if(event.getIsDenfense() == 1)
                                                dialogShow(devices.get(i).getDeviceName(),"防拆报警");
                                        }
                                        return;
                                    }
                                }
                            }
                        }

                        //门锁电量相关
                        if (event.getClusterId() == DoorLockGlobal.CLUSTER_ID_POWER_REPORT) {
                            switch (event.getAttribID()) {
                                case DoorLockGlobal.BATTERY_ATTRID_POWER:
                                    //设置电量百分比
                                    int powerPerent = (int)event.getData();
                                    int uid = event.getuId();
                                    AppContext.getDoorLockPowerMap().put(uid, powerPerent);
                                    DoorLockBatteryNotification(event);
                                    break;
                            }
                        }
                        //门锁可控状态上报
                        int uid = event.getuId();
                        int clusterId = event.getClusterId();
                        LogUtil.e("门锁上报clusterId", "===" + clusterId);
                        int attribId = event.getAttribID();
                        LogUtil.e("门锁上报attribId", "===" + attribId);
                        int data = (int)event.getData();
                        LogUtil.e("门锁上报data", "data=" + data);
                        if (clusterId == 0x00 && attribId == 0x12) { // 门锁可以控制
                            //可控状态，开启倒计时
                            if (timerMap.get(uid) != null) {
                                TimerCount timerCount = AppContext.timerMap.get(uid);
                                AppContext.timerMap.remove(uid);
                                timerCount.cancel();
                            }
                            TimerCount timerCount = new TimerCount(50000, 1000, uid);
                            timerCount.start();
                            AppContext.timerMap.put(uid, timerCount);
                        }
                    }
                });
        mCompositeSubscription.add(subscrip);
    }

    /**
     * 注册RXbus接收来自Serial中arriveReportgatedoor_CallBack(int uId, byte[] data,
     * char clusterId, char attribID)的数据
     */
    public void receiveDoorLockStateInfo() {
        mSubscription = RxBus.getInstance().toObservable(DoorLockStateInfo.class)
                .compose(TransformUtils.<DoorLockStateInfo>defaultSchedulers())
                .subscribe(new Action1<DoorLockStateInfo>() {
                    @Override
                    public void call(DoorLockStateInfo event) {
                        //初始化门锁通知
                        initDoorLockNotification(event);
                    }
                });
        mCompositeSubscription.add(mSubscription);
    }

    public void openLeftMenu() {
        activityMain.openDrawer(Gravity.LEFT);
    }

    /**
     * 判断是否是绑定设备用户
     *
     * @param userNum
     * @return
     */
    public boolean isAssociateDevicesUser(int uid, int userNum) {
        userEntityMap = AppContext.getMap();
        if (userEntityMap != null) {
            if (userEntityMap.size() > 0) {
                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = userEntityMap.get(String.valueOf(uid));
                if (bodyEntities != null) {
                    for (int i = 0; i < bodyEntities.size(); i++) {
                        if (bodyEntities.get(i).getId().equals(String.valueOf(userNum))) {
                            List<String> userList = bodyEntities.get(i).getWithout_notice_user_list();
                            if (userList != null && userList.size() > 0) {
                                for (int j = 0; j < userList.size(); j++) {
                                    if (phone.equals(userList.get(j))) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * //初始化门锁通知
     *
     * @param event
     */
    private void initDoorLockNotification(DoorLockStateInfo event) {
        // ==========更新数据
        int uid = event.getDoorLockUid(); // 得到uid
        byte[] doorLockData = event.getDoorLockData();//门锁上报字节数据
        String ieee = null;
        List<DeviceInfo> envInfo = AppContext.getmDoorLockDevices();
        for (int i = 0; i < envInfo.size(); i++) {
            if (uid != envInfo.get(i).getUId()) {
                continue;
            }
            deviceName = envInfo.get(i).getDeviceName();
            byte[] byteIeee = envInfo.get(i).getIEEE();
            ieee = ByteStringUtil.bytesToHexString(byteIeee);
        }
        if (deviceName == null || ieee == null) return;

        int data = event.getDoorLockSensorData(); // 开锁时间
        byte[] bytes = Tool.IntToBytes(data);
        int LockStatusByte = event.getNewDoorLockState();// 获得胁迫报警位
        MessageUtil su = new MessageUtil();
        int cardNum = -1;
        cardNum = event.getDoorLockCareNum();// 获取门卡编号
        int way = event.getDoorLockWay(); // 开锁方式
        String alias = UserDbUtil.getIns().getUserAliasByID(cardNum, uid);
        String alamName = null;//用户名
        if (TextUtils.isEmpty(alias)) {
            alamName = cardNum + "号用户";
        } else {
            alamName = alias;
        }

        String message = null;
        // 进入管理员菜单 // 通过门锁状态解析出，文字描述，通过三个字段：
        // LockStatusByte:门锁状态字节， way:开门方式，指纹、密码、刷卡等， Byte[3]:开门、关门、非法操作报警
        if ((LockStatusByte & 0x08) == 8) {
            // 按键进入管理员菜单
            if (way == 0) {
                message = Enter_Menu(LockStatusByte, alamName, "密码");
            }// 指纹操作进入管理员菜单
            else if (way == 2) {
                message = Enter_Menu(LockStatusByte, alamName, "指纹");
            } else if (way == 3) {
                message = Enter_Menu(LockStatusByte, alamName, "刷卡");
            } else if (way == 5) {
                message = Enter_Menu(LockStatusByte, alamName, "多重");
            }
            if ((LockStatusByte & 0xbf) == 0x88) {
                if (!isAssociateDevicesUser(uid, cardNum)) {
                    isAlarm = true;

                    //mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警进入菜单", uid);
                    dialogShow(deviceName, "胁迫报警进入菜单" + "(" + alamName + ")");
//                    showNotification(uid, deviceName, "-胁迫报警进入菜单");
                }
                message = alamName + "-胁迫报警进入菜单";
            }// 胁迫指纹进入菜单（双人模式）
            else if ((LockStatusByte & 0xbf) == 0x98) {
                if (!isAssociateDevicesUser(uid, cardNum)) {
                    isAlarm = true;

                    // mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警进入菜单(双人模式)", uid);
                    dialogShow(deviceName, "胁迫报警进入菜单(双人模式)" + "(" + alamName + ")");
//                    showNotification(uid, deviceName, "-胁迫报警进入菜单(双人模式)");
                }
                message = alamName + "-胁迫报警进入菜单(双人模式)";
            }// 胁迫报警启用常开（菜单）
            else if ((LockStatusByte & 0xbf) == 0x89) {
                if (!isAssociateDevicesUser(uid, cardNum)) {
                    isAlarm = true;

                    // mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警启用常开(菜单)", uid);
                    dialogShow(deviceName, "胁迫报警启用常开(菜单)" + "(" + alamName + ")");
//                    showNotification(uid, deviceName, "-胁迫报警启用常开(菜单)");
                }
                message = alamName + "-胁迫报警启用常开(菜单)";

            }// 胁迫报警取消常开（菜单）
            else if ((LockStatusByte & 0xbf) == 0x8a) {
                if (!isAssociateDevicesUser(uid, cardNum)) {
                    isAlarm = true;
                    //mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警取消常开(菜单)", uid);
                    dialogShow(deviceName, "胁迫报警取消常开(菜单)" + "(" + alamName + ")");
//                    showNotification(uid, deviceName, "-胁迫报警取消常开(菜单)");
                }
                message = alamName + "-胁迫报警取消常开(菜单)";
            }
            // 胁迫指纹进入菜单取消常开（双人模式）
            else if ((LockStatusByte & 0xbf) == 0x9a) {
                if (!isAssociateDevicesUser(uid, cardNum)) {
                    isAlarm = true;
                    //mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警取消常开(双人模式)", uid);
                    dialogShow(deviceName, "胁迫报警取消常开(双人模式)" + "(" + alamName + ")");
//                    showNotification(uid, deviceName, "-胁迫报警取消常开(双人模式)");
                }
                message = alamName + "-胁迫报警取消常开(双人模式)";
            }// 胁迫指纹进入菜单启用常开（双人模式）
            else if ((LockStatusByte & 0xbf) == 0x99) {
                if (!isAssociateDevicesUser(uid, cardNum)) {
                    isAlarm = true;
                    //mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警启用常开(双人模式)", uid);
                    dialogShow(deviceName, "胁迫报警启用常开(双人模式)" + "(" + alamName + ")");
//                    showNotification(uid, deviceName, "-胁迫报警启用常开(双人模式)");
                }
                message = alamName + "-胁迫报警启用常开(双人模式)";
            }
            //App.getInstance().doorLockObserver.update(uid);
        } else {// 开锁
            if (bytes[3] == 0x02) { // 01：关门 02：开门 03：非法操作报警 05：非法卡)
                // 刷卡
                if (way == 3) {
                    message = Unlocking(LockStatusByte, alamName, "刷卡");
                }
                // 多重验证
                else if (way == 5) {
                    message = Unlocking(LockStatusByte, alamName, "多重验证");
                }
                // 密码
                else if (way == 0) {
                    message = Unlocking(LockStatusByte, alamName, "密码");
                }
                // 指纹
                else if (way == 2) {
                    message = Unlocking(LockStatusByte, alamName, "指纹");
                }// 远程
                else if (way == 4) {
                    mediaplayer = MediaPlayer.create(this, R.raw.test);
                    mediaplayer.start();
                    message = alamName + "-远程开锁";
                    //mDbDAO.insertDoorLockInfo(alamName + "-远程开锁", uid);
//                    showNotification(uid, deviceName, "-远程开锁");
                }
                if ((LockStatusByte & 0xbf) == 0x80) {
                    if (!isAssociateDevicesUser(uid, cardNum)) {
                        isAlarm = true;

                        // mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警", uid);
                        dialogShow(deviceName, "胁迫报警" + "(" + alamName + ")");
//                        showNotification(uid, deviceName, "-胁迫报警");
                    }
                    message = alamName + "-胁迫报警";
                } else if ((LockStatusByte & 0xbf) == 0x90) {
                    if (!isAssociateDevicesUser(uid, cardNum)) {
                        isAlarm = true;

                        //mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警 (双人模式)", uid);
                        dialogShow(deviceName, "胁迫报警 (双人模式)" + "(" + alamName + ")");
//                        showNotification(uid, deviceName, "-胁迫报警 (双人模式)");
                    }
                    message = alamName + "-胁迫报警 (双人模式)";
                } else if ((LockStatusByte & 0xbf) == 0x82) {
                    if (!isAssociateDevicesUser(uid, cardNum)) {
                        isAlarm = true;

                        // mDbDAO.insertDoorLockInfo(alamName + "-敞开常开", uid);
                        dialogShow(deviceName, "胁迫报警取消常开" + "(" + alamName + ")");
//                        showNotification(uid, deviceName, "-胁迫报警取消常开");
                    }
                    message = alamName + "-胁迫报警取消常开";
                } else if ((LockStatusByte & 0xbf) == 0x81) {
                    if (!isAssociateDevicesUser(uid, cardNum)) {
                        isAlarm = true;

                        //mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警启用常开", uid);
                        dialogShow(deviceName, "胁迫报警启用常开" + "(" + alamName + ")");
//                        showNotification(uid, deviceName, "-胁迫报警启用常开");
                    }
                    message = alamName + "-胁迫报警取消常开";
                } else if ((LockStatusByte & 0xbf) == 0x92) {
                    if (!isAssociateDevicesUser(uid, cardNum)) {
                        isAlarm = true;

                        //mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警取消常开(双人模式)", uid);
                        dialogShow(deviceName, "胁迫报警取消常开(双人模式)" + "(" + alamName + ")");
//                        showNotification(uid, deviceName, "-胁迫报警取消常开(双人模式)");
                    }
                    message = alamName + "-胁迫报警取消常开(双人模式)";
                } else if ((LockStatusByte & 0xbf) == 0x91) {
                    if (!isAssociateDevicesUser(uid, cardNum)) {
                        isAlarm = true;

//                         mDbDAO.insertDoorLockInfo(alamName + "-胁迫报警启用常开(双人模式)", uid);
                        dialogShow(deviceName, "胁迫报警启用常开(双人模式)" + "(" + alamName + ")");
//                        showNotification(uid, deviceName, "-胁迫报警启用常开(双人模式)");
                    }
                    message = alamName + "-胁迫报警启用常开(双人模式)";
                }
            }// 非法操作报警
            else if (bytes[3] == 0x03) {
                isAlarm = true;
                //mDbDAO.insertDoorLockInfo("非法操作", uid);
                dialogShow(deviceName, "非法操作");
                message = "非法操作";
//                showNotification(uid, deviceName, "-非法操作");
            }
        }
        if (way == DoorLockGlobal.R_DOOR_LOCK_ALARM_FLAG) { // 防拆 报警各种报警
            //mDbDAO.insertDoorLockInfo(su.getTamperString(bytes[3]), uid);
            if (bytes[3] == 6) {
                if (!isAssociateDevicesUser(uid, cardNum)) {
                    isAlarm = true;
                    dialogShow(deviceName + "报警通知", su.getTamperString(bytes[3]));
//                    showNotification(uid, deviceName, "-非法操作");
                }
            } else {
                if (su.getTamperString(bytes[3]) != null && !su.getTamperString(bytes[3]).equals("")) {
                    isAlarm = true;
                    dialogShow(deviceName + "报警通知", su.getTamperString(bytes[3]));
//                    showNotification(uid, deviceName, "-非法操作");
                }
            }
            message = su.getTamperString(bytes[3]);
        }
        if (message != null) {
            Doorlockrecord info = new Doorlockrecord();
            info.setMsg(message);
            //info.setMsgType(DOOR_TYPE_RECORD);
            info.setDeviceID(uid);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long millis = System.currentTimeMillis();
            String dateTime = sdf.format(new Date(millis));
            info.setTime(dateTime);
            info.setUserID(cardNum);
            doorLockDbUtil.insertDoorLockRecord(info);
        }

        /****************** 添加用户 *******************/
        if (cardNum >= 0 && !UserDbUtil.getIns().queryUserIsExist(cardNum, uid)) {
            User user = new User(cardNum, uid, cardNum + "号用户");
            UserDbUtil.getIns().insert(user);
        }
        if (!String.valueOf(cardNum).isEmpty()) {
            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums02 = AppContext.getMap().get(String.valueOf(uid));
            if (userNums02 != null) {
                boolean tag = false;
                for (int j = 0; j < userNums02.size(); j++) {
                    if (userNums02.get(j).getId().equals(String.valueOf(cardNum))) {
                        tag = true;
                        break;
                    }
                }
                if (!tag) {
                    //添加设备用户到服务器
                    reqAddDeviceUser(ieee, String.valueOf(cardNum), cardNum + "号用户");
                }
            } else {
                //添加设备用户到服务器
                reqAddDeviceUser(ieee, String.valueOf(cardNum), cardNum + "号用户");
            }
        }

        // ============================通过sp存储jsonArray,jsa里存储jsonObject,实现门锁记录红点的更新
        // notificationId = 101;
        // ==================门锁被观察者对象调用更新方法
        //||(doorLockData.length==3&&(doorLockData[2]==2||doorLockData[2]==0x7f))远程开锁未允许标志
        if (doorLockData != null && (doorLockData.length == 5 || doorLockData.length == 12 || doorLockData.length == 13)) {
            /*if(topActivity()){
                RxBus.getInstance().post(new DoorlockLogMsgChangeEvent(String.valueOf(uid)));
                return;
            }*/
            try {
                // ===============如果jsonArray为空，则存储jsonobject
                String jsString = PreferencesUtils.getString(MainActivity.DOORMESSAGECOUNT);
                if (jsString == null) {
                    JSONArray jsonArraySet = new JSONArray();
                    JSONObject jsonObjectSet = new JSONObject();
                    jsonObjectSet.put("uid", uid);
                    jsonObjectSet.put("messageCount", 1);
                    jsonArraySet.put(jsonObjectSet);
                    PreferencesUtils.saveString(MainActivity.DOORMESSAGECOUNT, jsonArraySet.toString());
                } else {
                    JSONArray jsonArrayGet = new JSONArray(jsString);
                    int flag = -1;
                    //查询门锁消息记录是否存在，flag<0表示记录不存在，则添加，flag>0表示记录存在，则修改
                    for (int i = 0; i < jsonArrayGet.length(); i++) {
                        JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
                        if (jsonObjectGet.optInt("uid") == uid) {
                            flag = i;
                            break;
                        }
                    }
                    if (flag < 0) {
                        JSONObject jsonObjectSet = new JSONObject();
                        jsonObjectSet.put("uid", uid);
                        jsonObjectSet.put("messageCount", 1);
                        jsonArrayGet.put(jsonObjectSet);
                        PreferencesUtils.saveString(MainActivity.DOORMESSAGECOUNT, jsonArrayGet.toString());
                    } else {
                        JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(flag);
                        int count = jsonObjectGet.optInt("messageCount");
                        // ==============更新该门锁记录
                        remove(flag, jsonArrayGet);// 必须先删除,防止得到的消息数量一直为0
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("uid", uid);
                        jsonObject.put("messageCount", count + 1);
                        jsonArrayGet.put(jsonObject);
                        PreferencesUtils.saveString(MainActivity.DOORMESSAGECOUNT, jsonArrayGet.toString());
                    }
                }
                notifyBadgeNumber();
            } catch (Exception e) {
                e.printStackTrace();
            }
            RxBus.getInstance().post(new HintCountInfo(uid));
        }
    }// ======初始化门锁通知到此结束

    //JSONArray删除条目
    public void remove(int positon, JSONArray array) throws Exception {
        if (positon < 0)
            return;
        Field valuesField = JSONArray.class.getDeclaredField("values");
        valuesField.setAccessible(true);
        List<Object> values = (List<Object>) valuesField.get(array);
        if (positon >= values.size())
            return;
        values.remove(positon);
    }

    // 消息数量显示
    public void notifyBadgeNumber() {
        try {
            String jsString = PreferencesUtils.getString(MainActivity.DOORMESSAGECOUNT);
            int count = 0;
            if (jsString != null) {
                JSONArray jsonArrayGet = new JSONArray(jsString);
                for (int i = 0; i < jsonArrayGet.length(); i++) {
                    JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
                    count = count + jsonObjectGet.optInt("messageCount");
                }
            }
            BadgeUtil.setBadgeCount(MainActivity.this, count,
                    Build.MANUFACTURER);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dialogShow(String title, String str) {
        // ==========================如果报警，播放报警声音
        if (str == null || str.equals("")) {
            return;
        }
        if (isAlarm) {
            Intent intent = new Intent(MainActivity.this,
                    AlamDialogActivity.class);
            intent.putExtra("isAlarm", isAlarm);
            intent.putExtra("name", title);
            intent.putExtra("str", str);
            startActivity(intent);
        }
    }

    private String Unlocking(int LockStatusByte, String alamName, String str) {
        String content = "";
        if (LockStatusByte == 0) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            content = alamName + "-" + str + "开锁";
            //mDbDAO.insertDoorLockInfo(alamName + str + "开锁", cardNum);
        } else if ((LockStatusByte & 0xbf) == 0x10) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            //mDbDAO.insertDoorLockInfo(alamName + str + "开锁(双人模式)", cardNum);
            content = alamName + "-" + str + "开锁(双人模式)";
        } else if ((LockStatusByte & 0xbf) == 0x02) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            //mDbDAO.insertDoorLockInfo(alamName + str + "开锁取消常开", cardNum);
            content = alamName + "-" + str + "开锁取消常开";
        } else if ((LockStatusByte & 0xbf) == 0x01) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            //mDbDAO.insertDoorLockInfo(alamName + str + "开锁启用常开", cardNum);
            content = alamName + "-" + str + "开锁启用常开";
        } else if ((LockStatusByte & 0xbf) == 0x12) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            // mDbDAO.insertDoorLockInfo(alamName + str + "开锁取消常开(双人模式)", cardNum);
            content = alamName + "-" + str + "开锁取消常开(双人模式)";
        } else if ((LockStatusByte & 0xbf) == 0x11) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            //mDbDAO.insertDoorLockInfo(alamName + str + "开锁启用常开(双人模式)", cardNum);
            content = alamName + "-" + str + "开锁启用常开(双人模式)";
        } else {
            if ((LockStatusByte & 0xbf) != 0x80 && (LockStatusByte & 0xbf) != 0x90 && (LockStatusByte & 0xbf) != 0x82 && (LockStatusByte & 0xbf) != 0x81 && (LockStatusByte & 0xbf) != 0x91 && (LockStatusByte & 0xbf) != 0x92) {
                mediaplayer = MediaPlayer.create(this, R.raw.test);
                mediaplayer.start();
                //mDbDAO.insertDoorLockInfo(alamName + str + "开锁", cardNum);
                content = alamName + "-" + str + "开锁";
            }
        }
        // if (!TextUtils.isEmpty(content)) {
//            showNotification(cardNum, deviceName, content);
        // }
        return content;
    }

    private String Enter_Menu(int LockStatusByte, String alamName, String str) {
        String content = null;
        if (LockStatusByte == 8) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            content = alamName + "-" + str + "验证进入菜单";
//            // mDbDAO.insertDoorLockInfo(alamName + str + "验证进入菜单", cardNum);
//            content = str + "验证进入菜单";
        } else if (LockStatusByte == 0x18) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            content = alamName + "-" + str + "验证进入菜单(双人模式)";
//            //mDbDAO.insertDoorLockInfo(alamName + str + "验证进入菜单(双人模式)", cardNum);
//            content = str + "验证进入菜单(双人模式)";
        } else if (LockStatusByte == 10) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            content = alamName + "-取消常开(菜单)";
//            // mDbDAO.insertDoorLockInfo(alamName + "-取消常开(菜单)", cardNum);
//            content = "取消常开(菜单)";
        } else if (LockStatusByte == 9) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            content = alamName + "-启用常开(菜单)";
//            // mDbDAO.insertDoorLockInfo(alamName + "-启用常开(菜单)", cardNum);
//            content = "启用常开(菜单)";
        } else if (LockStatusByte == 26) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            content = alamName + "-取消常开(菜单)(双人)";
//            //mDbDAO.insertDoorLockInfo(alamName + "-取消常开(菜单)(双人)", cardNum);
//            content = "取消常开(菜单)(双人)";
        } else if ((LockStatusByte & 0xbf) == 0x19) {
            mediaplayer = MediaPlayer.create(this, R.raw.test);
            mediaplayer.start();
            content = alamName + "-启用常开(菜单)(双人)";
//            // mDbDAO.insertDoorLockInfo(alamName + "-启用常开(菜单)(双人)", cardNum);
//            content = "启用常开(菜单)(双人)";
        }
        return content;
////        showNotification(cardNum, dvname, content);
//
    }
//    private void showNotification(int uid, String deviceName, String content) {
//
//        long currentTime = System.currentTimeMillis();
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//        Date date = new Date(currentTime);
//        String time = formatter.format(date);
//        Intent intentNotification = new Intent(MainActivity.this, DoorLockLogActivity.class);
//        intentNotification.putExtra("uid", uid);
//        intentNotification.putExtra("deviceName", deviceName);
//
//        intentNotification.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        Random random = new Random(System.currentTimeMillis());
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, random.nextInt(),
//                intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (android.os.Build.BRAND.contains("OPPO")) {
//            builder = new NotificationCompat.Builder(MainActivity.this)
//                    .setSmallIcon(R.mipmap.ic_logol)
//                    .setContentTitle("王力固心提示")
//                    .setSound(Uri.parse("android.resource://"
//                            + this.getPackageName() + "/"
//                            + R.raw.beep))
//                    .setContentText("[" + deviceName + "]" + "-" + content)
//                    .setTicker("门锁记录")//第一次提示消息的时候显示在通知栏上
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true);
//            ;
//
//        }
//
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (android.os.Build.BRAND.contains("OPPO")) {
//            manager.notify(random.nextInt(), builder.build());
//        } else {
//            Notification notify2 = new Notification.Builder(this)
//                    .setSmallIcon(R.mipmap.ic_logol)
//                    .setContentTitle("门锁记录")
//                    .setWhen(System.currentTimeMillis())
//                    .setSound(Uri.parse("android.resource://"
//                            + this.getPackageName() + "/"
//                            + R.raw.beep))
//                    .setContentText("门锁记录")
//                    .setTicker("门锁记录")
//                    .setContentIntent(pendingIntent)
//                    .build();
//
//            notify2.contentView = new RemoteViews(getPackageName(),
//                    R.layout.view_notify_alarm);
//            notify2.contentView.setTextViewText(R.id.notify_time, time);
//            notify2.contentView.setTextViewText(R.id.notify_text, "[" + deviceName + "]" + "-" + content);
//            if (isAlarm) {
//                notify2.contentView.setTextColor(R.id.notify_text, Color.RED);
//            } else {
//                notify2.contentView.setTextColor(R.id.notify_text, getResources().getColor(R.color.colorAccent));
//            }
//            notify2.flags |= Notification.FLAG_AUTO_CANCEL;
//            manager.notify(random.nextInt(), notify2);
//        }
//        isAlarm = false;// =========重置是否报警变量
//    }

    // 低电量报警
    private void DoorLockBatteryNotification(ArriveReportCBInfo event) {
        //设置电量百分比
        int powerPerent = (int)event.getData() / 2;
        int uid = event.getuId();
        int ret = AppContext.findDoorLockExist(uid);
        if (ret == -1) return;
        if (powerPerent > 25) return;
        StringBuffer text = new StringBuffer();
        if (ret < AppContext.getmDoorLockDevices().size()) {
            deviceName = AppContext.getmDoorLockDevices().get(ret).getDeviceName();
        }
        isAlarm = true;
        text.append("电量低报警");
        dialogShow(deviceName, text + "");
        Doorlockrecord doorlockrecord = new Doorlockrecord();
        doorlockrecord.setDeviceID(uid);
        doorlockrecord.setMsg("电量低报警");
        //doorlockrecord.setMsgType(DoorlockinfoParse.DOOR_TYPE_POWER);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long millis = System.currentTimeMillis();
        String dateTime = sdf.format(new Date(millis));
        doorlockrecord.setTime(dateTime);
        doorLockDbUtil.insertDoorLockRecord(doorlockrecord);
        try {
            // ===============如果jsonArray为空，则存储jsonobject
            String jsString = PreferencesUtils.getString(MainActivity.DOORMESSAGECOUNT);
            if (jsString == null) {
                JSONArray jsonArraySet = new JSONArray();
                JSONObject jsonObjectSet = new JSONObject();
                jsonObjectSet.put("uid", uid);
                jsonObjectSet.put("messageCount", 1);
                jsonArraySet.put(jsonObjectSet);
                PreferencesUtils.saveString(MainActivity.DOORMESSAGECOUNT, jsonArraySet.toString());
            } else {
                JSONArray jsonArrayGet = new JSONArray(jsString);
                int flag = -1;
                //查询门锁消息记录是否存在，flag<0表示记录不存在，则添加，flag>0表示记录存在，则修改
                for (int i = 0; i < jsonArrayGet.length(); i++) {
                    JSONObject jsonObjectGet = (JSONObject) jsonArrayGet
                            .get(i);
                    if (jsonObjectGet.optInt("uid") == uid) {
                        flag = i;
                        break;
                    }
                }
                if (flag < 0) {
                    JSONObject jsonObjectSet = new JSONObject();
                    jsonObjectSet.put("uid", uid);
                    jsonObjectSet.put("messageCount", 1);
                    jsonArrayGet.put(jsonObjectSet);
                    PreferencesUtils.saveString(MainActivity.DOORMESSAGECOUNT, jsonArrayGet.toString());
                } else {
                    JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(flag);
                    int count = jsonObjectGet.optInt("messageCount");
                    // ==============更新该门锁记录
                    remove(flag, jsonArrayGet);// 必须先删除,防止得到的消息数量一直为0
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("uid", uid);
                    jsonObject.put("messageCount", count + 1);
                    jsonArrayGet.put(jsonObject);
                    PreferencesUtils.saveString(MainActivity.DOORMESSAGECOUNT, jsonArrayGet.toString());
                }
            }
            notifyBadgeNumber();

        } catch (Exception e) {
            e.printStackTrace();
        }


        int notificationId = 101;
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        java.sql.Date date = new java.sql.Date(currentTime);
        String time = formatter.format(date);
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.view_notify_alarm);
        remoteViews.setTextViewText(R.id.notify_time, time);
        /************************************* 这里现实消息通知 *******************************/
        if (isAlarm)
            remoteViews.setTextColor(R.id.notify_text, Color.RED);
        // ====================================================================Notification

//        Intent intentNotification = new Intent(MainActivity.this, cls);
//        intentNotification.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        String deviceName = "devicename";
//        String deviceId = "deviceid";
//        Bundle bundle = new Bundle();
//        bundle.putString(deviceName, deviceName);
//        bundle.putInt(deviceId, uid);
//        intentNotification.putExtras(bundle);
//        Notification notification = new Notification(R.mipmap.ic_logol,
//                "门锁记录",
//                System.currentTimeMillis());
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                intentNotification, 0);
//        notification.contentView = remoteViews;
//        notification.contentIntent = pendingIntent;
//        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击后自动消失

        // ==========================如果报警，播放报警声音

//        if (isAlarm) {
//            notification.sound = Uri.parse("android.resource://"
//                    + getPackageName() + "/" + R.raw.jingchejingbaosheng);
//
//            long[] vibrate = {0, 100, 200, 300};
//            notification.vibrate = vibrate;
//        } else {
//
//            notification.defaults |= Notification.DEFAULT_SOUND;// 声音默认
//            notification.defaults |= Notification.DEFAULT_VIBRATE;// 震动默认
//
//        }
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(notificationId, notification);
        isAlarm = false;// ================重置是否报警变量
        RxBus.getInstance().post(new HintCountInfo(uid));
    }
   /* */

    /**
     * 判断栈顶activity是否是.ui.doorlocklog.DoorLockLogActivity
     *
     * @return
     *//*
    private boolean topActivity(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = activityManager.getRunningTasks(1).get(0);
        String shortClassName = info.topActivity.getShortClassName(); //类名
        LogUtil.e("topactivity",shortClassName);
        if(".ui.doorlocklog.DoorLockLogActivity".equals(shortClassName)){
            return true;
        }
        return false;
    }*/
    private void updateVersion() {
        PusBodyBean bodyBean = new PusBodyBean();
        bodyBean.setToken("");
        bodyBean.setProduct_name("wonly");
        bodyBean.setPlatform("android");
        bodyBean.setEndpoint_type("general");
        presenter.updateVersion(bodyBean);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void
    onClick(View v) {
        switch (v.getId()) {
            //个人信息设置
            case R.id.rl_user_picture:
                skipAct(PersonAccountInfoActivity.class);
                break;
            //场景管理
            case R.id.main_scene_management:
                skipAct(ScenaManagerActivity.class);
                break;
            //设备管理
            case R.id.main_equipment_management:
                icvss.equesGetDeviceList();
                skipAct(DeviceManagerActivity.class);
                break;
            //区域管理
            case R.id.main_area_management:
                skipAct(AreaManagerActivity.class);
                break;
            //推送设置
            case R.id.main_push:
                skipAct(MenuJpushActivity.class);
                break;
            //网关列表
            case R.id.gatewaylist:
                skipAct(GatewayListActicity.class);
                break;
            //关于信息
            case R.id.main_about_info:
                skipAct(AboutInfoActivity.class);
                break;
            //定时计划
            case R.id.main_plans:
                skipAct(PlanListActivity.class);
                break;
            //触发联动
            case R.id.main_rules:
                skipAct(RuleListActivity.class);
                break;
            //切换模式
            case R.id.main_change:
                //我的
                Drawable top = getResources().getDrawable(R.drawable.main_tab_my_selector);
                mainAreaBtn.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                //发现
                Drawable topeq = getResources().getDrawable(R.drawable.main_tab_find_selector);
                mainEquipmentBtn.setCompoundDrawablesWithIntrinsicBounds(null, topeq, null, null);
                mainScenarioBtn.setVisibility(View.GONE);
                mainEquipmentBtn.setText("发现");
                mainAreaBtn.setText("我的");
                //关闭手势侧滑
                activityMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                HomeSimpleFragment simple = new HomeSimpleFragment();
                FindFragment find = new FindFragment();
                MyFragment my = new MyFragment();

                fragmentList.clear();
                fragmentList.add(simple);
                fragmentList.add(find);
                fragmentList.add(my);
                if (null == fadapter) {
                    fadapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
                    mainFragment.setAdapter(fadapter);
                } else {
                    fadapter.notifyDataSetChanged();
                }
                mainHomeBtn.setChecked(true);

                PreferencesUtils.saveString(PreferencesUtils.MODEL, "simplify");

                break;
        }
    }


    /**
     * 接收模式切换
     */
    private void receiveModelChange() {

        Subscription mSubscription04 = RxBus.getInstance().toObservable(SwitchFragmentEvent.class)
                .compose(TransformUtils.<SwitchFragmentEvent>defaultSchedulers())
                .subscribe(new Action1<SwitchFragmentEvent>() {
                    @Override
                    public void call(SwitchFragmentEvent event) {

                        mainEquipmentBtn.setText("设备");
                        mainAreaBtn.setText("区域");
                        Drawable top = getResources().getDrawable(R.drawable.main_tab_area_selector);
                        mainAreaBtn.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);

                        Drawable topeq = getResources().getDrawable(R.drawable.main_tab_equipment_selector);
                        mainEquipmentBtn.setCompoundDrawablesWithIntrinsicBounds(null, topeq, null, null);
                        mainScenarioBtn.setVisibility(View.VISIBLE);
                        //侧滑栏打开手势滑动
                        activityMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        fragmentList.clear();
                        HomeFragment homeFragment = new HomeFragment();
                        EquipmentFragment equipmentFragment = new EquipmentFragment();
                        AreaFragment areaFragment = new AreaFragment();
                        ScenarioFragment scenarioFragment = new ScenarioFragment();

                        fragmentList.add(homeFragment);
                        fragmentList.add(equipmentFragment);
                        fragmentList.add(areaFragment);
                        fragmentList.add(scenarioFragment);
                        try {
                            if (null == fadapter) {
                                fadapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
                                mainFragment.setAdapter(fadapter);
                            } else {
                                fadapter.notifyDataSetChanged();
                            }
                            mainHomeBtn.setChecked(true);
                        } catch (Exception e) {
                            showToast("切换出错");
                        }
                        PreferencesUtils.saveString(PreferencesUtils.MODEL, "household");

                    }
                });


        mCompositeSubscription.add(mSubscription04);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            //首页
            case R.id.main_home_btn:
//                showFragment(R.id.main_fragment, homeFragment);
                mainFragment.setCurrentItem(0, false);
                break;
            //设备
            case R.id.main_equipment_btn:
                mainFragment.setCurrentItem(1, false);
//                showFragment(R.id.main_fragment, equipmentFragment);
                break;
            //区域
            case R.id.main_area_btn:
                mainFragment.setCurrentItem(2, false);
//                showFragment(R.id.main_fragment, areaFragment);
                break;
            //场景
            case R.id.main_scenario_btn:
                mainFragment.setCurrentItem(3, false);
//                showFragment(R.id.main_fragment, scenarioFragment);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                AppContext.getInstance().getSerialInstance().getGateWayInfo();
                if (null != handler)
                    handler.sendEmptyMessageDelayed(1, 8000L);
                break;
            case 2:
                if (null != progressDialog) {
                    progressDialog.setMessage("正在往网关发升级包，\n切勿关闭APP，切勿关闭网关电源");
                    progressDialog.setProgress(msg.arg1);
                }
                break;
            case 3:
                if (null != progressDialog) {
                    progressDialog.setMessage("网关升级中...");
                    progressDialog.setProgress(msg.arg1);
                }
                break;
            case 4:
                if (null != subInterval)
                    subInterval.unsubscribe();
                if (null != progressDialog) {
                    progressDialog.dismiss();
                }
                showToast("升级失败");
                break;
            case 5:
                if (null != progressDialog) {
                    progressDialog.setMessage("升级包下载中...");
                    progressDialog.setProgress(msg.arg1);
                }
                break;
            case 6:
                if (null != subInterval)
                    subInterval.unsubscribe();
                if (null != mDialog) {
                    mDialog.dismiss();
                }
                showToast("升级失败");
                break;
            case 7:
                if (null != mDialog) {
                    mDialog.setTitle("网关升级中...");
                    mDialog.setProgress(msg.arg1);
                }
                break;
            case 8:
                if (null != pb)
                    pb.setProgress(msg.arg1);
                break;


        }
        return false;
    }

    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        List<BaseFragment> list;
        private FragmentManager mFragmentManager;

        public MyFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
            super(fm);
            this.list = list;
            this.mFragmentManager = fm;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public BaseFragment getItem(int arg0) {
            return list.get(arg0);
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
//        }
    }

    /**
     * 升级接口回调
     *
     * @param updateinfo
     */
    @Override
    public void resUpdate(UpdateModel updateinfo) {
        String responseCode = updateinfo.getHeader().getHttp_code();
        if ("200".equals(responseCode)) {
            String newVersion = updateinfo.getBody().getNew_version();
            int falg = newVersion.compareTo(AppUtil.getAppVersionName(this));
            //有新版本
            if (falg > 0) {
                boolean isMust = false;
                //是否为强制更新  true 为强制更新
                if (updateinfo.getBody().getForce_upgrade().equals("true")) {
                    isMust = true;
                }
                showDialog(isMust, updateinfo.getBody().getUrl(), updateinfo.getBody().getReadme());
            }
        }
    }

    private void showProgressDialog(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress <= 100) {
                    mDialog.setTitle("正在下载中...\n切勿切断网关电源");
                    mDialog.setProgress(progress);

                } else if (progress > 100 && progress < 200) {
                    mDialog.setTitle("网关下载完成，网关升级中...");
                    mDialog.setProgress(progress - 100);
                }
                mDialog.show();
                if (progress >= 200) {
                    showProssDialog();
                }
            }
        });
    }


    private void showProssDialog() {
        subInterval = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mDialog)
                            mDialog.dismiss();//关闭对话框
                        showToast("网关升级出错！");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong >= 25) {
                            handler.sendEmptyMessage(6);
                            return;
                        }

                        if (aLong >= 5 && 25 > aLong) {
                            AppContext.getInstance().getSerialInstance().getGateWayInfo();
                        }

                        if (aLong < 25) {
                            long pross = aLong * 5;
                            Message msg = Message.obtain();
                            msg.what = 7;
                            msg.arg1 = (int) pross;
                            handler.sendMessage(msg);
                        }
                    }
                });
    }

    /**
     * 网关升级
     *
     * @param updateinfo
     */
    @Override
    public void resGwCallback(UpdataApkResponse updateinfo) {
        String responseCode = updateinfo.getHeader().getHttp_code();
        if ("200".equals(responseCode)) {
            this.updateinfo = updateinfo;
        }
    }

    /**
     * 检查版本更新
     *
     * @param isMust
     */
    private void showupdateDialog(final boolean isMust, final String url) {
        String cancel_txt = "";
        if (isMust) {
            cancel_txt = "退出";
        } else {
            cancel_txt = "暂时不";
        }
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("网关需要升级，不消耗额外流量").cancelable(false).title("更新提示")
                .leftBtnText(cancel_txt).Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("更新");
        DialogManager.showDialog(MainActivity.this, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onDismiss() {
                super.onDismiss();
            }

            @Override
            public void onRightClick() {
                //2.2.5以下版本
                if (AppContext.getVer().toString().compareTo("2.2.5") <= 0) {
                    if ("WIFI".equals(AppUtil.getNetworkType(MainActivity.this))) {
                        pdialog = new ProgressDialog(MainActivity.this);
                        pdialog.setMessage("正在连接网关...");
                        pdialog.show();
                        ThreadPoolUtils.execute(new Runnable() {
                            @Override
                            public void run() {
                                int number = AppContext.getInstance().getSerialInstance().connectLANZll();
                                if (number > 0) {
                                    final String[] ips = AppContext.getInstance().getSerialInstance().getGatewayIps(number);
                                    final String[] snids = AppContext.getInstance().getSerialInstance().getBoxSnids(number);
                                    final LoginResult.BodyBean.GatewayListBean currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));

                                    boolean flag = true;
                                    for (int i = 0; i < ips.length; i++) {
                                        if (currentGw.getUuid().equals(snids[i])) {
                                            LogUtil.e("sind=", "snid=" + snids[i]);
                                            flag = false;
                                            LoginLocalGateway(snids[i], ips[i], url);
                                        }
                                    }
                                    if (flag) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "本地连接网关失败，请确保手机与网关在相同局域网", Toast.LENGTH_LONG).show();
                                                if (null != pdialog)
                                                    pdialog.dismiss();
                                                AppContext.getInstance().getSerialInstance().connectRemoteZll(currentGw.getUsername(), currentGw.getPassword());
                                            }
                                        });

                                    }

                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.showShort("本地连接网关失败，请确保手机与网关在相同局域网");
                                            if (null != pdialog)
                                                pdialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        showToast("本地连接网关失败，请确保手机与网关在相同局域网");
                    }
                }
                //版本2.2.5以上的升级
                else {
                    ThreadPoolUtils.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppContext.getInstance().getSerialInstance().upgradeGatewayGD(url);
                        }
                    });
                }
            }

            @Override
            public void onLeftClick() {
                if (isMust) {
                    finish();
                    ActivityPageManager.getInstance().finishAllActivity();
                    System.exit(0);
                }
            }
        });
    }

    /**
     * 登录本地网关
     */
    private void LoginLocalGateway(final String snid, final String ip, final String url) {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int result = AppContext.getInstance().getSerialInstance().connectLANZllByIp(ip, snid);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer ret) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != pdialog)
                            pdialog.dismiss();
                    }
                });

                if (ret > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("本地连接成功.");
                        }
                    });
                    return true;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("本地连接网关失败，请确保手机与网关在相同局域网");
                        }
                    });
                    LoginResult.BodyBean.GatewayListBean currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                    AppContext.getInstance().getSerialInstance().connectRemoteZll(currentGw.getUsername(), currentGw.getPassword());
                    return false;
                }
            }
        }).observeOn(Schedulers.io())
                .flatMap(new Func1<Integer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Integer ret) {
                        return downloadBin(url);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != pdialog)
                            pdialog.dismiss();
                        if (null != progressDialog)
                            progressDialog.dismiss();//关闭对话框
                        showToast("网关升级出错！");
                    }

                    @Override
                    public void onNext(Boolean b) {
                        if (b) {
                            showPross();
                        } else {
                            progressDialog.dismiss();//关闭对话框
                            showToast("网关升级出错！");
                        }
                    }
                });
        mCompositeSubscription.add(sub);
    }

    //网关升级进度
    private void showPross() {
        subInterval = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != progressDialog)
                            progressDialog.dismiss();//关闭对话框
                        showToast("网关升级出错！");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong >= 40) {
                            handler.sendEmptyMessage(4);
                            return;
                        }
                        if (aLong >= 5 && aLong % 5 == 0) {
                            ThreadPoolUtils.getInstance().getSingleThreadExecutor().submit(new Runnable() {
                                @Override
                                public void run() {
                                    AppContext.getInstance().getSerialInstance().getGateWayInfo();
                                }
                            });
                        }
                        if (aLong < 40) {
                            long pross = (long) (aLong * 2.5);
                            Message msg = Message.obtain();
                            msg.what = 3;
                            msg.arg1 = (int) pross;
                            handler.sendMessage(msg);
                        }
                    }
                });
    }

    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private void verifyRecordPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_RECORD,
                    1);
        }
    }

    private ProgressDialog progressDialog;//进度条
    int progress = 0;

    /**
     * 下载bin 同时发送给网关 进行升级
     */
    private Observable<Boolean> downloadBin(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("正在往网关发升级包，\n切勿关闭APP，切勿关闭网关电源");
                progressDialog.setMax(100);//进度条最大值
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平样式
                progressDialog.show();
            }
        });

        Observable<Boolean> call = mApiWrapper.downloadFile(url).
                observeOn(Schedulers.io()).
                map(new Func1<ResponseBody, Boolean>() {
                    @Override
                    public Boolean call(ResponseBody body) {
                        InputStream inputStream = null;
                        FileInputStream stream = null;
                        File directory = Environment.getExternalStorageDirectory();
                        String fileName = url.substring(url.lastIndexOf("/") + 1);
                        final String filePath = directory.getAbsolutePath() + "/WangLi/" + fileName;
                        File file = new File(directory.getAbsolutePath() + "/WangLi");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        FileUtils.createFile(filePath);
                        OutputStream outputStream = null;
                        try {
                            byte[] fileReader = new byte[4096];
                            long fileSize = body.contentLength();
                            long fileSizeDownloaded = 0;
                            inputStream = body.byteStream();
                            outputStream = new FileOutputStream(filePath);
                            while (true) {
                                int read = inputStream.read(fileReader);
                                if (read == -1) {
                                    break;
                                }
                                fileSizeDownloaded += read;
                                outputStream.write(fileReader, 0, read);

                                //计算进度
                                progress = (int) ((double) fileSizeDownloaded * 100 / fileSize);//先计算出百分比在转换成整型
                                Message msg = Message.obtain();
                                msg.what = 5;
                                msg.arg1 = progress;
                                handler.sendMessage(msg);
                            }
                            outputStream.flush();
                            int bytcnt11 = 256;
                            stream = new FileInputStream(filePath);
                            long fCRC = 0;
                            long total = 0;
                            byte[] upbyte = new byte[bytcnt11 + 5];
                            while (true) {
                                int upread = stream.read(upbyte, 5, bytcnt11);
                                if (upread == -1) {
                                    break;
                                }
                                //计算进度
                                progress = (int) ((double) total * 100 / fileSize);//先计算出百分比在转换成整型
                                Message msg = Message.obtain();
                                msg.what = 2;
                                msg.arg1 = progress;
                                handler.sendMessage(msg);
                                for (int i2 = 5; i2 < upread + 5; i2++) {
                                    fCRC += ((long) upbyte[i2]) & 0xff;
                                }
                                AppContext.getInstance().getSerialInstance().upd(upbyte, upread, total);
//                                total += upread;
                                total += (long) bytcnt11;
                                LogUtil.e("total=", total + "/" + fileSize);
                                try {
                                    Thread.sleep(50);
                                    LogUtil.e("Thread", Thread.currentThread().getName());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            AppContext.getInstance().getSerialInstance().updStart(fCRC);
                            //将获得的所有字节全部返回
                        } catch (IOException e) {
                            return false;
                        } finally {
                            try {
                                if (stream != null) {
                                    stream.close();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            } catch (Exception e) {
                                progressDialog.dismiss();//关闭对话框
                                return false;
                            }
                        }
                        return true;
                    }
                });
        return call;
    }

    /**
     * isMust 是否为强制更新
     * apkUrl apk下载路径
     *
     * @author ZhaoLi.Wang
     * @date 2016-12-6 上午11:49:03
     */
    private void showDialog(final boolean isMust, final String apkUrl, String msg) {
        String cancel_txt = "";
        String content_txt = "有新版本是否更新？";
        if (isMust) {
            cancel_txt = "退出";
        } else {
            cancel_txt = "暂时不";
        }

        if (msg != null && msg.length() > 0) {
            content_txt = msg;
        }
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg(content_txt).cancelable(false).title("更新提示")
                .leftBtnText(cancel_txt).Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("更新");
        DialogManager.showDialog(this, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onDismiss() {
                super.onDismiss();
            }

            @Override
            public void onRightClick() {
                rl_pb.setVisibility(View.VISIBLE);
                File directory = Environment.getExternalStorageDirectory();
                final String saveurl = directory.getAbsolutePath() + "/WangLi/" + "smarthome_wl.apk";
                new DownloadProgressUtil(apkUrl, handler, saveurl, mApiWrapper, new DownloadProgressUtil.DownloadListener() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rl_pb.setVisibility(View.GONE);
                            }
                        });
                        Intent apkIntent = new Intent(Intent.ACTION_VIEW);
                        apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        apkIntent.setAction(Intent.ACTION_VIEW);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            apkIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.fbee.smarthome_wl.fileprovider", new File(saveurl));
                            apkIntent.setDataAndType(uri,
                                    "application/vnd.android.package-archive");
                        } else {
                            apkIntent.setDataAndType(Uri.fromFile(new File(saveurl)),
                                    "application/vnd.android.package-archive");
                        }
                        startActivity(apkIntent);
                        Process.killProcess(Process.myPid());
                    }

                    @Override
                    public void onFail() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rl_pb.setVisibility(View.GONE);
                                showToast("升级失败！");
                            }
                        });

                    }
                }).start();

                // 不设置down.setDestinationInExternalFilesDir(context,null,apkname);
                //会默认将下载的apk文件放在/data/data/com.android.providers.downloads/cache/xxx.apk
//                DownloadManager.Request request = new DownloadManager.Request(
//                        Uri.parse(apkUrl));
//                // 设置通知栏标题
//                request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE);
//                request.setTitle("下载");
//                request.setDescription("下载王力智能");
//                request.setAllowedOverRoaming(false);
//                // 设置文件存放目录
//                DownloadManager.Query query = new DownloadManager.Query();
//                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
//                if (!checkSDCardAvailable()) {
//                    DownloadManager downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                    Long id = downManager.enqueue(request);
//                    PreferencesUtils.saveLong("downID", id);
//                    rl_pb.setVisibility(View.VISIBLE);
//                    showUpdateProgressBar(id);
//                } else {
//                    request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, "smarthome_wl.apk");
//                    DownloadManager downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                    Long id = downManager.enqueue(request);
//                    PreferencesUtils.saveLong("downID", id);
//                    rl_pb.setVisibility(View.VISIBLE);
//                    showUpdateProgressBar(id);
//                }
            }

            @Override
            public void onLeftClick() {
                if (isMust) {
                    finish();
                    ActivityPageManager.getInstance().finishAllActivity();
                    System.exit(0);
                }
            }
        });
    }


    private String getFilePathFromUri(Context c, Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            ContentResolver contentResolver = c.getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        return filePath;
    }

    private void showUpdateProgressBar(final Long id) {
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final int[] arr = getBytesAndStatus(id);
                pb.setProgress((int) ((1f * arr[0] / arr[1]) * 100 + 0.5));
                if ((int) ((1f * arr[0] / arr[1]) * 100 + 0.5) >= 100) {
                    cancel();
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            rl_pb.setVisibility(View.GONE);
                        }
                    });
                }
            }
        };
        timer.schedule(task, 0, 1000);
    }

    public int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[]{-1, -1, 0};
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query()
                .setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c
                        .getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[1] = c
                        .getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }


    /**
     * Check the SD card
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showLoadingDialog() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                AppContext.clearAllDatas();
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                //释放资源
                mSerial.releaseSource();
                //移康退出
                icvss.equesUserLogOut();
                ActivityPageManager.finishAllActivity();
                System.exit(0);
            }
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND
                                | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND
                                | AudioManager.FLAG_SHOW_UI);
                return true;
        }
        return true;
    }

    /**
     * 请求添加用户设备到本地服务器
     */
    public void reqAddDeviceUser(String ieee, String userId, String alairs) {
        AddDeviceUser body = new AddDeviceUser();
        body.setVendor_name("feibee");
        body.setUuid(ieee);
        AddDeviceUser.DeviceUserBean deviceUserBean = new AddDeviceUser.DeviceUserBean();
        deviceUserBean.setId(userId);
        deviceUserBean.setNote(alairs);
       /* List<String> noticeList=new ArrayList<>();
        noticeList.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        deviceUserBean.setWithout_notice_user_list(noticeList);*/
        body.setDevice_user(deviceUserBean);
        presenter.reqAddDeviceUser(body);
    }

    /**
     * 添加设备用户返回
     *
     * @param bean
     */
    @Override
    public void resAddDeviceUser(BaseResponse bean) {
    }

    @Override
    protected void onDestroy() {
        presenter.reqDestroyUser();
        if (subInterval != null && !subInterval.isUnsubscribed()) {
            subInterval.unsubscribe();
        }
        super.onDestroy();
    }
}