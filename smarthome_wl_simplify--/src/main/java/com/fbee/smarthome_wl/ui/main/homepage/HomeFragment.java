package com.fbee.smarthome_wl.ui.main.homepage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.HomeDeviceAdapter;
import com.fbee.smarthome_wl.adapter.HomeScenarioAdapter;
import com.fbee.smarthome_wl.adapter.itemdecoration.SpaceItemDecoration;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.HintCountInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.AddDefenseAreaEvent;
import com.fbee.smarthome_wl.event.AddDevicesToDefenseArea;
import com.fbee.smarthome_wl.event.ChangeDevicenameEvent;
import com.fbee.smarthome_wl.event.DefenseAreaEvent;
import com.fbee.smarthome_wl.event.EnableDefensAreaEvent;
import com.fbee.smarthome_wl.event.GateSnidEvent;
import com.fbee.smarthome_wl.event.HomePagerUpMain;
import com.fbee.smarthome_wl.event.SwitchDataEvent;
import com.fbee.smarthome_wl.event.UpDataGwName;
import com.fbee.smarthome_wl.event.UpdateEquesNameEvent;
import com.fbee.smarthome_wl.event.UpdateHomeSetEvent;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.request.HomePageInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.corecode.CaptureActivity;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockActivity;
import com.fbee.smarthome_wl.ui.equesdevice.EquesDeviceInfoActivity;
import com.fbee.smarthome_wl.ui.homedeviceedit.HomeEditActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.AES256Encryption;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.view.AnimationLinearLayout;
import com.fbee.smarthome_wl.view.loopswitch.AutoSwitchAdapter;
import com.fbee.smarthome_wl.view.loopswitch.AutoSwitchView;
import com.fbee.smarthome_wl.view.loopswitch.LoopModel;
import com.fbee.smarthome_wl.widget.dialog.DialogColor;
import com.fbee.smarthome_wl.widget.dialog.DialogCurtain;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.fbee.smarthome_wl.widget.dialog.DialogSwitch;
import com.fbee.smarthome_wl.widget.dialog.DialogTemperature;
import com.fbee.smarthome_wl.widget.pop.PopwindowChoose;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.Serial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.common.AppContext.getmOurDevices;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.homepage
 * @anthor create by Zhaoli.Wang
 * @time 2017/2/13 10:22
 */
public class HomeFragment extends BaseFragment<HomeContract.Presenter> implements BaseRecylerAdapter.OnItemClickLitener, HomeContract.View, View.OnClickListener {
    private AnimationLinearLayout layoutMainEquip;
    private RelativeLayout rlMainEquip;
    private RecyclerView rvMainEquip;
    private AnimationLinearLayout layoutMainScenario;
    private RelativeLayout rlMainScenario;
    private RecyclerView rvMainScenario;

    private ImageView ivIconArrow;
    private ImageView ivIconSarrow;

    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ImageView ivSwitchVideo;
    private ImageView ivVideo;
    private AutoSwitchView loopswitch;
    private AutoSwitchAdapter mAdapter;
    private List<String> imageUrls;
    private List<HomePageResponse.BodyBean.SceneListBean> scenarioList;
    private List<HomePageResponse.BodyBean.DeviceListBean> deviceInfos;
    private HomeDeviceAdapter devicesAdapter;
    private HomeScenarioAdapter scenarioAdapter;
    private PopwindowChoose popwindow; //切换网关pop

    private RelativeLayout layoutEquipmentState;
    private TextView tvOnlineState;
    private TextView tvEquipmentName;
    private TextView tvDeviceBattery;
    //    private DialogChooseCateye dialog;
    private String itemDviceInfo = "itemDviceInfo";
    /**
     * 网关列表
     */
    private List<LoginResult.BodyBean.GatewayListBean> mGateWaylist;
    private LoginResult.BodyBean.GatewayListBean gw; //网关信息

    private LoginResult.BodyBean.GatewayListBean currentGw; //当前网关信息
    private Subscription sebceSub;
    private HashSet<String> stringSet;
    private String Alarm;
    private String Call;
    private String mcountryName;
    public static String adduser;
    private String passwd;
    private String username;
    private List<DeviceInfo> fbeeDevices;
    private List<EquesListInfo.bdylistEntity> equessDevices;
    private String equessUid;
    private List<LoginResult.BodyBean.GatewayListBean> gateway_list;
    private List<LoopModel> datas;
    private LoginResult.BodyBean bodyBean;
    private List<LoginResult.BodyBean.GatewayListBean> gatewaLyist;
    private long currTime;
    private Subscription mAddSub;
    private ImageView ivEquip;
    private ImageView ivScence;


    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_homepage;
    }

    @Override
    public void initView() {
        initApi();
        createPresenter(new HomePresenter(this));
        back = (ImageView) mContentView.findViewById(R.id.back);
        title = (TextView) mContentView.findViewById(R.id.title);
        ivRightMenu = (ImageView) mContentView.findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) mContentView.findViewById(R.id.tv_right_menu);
        ivVideo = (ImageView) mContentView.findViewById(R.id.iv_video);
        layoutMainEquip = (AnimationLinearLayout) mContentView.findViewById(R.id.layout_main_equip);
        rlMainEquip = (RelativeLayout) mContentView.findViewById(R.id.rl_main_equip);
        rvMainEquip = (RecyclerView) mContentView.findViewById(R.id.rv_main_equip);
        layoutMainScenario = (AnimationLinearLayout) mContentView.findViewById(R.id.layout_main_scenario);
        rlMainScenario = (RelativeLayout) mContentView.findViewById(R.id.rl_main_scenario);
        rvMainScenario = (RecyclerView) mContentView.findViewById(R.id.rv_main_scenario);
        ivIconArrow = (ImageView) mContentView.findViewById(R.id.iv_icon_arrow);
        ivIconSarrow = (ImageView) mContentView.findViewById(R.id.iv_icon_sarrow);
        ivSwitchVideo = (ImageView) mContentView.findViewById(R.id.iv_switch_video);
        //轮播效果
        loopswitch = (AutoSwitchView) mContentView.findViewById(R.id.loopswitch);
        //设备装态栏
        layoutEquipmentState = (RelativeLayout) mContentView.findViewById(R.id.layout_equipment_state);
        tvOnlineState = (TextView) mContentView.findViewById(R.id.tv_online_state);
        tvEquipmentName = (TextView) mContentView.findViewById(R.id.tv_equipment_name);
        tvDeviceBattery = (TextView) mContentView.findViewById(R.id.tv_device_battery);
        ivEquip = (ImageView)mContentView.findViewById(R.id.iv_equip);
        ivScence = (ImageView) mContentView.findViewById(R.id.iv_scence);

    }

    @Override
    public void bindEvent() {
        back.setVisibility(View.VISIBLE);
        back.setImageResource(R.mipmap.home_menu);
        back.setOnClickListener(this);
        ivRightMenu.setOnClickListener(this);
        ivVideo.setOnClickListener(this);
        ivEquip.setOnClickListener(this);
        ivScence.setOnClickListener(this);
        //轮播图
        datas = new ArrayList<LoopModel>();
        LoopModel model = null;
        List<String> slides = AppContext.getInstance().getSlideshow();
        if(null != slides){
            for (int i = 0; i <slides.size() ; i++) {
                model = new LoopModel(slides.get(i), R.mipmap.loop);
                datas.add(model);
            }
        }else{
            model = new LoopModel(null, R.mipmap.loop);
            datas.add(model);
            model = new LoopModel(null, R.mipmap.loop);
            datas.add(model);
            model = new LoopModel(null, R.mipmap.loop);
            datas.add(model);
        }

        mAdapter = new AutoSwitchAdapter(getContext(), datas);
        mAdapter.setListener(new AutoSwitchAdapter.OnIitemClickListener() {
            @Override
            public void onIitemClickListener(int positon) {
                //showToast(datas.get(positon).getTitle());
            }
        });
        loopswitch.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        //飞比设备
        fbeeDevices = AppContext.getmOurDevices();
        //移康设备
        equessDevices = AppContext.getBdylist();
        currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        if (currentGw != null) {
            gatewaLyist = AppContext.getInstance().getBodyBean().getGateway_list();
            if (gatewaLyist != null && gatewaLyist.size() > 0) {
                for (int i = 0; i < gatewaLyist.size(); i++) {
                    String username = gatewaLyist.get(i).getUsername();
                    if (currentGw.getUsername().equals(username)) {
                        currentGw = AppContext.getInstance().getBodyBean().getGateway_list().get(i);
                    }
                }
            }
        }
        //防区添加结果接收
        receiveDefensAddSuccess();
        //接收所有防区
        receiveDefensArea();
        //布防撤防回调
        changgeDefensArea();
        //获取所有防区IDs
        AppContext.getInstance().getSerialInstance().getDefenseAreaInfos();

        initAdapter();
        //接收飞比设备名字改变
        receiveFbeeDeviceNameChange();
        //接收移康设备名修改
        receiveEquessDeviceNameChange();
        receiveHintChange();
        initPopup();
        addGateWay();
        mSubscription = RxBus.getInstance().toObservable(UpDataGwName.class)
                .compose(TransformUtils.<UpDataGwName>defaultSchedulers())
                .subscribe(new Action1<UpDataGwName>() {

                               private String username;
                               private LoginResult.BodyBean.GatewayListBean gatewayListBean;

                               @Override
                               public void call(UpDataGwName gw) {
                                   currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                                   popwindow.changeData(getMenuList());
                                   if (gw.getName() == null) {
                                       return;
                                   }
                                   if (gw.isAdd())
                                       getUserConfig();

                                   if (AppContext.getInstance().getBodyBean() != null) {
                                       String upDataName = gw.getName();
                                       mGateWaylist = AppContext.getInstance().getBodyBean().getGateway_list();
                                       if (mGateWaylist == null) {
                                           mGateWaylist = new ArrayList<LoginResult.BodyBean.GatewayListBean>();
                                       } else {
                                           for (int i = 0; i < mGateWaylist.size(); i++) {
                                               String note = mGateWaylist.get(i).getNote();
                                               String username = mGateWaylist.get(i).getUsername();
                                               if (note.equals("")) {
                                                   note = username;
                                               }
                                               if (upDataName.equals(note)) {
                                                   if (username.equals(currentGw.getUsername())) {
                                                       if (!note.isEmpty()) {
                                                           title.setText(note);
                                                       } else {
                                                           title.setText(upDataName);
                                                       }
                                                       RxBus.getInstance().post(new HomePagerUpMain(upDataName));
                                                       return;
                                                   }
                                                   if (note.equals(currentGw.getUsername())) {
                                                       RxBus.getInstance().post(new HomePagerUpMain(upDataName));
                                                   }
                                               }
                                           }
                                       }
                                   }
                               }
                           }
                );

        if (null == currentGw) {
            title.setText("首页");
            if (AppContext.getInstance().getBodyBean() != null && AppContext.getInstance().getBodyBean().getGateway_list() != null && AppContext.getInstance().getBodyBean().getGateway_list().size() > 0) {
                currentGw = AppContext.getInstance().getBodyBean().getGateway_list().get(0);
            } else {
                return;
            }

        }
        if (null != currentGw) {
            if (currentGw.getNote() != null && currentGw.getNote().length() > 0) {
                title.setText(currentGw.getNote());
                RxBus.getInstance().post(currentGw);
            } else {
                title.setText(currentGw.getUsername());
                RxBus.getInstance().post(currentGw);

            }
        }
        ivSwitchVideo.setOnClickListener(this);

        layoutMainEquip.setClickedViewHideView(rlMainEquip, rvMainEquip, ivIconArrow, true);
        layoutMainEquip.setDownResource(R.mipmap.arrow_down);
        layoutMainEquip.setUpResource(R.mipmap.arrow_up);

        layoutMainScenario.setClickedViewHideView(rlMainScenario, rvMainScenario, ivIconSarrow, true);
        layoutMainScenario.setDownResource(R.mipmap.arrow_down);
        layoutMainScenario.setUpResource(R.mipmap.arrow_up);
        showLoadingDialog();
        getUserConfig();
    }

    private void receiveHintChange() {
        //接收小红点改变
        Subscription subHint = RxBus.getInstance().toObservable(HintCountInfo.class)
                .compose(TransformUtils.<HintCountInfo>defaultSchedulers())
                .subscribe(new Action1<HintCountInfo>() {
                    @Override
                    public void call(HintCountInfo event) {
                        devicesAdapter.notifyDataSetChanged();
                    }
                });
        mCompositeSubscription.add(subHint);
    }

    /**
     * 接收移康设备名改变
     */
    private void receiveEquessDeviceNameChange() {

        //移康设备名修改成功
        Subscription mSubs = RxBus.getInstance().toObservable(UpdateEquesNameEvent.class)
                .compose(TransformUtils.<UpdateEquesNameEvent>defaultSchedulers())
                .subscribe(new Action1<UpdateEquesNameEvent>() {
                    @Override
                    public void call(UpdateEquesNameEvent event) {
                        for (int i = 0; i < deviceInfos.size(); i++) {
                            if (deviceInfos.get(i).getVendor_name().equals(FactoryType.EQUES)) {
                                if (deviceInfos.get(i).getUuid().equals(String.valueOf(event.getId()))) {
                                    deviceInfos.get(i).setNote(event.getName());
                                }
                            }
                        }
                        for (int i = 0; i < AppContext.getBdylist().size(); i++) {
                            if (AppContext.getBdylist().get(i).getBid().equals(event.getId())) {
                                AppContext.getBdylist().get(i).setName(event.getName());
                            }
                        }
                        devicesAdapter.notifyDataSetChanged();
                    }
                });
        mCompositeSubscription.add(mSubs);
    }

    /**
     * 接收飞比名字改变
     */
    private void receiveFbeeDeviceNameChange() {

        //飞比设备改名字
        Subscription mSubscription04 = RxBus.getInstance().toObservable(ChangeDevicenameEvent.class)
                .compose(TransformUtils.<ChangeDevicenameEvent>defaultSchedulers())
                .subscribe(new Action1<ChangeDevicenameEvent>() {
                    @Override
                    public void call(ChangeDevicenameEvent event) {
                        try {
                            for (int i = 0; i < deviceInfos.size(); i++) {
                                if (deviceInfos.get(i).getVendor_name().equals(FactoryType.FBEE)) {
                                    if (deviceInfos.get(i).getUuid().equals(String.valueOf(event.getUid()))) {
                                        deviceInfos.get(i).setNote(event.getName());
                                    }
                                }
                            }
                            for (int i = 0; i < AppContext.getmOurDevices().size(); i++) {
                                if (AppContext.getmOurDevices().get(i).getUId() == Integer.valueOf(event.getUid())) {
                                    AppContext.getmOurDevices().get(i).setDeviceName(event.getName());
                                }
                            }
                            devicesAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                        }

                    }
                });


        mCompositeSubscription.add(mSubscription04);
    }


    /**
     * 防区是否添加成功
     */
    private void receiveDefensAddSuccess(){
        Subscription mSubscription = RxBus.getInstance().toObservable(AddDefenseAreaEvent.class)
                .compose(TransformUtils.<AddDefenseAreaEvent>defaultSchedulers())
                .subscribe(new Action1<AddDefenseAreaEvent>() {
                    @Override
                    public void call(AddDefenseAreaEvent event) {
                        LogUtil.e("AddDefenseAreaEvent",event.toString());
                        if(event.getId() == 1 && event.getSuccess() ==1){
                            addDeviceToDefens();
                        }
                    }
                });
        mCompositeSubscription.add(mSubscription);

    }


    private void changgeDefensArea(){
            Subscription mSubscription = RxBus.getInstance().toObservable(EnableDefensAreaEvent.class)
                    .compose(TransformUtils.<EnableDefensAreaEvent>defaultSchedulers())
                    .subscribe(new Action1<EnableDefensAreaEvent>() {
                        @Override
                        public void call(EnableDefensAreaEvent event) {
                            if(event.getId() == 1 && event.getSuccess() ==1){
                                if(event.getEnable() == 1){
                                    ivRightMenu.setImageResource(R.mipmap.icon_protection);
                                    ivRightMenu.setTag(R.mipmap.icon_protection);
                                    showToast("布防成功！");
                                }else if(event.getEnable() == 0){
                                    ivRightMenu.setImageResource(R.mipmap.icon_removal);
                                    ivRightMenu.setTag(R.mipmap.icon_removal);
                                    showToast("撤防成功！");
                                }
                            }
                        }
                    });
            mCompositeSubscription.add(mSubscription);

    }


    private void getDeviceDefensArea(){
        Subscription mSubscription = RxBus.getInstance().toObservable(AddDevicesToDefenseArea.class)
                .compose(TransformUtils.<AddDevicesToDefenseArea>defaultSchedulers())
                .subscribe(new Action1<AddDevicesToDefenseArea>() {
                    @Override
                    public void call(AddDevicesToDefenseArea event) {
                        if(event.getId() == 1 && event.getSuccess() ==1){
                            ivRightMenu.setVisibility(View.VISIBLE);
                            ivRightMenu.setImageResource(R.mipmap.icon_protection);
                            ivRightMenu.setTag(R.mipmap.icon_protection);
                        }
                    }
                });
        mCompositeSubscription.add(mSubscription);

    }




    /**
     * 添加设备到防区
     */

    private void  addDeviceToDefens(){
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        Subscription subscription1 = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        LogUtil.e("addDeviceToDefens","addDeviceToDefens");
                        List<DeviceInfo> devices = AppContext.getmOurDevices();
                        ArrayList<Integer> mcevices = new ArrayList<Integer>();
                        for (int i = 0; i <devices.size(); i++) {
                            //传感器设备
                            if(devices.get(i).getDeviceId() == DeviceList.DEVICE_ID_SENSOR){
                                mcevices.add(devices.get(i).getUId());
                            }
                        }
                        int[] deviceIDs = new int[mcevices.size()];
                        for (int i = 0; i < mcevices.size(); i++) {
                            deviceIDs[i] = mcevices.get(i);
                        }
                        if(deviceIDs.length>0)
                        AppContext.getInstance().getSerialInstance().addDeviceToDefenseArea((byte) 1,deviceIDs);

                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);

    }


    /**
     * 接收防区
     */
    DefenseAreaEvent mDefenseAreaEvent;
    private void  receiveDefensArea(){

//        Subscription mSubscription = RxBus.getInstance().toObservable(DefenseAreaIDsEvent.class)
//                .compose(TransformUtils.<DefenseAreaIDsEvent>defaultSchedulers())
//                .subscribe(new Action1<DefenseAreaIDsEvent>() {
//                    @Override
//                    public void call(DefenseAreaIDsEvent event) {
//                        LogUtil.e("DefenseAreaIDsEvent",event.toString());
//                        boolean falg = true;
//                        for (int i = 0; i <event.getDefenseAreaIDs().length ; i++) {
//                            if(event.getDefenseAreaIDs()[i] ==1 ){
//                                falg = false;
//                                break;
//                            }
//                        }
//                        if(falg){
//                            byte[]  name= "防区1".getBytes();
//                            AppContext.getInstance().getSerialInstance().createDefenseArea(name, (byte) 1);
//                        }else{
//                            AppContext.getInstance().getSerialInstance().getDefenseAreaDetails((byte) 1);
//                        }
//
//
//                    }
//                });



        Subscription mSubscription04 = RxBus.getInstance().toObservable(DefenseAreaEvent.class)
                .compose(TransformUtils.<DefenseAreaEvent>defaultSchedulers())
                .subscribe(new Action1<DefenseAreaEvent>() {
                    @Override
                    public void call(DefenseAreaEvent event) {
                        mDefenseAreaEvent = event;
                        LogUtil.e("DefenseArea",event.toString());
                        if(null !=event && event.getId() == 1){
                            if(1==event.getEnable()){
                                ivRightMenu.setVisibility(View.VISIBLE);
                                ivRightMenu.setImageResource(R.mipmap.icon_protection);
                                ivRightMenu.setTag(R.mipmap.icon_protection);
                            }else if(0== event.getEnable()){
                                ivRightMenu.setVisibility(View.VISIBLE);
                                ivRightMenu.setImageResource(R.mipmap.icon_removal);
                                ivRightMenu.setTag(R.mipmap.icon_removal);
                            }
                        }

                    }
                });
//        mCompositeSubscription.add(mSubscription);
        mCompositeSubscription.add(mSubscription04);
    }

    /**
     * 获取用户配置
     */
    private void getUserConfig() {
        if (null == currentGw)
            return;
        HomePageInfoReq body = new HomePageInfoReq();
        body.setGateway_vendor_name(currentGw.getVendor_name());
        body.setGateway_uuid(currentGw.getUuid());
        presenter.getHomePageSetting(body);
    }


    /**
     * 初始化Adapter
     */
    private void initAdapter() {
        //设备
        deviceInfos = new ArrayList<HomePageResponse.BodyBean.DeviceListBean>();
        devicesAdapter = new HomeDeviceAdapter(mContext, deviceInfos);
        //场景
        scenarioList = new ArrayList<>();
        scenarioAdapter = new HomeScenarioAdapter(mContext, scenarioList);

        //更新配置
        Subscription mSubscriptionState = RxBus.getInstance().toObservable(UpdateHomeSetEvent.class)
                .compose(TransformUtils.<UpdateHomeSetEvent>defaultSchedulers())
                .subscribe(new Action1<UpdateHomeSetEvent>() {
                    @Override
                    public void call(UpdateHomeSetEvent event) {

                        deviceInfos.clear();
                        deviceInfos.addAll(AppContext.getInstance().getmHomebody().getDevice_list());
                        devicesAdapter.notifyDataSetChanged();
                        layoutMainEquip.setHideView(rvMainEquip);

                        scenarioList.clear();
                        scenarioList.addAll(AppContext.getInstance().getmHomebody().getScene_list());
                        scenarioAdapter.notifyDataSetChanged();
                        layoutMainScenario.setHideView(rvMainScenario);

                    }
                });

        mCompositeSubscription.add(mSubscriptionState);
        //设备点击
        devicesAdapter.setOnItemClickLitener(new BaseRecylerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

                HomePageResponse.BodyBean.DeviceListBean device = deviceInfos.get(position);
                //飞比
                if (device.getVendor_name().equals(FactoryType.FBEE)) {
                    DeviceInfo fbDeviceInfo = null;
                    fbeeDevices = AppContext.getmOurDevices();
                    for (int i = 0; i < fbeeDevices.size(); i++) {
                        if (device.getUuid().equals(String.valueOf(fbeeDevices.get(i).getUId()))) {
                            fbDeviceInfo = fbeeDevices.get(i);
                        }
                    }
                    if (fbDeviceInfo == null) {
                        LogUtil.e("首页设备", "++++++点击为空+++++++++");
                        return;
                    }

                    /*int status = fbDeviceInfo.getDeviceStatus();
                    if (status <= 0) {
                        ToastUtils.showShort("设备不在线!");
                    }*/
                    //插座
                    if (device.getType().equals(String.valueOf(DeviceList.DEVICE_ID_SOCKET))) {

                        DialogSwitch dialogSwitch = new DialogSwitch(getActivity(), fbDeviceInfo);
                        dialogSwitch.show();
                    }
                    //色温灯
                    else if (device.getType().equals(String.valueOf(DeviceList.DEVICE_ID_COLOR_TEMP1)) ||
                            device.getType().equals(String.valueOf(DeviceList.DEVICE_ID_COLOR_TEMP2))) {
                        DialogTemperature dialog = new DialogTemperature(mContext, fbDeviceInfo);
                        dialog.show();
                    }
                    //彩灯
                    else if (device.getType().equals(String.valueOf(DeviceList.DEVICE_ID_COLOR_PHILIPS))) {

                        DialogColor dialog = new DialogColor(mContext, fbDeviceInfo);
                        dialog.show();
                    }
                    //窗帘
                    else if (device.getType().equals(String.valueOf(DeviceList.DEVICE_ID_CURTAIN))) {
                        new DialogCurtain(mContext, fbDeviceInfo).show();
                    }
                    //门锁
                    else if (device.getType().equals(String.valueOf(DeviceList.DEVICE_ID_DOOR_LOCK))) {
                        Intent intent = new Intent(getActivity(), DoorLockActivity.class);
                        intent.putExtra(itemDviceInfo, fbDeviceInfo);
                        startActivity(intent);
                    }
                    //智能开关
                    else if (device.getType().equals(String.valueOf(DeviceList.DEVICE_ID_SWITCH))) {
                        DialogSwitch dialogSwitch = new DialogSwitch(mContext, fbDeviceInfo);
                        dialogSwitch.show();
                    }

                }


                //移康
                else if (device.getVendor_name().equals(FactoryType.EQUES)) {
                    for (int i = 0; i < equessDevices.size(); i++) {
                        if (device.getUuid().equals(equessDevices.get(i).getBid())) {
                            EquesListInfo.bdylistEntity bdylistEntity = equessDevices.get(i);
                            Bundle bundle = new Bundle();
                            bundle.putString(Method.ATTR_BUDDY_NICK, bdylistEntity.getNick());
                            bundle.putString(Method.ATTR_BUDDY_BID, bdylistEntity.getBid());
                            if (AppContext.getOnlines().size() > 0) {
                                for (int j = 0; j < AppContext.getOnlines().size(); j++) {
                                    String bid = AppContext.getOnlines().get(j).getBid();
                                    if (bdylistEntity.getBid().equals(bid)) {
                                        bundle.putString(Method.ATTR_BUDDY_UID, AppContext.getOnlines().get(j).getUid());
                                        bundle.putInt(Method.ATTR_BUDDY_STATUS, AppContext.getOnlines().get(j).getStatus());
                                    }
                                }
                            }
                            bundle.putString(Method.ATTR_BUDDY_NAME, bdylistEntity.getName());

                            skipAct(EquesDeviceInfoActivity.class, bundle);
                        }
                    }
                }

            }
        });

        //场景点击
        scenarioAdapter.setOnItemClickLitener(new BaseRecylerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, final int position) {
                showLoadingDialog("正在执行场景");
                currTime = System.currentTimeMillis();
                sebceSub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        Serial mSerial = AppContext.getInstance().getSerialInstance();
                        int ret = mSerial.setSence(Short.valueOf(scenarioList.get(position).getUuid()));
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
                                hideLoadingDialog();
                                showToast("场景执行失败");
                            }

                            @Override
                            public void onNext(Integer ret) {
                                long nowTime = System.currentTimeMillis();
                                if ((nowTime - currTime) > 2000) {
                                    mContext.hideLoadingDialog();
                                    if (ret >= 0) {
                                        showToast("场景执行成功");
                                    } else {
                                        showToast("场景执行失败");
                                    }
                                } else {
                                    long time = 2000 - (nowTime - currTime);
                                    delayDoing(time, ret);
                                }
                            }
                        });

            }
        });


        //默认列表，参数4就是几列
        GridLayoutManager gm = new GridLayoutManager(mContext, 3);
        rvMainEquip.setLayoutManager(gm);
        rvMainEquip.setItemAnimator(new DefaultItemAnimator());
        rvMainEquip.addItemDecoration(new SpaceItemDecoration(AppUtil.dip2px(mContext, 10)));
        rvMainEquip.setAdapter(devicesAdapter);

        GridLayoutManager g = new GridLayoutManager(mContext, 3);
        rvMainScenario.setLayoutManager(g);
        rvMainScenario.setItemAnimator(new DefaultItemAnimator());
        rvMainScenario.addItemDecoration(new SpaceItemDecoration(AppUtil.dip2px(mContext, 10)));
        rvMainScenario.setAdapter(scenarioAdapter);


    }

    private void delayDoing(long time, final int ret) {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        Subscription subscription1 = Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        hideLoadingDialog();
                        if (ret >= 0) {
                            showToast("场景执行成功");
                        } else {
                            showToast("场景执行失败");
                        }
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);
    }

    private void initPopup() {
        if (AppContext.getInstance().getBodyBean() != null) {
            mGateWaylist = AppContext.getInstance().getBodyBean().getGateway_list();
            if (mGateWaylist == null) {
                mGateWaylist = new ArrayList<>();
            }
        } else {
            return;
        }

        if (popwindow == null) {
            ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();
            for (int i = 0; i < mGateWaylist.size(); i++) {
                String alias = mGateWaylist.get(i).getNote();
                if (null == alias || alias.length() == 0) {
                    alias = mGateWaylist.get(i).getUsername();
                }
                PopwindowChoose.Menu pop = new PopwindowChoose.Menu(R.mipmap.add, alias);
                menulist.add(pop);
            }
            popwindow = new PopwindowChoose(mContext, menulist, this, this, false);
            popwindow.setAnimationStyle(R.style.popwin_anim_style);
        } else {

        }

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popwindow != null && popwindow.isShowing()) {
                    colsePop();
                } else {
                    showPop();
                }

            }

        });

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
            title.getLocationOnScreen(location);
            //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
            popwindow.showAtLocation(title, Gravity.NO_GRAVITY, (location[0] + title.getWidth() / 2) - popupWidth / 2, location[1] + title.getHeight());

        }

    }


    @Override
    public void onItemClick(View view, int position) {
        colsePop();
        adduser = null;
        if (mGateWaylist == null || mGateWaylist.size() <= position)
            return;
        gw = mGateWaylist.get(position);
        boolean aBoolean = PreferencesUtils.getBoolean(gw.getUsername() + PreferencesUtils.JPUSH_LOCK);
        if (aBoolean) {
            CloudPushService cloudPushService = Api.getCloudPushService();
            cloudPushService.bindTag(CloudPushService.DEVICE_TARGET, new String[]{gw.getUsername()}, null, new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    LogUtil.e("MyMessageReceiver+register+bindTag+onSuccess 2", s + "2");
                }

                @Override
                public void onFailed(String s, String s1) {
                    LogUtil.e("MyMessageReceiver+register+bindTag+onFailed 2", s + "---" + s1);
                }
            });
        }
        //切换的网关为当前网关
//        if (title.getText().equals(gw.getNote()) || title.getText().equals(gw.getUsername()))
//            return;


        //切换成虚拟网关
        if (AppUtil.isMobileNO(gw.getUsername())) {
            //移康退出
            mContext.icvss.equesUserLogOut();
            mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
            List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
            AppContext.clearAllDatas();
            AppContext.getInstance().getBodyBean().setGateway_list(gateway_list);
            AppContext.getInstance().getSerialInstance().releaseSource();

            deviceInfos.clear();
            devicesAdapter.notifyDataSetChanged();
            layoutMainEquip.setHideView(rvMainEquip);

            scenarioList.clear();
            scenarioAdapter.notifyDataSetChanged();
            layoutMainScenario.setHideView(rvMainScenario);
            AppContext.getInstance().setmHomebody(null);
            //通知页面更新数据
            RxBus.getInstance().post(new SwitchDataEvent());
            if (!gw.getNote().isEmpty()) {
                title.setText(gw.getNote());
            } else {
                title.setText(gw.getUsername());
            }
            RxBus.getInstance().post(gw);
            return;
        }


        if (AppUtil.isNetworkAvailable(mContext)) {
            //移康登录
            showLoadingDialog();
            presenter.loginFbee(gw.getUsername(), gw.getPassword());
//            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
        } else {
            showToast("当前无网络连接");
        }

    }


    @Override
    public void loginFbeeResult(int result) {
        switch (result) {
            case 1:
                //移康退出
                mContext.icvss.equesUserLogOut();
                colsePop();
                //是否是新网关
                boolean flag = true;
                if (null != adduser) {
                    if (mGateWaylist != null && mGateWaylist.size() > 0) {
                        for (int i = 0; i < mGateWaylist.size(); i++) {
                            String username = mGateWaylist.get(i).getUsername();
                            if (adduser.equals(username)) {
                                gw = mGateWaylist.get(i);
                                flag = false;
                                break;
                            }
                        }
                    }

                }
                ivRightMenu.setVisibility(View.INVISIBLE);
                //获取所有防区IDs
                AppContext.getInstance().getSerialInstance().getDefenseAreaInfos();

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


                if (adduser != null && flag) {
                    showLoadingDialog("网关登录成功，添加中...");
                    AppContext.getInstance().getSerialInstance().getGateWayInfo();
//                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                    mAddSub = Observable.timer(5000, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    showToast("网关掉线，添加网关失败，请确保网关联网成功顶灯是绿色");
                                    hideLoadingDialog();
                                    if (AppUtil.isNetworkAvailable(mContext)) {
                                        adduser = null;
                                        LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                                        if (null != gw)
                                            presenter.loginFbee(gw.getUsername(), gw.getPassword());

                                    } else {
                                        showToast("当前无网络连接");
                                    }
                                }
                            });

                } else {
                    hideLoadingDialog();
                    if (null != gw) {
                        mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
                        Api.jpushSetting(getActivity(), gw.getUsername());
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                        //更新首页配置
                        currentGw = gw;
                        showToast("登录网关成功");
                        getUserConfig();

                        String alias = gw.getNote();
                        username = gw.getUsername();
                        if (alias == null || alias.length() == 0) {
                            title.setText(username);
                            RxBus.getInstance().post(gw);
                        } else {
                            title.setText(alias);
                            RxBus.getInstance().post(gw);
                        }
                    }

                }


                break;
            case -4:
                hideLoadingDialog();
                showToast("网关登录人数已达到上限!");
                LoginResult.BodyBean.GatewayListBean bean = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                if (AppUtil.isMobileNO(bean.getUsername())) {
                    return;
                }
                presenter.loginFbee(bean.getUsername(), bean.getPassword());
                gw = bean;
                break;
            case -2:
//                hideLoadingDialog();
//                LoginResult.BodyBean.GatewayListBean bean02 = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
//                if (AppUtil.isMobileNO(bean02.getUsername())) {
//                    return;
//                }
//                presenter.loginFbee(bean02.getUsername(), bean02.getPassword());
//                gw = bean02;
                showToast("网关账号或密码错误!正在登录本地网关");
                presenter.loginLocalFbee(gw.getUuid());
                break;
            case -3:
//                hideLoadingDialog();
//
//                LoginResult.BodyBean.GatewayListBean bean03 = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
//                if (AppUtil.isMobileNO(bean03.getUsername())) {
//                    return;
//                }
//                presenter.loginFbee(bean03.getUsername(), bean03.getPassword());
//                gw = bean03;
                showToast("网关登录超时！正在登录本地网关");
                presenter.loginLocalFbee(gw.getUuid());
                break;
            case -5:
                showToast("DNS错误网关登录超时，正在登录本地网关!");
                presenter.loginLocalFbee(gw.getUuid());
                break;
            default:
                showToast("网关登录失败，正在登录本地网关!");
                presenter.loginLocalFbee(gw.getUuid());
                break;




        }
    }

    /**
     * 飞比本地登录返回
     * @param result
     */
    @Override
    public void loginLocalFbeeResult(int result) {
            if(result>0){
                showToast("本地登录网关成功");
                //移康退出
                mContext.icvss.equesUserLogOut();
                colsePop();
                //是否是新网关
                boolean flag = true;
                if (null != adduser) {
                    if (mGateWaylist != null && mGateWaylist.size() > 0) {
                        for (int i = 0; i < mGateWaylist.size(); i++) {
                            String username = mGateWaylist.get(i).getUsername();
                            if (adduser.equals(username)) {
                                gw = mGateWaylist.get(i);
                                flag = false;
                                break;
                            }
                        }
                    }

                }


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


                if (adduser != null && flag) {
                    showLoadingDialog("网关登录成功，添加中...");
                    AppContext.getInstance().getSerialInstance().getGateWayInfo();
//                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                    mAddSub = Observable.timer(5000, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    showToast("网关掉线，添加网关失败，请确保网关联网成功顶灯是绿色");
                                    hideLoadingDialog();
                                    if (AppUtil.isNetworkAvailable(mContext)) {
                                        adduser = null;
                                        LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                                        if (null != gw)
                                            presenter.loginFbee(gw.getUsername(), gw.getPassword());

                                    } else {
                                        showToast("当前无网络连接");
                                    }
                                }
                            });

                } else {
                    hideLoadingDialog();
                    if (null != gw) {
                        mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
                        Api.jpushSetting(getActivity(), gw.getUsername());
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                        //更新首页配置
                        currentGw = gw;

                        getUserConfig();

                        String alias = gw.getNote();
                        username = gw.getUsername();
                        if (alias == null || alias.length() == 0) {
                            title.setText(username);
                            RxBus.getInstance().post(gw);
                        } else {
                            title.setText(alias);
                            RxBus.getInstance().post(gw);
                        }
                    }

                }
            }else{
                hideLoadingDialog();
                showToast("网关登录失败");
                LoginResult.BodyBean.GatewayListBean bean = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                if (AppUtil.isMobileNO(bean.getUsername())) {
                    return;
                }
                gw = bean;
            }
    }


    LoginResult.BodyBean.GatewayListBean gwadd;

    private void addGateWay() {
        Subscription gateWaySubscription = RxBus.getInstance().toObservable(GateSnidEvent.class)
                .compose(TransformUtils.<GateSnidEvent>defaultSchedulers())
                .subscribe(new Action1<GateSnidEvent>() {
                    private List<LoginResult.BodyBean.GatewayListBean> gateway_list;

                    @Override
                    public void call(GateSnidEvent event) {
                        if (null == adduser) {
                            return;
                        }
                        Api.jpushSetting(getActivity(), adduser);
                        mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, adduser, EquesConfig.APPKEY);
                        boolean isAdd = true;
                        if (mGateWaylist != null && mGateWaylist.size() > 0) {
                            for (int i = 0; i < mGateWaylist.size(); i++) {
                                String snid = mGateWaylist.get(i).getUuid();
                                if (event.snid.equals(snid)) {
                                    isAdd = false;
                                    break;
                                }
                            }
                        }
                        if (isAdd) {
                            AddGateWayReq bodyBean = new AddGateWayReq();
                            bodyBean.setVendor_name(FactoryType.FBEE);
                            bodyBean.setUuid(AppContext.getGwSnid());
                            bodyBean.setUsername(adduser);
                            try {
                                bodyBean.setPassword(AES256Encryption.encrypt(passwd, AppContext.getGwSnid()));
                            } catch (Exception e) {
                            }
                            bodyBean.setAuthorization("admin");
                            bodyBean.setNote(adduser);
                            bodyBean.setVersion(AppContext.getVer());

//                            gwadd = new LoginResult.BodyBean.GatewayListBean();
//                            //存储当前网关
//                            gwadd.setUsername(adduser);
//                            gwadd.setPassword(bodyBean.getPassword());
//                            gwadd.setAuthorization("admin");
//                            gwadd.setVendor_name(FactoryType.FBEE);
//                            gwadd.setUuid(AppContext.getGwSnid());
//                            gwadd.setVersion(AppContext.getVer());
//                            gwadd.setNote(adduser);
//
//                            //添加到缓存网关列表
//                            boolean isexist = true;
//                            if (null != AppContext.getInstance().getBodyBean()) {
//                                gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
//                                for (int i = 0; i < gateway_list.size(); i++) {
//                                    if (gateway_list.get(i).getUuid().equals(gwadd.getUuid())) {
//                                        isexist = false;
//                                        break;
//                                    }
//                                }
//                                if (isexist) {
//                                    AppContext.getInstance().getBodyBean().getGateway_list().add(gwadd);
//                                }
//                            }
//                            popwindow.changeData(getMenuList());
//                            //更新首页配置
//                            currentGw = gwadd;
//                            getUserConfig();
//                            String alias = gwadd.getNote();
//                            username = gwadd.getUsername();
//                            if (TextUtils.isEmpty(alias)) {
//                                title.setText(username);
//                                RxBus.getInstance().post(gwadd);
//                            } else {
//                                title.setText(alias);
//                                RxBus.getInstance().post(gwadd);
//                            }
                            //网关地理信息
                            AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();
                            location.setCountries(AppContext.getMcountryName());
                            location.setProvince(AppContext.getMadminArea());
                            location.setCity(AppContext.getMlocality());
                            location.setPartition(AppContext.getMsubLocality());
                            location.setStreet(AppContext.getMfeatureName());
                            bodyBean.setLocation(location);
                            presenter.addGateway(bodyBean);

                        }
                    }
                });
        mCompositeSubscription.add(gateWaySubscription);
    }

    private ArrayList<PopwindowChoose.Menu> getMenuList() {

        ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();
        bodyBean = AppContext.getInstance().getBodyBean();
        if (bodyBean != null) {
            gateway_list = bodyBean.getGateway_list();
        }
        if (gateway_list == null || bodyBean == null) {
            gateway_list = new ArrayList<>();
        }
        for (int i = 0; i < gateway_list.size(); i++) {
            String note = AppContext.getInstance().getBodyBean().getGateway_list().get(i).getNote();
            if (null == note || note.length() == 0) {
                note = AppContext.getInstance().getBodyBean().getGateway_list().get(i).getUsername();
            }
            PopwindowChoose.Menu pop = new PopwindowChoose.Menu(R.mipmap.add, note);
            menulist.add(pop);
        }
        return menulist;
    }

    /**
     * 配置没有body时返回
     *
     * @param info
     */
    @Override
    public void userConfigResultNoBody(BaseResponse info) {
        if (info.getHeader().getHttp_code().equals("200")) {
            deviceInfos.clear();
            devicesAdapter.notifyDataSetChanged();
            layoutMainEquip.setHideView(rvMainEquip);
            scenarioList.clear();
            scenarioAdapter.notifyDataSetChanged();
            layoutMainScenario.setHideView(rvMainScenario);
            AppContext.getInstance().setmHomebody(null);
            addDevice();
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(info.getHeader().getReturn_string()));
        }
    }



    /**
     * 配置
     *
     * @param response
     */
    @Override
    public void userConfigResult(final HomePageResponse response) {
        hideLoading();

        /*if (null == response) {
            deviceInfos.clear();
            devicesAdapter.notifyDataSetChanged();
            layoutMainEquip.setHideView(rvMainEquip);

            scenarioList.clear();
            scenarioAdapter.notifyDataSetChanged();
            layoutMainScenario.setHideView(rvMainScenario);
            AppContext.getInstance().setmHomebody(null);

            addDevice();
            return;
        }*/
        //添加错误提示
        if (!response.getHeader().getHttp_code().equals("200")) {
            ToastUtils.showShort(RequestCode.getRequestCode(response.getHeader().getReturn_string()));
            return;
        }

        if (response.getBody().getSlideshow() != null) {
            datas.clear();
            for (int i = 0; i < response.getBody().getSlideshow().size(); i++) {
                if (response.getBody().getSlideshow().get(i) != null) {
                    LoopModel model = new LoopModel(response.getBody().getSlideshow().get(i), R.mipmap.loop);
                    datas.add(model);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
        if (response.getBody().getDevice_list() != null) {
//            devicesAdapter.notifyDataSetChanged();
            layoutMainEquip.setClickedViewHideView(rlMainEquip, rvMainEquip, ivIconArrow, true);
            layoutMainEquip.setHideView(rvMainEquip);
            //缓存首页展示数据
            AppContext.getInstance().setmHomebody(response.getBody());
            if (deviceInfos != null && response.getBody().getDevice_list().size() > 0) {
                Observable.timer(3000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {

                                if (null != deviceInfos) {
                                    deviceInfos.clear();
                                    deviceInfos.addAll(response.getBody().getDevice_list());
                                }


                                ArrayList<HomePageResponse.BodyBean.DeviceListBean> lastdevice = new ArrayList<HomePageResponse.BodyBean.DeviceListBean>();
                                List<DeviceInfo> devices = AppContext.getmOurDevices();
                                List<EquesListInfo.bdylistEntity> bdylist = AppContext.getBdylist();
                                for (int i = 0; i < deviceInfos.size(); i++) {

                                    if (deviceInfos.get(i).getVendor_name().equals(FactoryType.EQUES)) {
                                        for (int j = 0; j < bdylist.size(); j++) {
                                            if (deviceInfos.get(i).getUuid().equals(bdylist.get(j).getBid())) {
                                                HomePageResponse.BodyBean.DeviceListBean info = deviceInfos.get(i);
                                                String name = bdylist.get(j).getNick() != null ? bdylist.get(j).getNick() : bdylist.get(j).getName();
                                                info.setNote(name);
                                                lastdevice.add(info);
                                                break;
                                            }
                                        }

                                    } else if (deviceInfos.get(i).getVendor_name().equals(FactoryType.FBEE)) {
                                        for (int j = 0; j < devices.size(); j++) {
                                            if (deviceInfos.get(i).getUuid().equals(String.valueOf(devices.get(j).getUId()))) {
                                                HomePageResponse.BodyBean.DeviceListBean info = deviceInfos.get(i);
                                                info.setNote(devices.get(j).getDeviceName());
                                                lastdevice.add(info);
                                                break;
                                            }
                                        }
                                    }


                                }
                                deviceInfos.clear();
                                deviceInfos.addAll(lastdevice);
                                devicesAdapter.notifyDataSetChanged();
                                layoutMainEquip.setHideView(rvMainEquip);
                                if (lastdevice != null) {
                                    AppContext.getInstance().getmHomebody().setDevice_list(lastdevice);
                                }
                            }
                        });
            }

        } else {
            deviceInfos.clear();
            devicesAdapter.notifyDataSetChanged();
            layoutMainEquip.setClickedViewHideView(rlMainEquip, rvMainEquip, ivIconArrow, false);
            layoutMainEquip.setHideView(rvMainEquip);

            addDevice();
        }

        if (response.getBody().getScene_list() != null) {

            layoutMainScenario.setClickedViewHideView(rlMainScenario, rvMainScenario, ivIconSarrow, true);
            layoutMainScenario.setHideView(rvMainScenario);
            if (scenarioList != null && response.getBody().getScene_list().size() > 0) {
                Observable.timer(2000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {

                                if (null != scenarioList) {
                                    scenarioList.clear();
                                    scenarioList.addAll(response.getBody().getScene_list());
                                }
                                List<HomePageResponse.BodyBean.SceneListBean> resultList = new ArrayList<HomePageResponse.BodyBean.SceneListBean>();
                                List<SenceInfo> sences = AppContext.getmOurScenes();
                                for (int i = 0; i < scenarioList.size(); i++) {
                                    for (int j = 0; j < sences.size(); j++) {
                                        if (scenarioList.get(i).getNote().equals(sences.get(j).getSenceName())) {
                                            resultList.add(scenarioList.get(i));
                                            break;
                                        }
                                    }
                                }
                                scenarioList.clear();
                                scenarioList.addAll(resultList);
                                scenarioAdapter.notifyDataSetChanged();
                                layoutMainScenario.setHideView(rvMainScenario);
                                AppContext.getInstance().getmHomebody().setScene_list(resultList);
                            }
                        });
            }

        } else {
            scenarioList.clear();
            scenarioAdapter.notifyDataSetChanged();
            layoutMainScenario.setClickedViewHideView(rlMainScenario, rvMainScenario, ivIconSarrow, false);
            layoutMainScenario.setHideView(rvMainScenario);

        }


    }

    //添加门锁设备到首页
    private void addDevice() {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        List<DeviceInfo> doorlocks = AppContext.getInstance().getmDoorLockDevices();
                        if (null == doorlocks || doorlocks.size() == 0)
                            return;
                        ArrayList<HomePageResponse.BodyBean.DeviceListBean> lastdevice = new ArrayList<HomePageResponse.BodyBean.DeviceListBean>();
                        if (doorlocks.size() < 3) {
                            for (int i = 0; i < doorlocks.size(); i++) {
                                HomePageResponse.BodyBean.DeviceListBean bean = new HomePageResponse.BodyBean.DeviceListBean();
                                bean.setType(String.valueOf(DeviceList.DEVICE_ID_DOOR_LOCK));
                                bean.setUuid(String.valueOf(doorlocks.get(i).getUId()));
                                bean.setNote(doorlocks.get(i).getDeviceName());
                                bean.setVendor_name(FactoryType.FBEE);
                                lastdevice.add(bean);
                            }
                        } else {
                            for (int i = 0; i < 3; i++) {
                                HomePageResponse.BodyBean.DeviceListBean bean = new HomePageResponse.BodyBean.DeviceListBean();
                                bean.setType(String.valueOf(DeviceList.DEVICE_ID_DOOR_LOCK));
                                bean.setUuid(String.valueOf(doorlocks.get(i).getUId()));
                                bean.setNote(doorlocks.get(i).getDeviceName());
                                bean.setVendor_name(FactoryType.FBEE);
                                lastdevice.add(bean);
                            }

                        }
                        layoutMainEquip.setIsExpend(true);
                        deviceInfos.clear();
                        deviceInfos.addAll(lastdevice);
                        devicesAdapter.notifyDataSetChanged();
                        layoutMainEquip.setHideView(rvMainEquip);
                        if (lastdevice != null) {
                            AppContext.getInstance().getmHomebody().setDevice_list(lastdevice);
                        }
                    }

                });
    }


    /**
     * 用户配置设置返回
     *
     * @param info
     */
    @Override
    public void setCallBack(BaseResponse info) {
        hideLoadingDialog();
        if (info.getHeader().getHttp_code().equals("200")) {
            //保存最后登录的网关信息
            if (null != mAddSub) {
                mAddSub.unsubscribe();
            }
            if (null != adduser && AppUtil.isMobileNO(adduser)) {
                //移康退出
                mContext.icvss.equesUserLogOut();
                mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, adduser, EquesConfig.APPKEY);
                LoginResult.BodyBean.GatewayListBean bean = null;
                List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                boolean flag = false;
                for (int i = 0; i < gateway_list.size(); i++) {
                    if (gateway_list.get(i).getUsername().equals(adduser)) {
                        flag = true;
                        bean = gateway_list.get(i);
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gateway_list.get(i));
                        if (gateway_list.get(i).getNote() != null) {
                            title.setText(gateway_list.get(i).getNote());
                        } else {
                            title.setText(gateway_list.get(i).getUsername());
                        }
                    }
                }
                if (!flag) {
                    bean = new LoginResult.BodyBean.GatewayListBean();
                    bean.setUsername(adduser);
                    bean.setNote(adduser);
                    bean.setPassword("123456");
                    bean.setAuthorization("admin");
                    bean.setVendor_name("virtual");
                    bean.setUuid(adduser);
                    bean.setVersion("1.0.0");
                    gateway_list.add(bean);
                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), bean);
                    popwindow.changeData(getMenuList());
                    title.setText(adduser);
                }

                AppContext.clearAllDatas();
                LoginResult.BodyBean mbodyBean = new LoginResult.BodyBean();
                mbodyBean.setGateway_list(gateway_list);
                AppContext.getInstance().setBodyBean(mbodyBean);
                AppContext.getInstance().getSerialInstance().releaseSource();

                deviceInfos.clear();
                devicesAdapter.notifyDataSetChanged();
                layoutMainEquip.setHideView(rvMainEquip);

                scenarioList.clear();
                scenarioAdapter.notifyDataSetChanged();
                layoutMainScenario.setHideView(rvMainScenario);
                AppContext.getInstance().setmHomebody(null);
                //通知页面更新数据
                RxBus.getInstance().post(new SwitchDataEvent());
                RxBus.getInstance().post(bean);
                return;

            } else {
                gwadd = new LoginResult.BodyBean.GatewayListBean();
                //存储当前网关
                gwadd.setUsername(adduser);
                gwadd.setPassword(AES256Encryption.encrypt(passwd, AppContext.getGwSnid()));
                gwadd.setAuthorization("admin");
                gwadd.setVendor_name(FactoryType.FBEE);
                gwadd.setUuid(AppContext.getGwSnid());
                gwadd.setVersion(AppContext.getVer());
                gwadd.setNote(adduser);
                if(null != adduser)
                PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gwadd);
                showToast("网关添加成功");
                //添加到缓存网关列表
                boolean isexist = true;
                if (null != AppContext.getInstance().getBodyBean()) {
                    gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                    for (int i = 0; i < gateway_list.size(); i++) {
                        if (gateway_list.get(i).getUuid().equals(gwadd.getUuid())) {
                            isexist = false;
                            break;
                        }
                    }
                    if (isexist) {
                        AppContext.getInstance().getBodyBean().getGateway_list().add(gwadd);
                    }
                }
                popwindow.changeData(getMenuList());
                //更新首页配置
                currentGw = gwadd;
                getUserConfig();
                String alias = gwadd.getNote();
                username = gwadd.getUsername();
                if (TextUtils.isEmpty(alias)) {
                    title.setText(username);
                    RxBus.getInstance().post(gwadd);
                } else {
                    title.setText(alias);
                    RxBus.getInstance().post(gwadd);
                }
                adduser = null;
            }
        } else {
            showToast("网关添加到服务器失败");
            LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
            if (null != gw)
                presenter.loginFbee(gw.getUsername(), gw.getPassword());
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
    }

    @Override
    public void hideLoading() {
//        hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ((MainActivity) getActivity()).openLeftMenu();
                break;

            //首页猫眼点击
//            case R.id.iv_video:
//                //equessUid
//                //bundle1.putString(Method.ATTR_BUDDY_NICK, Nick);
//                if (equessUid != null) {
//                    Bundle bundle1 = new Bundle();
//                    bundle1.putString(Method.ATTR_BUDDY_UID, equessUid);
//                    skipAct(EquesCallActivity.class, bundle1);
//                }
//                break;
            case R.id.iv_right_menu:
                if(null != ivRightMenu.getTag() ){
                    if ((int) ivRightMenu.getTag() == R.mipmap.icon_protection) {
                        showCustomizeDialog();
                    } else if ((int) ivRightMenu.getTag() ==  R.mipmap.icon_removal) {
                        showdialog(mContext);
                    }
                }

                break;
            case R.id.tv_add:
                if (AppContext.getInstance().getBodyBean().getGateway_list() != null && AppContext.getInstance().getBodyBean().getGateway_list().size() > 9) {
                    showToast("最多添加10个网关");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    verifyRecordPermissions(mContext);
                } else {
                    startActivityForResult(new Intent(mContext, CaptureActivity.class), 88);
                }
                break;

            case R.id.iv_equip:
                Bundle bundle = new Bundle();
                bundle.putString("Type","equip");
                skipAct(HomeEditActivity.class,bundle);
                break;

            case R.id.iv_scence:
                Bundle bundle02 = new Bundle();
                bundle02.putString("Type","scence");
                skipAct(HomeEditActivity.class,bundle02);
                break;

        }
    }


    private  void showdialog(final Context context) {
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("是否确定布防？").cancelable(false).title("提示")
                .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("确定");

        DialogManager.showDialog(context, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                String psw = currentGw.getPassword();
                LogUtil.e("password",psw);
                AppContext.getInstance().getSerialInstance().setDefenseAreaToAble((byte)1,(byte)1,psw.getBytes());
            }
        });
    }

    AlertDialog alertDialog;
    private void showCustomizeDialog() {
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(mContext);
        final View dialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_modify_doolock_name, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_title);
        title.setText("撤防请输入登录密码");
        final EditText editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String editName = editText.getText().toString().trim();
                if (editName == null || editName.isEmpty()) {
                    ToastUtils.showShort("密码不能为空!");
                    return;
                }
                if(PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW).equals(editName)){
                    String psw = currentGw.getPassword();
                    LogUtil.e("password",psw);
                    AppContext.getInstance().getSerialInstance().setDefenseAreaToAble((byte)1,(byte)0,psw.getBytes());
                    if (alertDialog != null)
                        alertDialog.dismiss();
                }else{
                    ToastUtils.showShort("密码错误!");
                }

            }
        });
        cancleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        alertDialog = customizeDialog.show();
    }




    private static String[] PERMISSIONS_RECORD = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};

    private void verifyRecordPermissions(BaseActivity captureActivity) {
        //摄像头权限
        if (ContextCompat.checkSelfPermission(captureActivity,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //先判断有没有权限 ，没有就在这里进行权限的申请
            HomeFragment.this.requestPermissions(PERMISSIONS_RECORD, 2);
        } else {
            startActivityForResult(new Intent(mContext, CaptureActivity.class), 88);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(mContext, CaptureActivity.class), 88);
            } else {
                Intent intent = new Intent(mContext, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isPermission", true);
                startActivityForResult(intent, 88, bundle);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("首页设备", "+++onResume请求设备+++");
        if (fbeeDevices == null || fbeeDevices.size() == 0) {
            ThreadPoolUtils.getInstance().getSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    AppContext.getInstance().getSerialInstance().getDevices();
                    AppContext.getInstance().getSerialInstance().getSences();
                    AppContext.getInstance().getSerialInstance().getGroups();
                }
            });
        } else if (equessDevices == null || equessDevices.size() == 0) {
            mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME), EquesConfig.APPKEY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 88) {
            String result = data.getExtras().getString("result");
            LogUtil.e("扫描结果", result);
            try {
                adduser = result.split("GT")[1].split("pass")[0];
            } catch (Exception e) {
                showToast("扫描二维码解析出错");
            }

            if (null != adduser) {
                while (adduser.startsWith("0")) {
                    adduser = adduser.substring(1);
                }
            } else {
                showToast("扫描失败");
            }
            //虚拟
            if (null != adduser && AppUtil.isMobileNO(adduser)) {
                //添加网关到服务器
                AddGateWayReq bodyBean = new AddGateWayReq();
                bodyBean.setVendor_name("virtual");
                bodyBean.setUuid(adduser);
                bodyBean.setUsername(adduser);
                bodyBean.setPassword("123456");
                bodyBean.setAuthorization("admin");
                bodyBean.setNote(adduser);
                bodyBean.setVersion("1.0.0");
                AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();
                location.setCountries(AppContext.getMcountryName());
                location.setProvince(AppContext.getMadminArea());
                location.setCity(AppContext.getMlocality());
                location.setPartition(AppContext.getMsubLocality());
                location.setStreet(AppContext.getMfeatureName());
                bodyBean.setLocation(location);
                presenter.addGateway(bodyBean);
                return;
            }
            try {
                passwd = result.split("GT")[1].split("pass")[1];
            } catch (Exception e) {
                showToast("扫描二维码解析出错");
            }

            if (adduser != null && passwd.length() > 0) {
                if (AppUtil.isNetworkAvailable(mContext)) {
                    Api.jpushSetting(mContext, adduser);
                    //移康退出
//                    mContext.icvss.equesUserLogOut();
//                    //移康登录
//                    mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, adduser, EquesConfig.APPKEY);
                    presenter.loginFbee(adduser, passwd);
                    showLoadingDialog("登录中...");
                } else {
                    showToast("当前无网络连接");
                }
            } else {
                showToast("扫描失败");
            }
        } else if (resultCode == 66) {
            adduser = null;
            adduser = data.getExtras().getString("username");
            passwd = data.getExtras().getString("password");
            if (!adduser.isEmpty() && !passwd.isEmpty()) {
                presenter.loginFbee(adduser, passwd);
                showLoadingDialog("正在登录...");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoading();
//        if (null != dialog) {
//            dialog.cancel();
//        }
        //取消订阅
        mContext.cancelSubscription(sebceSub);
    }
}
