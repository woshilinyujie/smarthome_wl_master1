package com.fbee.smarthome_wl.ui.rule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.TaskDeviceAction;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class AddRuleActivity extends BaseActivity {
    private LinearLayout activityAddRule;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RelativeLayout rlRuleName;
    private EditText etname;
    private RelativeLayout rlRuleDevice;
    private TextView tvDevice;
    private RelativeLayout rlRuleConditions;
    private Spinner spCondition;
    private RelativeLayout rlRuleScene;
    private TextView tvScene;
    private TextView tvCondition;
    private RelativeLayout rlDeploymentRule;
    private TextView tvDeploymentRule;



    private ArrayList<String> nameList;
    private List<DeviceInfo> devices;
    private List<SenceInfo>  sences;
    private TaskDeviceAction action;
    
    private short SceneID = -1;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);
    }

    @Override
    protected void initView() {
        activityAddRule = (LinearLayout) findViewById(R.id.activity_add_rule);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        rlRuleName = (RelativeLayout) findViewById(R.id.rl_rule_name);
        etname = (EditText) findViewById(R.id.et_rulename);
        rlRuleDevice = (RelativeLayout) findViewById(R.id.rl_rule_device);
        tvDevice = (TextView) findViewById(R.id.tv_device);
        rlRuleConditions = (RelativeLayout) findViewById(R.id.rl_rule_conditions);
        spCondition = (Spinner) findViewById(R.id.sp_condition);
        rlRuleScene = (RelativeLayout) findViewById(R.id.rl_rule_scene);
        tvScene = (TextView) findViewById(R.id.tv_scene);
        tvCondition = (TextView) findViewById(R.id.tv_condition);
        action = new TaskDeviceAction();
        action.setCondition1((byte) 2);
        action.setZoneabel((byte)3);
        action.setZoneArea(0xFFFF);
        rlDeploymentRule = (RelativeLayout) findViewById(R.id.rl_Deployment_rule);
        tvDeploymentRule = (TextView) findViewById(R.id.tv_Deployment_rule);
    }

    @Override
    protected void initData() {
        title.setText("添加规则");
        tvRightMenu.setText("完成");
        back.setVisibility(View.VISIBLE);
        tvRightMenu.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setOnClickListener(this);
        rlDeploymentRule.setOnClickListener(this);
        devices = new ArrayList<>();

        if(AppContext.getmOurDevices().size() ==0){
            AppContext.getInstance().getSerialInstance().getDevices();
        }
        if(AppContext.getmOurScenes().size() ==0){
            AppContext.getInstance().getSerialInstance().getSences();
        }


        showSpinner();
        rlRuleDevice.setOnClickListener(this);
        rlRuleScene.setOnClickListener(this);
    }


    private void showSpinner() {
        String[] mItems = new String[]{"门磁开", "门磁关"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCondition.setAdapter(adapter);
        action.setData1(1);
        spCondition.setSelection(0, true);
        spCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                switch (pos) {
                    case 0:
                        action.setData1(1);
                        break;
                    case 1:
                        action.setData1(0);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }

    public  void getmDoorLockDevices() {
        for (int i = 0; i < AppContext.getmOurDevices().size(); i++) {
            if (AppContext.getmOurDevices().get(i).getDeviceId() == 10 ||
                    AppContext.getmOurDevices().get(i).getDeviceId() == 1026) {
                if(devices.contains(AppContext.getmOurDevices().get(i)))
                    continue;
                devices.add(AppContext.getmOurDevices().get(i));
            }
        }
    }






    static final int DEVICE_RESULT = 1;
    static final int SENCE_RESULT = 2;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_rule_device:
                getmDoorLockDevices();
                nameList= new ArrayList<String>();
                for (int i = 0; i <devices.size() ; i++) {
                    nameList.add(devices.get(i).getDeviceName());
                }
                if(nameList.size()==0){
                    showToast("没有可操作设备");
                    return;
                }
                Intent intent = new Intent(this, ChooseActivity.class);
                intent.putExtra("DataList",nameList);
                intent.putExtra("tag","设备");
                startActivityForResult(intent,DEVICE_RESULT);
                break;
            //创建规则
            case R.id.tv_right_menu:
                createRule();
                break;
            case R.id.rl_rule_scene:
                sences = AppContext.getmOurScenes();
                nameList= new ArrayList<String>();
                for (int i = 0; i <sences.size() ; i++) {
                    nameList.add(sences.get(i).getSenceName());
                }
                if(nameList.size()==0){
                    showToast("没有可操作设备");
                    return;
                }
                Intent intent01 = new Intent(this, ChooseActivity.class);
                intent01.putExtra("DataList",nameList);
                intent01.putExtra("tag","场景");
                startActivityForResult(intent01,SENCE_RESULT);
                break;
            case R.id.back:
                finish();
                break;
            //布防时机
            case R.id.rl_Deployment_rule:
                if(deviceTag==1){
                    showAssociationbindDialog();
                }else{
                    ToastUtils.showShort("请选择安防探头设备");
                }
                break;

        }
    }

    private void showAssociationbindDialog(){
        /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_associatebind, null);
        TextView tvAssociateDevice = (TextView) dialogView.findViewById(R.id.tv_associate_device);
        tvAssociateDevice.setText("布防时");
        TextView tvAssociateScence = (TextView) dialogView.findViewById(R.id.tv_associate_scence);
        tvAssociateScence.setText("撤防时");
        TextView tvUnbind = (TextView) dialogView.findViewById(R.id.tv_unbind);
        tvUnbind.setText("任何时候");
        tvAssociateDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //绑定设备
                if (alertDialog != null)
                    alertDialog.dismiss();
                action.setZoneabel((byte)1);
                tvDeploymentRule.setText("布防时");
            }
        });

        tvAssociateScence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //绑定场景
                if (alertDialog != null)
                    alertDialog.dismiss();
                action.setZoneabel((byte)2);
                tvDeploymentRule.setText("撤防时");

            }
        });

        tvUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解绑
                if (alertDialog != null)
                    alertDialog.dismiss();
                action.setZoneabel((byte)3);
                tvDeploymentRule.setText("任何时候");
            }
        });

        customizeDialog.setView(dialogView);
        alertDialog = customizeDialog.show();
    }
    /**
     * 创建规则
     */
    private void createRule() {
     final   String name=etname.getText().toString();
        if(TextUtils.isEmpty(name)){
            showToast("请输入规则名称");
            return;
        }
        if(action.getuId()<=0){
            showToast("请选择设备");
            return;
        }
        if(SceneID<0){
            showToast("请选择联动场景");
            return;
        }

        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret= AppContext.getInstance().getSerialInstance().addDeviceTask(name,action,SceneID,(byte) 0,1);
                subscriber.onNext(ret);
                subscriber.onCompleted();
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer ret) {
                        if(ret >=0){
                            showToast("添加成功");
                            finish();
                        }else{
                            showToast("添加失败");
                        }
                    }
                });



    }

    private int deviceTag=-1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode ==RESULT_OK){
            switch (requestCode){
                case DEVICE_RESULT:
                    int index = data.getIntExtra("INDEX", 0);
                    if(index>=0){
                        action.setZoneabel((byte)3);
                        tvDeploymentRule.setText("任何时候");
                        tvDevice.setText(devices.get(index).getDeviceName());
                        action.setuId(devices.get(index).getUId());
                        action.setDeviceId(devices.get(index).getDeviceId());
                        //传感器
                        if(devices.get(index).getDeviceId() == DeviceList.DEVICE_ID_SENSOR){
                            //门磁
                            if(devices.get(index).getZoneType() == DeviceList.SENSOR_ZONE_TYPE_DOOR){
                                tvCondition.setText("门磁报警");
                                rlDeploymentRule.setVisibility(View.VISIBLE);
                                deviceTag=1;
                            }
                            //人体
                            else if(devices.get(index).getZoneType() == DeviceList.SENSOR_ZONE_TYPE_HUMAN)
                            {
                                tvCondition.setText("人体报警");
                                rlDeploymentRule.setVisibility(View.VISIBLE);
                                deviceTag=1;
                            }
                            //烟雾
                            else if(devices.get(index).getZoneType() == DeviceList.SENSOR_ZONE_TYPE_SMOKE){
                                spCondition.setVisibility(View.GONE);
                                tvCondition.setVisibility(View.VISIBLE);
                                tvCondition.setText("烟雾报警");
                                rlDeploymentRule.setVisibility(View.VISIBLE);
                                deviceTag=1;
                            }
                            //水位
                            else if(devices.get(index).getZoneType() ==DeviceList.SENSOR_ZONE_TYPE_WATER){
                                tvCondition.setText("水位报警");
                                rlDeploymentRule.setVisibility(View.VISIBLE);
                                deviceTag=1;
                            }

                        }
                        //门锁
                        else if(devices.get(index).getDeviceId() == DeviceList.DEVICE_ID_DOOR_LOCK){
                            spCondition.setVisibility(View.GONE);
                            tvCondition.setVisibility(View.VISIBLE);
                            tvCondition.setText("开门");
                            rlDeploymentRule.setVisibility(View.VISIBLE);
                            deviceTag=1;
                        }
                    }

                    break;

                case SENCE_RESULT:
                    int positon = data.getIntExtra("INDEX", 0);
                    if(positon>=0){
                        tvScene.setText(sences.get(positon).getSenceName());
                        SceneID  = sences.get(positon).getSenceId();
                    }
                    break;

            }

        }

    }
}
