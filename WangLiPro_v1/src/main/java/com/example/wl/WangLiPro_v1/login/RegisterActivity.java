package com.example.wl.WangLiPro_v1.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.example.wl.WangLiPro_v1.bean.SmsReq;
import com.example.wl.WangLiPro_v1.bean.UMSBean;
import com.example.wl.WangLiPro_v1.bean.Ums;
import com.example.wl.WangLiPro_v1.utils.ApiServices;
import com.example.wl.WangLiPro_v1.utils.RetrofitUtils;
import com.example.wl.WangLiPro_v1.view.SendCodeButton;
import com.google.gson.JsonObject;
import com.jwl.android.jwlandroidlib.Exception.HttpException;
import com.jwl.android.jwlandroidlib.bean.BaseBean;
import com.jwl.android.jwlandroidlib.http.HttpManager;
import com.jwl.android.jwlandroidlib.httpInter.HttpDataCallBack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    public final String Global_CHINA = "0086";
    private EditText mPhonenumEditRegister;
    private EditText mLoginmimaEditRegister;
    private EditText mConfirmmimaEditRegister;
    private EditText mUserAlairsEditRegister;
    private EditText mYanzhengmaEditRegister;
    private SendCodeButton mSendyanzhengmaRegister;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initData() {
        mPhonenumEditRegister.setOnClickListener(this);
        mLoginmimaEditRegister.setOnClickListener(this);
        mConfirmmimaEditRegister.setOnClickListener(this);
        mUserAlairsEditRegister.setOnClickListener(this);
        mYanzhengmaEditRegister.setOnClickListener(this);
        mSendyanzhengmaRegister.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mPhonenumEditRegister = (EditText) findViewById(R.id.phonenum_edit_register);
        mLoginmimaEditRegister = (EditText) findViewById(R.id.loginmima_edit_register);
        mConfirmmimaEditRegister = (EditText) findViewById(R.id.confirmmima_edit_register);
        mUserAlairsEditRegister = (EditText) findViewById(R.id.user_alairs_edit_register);
        mYanzhengmaEditRegister = (EditText) findViewById(R.id.yanzhengma_edit_register);
        mSendyanzhengmaRegister = (SendCodeButton) findViewById(R.id.sendyanzhengma_register);
        mRegisterButton = (Button) findViewById(R.id.register_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendyanzhengma_register:

                break;
            case R.id.register_button:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        final String registerNumber = mPhonenumEditRegister.getText().toString().trim();
        if (TextUtils.isEmpty(registerNumber) && isMobileNO(registerNumber)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        final String registerPasw = mLoginmimaEditRegister.getText().toString().trim();
        if (TextUtils.isEmpty(registerPasw)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        String confirmPasw = mConfirmmimaEditRegister.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPasw)) {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
            return;
        }

        final String userAlairs = mUserAlairsEditRegister.getText().toString().trim();
        if (TextUtils.isEmpty(userAlairs)) {
            Toast.makeText(this, "请输入用户昵称", Toast.LENGTH_SHORT).show();
            return;
        }

        String verificationCode = mYanzhengmaEditRegister.getText().toString().trim();
        if (TextUtils.isEmpty(verificationCode)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_USER_LOGIN_REQ");
        SmsReq body = new SmsReq();
        body.setGlobal_roaming(Global_CHINA);
        body.setUsername(registerNumber);
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(body);
        ums.setUMS(umsbean);

        Call<JsonObject> ums1 = new RetrofitUtils().getApi(this).Ums(ums);
        ums1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                registWangLiPro(registerNumber, registerPasw, userAlairs);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void registWangLiPro(String registerNumber, String registerPasw, String userAlairs) {
        HttpManager.getInstance(RegisterActivity.this).register(registerNumber, registerPasw, userAlairs, AppContext.company, new HttpDataCallBack<BaseBean>() {
            @Override
            public void httpDateCallback(BaseBean baseBean) {
                if (baseBean.getResponse().getCode() == 200) {
                    Toast.makeText(RegisterActivity.this, "注册成功！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败！",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void httpException(HttpException e) {

            }

            @Override
            public void complet() {

            }
        });
    }

    //判断是否是市场上标准手机号
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
