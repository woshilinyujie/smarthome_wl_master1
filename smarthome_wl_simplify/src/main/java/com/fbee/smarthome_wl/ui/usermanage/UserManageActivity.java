package com.fbee.smarthome_wl.ui.usermanage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.doorlocklog.UserManagerAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.dbutils.UserDbUtil;
import com.fbee.smarthome_wl.event.ModefyDeviceUserNum;
import com.fbee.smarthome_wl.greendao.User;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class UserManageActivity extends BaseActivity<UserManageContract.Presenter> implements UserManageContract.View {
    private RecyclerView recyclerView;
    private ImageView back;
    private TextView title;
    private List<User> datas;
    private String deviceId="deviceid";
    private String deviceIeee="deviceIeee";
    private int uid;
    private UserManagerAdapter adapter;
    private AlertDialog alertDialog;
    private TextView guanlianUserText;
    private String userPhone;
    private RelativeLayout relativeGuanlian;
    private String PRIMARYUSERID="primaryUserId";
    private int primaryUserId=-1;
    private User newUser;
    private int itemPosition=-1;
    private String mDeviceIeee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);
    }

    @Override
    protected void initView() {
        recyclerView= (RecyclerView) findViewById(R.id.recycler_user_manage);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        guanlianUserText = (TextView) findViewById(R.id.guanlian_user_text);
        relativeGuanlian = (RelativeLayout) findViewById(R.id.relative_guanlian);
    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new UserManagePresenter(this));
        title.setText("用户");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        relativeGuanlian.setOnClickListener(this);
        datas=new ArrayList<>();
        uid=getIntent().getExtras().getInt(deviceId);
        mDeviceIeee=getIntent().getExtras().getString(deviceIeee);
        userPhone=PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        if (userPhone==null) return;
        showLoadingDialog(null);
        //请求查询设备信息
        reqQueryDevice();
        guanlianUserText.setText("暂无关联用户");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new UserManagerAdapter(this,datas);
        adapter.setOnItemClickLitener(new BaseRecylerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                itemPosition=position;
                showCustomizeDialog(position);
            }
        });
        recyclerView.setAdapter(adapter);
        //接收绑定用户改变
        receiveDeviceUserChange();

        //四秒钟如果请求没返回loading取消
        dismissLoading();

    }


    /**
     * 四秒钟如果请求没返回loading取消
     */
    private void dismissLoading(){
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        Subscription subscription1 = Observable.timer(4000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        hideLoadingDialog();
                    }
                },onErrorAction);
        mCompositeSubscription.add(subscription1);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
            break;
            //关联用户
            case R.id.relative_guanlian:
                Intent intent=new Intent(this,DeviceAssociateUserActivity.class);
                intent.putExtra(deviceId,uid);
                intent.putExtra(deviceIeee,mDeviceIeee);
                intent.putExtra(PRIMARYUSERID,primaryUserId);
                startActivity(intent);
                break;
        }
    }

    /**
     * 接收绑定用户改变
     */
    private void receiveDeviceUserChange() {
        Subscription subHint= RxBus.getInstance().toObservable(ModefyDeviceUserNum.class)
                .compose(TransformUtils.<ModefyDeviceUserNum>defaultSchedulers())
                .subscribe(new Action1<ModefyDeviceUserNum>() {
                    @Override
                    public void call(ModefyDeviceUserNum event) {
                        if(event.getUserNum()==null){
                            guanlianUserText.setText("暂无关联用户");
                            primaryUserId=-1;
                        }else{
                            for(int k=0;k<datas.size();k++){
                                if(datas.get(k)==null)continue;
                                if(event.getUserNum().equals(String.valueOf(datas.get(k).getUserid()))){
                                    if(datas.get(k).getUseralias()==null||datas.get(k).getUseralias().isEmpty()){
                                        guanlianUserText.setText(event.getUserNum()+"号用户");
                                    }else{
                                        guanlianUserText.setText(datas.get(k).getUseralias());
                                    }
                                    break;
                                }
                            }
                            primaryUserId=Integer.parseInt(event.getUserNum());
                        }
                    }
                });
        mCompositeSubscription.add(subHint);
    }

    /**
     *  修改用户名称弹出对话框
     */
    private void showCustomizeDialog(final int position) {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name,null);
        TextView title= (TextView) dialogView.findViewById(R.id.tv_title);
        title.setText("修改用户名称");
        TextView tvDialogHint = (TextView) dialogView.findViewById(R.id.tv_dialog_hint);
        tvDialogHint.setVisibility(View.VISIBLE);
        final EditText editText= (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        editText.setText(datas.get(position).getUseralias());
        editText.setSelection(datas.get(position).getUseralias().length());
        TextView cancleText= (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText= (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editName = editText.getText().toString().trim();
                if (editName == null || editName.isEmpty()) {
                    ToastUtils.showShort("用户名不能为空!");
                    return;
                }
                for (int i = 0; i < datas.size(); i++) {
                    if (i != position) {
                        if (editName.equals(datas.get(i).getUseralias())) {
                            ToastUtils.showShort("用户名已存在，请重新输入!");
                            return;
                        }
                    }
                }
                newUser = new User(datas.get(itemPosition).getUserid(), uid, editName);
                if(alertDialog!=null)
                    alertDialog.dismiss();
                showLoadingDialog(null);

                //三秒钟如果请求没返回loading取消
                dismissLoading();

                //用户别名改变通知服务器
                if(!String.valueOf(datas.get(position).getUserid()).isEmpty()){
                    reqAddDeviceUser(String.valueOf(datas.get(position).getUserid()),editName);
                }else {
                    ToastUtils.showShort("操作失败");
                    return;
                }
            }

        });
        cancleText.setOnClickListener(new View.OnClickListener() {
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
     * 请求查询设备信息
     */
    private void reqQueryDevice(){
        QueryDeviceuserlistReq body=new QueryDeviceuserlistReq();
        body.setVendor_name(FactoryType.FBEE);
        body.setUuid(mDeviceIeee);
        body.setShort_id(String.valueOf(uid));
        presenter.reqQueryDevice(body);
    }

    /**
     * 查询设备信息返回
     * @param bean
     */
    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {

        if(bean==null){
            LogUtil.e("请求返回：","====bean==null=====");
            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = new ArrayList<>();
            List<User> userList= UserDbUtil.getIns().queryAllUsersByUid(uid);
            if(userList!=null){
                for (int i = 0; i <userList.size() ; i++) {
                    QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean=new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                    deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                    deviceUserListBean.setNote(userList.get(i).getUseralias());
                    bodyEntities.add(deviceUserListBean);
                }
                datas.addAll(userList);
                adapter.notifyDataSetChanged();
                AppContext.getMap().put(String.valueOf(uid), bodyEntities);
            }

            hideLoadingDialog();
            return;
        }

        if(bean.getHeader().getHttp_code().equals("200")){
            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = bean.getBody().getDevice_user_list();
            List<User> resUserList=new ArrayList<>();
            if(bodyEntities!=null){
                for (int i = 0; i <bodyEntities.size() ; i++) {
                    User user=new User();
                    user.setDeviceid(uid);
                    user.setUserid(Long.parseLong(bodyEntities.get(i).getId()));
                    if(bodyEntities.get(i).getNote()==null||bodyEntities.get(i).getNote().equals("")){
                        user.setUseralias(bodyEntities.get(i).getId()+"号用户");
                    }else{
                        user.setUseralias(bodyEntities.get(i).getNote());
                    }
                    resUserList.add(user);
                    UserDbUtil.getIns().insert(user);
                    List<String> phoneList=bodyEntities.get(i).getWithout_notice_user_list();
                    if(phoneList!=null&&phoneList.size()>0){
                        for (int j = 0; j <phoneList.size() ; j++) {
                            if(userPhone.equals(phoneList.get(j))){
                                primaryUserId=Integer.parseInt(bodyEntities.get(i).getId());
                                if(bodyEntities.get(i).getNote()==null||bodyEntities.get(i).getNote().isEmpty()){
                                    guanlianUserText.setText(bodyEntities.get(i).getId()+"号用户");
                                }else{
                                    guanlianUserText.setText(bodyEntities.get(i).getNote());
                                }
                                break;
                            }
                        }
                    }
                }


                List<User> userList= UserDbUtil.getIns().queryAllUsersByUid(uid);
                if(userList!=null&&userList.size()>0) {
                    if(resUserList.size()>0){
                        for (int i = 0; i <userList.size() ; i++) {
                            boolean tag=false;
                            for (int j = 0; j <resUserList.size() ; j++) {
                                if(userList.get(i).getUserid()==resUserList.get(j).getUserid()){
                                    tag=true;
                                    break;
                                }
                            }
                            if(!tag){
                                QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean=new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                                deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                                deviceUserListBean.setNote(userList.get(i).getUseralias());
                                bodyEntities.add(deviceUserListBean);
                            }
                        }

                        /*for (int i = 0; i <resUserList.size() ; i++) {
                            boolean tag=false;
                            for (int j = 0; j <userList.size() ; j++) {
                                if(resUserList.get(i).getUserid()==userList.get(j).getUserid()&&resUserList.get(i).getUseralias().equals(userList.get(j).getUseralias())){
                                    tag=true;
                                    break;
                                }
                            }
                            if(!tag){
                                //存到本地数据库
                                UserDbUtil.getIns().insert(resUserList.get(i));
                            }
                        }*/
                        datas.addAll(userList);
                    }else{
                        for (int i = 0; i <userList.size() ; i++) {
                            QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean=new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                            deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                            deviceUserListBean.setNote(userList.get(i).getUseralias());
                            bodyEntities.add(deviceUserListBean);
                        }
                        datas.addAll(userList);
                    }

                    adapter.notifyDataSetChanged();

                    AppContext.getMap().put(String.valueOf(uid), bodyEntities);
                }/*else{
                    if(resUserList.size()>0){

                        for (int i = 0; i <resUserList.size() ; i++) {
                            //存到本地数据库
                            UserDbUtil.getIns().insert(resUserList.get(i));
                        }
                        datas.addAll(resUserList);
                        adapter.notifyDataSetChanged();
                        AppContext.getMap().put(String.valueOf(uid), bodyEntities);
                    }
                }*/

            }else{
                bodyEntities = new ArrayList<>();
                List<User> userList= UserDbUtil.getIns().queryAllUsersByUid(uid);
                if(userList!=null){
                    for (int i = 0; i <userList.size() ; i++) {
                        QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean=new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                        deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                        deviceUserListBean.setNote(userList.get(i).getUseralias());
                        bodyEntities.add(deviceUserListBean);
                    }
                    datas.addAll(userList);
                    adapter.notifyDataSetChanged();
                    AppContext.getMap().put(String.valueOf(uid), bodyEntities);
                }
            }

        }else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
        hideLoadingDialog();
    }


    /**
     * 请求添加用户设备
     */
    public void reqAddDeviceUser(String userId,String aliars){
        AddDeviceUser body=new AddDeviceUser();
        body.setVendor_name("feibee");
        body.setUuid(mDeviceIeee);
        AddDeviceUser.DeviceUserBean deviceUserBean=new AddDeviceUser.DeviceUserBean();
        deviceUserBean.setId(userId);
        deviceUserBean.setNote(aliars);
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums=AppContext.getMap().get(String.valueOf(uid));
        List<String> noticeList=new ArrayList<>();
        if(userNums!=null){
            for (int i = 0; i <userNums.size() ; i++) {
                if(userNums.get(i).getId().isEmpty()) continue;
                if(userNums.get(i).getId().equals(userId)){
                    if(userNums.get(i).getWithout_notice_user_list()!=null){
                        noticeList.addAll(userNums.get(i).getWithout_notice_user_list());
                        break;
                    }
                }
            }
        }

        deviceUserBean.setWithout_notice_user_list(noticeList);
        body.setDevice_user(deviceUserBean);
        presenter.reqAddDeviceUser(body);

    }



    @Override
    public void resAddDeviceUser(BaseResponse bean) {
        if(bean.getHeader().getHttp_code().equals("200")){
            showToast("操作成功!");

            UserDbUtil.getIns().insert(newUser);

            datas.set(itemPosition, newUser);
            if(primaryUserId==datas.get(itemPosition).getUserid()){
                guanlianUserText.setText(newUser.getUseralias());
            }
            adapter.notifyDataSetChanged();
            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums=AppContext.getMap().get(String.valueOf(uid));
            if(userNums!=null){
                for (int i = 0; i <userNums.size() ; i++) {
                    if(userNums.get(i).getId().equals(String.valueOf(newUser.getUserid()))){
                        userNums.get(i).setNote(newUser.getUseralias());
                    }
                }
            }


        }else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
        hideLoadingDialog();
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }
}
