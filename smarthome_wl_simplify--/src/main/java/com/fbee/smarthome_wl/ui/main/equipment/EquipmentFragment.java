package com.fbee.smarthome_wl.ui.main.equipment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.DeviceAdapter;
import com.fbee.smarthome_wl.adapter.equipfragmentpopwindow.PopWindowAdapter;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.AddVideoLockInfo;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.HintCountInfo;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;
import com.fbee.smarthome_wl.bean.UpdateDoorLockName;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.ChangeDevicenameEvent;
import com.fbee.smarthome_wl.event.SwitchDataEvent;
import com.fbee.smarthome_wl.event.UpdateEquesNameEvent;
import com.fbee.smarthome_wl.event.VideolockDelEvent;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.request.QueryGateWayReq;
import com.fbee.smarthome_wl.request.videolockreq.DeviceInfoUpdateRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserAuthRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserDeviceStatusQueryRequest;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QueryDeviceListResponse;
import com.fbee.smarthome_wl.response.QueryGateWayInfoResponse;
import com.fbee.smarthome_wl.response.videolockres.MnsBaseResponse;
import com.fbee.smarthome_wl.response.videolockres.UserDeviceStatusQueryResponse;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverContract;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverPresenter;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockActivity;
import com.fbee.smarthome_wl.ui.equesdevice.EquesDeviceInfoActivity;
import com.fbee.smarthome_wl.ui.equesdevice.adddevices.AddDeviceActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.DoorLockVideoInfoActivity;
import com.fbee.smarthome_wl.ui.videodoorlock.VideoLockDMSFirstActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogColor;
import com.fbee.smarthome_wl.widget.dialog.DialogCurtain;
import com.fbee.smarthome_wl.widget.dialog.DialogSwitch;
import com.fbee.smarthome_wl.widget.dialog.DialogTemperature;
import com.fbee.zllctl.DeviceInfo;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.equipment
 * @anthor create by Zhaoli.Wang
 * @time 2017/2/13 10:32
 */
public class EquipmentFragment extends BaseFragment<EquipmentContract.Presenter> implements EquipmentContract.View, View.OnClickListener {

    private GridView gvDevices;
    private SwipeRefreshLayout swiperefreshlayoutDevices;
    private List<EquesListInfo.bdylistEntity> bdylist;
    private List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> device_list;
    private List<DeviceInfo> deviceInfos;
    private DeviceAdapter devicesAdapter;
    private TextView title;
    private String itemDviceInfo = "itemDviceInfo";
    //存储门锁的uid和name
    private Map<Integer, String> doorLockMaps;
    private String doorLockmaps = "doorLockMaps";
    private int requestCode = 500;
    private int resultCode = 600;
    private View parentView;
    private Timer timer;
    private TimerTask timerTask;
    private ImageView back;
    private PopupWindow popupWindow;
    private Subscription mSubscriptionState;
    private boolean deviceState = true;
    private int state;
    private int onRet = 0;
    private int offRet = 0;
    private ImageView ivRightMenu;
    private LinearLayout linerAdd;
    private ImageView imageAdd;
    private Socket socket;
    private boolean devicesStatusTag = false;
    private boolean isNeedConfirm = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mContext.icvss.equesGetDeviceList();
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_equipment;
    }


    @Override
    public void initView() {
        swiperefreshlayoutDevices = (SwipeRefreshLayout) mContentView.findViewById(R.id.swipeToLoadLayout);
        title = (TextView) mContentView.findViewById(R.id.title);
        back = (ImageView) mContentView.findViewById(R.id.back);
        ivRightMenu = (ImageView) mContentView.findViewById(R.id.iv_right_menu);
        gvDevices = (GridView) mContentView.findViewById(R.id.gd_devices);
        linerAdd = (LinearLayout) mContentView.findViewById(R.id.liner_add);
        mCompositeSubscription = mContext.getCompositeSubscription();

        socket = BaseApplication.getInstance().getSocket();
        socket.on("MSG_USER_DEVICE_STATUS_QUERY_RSP", checkDeviceState);
        socket.on("MSG_USER_AUTH_RSP", userConfirmation);

    }

    @Override
    public void bindEvent() {
        initApi();
        createPresenter(new EquipmentPresenter(this));
        back.setVisibility(View.VISIBLE);
        back.setImageResource(R.mipmap.home_menu);
        back.setOnClickListener(this);
        title.setText("设备");

        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        ivRightMenu.setOnClickListener(this);
        parentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_equipment, null);
        doorLockMaps = new HashMap<>();
        bdylist = AppContext.getBdylist();
        showLoadingDialog(null);
        if (bdylist != null && bdylist.size() > 0) {
            //linerAdd.setVisibility(View.GONE);
            if (linerAdd.getVisibility() == View.VISIBLE) {
                linerAdd.setVisibility(View.GONE);
            }
            hideLoadingDialog();
        }
        deviceInfos = new ArrayList<DeviceInfo>();
        //视频锁
        device_list = new ArrayList<QueryGateWayInfoResponse.BodyBean.DeviceListBean>();
        devicesAdapter = new DeviceAdapter(mContext, bdylist, deviceInfos, device_list);
        gvDevices.setAdapter(devicesAdapter);

        //接收小红点改变
        receiveHintChange();

        //三秒后做默认操作
        secondsLaterDoThings();

        //接收飞比设备
        receiveDevicesList();

        //条目点击
        onItemClick();

        //接收移康设备列表
        receiveEquesListInfo();

        //接受wifi设备 视频锁
        receiveWifiDecvices();

        //切换网关，更新界面数据
        receiveSwitchDataEvent();

        //删除飞比设备更新界面
        receiveDelDeviceInfoEvent();

        //接收移康设备该名字
        receiveEquessDeviceNameChange();

        // 接收视频锁添加
        receiveVideoLockAdd();

        //接收视频锁状态改变
        receiveVideoLockStateChange();

        //飞比设备改名字
        receiveChangeDevicenameEvent();

        //刷新操作
        onRefresh();

        //删除视频锁设备更新界面
        delVideolock();
        RxBus.getInstance().toObservable(UpdateDoorLockName.class).compose(TransformUtils.<UpdateDoorLockName>defaultSchedulers()).subscribe(new Action1<UpdateDoorLockName>() {
            @Override
            public void call(UpdateDoorLockName upDataGwName) {
                if (upDataGwName.getUuid() != null && upDataGwName.getUpdateName() != null && device_list.size() > 0) {
                    for (int i = 0; i < device_list.size(); i++) {
                        if (upDataGwName.getUuid().equals(device_list.get(i).getUuid())) {
                            device_list.get(i).setNote(upDataGwName.getUpdateName());
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取服务器wifi设备列表
     */
    private void receiveWifiDecvices() {
        LoginResult.BodyBean.GatewayListBean localgateway = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        if (null != localgateway) {
            QueryGateWayReq body = new QueryGateWayReq();
            if (AppUtil.isMobileNO(localgateway.getUsername())) {
                body.setVendor_name("virtual");
                body.setUuid(localgateway.getUuid());
            } else {
                body.setVendor_name(FactoryType.FBEE);
                if (null != AppContext.getGwSnid()) {
                    body.setUuid(AppContext.getGwSnid());
                } else {
                    body.setUuid(localgateway.getUuid());
                }

            }
            presenter.getGateWayInfo(body, FactoryType.GENERAL);
        }
    }

    private void receiveVideoLockStateChange() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                LogUtil.e("EquipmentFragment", throwable.getMessage() + "");
            }
        };
        Subscription state = RxBus.getInstance().toObservable(DeviceInfoUpdateRequest.class)
                .compose(TransformUtils.<DeviceInfoUpdateRequest>defaultSchedulers())
                .subscribe(new Action1<DeviceInfoUpdateRequest>() {
                    @Override
                    public void call(DeviceInfoUpdateRequest event) {
                        if (event == null) return;
                        if (device_list != null && device_list.size() > 0) {
                            for (int i = 0; i < device_list.size(); i++) {
                                if (event.getUuid() != null && event.getUuid().equals(device_list.get(i).getUuid()) && event.getData() != null
                                        && event.getData().getStatus() != null && !event.getData().getStatus().equals(device_list.get(i).getState())) {
                                    device_list.get(i).setState(event.getData().getStatus());
                                    devicesAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }

                    }
                }, onErrorAction);
        mCompositeSubscription.add(state);
    }

    /**
     * 视频锁删除后界面刷新
     */
    private void delVideolock() {
        Subscription deteleSub = RxBus.getInstance().toObservable(VideolockDelEvent.class)
                .compose(TransformUtils.<VideolockDelEvent>defaultSchedulers())
                .subscribe(new Action1<VideolockDelEvent>() {
                    @Override
                    public void call(VideolockDelEvent event) {
                        if (device_list != null) {
                            for (int i = 0; i < device_list.size(); i++) {
                                if (device_list.get(i).getUuid().equals(event.getUuid())) {
                                    device_list.remove(i);
                                    break;
                                }
                            }
                            if (device_list.size() == 0) {
                                if (linerAdd.getVisibility() == View.GONE) {
                                    linerAdd.setVisibility(View.VISIBLE);
                                }
                            }
                            devicesAdapter.notifyDataSetChanged();
                        }

                    }

                });
        mCompositeSubscription.add(deteleSub);
    }

    private Emitter.Listener checkDeviceState = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (devicesStatusTag) {
                        devicesStatusTag = false;

                        JSONObject data = (JSONObject) args[0];
                        UserDeviceStatusQueryResponse response = new Gson().fromJson(data.toString(), UserDeviceStatusQueryResponse.class);
                        if (response != null && response.getReturn_string() != null) {
                            Toast.makeText(getContext(), "用户设备状态返回" + response.getReturn_string(), Toast.LENGTH_SHORT).show();
                            if (response.getReturn_string().contains("SUCCESS")) {
                                List<UserDeviceStatusQueryResponse.StatusListBean> statusList = response.getStatus_list();
                                if (statusList != null) {
                                    for (int i = 0; i < statusList.size(); i++) {
                                        String status = statusList.get(i).getStatus();
                                        for (int j = 0; j < device_list.size(); j++) {
                                            if (device_list.get(j).getUuid().equals(statusList.get(i).getUuid())) {
                                                device_list.get(j).setState(status);
                                            }
                                        }
                                    }
                                    devicesAdapter.notifyDataSetChanged();
                                }
                            }
                            //需要验证
                            else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                                LogUtil.e("服务器发送", "equip设备状态需要验证");
                                userConfirmationReq();
                            }
                            //token失效
                            else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING")
                                    || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                                reqAddToken();
                            }
                        }
                    }

                }
            });
        }
    };

    /**
     * 请求token
     */
    private void reqAddToken() {
        AddTokenReq addTokenReq = new AddTokenReq();
        addTokenReq.setAttitude("read");
        addTokenReq.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        addTokenReq.setSecret_key(AppContext.getInstance().getBodyBean().getSecret_key());
        presenter.reqAddToken(addTokenReq);
    }

    /**
     * 用户验证
     */
    private void userConfirmationReq() {
        isNeedConfirm = false;
        UserAuthRequest request = new UserAuthRequest();
        request.setUsername(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        request.setApi_version("1.0");
        request.setToken(AppContext.getToken());
        JSONObject jsonObject = null;
        try {
            String req = new Gson().toJson(request);
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_AUTH_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "equip用户验证发送成功");
                    isNeedConfirm = true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Emitter.Listener userConfirmation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isNeedConfirm) {
                        isNeedConfirm = false;
                        JSONObject data = (JSONObject) args[0];
                        MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                        Log.e("服务器发送", "用户验证返回:" + response.getReturn_string());
                        if (response.getReturn_string().contains("SUCCESS")) {
                            getDeviceStaus();
                        }
                    }

                }
            });
        }
    };
    private boolean videoLockDeviceStateTag;
    private Subscription subscription;

    /**
     * 查询wifi设备状态
     */
    private void getDeviceStaus() {

        if (videoLockDeviceStateTag) {
            devicesStatusTag = false;
            //查询获取到的wifi设备的状态
            UserDeviceStatusQueryRequest bean = new UserDeviceStatusQueryRequest();
            bean.setToken(AppContext.getToken());
            bean.setApi_version("1.0");
            List<UserDeviceStatusQueryRequest.DeviceListBean> deviceListBean = new ArrayList<>();

            for (int i = 0; i < device_list.size(); i++) {
                UserDeviceStatusQueryRequest.DeviceListBean listBean = new UserDeviceStatusQueryRequest.DeviceListBean();
                listBean.setUuid(device_list.get(i).getUuid());
                listBean.setVendor_name(FactoryType.GENERAL);
                deviceListBean.add(listBean);
            }
            bean.setDevice_list(deviceListBean);
            String req = new Gson().toJson(bean);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(req);
                socket.emit("MSG_USER_DEVICE_STATUS_QUERY_REQ", jsonObject, new Ack() {
                    @Override
                    public void call(Object... objects) {
                        devicesStatusTag = true;
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            subscription = Observable.timer(1500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            devicesStatusTag = false;
                            //查询获取到的wifi设备的状态
                            UserDeviceStatusQueryRequest bean = new UserDeviceStatusQueryRequest();
                            bean.setToken(AppContext.getToken());
                            bean.setApi_version("1.0");
                            List<UserDeviceStatusQueryRequest.DeviceListBean> deviceListBean = new ArrayList<>();

                            for (int i = 0; i < device_list.size(); i++) {
                                UserDeviceStatusQueryRequest.DeviceListBean listBean = new UserDeviceStatusQueryRequest.DeviceListBean();
                                listBean.setUuid(device_list.get(i).getUuid());
                                listBean.setVendor_name(FactoryType.GENERAL);
                                deviceListBean.add(listBean);
                            }
                            bean.setDevice_list(deviceListBean);
                            String req = new Gson().toJson(bean);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(req);
                                socket.emit("MSG_USER_DEVICE_STATUS_QUERY_REQ", jsonObject, new Ack() {
                                    @Override
                                    public void call(Object... objects) {
                                        devicesStatusTag = true;
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

        }

    }

    /**
     * 刷新操作
     */
    private void onRefresh() {
        swiperefreshlayoutDevices.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //通知页面更新数据
                try {
                    if (null != deviceInfos)
                        deviceInfos.clear();
                    if (bdylist != null)
                        bdylist.clear();
                    if (device_list != null)
                        device_list.clear();
                    devicesAdapter.notifyDataSetChanged();
                    LogUtil.e("设备刷新", "============");
                    AppContext.getInstance().getSerialInstance().getDevices();
                    videoLockDeviceStateTag = true;
                    if (mContext.icvss.equesIsLogin()) {
                        mContext.icvss.equesGetDeviceList();
                    } else {
                        LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                        mContext.icvss.equesLogin(mContext, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
                    }
                } catch (Exception e) {

                }
                swiperefreshlayoutDevices.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swiperefreshlayoutDevices.isRefreshing()) {
                            swiperefreshlayoutDevices.setRefreshing(false);
                        }
                    }
                }, 3000);

            }
        });
    }

    /**
     * 条目点击
     */
    private void onItemClick() {
        gvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private String bid;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                {
                    /*if (position == bdylist.size() + deviceInfos.size()) {
                        showPopupWindow(parentView);
                        //skipAct(AddDeviceActivity.class);
                    } else */
                    if (position < deviceInfos.size()) {
                        final DeviceInfo info = deviceInfos.get(position);
                        /*int status = info.getDeviceStatus();
                        if (status <= 0) {
                            ToastUtils.showShort("设备不在线!");
                        }*/
                        //判断是否是门锁
                        if (info.getDeviceId() == DeviceList.DEVICE_ID_DOOR_LOCK) {
                            Intent intent = new Intent(getActivity(), DoorLockActivity.class);
                            intent.putExtra(itemDviceInfo, info);
                            startActivityForResult(intent, requestCode);
                            //skipAct(DoorLockActivity.class,bundle);
                        }
                        //判断是否是插座
                        if (info.getDeviceId() == DeviceList.DEVICE_ID_SOCKET) {
                            DialogSwitch dialogSwitch = new DialogSwitch(getActivity(), info);
                            dialogSwitch.show();
                        }
                        //色温灯
                        else if (info.getDeviceId() == DeviceList.DEVICE_ID_COLOR_TEMP1 ||
                                info.getDeviceId() == DeviceList.DEVICE_ID_COLOR_TEMP2
                                ) {
                            DialogTemperature dialog = new DialogTemperature(mContext, deviceInfos.get(position));
                            dialog.show();

                        }
                        //彩灯
                        else if (info.getDeviceId() == DeviceList.DEVICE_ID_COLOR_PHILIPS) {
                            DialogColor dialog = new DialogColor(mContext, deviceInfos.get(position));
                            dialog.show();
                        }
                        //窗帘
                        else if (info.getDeviceId() == DeviceList.DEVICE_ID_CURTAIN) {
                            new DialogCurtain(mContext, info).show();
                        }
                        //智能开关
                        else if (info.getDeviceId() == DeviceList.DEVICE_ID_SWITCH) {
                            DialogSwitch dialogSwitch = new DialogSwitch(getActivity(), info);
                            dialogSwitch.show();
                        }

                    } else if (position < deviceInfos.size() + bdylist.size()) {
                        EquesListInfo.bdylistEntity bdylistEntity = bdylist.get(position - deviceInfos.size());
                        Bundle bundle = new Bundle();
                        bundle.putString(Method.ATTR_BUDDY_NICK, bdylistEntity.getNick());
                        bid = bdylistEntity.getBid();
                        bundle.putString(Method.ATTR_BUDDY_BID, bid);
                        if (AppContext.getOnlines().size() > 0) {
                            for (int i = 0; i < AppContext.getOnlines().size(); i++) {
                                String bid = AppContext.getOnlines().get(i).getBid();
                                if (bdylistEntity.getBid().equals(bid)) {
                                    bundle.putString(Method.ATTR_BUDDY_UID, AppContext.getOnlines().get(i).getUid());
                                    bundle.putInt(Method.ATTR_BUDDY_STATUS, AppContext.getOnlines().get(i).getStatus());
                                }
                            }
                        }
                        bundle.putString(Method.ATTR_BUDDY_NAME, bdylistEntity.getName());

                        skipAct(EquesDeviceInfoActivity.class, bundle);

                    }
                    //视频锁
                    else {
                        QueryGateWayInfoResponse.BodyBean.DeviceListBean deviceListBean = device_list.get(position - deviceInfos.size() - bdylist.size());
                        Bundle bund = new Bundle();
                        String note = deviceListBean.getNote();
                        if (TextUtils.isEmpty(note) || "".equals(note)) {
                            bund.putString("note", "视频锁");
                        } else {
                            bund.putString("note", deviceListBean.getNote());
                        }
                        bund.putString("deviceUuid", deviceListBean.getUuid());
                        skipAct(DoorLockVideoInfoActivity.class, bund);
                    }
                }
            }
        });
    }

    /**
     * 接收视频锁添加
     */
    private void receiveVideoLockAdd() {
        Subscription mSubscriptionAdd = RxBus.getInstance().toObservable(AddVideoLockInfo.class)
                .compose(TransformUtils.<AddVideoLockInfo>defaultSchedulers())
                .subscribe(new Action1<AddVideoLockInfo>() {
                    @Override
                    public void call(AddVideoLockInfo event) {
                        try {
                            for (int i = 0; i < device_list.size(); i++) {
                                if (device_list.get(i).getUuid().equals(event.getId())) {
                                    return;
                                }
                            }
                            if (linerAdd.getVisibility() == View.VISIBLE) {
                                linerAdd.setVisibility(View.GONE);
                            }
                            QueryGateWayInfoResponse.BodyBean.DeviceListBean bean = new QueryGateWayInfoResponse.BodyBean.DeviceListBean();
                            bean.setNote(event.getName());
                            bean.setUuid(event.getId());
                            bean.setType(event.getDeviceType());
                            bean.setState(event.getStatus());
                            device_list.add(bean);
                            devicesAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                        }

                    }
                });
        mCompositeSubscription.add(mSubscriptionAdd);
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
                            for (int i = 0; i < deviceInfos.size(); i++) {
                                if (deviceInfos.get(i).getUId() == event.getUid()) {
                                    deviceInfos.get(i).setDeviceName(event.getName());
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
                            /*for (int i = 0; i < AppContext.getmOurDevices().size(); i++) {
                                if (AppContext.getmOurDevices().get(i).getUId() == Integer.parseInt(event.getId())) {
                                    AppContext.getmOurDevices().remove(i);
                                }
                            }*/
                            devicesAdapter.notifyDataSetChanged();
                            if (deviceInfos != null && deviceInfos.size() == 0 && bdylist != null && bdylist.size() == 0) {
                                //linerAdd.setVisibility(View.VISIBLE);
                                if (linerAdd.getVisibility() == View.GONE) {
                                    linerAdd.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (Exception e) {
                        }

                    }
                });
        mCompositeSubscription.add(mSubscription03);
    }

    /**
     * //切换网关，更新界面数据
     */
    private void receiveSwitchDataEvent() {

        Subscription mSubscription02 = RxBus.getInstance().toObservable(SwitchDataEvent.class)
                .compose(TransformUtils.<SwitchDataEvent>defaultSchedulers())
                .subscribe(new Action1<SwitchDataEvent>() {
                    @Override
                    public void call(SwitchDataEvent event) {
                        try {
                            if (null != deviceInfos)
                                deviceInfos.clear();
                            if (bdylist != null)
                                bdylist.clear();
                            if (device_list != null)
                                device_list.clear();
                            devicesAdapter.notifyDataSetChanged();
                            if (linerAdd.getVisibility() == View.GONE) {
                                linerAdd.setVisibility(View.VISIBLE);
                            }
                            /*if (linerAdd.getVisibility() == View.GONE) {
                                linerAdd.setVisibility(View.VISIBLE);
                            }*/
                            //                          AppContext.getInstance().getSerialInstance().getDevices();
                            //                            mContext.icvss.equesGetDeviceList();

                        } catch (Exception e) {
                        }

                    }
                });
        mCompositeSubscription.add(mSubscription02);
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
                        if (deviceInfos != null && deviceInfos.size() == 0 && bdylist != null && bdylist.size() == 0) {
                            //linerAdd.setVisibility(View.VISIBLE);
                            if (linerAdd.getVisibility() == View.GONE) {
                                linerAdd.setVisibility(View.VISIBLE);
                            }
                        } else {
                            //linerAdd.setVisibility(View.GONE);
                            if (linerAdd.getVisibility() == View.VISIBLE) {
                                linerAdd.setVisibility(View.GONE);
                            }
                        }
                        hideLoading();
                        devicesAdapter.notifyDataSetChanged();
                        QueryGateWayReq body = new QueryGateWayReq();
                        lockUserName = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                        if (lockUserName == null) {
                            localUsername = PreferencesUtils.getString(LOCAL_USERNAME);
                            if (AppUtil.isMobileNO(localUsername)) {
                                body.setVendor_name("virtual");
                                body.setUuid(localUsername);
                            } else {
                                body.setVendor_name(FactoryType.FBEE);
                                body.setUuid(AppContext.getGwSnid());
                            }
                        } else {
                            if (AppUtil.isMobileNO(lockUserName.getUsername())) {
                                body.setVendor_name("virtual");
                                body.setUuid(lockUserName.getUuid());
                            } else {
                                body.setVendor_name(FactoryType.FBEE);
                                body.setUuid(AppContext.getGwSnid());
                            }
                        }

                        presenter.getGateWayInfo(body, FactoryType.EQUES);
                    }
                });
        mCompositeSubscription.add(mSubscription01);
    }

    //三秒后做默认操作
    private void secondsLaterDoThings() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                LogUtil.e("deviceinfoError", throwable.getMessage() + "");
            }
        };
        Subscription subscription1 = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        if (AppContext.getmOurDevices() != null && AppContext.getmOurDevices().size() > 0) {
                            if (linerAdd.getVisibility() == View.VISIBLE) {
                                linerAdd.setVisibility(View.GONE);
                            }
                            deviceInfos.clear();
                            for (int i = 0; i < AppContext.getmOurDevices().size(); i++) {
                                //门磁设备不显示
                                if (DeviceList.DEVICE_ID_SENSOR != AppContext.getmOurDevices().get(i).getDeviceId()
                                        && DeviceList.DEVICE_ID_SWITCH_SCENE != AppContext.getmOurDevices().get(i).getDeviceId()
                                        && DeviceList.DEVICE_ID_THTB2 != AppContext.getmOurDevices().get(i).getDeviceId())
                                    deviceInfos.add(AppContext.getmOurDevices().get(i));
                            }
                            devicesAdapter.notifyDataSetChanged();
                        }
                        hideLoadingDialog();
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);
    }

    /**
     * 接收红点数量改变
     */
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
                            if (event.getDeviceId() == DeviceList.DEVICE_ID_SENSOR
                                    || event.getDeviceId() == DeviceList.DEVICE_ID_SWITCH_SCENE
                                    || event.getDeviceId() == DeviceList.DEVICE_ID_THTB2) {
                                return;
                            }

                            for (int i = 0; i < deviceInfos.size(); i++) {
                                if (deviceInfos.get(i).getUId() == event.getUId()) {
                                    return;
                                }
                            }

                            LogUtil.e("deviceinfo", event.toString());
                            if (linerAdd.getVisibility() == View.VISIBLE) {
                                linerAdd.setVisibility(View.GONE);
                            }
                            /*if (linerAdd.getVisibility() == View.VISIBLE) {
                                linerAdd.setVisibility(View.GONE);
                            }*/
                            if (swiperefreshlayoutDevices.isRefreshing()) {
                                swiperefreshlayoutDevices.setRefreshing(false);
                            }
                            //                            doorLockMaps.put(event.getUId(),event.getDeviceName());
                            deviceInfos.add(event);
                            devicesAdapter = new DeviceAdapter(mContext, bdylist, deviceInfos, device_list);
                            gvDevices.setAdapter(devicesAdapter);
                            //sp存储门锁uid和门锁name
                            //                            PreferencesUtils.saveObject(doorLockmaps,doorLockMaps);
//                            devicesAdapter.notifyDataSetChanged();
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            if (timerTask != null) {
                                timerTask.cancel();
                                timerTask = null;
                            }
                            hideLoading();
                        }
                    }
                }, onErrorAction);
        mCompositeSubscription.add(mSubscriptionDevice);
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
                        for (int i = 0; i < bdylist.size(); i++) {
                            if (bdylist.get(i).getBid().equals(event.getId())) {
                                bdylist.get(i).setName(event.getName());
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

    //    @Override
//    public void onResume() {
//        super.onResume();
//        mContext.icvss.equesGetDeviceList();
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode==this.requestCode&&resultCode==this.resultCode){
//            int uid=data.getIntExtra("UID",0);
//            String devName=data.getStringExtra("devieceName");
//            for(int i=0;i<deviceInfos.size();i++){
//                if(deviceInfos.get(i).getUId()==uid){
//                    if(devName!=null){
//                        if(devName.equals(deviceInfos.get(i).getDeviceName())){
//                            return;
//                        }else{
//                            deviceInfos.get(i).setDeviceName(devName);
//                            devicesAdapter.notifyDataSetChanged();
//                            return;
//                        }
//                    }else {
//                        return;
//                    }
//                }
//            }
//        }
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
//        datas.add("王力视频智能锁");
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
                } else if (position == 2) {
                    popupWindow.dismiss();
                    skipAct(VideoLockDMSFirstActivity.class);
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ((MainActivity) getActivity()).openLeftMenu();
                break;
            case R.id.iv_right_menu:
                showPopupWindow(parentView);
                break;
           /* case R.id.liner_refresh:
                showLoadingDialog();
                //通知页面更新数据
                try {
                    if (null != deviceInfos)
                        deviceInfos.clear();
                    if (bdylist != null)
                        bdylist.clear();
                    devicesAdapter.notifyDataSetChanged();
                    AppContext.getInstance().getSerialInstance().getDevices();
                    mContext.icvss.equesGetDeviceList();
                } catch (Exception e) {
                }
                Observable.timer(3, TimeUnit.SECONDS).compose(TransformUtils.<Long>defaultSchedulers()).subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }
                });
                break;
*/
        }
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {

    }

    /**
     * 获取到网关下的wifi设备
     *
     * @param mVideoLockList
     */
    @Override
    public void getGateWayResult(List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> mVideoLockList) {
        if (mVideoLockList.size() > 0) {
            device_list.clear();
            device_list.addAll(mVideoLockList);

            AppContext.getInstance().setmVideoLockList(mVideoLockList);
            //设置极光
            Api.jpushSetting(mContext, PreferencesUtils.getString(PreferencesUtils.USER_NAME));

            if (linerAdd.getVisibility() == View.VISIBLE) {
                linerAdd.setVisibility(View.GONE);
            }
            devicesAdapter.notifyDataSetChanged();
            //查询返回设备的状态
            getDeviceStaus();

        }
    }

    @Override
    public void equesDeviceListCallback(List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> device_list) {
        if (device_list != null) {
            if (bdylist == null || bdylist.size() == 0) {
                for (int i = 0; i < device_list.size(); i++) {
                    if (device_list.get(i).getVendor_name().equals(FactoryType.EQUES)) {
                        String uuid = device_list.get(i).getUuid();
                        DeleteDevicesReq body = new DeleteDevicesReq();
                        DeleteDevicesReq.DeviceBean deviceBean = new DeleteDevicesReq.DeviceBean();
                        deviceBean.setVendor_name(FactoryType.EQUES);
                        deviceBean.setUuid(uuid);
                        String gwSnid = AppContext.getGwSnid();
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
                }
            }
        }
    }

    /**
     * token请求放回
     *
     * @param bean
     */
    @Override
    public void resAddTokenCallback(AddTokenResponse bean) {
        if ("200".equals(bean.getHeader().getHttp_code())) {
            if (bean.getBody() != null && bean.getBody().getToken() != null) {
                LogUtil.e("token", "" + bean.getBody().getToken());
                AppContext.setToken(bean.getBody().getToken());
                //查询返回设备的状态
                getDeviceStaus();
            }
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }

    }
}
