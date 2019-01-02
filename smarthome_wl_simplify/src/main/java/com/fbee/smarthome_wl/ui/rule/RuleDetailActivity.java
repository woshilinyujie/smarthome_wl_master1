package com.fbee.smarthome_wl.ui.rule;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.event.MyTimerTaskInfo;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.TaskDeviceDetails;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RuleDetailActivity extends BaseActivity {
    private LinearLayout activityAddRule;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private TextView tvRulename;
    private TextView tvDevice;
    private TextView tvCondition;
    private TextView tvScene;

    private TextView tvWeek;
    private TextView tvTime;
    private RelativeLayout rlDeployment;
    private TextView tvDeployment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_detail);
    }

    @Override
    protected void initView() {
        activityAddRule = (LinearLayout) findViewById(R.id.activity_add_rule);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        tvRulename = (TextView) findViewById(R.id.tv_rulename);
        tvDevice = (TextView) findViewById(R.id.tv_device);
        tvCondition = (TextView) findViewById(R.id.tv_condition);
        tvScene = (TextView) findViewById(R.id.tv_scene);

        tvWeek = (TextView) findViewById(R.id.tv_week);
        tvTime = (TextView) findViewById(R.id.tv_time);

        if(AppContext.getmOurDevices().size() ==0){
            AppContext.getInstance().getSerialInstance().getDevices();
        }
        if(AppContext.getmOurScenes().size() ==0){
            AppContext.getInstance().getSerialInstance().getSences();
        }

        rlDeployment = (RelativeLayout) findViewById(R.id.rl_Deployment);
        tvDeployment = (TextView) findViewById(R.id.tv_Deployment);
    }

    @Override
    protected void initData() {
        mCompositeSubscription=new CompositeSubscription();
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        showLoadingDialog(null);
        final String taskname = getIntent().getStringExtra("taskname");
        title.setText(taskname);
        //联动任务
       Subscription lianSubscription = RxBus.getInstance().toObservable(TaskDeviceDetails.class)
                .compose(TransformUtils.<TaskDeviceDetails>defaultSchedulers())
                .subscribe(new Action1<TaskDeviceDetails>() {
                    @Override
                    public void call(TaskDeviceDetails event) {

                        try{
                            if(event.getTaskInfo().getTaskName().equals(taskname)){
                                tvRulename.setText(event.getTaskInfo().getTaskName());
                                for (int i = 0; i <AppContext.getmOurDevices().size() ; i++) {
                                    if(event.getTaskDeviceAction().getuId()== AppContext.getmOurDevices().get(i).getUId()){
                                        tvDevice.setText(AppContext.getmOurDevices().get(i).getDeviceName());
                                        break;
                                    }
                                }
                                //门锁设备
                                if(event.getTaskDeviceAction().getDeviceId() == DeviceList.DEVICE_ID_DOOR_LOCK){
                                    if(event.getTaskDeviceAction().getData1() ==1 ){
                                        tvCondition.setText("开门");
                                    }else{
                                        tvCondition.setText("关门");
                                    }
                                }
                                //传感器设备
                                else if(event.getTaskDeviceAction().getDeviceId() == DeviceList.DEVICE_ID_SENSOR){
                                    for (int i = 0; i <AppContext.getmOurDevices().size() ; i++) {
                                        if(event.getTaskDeviceAction().getuId() == AppContext.getmOurDevices().get(i).getUId()){
                                            switch (AppContext.getmOurDevices().get(i).getZoneType()){
                                                //人体
                                                case DeviceList.SENSOR_ZONE_TYPE_HUMAN:
                                                    tvCondition.setText("人体报警");
                                                    int zoneabel=event.getTaskDeviceAction().getZoneabel();
                                                    setDeploymentText(zoneabel);
                                                    break;
                                                //门磁
                                                case DeviceList.SENSOR_ZONE_TYPE_DOOR:
                                                    tvCondition.setText("门磁报警");
                                                    int z=event.getTaskDeviceAction().getZoneabel();
                                                    setDeploymentText(z);
                                                    break;
                                                //烟雾
                                                case DeviceList.SENSOR_ZONE_TYPE_SMOKE:
                                                    tvCondition.setText("烟雾报警");
                                                    int x=event.getTaskDeviceAction().getZoneabel();
                                                    setDeploymentText(x);
                                                    break;
                                                //水位
                                                case DeviceList.SENSOR_ZONE_TYPE_WATER:
                                                    tvCondition.setText("水位报警");
                                                    int t=event.getTaskDeviceAction().getZoneabel();
                                                    setDeploymentText(t);
                                                    break;

                                            }
                                            break;
                                        }
                                    }



                                }

                                for (int i = 0; i <AppContext.getmOurScenes().size() ; i++) {
                                    if(event.getSceneId()== AppContext.getmOurScenes().get(i).getSenceId()){
                                        tvScene.setText(AppContext.getmOurScenes().get(i).getSenceName());
                                        break;
                                    }
                                }
                                hideLoadingDialog();

                            }

                        }catch(Exception e){

                        }

                    }
                });
        mCompositeSubscription.add(lianSubscription);

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


        //联动定时任务
        mSubscription = RxBus.getInstance().toObservable(MyTimerTaskInfo.class)
                .compose(TransformUtils.<MyTimerTaskInfo>defaultSchedulers())
                .subscribe(new Action1<MyTimerTaskInfo>() {
                    @Override
                    public void call(final MyTimerTaskInfo event) {
                        if(event==null)return;
                        try{
                            if(event.getTaskName().equals(taskname)) {
                                tvWeek.setText("周期");
                                tvTime.setText("时间");

                                tvRulename.setText(event.getTaskName());


                                byte workmode = event.getTimerAction().getWorkMode();

                                StringBuffer buffer = new StringBuffer();
                                if( (workmode &0x01)!= 0){
                                    buffer.append("周一");
                                    buffer.append(" ");
                                }
                                if((workmode &0x02)!= 0){
                                    buffer.append("周二");
                                    buffer.append(" ");
                                }

                                if((workmode &0x04)!= 0){
                                    buffer.append("周三");
                                    buffer.append(" ");
                                }
                                if((workmode &0x08)!= 0){
                                    buffer.append("周四");
                                    buffer.append(" ");
                                }
                                if((workmode &0x10)!= 0){
                                    buffer.append("周五");
                                    buffer.append(" ");
                                }

                                if((workmode &0x20)!= 0){
                                    buffer.append("周六");
                                    buffer.append(" ");
                                }

                                if((workmode &0x40)!= 0){
                                    buffer.append("周日");
                                }
                                tvDevice.setText(buffer);
                                tvCondition.setText(event.getTimerAction().getH()+":"+event.getTimerAction().getM()+":"+event.getTimerAction().getS());
                                for (int i = 0; i <AppContext.getmOurScenes().size() ; i++) {
                                    if(event.getSceneId()== AppContext.getmOurScenes().get(i).getSenceId()){
                                        tvScene.setText(AppContext.getmOurScenes().get(i).getSenceName());
                                        break;
                                    }
                                }
                            }

                        }catch (Exception e ){

                        }

                    }
                });
        mCompositeSubscription.add(mSubscription);

        AppContext.getInstance().getSerialInstance().getTaskInfo(taskname);
    }
    private void setDeploymentText(int zoneabel){
        if(zoneabel==1){
            rlDeployment.setVisibility(View.VISIBLE);
            tvDeployment.setText("布防时");
        }else if(zoneabel==2){
            rlDeployment.setVisibility(View.VISIBLE);
            tvDeployment.setText("撤防时");
        }else if(zoneabel==3){
            rlDeployment.setVisibility(View.VISIBLE);
            tvDeployment.setText("任何时候");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;

        }

    }
}
