package com.fbee.smarthome_wl.ui.mainsimplify.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.equipfragmentpopwindow.PopWindowAdapter;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.EquesDeviceDelete;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.event.ChangeDevicenameEvent;
import com.fbee.smarthome_wl.event.GateSnidEvent;
import com.fbee.smarthome_wl.event.HomePagerUpMain;
import com.fbee.smarthome_wl.event.SwitchDataEvent;
import com.fbee.smarthome_wl.event.UpDataGwName;
import com.fbee.smarthome_wl.event.UpdateEquesNameEvent;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.corecode.CaptureActivity;
import com.fbee.smarthome_wl.ui.equesdevice.adddevices.AddDeviceActivity;
import com.fbee.smarthome_wl.ui.mainsimplify.home.doorlock.DlFragment;
import com.fbee.smarthome_wl.ui.mainsimplify.home.eques.EQfragment;
import com.fbee.smarthome_wl.ui.mainsimplify.home.other.FBotherFragment;
import com.fbee.smarthome_wl.utils.AES256Encryption;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.view.loopswitch.AutoSwitchAdapter;
import com.fbee.smarthome_wl.view.loopswitch.AutoSwitchView;
import com.fbee.smarthome_wl.view.loopswitch.LoopModel;
import com.fbee.smarthome_wl.widget.pop.PopwindowChoose;
import com.fbee.zllctl.DeviceInfo;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.common.AppContext.getmOurDevices;
import static com.fbee.smarthome_wl.ui.main.homepage.HomeFragment.adduser;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * 简化版首页
 */
public class HomeSimpleFragment extends BaseFragment<HomeSimpleContract.Presenter> implements View.OnClickListener, BaseRecylerAdapter.OnItemClickLitener,HomeSimpleContract.View,OnRefreshListener {
    private LinearLayout titleBar;
    private RelativeLayout headerRl;
    private TextView title;
    private ImageView ivRightMenu;
    private AutoSwitchView loopswitch;
    private TextView tvDeviceTitle;
    private ViewPager viewpager;
    //飞比设备
    private List<DeviceInfo> deviceInfos;
    private ArrayList<BaseFragment> fragmentList;
    MyFragmentPagerAdapter fadapter;
    private LoginResult.BodyBean.GatewayListBean currentGw; //当前网关信息
    private List<LoginResult.BodyBean.GatewayListBean> gatewaLyist;
    private PopwindowChoose popwindow; //切换网关pop
    private PopwindowChoose devidePop; //设备

    private List<PopwindowChoose.Menu> deviceMenuList;
    private List<EquesListInfo.bdylistEntity> bdylist; //移康设备
    private String username = null; //网关名
    private String passwd;
    private Subscription mAddSub;
    private PopupWindow popupWindow;
    private View parentView;
    private Timer timer;
    private TimerTask timerTask;
    private SwipeToLoadLayout swipeToLoadLayout;
    private LinearLayout mainLinear;

    private int mNum;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_home_simple;
    }

    @Override
    public void initView() {
        initApi();
        createPresenter(new HomeSimplePresenter(this));
        titleBar = (LinearLayout) mContentView.findViewById(R.id.title_bar);
        headerRl = (RelativeLayout) mContentView.findViewById(R.id.header_rl);
        title = (TextView) mContentView.findViewById(R.id.title);
        ivRightMenu = (ImageView) mContentView.findViewById(R.id.iv_right_menu);
        ivRightMenu.setOnClickListener(this);
        loopswitch = (AutoSwitchView) mContentView.findViewById(R.id.loopswitch);
        tvDeviceTitle = (TextView) mContentView.findViewById(R.id.tv_device_title);
        viewpager = (ViewPager) mContentView.findViewById(R.id.viewpager);
        swipeToLoadLayout = (SwipeToLoadLayout) mContentView.findViewById(R.id.swipeToLoadLayout);
        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        mainLinear = (LinearLayout) mContentView.findViewById(R.id.main_linear);
        parentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_equipment, null);
        ArrayList<LoopModel> datas = new ArrayList<LoopModel>();
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
        //轮播图
        AutoSwitchAdapter mAdapter = new AutoSwitchAdapter(getContext(), datas);
        mAdapter.setListener(new AutoSwitchAdapter.OnIitemClickListener() {
            @Override
            public void onIitemClickListener(int positon) {
                //showToast(datas.get(positon).getTitle());
            }
        });
        loopswitch.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setLoadMoreEnabled(false); //设置上拉不可用
        swipeToLoadLayout.setRefreshEnabled(false); //设置刷新不可用
        
    }

    @Override
    public void bindEvent() {
        deviceInfos = new ArrayList<>();
        fragmentList = new ArrayList<BaseFragment>();

        fadapter= new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), mContext);
        fadapter.setLists((ArrayList<BaseFragment>) fragmentList);
        viewpager.setAdapter(fadapter);
        //title
        setTitleName();
        initPop();
        //接收网关信息返回
        addGateWay();
        //网关名称更新
        updateGw();
        deviceMenuList = new ArrayList<>();

        receiveDevicesList();
        //接收移康设备列表
        receiveEquesListInfo();
        //删除飞比设备
        receiveDelDeviceInfoEvent();
        //修改飞比设备名称
        receiveChangeDevicenameEvent();
        //接收移康设备
        receiveEquessDeviceNameChange();

        bdylist = AppContext.getBdylist();
        for (int i = 0; i <bdylist.size() ; i++) {
            if(null !=   bdylist.get(i).getNick()){
                deviceMenuList.add(new PopwindowChoose.Menu(R.mipmap.add,
                        bdylist.get(i).getNick()));
            }else{
                deviceMenuList.add(new PopwindowChoose.Menu(R.mipmap.add,
                        bdylist.get(i).getName()));
            }

            EQfragment fragement=  new EQfragment();
            Bundle bundle=new Bundle();
            bundle.putSerializable("EQ",bdylist.get(i));
            fragement.setArguments(bundle);
            fragmentList.add(fragement);
        }
        initDeviceMenu();
        fadapter.notifyDataSetChanged();


        AppContext.getInstance().getSerialInstance().getDevices();

        //滑动监听
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvDeviceTitle.setText(deviceMenuList.get(position).getMenuText());

                mainLinear.getChildAt(mNum).setEnabled(false);
                mainLinear.getChildAt(position).setEnabled(true);
                mNum = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //刷新操作
        onRefresh();

    }




    private void updateGw() {

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

                                   if (AppContext.getInstance().getBodyBean() != null) {
                                       String upDataName = gw.getName();
                                       List<LoginResult.BodyBean.GatewayListBean> mGateWaylist = AppContext.getInstance().getBodyBean().getGateway_list();
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
    }

    private void initPop() {
        gatewaLyist = AppContext.getInstance().getBodyBean().getGateway_list();

        if (popwindow == null) {
            ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();
            for (int i = 0; i < gatewaLyist.size(); i++) {
                String alias = gatewaLyist.get(i).getNote();
                if (null == alias || alias.length() == 0) {
                    alias = gatewaLyist.get(i).getUsername();
                }
                PopwindowChoose.Menu pop = new PopwindowChoose.Menu(R.mipmap.add, alias);
                menulist.add(pop);
            }
            popwindow = new PopwindowChoose(mContext, menulist, this, this, false);
            popwindow.setAnimationStyle(R.style.popwin_anim_style);
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


    /**
     * 首页名字设置
     */
    private void setTitleName() {
        currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        gatewaLyist = AppContext.getInstance().getBodyBean().getGateway_list();
        if (currentGw != null) {
            if ( gatewaLyist.size() > 0) {
                for (int i = 0; i < gatewaLyist.size(); i++) {
                    String username = gatewaLyist.get(i).getUsername();
                    if (currentGw.getUsername().equals(username)) {
                        currentGw = AppContext.getInstance().getBodyBean().getGateway_list().get(i);
                    }
                }
            }
        }else{
            title.setText("首页");
            if ( gatewaLyist.size() > 0) {
                currentGw = gatewaLyist.get(0);
            }else{
                return;
            }
        }

        if (currentGw.getNote() != null && currentGw.getNote().length() > 0) {
            title.setText(currentGw.getNote());
            RxBus.getInstance().post(currentGw);
        } else {
            title.setText(currentGw.getUsername());
            RxBus.getInstance().post(currentGw);
        }

    }




    /**
     * 接收飞比设备
     */
    private void receiveDevicesList() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                receiveDevicesList();
            }
        };
        //注册RXbus接收飞比设备
        Subscription mSubscriptionDevice = RxBus.getInstance().toObservable(DeviceInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DeviceInfo>() {
                    @Override
                    public void call(DeviceInfo event) {
                        if (null != event) {
                            boolean tag = false;
                            for (int i = 0; i < AppContext.getmOurDevices().size(); i++) {
                                if (AppContext.getmOurDevices().get(i).getUId() == event.getUId()) {
                                    tag = true;
                                    break;
                                }
                            }
                            if (!tag) {
                                AppContext.getmOurDevices().add(event);
                            }

                            //门磁传感器不显示
                            if(event.getDeviceId() == DeviceList.DEVICE_ID_SENSOR
                                    || event.getDeviceId() ==DeviceList.DEVICE_ID_SWITCH_SCENE
                                    ||event.getDeviceId() ==DeviceList.DEVICE_ID_THTB2){
                                return;
                            }


                            for (int i = 0; i < deviceInfos.size(); i++) {
                                if (deviceInfos.get(i).getUId() == event.getUId()) {
                                    return;
                                }
                            }
                            deviceInfos.add(event);
                            deviceMenuList.add(new PopwindowChoose.Menu(R.mipmap.add,event.getDeviceName(),String.valueOf(event.getUId())));

                            initDeviceMenu();

                            switch (event.getDeviceId()){
                                //门锁
                                case  DeviceList.DEVICE_ID_DOOR_LOCK:
                                    DlFragment fragement=  new DlFragment();
                                    Bundle bundle=new Bundle();
                                    bundle.putSerializable("Dl",event);
                                    fragement.setArguments(bundle);
                                    fragmentList.add(fragement);
                                    addindicator();
                                    break;
                                default:
                                    FBotherFragment other = new FBotherFragment();
                                    Bundle bundle1=new Bundle();
                                    bundle1.putSerializable("other",event);
                                    other.setArguments(bundle1);
                                    fragmentList.add(other);
                                    addindicator();
                                    break;
                            }
                            fadapter.notifyDataSetChanged();

                        }
                    }


                }, onErrorAction);

        mCompositeSubscription.add(mSubscriptionDevice);

    }


    /**
     * 指示器
     */
    private View view;
    private void addindicator(){
        //创建底部指示器(小圆点)
        view = new View(mContext);
        view.setBackgroundResource(R.drawable.viewpager_background);
        view.setEnabled(false);
        //设置宽高
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(AppUtil.dp2px(mContext,5), AppUtil.dp2px(mContext,5));
        layoutParams.leftMargin = 10;
        //添加到LinearLayout
        mainLinear.addView(view, layoutParams);
        mainLinear.getChildAt(0).setEnabled(true);
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
                        for (int i = 0; i < deviceMenuList.size(); i++) {
                            if (deviceMenuList.get(i).getUid().equals(event.getId())) {
                                deviceMenuList.get(i).setMenuText(event.getName());
                            }

                        }
                        for (int i = 0; i < AppContext.getBdylist().size(); i++) {
                            if (AppContext.getBdylist().get(i).getBid().equals(event.getId())) {
                                AppContext.getBdylist().get(i).setName(event.getName());
                            }
                        }
                        devidePop.notifydata();
                    }
                });
        mCompositeSubscription.add(mSubs);
    }



    /**
     * 飞比设备改名字
     */
    private void receiveChangeDevicenameEvent() {
        Subscription mSubscription04 = RxBus.getInstance().toObservable(ChangeDevicenameEvent.class)
                .compose(TransformUtils.<ChangeDevicenameEvent>defaultSchedulers())
                .subscribe(new Action1<ChangeDevicenameEvent>() {
                    @Override
                    public void call(ChangeDevicenameEvent event) {
                        try {
                            for (int i = 0; i < deviceMenuList.size(); i++) {
                                if (deviceMenuList.get(i).getUid().equals(String.valueOf(event.getUid()))) {
                                    deviceMenuList.get(i).setMenuText(event.getName());
                                    break;
                                }
                            }
                            for (int i = 0; i < AppContext.getmOurDevices().size(); i++) {
                                if (AppContext.getmOurDevices().get(i).getUId() == Integer.valueOf(event.getUid())) {
                                    AppContext.getmOurDevices().get(i).setDeviceName(event.getName());
                                }
                            }
                            devidePop.notifydata();

                        } catch (Exception e) {
                        }

                    }
                });

        mCompositeSubscription.add(mSubscription04);
    }



    /**
     * //删除飞比设备更新界面
     */
    private void receiveDelDeviceInfoEvent() {
        Subscription mSubscription03 = RxBus.getInstance().toObservable(MyDeviceInfo.class)
                .compose(TransformUtils.<MyDeviceInfo>defaultSchedulers())
                .subscribe(new Action1<MyDeviceInfo>() {
                    @Override
                    public void call(MyDeviceInfo event) {
                        try {
                            for (int i = 0; i < deviceInfos.size(); i++) {
                                if (deviceInfos.get(i).getUId() == Integer.parseInt(event.getId())) {
                                    deviceInfos.remove(i);
                                }
                            }

                            if(null !=deviceMenuList){
                                for (int i = 0; i <deviceMenuList.size() ; i++) {
                                    if(deviceMenuList.get(i).getUid().equals(event.getId())){
                                        deviceMenuList.remove(i);
                                        fragmentList.remove(i);
                                        mainLinear.removeViewAt(i);
                                        break;
                                    }
                                }
                            }
                            devidePop.notifydata();
                            fadapter.notifyDataSetChanged();

                        } catch (Exception e) {
                        }

                    }
                });
        mCompositeSubscription.add(mSubscription03);
    }



    /**
     * 接收移康设备列表
     */
    private void receiveEquesListInfo() {
        Subscription mSubscription01 = RxBus.getInstance().toObservable(EquesListInfo.class)
                .compose(TransformUtils.<EquesListInfo>defaultSchedulers())
                .subscribe(new Action1<EquesListInfo>() {

                    private String localUsername;
                    private LoginResult.BodyBean.GatewayListBean lockUserName;

                    @Override
                    public void call(EquesListInfo event) {
                        bdylist = AppContext.getBdylist();
                        hideLoading();
                        if(bdylist.size()>0){
                            for (int i = 0; i <bdylist.size() ; i++) {
                                //校验是否已存在
                                boolean flag =  true;
                                for (int j = 0; j <deviceMenuList.size() ; j++) {
                                    if(bdylist.get(i).getBid().equals(deviceMenuList.get(j).getUid())){
                                        flag = false;
                                        break;
                                    }
                                }

                                if(flag){
                                    if(null !=   bdylist.get(i).getNick()){
                                        deviceMenuList.add(new PopwindowChoose.Menu(R.mipmap.add,
                                                bdylist.get(i).getNick(),bdylist.get(i).getBid()));
                                    }else{
                                        deviceMenuList.add(new PopwindowChoose.Menu(R.mipmap.add,
                                                bdylist.get(i).getName(),bdylist.get(i).getBid()));
                                    }
                                    EQfragment fragement=  new EQfragment();
                                    Bundle bundle =new Bundle();
                                    bundle.putSerializable("EQ",bdylist.get(i));
                                    fragement.setArguments(bundle);
                                    fragmentList.add(fragement);
                                    addindicator();
                                }

                            }
                            
                        }
                        if(deviceMenuList != null && deviceMenuList.size() >0)
                        tvDeviceTitle.setText(deviceMenuList.get(0).getMenuText());
                        devidePop.notifydata();
                        fadapter.notifyDataSetChanged();

//                        QueryDevicesListInfo body = new QueryDevicesListInfo();
//                        lockUserName = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
//                        if (lockUserName == null) {
//                            localUsername = PreferencesUtils.getString(LOCAL_USERNAME);
//                            if (AppUtil.isMobileNO(localUsername)) {
//                                body.setVendor_name("virtual");
//                                body.setUuid(localUsername);
//                            } else {
//                                body.setVendor_name(FactoryType.FBEE);
//                                body.setUuid(AppContext.getGwSnid());
//                            }
//                        } else {
//                            if (AppUtil.isMobileNO(lockUserName.getUsername())) {
//                                body.setVendor_name("virtual");
//                                body.setUuid(lockUserName.getUuid());
//                            } else {
//                                body.setVendor_name(FactoryType.FBEE);
//                                body.setUuid(AppContext.getGwSnid());
//                            }
//                        }

//                        presenter.reqGateWayInfo(body);
                    }
                });


        Subscription subdetele = RxBus.getInstance().toObservable(EquesDeviceDelete.class)
                .compose(TransformUtils.<EquesDeviceDelete>defaultSchedulers())
                .subscribe(new Action1<EquesDeviceDelete>() {
                    @Override
                    public void call(EquesDeviceDelete equesDeviceDelete) {
                        onRefresh();
                    }
                });

        mCompositeSubscription.add(mSubscription01);
        mCompositeSubscription.add(subdetele);
    }



    /**
     * 更新设备选择列表
     */
    private void initDeviceMenu() {
        if(null !=deviceMenuList && deviceMenuList.size()>0)
        tvDeviceTitle.setText(deviceMenuList.get(0).getMenuText());

        if(null == devidePop){
            devidePop = new PopwindowChoose(mContext, deviceMenuList, new BaseRecylerAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (null != devidePop && devidePop.isShowing()) {
                        devidePop.dismiss();
                    }
                    tvDeviceTitle.setText(deviceMenuList.get(position).getMenuText());
                    //选中设备
                    viewpager.setCurrentItem(position, false);
                }
            }, HomeSimpleFragment.this, true);
            devidePop.setAnimationStyle(R.style.popwin_anim_style);
        }else{
            devidePop.notifydata();
        }

        tvDeviceTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != devidePop && devidePop.isShowing()){
                    devidePop.dismiss();
                }else{
                    showDevicePop();
                }
            }

        });




    }

    private void showDevicePop() {
        if (devidePop != null && !devidePop.isShowing()) {
            View view = devidePop.mContentView;
            //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
            int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
            int[] location = new int[2];
            // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
            tvDeviceTitle.getLocationOnScreen(location);
            //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
            devidePop.showAtLocation(tvDeviceTitle, Gravity.NO_GRAVITY, (location[0] + tvDeviceTitle.getWidth() / 2) - popupWidth / 2, location[1] + tvDeviceTitle.getHeight());

        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //添加设备
            case R.id.iv_right_menu:
                showPopupWindow(parentView);
               break;
            //添加网关
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

        }
    }
    String[] PERMISSIONS_RECORD = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA2};
    private void verifyRecordPermissions(BaseActivity captureActivity) {
        //摄像头权限
        if (ContextCompat.checkSelfPermission(captureActivity,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //先判断有没有权限 ，没有就在这里进行权限的申请
            HomeSimpleFragment.this.requestPermissions(PERMISSIONS_RECORD, 2);
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

    /**
     * 网关列表点击
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        colsePop();
        currentGw= gatewaLyist.get(position);

        //切换成虚拟网关
        if (AppUtil.isMobileNO(currentGw.getUsername())) {
            //移康退出
            mContext.icvss.equesUserLogOut();
            mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, currentGw.getUsername(), EquesConfig.APPKEY);
            PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), currentGw);
            List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
            AppContext.clearAllDatas();
            AppContext.getInstance().getBodyBean().setGateway_list(gateway_list);
            AppContext.getInstance().getSerialInstance().releaseSource();

            deviceInfos.clear();
            deviceMenuList.clear();
            devidePop.changeData(deviceMenuList);
            if(fragmentList != null){
                fragmentList.clear();
                mNum =0;
                mainLinear.removeAllViews();
                fadapter.notifyDataSetChanged();
            }
            tvDeviceTitle.setText("设备");
            initDeviceMenu();

            AppContext.getInstance().setmHomebody(null);
            //通知页面更新数据
            RxBus.getInstance().post(new SwitchDataEvent());
            if (!currentGw.getNote().isEmpty()) {
                title.setText(currentGw.getNote());
            } else {
                title.setText(currentGw.getUsername());
            }
            RxBus.getInstance().post(currentGw);
            return;
        }

        if (AppUtil.isNetworkAvailable(mContext)) {
            showLoadingDialog();
            presenter.loginFbee(currentGw.getUsername(), currentGw.getPassword());
        } else {
            showToast("当前无网络连接");
        }


    }


    /**
     * 切换网关返回
     * @param result
     */
    @Override
    public void loginFbeeResult(int result) {
        switch (result){
            case 1:
                //移康退出
                mContext.icvss.equesUserLogOut();
                colsePop();

                //是否是新网关
                boolean flag = true;
                if(null != username){
                    List<LoginResult.BodyBean.GatewayListBean> mGateWaylist = AppContext.getInstance().getBodyBean().getGateway_list();
                    if (mGateWaylist != null && mGateWaylist.size() > 0) {
                        for (int i = 0; i < mGateWaylist.size(); i++) {
                            String user = mGateWaylist.get(i).getUsername();
                            if (username.equals(user)) {
                                currentGw = mGateWaylist.get(i);
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
                if(deviceInfos != null)
                    deviceInfos.clear();

                if(deviceMenuList != null){
                    deviceMenuList.clear();
                    tvDeviceTitle.setText("设备");
                }

                if(fragmentList !=null){
                    fragmentList.clear();
                    mNum = 0;
                    mainLinear.removeAllViews();
                    fadapter.notifyDataSetChanged();
                }
                initDeviceMenu();
                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppContext.getInstance().getSerialInstance().getDevices();
                    }
                });
                hideLoadingDialog();

                if (username != null && flag) {
                    showLoadingDialog("网关登录成功，添加中...");
                    AppContext.getInstance().getSerialInstance().getGateWayInfo();
                    mAddSub= Observable.timer(5000, TimeUnit.MILLISECONDS)
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

                }else{
                    if(null != currentGw){
                        mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, currentGw.getUsername(), EquesConfig.APPKEY);
                        Api.jpushSetting(getActivity(), currentGw.getUsername());
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), currentGw);
                        //更新首页配置
                        showToast("登录网关成功");
                        String alias = currentGw.getNote();
                        String username = currentGw.getUsername();
                        if (alias == null || alias.length() == 0) {
                            title.setText(username);
                            RxBus.getInstance().post(currentGw);
                        } else {
                            title.setText(alias);
                            RxBus.getInstance().post(currentGw);
                        }
                    }
                }

                break;

            case -2:
                hideLoadingDialog();
                showToast("网关账号或密码错误!");
                LoginResult.BodyBean.GatewayListBean bean02 = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                if (AppUtil.isMobileNO(bean02.getUsername())) {
                    return;
                }
                presenter.loginFbee(bean02.getUsername(), bean02.getPassword());
                currentGw = bean02;
                break;

            case -3:
                hideLoadingDialog();
                showToast("网关登录超时！");
                LoginResult.BodyBean.GatewayListBean bean03 = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                if (AppUtil.isMobileNO(bean03.getUsername())) {
                    return;
                }
                presenter.loginFbee(bean03.getUsername(), bean03.getPassword());
                currentGw = bean03;
                break;
            case -4:
                hideLoadingDialog();
                showToast("网关登录人数已达到上限!");
                LoginResult.BodyBean.GatewayListBean bean = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                if (AppUtil.isMobileNO(bean.getUsername())) {
                    return;
                }
                presenter.loginFbee(bean.getUsername(), bean.getPassword());
                currentGw = bean;
                break;

        }


    }

    /**
     * 服务器添加网关返回
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
            if (null != username && AppUtil.isMobileNO(username)) {
                //移康退出
//                mContext.icvss.equesUserLogOut();
//                mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, adduser, EquesConfig.APPKEY);
                LoginResult.BodyBean.GatewayListBean bean = null;
                List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                boolean flag = false;
                for (int i = 0; i < gateway_list.size(); i++) {
                    if (gateway_list.get(i).getUsername().equals(username)) {
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
                    bean.setUsername(username);
                    bean.setNote(username);
                    bean.setPassword("123456");
                    bean.setAuthorization("admin");
                    bean.setVendor_name("virtual");
                    bean.setUuid(username);
                    bean.setVersion("1.0.0");
                    gateway_list.add(bean);
                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), bean);
                    popwindow.changeData(getMenuList());
                    title.setText(username);
                }

                AppContext.clearAllDatas();
                LoginResult.BodyBean mbodyBean = new LoginResult.BodyBean();
                mbodyBean.setGateway_list(gateway_list);
                AppContext.getInstance().setBodyBean(mbodyBean);
                AppContext.getInstance().getSerialInstance().releaseSource();

                deviceInfos.clear();
                deviceMenuList.clear();
                devidePop.changeData(deviceMenuList);
                if(fragmentList != null){
                    fragmentList.clear();
                    mNum = 0;
                    mainLinear.removeAllViews();
                    fadapter.notifyDataSetChanged();
                }
                tvDeviceTitle.setText("设备");
                initDeviceMenu();

                AppContext.getInstance().setmHomebody(null);
                //通知页面更新数据
                RxBus.getInstance().post(new SwitchDataEvent());
                RxBus.getInstance().post(bean);
                return;

            } else {
                LoginResult.BodyBean.GatewayListBean gwadd = new LoginResult.BodyBean.GatewayListBean();
                //存储当前网关
                gwadd.setUsername(username);
                gwadd.setPassword(AES256Encryption.encrypt(passwd, AppContext.getGwSnid()));
                gwadd.setAuthorization("admin");
                gwadd.setVendor_name(FactoryType.FBEE);
                gwadd.setUuid(AppContext.getGwSnid());
                gwadd.setVersion(AppContext.getVer());
                gwadd.setNote(username);
                PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gwadd);
                showToast("网关添加成功");
                //添加到缓存网关列表
                boolean isexist = true;
                if (null != AppContext.getInstance().getBodyBean()) {
                    List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
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
                String alias = gwadd.getNote();
                username = gwadd.getUsername();
                if (TextUtils.isEmpty(alias)) {
                    title.setText(username);
                    RxBus.getInstance().post(gwadd);
                } else {
                    title.setText(alias);
                    RxBus.getInstance().post(gwadd);
                }
                username = null;
            }
        } else {
            showToast("网关添加到服务器失败");
            LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
            if (null != gw)
                presenter.loginFbee(gw.getUsername(), gw.getPassword());
        }
    }


    private ArrayList<PopwindowChoose.Menu> getMenuList() {

        ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();
        LoginResult.BodyBean bodyBean = AppContext.getInstance().getBodyBean();
        List<LoginResult.BodyBean.GatewayListBean> gateway_list = null;
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
     * 接收网关信息返回
     */
    private void addGateWay() {
        Subscription gateWaySubscription = RxBus.getInstance().toObservable(GateSnidEvent.class)
                .compose(TransformUtils.<GateSnidEvent>defaultSchedulers())
                .subscribe(new Action1<GateSnidEvent>() {
                    @Override
                    public void call(GateSnidEvent event) {
                        if (null == username) {
                            return;
                        }
                        Api.jpushSetting(getActivity(), username);
                        mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, username, EquesConfig.APPKEY);
                        boolean isAdd = true;
                        List<LoginResult.BodyBean.GatewayListBean> mGateWaylist = AppContext.getInstance().getBodyBean().getGateway_list();
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
                            bodyBean.setUsername(username);
                            try {
                                bodyBean.setPassword(AES256Encryption.encrypt(passwd, AppContext.getGwSnid()));
                            } catch (Exception e) {
                            }
                            bodyBean.setAuthorization("admin");
                            bodyBean.setNote(username);
                            bodyBean.setVersion(AppContext.getVer());
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



    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void onRefresh() {
        //通知页面更新数据
        try {
            if (null != deviceInfos)
                deviceInfos.clear();
            if (bdylist != null)
                bdylist.clear();

            deviceInfos.clear();
            deviceMenuList.clear();
            devidePop.changeData(deviceMenuList);
            if(fragmentList != null){
                fragmentList.clear();
                mNum = 0;
                mainLinear.removeAllViews();
                fadapter.notifyDataSetChanged();
            }
            tvDeviceTitle.setText("设备");

            AppContext.getInstance().getSerialInstance().getDevices();
            if(mContext.icvss.equesIsLogin()){
                mContext.icvss.equesGetDeviceList();
            }else{
                LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
            }
        } catch (Exception e) {

        }

        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefreshComplete();
            }
        }, 3000);
    }


    private void onRefreshComplete() {
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }


    class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<BaseFragment> mList;
        private Context mContext;

        public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
            mList = new ArrayList<BaseFragment>();
        }

        public void setLists(ArrayList<BaseFragment> lists) {
            this.mList = lists;
        }


        @Override
        public BaseFragment getItem(int arg0) {
            return mList.get(arg0);
        }

        @Override
        public int getCount() {

            return mList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return PagerAdapter.POSITION_NONE;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 88) {
            adduser = null;
            String result = data.getExtras().getString("result");
            try {
                username = result.split("GT")[1].split("pass")[0];
            } catch (Exception e) {
                showToast("扫描二维码解析出错");
            }

            if (null != username) {
                while (username.startsWith("0")) {
                    username = username.substring(1);
                }
            } else {
                showToast("扫描失败");
            }

            //虚拟
            if (null != username &&AppUtil.isMobileNO(username)) {
                //移康退出
                mContext.icvss.equesUserLogOut();
                mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, username, EquesConfig.APPKEY);
                List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                boolean flag = false;
                String note = username;
                for (int i = 0; i < gateway_list.size(); i++) {
                    if (gateway_list.get(i).getUsername().equals(username)) {
                        flag = true;
                        note = gateway_list.get(i).getNote();
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gateway_list.get(i));
                    }
                }

                if (!flag) {
                    //添加到服务器
                    AddGateWayReq bodyBean = new AddGateWayReq();
                    bodyBean.setVendor_name("virtual");
                    bodyBean.setUuid(username);
                    bodyBean.setUsername(username);
                    bodyBean.setPassword("123456");
                    bodyBean.setAuthorization("admin");
                    bodyBean.setNote(username);
                    bodyBean.setVersion("1.0.0");

                    AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();
                    String mcountryName = AppContext.getMcountryName();
                    location.setCountries(mcountryName);
                    location.setProvince(AppContext.getMadminArea());
                    location.setCity(AppContext.getMlocality());
                    location.setPartition(AppContext.getMsubLocality());
                    location.setStreet(AppContext.getMfeatureName());
                    bodyBean.setLocation(location);
                    presenter.addGateway(bodyBean);
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

            try {
                passwd = result.split("GT")[1].split("pass")[1];
            } catch (Exception e) {
                showToast("扫描二维码解析出错");
            }

            if (username != null && passwd.length() > 0) {
                if (AppUtil.isNetworkAvailable(mContext)) {
                    Api.jpushSetting(mContext, username);
                    presenter.loginFbee(username, passwd);
                    showLoadingDialog("登录中...");
                } else {
                    showToast("当前无网络连接");
                }
            } else {
                showToast("扫描失败");
            }

        }else if(resultCode == 66){
            adduser = null;
            username = data.getExtras().getString("username");
            passwd = data.getExtras().getString("password");
            if (!username.isEmpty() && !passwd.isEmpty()) {
                presenter.loginFbee(username, passwd);
                showLoadingDialog("正在登录...");
            }
        }


    }

    //添加设备弹出对话框
    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popwindow_equipfragment, null);
        // 设置按钮的点击事件
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyler_popwindow);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<String> datas = new ArrayList<>();
        datas.add("其他设备(允许入网)");
        datas.add("猫眼设备");
        PopWindowAdapter adapter = new PopWindowAdapter(getActivity(), datas);
        adapter.setOnItemClickLitener(new BaseRecylerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    //允许设备入网
                    //用于添加设备
                    popupWindow.dismiss();
                    initTimer();
                } else if (position == 1) {
                    popupWindow.dismiss();
                    skipAct(AddDeviceActivity.class);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(1f);
            }
        });
        darkenBackground(0.4f);
    }





    /**
     * 改变背景颜色
     */
    private void darkenBackground(Float bgcolor){
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgcolor;
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mContext.getWindow().setAttributes(lp);

    }


    private void initTimer() {
        if (!AppUtil.isNetworkAvailable(mContext)) {
            showToast("当前无网络，请连接网络！");
            return;
        } else {
            showLoadingDialog("允许设备入网中...\n请在设备上操作入网");
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            timer = new Timer();
            timerTask = new TimerTask() {
                int time = 0;

                @Override
                public void run() {
                    time++;
                    if (time < 5) {
                        AppContext.getInstance().getSerialInstance().permitJoin();
                    }
                    if (time == 60) {
                        hideLoadingDialog();
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (timerTask != null) {
                            timerTask.cancel();
                            timerTask = null;
                        }
                    }
                }
            };
            timer.schedule(timerTask, 0, 1000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 88) {
            adduser = null;
            String result = data.getExtras().getString("result");
            try {
                username = result.split("GT")[1].split("pass")[0];
            } catch (Exception e) {
                showToast("扫描二维码解析出错");
            }

            if (null != username) {
                while (username.startsWith("0")) {
                    username = username.substring(1);
                }
            } else {
                showToast("扫描失败");
            }

            //虚拟
            if (null != username &&AppUtil.isMobileNO(username)) {
                //移康退出
                mContext.icvss.equesUserLogOut();
                mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, username, EquesConfig.APPKEY);
                List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                boolean flag = false;
                String note = username;
                for (int i = 0; i < gateway_list.size(); i++) {
                    if (gateway_list.get(i).getUsername().equals(username)) {
                        flag = true;
                        note = gateway_list.get(i).getNote();
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gateway_list.get(i));
                    }
                }

                if (!flag) {
                    //添加到服务器
                    AddGateWayReq bodyBean = new AddGateWayReq();
                    bodyBean.setVendor_name("virtual");
                    bodyBean.setUuid(username);
                    bodyBean.setUsername(username);
                    bodyBean.setPassword("123456");
                    bodyBean.setAuthorization("admin");
                    bodyBean.setNote(username);
                    bodyBean.setVersion("1.0.0");

                    AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();
                    String mcountryName = AppContext.getMcountryName();
                    location.setCountries(mcountryName);
                    location.setProvince(AppContext.getMadminArea());
                    location.setCity(AppContext.getMlocality());
                    location.setPartition(AppContext.getMsubLocality());
                    location.setStreet(AppContext.getMfeatureName());
                    bodyBean.setLocation(location);
                    presenter.addGateway(bodyBean);
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

            try {
                passwd = result.split("GT")[1].split("pass")[1];
            } catch (Exception e) {
                showToast("扫描二维码解析出错");
            }

            if (username != null && passwd.length() > 0) {
                if (AppUtil.isNetworkAvailable(mContext)) {
                    Api.jpushSetting(mContext, username);
                    presenter.loginFbee(username, passwd);
                    showLoadingDialog("登录中...");
                } else {
                    showToast("当前无网络连接");
                }
            } else {
                showToast("扫描失败");
            }

        }else if(resultCode == 66){
            adduser = null;
            username = data.getExtras().getString("username");
            passwd = data.getExtras().getString("password");
            if (!username.isEmpty() && !passwd.isEmpty()) {
                presenter.loginFbee(username, passwd);
                showLoadingDialog("正在登录...");
            }
        }


    }


}
