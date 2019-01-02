package com.fbee.smarthome_wl.ui.scenario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.ScenarioAddDeviceAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.SenceData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 添加设备到场景
 * @class name：com.fbee.smarthome_wl.ui.scenario
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/11 10:41
 */
public class AddDevicetoScenarioActivity extends BaseActivity implements ScenarioAddDeviceAdapter.OnListFragmentInteractionListener{
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RecyclerView rcAdddevice;
    private LinearLayout rlNodata;

    private ScenarioAddDeviceAdapter adapter;
    private List<DeviceInfo> mValues;
    private List<DeviceInfo> mDeviceRes;
    private List<SenceData> mResultList;
//    private List<SenceData> selectedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddevice_to_scenario);
    }

    @Override
    protected void initView() {
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        rcAdddevice = (RecyclerView) findViewById(R.id.rc_adddevice);
        rlNodata = (LinearLayout) findViewById(R.id.rl_nodata);
    }

    @Override
    protected void initData() {
        mCompositeSubscription  = getCompositeSubscription();
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
//        selectedList= (List<SenceData>) getIntent().getSerializableExtra("selected");
        showLoadingDialog(null);
        title.setText("添加设备");
        tvRightMenu.setText("完成");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        mResultList = new ArrayList<>();
        mDeviceRes = new ArrayList<>();
        mValues =new ArrayList<>();
        adapter = new ScenarioAddDeviceAdapter(mValues,this);
        rcAdddevice.setLayoutManager(new LinearLayoutManager(this));
        rcAdddevice.setAdapter(adapter);


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
                        if (mValues.size() == 0) {
                            hideLoadingDialog();
                        }
                    }
                },onErrorAction);
        mCompositeSubscription.add(subscription1);


        //注册RXbus
        mSubscription = RxBus.getInstance().toObservable(DeviceInfo.class)
                .compose(TransformUtils.<DeviceInfo>defaultSchedulers())
                .subscribe(new Action1<DeviceInfo>() {
                    @Override
                    public void call(DeviceInfo event) {
                        hideLoadingDialog();
                        if(null == event)
                            return;
                        try {

                            //门锁,场景面板，传感器
                            if(event.getDeviceId() == DeviceList.DEVICE_ID_DOOR_LOCK
                                    || event.getDeviceId() == DeviceList.DEVICE_ID_SWITCH_SCENE
                                    ||event.getDeviceId() == DeviceList.DEVICE_ID_SENSOR
                                    ||event.getDeviceId() ==DeviceList.DEVICE_ID_THTB2){
                                return;
                            }

                            for (int i = 0; i < mValues.size(); i++) {
                                if (mValues.get(i).getUId()==event.getUId()) {
                                    return;
                                }

                            }
//                            if(null != selectedList){
//                                for (int i = 0; i <selectedList.size(); i++) {
//                                   if(selectedList.get(i).getuId()==event.getUId()){
//                                       return;
//                                   }
//                                }
//                            }
                            if(rlNodata.getVisibility()==View.VISIBLE){
                                rlNodata.setVisibility(View.GONE);
                            }
                            mValues.add(event);
                            adapter.notifyDataSetChanged();

                        }catch(Exception e){

                        }

                    }
                },onErrorAction);
        mCompositeSubscription.add(mSubscription);
        AppContext.getInstance().getSerialInstance().getDevices();



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right_menu:
                Intent intent = new Intent();
                if(mDeviceRes.size()>0){
                    for (int i = 0; i <mDeviceRes.size() ; i++) {
                        SenceData data=new SenceData();
                        data.setuId(mDeviceRes.get(i).getUId());
                        data.setDeviceId(mDeviceRes.get(i).getDeviceId());
                        data.setData1(mDeviceRes.get(i).getDeviceState());
                        mResultList.add(data);
                    }
                    intent.putExtra("Result", (Serializable) mResultList);
                    setResult(RESULT_OK,intent);
                }
                finish();
                break;
            case R.id.back:
                finish();
                break;

        }

    }

    @Override
    public void onListFragmentInteraction(CompoundButton compoundButton, boolean b, int position) {
        if(b){
            mDeviceRes.add(mValues.get(position));
        }else{
            mDeviceRes.remove(mValues.get(position));
        }
    }


}
