package com.fbee.smarthome_wl.ui.choseareaorsensces;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.ItemAreaAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.GroupMemberInfo;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.event.AreaDevicesChangeEvent;
import com.fbee.smarthome_wl.event.AreaNameChange;
import com.fbee.smarthome_wl.ui.areamanager.AreaDevicesEditActivity;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockActivity;
import com.fbee.smarthome_wl.ui.main.area.AreaFragment;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogColor;
import com.fbee.smarthome_wl.widget.dialog.DialogCurtain;
import com.fbee.smarthome_wl.widget.dialog.DialogGroupSet;
import com.fbee.smarthome_wl.widget.dialog.DialogSwitch;
import com.fbee.smarthome_wl.widget.dialog.DialogTemperature;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.Serial;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

import static com.fbee.smarthome_wl.ui.main.area.AreaFragment.GROUPINFOS;

public class ItemAreaActivity extends BaseActivity implements BaseRecylerAdapter.OnItemClickLitener{
    private GroupInfo groupInfo;

    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RecyclerView devicesRecyclerItemArea;
    private List<Integer> deviceUid;
    private Serial serial;
    private List<DeviceInfo> deviceInfos;
    private List<EquesListInfo.bdylistEntity> bdylist;
    private List<MyDeviceInfo> addDecices;
    private ItemAreaAdapter addAdapter;
    private String itemDviceInfo ="itemDviceInfo";
    private  int requestCode=500;
    private int resultCode=600;
    private PopupWindow popupWindow;
    private View parentView;
    private LinearLayout allOpenLinearItemArea;
    private LinearLayout groupMeasureLinearItemArea;
    private LinearLayout allCloseLinearItemArea;
    private LinearLayout linerAdd;

    private DialogGroupSet dialog;
    private ArrayList<String> groupNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_area);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        devicesRecyclerItemArea = (RecyclerView) findViewById(R.id.devices_recycler_item_area);
        allOpenLinearItemArea = (LinearLayout) findViewById(R.id.all_open_linear_item_area);
        groupMeasureLinearItemArea = (LinearLayout) findViewById(R.id.group_measure_linear_item_area);
        allCloseLinearItemArea = (LinearLayout) findViewById(R.id.all_close_linear_item_area);
        linerAdd = (LinearLayout) findViewById(R.id.liner_add);
    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("编辑");
        tvRightMenu.setOnClickListener(this);
        allOpenLinearItemArea.setOnClickListener(this);
        groupMeasureLinearItemArea.setOnClickListener(this);
        mCompositeSubscription  =getCompositeSubscription();
        allCloseLinearItemArea.setOnClickListener(this);
        parentView = LayoutInflater.from(this).inflate(R.layout.item_area, null);
        //初始化Serial
        serial= AppContext.getInstance().getSerialInstance();
        groupNames=getIntent().getStringArrayListExtra(GROUPINFOS);
        if(groupNames==null)return;
        groupInfo= (GroupInfo) getIntent().getSerializableExtra(AreaFragment.GROUPINFO);
        if(groupInfo!=null&&groupInfo.getGroupName()!=null){
            title.setText(groupInfo.getGroupName());
        }
        //统一设备
        addDecices=new ArrayList<>();
        //飞比设备
        deviceInfos=new ArrayList<>();
        deviceUid=new ArrayList<>();
        //猫眼设备
        bdylist = AppContext.getBdylist();
        showLoadingDialog(null);
       /* //猫眼设备转换成统一对象MyDeviceInfo
        if(bdylist!=null&&bdylist.size()>0){
            for (int i = 0; i <bdylist.size() ; i++) {
                String name ="";
                MyDeviceInfo info=new MyDeviceInfo();
                EquesListInfo.bdylistEntity eques=bdylist.get(i);
                info.setId(eques.getBid());
                if(eques.getNick()==null || eques.getNick().length() ==0){
                    name =eques.getName();
                }else{
                    name = eques.getNick();
                }
                info.setName(name);
                info.setSupplier(FactoryType.EQUES);
                info.setDeviceType("猫眼");
                addDecices.add(info);

            }
        }*/

        addAdapter = new ItemAreaAdapter(this,addDecices);
        LinearLayoutManager addm = new LinearLayoutManager(this);
        devicesRecyclerItemArea.setLayoutManager(addm);
        devicesRecyclerItemArea.setItemAnimator(new DefaultItemAnimator());
        devicesRecyclerItemArea.setAdapter(addAdapter);
        addAdapter.setOnItemClickLitener(this);
        //注册RXbus接收groupMember_CallBack(short groupId, int[] deviceUid)中数据
        mSubscription = RxBus.getInstance().toObservable(GroupMemberInfo.class)
                .compose(TransformUtils.<GroupMemberInfo>defaultSchedulers())
                .subscribe(new Action1<GroupMemberInfo>() {
                    @Override
                    public void call(GroupMemberInfo event) {
                        if(event==null)return;
                        if(groupInfo.getGroupId()!=event.getGroupId())return;
                        if(event.getDeviceUid().length>0){
                            if(linerAdd.getVisibility()==View.VISIBLE){
                                linerAdd.setVisibility(View.GONE);
                            }
                            if(addDecices != null)
                                addDecices.clear();
                            for (int i = 0; i <event.getDeviceUid().length ; i++) {

                                for (int j = 0; j < AppContext.getmOurDevices().size(); j++) {
                                    if(event.getDeviceUid()[i] ==AppContext.getmOurDevices().get(j).getUId()){
                                        MyDeviceInfo bean=new MyDeviceInfo();
                                        bean.setSupplier(FactoryType.FBEE);
                                        String type="";
                                        switch (AppContext.getmOurDevices().get(j).getDeviceId()){
                                            case DeviceList.DEVICE_ID_DOOR_LOCK:
                                                type = "门锁";
                                                break;
                                            case DeviceList.DEVICE_ID_COLOR_TEMP1:
                                            case DeviceList.DEVICE_ID_COLOR_TEMP2:
                                                type = "色温灯";
                                                break;
                                            case DeviceList.DEVICE_ID_COLOR_PHILIPS:
                                                type = "彩灯";
                                                break;
                                            case DeviceList.DEVICE_ID_SOCKET:
                                                type = "开关/插座";
                                                break;
                                            case DeviceList.DEVICE_ID_SWITCH:
                                                type = "智能开关";
                                                break;
                                            //窗帘
                                            case DeviceList.DEVICE_ID_CURTAIN:
                                                type = "窗帘";
                                                break;
                                        }
                                        bean.setDeviceType(type);
                                        bean.setId(String.valueOf(AppContext.getmOurDevices().get(j).getUId()));
                                        bean.setName(AppContext.getmOurDevices().get(j).getDeviceName());
                                        addDecices.add(bean);
                                    }
                                }

//                                if(deviceUid.contains(event.getDeviceUid()[i]))
//                                    continue;
//                                deviceUid.add(event.getDeviceUid()[i]);
                            }
                            addAdapter.notifyDataSetChanged();

//                            LogUtil.e("ItemAreaActivity","获取飞比设备");
//                            //获取飞比设备
//                            serial.getDevices();
                        }
                        hideLoadingDialog();
                    }
                });

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };

        //注册RXbus接收飞比设备并转换成统一对象MyDeviceInfo
       Subscription mSubscription01 = RxBus.getInstance().toObservable(DeviceInfo.class)
                .compose(TransformUtils.<DeviceInfo>defaultSchedulers())
                .subscribe(new Action1<DeviceInfo>() {
                    @Override
                    public void call(DeviceInfo event) {
                        hideLoadingDialog();
                        if(null != event){
                            for (int i = 0; i <deviceUid.size() ; i++) {
                                if(!deviceUid.contains(event.getUId()))return;
                                for (int j = 0; j < deviceInfos.size(); j++) {
                                    if (deviceInfos.get(j).getUId() == event.getUId()) {
                                        return;
                                    }
                                }
                                if(linerAdd.getVisibility()==View.VISIBLE){
                                    linerAdd.setVisibility(View.GONE);
                                }
                                deviceInfos.add(event);
                                MyDeviceInfo bean=new MyDeviceInfo();
                                bean.setSupplier(FactoryType.FBEE);
                                String type="";
                                switch (event.getDeviceId()){
                                    case DeviceList.DEVICE_ID_DOOR_LOCK:
                                        type ="门锁";
                                        break;
                                    case DeviceList.DEVICE_ID_COLOR_TEMP1:
                                    case DeviceList.DEVICE_ID_COLOR_TEMP2:
                                        type ="色温灯";
                                        break;
                                    case DeviceList.DEVICE_ID_COLOR_PHILIPS:
                                        type="彩灯";
                                        break;
                                    case DeviceList.DEVICE_ID_SOCKET:
                                        type="开关/插座";
                                        break;
                                    case DeviceList.DEVICE_ID_SWITCH:
                                        type="智能开关";
                                        break;
                                    //窗帘
                                    case DeviceList.DEVICE_ID_CURTAIN :
                                        type="窗帘";
                                        break;

                                }
                                bean.setDeviceType(type);
                                bean.setId(String.valueOf(event.getUId()));
                                bean.setName(event.getDeviceName());
                                addDecices.add(bean);
                                addAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                },onErrorAction);
        mCompositeSubscription.add(mSubscription);
        mCompositeSubscription.add(mSubscription01);
        //获取区域成员
        serial.getGroupMember(groupInfo.getGroupId(),null);

        //接收区域名改变
        receiveChangeName();

        //接收区域设备改变
        receiveAreaDevicesChange();
    }



    /**
     * 接收区域名改变
     */
    private void receiveChangeName(){
        //接收添加区域
        Subscription mSubscription03 = RxBus.getInstance().toObservable(AreaNameChange.class)
                .compose(TransformUtils.<AreaNameChange>defaultSchedulers())
                .subscribe(new Action1<AreaNameChange>() {
                    @Override
                    public void call(AreaNameChange event) {
                        if(event==null)return;
                        if(groupInfo.getGroupId()==event.getGroupId()){
                            title.setText(event.getGroupName());
                        }
                    }
                });

        mCompositeSubscription.add(mSubscription03);
    }


    /**
     * 接收区域设备改变
     */
    private void receiveAreaDevicesChange(){
        //接收添加区域
        Subscription mSubscription03 = RxBus.getInstance().toObservable(AreaDevicesChangeEvent.class)
                .compose(TransformUtils.<AreaDevicesChangeEvent>defaultSchedulers())
                .subscribe(new Action1<AreaDevicesChangeEvent>() {
                    @Override
                    public void call(AreaDevicesChangeEvent event) {
                        if(event==null)return;
                        if(groupInfo.getGroupId()==event.getGroupId()){
                            showLoadingDialog(null);
                            addDecices.clear();
                            deviceUid.clear();
                            deviceInfos.clear();
                            //获取区域成员
                            serial.getGroupMember(groupInfo.getGroupId(),null);
                        }
                    }
                });

        mCompositeSubscription.add(mSubscription03);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
            break;
            //全开
            case R.id.all_open_linear_item_area:
                allOpen();
                break;
            //组调
            case R.id.group_measure_linear_item_area:
                if(dialog == null){
                    dialog =new DialogGroupSet(this, groupInfo.getGroupId());
                }
                if(!dialog.isShowing()){
                    dialog.show();
                }

                break;
            //全关
            case R.id.all_close_linear_item_area:
                allClose();
            break;

            //向区域中添加编辑设备
            case R.id.tv_right_menu:
                Intent intent1=new Intent(this,AreaDevicesEditActivity.class);
                intent1.putStringArrayListExtra(GROUPINFOS,groupNames);
                intent1.putExtra(AreaFragment.GROUPINFO,groupInfo);
                startActivity(intent1);
                break;
        }
    }
    public  void  allOpen(){
        showLoadingDialog("请稍后...");
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret= serial.setGroupState(groupInfo.getGroupId(),(byte)1);
                subscriber.onNext(ret);
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        ToastUtils.showShort("执行失败!");
                    }

                    @Override
                    public void onNext(Integer ret) {
                        hideLoadingDialog();
                        if(ret>=0){
                            ToastUtils.showShort("执行成功!");
                        }else {
                            ToastUtils.showShort("执行失败!");
                        }
                    }
                });
        mCompositeSubscription.add(sub);
    }
    public  void  allClose(){
        showLoadingDialog("请稍后...");
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret= serial.setGroupState(groupInfo.getGroupId(),(byte)0);
                subscriber.onNext(ret);
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        ToastUtils.showShort("执行失败!");
                    }

                    @Override
                    public void onNext(Integer ret) {
                        hideLoadingDialog();
                        if(ret>=0){
                            ToastUtils.showShort("执行成功!");
                        }else {
                            ToastUtils.showShort("执行失败!");
                        }
                    }
                });
        mCompositeSubscription.add(sub);
    }
    @Override
    public void onItemClick(View view, int position) {
        if(FactoryType.FBEE.equals(addDecices.get(position).getSupplier())){
            for (int i = 0; i <deviceInfos.size() ; i++) {
                if(addDecices.get(position).getId().equals(String.valueOf(deviceInfos.get(i).getUId()))){
                    DeviceInfo info=deviceInfos.get(i);
                    switch (info.getDeviceId()){
                        //门锁
                        case DeviceList.DEVICE_ID_DOOR_LOCK:
                            Intent intent=new Intent(this,DoorLockActivity.class);
                            intent.putExtra(itemDviceInfo,info);
                            startActivityForResult(intent,requestCode);
                            break;
                        //色温灯
                        case DeviceList.DEVICE_ID_COLOR_TEMP1:
                        case DeviceList.DEVICE_ID_COLOR_TEMP2:
                            DialogTemperature dialog = new DialogTemperature(this,info);
                            dialog.show();
                            break;
                        //彩灯
                        case DeviceList.DEVICE_ID_COLOR_PHILIPS:
                            DialogColor colorDialog  = new DialogColor(this,info);
                            colorDialog.show();
                            break;
                        //开关插座
                        case DeviceList.DEVICE_ID_SOCKET:
                            /*int  status=info.getDeviceStatus();
                            if(status<=0){
                                ToastUtils.showShort("设备不在线!");
                                return;
                            }*/
                            DialogSwitch dialogSwitch=new DialogSwitch(this,info);
                            dialogSwitch.show();

                           /* int  status=info.getDeviceStatus();
                            if(status<=0){
                                ToastUtils.showShort("设备不在线!");
                                return;
                            }
                            showPop(parentView,info);*/

                            break;
                        //窗帘
                        case DeviceList.DEVICE_ID_CURTAIN:
                                new DialogCurtain(this,info).show();
                            break;
                        //智能开关
                        case DeviceList.DEVICE_ID_SWITCH:
                            DialogSwitch zhinengSwitch = new DialogSwitch(this, info);
                            zhinengSwitch.show();
                            break;
                        default:
                            //未知设备

                            break;

                    }
                    return;
                }
            }
        }else if(FactoryType.EQUES.equals(addDecices.get(position).getSupplier())){

        }
    }

    private void showPop(View v,final DeviceInfo info) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_switch_item_area, null);
        // 设置按钮的点击事件
        TextView yes= (TextView) contentView.findViewById(R.id.yes_pop_switch);
        TextView no= (TextView) contentView.findViewById(R.id.no_pop_switch);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog("请稍后....");
                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        //                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);

                        int ret= serial.setDeviceState(info,1);

                        subscriber.onNext(ret);
                        subscriber.onCompleted();
                    }

                }).compose(TransformUtils.<Integer>defaultSchedulers())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                hideLoadingDialog();
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoadingDialog();
                                ToastUtils.showShort("打开失败!");
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onNext(Integer ret) {
                                hideLoadingDialog();
                                if(ret>0){
                                    ToastUtils.showShort("开关已打开!");
                                }
                                popupWindow.dismiss();
                            }
                        });
                mCompositeSubscription.add(sub);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog("请稍后....");
                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        //                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);

                        int ret= serial.setDeviceState(info,0);

                        subscriber.onNext(ret);
                        subscriber.onCompleted();
                    }

                }).compose(TransformUtils.<Integer>defaultSchedulers())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                hideLoadingDialog();
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoadingDialog();
                                ToastUtils.showShort("关闭失败!");
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onNext(Integer ret) {
                                hideLoadingDialog();
                                if(ret>=0){
                                    ToastUtils.showShort("开关已关闭!");
                                }
                                popupWindow.dismiss();
                            }
                        });
                mCompositeSubscription.add(sub);
            }
        });
        popupWindow = new PopupWindow(contentView,
                300, 200, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==this.requestCode&&resultCode==this.resultCode){
            int uid=data.getIntExtra("UID",0);
            String devName=data.getStringExtra("devieceName");
            for(int i=0;i<deviceInfos.size();i++){
                if(deviceInfos.get(i).getUId()==uid){
                    if(devName!=null){
                        if(devName.equals(deviceInfos.get(i).getDeviceName())){
                            break;
                        }else{
                            deviceInfos.get(i).setDeviceName(devName);
                            break;
                        }
                    }else {
                        return;
                    }
                }
            }
            for (int i = 0; i <addDecices.size() ; i++) {
                if(addDecices.get(i).getId().equals(String.valueOf(uid))){
                    if(devName!=null){
                        if(devName.equals(addDecices.get(i).getName())){
                            return;
                        }else {
                            addDecices.get(i).setName(devName);
                            addAdapter.notifyDataSetChanged();
                            return;
                        }
                    }else{
                        return;
                    }

                }
            }
        }
    }
}
