package com.fbee.smarthome_wl.ui.personaccount;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.ModifyAccountInfo;
import com.fbee.smarthome_wl.bean.ModifyAliars;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.constant.RequestConstant;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.request.UpdateUserinfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.SendCodeButton;

import java.util.Date;

public class ModifyPersonAccountActivity extends BaseActivity<ModifyPersonAccountContract.Presenter> implements ModifyPersonAccountContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private EditText phonenumEditModifyPersonAccount;
    private EditText yanzhengmaEditModifyPersonAccount;
    private SendCodeButton sendyanzhengmaModifyPersonAccount;
    private EditText newaliarsEditModifyPersonAccount;
    private Button confirmButtonModifyPersonAccount;
    private EditText primarymimaEditModifyPersonAccount;
    private  int tag;
    private String modify="modify";
    private int responseCode=400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_person_account);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        phonenumEditModifyPersonAccount = (EditText) findViewById(R.id.phonenum_edit_modify_person_account);
        yanzhengmaEditModifyPersonAccount = (EditText) findViewById(R.id.yanzhengma_edit_modify_person_account);
        sendyanzhengmaModifyPersonAccount = (SendCodeButton) findViewById(R.id.sendyanzhengma_modify_person_account);
        newaliarsEditModifyPersonAccount = (EditText) findViewById(R.id.newaliars_edit_modify_person_account);
        confirmButtonModifyPersonAccount = (Button) findViewById(R.id.confirm_button_modify_person_account);
        primarymimaEditModifyPersonAccount = (EditText) findViewById(R.id.primarymima_edit_modify_person_account);
    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new ModifyPersonAccountPresenter(this));
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("账户编辑");
        confirmButtonModifyPersonAccount.setOnClickListener(this);
        sendyanzhengmaModifyPersonAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
            break;
            //发送验证码
            case R.id.sendyanzhengma_modify_person_account:
                if (sendyanzhengmaModifyPersonAccount.isRunning()) {
                    return;
                }
                if(AppUtil.isMobileNO(phonenumEditModifyPersonAccount.getText().toString())){
                    SmsReq body = new SmsReq();
                    body.setGlobal_roaming(RequestConstant.Global_CHINA);
                    body.setUsername(phonenumEditModifyPersonAccount.getText().toString());
                    showLoadingDialog();
                    presenter.sendMessageCode(body);
                }else{
                    showToast("请输入正确的手机号");
                }
                break;
            //确认
            case R.id.confirm_button_modify_person_account:
                if(primarymimaEditModifyPersonAccount.getText().length() <6){
                    showToast("密码必须大于等于6位");
                    return;
                }
                if(!primarymimaEditModifyPersonAccount.getText().toString().trim().equals(PreferencesUtils.getString(PreferencesUtils.LOCAL_PSW))){
                    showToast("密码不正确");
                    return;
                }
                if(!AppUtil.isMobileNO(phonenumEditModifyPersonAccount.getText().toString().trim())) {
                    showToast("请输入正确的手机号");
                    return;
                }
                if(yanzhengmaEditModifyPersonAccount.getText().length()!=6){
                    showToast("请输入6位验证码");
                    return;
                }

                showLoadingDialog();
                UpdateUserinfoReq body=new UpdateUserinfoReq();
                body.setSafe_update("true");
                //登录密码
                String psw= Aes256EncodeUtil.SHAEncrypt(primarymimaEditModifyPersonAccount.getText().toString().trim());
                Date dt = new Date();
                long second = dt.getTime()/1000;
                String secondStr = String.valueOf(second);
                String subsecond = secondStr.substring(0, secondStr.length() - 2);
                body.setPassword(Aes256EncodeUtil.SHAEncrypt(psw.substring(psw.length()-24)+subsecond));

//                body.setPassword(MD5Tools.MD5(primarymimaEditModifyPersonAccount.getText().toString().trim()));
                body.setNew_username(phonenumEditModifyPersonAccount.getText().toString().trim());
                body.setNew_sms_code(yanzhengmaEditModifyPersonAccount.getText().toString().trim());
                if(!TextUtils.isEmpty(newaliarsEditModifyPersonAccount.getText().toString())){
                    body.setNew_alias(newaliarsEditModifyPersonAccount.getText().toString().trim());
                }
                presenter.reqModifyPersonAccount(body);
                break;
        }
    }

    /**
     * 修改个人账户返回
     * @param bean
     */
    @Override
    public void resModifyPersonAccount(BaseResponse bean) {
        //修改成功
        if(bean.getHeader().getHttp_code().equals("200")){
            //发送用户名修改通知
            RxBus.getInstance().post(new ModifyAccountInfo(phonenumEditModifyPersonAccount.getText().toString().trim()));
            PreferencesUtils.saveString(PreferencesUtils.LOCAL_USERNAME,phonenumEditModifyPersonAccount.getText().toString().trim());
            //PreferencesUtils.saveString(PreferencesUtils.LOCAL_PSW,newmmimaEditModifyPersonAccount.getText().toString().trim());
            if(TextUtils.isEmpty(newaliarsEditModifyPersonAccount.getText().toString())){
                PreferencesUtils.saveString(PreferencesUtils.LOCAL_ALAIRS,newaliarsEditModifyPersonAccount.getText().toString().trim());
                //发送用户昵称修改通知
                RxBus.getInstance().post(new ModifyAliars(newaliarsEditModifyPersonAccount.getText().toString().trim()));
            }
            hideLoading();
            showToast("修改成功!");
        }else {
            hideLoading();
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }


    //请求验证码返回
    @Override
    public void resCode(BaseResponse resCode) {
        hideLoading();
        if(resCode.getHeader().getHttp_code().equals("200"))
            sendyanzhengmaModifyPersonAccount.sendCode();

    }

    //请求验证码失败返回
    @Override
    public void resCodeFail() {
        hideLoading();
        showToast("请求验证码失败");
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
