package com.fbee.smarthome_wl.ui.subuser;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.AddSubUserAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.AddSubUserInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.constant.RequestConstant;
import com.fbee.smarthome_wl.request.AddChildUserReq;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.AES256Encryption;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RSAUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.SendCodeButton;

import java.util.ArrayList;
import java.util.List;

public class AddSubUserActivity extends BaseActivity<AddSubUserContract.Presenter> implements AddSubUserContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private EditText phonenumEditAddSubUser;
    private LinearLayout linearYanzhengAddSubUser;
    private EditText yanzhengmaEditAddSubUser;
    private SendCodeButton sendyanzhengmaAddSubUser;
    private EditText passwordEditAddSubUser;
    private EditText confirmPasswordEditAddSubUser;
    private EditText aliarsEditAddSubUser;
    private ListView listviewEditAddSubUser;
    private List<LoginResult.BodyBean.GatewayListBean> mDatas;
    private AddSubUserAdapter adapter;
    private String secret_key;
    private List<LoginResult.BodyBean.ChildUserListBean> subUserList;
    private String public_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_user);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        phonenumEditAddSubUser = (EditText) findViewById(R.id.phonenum_edit_add_sub_user);
        linearYanzhengAddSubUser = (LinearLayout) findViewById(R.id.linear_yanzheng_add_sub_user);
        yanzhengmaEditAddSubUser = (EditText) findViewById(R.id.yanzhengma_edit_add_sub_user);
        sendyanzhengmaAddSubUser = (SendCodeButton) findViewById(R.id.sendyanzhengma_add_sub_user);
        passwordEditAddSubUser = (EditText) findViewById(R.id.password_edit_add_sub_user);
        confirmPasswordEditAddSubUser = (EditText) findViewById(R.id.confirm_password_edit_add_sub_user);
        aliarsEditAddSubUser = (EditText) findViewById(R.id.aliars_edit_add_sub_user);
        listviewEditAddSubUser = (ListView) findViewById(R.id.listview_edit_add_sub_user);
    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new AddSubUserPresenter(this));
        subUserList=AppContext.getInstance().getBodyBean().getChild_user_list();
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("添加子账号");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("完成");
        tvRightMenu.setOnClickListener(this);
        sendyanzhengmaAddSubUser.setOnClickListener(this);
        mDatas= AppContext.getInstance().getBodyBean().getGateway_list();
        if(mDatas==null) return;
        adapter=new AddSubUserAdapter(this,mDatas);
        listviewEditAddSubUser.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            //发送验证码
            case R.id.sendyanzhengma_add_sub_user:
                if (sendyanzhengmaAddSubUser.isRunning()) {
                    return;
                }
                if(AppUtil.isMobileNO(phonenumEditAddSubUser.getText().toString().trim())){
                    SmsReq body = new SmsReq();
                    body.setGlobal_roaming(RequestConstant.Global_CHINA);
                    body.setUsername(phonenumEditAddSubUser.getText().toString().trim());
                    showLoadingDialog();
                    presenter.sendMessageCode(body);
                }else{
                    showToast("请输入正确的手机号");
                }
                break;

            //完成
            case R.id.tv_right_menu:
                if(!AppUtil.isMobileNO(phonenumEditAddSubUser.getText().toString().trim())){
                    showToast("请输入正确的手机号");
                    return;
                }

                if(passwordEditAddSubUser.getText().toString().trim().isEmpty()){
                    showToast("密码不能为空");
                    return;
                }
                if(confirmPasswordEditAddSubUser.getText().toString().trim().isEmpty()){
                    showToast("请确认密码");
                    return;
                }
                if(!passwordEditAddSubUser.getText().toString().trim().equals(confirmPasswordEditAddSubUser.getText().toString().trim())){
                    showToast("两次密码不匹配");
                    return;
                }
                if(yanzhengmaEditAddSubUser.getText().toString().trim().isEmpty()){
                    showToast("请输入验证码");
                    return;
                }
                ArrayMap<Integer,String> selectMap=adapter.getSelectMap();
                boolean tag=false;
                for (Integer key:selectMap.keySet()) {
                    if(selectMap.get(key)!=null){
                        tag=true;
                        break;
                    }
                }
                if(!tag){
                    showToast("请选择网关");
                    return;
                }
                //请求添加子用户
                reqAddSubUser();
                break;
        }
    }

    /**
     * 请求添加子用户
     */
    private void reqAddSubUser() {
        if(public_key==null){
            showToast("请获取验证码!");
            return;
        }
        AddChildUserReq body=new AddChildUserReq();
        body.setGlobal_roaming(RequestConstant.Global_CHINA);
        body.setUsername(phonenumEditAddSubUser.getText().toString().trim());

        //登录密码
        String psw= Aes256EncodeUtil.SHAEncrypt(passwordEditAddSubUser.getText().toString().trim());
        body.setPassword(RSAUtils.encrypt(psw.substring(psw.length()-24),public_key));
//        body.setPassword(MD5Tools.MD5(passwordEditAddSubUser.getText().toString().trim()));
        body.setSms_code(yanzhengmaEditAddSubUser.getText().toString().trim());
        if(aliarsEditAddSubUser.getText().toString().trim()!=null){
            body.setUser_alias(aliarsEditAddSubUser.getText().toString().trim());
        }
        List<AddChildUserReq.GatewayListBean> gateway_list=new ArrayList<>();
        ArrayMap<Integer,String> selectMap=adapter.getSelectMap();
        for (Integer key:selectMap.keySet()){
            if(selectMap.get(key)!=null){
                AddChildUserReq.GatewayListBean gateBean=new AddChildUserReq.GatewayListBean();
                gateBean.setAuthorization(selectMap.get(key));
                gateBean.setUuid(mDatas.get(key).getUuid().toLowerCase());
                gateBean.setVendor_name(mDatas.get(key).getVendor_name());
                gateBean.setUsername(mDatas.get(key).getUsername());
                gateBean.setPassword(AES256Encryption.encrypt(mDatas.get(key).getPassword(),mDatas.get(key).getUuid().toLowerCase()));
                gateBean.setVersion(mDatas.get(key).getVersion());
                gateway_list.add(gateBean);
            }
        }
        body.setGateway_list(gateway_list);
        presenter.reqAddSubUser(body);

    }


    //添加子用户返回
    @Override
    public void resAddSubUser(BaseResponse bean) {
        if(bean.getHeader().getHttp_code().equals("200")){
            showToast("添加成功");
            LoginResult.BodyBean.ChildUserListBean subUser=new LoginResult.BodyBean.ChildUserListBean();
            subUser.setUsername(phonenumEditAddSubUser.getText().toString().trim());
            subUser.setUser_alias(aliarsEditAddSubUser.getText().toString().trim());
            AppContext.getInstance().getBodyBean().getChild_user_list().add(subUser);
            RxBus.getInstance().post(new AddSubUserInfo());
            finish();
        }else{
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }
    //返回验证码
    @Override
    public void resCode(CodeResponse resCode) {
        hideLoading();
        if(resCode.getHeader().getHttp_code().equals("200")){
            sendyanzhengmaAddSubUser.sendCode();
            public_key=resCode.getBody().getPublic_key();
        }else{
            ToastUtils.showShort(RequestCode.getRequestCode(resCode.getHeader().getReturn_string()));
        }


    }
    //返回验证码失败
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
