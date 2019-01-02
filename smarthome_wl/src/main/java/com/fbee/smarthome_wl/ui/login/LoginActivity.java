package com.fbee.smarthome_wl.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.LocalLoginReq;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.corecode.AddGatewayActivity;
import com.fbee.smarthome_wl.ui.forgetpassword.ForgetPassWordActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.fbee.smarthome_wl.utils.AppUtil.isMobileNO;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;


/**
 * @class name：com.fbee.smarthome_wl.ui.login
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/2 14:29
 */
public class LoginActivity extends BaseActivity<LoginContract.Presenter> implements LoginContract.View {

    private Button bt_login;
    private Button bt_register;
    private EditText userName_Edit;
    private EditText passWord_Edit;
    private LinearLayout linearForget;
    private CheckBox autoLoginCheck;
    private ImageView pwdEyeCheckedLogin;
    private LinearLayout pwdEyeCheckedLinerLogin;
    private ImageView phoneRightLogin;

    private boolean isVisableTag = false;
    private LoginResult.BodyBean.GatewayListBean gw;
    private String Alarm;
    private String Call;
    private HashSet<String> stringSet;
    private int requestCode = 500;
    private int responseCode = 600;
    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.READ_PHONE_STATE};
    private long queryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            verifyRecordPermissions(this);
        bt_register = (Button) findViewById(R.id.button_register);
        linearForget = (LinearLayout) findViewById(R.id.liner_forget);
        bt_login = (Button) findViewById(R.id.button_login);
        userName_Edit = (EditText) findViewById(R.id.username_edit_login);
        userName_Edit.requestFocus();
        passWord_Edit = (EditText) findViewById(R.id.password_edit_login);
        //自动登录checkBox
        autoLoginCheck = (CheckBox) findViewById(R.id.auto_login_check);
        //密码可否显示
        pwdEyeCheckedLogin = (ImageView) findViewById(R.id.pwd_eye_checked_login);
        pwdEyeCheckedLinerLogin = (LinearLayout) findViewById(R.id.pwd_eye_checked_liner_login);
        phoneRightLogin = (ImageView) findViewById(R.id.phone_right_login);
        createPresenter(new LoginPresenter(this));
        if (PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME) != null && !PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME).equals("")) {
            userName_Edit.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
            userName_Edit.setSelection(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME).length());
            phoneRightLogin.setImageResource(R.mipmap.right_blue_login);

        } else {

            phoneRightLogin.setImageResource(R.mipmap.error_gray_login);
        }
        if (PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW) != null && !PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW).isEmpty()) {
            passWord_Edit.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW));
        }
    }

    public static void verifyRecordPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_RECORD,
                    1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //密码可否显示明文
            case R.id.pwd_eye_checked_liner_login:
                if (isVisableTag) {
                    pwdEyeCheckedLogin.setImageResource(R.mipmap.open_eye_login);
                    passWord_Edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    pwdEyeCheckedLogin.setImageResource(R.mipmap.close_eye_login);
                    passWord_Edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isVisableTag = !isVisableTag;
                break;
            //登录
            case R.id.button_login:
                if (!AppUtil.isNetworkAvailable(this)) {
                    showToast("当前无网络，请连接网络！");
                    return;
                }
                String userName = userName_Edit.getText().toString().trim();
                String passWord = passWord_Edit.getText().toString().trim();

                //登录本地服务器
                if (isUserNameOrPassWordRight(userName, passWord)) {
                    showLoadingDialog();
                    LocalLoginReq body = new LocalLoginReq();
                    // body.setGlobal_roaming(RequestConstant.Global_CHINA);
                    body.setUsername(userName);

                    String psw = Aes256EncodeUtil.SHAEncrypt(passWord);
                    Date dt = new Date();
                    long second = dt.getTime() / 1000;
                    queryTime = second;
                    String secondStr = String.valueOf(second);
                    String subsecond = secondStr.substring(0, secondStr.length() - 2);
                    body.setPassword(Aes256EncodeUtil.SHAEncrypt(psw.substring(psw.length() - 24) + subsecond));
//                    body.setPassword(MD5Tools.MD5(passWord));
                    LocalLoginReq.PhoneBean phoneBean = new LocalLoginReq.PhoneBean();
                    phoneBean.setUuid(AppUtil.getIMEI(this));
                    phoneBean.setPlatform("android");
                    phoneBean.setPlatform_version(android.os.Build.VERSION.RELEASE);
                    phoneBean.setNetwork_type(AppUtil.getNetworkType(this));
                    phoneBean.setEndpoint_type(android.os.Build.MODEL);
                    body.setPhone(phoneBean);
                    presenter.loginLocal(body);
                }

                break;
            //忘记密码
            case R.id.liner_forget:
                skipAct(ForgetPassWordActivity.class);
                break;
            //注册
            case R.id.button_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, requestCode);
                //skipAct(RegisterActivity.class);
                break;
        }
    }

    @Override
    public void loginSuccess(Object obj) {

    }


    @Override
    public void loginFbeeResult(int result) {
        switch (result) {
            case 1:
                //保存最后登录的网关信息
                if (null != gw){
                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                    AppContext.setGwSnid(gw.getUuid());
                    AppContext.setVer(gw.getVersion());
                }

                hideLoadingDialog();
                AppContext.getInstance().getSerialInstance().getGateWayInfo();
                skipAct(MainActivity.class);
                finish();
                break;
            case -4:
                showToast("网关登录人数已达到上限!");
                hideLoadingDialog();
                skipAct(MainActivity.class);
                finish();
                break;
            case -2:
                //虚拟网关
                if (gw != null && gw.getUsername().equals(userName_Edit.getText().toString().trim())) {
                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                    hideLoadingDialog();
                    skipAct(MainActivity.class);
                    finish();
                } else {
                    showToast("网关账号或密码错误!");
                    hideLoadingDialog();
                    skipAct(MainActivity.class);
                    finish();
                }
                break;
            case -3:
                showToast("网关登录超时！");
                hideLoadingDialog();
                skipAct(MainActivity.class);
                finish();
                break;
            case -1:
                break;

        }
    }

    /**
     * 本地服务器登录返回
     *
     * @param result
     */
    @Override
    public void loginLocalsuccess(LoginResult result, long time) {
        //本地登录成功
        if ("200".equals(result.getHeader().getHttp_code())) {
            if (null != result && result.getBody() != null) {
                AppContext.getInstance().setSlideshow(result.getBody().getSlideshow());
                AppContext.getInstance().setBodyBean(result.getBody());
                AppContext.getInstance().getBodyBean().setGateway_list(result.getBody().getGateway_list());
            }
            //登录成功保存用户名密码
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_USERNAME, userName_Edit.getText().toString().trim());
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_PSW, passWord_Edit.getText().toString().trim());
            //保存用户昵称
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_ALAIRS, result.getBody().getUser_alias());
            //保存用户secret_key
            PreferencesUtils.saveString(PreferencesUtils.SECRET_KEY, result.getBody().getSecret_key());
            if (result.getBody().getGateway_list() != null && result.getBody().getGateway_list().size() > 0) {
                gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                List<LoginResult.BodyBean.GatewayListBean> gateway_list = result.getBody().getGateway_list();
                if (gw != null) {
                    boolean isexist = true;
                    for (int i = 0; i < gateway_list.size(); i++) {
                        if (gateway_list.get(i).getUuid().equals(gw.getUuid())) {
                            isexist = false;
                            break;
                        }
                    }
                    if (isexist) {
                        gw = gateway_list.get(0);
                        PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                    }
                }
            } else {
                PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), null);
            }
//            icvss.equesUserLogOut();
            //最后一次登录的网关
            if (null != gw && !AppUtil.isMobileNO(gw.getUsername())) {
                presenter.loginFbee(gw.getUsername(), gw.getPassword());
            } else {
                if (result.getBody().getGateway_list() != null && result.getBody().getGateway_list().size() > 0) {
                    gw = result.getBody().getGateway_list().get(0);
                    PreferencesUtils.saveObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME), gw);
                    if (AppUtil.isMobileNO(gw.getUsername()) && result.getBody().getGateway_list().size() > 0) {
                        Api.jpushSetting(this, gw.getUsername());
                        skipAct(MainActivity.class);
                    } else {
                        presenter.loginFbee(gw.getUsername(), gw.getPassword());
                    }
                } else {
                    //账号下没有网关
                    skipAct(AddGatewayActivity.class);
                    finish();
                }
            }
            //移康猫眼登录
            if (gw != null && gw.getUsername() != null) {
                icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, gw.getUsername(), EquesConfig.APPKEY);
                Api.jpushSetting(this, gw.getUsername());
            }

        } else {
            hideLoadingDialog();
            if (Math.abs(queryTime - time / 1000) > 3 * 60) {
                ToastUtils.showShort("登录失败，请检查手机时间是否已校准");
            } else {
                ToastUtils.showShort(RequestCode.getRequestCode(result.getHeader().getReturn_string()));
            }
        }
    }


    //判断手机号或者密码是否为空
    public boolean isUserNameOrPassWordRight(String user_name, String pass) {
        if (user_name.isEmpty()) {
            ToastUtils.showShort("手机号不能为空!");
            userName_Edit.requestFocus();
            return false;
        } else {
            if (!isMobileNO(user_name)) {
                ToastUtils.showShort("请输入正确的手机号!");
                userName_Edit.requestFocus();
                return false;
            } else {
                if (pass.isEmpty()) {
                    ToastUtils.showShort("密码不能为空!");
                    passWord_Edit.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == requestCode && this.responseCode == resultCode) {
            String userName = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
            String passWord = PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW);
            userName_Edit.setText(userName);
            passWord_Edit.setText(passWord);
        }
    }

    @Override
    protected void initData() {
        linearForget.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        pwdEyeCheckedLinerLogin.setOnClickListener(this);

        //账号内容监听
        userName_Edit.addTextChangedListener(new TextWatcher() {
            CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
                if (isMobileNO(charSequence.toString())) {

                    phoneRightLogin.setImageResource(R.mipmap.right_blue_login);
                } else {

                    phoneRightLogin.setImageResource(R.mipmap.error_gray_login);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //初始化自动登录状态
        if (PreferencesUtils.getBoolean(PreferencesUtils.ISAUTOLOGIN)) {
            autoLoginCheck.setChecked(true);
        } else {
            autoLoginCheck.setChecked(false);
        }
        //自动登录监听
        autoLoginCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferencesUtils.saveBoolean(PreferencesUtils.ISAUTOLOGIN, true);

                } else {
                    PreferencesUtils.saveBoolean(PreferencesUtils.ISAUTOLOGIN, false);

                }
            }
        });


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
