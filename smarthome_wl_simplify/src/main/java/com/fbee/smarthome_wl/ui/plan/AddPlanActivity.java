package com.fbee.smarthome_wl.ui.plan;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.view.CircleView;
import com.fbee.smarthome_wl.widget.dialog.ColorSelectDialog;
import com.fbee.smarthome_wl.widget.dialog.DialogChooseDevice;
import com.fbee.smarthome_wl.widget.dialog.DialogChose;
import com.fbee.smarthome_wl.widget.dialog.DialogListChoose;
import com.fbee.smarthome_wl.widget.dialog.DialogtimeSelected;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.TaskTimerAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class AddPlanActivity extends BaseActivity {
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RelativeLayout rlPlanCycle;
    private TextView tvWeek;
    private RelativeLayout rlPlanTime;
    private TextView tvTime;
    private RelativeLayout rlProjectType;
    private TextView spPlanType;

    private RelativeLayout rlPlanOperation;
    private TextView spOperation;


    private RelativeLayout rlState;
    private SwitchCompat switch1;
    private LinearLayout layoutLight;
    private SeekBar seekBarLight;
    private RelativeLayout rlColor;
    private CircleView ivColor;
    private LinearLayout layoutColor;
    private ImageView ivSlatebule;
    private ImageView ivBlue;
    private ImageView ivGreen;
    private ImageView ivOrange;
    private ImageView ivRed;
    private ImageView ivPalette;

    private RelativeLayout rlPlanType;
    private TextView tvType;
    private TextView tvDeviceorgroupName;

    //场景定时
//    private RelativeLayout rlTaskName;
//    private EditText etTaskname;


    private DialogtimeSelected dialogtime;
    private List<DeviceInfo> devices;
    private List<GroupInfo> mDatas;
    private ColorSelectDialog dialog;
    //进行置位
    private byte[] MODE = new byte[]{0x01, 0x02, 0x04, 0x08, 0x10, 0x20,0x40};
    private String[] arr = new String[] {"周一", "周二","周三","周四","周五","周六","周日"};
    private List<String>  mSelectWeek;
    private byte workMode;
    private byte taskType;
    private byte hours=-1,minutes, seconds;
    private byte data1;
    private byte data2;
    private int uid = -1; //设备id
    private  short groupid=-1; //区域id
    private byte sceneId =-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

    }

    @Override
    protected void initView() {
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        rlPlanCycle = (RelativeLayout) findViewById(R.id.rl_plan_cycle);
        tvWeek = (TextView) findViewById(R.id.tv_week);
        rlPlanTime = (RelativeLayout) findViewById(R.id.rl_plan_time);
        tvTime = (TextView) findViewById(R.id.tv_time);

        rlProjectType = (RelativeLayout) findViewById(R.id.rl_project_type);
        spPlanType = (TextView) findViewById(R.id.sp_plan_type);

        rlPlanOperation = (RelativeLayout) findViewById(R.id.rl_plan_operation);
        spOperation = (TextView) findViewById(R.id.sp_operation);

        rlState = (RelativeLayout) findViewById(R.id.rl_state);
        switch1 = (SwitchCompat) findViewById(R.id.switch1);
        layoutLight = (LinearLayout) findViewById(R.id.layout_light);
        seekBarLight = (SeekBar) findViewById(R.id.seekBar_light);
        rlColor = (RelativeLayout) findViewById(R.id.rl_color);
        ivColor = (CircleView) findViewById(R.id.iv_color);
        layoutColor = (LinearLayout) findViewById(R.id.layout_color);
        ivSlatebule = (ImageView) findViewById(R.id.iv_slatebule);
        ivBlue = (ImageView) findViewById(R.id.iv_blue);
        ivGreen = (ImageView) findViewById(R.id.iv_green);
        ivOrange = (ImageView) findViewById(R.id.iv_orange);
        ivRed = (ImageView) findViewById(R.id.iv_red);
        ivPalette = (ImageView) findViewById(R.id.iv_palette);
        rlPlanType = (RelativeLayout) findViewById(R.id.rl_plan_type);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvDeviceorgroupName = (TextView) findViewById(R.id.tv_deviceorgroup_name);

//        rlTaskName = (RelativeLayout) findViewById(R.id.rl_task_name);
//        etTaskname = (EditText) findViewById(R.id.et_taskname);
        rlState.setVisibility(View.GONE);
        layoutLight.setVisibility(View.GONE);
        rlColor.setVisibility(View.GONE);
        layoutColor.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        mCompositeSubscription=new CompositeSubscription();
        title.setText("添加计划");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setText("完成");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        rlPlanCycle.setOnClickListener(this);
        rlPlanTime.setOnClickListener(this);
        rlPlanType.setOnClickListener(this);
        rlPlanOperation.setOnClickListener(this);
        rlProjectType.setOnClickListener(this);
//        showSpinner();
//        showOpSpinner();
        dialogtime =new DialogtimeSelected(this, new DialogtimeSelected.OnChooseListener() {
            @Override
            public void onTimeCallback(int hour, int minute, int second) {
                tvTime.setText(hour+":"+minute+":"+second);
                hours = (byte) hour;
                minutes =(byte) minute;
                seconds = (byte) second;
            }
        });
        mDatas = new ArrayList<>();


        //开关
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    data1 = 1;
                }else{
                    data1 = 0;
                }
            }
        });
        //亮度
        seekBarLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                data1 = (byte)seekBar.getProgress();

            }
        });
        //默认红色
        setColor("FF0000");

        //颜色
        ivSlatebule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#6A5ACD"));
                setColor("6A5ACD");
            }
        });


        ivBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#0000FF"));
                setColor("0000FF");
            }
        });

        ivGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#00FF00"));
                setColor("00FF00");
            }
        });


        ivOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#FFA500"));
                setColor("FFA500");
            }
        });

        ivRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#FF0000"));
                setColor("FF0000");
            }
        });

        ivPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == dialog){
                    dialog = new ColorSelectDialog(AddPlanActivity.this);
                    dialog.setOnColorSelectListener(new ColorSelectDialog.OnColorSelectListener() {
                        @Override
                        public void onSelectFinish(int color) {
                            int red=Color.red(color);
                            int green=Color.green(color);
                            int blue=Color.blue(color);

                            ivColor.setColor(Color.rgb(red,green,blue));
                            float[] arrayOfObject2 = new float[3];
                            Color.colorToHSV(Color.rgb(red,green,blue), arrayOfObject2);
                            data1 = (byte)((int)(arrayOfObject2[0] / 360.0F * 255.0F));
                            data2 = (byte)((int)(arrayOfObject2[1] * 254.0F));

                        }
                    });
                    dialog.show();
                }else{
                    dialog.show();
                }
            }
        });




    }


    private void setColor(String color){
        float[] arrayOfObject2 = new float[3];
        Color.colorToHSV(Integer.parseInt(color, 16), arrayOfObject2);
         data1 = (byte)((int)(arrayOfObject2[0] / 360.0F * 255.0F));
         data2 = (byte)((int)(arrayOfObject2[1] * 254.0F));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            //周期
            case R.id.rl_plan_cycle:
                showChooseWeekDialog();
                break;
            //时间
            case R.id.rl_plan_time:
                showSelectDialog();
                break;
            //类型选择
            case R.id.rl_plan_type:
                if("设备".equals(spPlanType.getText().toString())){
                    showChose_Device();
                    //showChooseDevice();
                }else if("区域".equals(spPlanType.getText().toString())){
                    showArea_Dialog();
                   //showAreaDialog();
                }else if("场景".equals(spPlanType.getText().toString())){
                    showChoose_Sence();
                    //showChooseSence();
                }else{
                    showToast("请先选择执行类型！");
                }
                break;
            //操作的类型
            case R.id.rl_plan_operation:
                showOpSpinner();
                break;
            //完成
            case R.id.tv_right_menu:
                createPlan();
                break;
            case R.id.rl_project_type:
                showSpinner();
                break;

            default:
                break;

        }
    }


    private void createPlan() {
        if(workMode == 0){
            showToast("请选择周期");
            return;
        }
        if(hours ==-1){
            showToast("请选择时间");
            return;
        }

        if(spPlanType.getText().toString().equals("设备")){
            if(uid== -1){
                showToast("请选择设备");
                return;
            }
            if(taskType ==0){
                showToast("请选择操作类型");
                return;
            }
            //设备定时
            Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    AppContext.getInstance().getSerialInstance().
                            addDeviceTimer(uid,workMode,hours,minutes,seconds,taskType,data1,data2);
                    subscriber.onNext(-1);
                    subscriber.onCompleted();
                }

            }).compose(TransformUtils.<Integer>defaultSchedulers())
                    .subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onCompleted() {
                            showToast("添加完成");
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(Integer ret) {

                        }
                    });
            mCompositeSubscription.add(sub);
        }
        //区域定时
        else if(spPlanType.getText().toString().equals("区域")){
            if(groupid== -1){
                showToast("请选择区域");
                return;
            }
            if(taskType ==0){
                showToast("请选择操作类型");
                return;
            }
            //区域定时
            Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    AppContext.getInstance().getSerialInstance().
                            addGroupTimer(groupid,workMode,hours,minutes,seconds,taskType,data1,data2);
                    subscriber.onNext(-1);
                    subscriber.onCompleted();
                }

            }).compose(TransformUtils.<Integer>defaultSchedulers())
                    .subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onCompleted() {
                            showToast("添加完成");
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(Integer ret) {

                        }
                    });
            mCompositeSubscription.add(sub);

        }else if(spPlanType.getText().toString().equals("场景")){
            if(sceneId== -1){
                showToast("请选择场景");
                return;}

//            if(TextUtils.isEmpty(etTaskname.getText().toString())){
//                showToast("请输入任务名称");
//                return;
//            }
            showLoadingDialog("添加中...");

            Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
//                    AppContext.getInstance().getSerialInstance().
//                            addSenceTimer(sceneId,workMode,hours,minutes,seconds,taskType,data1,data2);

                    TaskTimerAction action =new TaskTimerAction();
                    action.setH(hours);
                    action.setM(minutes);
                    action.setS(seconds);
                    action.setWorkMode(workMode);
                    int ret = -1;
                    long second= new Date().getTime()/1000;
                    ret = AppContext.getInstance().getSerialInstance().addTimerTask(
                           String.valueOf(second), action, sceneId,1);

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
                            hideLoadingDialog();
                            showToast("添加失败");
                        }

                        @Override
                        public void onNext(Integer ret) {
                            hideLoadingDialog();
                            if(ret >=0){
                                showToast("添加完成");
                                setResult(RESULT_OK);
                                finish();
                            }else{
                                showToast("添加失败");
                            }

                        }
                    });
        mCompositeSubscription.add(sub);
        }else{
            showToast("请选择执行类型！");
        }

    }

    /**
     * 选择设备
     */
    private void showChose_Device(){
        List<DeviceInfo>  list = AppContext.getmOurDevices();
        List<String> resultlist=new ArrayList<String>();
        devices= new ArrayList<>();
        if(taskType ==0){
            showToast("请先选择操作类型");
            return;
        }
        switch (taskType){
            case 0x01:
                for (int i = 0; i < list.size(); i ++) {
                    if(list.get(i).getDeviceId() != DeviceList.DEVICE_ID_DOOR_LOCK){
                        resultlist.add(list.get(i).getDeviceName());
                        devices.add(list.get(i));
                    }

                }

                break;
            case 0x02:
                for (int i = 0; i < list.size(); i ++) {
                    if(list.get(i).getDeviceId() == DeviceList.DEVICE_ID_COLOR_TEMP1
                            || list.get(i).getDeviceId() == DeviceList.DEVICE_ID_COLOR_TEMP2
                            || list.get(i).getDeviceId() == DeviceList.DEVICE_ID_COLOR_PHILIPS){
                        resultlist.add(list.get(i).getDeviceName());
                        devices.add(list.get(i));
                    }
                }

                break;
            case 0x04:
                for (int i = 0; i < list.size(); i ++) {
                    if(list.get(i).getDeviceId() == DeviceList.DEVICE_ID_COLOR_PHILIPS){
                        resultlist.add(list.get(i).getDeviceName());
                        devices.add(list.get(i));
                    }

                }
                break;


        }

        if(resultlist.size() >0){
            new DialogChose(this, resultlist, new DialogChose.OnItemClickListener() {
                @Override
                public void onItemClickback(View view,int position) {
                    DeviceInfo info=devices.get(position);
                    uid = info.getUId();
                    tvDeviceorgroupName.setText(info.getDeviceName());
                }
            }).show();
        }else{
            showToast("暂时没有可添加设备");
        }
    }




    /**
     * 区域选择
     */
    private void showArea_Dialog(){
        mDatas = AppContext.getmOurGroups();
        if(mDatas.size()<=0){
            showToast("暂时没有可操作区域");
            return;
        }
        ArrayList  array = new ArrayList<String>();
        for (int i = 0; i <mDatas.size() ; i++) {
            array.add(mDatas.get(i).getGroupName());
        }

        new DialogChose(this, array, new DialogChose.OnItemClickListener(){
            @Override
            public void onItemClickback(View view,int position)  {
                GroupInfo info = mDatas.get(position);
                groupid = info.getGroupId();
                tvDeviceorgroupName.setText(info.getGroupName());
            }
        }).show();


    }

    /**
     * 场景选择
     */
    private void showChoose_Sence() {
        final List<SenceInfo> scenes = AppContext.getmOurScenes();
        if(scenes.size()<=0){
            showToast("暂时没有可操作场景");
            return;
        }
        ArrayList  array = new ArrayList<String>();
        for (int i = 0; i <scenes.size() ; i++) {
            array.add(scenes.get(i).getSenceName());
        }

        new DialogChose(this, array, new DialogChose.OnItemClickListener(){
            @Override
            public void onItemClickback(View view,int position)  {
                SenceInfo info = scenes.get(position);
                sceneId = (byte)info.getSenceId();
                tvDeviceorgroupName.setText(info.getSenceName());
            }
        }).show();

    }

    /**
     * 选择设备
     */
    private void showChooseDevice(){
        List<DeviceInfo>  list = AppContext.getmOurDevices();
        List<String> resultlist=new ArrayList<String>();
        devices= new ArrayList<>();

        switch (taskType){
            case 0x01:
                for (int i = 0; i < list.size(); i ++) {
                    if(list.get(i).getDeviceId() != DeviceList.DEVICE_ID_DOOR_LOCK){
                        resultlist.add(list.get(i).getDeviceName());
                        devices.add(list.get(i));
                    }

                }

                break;
            case 0x02:
                for (int i = 0; i < list.size(); i ++) {
                    if(list.get(i).getDeviceId() == DeviceList.DEVICE_ID_COLOR_TEMP1
                           || list.get(i).getDeviceId() == DeviceList.DEVICE_ID_COLOR_TEMP2
                            || list.get(i).getDeviceId() == DeviceList.DEVICE_ID_COLOR_PHILIPS){
                        resultlist.add(list.get(i).getDeviceName());
                        devices.add(list.get(i));
                    }
                }

                break;
            case 0x04:
                for (int i = 0; i < list.size(); i ++) {
                    if(list.get(i).getDeviceId() == DeviceList.DEVICE_ID_COLOR_PHILIPS){
                        resultlist.add(list.get(i).getDeviceName());
                        devices.add(list.get(i));
                    }

                }
                break;


        }

        if(resultlist.size() >0){
            new DialogChooseDevice(this, resultlist, new DialogChooseDevice.OnChooseListener() {
                @Override
                public void onChooseCallback(int index) {
                    DeviceInfo info=devices.get(index);
                    uid = info.getUId();
                    tvDeviceorgroupName.setText(info.getDeviceName());
                }
            }).show();
        }else{
            showToast("暂时没有可添加设备");
        }

    }


    /**
     * 区域选择
     */
    private void showAreaDialog(){
        mDatas = AppContext.getmOurGroups();
        if(mDatas.size()<=0){
            showToast("暂时没有可操作区域");
            return;
        }
        ArrayList  array = new ArrayList<String>();
        for (int i = 0; i <mDatas.size() ; i++) {
            array.add(mDatas.get(i).getGroupName());
        }

        new DialogChooseDevice(this, array, new DialogChooseDevice.OnChooseListener() {
            @Override
            public void onChooseCallback(int index) {
                GroupInfo info = mDatas.get(index);
                groupid = info.getGroupId();
                tvDeviceorgroupName.setText(info.getGroupName());
            }
        }).show();


    }

    /**
     * 场景选择
     */
    private void showChooseSence() {
       final List<SenceInfo> scenes = AppContext.getmOurScenes();
        if(scenes.size()<=0){
            showToast("暂时没有可操作场景");
            return;
        }
        ArrayList  array = new ArrayList<String>();
        for (int i = 0; i <scenes.size() ; i++) {
            array.add(scenes.get(i).getSenceName());
        }

        new DialogChooseDevice(this, array, new DialogChooseDevice.OnChooseListener() {
            @Override
            public void onChooseCallback(int index) {
                SenceInfo info = scenes.get(index);
                sceneId = (byte)info.getSenceId();
                tvDeviceorgroupName.setText(info.getSenceName());
            }
        }).show();

    }


    private void showOpSpinner() {
        final String[] mItems = new String[]{"开关","亮度","颜色"};
        List list = java.util.Arrays.asList(mItems);
        new DialogChose(this, list, new DialogChose.OnItemClickListener() {
            @Override
            public void onItemClickback(View view,int position)  {
                spOperation.setText(mItems[position]);
                switch (position){
                    case 0:
                        taskType = 0x01;
                        rlState.setVisibility(View.VISIBLE);
                        layoutLight.setVisibility(View.GONE);
                        rlColor.setVisibility(View.GONE);
                        layoutColor.setVisibility(View.GONE);
                        break;
                    case 1:
                        taskType = 0x02;
                        rlState.setVisibility(View.GONE);
                        layoutLight.setVisibility(View.VISIBLE);
                        rlColor.setVisibility(View.GONE);
                        layoutColor.setVisibility(View.GONE);
                        break;
                    case 2:
                        taskType = 0x04;
                        rlState.setVisibility(View.GONE);
                        layoutLight.setVisibility(View.GONE);
                        rlColor.setVisibility(View.VISIBLE);
                        layoutColor.setVisibility(View.VISIBLE);
                        break;

                }

            }
        }).show();




//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spOperation .setAdapter(adapter);
//
//        spOperation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int pos, long id) {
//                switch (pos){
//                    case 0:
//                        taskType = 0x01;
//                        rlState.setVisibility(View.VISIBLE);
//                        layoutLight.setVisibility(View.GONE);
//                        rlColor.setVisibility(View.GONE);
//                        layoutColor.setVisibility(View.GONE);
//                        break;
//                    case 1:
//                        taskType = 0x02;
//                        rlState.setVisibility(View.GONE);
//                        layoutLight.setVisibility(View.VISIBLE);
//                        rlColor.setVisibility(View.GONE);
//                        layoutColor.setVisibility(View.GONE);
//                        break;
//                    case 2:
//                        taskType = 0x04;
//                        rlState.setVisibility(View.GONE);
//                        layoutLight.setVisibility(View.GONE);
//                        rlColor.setVisibility(View.VISIBLE);
//                        layoutColor.setVisibility(View.VISIBLE);
//                        break;
//
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Another interface callback
//            }
//        });
//        spOperation.setSelection(0,true);
    }

    private void showSpinner() {
        final String[] mItems = new String[]{"设备","区域","场景"};

        List list = java.util.Arrays.asList(mItems);
        new DialogChose(this, list, new DialogChose.OnItemClickListener() {
            @Override
            public void onItemClickback(View view,int position)  {
                spPlanType.setText(mItems[position]);
                switch (position){
                    case 0:
                        tvType.setText("执行设备");
                        rlPlanOperation.setVisibility(View.VISIBLE);
                        tvDeviceorgroupName.setText("请选择设备");
                        break;
                    case 1:
                        tvType.setText("执行区域");
                        rlPlanOperation.setVisibility(View.VISIBLE);
                        tvDeviceorgroupName.setText("请选择区域");
                        break;
                    case 2:
                        tvType.setText("执行场景");
                        rlPlanOperation.setVisibility(View.GONE);
                        rlState.setVisibility(View.GONE);
                        layoutLight.setVisibility(View.GONE);
                        rlColor.setVisibility(View.GONE);
                        layoutColor.setVisibility(View.GONE);
                        tvDeviceorgroupName.setText("请选择场景");
                        break;

                }

            }
        }).show();






//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spPlanType .setAdapter(adapter);
//        spPlanType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int pos, long id) {
//
//                switch (pos){
//                    case 0:
//                        tvType.setText("执行设备");
//                        rlPlanOperation.setVisibility(View.VISIBLE);
//                        showOpSpinner();
//                        tvDeviceorgroupName.setText("请选择设备");
//                        break;
//                    case 1:
//                        tvType.setText("执行区域");
//                        rlPlanOperation.setVisibility(View.VISIBLE);
//                        showOpSpinner();
//                        tvDeviceorgroupName.setText("请选择区域");
//                        break;
//                    case 2:
//                        tvType.setText("执行场景");
//                        rlPlanOperation.setVisibility(View.GONE);
//                        rlState.setVisibility(View.GONE);
//                        layoutLight.setVisibility(View.GONE);
//                        rlColor.setVisibility(View.GONE);
//                        layoutColor.setVisibility(View.GONE);
//                        tvDeviceorgroupName.setText("请选择场景");
//                        break;
//
//
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Another interface callback
//            }
//        });
//        spPlanType.setSelection(0,true);

    }

    /**
     * 时间
     */
    private void showSelectDialog() {
        if(null== dialogtime){
            dialogtime =new DialogtimeSelected(this, new DialogtimeSelected.OnChooseListener() {
                @Override
                public void onTimeCallback(int hour, int minute, int second) {
                    tvTime.setText(hour+":"+minute+":"+second);
                }
            });
        }else{
            dialogtime.show();
        }

    }


    /**
     * 周期选择
     */
    private void showChooseWeekDialog() {
        mSelectWeek = new ArrayList<>();
        DialogListChoose dialog = new DialogListChoose(this, new DialogListChoose.DialogListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i <mSelectWeek.size() ; i++) {
                    buffer.append(mSelectWeek.get(i));
                    buffer.append(" ");
                }
                tvWeek.setText(buffer);

            }

            @Override
            public void OnItemCheckedListener(int postion, boolean checked) {
                if(checked){
                    workMode |= MODE[postion];
                    mSelectWeek.add(arr[postion]);
                }else{
                    workMode &= ~MODE[postion];
                    mSelectWeek.remove(arr[postion]);
                }
            }
        });
        dialog.show();
        List list = Arrays.asList(arr);
        dialog.setTilte("周期选择");
        dialog.setData(list);

    }
}
