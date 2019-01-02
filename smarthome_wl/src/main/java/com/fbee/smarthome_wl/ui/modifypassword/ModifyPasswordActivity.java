package com.fbee.smarthome_wl.ui.modifypassword;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.ActivityPageManager;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.ModifyPasswordReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.ui.login.LoginActivity;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.zllctl.Serial;

import java.util.Date;

public class ModifyPasswordActivity extends BaseActivity<ModifyContract.Presenter> implements ModifyContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private EditText primaryPass;
    private EditText newPass;
    private EditText confiryPass;
    private Button confiryButton;
    private String newP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

    }

    @Override
    protected void initView() {
        initApi();
        createPresenter(new Modifypresenter(this));
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        primaryPass = (EditText) findViewById(R.id.primary_pass);
        newPass = (EditText) findViewById(R.id.new_pass);
        confiryPass = (EditText) findViewById(R.id.confiry_pass);
        confiryButton = (Button) findViewById(R.id.confiry_button);
    }

    @Override
    protected void initData() {
        title.setText("重置密码");
        back.setVisibility(View.VISIBLE);
        confiryButton.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //请求修改
            case R.id.confiry_button:
                String localPsw = PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW);
                String pripass = primaryPass.getText().toString().trim();
                newP = newPass.getText().toString().trim();
                String confirmP = confiryPass.getText().toString().trim();
                if (pripass.isEmpty()) {
                    showToast("原密码不能为空!");
                    return;
                }
                if (!pripass.equals(localPsw)) {
                    showToast("原密码不正确!");
                    return;
                }
                if(newP.isEmpty()){
                    showToast("新密码不能为空!");
                    return;
                }
                if(confirmP.isEmpty()){
                    showToast("请确认密码!");
                    return;
                }
                if (!newP.equals(confirmP)) {
                    showToast("两次密码不匹配!");
                    return;
                }
                showLoadingDialog("请稍后...");
                ModifyPasswordReq body = new ModifyPasswordReq();

                //登录密码
                String psw = Aes256EncodeUtil.SHAEncrypt(localPsw);
                Date dt = new Date();
                long second = dt.getTime() / 1000;
                String secondStr = String.valueOf(second);
                String subsecond = secondStr.substring(0, secondStr.length() - 2);
                body.setPassword(Aes256EncodeUtil.SHAEncrypt(psw.substring(psw.length() - 24) + subsecond));
//                body.setPassword(MD5Tools.MD5(localPsw));

                //新密码
                String psw1 = Aes256EncodeUtil.SHAEncrypt(newP);
                body.setNew_password(psw1.substring(psw1.length() - 24));

//                body.setNew_password(MD5Tools.MD5(newP));
                presenter.sendModifyPassCode(body);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void resModifyPass(BaseResponse bean) {
        hideLoadingDialog();
        if (bean != null) {
            if (bean.getHeader().getHttp_code().equals("200")) {
                showToast("修改成功!");
                PreferencesUtils.saveString(PreferencesUtils.LOCAL_PSW,newP);
                //清除AppContext中的缓存数据
                AppContext.clearAllDatas();
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                //释放资源
                mSerial.releaseSource();
                //移康退出
                icvss.equesUserLogOut();
                ActivityPageManager.finishAllActivity();
                skipAct(LoginActivity.class);
            } else {
                ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
            }
        } else {
            showToast("修改失败!");
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
