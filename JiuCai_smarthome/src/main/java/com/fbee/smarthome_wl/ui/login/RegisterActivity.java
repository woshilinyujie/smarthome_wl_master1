package com.fbee.smarthome_wl.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.constant.RequestConstant;
import com.fbee.smarthome_wl.request.RegReq;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RSAUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.SendCodeButton;

import static com.fbee.smarthome_wl.R.id.phonenum_edit_register;
import static com.fbee.smarthome_wl.R.id.yanzhengma_edit_register;

public class RegisterActivity extends BaseActivity<RegisterContract.Presenter> implements RegisterContract.View {
    private LinearLayout activityRegister;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private EditText phonenumEditRegister;
    private ImageView line1Image;
    private LinearLayout linearYanzhengRegister;
    private EditText yanzhengmaEditRegister;
    private SendCodeButton sendyanzhengmaRegister;
    private ImageView line2Image;
    private EditText loginmimaEditRegister;
    private ImageView line3Image;
    private Button registerButton;
    private EditText confirmmimaEditRegister;
    private EditText userAlairsEditRegister;

    private String secret_key;
    private int responseCode=600;
    private String public_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initView() {
        initApi();
        createPresenter(new RegisterPresenter(this));
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        phonenumEditRegister = (EditText) findViewById(phonenum_edit_register);
        yanzhengmaEditRegister = (EditText) findViewById(yanzhengma_edit_register);
        sendyanzhengmaRegister = (SendCodeButton) findViewById(R.id.sendyanzhengma_register);
        loginmimaEditRegister = (EditText) findViewById(R.id.loginmima_edit_register);
        registerButton = (Button) findViewById(R.id.register_button);
        confirmmimaEditRegister = (EditText) findViewById(R.id.confirmmima_edit_register);
        userAlairsEditRegister = (EditText) findViewById(R.id.user_alairs_edit_register);
    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("注册");
        sendyanzhengmaRegister.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            //发送验证码
            case R.id.sendyanzhengma_register:
                if (sendyanzhengmaRegister.isRunning()) {
                    return;
                }
                if(AppUtil.isMobileNO(phonenumEditRegister.getText().toString())){
                    SmsReq body = new SmsReq();
                    body.setGlobal_roaming(RequestConstant.Global_CHINA);
                    body.setUsername(phonenumEditRegister.getText().toString());
                    showLoadingDialog();
                    presenter.sendMessageCode(body);
                }else{
                    showToast("请输入正确的手机号");
                }
                break;
            //发起注册
            case R.id.register_button:
                if(!AppUtil.isMobileNO(phonenumEditRegister.getText().toString())) {
                    showToast("请输入正确的手机号");
                    return;
                }
                if(yanzhengmaEditRegister.getText().length()!=6){
                    showToast("请输入6位验证码");
                    return;
                }
                if(loginmimaEditRegister.getText().length() <6){
                    showToast("密码必须大于等于6位");
                    return;
                }
                if(!confirmmimaEditRegister.getText().toString().trim().equals(loginmimaEditRegister.getText().toString().trim())){
                    showToast("两次密码不匹配");
                    return;
                }
                if(public_key==null){
                    showToast("请获取验证码!");
                    return;
                }
                showLoadingDialog();

                RegReq body = new RegReq();
                body.setGlobal_roaming(RequestConstant.Global_CHINA);
                body.setUsername(phonenumEditRegister.getText().toString().trim());

                String psw=Aes256EncodeUtil.SHAEncrypt(loginmimaEditRegister.getText().toString().trim());
                body.setPassword(RSAUtils.encrypt(psw.substring(psw.length()-24),public_key));
                body.setSms_code(yanzhengmaEditRegister.getText().toString().trim());
                if(userAlairsEditRegister.getText().toString().trim()!=null&&!"".equals(userAlairsEditRegister.getText().toString().trim())){
                    body.setUser_alias(userAlairsEditRegister.getText().toString().trim());
                }
                RegReq.PhoneBean phoneBean=new RegReq.PhoneBean();
                phoneBean.setUuid(AppUtil.getIMEI(this));
                phoneBean.setPlatform("android");
                phoneBean.setPlatform_version(android.os.Build.VERSION.RELEASE);
                phoneBean.setNetwork_type(AppUtil.getNetworkType(this));
                phoneBean.setEndpoint_type(android.os.Build.MODEL);
                body.setPhone(phoneBean);

                presenter.register(body);
                break;

        }
    }

    /**
     * 短信验证码返回
     * @param code
     */
    @Override
    public void resCode(CodeResponse code) {
        hideLoading();
        if(code.getHeader().getHttp_code().equals("200")){
            sendyanzhengmaRegister.sendCode();
            public_key=code.getBody().getPublic_key();
        }else{
            ToastUtils.showShort(RequestCode.getRequestCode(code.getHeader().getReturn_string()));
        }

    }

    /**
     * 请求验证码失败
     */
    @Override
    public void resCodeFail() {
        hideLoading();
        showToast("请求验证码失败");
    }

    /**
     * 注册返回
     * @param bean
     */
    @Override
    public void resRegister(BaseResponse bean) {
        hideLoading();
        //注册成功
        if(bean.getHeader().getHttp_code().equals("200")){

            //存储账户对应秘钥，短信验证码
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_USERNAME,phonenumEditRegister.getText().toString());
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_PSW,loginmimaEditRegister.getText().toString());
//            secret_key =bean.getBody().getSecret_key();
//            PreferencesUtils.saveString(SECRET_KEY,secret_key);
            showToast("注册成功");
            setResult(responseCode,new Intent());
            finish();
        }else {
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
