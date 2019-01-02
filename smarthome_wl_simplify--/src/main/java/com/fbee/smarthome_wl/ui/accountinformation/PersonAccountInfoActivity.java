package com.fbee.smarthome_wl.ui.accountinformation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.ModifyAccountInfo;
import com.fbee.smarthome_wl.bean.ModifyAliars;
import com.fbee.smarthome_wl.common.ActivityPageManager;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.eques.ICVSSUserModule;
import com.fbee.smarthome_wl.event.UserHaderIconChange;
import com.fbee.smarthome_wl.request.UpdateUserinfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.ui.login.LoginActivity;
import com.fbee.smarthome_wl.ui.modifypassword.ModifyPasswordActivity;
import com.fbee.smarthome_wl.ui.personaccount.ModifyPersonAccountActivity;
import com.fbee.smarthome_wl.ui.subuser.SubUserListActivity;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.UriToPathUtil;
import com.fbee.zllctl.Serial;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import rx.Subscription;
import rx.functions.Action1;


public class PersonAccountInfoActivity extends BaseActivity<PersonAccountInfoContract.Presenter> implements PersonAccountInfoContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ImageView headIconPersonInfo;
    private RelativeLayout moreInfoPersonInfo;
    private TextView accountTextPersonInfo;
    private TextView setNamePersonInfo;
    private RelativeLayout setnameMoreInfoPersonInfo;
    private RelativeLayout editLoginpwdPersonInfo;
    private RelativeLayout editSubuserPersonInfo;
    private TextView loginOutSubuserPersonInfo;
    private RelativeLayout editAccountPersonInfo;
    private AlertDialog alertDialog;
    private String editName;
    private final int REQUEST_CODE = 1001;
    private String aliars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_account_info);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        headIconPersonInfo = (ImageView) findViewById(R.id.head_icon_person_info);
        moreInfoPersonInfo = (RelativeLayout) findViewById(R.id.more_info_person_info);
        accountTextPersonInfo = (TextView) findViewById(R.id.account_text_person_info);
        setNamePersonInfo = (TextView) findViewById(R.id.set_name_person_info);
        setnameMoreInfoPersonInfo = (RelativeLayout) findViewById(R.id.setname_more_info_person_info);
        editLoginpwdPersonInfo = (RelativeLayout) findViewById(R.id.edit_loginpwd_person_info);
        editSubuserPersonInfo = (RelativeLayout) findViewById(R.id.edit_subuser_person_info);
        loginOutSubuserPersonInfo = (TextView) findViewById(R.id.login_out_subuser_person_info);
        editAccountPersonInfo = (RelativeLayout) findViewById(R.id.edit_account_person_info);
    }

    @Override
    protected void initData() {
        title.setText("账号设置");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        //初始化头像
        initHeaderIcon();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            verifyRecordPermissions();
        initApi();
        createPresenter(new PersonAccountInfoPresenter(this));
        accountTextPersonInfo.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        aliars = PreferencesUtils.getString(PreferencesUtils.LOCAL_ALAIRS);
        if (aliars != null && !"".equals(aliars)) {
            setNamePersonInfo.setText(aliars);
        }
        moreInfoPersonInfo.setOnClickListener(this);
        setnameMoreInfoPersonInfo.setOnClickListener(this);
        editLoginpwdPersonInfo.setOnClickListener(this);
        editSubuserPersonInfo.setOnClickListener(this);
        loginOutSubuserPersonInfo.setOnClickListener(this);
        editAccountPersonInfo.setOnClickListener(this);
        //接收用户昵称改变
        receiveAliarsChange();
        //接收用户名改变
        receiveUserNameChange();
    }


    /**
     * 初始化头像
     */
    private void initHeaderIcon() {
        HashMap<String, String> map = (HashMap<String, String>) PreferencesUtils.getObject(PreferencesUtils.HEAD_ICON);
        if (map != null) {
            String uri = map.get(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
            if (uri != null) {
                ImageLoader.loadCropCircle(PersonAccountInfoActivity.this, uri, headIconPersonInfo, R.mipmap.default_user_picture);
            }
        }


    }


    private void verifyRecordPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.back:
                finish();
                break;

            //头像设置
            case R.id.more_info_person_info:
                Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
                if (Build.VERSION.SDK_INT < 19) {
                    innerIntent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择头像");
                startActivityForResult(wrapperIntent, REQUEST_CODE);
                break;

            //账号设置
            case R.id.edit_account_person_info:
                skipAct(ModifyPersonAccountActivity.class);
                break;

            //昵称设置
            case R.id.setname_more_info_person_info:
                showCustomizeDialog();
                break;

            //登录密码设置
            case R.id.edit_loginpwd_person_info:
                skipAct(ModifyPasswordActivity.class);
                break;

            //子用户设置
            case R.id.edit_subuser_person_info:
                skipAct(SubUserListActivity.class);
                break;

            //退出登录
            case R.id.login_out_subuser_person_info:
//                Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
//                    @Override
//                    public void onNext(JsonObject json) {
//                        if (!mCompositeSubscription.isUnsubscribed()) {
//                            if (null != json) {
//                                JsonObject jsonObj = json.getAsJsonObject("UMS");
//                                if (null == jsonObj || jsonObj.size() == 0)
//                                    return;
//                                BaseResponse res=new Gson().fromJson(jsonObj.toString(), BaseResponse.class);
//                            }
//                        }
//                    }
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                    }
//                    @Override
//                    public void onCompleted() {
//                    }
//                });
//
//                UMSBean.HeaderBean header = new UMSBean.HeaderBean();
//                header.setApi_version("1.0");
//                header.setMessage_type("MSG_USER_DEL_REQ");
//                header.setSeq_id("1");
//                Ums ums = new Ums();
//                UMSBean umsbean=new UMSBean();
//                umsbean.setHeader(header);
//                ums.setUMS(umsbean);
//                Subscription subscription=mApiWrapper.addGateWay(ums).subscribe(subscriber);
//                mCompositeSubscription.add(subscription);

                ThreadPoolUtils.getInstance().getSingleThreadExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        //清除AppContext中的缓存数据
                        AppContext.clearAllDatas();
                        Serial mSerial = AppContext.getInstance().getSerialInstance();
                        //释放资源
                        mSerial.releaseSource();
                        //移康退出
                        icvss.equesUserLogOut();
                        ICVSSUserModule.getInstance(PersonAccountInfoActivity.this).closeIcvss();
                        icvss = null;
                    }
                });

                //注销用户
                destroyUser();
                //销毁 MainActivity
//                ActivityPageManager.getInstance().finishActivity(MainActivity.class);
                ActivityPageManager.finishAllActivity();
                skipAct(LoginActivity.class);
                finish();
                break;

        }
    }

    /**
     * 注销用户
     */
    private void destroyUser(){
        presenter.reqDestroyUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    String imgPath = null;
                    Uri uri = data.getData();
                    if ("Xiaomi".equals(Build.MANUFACTURER)) {
                        uri = UriToPathUtil.geturi(data,PersonAccountInfoActivity.this);
                    }
                    imgPath = UriToPathUtil.getImageAbsolutePath(this, uri);

                    ImageLoader.loadCropCircle(this, imgPath, headIconPersonInfo, R.mipmap.default_user_picture);
                    HashMap<String, String> map = (HashMap<String, String>) PreferencesUtils.getObject(PreferencesUtils.HEAD_ICON);
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    map.put(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME), imgPath);
                    PreferencesUtils.saveObject(PreferencesUtils.HEAD_ICON, map);
                    RxBus.getInstance().post(new UserHaderIconChange(imgPath));
                    break;
                default:
                    break;
            }
        }
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
                        setNamePersonInfo.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_ALAIRS));

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
                        accountTextPersonInfo.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                    }
                });
        mCompositeSubscription.add(accountSucription);
    }


    /**
     * 修改昵称弹出对话框
     */
    private void showCustomizeDialog() {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name, null);
        TextView title = (TextView) dialogView.findViewById(R.id.tv_title);
        title.setText("修改用户昵称");
        final EditText editText = (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        if(aliars!=null && !"".equals(aliars)){
            editText.setText(aliars);
            editText.setSelection(aliars.length());
        }
        TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName = editText.getText().toString().trim();
                if (editName == null || editName.isEmpty()) {
                    ToastUtils.showShort("昵称不能为空!");
                    return;
                }

                try {
                    final byte[] temp = editName.getBytes("utf-8");
                    if (temp.length > 16) {
                        ToastUtils.showShort("昵称名过长!");
                        return;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                showLoadingDialog();
                UpdateUserinfoReq body = new UpdateUserinfoReq();
                body.setSafe_update("false");
                body.setNew_alias(editName);
                presenter.reqModifyPersonAccount(body);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //修改昵称返回
    @Override
    public void resModifyPersonAccount(BaseResponse bean) {
        if(bean==null){
            showToast("修改失败!");
            return;
        }
        if (bean.getHeader().getHttp_code().equals("200")) {
            //发送修改昵称以便他处修改同步
            RxBus.getInstance().post(new ModifyAliars(editName));
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_ALAIRS, editName);
            if (alertDialog != null)
                alertDialog.dismiss();
            hideLoading();
            showToast("修改成功!");
        } else {
            hideLoading();
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog(null);
    }
}
