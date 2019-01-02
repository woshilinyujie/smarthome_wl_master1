package com.fbee.smarthome_wl.ui.gateway;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.UpDataGwName;
import com.fbee.smarthome_wl.event.UpdateGwEvent;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.AES256Encryption;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.QrcodeUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import static com.fbee.smarthome_wl.base.BaseCommonPresenter.mSeqid;

public class GateWayQrcodeActivity extends BaseActivity {
    private RelativeLayout activityGateWayQrcode;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ImageView ivQrcode;
    private RelativeLayout rlModifySecurityCode;
    private TextView tvSecurity;
    private ImageView ivArrow05;
    private TextView tvHint;
    private String username;
    private Bitmap bitmap;
    private Bitmap mbitmap;
    Subscription sub;
    private TextView tvAccount;
    private TextView tvPsw;
    private TextView tvSnid;
    private TextView tvVersion;
    private TextView tvName;
    private TextView tvGwname;
    private RelativeLayout rlGwName;
    private String pass;
    private AlertDialog alertDialog;
    private AlertDialog checkAlertDialog;
    private AddGateWayReq gwBean;
    private String note;
    private String snid;
    private String name;
    private boolean isCurrentGW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_way_qrcode);
    }

    @Override
    protected void initView() {
        initApi();
        activityGateWayQrcode = (RelativeLayout) findViewById(R.id.activity_gate_way_qrcode);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        ivQrcode = (ImageView) findViewById(R.id.iv_qrcode);
        tvAccount = (TextView) findViewById(R.id.tv_account);
        tvPsw = (TextView) findViewById(R.id.tv_psw);
        tvSnid = (TextView) findViewById(R.id.tv_snid);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvGwname = (TextView) findViewById(R.id.tv_gwname);
        rlGwName = (RelativeLayout) findViewById(R.id.rl_gw_name);
        rlModifySecurityCode = (RelativeLayout) findViewById(R.id.rl_modify_security_code);
        tvSecurity = (TextView) findViewById(R.id.tv_security);
        ivArrow05 = (ImageView) findViewById(R.id.iv_arrow05);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        title.setText("网关信息");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        //rlModifySecurityCode.setOnClickListener(this);
        tvRightMenu.setText("保存");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);

        rlGwName.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        LoginResult.BodyBean.GatewayListBean bean = (LoginResult.BodyBean.GatewayListBean) getIntent().getSerializableExtra("Bean");
        isCurrentGW=getIntent().getBooleanExtra("isCurrentGW",false);
        if (null != bean) {
            snid = bean.getUuid();
            username = bean.getUsername();
            if (AppUtil.isMobileNO(username)) {
                pass = "123456";
            } else {
                pass = bean.getPassword();
            }
            if (TextUtils.isEmpty(bean.getNote())) {
                note = bean.getUsername();
            } else {
                note = bean.getNote();
            }

            String ver = bean.getVersion();
            tvGwname.setText(note);
            tvName.setText(note);
            tvAccount.setText(username);
            tvPsw.setText(pass);
            tvSnid.setText(snid);
            tvVersion.setText(ver);

            String result = "FHA" + snid + "GT" + username + "pass" + pass;
            String str = "账号:" + username + "," + "密码:" + pass;
            bitmap = QrcodeUtil.createQRImage(result, note, str, AppUtil.dp2px(this, 210), AppUtil.dp2px(this, 200), this);
            ivQrcode.setImageBitmap(bitmap);
            if(isCurrentGW){
                tvSecurity.setTextColor(getResources().getColor(R.color.black));
                ivArrow05.setVisibility(View.VISIBLE);
                tvHint.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                showLoadingDialog("保存中...");
                sub = Observable.create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        Boolean b = false;
//                        mbitmap = takeScreenShot(GateWayQrcodeActivity.this);
                        if (null != bitmap && null != username) {
                            b = QrcodeUtil.saveImageToGallery(GateWayQrcodeActivity.this, bitmap, username);
                        }
                        subscriber.onNext(b);
                        subscriber.onCompleted();
                    }

                }).compose(TransformUtils.<Boolean>defaultSchedulers())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {
                                hideLoadingDialog();
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoadingDialog();
                                showToast("保存失败");
                            }

                            @Override
                            public void onNext(Boolean b) {
                                hideLoadingDialog();
                                if (b) {
                                    showToast("已保存到本地路径" + Environment.getExternalStorageDirectory() + "/WL_GATEWAY");
                                } else {
                                    showToast("保存失败");
                                }
                            }
                        });

                break;

            case R.id.rl_gw_name:
                showUpdataName();
                break;
            //安防密码查看修改
            case R.id.rl_modify_security_code:
                if(isCurrentGW)
                showCheckSecutityCodeDialog();
                break;

        }
    }

    /**
     * 查看安防密码弹框
     */
    public void showCheckSecutityCodeDialog(){
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_check_security_code, null);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        final EditText tvDialogContent = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        TextView tvLeftCancelBtn = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView tvRightConfirmBtn = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        tvTitle.setText("查看安防密码");
        customizeDialog.setCancelable(false);
        tvRightConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = tvDialogContent.getText().toString().trim();
                if (content == null || content.isEmpty()) {
                    showToast("请输入登录密码");
                    return;
                }else{
                    if(content.equals(PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW))){
                        checkAlertDialog.dismiss();
                        showLocalSecutityCodeDialog();
                    }else{
                        showToast("登录密码不正确");
                        return;
                    }
                }

            }
        });
        tvLeftCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAlertDialog != null)
                    checkAlertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        checkAlertDialog = customizeDialog.show();
    }

    /**
     * 查看本地安防密码弹框
     */
    public void showLocalSecutityCodeDialog(){
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_check_security_code, null);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        LinearLayout linerCheck = (LinearLayout)dialogView. findViewById(R.id.liner_check);
        LinearLayout linerHint = (LinearLayout) dialogView.findViewById(R.id.liner_hint);
        TextView tvLocalSecurityCode = (TextView) dialogView.findViewById(R.id.tv_local_security_code);
        TextView tvLeftCancelBtn = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView tvRightConfirmBtn = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        linerCheck.setVisibility(View.GONE);
        linerHint.setVisibility(View.VISIBLE);
        tvTitle.setText("查看安防密码");
        tvRightConfirmBtn.setText("修改");
        tvLocalSecurityCode.setText("111111");
        customizeDialog.setCancelable(false);
        tvRightConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAlertDialog != null)
                    checkAlertDialog.dismiss();
                showChangeSecutityCodeDialog();
            }
        });
        tvLeftCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAlertDialog != null)
                    checkAlertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        checkAlertDialog = customizeDialog.show();
    }

    /**
     * 修改本地安防密码
     */
    public void showChangeSecutityCodeDialog(){
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_check_security_code, null);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        LinearLayout linerCheck = (LinearLayout)dialogView. findViewById(R.id.liner_check);
        LinearLayout linerChangeSecurityCode = (LinearLayout) dialogView.findViewById(R.id.liner_change_security_code);
        final EditText etPrimarySecurityCode = (EditText) dialogView.findViewById(R.id.et_primary_security_code);
        final EditText etNewSecurityCode = (EditText) dialogView.findViewById(R.id.et_new_security_code);
        final EditText etComfirmNewSecurityCode = (EditText) dialogView.findViewById(R.id.et_comfirm_new_security_code);
        TextView tvLeftCancelBtn = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView tvRightConfirmBtn = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        linerCheck.setVisibility(View.GONE);
        linerChangeSecurityCode.setVisibility(View.VISIBLE);
        tvTitle.setText("修改安防密码");
        customizeDialog.setCancelable(false);
        tvRightConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String primaryCode = etPrimarySecurityCode.getText().toString().trim();
                String newCode = etNewSecurityCode.getText().toString().trim();
                String confirmNewCode = etComfirmNewSecurityCode.getText().toString().trim();
                if (primaryCode == null || primaryCode.isEmpty()) {
                    showToast("请输入原安防密码");
                    return;
                }
                if (newCode == null || newCode.isEmpty()) {
                    showToast("请输入新安防密码");
                    return;
                }
                if(!newCode.equals(confirmNewCode)){
                    showToast("请确认新安防密码");
                    return;
                }
                if (checkAlertDialog != null)
                    checkAlertDialog.dismiss();
            }
        });
        tvLeftCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAlertDialog != null)
                    checkAlertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        checkAlertDialog = customizeDialog.show();
    }

    public void showUpdataName() {
        final List<LoginResult.BodyBean.GatewayListBean> gatewayList = AppContext.getInstance().getBodyBean().getGateway_list();

        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name, null);
        TextView nameTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        nameTitle.setText("修改设备名称");
        final EditText editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        editText.setFilters(new InputFilter[]{Api.filter});
        editText.setText(tvGwname.getText().toString());
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
                    if (AppUtil.isMobileNO(username)) {
                        psw = "123456";
                    } else {
                        psw = pass;
                    }
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

    /**
     * 更新网关
     *
     * @param username
     * @param name
     * @param psw
     */

    private void addgateway(final String username, final String name, String psw) {
        showLoadingDialog("加载中...");
        String uuid = null;
        List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
        for (int i = 0; i < gateway_list.size(); i++) {
            String username1 = gateway_list.get(i).getUsername();
            if (username1.equals(username)) {
                uuid = gateway_list.get(i).getUuid();
                break;
            }
        }

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onNext(JsonObject json) {
                hideLoadingDialog();
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        BaseResponse res = new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
                        if ("200".equals(res.getHeader().getHttp_code())) {
                            tvGwname.setText(name);
                            List<LoginResult.BodyBean.GatewayListBean> gateway_list = AppContext.getInstance().getBodyBean().getGateway_list();
                            for (int i = 0; i < gateway_list.size(); i++) {
                                String username1 = gateway_list.get(i).getUsername();
                                if (username1.equals(username)) {
                                    gateway_list.get(i).setNote(name);
                                    break;
                                }
                            }
                            //重新绘制二维码
                            String result = "FHA" + snid + "GT" + username + "pass" + pass;
                            String str = "账号:" + username + "," + "密码:" + pass;
                            bitmap = QrcodeUtil.createQRImage(result, name, str,
                                    AppUtil.dp2px(GateWayQrcodeActivity.this, 210),
                                    AppUtil.dp2px(GateWayQrcodeActivity.this, 200), GateWayQrcodeActivity.this);
                            ivQrcode.setImageBitmap(bitmap);
                            RxBus.getInstance().post(new UpdateGwEvent());
                            RxBus.getInstance().post(new UpDataGwName(name, false));
                            showToast("修改成功！");
                        } else {
                            ToastUtils.showShort(RequestCode.getRequestCode(res.getHeader().getReturn_string()));
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideLoadingDialog();
            }

            @Override
            public void onCompleted() {

            }
        });


        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_GATEWAY_ADD_REQ");
        header.setSeq_id(mSeqid.getAndIncrement() + "");
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);


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

        umsbean.setBody(gwBean);
        ums.setUMS(umsbean);
        Subscription subscription = mApiWrapper.addGateWay(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);

    }


    /**
     * 截屏
     */
    private static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        int titleH = AppUtil.dp2px(activity, 45);
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, titleH + statusBarHeight, width, height
                - statusBarHeight - titleH);
        view.destroyDrawingCache();
        return b;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (mbitmap != null) {
            mbitmap.recycle();
            mbitmap = null;
        }
        if (sub != null) {
            sub.unsubscribe();
        }

    }
}
