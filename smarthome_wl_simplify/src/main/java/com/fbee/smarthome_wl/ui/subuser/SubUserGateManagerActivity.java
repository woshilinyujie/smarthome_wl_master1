package com.fbee.smarthome_wl.ui.subuser;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.AddSubUserAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.GateWayChangeInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.constant.RequestConstant;
import com.fbee.smarthome_wl.request.AddChildUserReq;
import com.fbee.smarthome_wl.request.QuerySubUserInfoReq;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QuerySubUserInfoResponse;
import com.fbee.smarthome_wl.utils.AES256Encryption;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RSAUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.SendCodeButton;

import java.util.ArrayList;
import java.util.List;

public class SubUserGateManagerActivity extends BaseActivity<SubUserGateManagerContract.Presenter> implements SubUserGateManagerContract.View  {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ListView listviewGateWay;
    private List<LoginResult.BodyBean.GatewayListBean> mDatas;
    private AddSubUserAdapter adapter;
    private LoginResult.BodyBean.ChildUserListBean childUserListBean;
    private String  USERINFO="userinfo";
    private AlertDialog alertDialog;
    private SendCodeButton dialogSendyanzhengma;
    private EditText dialogYanzhengmaEdit;
    private EditText dialogMmimaEdit;
    private String public_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_user_gate_manager);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        listviewGateWay = (ListView) findViewById(R.id.listview_gateway);
    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new SubUserGateManagerPresenter(this));
        childUserListBean= (LoginResult.BodyBean.ChildUserListBean) getIntent().getSerializableExtra(USERINFO);
        if(childUserListBean==null) return;
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("账号编辑");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("完成");
        tvRightMenu.setOnClickListener(this);

        mDatas= AppContext.getInstance().getBodyBean().getGateway_list();
        if(mDatas==null) return;
        adapter=new AddSubUserAdapter(this,mDatas);
        listviewGateWay.setAdapter(adapter);

        //请求子账号信息
        querySubUserList();
    }
    /**
     * 发送子账号请求
     */
    private void querySubUserList() {

        QuerySubUserInfoReq body=new QuerySubUserInfoReq();
        body.setUsername(childUserListBean.getUsername());
        presenter.reqQuerySubUser(body);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                showCustomizeDialog();
                break;
        }

    }


    /**
     *  编辑网关弹出dialog
     */
    private void showCustomizeDialog() {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_subuser_gate_manager,null);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        tvTitle.setText("网关编辑");

        dialogMmimaEdit = (EditText) dialogView.findViewById(R.id.dialog_mmima_edit);
        dialogYanzhengmaEdit = (EditText)dialogView. findViewById(R.id.dialog_yanzhengma_edit);
        dialogSendyanzhengma = (SendCodeButton) dialogView.findViewById(R.id.dialog_sendyanzhengma);
        TextView tvLeftCancelBtn = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView tvRightConfirmBtn = (TextView)dialogView. findViewById(R.id.tv_right_confirm_btn);

        dialogSendyanzhengma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogSendyanzhengma.isRunning()) {
                    return;
                }
                if(AppUtil.isMobileNO(childUserListBean.getUsername())){
                    SmsReq body = new SmsReq();
                    body.setGlobal_roaming(RequestConstant.Global_CHINA);
                    body.setUsername(childUserListBean.getUsername());
                    showLoadingDialog();
                    presenter.sendMessageCode(body);
                }
            }
        });
        tvRightConfirmBtn.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               if(dialogMmimaEdit.getText().toString().trim()==null){
                                                   showToast("请输入密码");
                                                   return;
                                               }
                                               if(dialogYanzhengmaEdit.getText().length()!=6){
                                                   showToast("请输入6位验证码");
                                                   return;
                                               }

                                               showLoadingDialog();
                                               //请求添加
                                               reqAddSubUser();
                                           }
                                       });
        tvLeftCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alertDialog!=null)
                    alertDialog.dismiss();
            }
        });
        customizeDialog.setView(dialogView);
        alertDialog=customizeDialog.show();
    }


    /**
     * 请求添加子用户
     */
    private void reqAddSubUser() {
        AddChildUserReq body=new AddChildUserReq();
        body.setGlobal_roaming(RequestConstant.Global_CHINA);
        body.setUsername(childUserListBean.getUsername());
        //登录密码
        String psw= Aes256EncodeUtil.SHAEncrypt(dialogMmimaEdit.getText().toString().trim());
        body.setPassword(RSAUtils.encrypt(psw.substring(psw.length()-24),public_key));
//        body.setPassword(MD5Tools.MD5(dialogMmimaEdit.getText().toString().trim()));
        body.setSms_code(dialogYanzhengmaEdit.getText().toString().trim());
        body.setUser_alias(childUserListBean.getUser_alias());
        List<AddChildUserReq.GatewayListBean> gateway_list=new ArrayList<>();
        ArrayMap<Integer,String> selectMap=adapter.getSelectMap();
        for (Integer key:selectMap.keySet()){
            if(selectMap.get(key)!=null){
                AddChildUserReq.GatewayListBean gateBean=new AddChildUserReq.GatewayListBean();
                gateBean.setAuthorization(selectMap.get(key));
                gateBean.setUuid(mDatas.get(key).getUuid());
                gateBean.setVendor_name(mDatas.get(key).getVendor_name());
                gateBean.setUsername(mDatas.get(key).getUsername());
                gateBean.setPassword(AES256Encryption.encrypt(mDatas.get(key).getPassword(),mDatas.get(key).getUuid()));
                gateBean.setVersion(mDatas.get(key).getVersion());
                gateway_list.add(gateBean);
            }
        }
        body.setGateway_list(gateway_list);
        presenter.reqAddSubUser(body);

    }


    /**
     * 添加子账号返回
     * @param bean
     */
    @Override
    public void resAddSubUser(BaseResponse bean) {
        hideLoading();
        if("200".equals(bean.getHeader().getHttp_code())){
            showToast("操作成功");
            if(alertDialog!=null)
                alertDialog.dismiss();
            //发送网关列表改变通知
            RxBus.getInstance().post(new GateWayChangeInfo());
        }else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    /**
     * 请求验证码返回
     * @param resCode
     */
    @Override
    public void resCode(CodeResponse resCode) {
        hideLoading();
        if(resCode.getHeader().getHttp_code().equals("200")){
            dialogSendyanzhengma.sendCode();
            public_key=resCode.getBody().getPublic_key();
        }else {
            showToast("验证码获取失败");
        }

    }

    /**
     * 查询子用户信息返回
     * @param bean
     */
    @Override
    public void resQuerySubUser(QuerySubUserInfoResponse bean) {
        if (bean==null)return;

        if(bean.getHeader().getHttp_code().equals("200")){
            if(bean.getBody().getGateway_list()==null&&bean.getBody().getGateway_list().size()==0)return;
            for (int i = 0; i <bean.getBody().getGateway_list().size() ; i++) {
                for (int j = 0; j <mDatas.size() ; j++) {
                    if(bean.getBody().getGateway_list().get(i).getUuid().equals(mDatas.get(j).getUuid())){
                        adapter.getSelectMap().put(j,bean.getBody().getGateway_list().get(i).getAuthorization());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }


    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
