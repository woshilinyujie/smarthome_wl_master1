package com.fbee.smarthome_wl.ui.doorlocklog;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.doorlocklog.DoorLockLogAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.HintCountInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.dbutils.DoorLockDbUtil;
import com.fbee.smarthome_wl.dbutils.UserDbUtil;
import com.fbee.smarthome_wl.greendao.Doorlockrecord;
import com.fbee.smarthome_wl.greendao.User;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.BadgeUtil;
import com.fbee.smarthome_wl.utils.DateUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogDeteleRecord;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * 门锁开锁记录界面
 *
 * @class name：com.fbee.smarthome_wl.ui.doorlocklog
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/3 9:32
 */
public class DoorLockLogActivity extends BaseActivity<DoorLockLogContract.Presenter> implements DoorLockLogContract.View, OnRefreshListener, OnLoadMoreListener {
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private LinearLayout rlNodata;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView swipeTarget;


    private List<Doorlockrecord> mDatas;

    private DoorLockLogAdapter adapter;
    private String mUserName;
    private String mPassword;
    private int mDeviceUid;
    private String limitNumber = "20";
    private String mDeviceName;
    private String deviceName = "devicename";
    private String deviceId = "deviceid";
    private String deviceIeee="deviceIeee";
    private Long mStartTime;
    private Long mEndTime;
    private String lastItemTime;
    private boolean isRefresh;
    private int timeChoseTag;
    private DialogDeteleRecord dialog;
    private String mDeviceIeee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scenario);
    }

    @Override
    protected void initView() {
        initApi();
        createPresenter(new DoorlockLogPresenter(this));
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeTarget = (RecyclerView) findViewById(R.id.swipe_target);
        rlNodata = (LinearLayout) findViewById(R.id.rl_nodata);
    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("门锁记录");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("删除");
        tvRightMenu.setOnClickListener(this);
        mDeviceName = getIntent().getExtras().getString(deviceName);
        mDeviceIeee=getIntent().getExtras().getString(deviceIeee);
        mDeviceUid = getIntent().getExtras().getInt(deviceId);
        String json = getIntent().getExtras().getString("json");
        if (json!=null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                String uuid = jsonObject.optString("uuid");
                mDeviceUid= Integer.parseInt(uuid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        showLoadingDialog(null);
        //请求查询设备信息
        reqQueryDevice();

        //3秒钟之后do hideloding
        someMinuateLaterDoSth();

        mStartTime = DateUtil.getLastThreeDayTime();
        mEndTime = DateUtil.getCurrentTime();
        mDatas = new ArrayList<>();
        adapter = new DoorLockLogAdapter(this, mDatas);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(this));
        swipeTarget.setAdapter(adapter);
        //获取当前网关
        LoginResult.BodyBean.GatewayListBean gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
        if (gw == null) {
            return;
        }
        mUserName = gw.getUsername();
        mPassword = gw.getPassword();
        isRefresh = true;
        //获取门锁网络数据
        getNetRecord(String.valueOf(mStartTime / 1000L), String.valueOf(mEndTime / 1000L));
        //接收小红点数量改变刷新记录
        receiveHintCount();
    }

    /**
     * 3秒钟之后do
     */
    private void someMinuateLaterDoSth(){
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
                        if (mDatas.size() == 0) {
                           hideLoadingDialog();
                        }
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);
    }

    /**
     * 请求查询设备信息
     */
    private void reqQueryDevice(){
        QueryDeviceuserlistReq body=new QueryDeviceuserlistReq();
        body.setVendor_name("feibee");
        body.setUuid(mDeviceIeee);
        presenter.reqQueryDevice(body);
    }
    /**
     * 接收小红点数量改变刷新记录
     */
    private void receiveHintCount() {
        //接收小红点改变DoorlockLogMsgChangeEvent
        Subscription subHint = RxBus.getInstance().toObservable(HintCountInfo.class)
                .compose(TransformUtils.<HintCountInfo>defaultSchedulers())
                .subscribe(new Action1<HintCountInfo>() {
                    @Override
                    public void call(HintCountInfo event) {
                        //1时不做处理
                        if(event.getIsNeedReceive()==1)return;
                        if (event.getUid() == mDeviceUid) {

                            //降序排列后的数组
                            List<Doorlockrecord> dbDatas = DoorLockDbUtil.getIns().getDoorLockRecordByUid(mDeviceUid);
                            //刷新页面
                            if(dbDatas!=null&&dbDatas.size()>0){
                                if(rlNodata.getVisibility()==View.VISIBLE){
                                    rlNodata.setVisibility(View.GONE);
                                }
                                mDatas.add(0,dbDatas.get(0));
                                if(adapter!=null)
                                    adapter.notifyDataSetChanged();
                            }

//                            mDatas.clear();
//                            adapter.clearItems();
//                            mStartTime = DateUtil.getLastThreeDayTime();
//                            mEndTime = DateUtil.getCurrentTime();
//                            isRefresh=true;
//                            getNetRecord(String.valueOf(mStartTime / 1000L), String.valueOf(mEndTime / 1000L));
                        }

                        //hint红点置位
                        //retHint();
                    }
                });
        mCompositeSubscription.add(subHint);
    }


    //hint红点置位
    private void retHint() {
        //消息红点置位为零
        try {
            String jsString = PreferencesUtils.getString(MainActivity.DOORMESSAGECOUNT);
            // ===============如果jsonArray不为空，有消息的门锁，在设备详情页不再显示红点
            if (!jsString.equals("[]")) {
                JSONArray jsonArrayGet = new JSONArray(jsString);
                for (int i = 0; i < jsonArrayGet.length(); i++) {
                    JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
                    if (jsonObjectGet.optInt("uid") == mDeviceUid) {
                        // ==============更新该门锁记录
                        remove(i, jsonArrayGet);// 必须先删除,防止得到的消息数量一直为0
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("uid", mDeviceUid);
                        jsonObject.put("messageCount", 0);
                        jsonArrayGet.put(jsonObject);
                        PreferencesUtils.saveString(MainActivity.DOORMESSAGECOUNT,
                                jsonArrayGet.toString());
                        RxBus.getInstance().post(new HintCountInfo(mDeviceUid,1));
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // app icon消息数量显示置位
        notifyBadgeNumber();
    }

    //JSONArray删除条目
    public void remove(int positon, JSONArray array) throws Exception {
        if (positon < 0)
            return;
        Field valuesField = JSONArray.class.getDeclaredField("values");
        valuesField.setAccessible(true);
        List<Object> values = (List<Object>) valuesField.get(array);
        if (positon >= values.size())
            return;
        values.remove(positon);
    }

    // app icon消息数量显示
    public void notifyBadgeNumber() {
        try {
            String jsString = PreferencesUtils.getString(MainActivity.DOORMESSAGECOUNT);
            int count = 0;
            if (jsString != null) {
                JSONArray jsonArrayGet = new JSONArray(jsString);
                for (int i = 0; i < jsonArrayGet.length(); i++) {
                    JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
                    count = count + jsonObjectGet.optInt("messageCount");
                }
            }
            BadgeUtil.setBadgeCount(this, count,
                    Build.MANUFACTURER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //刷新页面
       // mDatas.clear();
       // adapter.clearItems();
        //mStartTime = DateUtil.getLastThreeDayTime();
       // mEndTime = DateUtil.getCurrentTime();


        //请求查询设备信息
        //reqQueryDevice();
        //isRefresh=true;
        //getNetRecord(String.valueOf(mStartTime / 1000L), String.valueOf(mEndTime / 1000L));

        //三秒钟do  hidloading
        //someMinuateLaterDoSth();
        //hint红点置位
        //retHint();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                //hint红点置位
                retHint();
                finish();
                break;
            case R.id.tv_right_menu:
                dialog = new DialogDeteleRecord(this, new DialogDeteleRecord.OnItemClickListener() {
                    @Override
                    public void onItemClickback(long endTime,int timeChoseTag) {
                        DoorLockLogActivity.this.timeChoseTag=timeChoseTag;
                        deteleRecord(endTime);
                    }
                });
                dialog.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //hint红点置位
        retHint();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //hint红点置位
        retHint();
    }

    /**
     * 获取互联网数据
     */
    public void getNetRecord(final String mStartTime, final String mEndTime) {
        HashMap paramsMap = new HashMap<String, String>();
        paramsMap.put("userNo", mUserName);
        paramsMap.put("userPass", mPassword);
        paramsMap.put("type", "8");
        paramsMap.put("uid", mDeviceUid + "");
        paramsMap.put("start", "100");
        paramsMap.put("end", mEndTime);
        paramsMap.put("limit", limitNumber);
        showLoadingDialog();
        presenter.getDoorInfo(paramsMap, mDeviceUid, mDeviceName,mDeviceIeee);
    }


    private void deteleRecord(final long time) {
        HashMap paramsMap = new HashMap<String, String>();
        paramsMap.put("userNo", mUserName);
        paramsMap.put("userPass", mPassword);
        paramsMap.put("type", "8");
        paramsMap.put("uid", mDeviceUid + "");
        paramsMap.put("start", "100");
        paramsMap.put("end", String.valueOf(time));
        showLoadingDialog();
        presenter.deteleDoorinfo(paramsMap);
    }


    /**
     * 获取门锁消息返回
     *
     * @param data
     */
    @Override
    public void resDoorInfoData(List<Doorlockrecord> data) {

        if (data!=null&&data.size() > 0) {
            Collections.sort(data);
            try{
                List<Doorlockrecord> lis = new ArrayList<>();
                if (data.size() > 20) {
                    for (int i = 0; i <20 ; i++) {
                        lis.add(data.get(i));
                    }
                    mDatas.addAll(lis);
                } else {
                    mDatas.addAll(data);
                }
            }catch(Exception e){
            }
            //刷新
            if(isRefresh){
                lastItemTime=getDoorLockLogRecordSord(mDeviceUid);
            }
            //加载更多
            else{
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    String time = mDatas.get(mDatas.size() - 1).getTime();
                    lastItemTime = String.valueOf(sdf.parse(time).getTime() / 1000L);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } else {
            //刷新
            if(isRefresh){
                //降序配列后的数组
                List<Doorlockrecord> dbDatas = DoorLockDbUtil.getIns().getDoorLockRecordByUid(mDeviceUid);
                if(dbDatas!=null&&dbDatas.size()>0){

                    if(dbDatas.size()>20){
                        List<Doorlockrecord> dblist=dbDatas.subList(0,20);
                        mDatas.addAll(dblist);
                    }else{
                        mDatas.addAll(dbDatas);
                    }

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        String time = mDatas.get(mDatas.size() - 1).getTime();
                        lastItemTime = String.valueOf(sdf.parse(time).getTime() / 1000L);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }else{
                    ToastUtils.showShort("暂无更多数据!");
                }
            }
            //加载更多
            else{
                ToastUtils.showShort("暂无更多数据!");
            }

        }

        if(mDatas.size()>0){
            rlNodata.setVisibility(View.GONE);
        }else{
            rlNodata.setVisibility(View.VISIBLE);
        }
        if (null != adapter)
            adapter.notifyDataSetChanged();

        hideLoadingDialog();
        onRefreshComplete();

    }

    /**
     * 数据库和服务器数据对比后的数据和最后一条数据的时间
     * @param mdeviceUid
     * @return
     */
    private String getDoorLockLogRecordSord( int mdeviceUid) {

        String lastItemTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            //降序排列后的数组
            List<Doorlockrecord> dbDatas = DoorLockDbUtil.getIns().getDoorLockRecordByUid(mdeviceUid);
            if(dbDatas==null||dbDatas.size()==0){
                String time=mDatas.get(mDatas.size() - 1).getTime();
                lastItemTime = String.valueOf(sdf.parse(time).getTime() / 1000L);
                DoorLockDbUtil.getIns().insertDoorLockRecordList(mDatas);
                return lastItemTime;
            }else{
                String netTime=mDatas.get(0).getTime();
                long netLongTime = sdf.parse(netTime).getTime()/1000;
                for (int i = 0; i <dbDatas.size() ; i++) {
                    String data=dbDatas.get(i).getTime();
                    long lo = sdf.parse(data).getTime() / 1000L;
                    if(netLongTime+10<lo){
                        mDatas.add(dbDatas.get(i));
                    }else{
                        DoorLockDbUtil.getIns().deleteAllDoorLockRecordByUid(mdeviceUid);
                        DoorLockDbUtil.getIns().insertDoorLockRecordList(mDatas);
                        break;
                    }
                }
            }

            Collections.sort(mDatas);

            if (mDatas.size() >20) {
                List<Doorlockrecord> list=mDatas.subList(0,20);
                mDatas=list;
                String strTime=list.get(19).getTime();
                Date date=sdf.parse(strTime);
                long lonTime=date.getTime()/1000;
                lastItemTime = String.valueOf(lonTime);
            } else {
                lastItemTime = String.valueOf(sdf.parse(mDatas.get(mDatas.size()-1).getTime()).getTime() / 1000L);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastItemTime;
    }



    @Override
    public void onLoadMore() {
        isRefresh=false;
        if (lastItemTime != null) {
            getNetRecord(String.valueOf(mStartTime / 1000L), lastItemTime);
        } else {
            ToastUtils.showShort("暂无更多数据!");
            onRefreshComplete();
        }
    }

    @Override
    public void onRefresh() {
        mDatas.clear();
        isRefresh=true;
        adapter.clearItems();
        mStartTime = DateUtil.getLastThreeDayTime();
        mEndTime = DateUtil.getCurrentTime();
        getNetRecord(String.valueOf(mStartTime / 1000L), String.valueOf(mEndTime / 1000L));
    }

    private void onRefreshComplete() {
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }

    /**
     * 获取门锁消息失败
     */
    @Override
    public void resDatafail() {
        hideLoadingDialog();
        onRefreshComplete();
        showToast("获取门锁消息失败");
    }

    @Override
    public void deteleSuccess() {
        //删除数据库数据
        deleteDbDate(timeChoseTag);
        dialog.cancel();
        mDatas.clear();
        mStartTime = DateUtil.getLastThreeDayTime();
        mEndTime = DateUtil.getCurrentTime();
        isRefresh=true;
        getNetRecord(String.valueOf(mStartTime / 1000L), String.valueOf(mEndTime / 1000L));
    }

    /**
     * 删除时间节点前的数据库数据
     * @param timeChoseTag
     */
    private void deleteDbDate(int timeChoseTag){
        long deleteTime=0;
        switch(timeChoseTag){
            //一个月前
            case 1:
                deleteTime= DateUtil.getTodayZeroTime()- DateUtil.getOneDayMillis(30);
                break;

            //三个月前
            case 3:
                deleteTime= DateUtil.getTodayZeroTime()- DateUtil.getOneDayMillis(90);
                break;

            //六个月前
            case 6:
                deleteTime= DateUtil.getTodayZeroTime()- DateUtil.getOneDayMillis(180);
                break;

            //所有记录
            case 10:
                deleteTime=DateUtil.getCurrentTime();
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(new Date(deleteTime));
        DoorLockDbUtil.getIns().deleteDoorLockRecordsTimeAgo(mDeviceUid,dateTime);
    }
    @Override
    public void deteleFail() {
        dialog.cancel();
        showToast("删除失败");
    }


    //查询设备信息返回
    @Override
    public void resQueryDevice(QueryDeviceUserResponse bean) {
        if(bean==null){
            LogUtil.e("请求返回：","====bean==null=====");
            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = new ArrayList<>();
            List<User> userList= UserDbUtil.getIns().queryAllUsersByUid(mDeviceUid);
            if(userList!=null){
                for (int i = 0; i <userList.size() ; i++) {
                    QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean=new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                    deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                    deviceUserListBean.setNote(userList.get(i).getUseralias());
                    bodyEntities.add(deviceUserListBean);
                }
                AppContext.getMap().put(String.valueOf(mDeviceUid), bodyEntities);
            }
//            isRefresh=true;
            //获取门锁网络数据
//            getNetRecord(String.valueOf(mStartTime / 1000L), String.valueOf(mEndTime / 1000L));
            return;
        }
        LogUtil.e("请求返回：",bean.getHeader().getHttp_code()+"");
        if(bean.getHeader().getHttp_code().equals("200")){
            List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> bodyEntities = bean.getBody().getDevice_user_list();
            List<User> resUserList=new ArrayList<>();
            if(bodyEntities!=null){
                for (int i = 0; i <bodyEntities.size() ; i++) {
                    User user=new User();
                    user.setDeviceid(mDeviceUid);
                    user.setUserid(Long.parseLong(bodyEntities.get(i).getId()));
                    if(bodyEntities.get(i).getNote()==null||bodyEntities.get(i).getNote().equals("")){
                        user.setUseralias(bodyEntities.get(i).getId()+"号用户");
                    }else{
                        user.setUseralias(bodyEntities.get(i).getNote());
                    }
                    resUserList.add(user);
                    UserDbUtil.getIns().insert(user);
                }


                List<User> userList= UserDbUtil.getIns().queryAllUsersByUid(mDeviceUid);
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
                                }
                            }
                            if(!tag){
                                //存到本地数据库
                                UserDbUtil.getIns().insert(resUserList.get(i));
                            }
                        }*/
                    }else{
                        for (int i = 0; i <userList.size() ; i++) {
                            QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean=new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                            deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                            deviceUserListBean.setNote(userList.get(i).getUseralias());
                            bodyEntities.add(deviceUserListBean);
                        }
                    }

                    AppContext.getMap().put(String.valueOf(mDeviceUid), bodyEntities);
                }/*else{
                    if(resUserList.size()>0){

                        for (int i = 0; i <resUserList.size() ; i++) {
                            //存到本地数据库
                            UserDbUtil.getIns().insert(resUserList.get(i));
                        }
                        AppContext.getMap().put(String.valueOf(uid), bodyEntities);
                    }
                }*/

            }else{
                bodyEntities = new ArrayList<>();
                List<User> userList= UserDbUtil.getIns().queryAllUsersByUid(mDeviceUid);
                if(userList!=null){
                    for (int i = 0; i <userList.size() ; i++) {
                        QueryDeviceUserResponse.BodyBean.DeviceUserListBean deviceUserListBean=new QueryDeviceUserResponse.BodyBean.DeviceUserListBean();
                        deviceUserListBean.setId(String.valueOf(userList.get(i).getUserid()));
                        deviceUserListBean.setNote(userList.get(i).getUseralias());
                        bodyEntities.add(deviceUserListBean);
                    }
                    AppContext.getMap().put(String.valueOf(mDeviceUid), bodyEntities);
                }
            }

//            isRefresh=true;
//            //获取门锁网络数据
//            getNetRecord(String.valueOf(mStartTime / 1000L), String.valueOf(mEndTime / 1000L));

        }else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
            hideLoadingDialog();
        }




    }


    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog(null);
    }


}
