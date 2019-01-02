package com.fbee.smarthome_wl.ui.devicemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.DeviceManagerAdapter;
import com.fbee.smarthome_wl.adapter.equipfragmentpopwindow.PopWindowAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.event.ChangeDevicenameEvent;
import com.fbee.smarthome_wl.event.UpdateEquesNameEvent;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QuerySubUserInfoResponse;
import com.fbee.smarthome_wl.ui.equesdevice.EquesDeviceInfoActivity;
import com.fbee.smarthome_wl.ui.equesdevice.adddevices.AddDeviceActivity;
import com.fbee.smarthome_wl.ui.subuser.SubUserInfoContract;
import com.fbee.smarthome_wl.ui.subuser.SubUserInfoPresenter;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.Serial;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * @class name：com.fbee.smarthome_wl.ui.devicemanager
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/18 17:44
 */
public class DeviceManagerActivity extends BaseActivity<SubUserInfoContract.Presenter> implements SubUserInfoContract.View {
    private List<MyDeviceInfo> mDatas;
    private LinearLayout activityScenaManager;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private SwipeMenuListView listView;
    private LinearLayout rlNodata;
    private TextView tvNodata;

    private View parentView;
    private DeviceManagerAdapter adapter;
    private AlertDialog alertDialog;
    private PopupWindow popupWindow;
    private Timer timer;
    private TimerTask timerTask;
    private LoginResult.BodyBean.GatewayListBean lockUserName;
    private String localUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scena_manager);
    }

    @Override
    protected void initView() {
        // icvss.equesGetDeviceList();
        activityScenaManager = (LinearLayout) findViewById(R.id.activity_scena_manager);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        rlNodata = (LinearLayout) findViewById(R.id.rl_nodata);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);
    }

    @Override
    protected void initData() {

        initApi();
        createPresenter(new SubUserInfoPresenter(this));
        tvNodata.setText("空空如也..请添加设备哦..");
        title.setText("设备管理");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        ivRightMenu.setOnClickListener(this);
        parentView = LayoutInflater.from(this).inflate(R.layout.activity_scena_manager, null);
        showLoadingDialog(null);
        mDatas = new ArrayList<>();
        adapter = new DeviceManagerAdapter(mDatas, this);
        List<EquesListInfo.bdylistEntity> equesinfo = AppContext.getBdylist();
        if (null != equesinfo && equesinfo.size() > 0) {
            if (rlNodata.getVisibility() == View.VISIBLE) {
                rlNodata.setVisibility(View.GONE);
            }
            for (int i = 0; i < equesinfo.size(); i++) {
                try {
                    hideLoadingDialog();
                    MyDeviceInfo info = new MyDeviceInfo();
                    String name = TextUtils.isEmpty(equesinfo.get(i).getNick()) ? equesinfo.get(i).getName() : equesinfo.get(i).getNick();
                    info.setName(name);
                    info.setSupplier(FactoryType.EQUES);
                    info.setId(equesinfo.get(i).getBid());
                    info.setDeviceType("猫眼");
                    mDatas.add(info);
                } catch (Exception e) {
                }

            }
        }

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
                        if (mDatas.size() == 0) {
                            hideLoadingDialog();
                        }
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);


        //注册RXbus接收飞比设备
        mSubscription = RxBus.getInstance().toObservable(DeviceInfo.class)
                .compose(TransformUtils.<DeviceInfo>defaultSchedulers())
                .subscribe(new Action1<DeviceInfo>() {
                    @Override
                    public void call(DeviceInfo event) {
                        if (event == null) return;
                        for (int i = 0; i < mDatas.size(); i++) {
                            if (mDatas.get(i).getId().equals(String.valueOf(event.getUId()))) {
                                return;
                            }
                        }
                        if (rlNodata.getVisibility() == View.VISIBLE) {
                            rlNodata.setVisibility(View.GONE);
                        }
                        MyDeviceInfo info = new MyDeviceInfo();
                        info.setName(event.getDeviceName());
                        info.setSupplier(FactoryType.FBEE);
                        info.setDeviceType(String.valueOf(event.getDeviceId()));
                        info.setId(String.valueOf(event.getUId()));
                        mDatas.add(info);
                        adapter.notifyDataSetChanged();
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (timerTask != null) {
                            timerTask.cancel();
                            timerTask = null;
                        }
                        hideLoadingDialog();
                    }
                }, onErrorAction);
        mCompositeSubscription.add(mSubscription);

        //注册RXbus接收怡康设备
        Subscription mSubscription01 = RxBus.getInstance().toObservable(EquesListInfo.class)
                .compose(TransformUtils.<EquesListInfo>defaultSchedulers())
                .subscribe(new Action1<EquesListInfo>() {
                    @Override
                    public void call(EquesListInfo event) {
                        if (mDatas.size() == 0) {
                            if (rlNodata.getVisibility() == View.GONE) {
                                rlNodata.setVisibility(View.VISIBLE);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
        mCompositeSubscription.add(mSubscription01);

        //访问网关拿到萤石设备
        QueryDevicesListInfo body = new QueryDevicesListInfo();
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
        presenter.reqGateWayInfo(body);

        //飞比设备改名字
        Subscription mSubscription = RxBus.getInstance().toObservable(ChangeDevicenameEvent.class)
                .compose(TransformUtils.<ChangeDevicenameEvent>defaultSchedulers())
                .subscribe(new Action1<ChangeDevicenameEvent>() {
                    @Override
                    public void call(ChangeDevicenameEvent event) {
                        try {
                            for (int i = 0; i < mDatas.size(); i++) {
                                if (mDatas.get(i).getId().equals(String.valueOf(event.getUid()))) {
                                    mDatas.get(i).setName(event.getName());
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                        }

                    }
                });
        mCompositeSubscription.add(mSubscription);

        //移康设备名修改成功
        Subscription mSubs = RxBus.getInstance().toObservable(UpdateEquesNameEvent.class)
                .compose(TransformUtils.<UpdateEquesNameEvent>defaultSchedulers())
                .subscribe(new Action1<UpdateEquesNameEvent>() {
                    @Override
                    public void call(UpdateEquesNameEvent event) {
                        try {
                            for (int i = 0; i < mDatas.size(); i++) {
                                if (mDatas.get(i).getId().equals(event.getId())) {
                                    mDatas.get(i).setName(event.getName());
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                        }

                    }
                });
        mCompositeSubscription.add(mSubs);

        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showDelateDialog(DeviceManagerActivity.this, position);
                        break;
                }
                return false;
            }
        });

        AppContext.getInstance().getSerialInstance().getDevices();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDatas.get(position).getSupplier().equals(FactoryType.FBEE)) {
                    //门锁
                    /*if(Integer.parseInt(mDatas.get(position).getDeviceType()) == DeviceList.DEVICE_ID_DOOR_LOCK){
                        Intent intent=new Intent(DeviceManagerActivity.this,DoorLockInfoActivity.class);
                        intent.putExtra("uuid",Integer.parseInt(mDatas.get(position).getId()));
                        startActivity(intent);
                    }else{
                        showCustomizeDialog(position,mDatas.get(position).getName());
                    }*/

                    if(mDatas.get(position).getDeviceType().equals("WonlySmartEyeYs7")){
                        //萤石设备

                    }else{
                        Intent intent = new Intent(DeviceManagerActivity.this, DoorLockInfoActivity.class);
                        intent.putExtra("uuid", Integer.parseInt(mDatas.get(position).getId()));
                        startActivity(intent);
                    }

                } else if (mDatas.get(position).getSupplier().equals(FactoryType.EQUES)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Method.ATTR_BUDDY_NICK, mDatas.get(position).getName());
                    bundle.putString(Method.ATTR_BUDDY_BID, mDatas.get(position).getId());
                    if (AppContext.getOnlines().size() > 0) {
                        for (int i = 0; i < AppContext.getOnlines().size(); i++) {
                            String bid = AppContext.getOnlines().get(i).getBid();
                            if (mDatas.get(position).getId().equals(bid)) {
                                bundle.putString(Method.ATTR_BUDDY_UID, AppContext.getOnlines().get(i).getUid());
                                bundle.putInt(Method.ATTR_BUDDY_STATUS, AppContext.getOnlines().get(i).getStatus());
                            }
                        }
                    }
                    skipAct(EquesDeviceInfoActivity.class, bundle);
                }


            }
        });


    }


    /**
     * 修改门锁名称弹出对话框
     */
    private void showCustomizeDialog(final int position, String name) {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final DeviceInfo deviceInfo = new DeviceInfo();
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_title);
        title.setText("修改设备名称");
        final EditText editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        editText.setText(name);
        editText.setSelection(name.length());
        TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String editName = editText.getText().toString().trim();
                if (editName == null || editName.isEmpty()) {
                    ToastUtils.showShort("设备不能为空!");
                    return;
                }

                try {
                    final byte[] temp = editName.getBytes("utf-8");
                    if (temp.length > 16) {
                        ToastUtils.showShort("设备名过长!");
                        return;
                    }

                    MyDeviceInfo info = mDatas.get(position);

                    deviceInfo.setUId(Integer.parseInt(info.getId()));
                    deviceInfo.setDeviceName(info.getName());

                    Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            Serial mSerial = AppContext.getInstance().getSerialInstance();
                            int ret = mSerial.ChangeDeviceName(deviceInfo, temp);
                            subscriber.onNext(ret);
                        }

                    }).compose(TransformUtils.<Integer>defaultSchedulers())
                            .subscribe(new Subscriber<Integer>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onNext(Integer ret) {
                                    if (alertDialog != null)
                                        alertDialog.dismiss();
                                    if (ret >= 0) {
                                        mDatas.get(position).setName(editName);
                                        adapter.notifyDataSetChanged();
                                        RxBus.getInstance().post(new ChangeDevicenameEvent(Integer.parseInt(mDatas.get(position).getId()), editName));
                                        ToastUtils.showShort("修改成功!");
                                    } else {
                                        ToastUtils.showShort("修改失败!");
                                    }

                                }
                            });
                    mCompositeSubscription.add(sub);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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


    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(AppUtil.dp2px(DeviceManagerActivity.this, 75));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

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
                detele(position);
            }


        });
    }

    private void detele(final int position) {
        showLoadingDialog("正在删除设备");
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret = -1;
                if (mDatas.get(position).getSupplier().equals(FactoryType.FBEE)) {
                    if(mDatas.get(position).getDeviceType().equals("WonlySmartEyeYs7")){

                        DeleteDevicesReq body = new DeleteDevicesReq();
                        DeleteDevicesReq.DeviceBean bean = new DeleteDevicesReq.DeviceBean();
                        bean.setUuid(mDatas.get(position).getId());
                        bean.setVendor_name("ys7");
                        body.setGateway_vendor_name(FactoryType.FBEE);
                        body.setGateway_uuid(AppContext.getGwSnid());
                        body.setDevice(bean);
                        presenter.reqDeleteGateWay(body,position);
                    }else{
                        Serial mSerial = AppContext.getInstance().getSerialInstance();
                        DeviceInfo deviceinfo = new DeviceInfo();
                        deviceinfo.setUId(Integer.parseInt(mDatas.get(position).getId()));
                        ret = mSerial.deleteDevice(deviceinfo);
                        subscriber.onNext(ret);
                    }
                } else if (mDatas.get(position).getSupplier().equals(FactoryType.EQUES)) {
                    DeleteDevicesReq body = new DeleteDevicesReq();
                    DeleteDevicesReq.DeviceBean bean = new DeleteDevicesReq.DeviceBean();
                    bean.setUuid(mDatas.get(position).getId());
                    bean.setVendor_name(FactoryType.EQUES);
                    body.setGateway_vendor_name(FactoryType.EQUES);
                    body.setGateway_uuid(AppContext.getGwSnid());
                    body.setDevice(bean);
                    presenter.reqDeleteGateWay(body, position);
                }
                subscriber.onCompleted();
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(Integer ret) {
                        hideLoadingDialog();
                        if (ret >= 0) {
                            showToast("删除成功");
                            RxBus.getInstance().post(mDatas.get(position));
                            List<DeviceInfo> devices = AppContext.getmOurDevices();
                            if (devices != null && devices.size() > 0) {
                                for (int i = 0; i < devices.size(); i++) {
                                    if (AppContext.getmOurDevices().get(i).getUId() == Integer.parseInt(mDatas.get(position).getId())) {
                                        AppContext.getmOurDevices().remove(i);
                                    }
                                }
                            }


                            mDatas.remove(mDatas.get(position));
                            if (mDatas.size() == 0) {
                                if (rlNodata.getVisibility() == View.GONE) {
                                    rlNodata.setVisibility(View.VISIBLE);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            showToast("删除失败");
                        }
                    }
                });

        mCompositeSubscription.add(sub);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.iv_right_menu:

                showPopupWindow(parentView);
                break;

        }
    }


    //添加设备弹出对话框
    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popwindow_equipfragment, null);
        // 设置按钮的点击事件
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyler_popwindow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> datas = new ArrayList<>();
        datas.add("其他设备(允许入网)");
        datas.add("猫眼设备");
        PopWindowAdapter adapter = new PopWindowAdapter(this, datas);
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

    }

    private void initTimer() {
        if (!AppUtil.isNetworkAvailable(this)) {
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
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }

    @Override
    public void resQuerySubUser(QuerySubUserInfoResponse bean) {

    }

    @Override
    public void resDeleteGateWay(BaseResponse bean, int position) {
        showToast("删除成功");
        icvss.equesDelDevice(mDatas.get(position).getId());
        mDatas.remove(mDatas.get(position));
        adapter.notifyDataSetChanged();
    }

    //查询服务器网关下的设备回调
    @Override
    public void resReqGateWayInfo(QueryGateWayInfoReq res) {
        List<QueryGateWayInfoReq.BodyEntity.DeviceListEntity> device_list = res.getBody().getDevice_list();
        if(device_list!=null &&device_list.size()>0){
            if (rlNodata.getVisibility() == View.VISIBLE) {
                rlNodata.setVisibility(View.GONE);
            }
            for(int x=0;x<device_list.size();x++){
                if(device_list.get(x).getVendor_name().equals("ys7")){
                    MyDeviceInfo info = new MyDeviceInfo();
                    info.setName("萤石猫眼");
                    info.setSupplier(FactoryType.FBEE);
                    info.setDeviceType(device_list.get(x).getType());
                    info.setId(device_list.get(x).getUuid());
                    mDatas.add(info);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {

    }




}
