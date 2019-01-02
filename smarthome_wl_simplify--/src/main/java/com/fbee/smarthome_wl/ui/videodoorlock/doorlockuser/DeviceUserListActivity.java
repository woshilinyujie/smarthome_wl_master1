package com.fbee.smarthome_wl.ui.videodoorlock.doorlockuser;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.DeviceUserAdapter;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.UserCofigSetting;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.ModefyDeviceUserNum;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.request.videolockreq.UserAuthRequest;
import com.fbee.smarthome_wl.request.videolockreq.UserTransportRequest;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.response.videolockres.DeviceTransportResponse;
import com.fbee.smarthome_wl.response.videolockres.MnsBaseResponse;
import com.fbee.smarthome_wl.ui.usermanage.UserManageContract;
import com.fbee.smarthome_wl.ui.usermanage.UserManagePresenter;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.Base64Util;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogEditUser;
import com.fbee.smarthome_wl.widget.dialog.DialogInputPsw;
import com.fbee.smarthome_wl.widget.dialog.DialogSingleChoose;
import com.fbee.smarthome_wl.widget.pop.PopwindowChoose;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.uid;

public class DeviceUserListActivity extends BaseActivity<UserManageContract.Presenter> implements BaseRecylerAdapter.OnItemClickLitener, UserManageContract.View {
    private LinearLayout activityUserManage;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RelativeLayout relativeGuanlian;
    private LinearLayout linear111111;
    private TextView textGuanlian;
    private TextView guanlianUserText;
    private SwipeMenuListView recyclerUserManage;
    private List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> datas;
    private DeviceUserAdapter adapter;
    private String userPhone;
    private String primaryUserId;
    private String uuid;
    private AlertDialog alertDialog;
    private PopwindowChoose popwindow;
    private io.socket.client.Socket socket;
    private QueryDeviceUserResponse deviceInfo;
    private int transportTag = 0;

    private int delPositon = -1;
    private DialogSingleChoose dialog;
    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviceuser_manager);
    }

    @Override
    protected void initView() {
        activityUserManage = (LinearLayout) findViewById(R.id.activity_user_manage);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        relativeGuanlian = (RelativeLayout) findViewById(R.id.relative_guanlian);
        linear111111 = (LinearLayout) findViewById(R.id.linear111111);
        textGuanlian = (TextView) findViewById(R.id.text_guanlian);
        guanlianUserText = (TextView) findViewById(R.id.guanlian_user_text);
        recyclerUserManage = (SwipeMenuListView) findViewById(R.id.recycler_user_manage);

        uuid = getIntent().getStringExtra("deviceUuid");
        deviceInfo = (QueryDeviceUserResponse) getIntent().getSerializableExtra("deviceInfo");
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        try {
            socket = BaseApplication.getInstance().getSocket();
            socket.on("MSG_DEVICE_TRANSPORT_REQ", deviceTransportRsp);
            socket.on("MSG_USER_TRANSPORT_RSP", userTransportRsp);
            socket.on("MSG_USER_AUTH_RSP", userConfirmation);
        } catch (Exception e) {

        }
        guanlianUserText.setText("暂无关联用户");
    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new UserManagePresenter(this));
        back.setVisibility(View.VISIBLE);
        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        back.setOnClickListener(this);
        ivRightMenu.setOnClickListener(this);
        title.setText("门锁用户");
        relativeGuanlian.setOnClickListener(this);
        userPhone = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        datas = new ArrayList<>();
        adapter = new DeviceUserAdapter(this, datas);
        recyclerUserManage.setAdapter(adapter);
        recyclerUserManage.setMenuCreator(creator);
        recyclerUserManage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showCustomizeDialog(position);
            }
        });

        recyclerUserManage.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (!isOnline) {
                    // showToast("设备不在线!");
                    showIsOnlineDialog();
                    return false;
                }
                switch (index) {
                    //删除 单个用户
                    case 1:
                        transportTag = 1;
                        delPositon = position;
                        getAmdpsw("del", datas.get(position).getId(),
                                datas.get(position).getLevel(), "pwd,fp,card");
//                        getAmdpsw("del",datas.get(position).getId(),
//                                datas.get(position).getLevel(),"all");

                        break;
                    //编辑
                    case 0:
                        showEditdialog(position);
                        break;


                }
                return false;
            }
        });

        initPopup();
        reqQueryDevice();
        receiveDeviceUserChange();
    }

    private void showEditdialog(final int position) {
        new DialogEditUser(this, new DialogEditUser.DialogListener() {
            @Override
            public void onRightClick(String mode, String unlock_mode) {
                if (null == mode) {
                    return;
                }
                if (mode.equals("del") && unlock_mode.equals("pwd,fp,card")) {
                    transportTag = 1;
                    delPositon = position;
                }
//                if (mode.equals("del") && unlock_mode.equals("all")) {
//                    transportTag = 1;
//                    delPositon = position;
//                }
                getAmdpsw(mode, datas.get(position).getId(),
                        datas.get(position).getLevel(), unlock_mode);


            }
        }).show();
    }

    private void showIsOnlineDialog() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("设备不在线!").setCancelable(true).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

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
                    Log.e("服务器发送", "用户验证发送成功");
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isNeedConfirm) {
                        isNeedConfirm = false;
                        JSONObject data = (JSONObject) args[0];
                        MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                        Log.e("服务器发送", "用户验证返回:" + response.getReturn_string());
                        if (response.getReturn_string().contains("SUCCESS")) {

                            showToast("操作失败请重试!");
                        }
                    }

                }
            });
        }
    };
    private boolean isNeedConfirm = false;
    //private boolean isTransportSuccess=false;
    private boolean isNeedAddToken = false;
    private Emitter.Listener userTransportRsp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // if(isTransportSuccess){
                    // isTransportSuccess=false;
                    JSONObject data = (JSONObject) args[0];
                    MnsBaseResponse response = new Gson().fromJson(data.toString(), MnsBaseResponse.class);
                    if (response != null && "DEVICE_USER_CONFIG".equals(response.getCmd())) {
                        showToast("用户透传返回:" + response.getReturn_string());
                        if (response.getReturn_string().contains("SUCCESS")) {
                            Log.e("服务器发送", "用户透传返回:" + response.getReturn_string());
                        }
                        //需要验证
                        else if (response.getReturn_string().equals("RETRUN_NEED_AUTH_STRING")) {
                            userConfirmationReq();
                        }
                        //token失效
                        else if (response.getReturn_string().equals("RETURN_TOKEN_NOT_EXISTS_STRING") || response.getReturn_string().equals("RETURN_INVALID_TOKEN_STRING")) {
                            isNeedAddToken = true;
                            reqAddToken();
                        }
                    }
                    // }

                }
            });
        }
    };

    private Emitter.Listener deviceTransportRsp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    DeviceTransportResponse response = new Gson().fromJson(data.toString(), DeviceTransportResponse.class);
                    if (response != null) {
                        if (response.getUuid() != null && response.getUuid().equals(uuid)) {
                            if (response.getData() != null) {
                                byte[] resData = Base64Util.decode(response.getData());
                                String resStrData = new String(resData);
                                LogUtil.e("服务器发送", "设备透出返回:" + resStrData);
                                JsonObject jsonData = new Gson().fromJson(resStrData, JsonObject.class);
                                if (jsonData != null) {
                                    String returnStr = jsonData.get("return_string").getAsString();
                                    String returnCmd = jsonData.get("cmd").getAsString();
                                    if ("RETURN_SUCCESS_OK_STRING".equals(returnStr)) {
                                        if ("DEVICE_USER_CONFIG".equals(returnCmd)) {
                                            showToast("操作成功!");
                                            switch (transportTag) {
                                                //删除单个
                                                case 1:
                                                    if (delPositon >= 0) {
                                                        datas.remove(delPositon);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                    break;
                                                //批量删除
                                                case 2:
                                                    if (null != level) {
                                                        if (datas == null || datas.size() == 0)
                                                            return;
                                                        for (int i = datas.size() - 1; i >= 0; i--) {
                                                            if (datas.get(i).getLevel().equals(level)) {
                                                                datas.remove(i);
                                                            }
                                                        }
                                                        level = null;
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                    break;
                                            }

                                        }
                                    } else {
                                        showToast("操作失败!");
                                    }
                                } else {
                                    showToast("操作失败!");
                                }
                            }
                        }
                    } else {
                        showToast("操作失败!");
                    }
                }
            });
        }
    };

    /**
     * 门锁透传命令
     *
     * @param pass
     * @param mode         操作方式
     * @param object       被操作对象
     * @param object_level 等级
     * @param unlock_mode  开锁方式
     */
    private void userTransportReq(String pass, String mode, String object,
                                  String object_level, String unlock_mode) {
        //isTransportSuccess=false;
        UserTransportRequest bean = new UserTransportRequest();
        bean.setUuid(uuid);
        bean.setApi_version("1.0");
        bean.setVendor_name(FactoryType.GENERAL);
        bean.setToken(AppContext.getToken());
        bean.setCmd("DEVICE_USER_CONFIG");
        UserCofigSetting usercofig = new UserCofigSetting();
        UserCofigSetting.BeOperatedBean beOperatedBean = new UserCofigSetting.BeOperatedBean();
        beOperatedBean.setMode(mode);
        beOperatedBean.setObject(object);
        beOperatedBean.setObject_level(object_level);
        beOperatedBean.setUnlock_mode(unlock_mode);
        usercofig.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000l));
        usercofig.setCmd("DEVICE_USER_CONFIG");
        usercofig.setUnlock_adm_pwd(pass);
        usercofig.setBe_operated(beOperatedBean);
        LogUtil.e("服务器发送", usercofig.toString());
        String dodlreq = new Gson().toJson(usercofig);
        String dodlreqresult = Base64Util.encode(dodlreq.getBytes());
        bean.setData(dodlreqresult);
        String req = new Gson().toJson(bean);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(req);
            socket.emit("MSG_USER_TRANSPORT_REQ", jsonObject, new Ack() {
                @Override
                public void call(Object... objects) {
                    Log.e("服务器发送", "用户透传发送成功");
                    //isTransportSuccess=true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {

            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            openItem.setBackground(new ColorDrawable(Color.rgb(0x3D, 0xA4,
                    0x6E)));
            // set item width
            openItem.setWidth(AppUtil.dp2px(DeviceUserListActivity.this, 75));
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
            deleteItem.setWidth(AppUtil.dp2px(DeviceUserListActivity.this, 75));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);


        }
    };


    /**
     * 请求查询设备信息
     */
    private void reqQueryDevice() {
        QueryDeviceuserlistReq body = new QueryDeviceuserlistReq();
        body.setVendor_name(FactoryType.GENERAL);
        body.setUuid(uuid);
        presenter.reqQueryDevice(body);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            //门锁关联用户界面
            case R.id.relative_guanlian:
                Intent intent = new Intent(this, UserassociatedActivity.class);
                intent.putExtra("deviceid", uuid);
                intent.putExtra("primaryUserId", primaryUserId);
                startActivity(intent);
                break;
            case R.id.iv_right_menu:
                if (popwindow != null && popwindow.isShowing()) {
                    colsePop();
                } else {
                    showPop();
                }
                break;
        }

    }


    private void initPopup() {
        if (popwindow == null) {
            ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();
            PopwindowChoose.Menu pop = new PopwindowChoose.Menu(R.mipmap.add, "新增用户");
            menulist.add(pop);
            PopwindowChoose.Menu pop1 = new PopwindowChoose.Menu(R.mipmap.add, "批量删除");
            menulist.add(pop1);
            popwindow = new PopwindowChoose(this, menulist, this, this, true);
            popwindow.setAnimationStyle(R.style.popwin_anim_style);
        }

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
            ivRightMenu.getLocationOnScreen(location);
            //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
            popwindow.showAtLocation(ivRightMenu, Gravity.NO_GRAVITY, (location[0] + ivRightMenu.getWidth() / 2) - popupWidth / 2, location[1] + ivRightMenu.getHeight());

        }

    }


    /**
     * 接收绑定用户改变
     */
    private void receiveDeviceUserChange() {
        Subscription subHint = RxBus.getInstance().toObservable(ModefyDeviceUserNum.class)
                .compose(TransformUtils.<ModefyDeviceUserNum>defaultSchedulers())
                .subscribe(new Action1<ModefyDeviceUserNum>() {
                    @Override
                    public void call(ModefyDeviceUserNum event) {
                        if (event.getUserNum() == null) {
                            guanlianUserText.setText("暂无关联用户");
                            primaryUserId = null;
                        } else {
                            for (int k = 0; k < datas.size(); k++) {
                                if (datas.get(k) == null) continue;
                                if (event.getUserNum().equals(String.valueOf(datas.get(k).getId()))) {
                                    if (datas.get(k).getNote() == null || datas.get(k).getNote().isEmpty()) {
                                        guanlianUserText.setText(event.getUserNum() + "号用户");
                                    } else {
                                        guanlianUserText.setText(datas.get(k).getNote());
                                    }
                                    break;
                                }
                            }
                            primaryUserId = event.getUserNum();
                        }
                    }
                });
        mCompositeSubscription.add(subHint);
    }


    /**
     * 请求添加用户设备
     */
    public void reqAddDeviceUser(String userId, String aliars) {
        AddDeviceUser body = new AddDeviceUser();
        body.setVendor_name(FactoryType.GENERAL);
        body.setUuid(uuid);
        AddDeviceUser.DeviceUserBean deviceUserBean = new AddDeviceUser.DeviceUserBean();
        deviceUserBean.setId(userId);
        deviceUserBean.setNote(aliars);
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums = AppContext.getMap().get(String.valueOf(uid));
        List<String> noticeList = new ArrayList<>();
        if (userNums != null) {
            for (int i = 0; i < userNums.size(); i++) {
                if (userNums.get(i).getId().isEmpty()) continue;
                if (userNums.get(i).getId().equals(userId)) {
                    if (userNums.get(i).getWithout_notice_user_list() != null) {
                        noticeList.addAll(userNums.get(i).getWithout_notice_user_list());
                        break;
                    }
                }
            }
        }
        deviceUserBean.setWithout_notice_user_list(noticeList);
        body.setDevice_user(deviceUserBean);
        presenter.reqAddDeviceUser(body);

    }


    /**
     * 修改用户名称弹出对话框
     */
    private void showCustomizeDialog(final int position) {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_title);
        title.setText("修改用户名称");
        TextView tvDialogHint = (TextView) dialogView.findViewById(R.id.tv_dialog_hint);
        tvDialogHint.setVisibility(View.VISIBLE);
        final EditText editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        editText.setFilters(new InputFilter[]{Api.filter});
        if (TextUtils.isEmpty(datas.get(position).getNote())) {
            String note = datas.get(position).getId() + "号用户";
            editText.setText(note);
            editText.setSelection(note.length());
        } else {
            editText.setText(datas.get(position).getNote());
            editText.setSelection(datas.get(position).getNote().length());
        }
        TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editName = editText.getText().toString().trim();
                if (editName == null || editName.isEmpty()) {
                    ToastUtils.showShort("用户名不能为空!");
                    return;
                }
                for (int i = 0; i < datas.size(); i++) {
                    if (i != position) {
                        if (editName.equals(datas.get(i).getNote())) {
                            ToastUtils.showShort("用户名已存在，请重新输入!");
                            return;
                        }
                    }
                }
                if (alertDialog != null)
                    alertDialog.dismiss();
                showLoadingDialog(null);

                //三秒钟如果请求没返回loading取消
                dismissLoading();

                //用户别名改变通知服务器
                if (!String.valueOf(datas.get(position).getId()).isEmpty()) {
                    reqAddDeviceUser(String.valueOf(datas.get(position).getId()), editName);
                    datas.get(position).setNote(editName);
                } else {
                    ToastUtils.showShort("操作失败");
                    return;
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


    /**
     * 四秒钟如果请求没返回loading取消
     */
    private void dismissLoading() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        Subscription subscription1 = Observable.timer(4000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        hideLoadingDialog();
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);
    }

    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {
        if (bean.getHeader().getHttp_code().equals("200")) {
            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = bean.getBody().getDevice_user_list();
            if (null == bodyEntities) {
                return;
            }
            String userPhone = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
            if (null != datas) {
                datas.clear();
                datas.addAll(bodyEntities);
                adapter.notifyDataSetChanged();

                for (int i = 0; i < bodyEntities.size(); i++) {
                    List<String> userlist = bodyEntities.get(i).getWithout_notice_user_list();
                    if (null != userlist) {
                        for (int j = 0; j < userlist.size(); j++) {
                            if (userPhone.equals(userlist.get(j))) {
                                primaryUserId = bodyEntities.get(i).getId();
                                if (bodyEntities.get(i).getNote() == null || bodyEntities.get(i).getNote().isEmpty()) {
                                    guanlianUserText.setText(bodyEntities.get(i).getId() + "号用户");
                                } else {
                                    guanlianUserText.setText(bodyEntities.get(i).getNote());
                                }
                                break;
                            }
                        }
                    }

                }

            }

        }
    }

    @Override
    public void resAddDeviceUser(BaseResponse bean) {
        if (bean.getHeader().getHttp_code().equals("200")) {
            showToast("操作成功!");
            adapter.notifyDataSetChanged();
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
        hideLoadingDialog();
    }

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
     * token返回
     *
     * @param bean
     */
    @Override
    public void resAddToken(AddTokenResponse bean) {
        LogUtil.e("token", "" + bean.getBody().getToken());
        if ("200".equals(bean.getHeader().getHttp_code())) {
            if (bean.getBody() != null && bean.getBody().getToken() != null) {
                LogUtil.e("token", "" + bean.getBody().getToken());
                AppContext.setToken(bean.getBody().getToken());
                if (isNeedAddToken) {
                    isNeedAddToken = false;
                    showToast("操作失败请重试!");
                }
            }
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void onItemClick(View view, int position) {
        if (!isOnline) {
            // showToast("设备不在线!");
            showIsOnlineDialog();
            return;
        }
        //新增用户
        if (position == 0) {
            showAddUserDialog();
        }
        //批量删除
        else if (position == 1) {
            showDelDialog();
        }
    }

    String paslevel; //添加用户等级

    private void showAddUserDialog() {
        String[] datas = new String[]{"管理员", "普通用户", "临时用户"};
        DialogSingleChoose dialog = new DialogSingleChoose(this, new DialogSingleChoose.DialogListener() {
            @Override
            public void onLeftClick() {
                paslevel = null;
            }

            @Override
            public void onRightClick() {
                getAmdpsw("create", "fff", paslevel
                        , "pwd,fp,card");

            }

            @Override
            public void OnItemCheckedListener(int postion) {

                switch (postion) {
                    case 0:
                        paslevel = "adm";
                        break;
                    case 1:
                        paslevel = "usr";
                        break;
                    case 2:
                        paslevel = "tmp";
                        break;
                }
            }
        });
        dialog.show();
        dialog.setData("选择新增用户等级", datas);

    }

    /**
     * 管理员密码
     */
    private void getAmdpsw(final String mode, final String object,
                           final String object_level, final String unlock_mode) {
        new DialogInputPsw(this, deviceInfo, uuid, new DialogInputPsw.DialogListener() {
            @Override
            public void onRightClick(String psw) {
                userTransportReq(psw, mode, object, object_level, unlock_mode);
            }
        }).show();
    }

    private String level;
    private String object;

    private void showDelDialog() {

        String[] datas = new String[]{"普通用户", "临时用户"};
        dialog = new DialogSingleChoose(this, new DialogSingleChoose.DialogListener() {
            @Override
            public void onLeftClick() {
                level = null;
                object = null;
            }

            @Override
            public void onRightClick() {
                if (null != level) {
                    transportTag = 2;
                    getAmdpsw("del", object, level
                            , "pwd,fp,card");
//                    getAmdpsw("del",object,level
//                            ,"all");
                } else {
                    showToast("请先选择批量删除的用户等级");
                }

            }

            @Override
            public void OnItemCheckedListener(int postion) {
                switch (postion) {
                    case 0:
                        level = "usr";
                        object = "usr_grp";
                        break;
                    case 1:
                        level = "tmp";
                        object = "tmp_grp";
                        break;
                }
            }
        });
        dialog.show();
        dialog.setData("选择需要删除的用户", datas);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.off("MSG_DEVICE_TRANSPORT_REQ", deviceTransportRsp);
            socket.off("MSG_USER_TRANSPORT_RSP", userTransportRsp);
            socket.off("MSG_USER_AUTH_RSP", userConfirmation);
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }
}
