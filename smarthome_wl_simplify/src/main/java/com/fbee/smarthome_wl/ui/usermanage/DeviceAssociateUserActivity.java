package com.fbee.smarthome_wl.ui.usermanage;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.DecviceAssociateUserAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.dbutils.UserDbUtil;
import com.fbee.smarthome_wl.event.ModefyDeviceUserNum;
import com.fbee.smarthome_wl.greendao.User;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.common.AppContext.getMap;

public class DeviceAssociateUserActivity extends BaseActivity<DeviceAssociateUserContract.Presenter> implements DeviceAssociateUserContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private LinearLayout rlNodata;

    private String deviceId="deviceid";
    private String deviceIeee="deviceIeee";
    private ListView lvDeviceAssociateUser;
    private List<User> datas;
    private int uid;
    private DecviceAssociateUserAdapter adapter;
    private String PRIMARYUSERID="primaryUserId";
    private long primaryId=-1;
    private User selectUser;
    private User primaryUser;
    private boolean modifyTag01=false;
    private boolean modifyTag02=false;
    private String userPhone;
    private String mDeviceIeee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_associate_user);

    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        lvDeviceAssociateUser = (ListView) findViewById(R.id.lv_device_associate_user);
        rlNodata = (LinearLayout) findViewById(R.id.rl_nodata);
    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new DeviceAssociateUserPresenter(this));
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("完成");
        tvRightMenu.setOnClickListener(this);
        title.setText("关联用户");
        datas=new ArrayList<>();
        userPhone=PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        primaryId=getIntent().getIntExtra(PRIMARYUSERID,-1);
        uid=getIntent().getIntExtra(deviceId,-1);
        mDeviceIeee=getIntent().getStringExtra(deviceIeee);
        if (mDeviceIeee==null)return;
        if(uid==-1) return;
        showLoadingDialog(null);
        adapter=new DecviceAssociateUserAdapter(this,datas);
        lvDeviceAssociateUser.setAdapter(adapter);
        //请求查询设备信息
        reqQueryDevice();

        //三秒钟如果没有返回do
        delayDoing();

        lvDeviceAssociateUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapter.getMap().get(i)){
                    for (Integer key:adapter.getMap().keySet()){

                        adapter.getMap().put(key,false);

                    }
                }else{
                    for (Integer key:adapter.getMap().keySet()){
                        if(key==i){
                            adapter.getMap().put(i,true);
                        }else {
                            adapter.getMap().put(key,false);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void delayDoing(){
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        Subscription subscription1 = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        hideLoadingDialog();
                        if (datas.size() == 0) {
                            hideLoadingDialog();
                        }
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                ArrayMap<Integer,Boolean> map=adapter.getMap();
                selectUser =null;
                for(Integer key: map.keySet()){
                    if(!map.get(key))continue;
                    if(map.get(key)){
                        selectUser=datas.get(key);
                        if(primaryId==-1){

                            //请求添加用户设备
                            reqAddDeviceUser();

                        }else{
                            if(selectUser.getUserid()==primaryId){
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
                    if(primaryId!=-1){
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
     * 请求查询设备信息
     */
    private void reqQueryDevice(){
        QueryDeviceuserlistReq body=new QueryDeviceuserlistReq();
        body.setVendor_name("feibee");
        body.setUuid(mDeviceIeee);
        body.setShort_id(String.valueOf(uid));
        presenter.reqQueryDevice(body);
    }

    /**
     * 请求添加用户设备
     */
    private void reqAddDeviceUser(){
        if(String.valueOf(selectUser.getUserid()).isEmpty()){
            ToastUtils.showShort("操作失败");
            return;
        }
        AddDeviceUser body=new AddDeviceUser();
        body.setVendor_name("feibee");
        body.setUuid(mDeviceIeee);
        AddDeviceUser.DeviceUserBean deviceUserBean=new AddDeviceUser.DeviceUserBean();
        deviceUserBean.setId(String.valueOf(selectUser.getUserid()));
        deviceUserBean.setNote(selectUser.getUseralias());
        List<String> noticeList=new ArrayList<>();
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums=AppContext.getMap().get(String.valueOf(uid));
        if(userNums!=null){
            for (int i = 0; i <userNums.size() ; i++) {
                if(userNums.get(i).getId().equals(String.valueOf(selectUser.getUserid()))){
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

    /**
     * 请求请求修改设备用户
     * @param primaryUser
     */
    private void  reqModifyDeviceUser(User primaryUser){
        if(String.valueOf(primaryUser.getUserid()).isEmpty()){
            ToastUtils.showShort("操作失败");
            return;
        }
        //删除设备用户
        AddDeviceUser body01=new AddDeviceUser();
        body01.setVendor_name("feibee");
        body01.setUuid(mDeviceIeee);
        AddDeviceUser.DeviceUserBean deviceUserBean01=new AddDeviceUser.DeviceUserBean();
        deviceUserBean01.setId(String.valueOf(primaryUser.getUserid()));
        deviceUserBean01.setNote(primaryUser.getUseralias());
        List<String> noticeList01=new ArrayList<>();
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums01=AppContext.getMap().get(String.valueOf(uid));
        if(userNums01!=null){
            for (int i = 0; i <userNums01.size() ; i++) {
                if(userNums01.get(i).getId().equals(String.valueOf(primaryUser.getUserid()))){
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
     * 请求修改设备用户
     */
    private void reqModifyDeviceUser(){
        if(String.valueOf(primaryUser.getUserid()).isEmpty()||String.valueOf(selectUser.getUserid()).isEmpty()){
            ToastUtils.showShort("操作失败");
            return;
        }
        //删除设备用户
        AddDeviceUser body01=new AddDeviceUser();
        body01.setVendor_name("feibee");
        body01.setUuid(mDeviceIeee);
        AddDeviceUser.DeviceUserBean deviceUserBean01=new AddDeviceUser.DeviceUserBean();
        deviceUserBean01.setId(String.valueOf(primaryUser.getUserid()));
        deviceUserBean01.setNote(primaryUser.getUseralias());
        List<String> noticeList01=new ArrayList<>();
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums01=AppContext.getMap().get(String.valueOf(uid));
        if(userNums01!=null){
            for (int i = 0; i <userNums01.size() ; i++) {
                if(userNums01.get(i).getId().equals(String.valueOf(primaryUser.getUserid()))){
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
        body02.setUuid(mDeviceIeee);
        AddDeviceUser.DeviceUserBean deviceUserBean02=new AddDeviceUser.DeviceUserBean();
        deviceUserBean02.setId(String.valueOf(selectUser.getUserid()));
        deviceUserBean02.setNote(selectUser.getUseralias());
        List<String> noticeList02=new ArrayList<>();
        List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums02=AppContext.getMap().get(String.valueOf(uid));
        if(userNums02!=null){
            for (int i = 0; i <userNums02.size() ; i++) {
                if(userNums02.get(i).getId().equals(String.valueOf(selectUser.getUserid()))){
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
     * 添加设备用户返回
     * @param bean
     */
    @Override
    public void resAddDeviceUser(BaseResponse bean) {
        if(bean.getHeader().getHttp_code().equals("200")){

            //只添加操作
            if(selectUser!=null&&primaryId==-1){

                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums= getMap().get(String.valueOf(uid));
                /*if(userNums==null){
                    userNums=new ArrayList<>();
                    QueryDeviceUserResponse.BodyBean.DeviceUserListBean info = new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                    info.setId(String.valueOf(selectUser.getUserid()));
                    if(selectUser.getUseralias()==null||"".equals(selectUser.getUseralias())){
                        info.setNote(String.valueOf(selectUser.getUserid()));
                    }else {
                        info.setNote(selectUser.getUseralias());
                    }
                    List<String> without_notice_user_list=new ArrayList<>();
                    without_notice_user_list.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                    info.setWithout_notice_user_list(without_notice_user_list);
                    userNums.add(info);
                    AppContext.getMap().put(String.valueOf(uid), userNums);

                }else*/
                if(userNums!=null){
                    boolean tag=false;
                    for (int i = 0; i <userNums.size() ; i++) {
                        if(userNums.get(i).getId().equals(String.valueOf(selectUser.getUserid()))){
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
                        info.setId(String.valueOf(selectUser.getUserid()));
                        if(selectUser.getUseralias()==null||"".equals(selectUser.getUseralias())){
                            info.setNote(String.valueOf(selectUser.getUserid()));
                        }else {
                            info.setNote(selectUser.getUseralias());
                        }
                        List<String> without_notice_user_list=new ArrayList<>();
                        without_notice_user_list.add(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                        info.setWithout_notice_user_list(without_notice_user_list);
                        userNums.add(info);
                    }
                }
                showToast("操作成功!");
                RxBus.getInstance().post(new ModefyDeviceUserNum(String.valueOf(selectUser.getUserid())));
                finish();

            }
            //只删除操作
            else if(selectUser==null&&primaryId!=-1){

                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums01=AppContext.getMap().get(String.valueOf(uid));
                if(userNums01!=null){
                    for (int i = 0; i <userNums01.size() ; i++) {
                        if(userNums01.get(i).getId().equals(String.valueOf(primaryUser.getUserid()))){
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
     * 查询设备信息返回
     * @param bean
     */
    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {
        if(bean==null){
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
                adapter.initMap();
                if(primaryId!=-1){
                    for (int i = 0; i <datas.size() ; i++) {
                        User user=datas.get(i);
                        if(user.getUserid()==primaryId){
                            primaryUser=user;
                            adapter.getMap().put(i,true);
                            break;
                        }
                    }
                }
                if(datas.size()>0){
                    if(rlNodata.getVisibility()== View.VISIBLE){
                        rlNodata.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    AppContext.getMap().put(String.valueOf(uid), bodyEntities);
                }
            }

            hideLoadingDialog();
            return;
        }
        if(bean.getHeader().getHttp_code().equals("200")){

            if(bean!=null){
                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = bean.getBody().getDevice_user_list();
                List<User> resUserList=new ArrayList<>();

                if(bodyEntities!=null&&bodyEntities.size()>0){
                    for (int i = 0; i <bodyEntities.size() ; i++) {

                        User user=new User();
                        user.setDeviceid(uid);
                        user.setUserid(Long.parseLong(bodyEntities.get(i).getId()));
                        if(bodyEntities.get(i).getNote()==null|| bodyEntities.get(i).getNote().equals("")){
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
                                    primaryId=Long.parseLong(bodyEntities.get(i).getId());
                                    break;
                                }
                            }
                        }
                    }
                    List<User> userList= UserDbUtil.getIns().queryAllUsersByUid(uid);
                    if(userList!=null&&userList.size()>0) {
                        if (resUserList.size() > 0) {
                            for (int i = 0; i < userList.size(); i++) {
                                boolean tag = false;
                                for (int j = 0; j < resUserList.size(); j++) {
                                    if (userList.get(i).getUserid() == resUserList.get(j).getUserid()) {
                                        tag = true;
                                        break;
                                    }
                                }
                                if (!tag) {
                                    QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean = new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                                    deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                                    deviceUserListBean.setNote(userList.get(i).getUseralias());
                                    bodyEntities.add(deviceUserListBean);
                                }
                            }

                        }else{
                            for (int i = 0; i <userList.size() ; i++) {
                                QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean=new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                                deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                                deviceUserListBean.setNote(userList.get(i).getUseralias());
                                bodyEntities.add(deviceUserListBean);
                            }
                        }

                    }
                    datas.addAll(userList);
                    adapter.initMap();
                    if(primaryId!=-1){
                        for (int i = 0; i <datas.size() ; i++) {
                            User user=datas.get(i);
                            if(user.getUserid()==primaryId){
                                primaryUser=user;
                                adapter.getMap().put(i,true);
                                break;
                            }
                        }
                    }
                    if(datas.size()>0){
                        if(rlNodata.getVisibility()==View.VISIBLE){
                            rlNodata.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                        AppContext.getMap().put(String.valueOf(uid), bodyEntities);
                    }

                    hideLoadingDialog();
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
                        adapter.initMap();
                        if(primaryId!=-1){
                            for (int i = 0; i <datas.size() ; i++) {
                                User user=datas.get(i);
                                if(user.getUserid()==primaryId){
                                    primaryUser=user;
                                    adapter.getMap().put(i,true);
                                    break;
                                }
                            }
                        }
                        if(datas.size()>0){
                            if(rlNodata.getVisibility()==View.VISIBLE){
                                rlNodata.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                            AppContext.getMap().put(String.valueOf(uid), bodyEntities);
                        }
                    }
                    hideLoadingDialog();
                }
            }

        }else{
            hideLoadingDialog();
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    /**
     * 修改设备用户返回
     * @param bean
     */
    @Override
    public void resModifyDeviceUser(BaseResponse bean) {
        //删除设备用户
        if("1001".equals(bean.getHeader().getSeq_id())){
            if("200".equals(bean.getHeader().getHttp_code())){
                modifyTag01=true;
                List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> userNums01=AppContext.getMap().get(String.valueOf(uid));
                if(userNums01!=null){
                    for (int i = 0; i <userNums01.size() ; i++) {
                        if(userNums01.get(i).getId().equals(String.valueOf(primaryUser.getUserid()))){
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
                        if(userNums02.get(i).getId().equals(String.valueOf(selectUser.getUserid()))){
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
            RxBus.getInstance().post(new ModefyDeviceUserNum(String.valueOf(selectUser.getUserid())));
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
