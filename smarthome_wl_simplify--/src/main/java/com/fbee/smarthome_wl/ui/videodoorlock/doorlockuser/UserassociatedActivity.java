package com.fbee.smarthome_wl.ui.videodoorlock.doorlockuser;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.UserAsDeviceAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.ModefyDeviceUserNum;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.ui.usermanage.DeviceAssociateUserContract;
import com.fbee.smarthome_wl.ui.usermanage.DeviceAssociateUserPresenter;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static com.fbee.smarthome_wl.common.AppContext.getMap;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.uid;

public class UserassociatedActivity extends BaseActivity<DeviceAssociateUserContract.Presenter> implements DeviceAssociateUserContract.View  {
    private LinearLayout activityDeviceAssociateUser;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private LinearLayout rlNodata;
    private ImageView ivNodata;
    private TextView tvNodata;
    private ListView lvDeviceAssociateUser;
    private String  uuid;
    private UserAsDeviceAdapter mAdapter;
    private String primaryId;
    private boolean modifyTag01=false;
    private boolean modifyTag02=false;
    private List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> mData;
    private QueryDeviceUserResponse.BodyBean.DeviceUserListBean selectUser,primaryUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_associate_user);
    }

    @Override
    protected void initView() {
        activityDeviceAssociateUser = (LinearLayout) findViewById(R.id.activity_device_associate_user);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        rlNodata = (LinearLayout) findViewById(R.id.rl_nodata);
        ivNodata = (ImageView) findViewById(R.id.iv_nodata);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);
        lvDeviceAssociateUser = (ListView) findViewById(R.id.lv_device_associate_user);

        createPresenter(new DeviceAssociateUserPresenter(this));
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("完成");
        tvRightMenu.setOnClickListener(this);
        title.setText("关联用户");
    }

    @Override
    protected void initData() {
        uuid = getIntent().getStringExtra("deviceid");
        primaryId=getIntent().getStringExtra("primaryUserId");
        mData = new ArrayList<>();
        mAdapter = new UserAsDeviceAdapter(this,mData);
        lvDeviceAssociateUser.setAdapter(mAdapter);
        reqQueryDevice();
    }


    /**
     * 请求查询设备信息
     */
    private void reqQueryDevice(){
        QueryDeviceuserlistReq body=new QueryDeviceuserlistReq();
        body.setVendor_name(FactoryType.GENERAL);
        body.setUuid(uuid);
        presenter.reqQueryDevice(body);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                ArrayMap<Integer,Boolean> map=mAdapter.getMap();
                selectUser =null;
                for(Integer key: map.keySet()){
                    if(!map.get(key))continue;
                    if(map.get(key)){
                        selectUser=mData.get(key);
                        if(primaryId==null){
                            //请求添加用户设备
                            reqAddDeviceUser();
                        }else{
                            if(selectUser.getId().equals(primaryId)){
                                ToastUtils.showShort("操作成功!");
                                finish();
                            }else{
                                //请求修改用户设备
                                reqModifyDeviceUser();
                            }
                        }
                        break;
                    }
                }
                if(selectUser==null){
                    if(primaryId!= null){
                        //请求修改用户设备
                        reqModifyDeviceUser(primaryUser);
                    }else{
                        finish();
                    }
                }
                break;

        }
    }


    /**
     * 请求请求修改设备用户
     * @param primaryUser
     */
    private void  reqModifyDeviceUser( QueryDeviceUserResponse.BodyBean.DeviceUserListBean primaryUser){
        if(primaryUser.getId().isEmpty()){
            ToastUtils.showShort("操作失败");
            return;
        }
        //删除设备用户
        AddDeviceUser body01=new AddDeviceUser();
        body01.setVendor_name(FactoryType.GENERAL);
        body01.setUuid(uuid);
        AddDeviceUser.DeviceUserBean deviceUserBean01=new AddDeviceUser.DeviceUserBean();
        deviceUserBean01.setId(String.valueOf(primaryUser.getId()));
        deviceUserBean01.setNote(primaryUser.getNote());
        List<String> noticeList01=new ArrayList<>();
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums01=AppContext.getMap().get(String.valueOf(uid));
        if(userNums01!=null){
            for (int i = 0; i <userNums01.size() ; i++) {
                if(userNums01.get(i).getId().equals(String.valueOf(primaryUser.getId()))){
                    noticeList01.addAll(userNums01.get(i).getWithout_notice_user_list());
                }
            }
        }
        noticeList01.remove(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        deviceUserBean01.setWithout_notice_user_list(noticeList01);
        body01.setDevice_user(deviceUserBean01);
        presenter.reqAddDeviceUser(body01);
    }




    /**
     * 请求添加用户设备
     */
    private void reqAddDeviceUser(){
        if(selectUser.getId().isEmpty()){
            ToastUtils.showShort("操作失败");
            return;
        }
        AddDeviceUser body=new AddDeviceUser();
        body.setVendor_name(FactoryType.GENERAL);
        body.setUuid(uuid);
        AddDeviceUser.DeviceUserBean deviceUserBean=new AddDeviceUser.DeviceUserBean();
        deviceUserBean.setId(selectUser.getId());
        deviceUserBean.setNote(selectUser.getNote());
        List<String> noticeList=new ArrayList<>();
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums=AppContext.getMap().get(String.valueOf(uid));
        if(userNums!=null){
            for (int i = 0; i <userNums.size() ; i++) {
                if(userNums.get(i).getId().equals(selectUser.getId())){
                    if(userNums.get(i).getWithout_notice_user_list()!=null){
                        noticeList.addAll(userNums.get(i).getWithout_notice_user_list());
                    }
                }
            }
        }
        noticeList.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        deviceUserBean.setWithout_notice_user_list(noticeList);
        body.setDevice_user(deviceUserBean);
        presenter.reqAddDeviceUser(body);

    }





    @Override
    public void resAddDeviceUser(BaseResponse bean) {
        if(bean.getHeader().getHttp_code().equals("200")){

            //只添加操作
            if(selectUser!=null&&primaryId==null){

                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums= getMap().get(uuid);
                if(userNums!=null){
                    boolean tag=false;
                    for (int i = 0; i <userNums.size() ; i++) {
                        if(userNums.get(i).getId().equals(selectUser.getId())){
                            tag=true;
                            if(userNums.get(i).getWithout_notice_user_list()!=null){
                                if(!userNums.get(i).getWithout_notice_user_list().contains(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME))){
                                    userNums.get(i).getWithout_notice_user_list().add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                                }
                            }else{
                                List<String> without_notice_user_list=new ArrayList<>();
                                without_notice_user_list.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                                userNums.get(i).setWithout_notice_user_list(without_notice_user_list);
                            }
                            break;
                        }
                    }
                    if(!tag){
                        QueryDeviceUserResponse.BodyBean.DeviceUserListBean info = new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                        info.setId(String.valueOf(selectUser.getId()));
                        if(selectUser.getNote()==null||"".equals(selectUser.getNote())){
                            info.setNote(String.valueOf(selectUser.getId()));
                        }else {
                            info.setNote(selectUser.getNote());
                        }
                        List<String> without_notice_user_list=new ArrayList<>();
                        without_notice_user_list.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                        info.setWithout_notice_user_list(without_notice_user_list);
                        userNums.add(info);
                    }
                }
                showToast("操作成功!");
                RxBus.getInstance().post(new ModefyDeviceUserNum(String.valueOf(selectUser.getId())));
                finish();

            }
            //只删除操作
            else if(selectUser==null&&primaryId!=null){

                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums01=AppContext.getMap().get(String.valueOf(uid));
                if(userNums01!=null){
                    for (int i = 0; i <userNums01.size() ; i++) {
                        if(userNums01.get(i).getId().equals(primaryUser.getId())){
                            if(userNums01.get(i).getWithout_notice_user_list()!=null){
                                userNums01.get(i).getWithout_notice_user_list().remove(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                                break;
                            }
                        }
                    }
                }
                showToast("操作成功!");
                RxBus.getInstance().post(new ModefyDeviceUserNum());
                finish();
            }


        }else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }





    /**
     * 请求修改设备用户
     */
    private void reqModifyDeviceUser(){
        if(primaryUser.getId().isEmpty()||selectUser.getId().isEmpty()){
            ToastUtils.showShort("操作失败");
            return;
        }
        //删除设备用户
        AddDeviceUser body01=new AddDeviceUser();
        body01.setVendor_name("feibee");
        body01.setUuid(uuid);
        AddDeviceUser.DeviceUserBean deviceUserBean01=new AddDeviceUser.DeviceUserBean();
        deviceUserBean01.setId(primaryUser.getId());
        deviceUserBean01.setNote(primaryUser.getNote());
        List<String> noticeList01=new ArrayList<>();
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums01=AppContext.getMap().get(String.valueOf(uid));
        if(userNums01!=null){
            for (int i = 0; i <userNums01.size() ; i++) {
                if(userNums01.get(i).getId().equals(String.valueOf(primaryUser.getId()))){
                    noticeList01.addAll(userNums01.get(i).getWithout_notice_user_list());
                }
            }
        }
        noticeList01.remove(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        deviceUserBean01.setWithout_notice_user_list(noticeList01);
        body01.setDevice_user(deviceUserBean01);


        //添加设备用户
        AddDeviceUser body02=new AddDeviceUser();
        body02.setVendor_name("feibee");
        body02.setUuid(uuid);
        AddDeviceUser.DeviceUserBean deviceUserBean02=new AddDeviceUser.DeviceUserBean();
        deviceUserBean02.setId(String.valueOf(selectUser.getId()));
        deviceUserBean02.setNote(selectUser.getNote());
        List<String> noticeList02=new ArrayList<>();
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums02=AppContext.getMap().get(String.valueOf(uid));
        if(userNums02!=null){
            for (int i = 0; i <userNums02.size() ; i++) {
                if(userNums02.get(i).getId().equals(String.valueOf(selectUser.getId()))){
                    if(userNums02.get(i).getWithout_notice_user_list()!=null){
                        noticeList02.addAll(userNums02.get(i).getWithout_notice_user_list());
                    }
                }
            }
        }
        noticeList02.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        deviceUserBean02.setWithout_notice_user_list(noticeList02);
        body02.setDevice_user(deviceUserBean02);

        presenter.reqModifyDeviceUser(body01,body02);

    }





    /**
     * 设备用户
     * @param bean
     */
    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {
        hideLoadingDialog();
        if(bean.getHeader().getHttp_code().equals("200")){
            mData.clear();
            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> data = bean.getBody().getDevice_user_list();
            if(null == data)
                return;
            if(data.size()>0){
                rlNodata.setVisibility(View.GONE);
            }
            mData.addAll(data);
            mAdapter.initMap();
            if(primaryId !=null){
                for (int i = 0; i <data.size() ; i++) {
                    if(primaryId.equals(data.get(i).getId())){
                        primaryUser=data.get(i);
                       mAdapter.getMap().put(i,true);
                        break;
                    }
                }
            }
            AppContext.getMap().put(uuid, data);
            mAdapter.notifyDataSetChanged();

        }else{
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    @Override
    public void resModifyDeviceUser(BaseResponse bean) {
        //删除设备用户
        if("1001".equals(bean.getHeader().getSeq_id())){
            if("200".equals(bean.getHeader().getHttp_code())){
                modifyTag01=true;
                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums01=AppContext.getMap().get(String.valueOf(uid));
                if(userNums01!=null){
                    for (int i = 0; i <userNums01.size() ; i++) {
                        if(userNums01.get(i).getId().equals(String.valueOf(primaryUser.getId()))){
                            if(userNums01.get(i).getWithout_notice_user_list()!=null){
                                userNums01.get(i).getWithout_notice_user_list().remove(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                                break;
                            }
                        }
                    }
                }

            }else{
                ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
            }
        }

        //添加设备用户
        else if("1002".equals(bean.getHeader().getSeq_id())){
            if("200".equals(bean.getHeader().getHttp_code())){
                modifyTag02=true;
                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums02=AppContext.getMap().get(String.valueOf(uid));
                if(userNums02!=null){
                    for (int i = 0; i <userNums02.size() ; i++) {
                        if(userNums02.get(i).getId().equals(String.valueOf(selectUser.getId()))){
                            if(userNums02.get(i).getWithout_notice_user_list()!=null){
                                userNums02.get(i).getWithout_notice_user_list().add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                                break;
                            }else{
                                List<String> without_notice_user_list=new ArrayList<>();
                                without_notice_user_list.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                                userNums02.get(i).setWithout_notice_user_list(without_notice_user_list);
                                break;
                            }
                        }
                    }
                }
            }else{
                ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
            }
        }

        if(modifyTag01==true&&modifyTag02==true){
            showToast("操作成功!");
            RxBus.getInstance().post(new ModefyDeviceUserNum(String.valueOf(selectUser.getId())));
            finish();
        }

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
