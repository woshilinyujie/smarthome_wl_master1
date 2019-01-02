package com.fbee.smarthome_wl.ui.plan;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.view.CircleView;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.TimerInfo;

import java.util.List;

public class PlanDetailActivity extends BaseActivity {
    private LinearLayout activityAddPlan;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private TextView tvWeek;
    private TextView tvTime;
    private TextView tvPlanType;
    private TextView tvOperation;
    private TextView tvType;
    private TextView tvDeviceorgroupName;
    private RelativeLayout rlState;
    private SwitchCompat switch1;
    private LinearLayout layoutLight;
    private SeekBar seekBarLight;
    private RelativeLayout rlColor;
    private CircleView ivColor;
    private RelativeLayout rlPlanType;
    private RelativeLayout rlPalnOperation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
    }

    @Override
    protected void initView() {

        activityAddPlan = (LinearLayout) findViewById(R.id.activity_add_plan);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        tvWeek = (TextView) findViewById(R.id.tv_week);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvPlanType = (TextView) findViewById(R.id.tv_plan_type);
        tvOperation = (TextView) findViewById(R.id.tv_operation);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvDeviceorgroupName = (TextView) findViewById(R.id.tv_deviceorgroup_name);
        rlState = (RelativeLayout) findViewById(R.id.rl_state);
        switch1 = (SwitchCompat) findViewById(R.id.switch1);
        layoutLight = (LinearLayout) findViewById(R.id.layout_light);
        seekBarLight = (SeekBar) findViewById(R.id.seekBar_light);
        rlColor = (RelativeLayout) findViewById(R.id.rl_color);
        ivColor = (CircleView) findViewById(R.id.iv_color);

        rlPlanType = (RelativeLayout) findViewById(R.id.rl_plan_type);
        rlPalnOperation = (RelativeLayout) findViewById(R.id.rl_plan_operation);

    }

    @Override
    protected void initData() {
        TimerInfo timer = (TimerInfo) getIntent().getSerializableExtra("TimerInfo");
        title.setText("计划详情");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        byte workmode = timer.getWorkMode();

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
        tvWeek.setText(buffer);

        StringBuffer str = new StringBuffer();
        if(timer.getH()<=9){
            str.append("0");
        }
        str.append(String.valueOf(timer.getH())+":");
        if(timer.getM()<=9){
            str.append("0");
        }
        str.append(String.valueOf(timer.getM())+":");
        if(timer.getS()<=9){
            str.append("0");
        }
        str.append(String.valueOf(timer.getS()));
        tvTime.setText(str.toString());

        if(timer.AddressMode == 1 ){
            tvPlanType.setText("区域");
            tvType.setText("区域");
            List<GroupInfo> groups = AppContext.getmOurGroups();
            for (int i = 0; i <groups.size() ; i++) {
                if(groups.get(i).getGroupId()==timer.getDevice()){
                    tvDeviceorgroupName.setText(groups.get(i).getGroupName());
                }
            }

        }else if(timer.AddressMode ==2){
            tvPlanType.setText("设备");
            tvType.setText("设备");
            List<DeviceInfo> devices = AppContext.getmOurDevices();
            for (int i = 0; i <devices.size() ; i++) {
                if(devices.get(i).getUId() == timer.getDevice()){
                    tvDeviceorgroupName.setText(devices.get(i).getDeviceName());
                }
            }
        }else if(timer.AddressMode ==3){
            tvType.setText("执行场景:");
            rlPlanType.setVisibility(View.GONE);
            rlPalnOperation.setVisibility(View.GONE);
            rlState.setVisibility(View.GONE);
            layoutLight.setVisibility(View.GONE);
            rlColor.setVisibility(View.GONE);
            List<SenceInfo> sences=AppContext.getmOurScenes();
            for (int i = 0; i <sences.size() ; i++) {
                if(sences.get(i).getSenceId() ==timer.getDevice()){
                    tvDeviceorgroupName.setText(sences.get(i).getSenceName());
                }
            }

        }

        byte tasktype = timer.getTaskType();
        String task="";
        switch (tasktype){
            case 1:
                task ="开关";
                rlState.setVisibility(View.VISIBLE);
                layoutLight.setVisibility(View.GONE);
                rlColor.setVisibility(View.GONE);
                if(timer.getData1() ==0){
                    switch1.setChecked(false);
                }else if(timer.getData1() ==1){
                    switch1.setChecked(true);
                }
                break;
            case 2:
                task ="亮度";
                rlState.setVisibility(View.GONE);
                layoutLight.setVisibility(View.VISIBLE);
                rlColor.setVisibility(View.GONE);
                seekBarLight.setProgress(timer.getData1());
                break;
            case 4:
                task ="颜色";
                rlState.setVisibility(View.GONE);
                layoutLight.setVisibility(View.GONE);
                rlColor.setVisibility(View.VISIBLE);

                float[] arrayOfObject2 = new float[3];
                if(timer.getData1()>0){
                    arrayOfObject2[0] = timer.getData1()*360.0f/255.0f;
                }else{
                    arrayOfObject2[0] =(256+timer.getData1())*360.0f/255.0f;
                }

                if(timer.getData2()>=0){
                    arrayOfObject2[1] = timer.getData2()/254.0f;
                }else{
                    arrayOfObject2[1] = (256+timer.getData2())/254.0f;
                }
                arrayOfObject2[2] = 1.0f;
                ivColor.setColor( Color.HSVToColor(arrayOfObject2));

                break;
        }

        tvOperation.setText(task);


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
