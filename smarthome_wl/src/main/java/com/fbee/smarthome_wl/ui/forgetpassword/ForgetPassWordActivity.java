package com.fbee.smarthome_wl.ui.forgetpassword;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.constant.RequestConstant;
import com.fbee.smarthome_wl.request.ForgetPwd;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RSAUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.SendCodeButton;


public class ForgetPassWordActivity extends BaseActivity<ForgetContract.Presenter> implements ForgetContract.View {
    private ImageView back;
    private TextView title;
    private EditText phonenumEditForgetPass;
    private LinearLayout linearYanzhengForgetPass;
    private EditText yanzhengmaEditForgetPass;
    private SendCodeButton sendyanzhengmaForgetPass;
    private EditText newPassEditForgetPass;
    private Button registerButton;
    private EditText confirmPassEditRegister;
    private String secret_key;
    private String public_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);

    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        phonenumEditForgetPass = (EditText) findViewById(R.id.phonenum_edit_forget_pass);
        linearYanzhengForgetPass = (LinearLayout) findViewById(R.id.linear_yanzheng_forget_pass);
        yanzhengmaEditForgetPass = (EditText) findViewById(R.id.yanzhengma_edit_forget_pass);
        sendyanzhengmaForgetPass = (SendCodeButton) findViewById(R.id.sendyanzhengma_forget_pass);
        newPassEditForgetPass = (EditText) findViewById(R.id.new_pass_edit_forget_pass);
        confirmPassEditRegister = (EditText) findViewById(R.id.confirm_pass_edit_forget_pass);
        registerButton = (Button) findViewById(R.id.register_button);
    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new ForgetPresenter(this));
        title.setText("找回密码");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        sendyanzhengmaForgetPass.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
            break;
            //获取验证码
            case R.id.sendyanzhengma_forget_pass:
                if (sendyanzhengmaForgetPass.isRunning()) {
                    return;
                }
                if(AppUtil.isMobileNO(phonenumEditForgetPass.getText().toString())){
                    SmsReq body = new SmsReq();
                    body.setGlobal_roaming(RequestConstant.Global_CHINA);
                    body.setUsername(phonenumEditForgetPass.getText().toString());
                    showLoadingDialog();
                    presenter.sendMessageCode(body);
                }else{
                    showToast("请输入正确的手机号");
                }
            break;
            //提交
            case R.id.register_button:
                if(!AppUtil.isMobileNO(phonenumEditForgetPass.getText().toString().trim())) {
                    showToast("请输入正确的手机号");
                    return;
                }
                if(yanzhengmaEditForgetPass.getText().toString().trim().length()!=6){
                    showToast("请输入6位验证码");
                    return;
                }
                if(newPassEditForgetPass.getText().toString().trim().length() <6){
                    showToast("密码必须大于等于6位");
                    return;
                }
                if(!newPassEditForgetPass.getText().toString().trim().equals(confirmPassEditRegister.getText().toString().trim())){
                    showToast("两次密码不匹配");
                    return;
                }
                if(null == public_key){
                    showToast("请获取验证码!");
                    return;
                }
                showLoadingDialog();
                ForgetPwd body=new ForgetPwd();
                body.setUsername(phonenumEditForgetPass.getText().toString().trim());

                //登录密码
                String psw=Aes256EncodeUtil.SHAEncrypt(newPassEditForgetPass.getText().toString().trim());

                body.setNew_password(RSAUtils.encrypt(psw.substring(psw.length()-24),public_key));

//                body.setNew_password(MD5Tools.MD5(newPassEditForgetPass.getText().toString().trim()));
                body.setSms_code(yanzhengmaEditForgetPass.getText().toString().trim());
                presenter.findPassWord(body);
            break;
        }
    }

    @Override
    public void resCode(CodeResponse resCode) {
        hideLoading();
        if(resCode.getHeader().getHttp_code().equals("200"))
            sendyanzhengmaForgetPass.sendCode();
            public_key = resCode.getBody().getPublic_key();

    }

    @Override
    public void resCodeFail() {
        hideLoading();
        showToast("请求验证码失败");
    }

    @Override
    public void resForgetPassWord(BaseResponse bean) {
        hideLoadingDialog();
        //注册成功
        if(bean.getHeader().getHttp_code().equals("200")){
            showToast("密码找回成功");
//            ArrayMap<String, String> map = new ArrayMap<String, String>();
//            if(!TextUtils.isEmpty(secret_key))
//                map.put("secret_key",secret_key);
//            String smscode = yanzhengmaEditForgetPass.getText().toString().trim();
//            if(!TextUtils.isEmpty(smscode))
//                map.put("smscode",smscode);
            String phone = phonenumEditForgetPass.getText().toString().trim();
            String pass=newPassEditForgetPass.getText().toString().trim();
//            if(!TextUtils.isEmpty(phone))
//                //存储账户对应秘钥，短信验证码
//                PreferencesUtils.saveObject(phone,map);
            //存储用户名密码
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_USERNAME,phone);
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_PSW,pass);
            finish();
        }
        else{
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
