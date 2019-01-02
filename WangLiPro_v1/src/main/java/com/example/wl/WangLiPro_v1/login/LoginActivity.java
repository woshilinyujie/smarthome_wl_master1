package com.example.wl.WangLiPro_v1.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.example.wl.WangLiPro_v1.bean.AddGateWayReq;
import com.example.wl.WangLiPro_v1.bean.LocalLoginReq;
import com.example.wl.WangLiPro_v1.bean.LoginResult;
import com.example.wl.WangLiPro_v1.bean.UMSBean;
import com.example.wl.WangLiPro_v1.bean.Ums;
import com.example.wl.WangLiPro_v1.main.MainActivity;
import com.example.wl.WangLiPro_v1.service.BgService;
import com.example.wl.WangLiPro_v1.utils.ApiServices;
import com.example.wl.WangLiPro_v1.utils.RetrofitUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jwl.android.jwlandroidlib.Exception.HttpException;
import com.jwl.android.jwlandroidlib.bean.BaseBean;
import com.jwl.android.jwlandroidlib.bean.IpBean;
import com.jwl.android.jwlandroidlib.bean.UserBean;
import com.jwl.android.jwlandroidlib.http.HttpManager;
import com.jwl.android.jwlandroidlib.httpInter.HttpDataCallBack;
import com.jwl.android.jwlandroidlib.utils.LogHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.READ_PHONE_STATE};
    private ImageView mIconLogin;
    private EditText mUsernameEditLogin;
    private ImageView mPhoneRightLogin;
    private ImageView mImageView2;
    private EditText mPasswordEditLogin;
    private ImageView mPwdEyeCheckedLogin;
    private CheckBox mAutoLoginCheck;
    private Button mButtonLogin;
    private Button mButtonRegister;
    private ApiServices retrofit;
    private SharedPreferences spre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyRecordPermissions(this);
        }
        spre = getSharedPreferences("myuser", Context.MODE_MULTI_PROCESS);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
//        }
    }

    @Override
    protected void initData() {
        retrofit = new RetrofitUtils().getApi(this);
        mIconLogin.setOnClickListener(this);
        mUsernameEditLogin.setOnClickListener(this);
        mPhoneRightLogin.setOnClickListener(this);
        mImageView2.setOnClickListener(this);
        mPasswordEditLogin.setOnClickListener(this);
        mPwdEyeCheckedLogin.setOnClickListener(this);
        mAutoLoginCheck.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
        mButtonRegister.setOnClickListener(this);
    }

    protected void initView() {
        mIconLogin = (ImageView) findViewById(R.id.icon_login);
        mUsernameEditLogin = (EditText) findViewById(R.id.username_edit_login);
        mPhoneRightLogin = (ImageView) findViewById(R.id.phone_right_login);
        mImageView2 = (ImageView) findViewById(R.id.imageView2);
        mPasswordEditLogin = (EditText) findViewById(R.id.password_edit_login);
        mPwdEyeCheckedLogin = (ImageView) findViewById(R.id.pwd_eye_checked_login);
        mAutoLoginCheck = (CheckBox) findViewById(R.id.auto_login_check);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonRegister = (Button) findViewById(R.id.button_register);
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
            case R.id.button_login:
                String userName = mUsernameEditLogin.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
//                    return;
                }
                String userPasw = mPasswordEditLogin.getText().toString();
                if (TextUtils.isEmpty(userPasw)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
//                    return;
                }
                LoginWangLi("17706815219", "123123");
                break;
            case R.id.button_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }

    public static final AtomicInteger mSeqid = new AtomicInteger(1);

    public void LoginWangLi(final String username, String passWord) {
        LocalLoginReq body = new LocalLoginReq();
        body.setUsername(username);
        String psw = SHAEncrypt(passWord);
        Date dt = new Date();
        long second = dt.getTime() / 1000;
        String secondStr = String.valueOf(second);
        String subsecond = secondStr.substring(0, secondStr.length() - 2);
        body.setPassword(SHAEncrypt(psw.substring(psw.length() - 24) + subsecond));
        LocalLoginReq.PhoneBean phoneBean = new LocalLoginReq.PhoneBean();
        phoneBean.setUuid(username);
        phoneBean.setPlatform("android");
        phoneBean.setPlatform_version(android.os.Build.VERSION.RELEASE);
        phoneBean.setEndpoint_type(android.os.Build.MODEL);
        body.setPhone(phoneBean);
        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setSeq_id(mSeqid.getAndIncrement() + "");
        header.setMessage_type("MSG_USER_LOGIN_REQ");
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(body);
        ums.setUMS(umsbean);
        Call<JsonObject> login = retrofit.Ums(ums);
        login.enqueue(new Callback<JsonObject>() {

            private List<LoginResult.BodyBean.GatewayListBean> gateway_list;
            private Boolean isHave = true;

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body1 = response.body();
                JsonObject jsonObj = body1.getAsJsonObject("UMS");
                LoginResult info = new Gson().fromJson(jsonObj.toString(), LoginResult.class);
                List<String> slideshow = info.getBody().getSlideshow();
                AppContext.setMapList(slideshow);
                gateway_list = info.getBody().getGateway_list();
                for (int i = 0; i < gateway_list.size(); i++) {
                    if (username.equals(gateway_list.get(i))) {
                        isHave = false;
                    }
                }
                LoginPro(username, isHave);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "登录失败,请重新登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void LoginPro(final String username, final Boolean isHave) {
        HttpManager.getInstance(LoginActivity.this).login("123456", "123456", AppContext.company, new HttpDataCallBack<UserBean>() {
            @Override
            public void httpDateCallback(final UserBean userBean) {
                if (userBean.getResponse().getCode() == 200) {
                    if (isHave) {
                        AddGateWayReq bodyBean = new AddGateWayReq();
                        bodyBean.setVendor_name("Jiuwanli");
                        bodyBean.setUuid(username);
                        bodyBean.setUsername(username);
                        bodyBean.setPassword("123456");
                        bodyBean.setAuthorization("admin");
                        bodyBean.setNote(username);
                        bodyBean.setVersion("1.0.0");
                        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
                        header.setApi_version("1.0");
                        header.setMessage_type("MSG_GATEWAY_ADD_REQ");
                        header.setSeq_id(mSeqid.getAndIncrement() + "");
                        Ums ums = new Ums();
                        UMSBean umsbean = new UMSBean();
                        umsbean.setHeader(header);
                        umsbean.setBody(bodyBean);
                        ums.setUMS(umsbean);
                        Call<JsonObject> login = retrofit.Ums(ums);
                        login.enqueue(new Callback<JsonObject>() {

                            private String id;
                            private String token;

                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                token = userBean.getData().getToken();
                                id = userBean.getData().getUser().getId();
                                AppContext.setUSERID(id);
                                AppContext.setTOKEN(token);
                                spre.edit().putString("token", token).commit();
                                spre.edit().putString("userId", id).commit();
                                spre.edit().putString("phoneNumber", "123456").commit();
                                getServer();
//                                androidClient();
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("结果", t.getMessage());
                            }
                        });
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "登录失败！",
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

    //登陆成功推送通知后台

    public void androidClient() {
        //取到app的版本号

        HttpManager.getInstance(this).androidClient("1.0", AppContext.getUSERID(), "clientId", "xiaomi", BgService.TOKEN, new HttpDataCallBack<BaseBean>() {
            @Override
            public void httpDateCallback(BaseBean b) {
                if (b.getResponse().getCode() == 200) {
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

    void getServer() {
        HttpManager.getInstance(this).getVoipServer(AppContext.getUSERID(), new HttpDataCallBack<IpBean>() {
            @Override
            public void httpDateCallback(IpBean b) {
                if (b.getResponse().getCode() == 200) {
                    spre.edit().putString("IP", b.getData().getVoipServerIp()).commit();
                    //广播下
                    sendBroadcast(new Intent(BgService.LOGIN_ACTION));

                    Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
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

    /**
     * SHA-256 加密
     *
     * @param strSrc
     * @return
     */
    public static String SHAEncrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            strDes = parseByte2HexStr(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xff);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toLowerCase());
        }
        return sb.toString();
    }
}

