package com.fbee.smarthome_wl.ui.areamanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.homepageactivitysadapter.ChoseDevicesAddRecyclerAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.GroupMemberInfo;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.dbutils.IconDbUtil;
import com.fbee.smarthome_wl.event.AreaDevicesChangeEvent;
import com.fbee.smarthome_wl.event.AreaImageChangeEvent;
import com.fbee.smarthome_wl.event.AreaNameChange;
import com.fbee.smarthome_wl.greendao.Icon;
import com.fbee.smarthome_wl.layoutmanager.FullyLinearLayoutManager;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.chooseImage.ChooseImageActivity;
import com.fbee.smarthome_wl.ui.main.area.AreaFragment;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.UriToPathUtil;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.Serial;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class AreaDevicesEditActivity extends BaseActivity implements BaseRecylerAdapter.OnItemClickLitener{
    private ImageView back;
    private TextView titleName;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private TextView yixuanNullText;
    private TextView kexuanNullText;
    private TextView textName;
    private RelativeLayout linerModifyAreaname;

    public static final String GROUPINFO ="groupInfo";
    private List<DeviceInfo> mDatas;
    private Serial serial;
    private List<DeviceInfo> allDevices;
    private GroupInfo groupInfo;
    private Subscriber subscriber;
    private RecyclerView choseDevicesDeleterecyclEdit;
    private RecyclerView choseDevicesAddrecyclerEdit;
    private List<MyDeviceInfo> deleteDecices;
    private List<MyDeviceInfo> addDecices;
    private ChoseDevicesAddRecyclerAdapter addAdapter;
    private ChoseDevicesAddRecyclerAdapter deleteAdapter;
    private List<DeviceInfo> originallyDevices;
    List<DeviceInfo> deleteDevicesInfo;
    List<DeviceInfo> addDevicesInfo;
    private AlertDialog alertDialog;
    private ArrayList<String> groupNames;
    private RelativeLayout rlGroupPic;
    private ImageView ivGroup;


    private long  id = -1;
    private  Icon icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_devices_edit);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        titleName = (TextView) findViewById(R.id.title);
        yixuanNullText = (TextView) findViewById(R.id.yixuan_null_text);
        kexuanNullText = (TextView) findViewById(R.id.kexuan_null_text);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        choseDevicesDeleterecyclEdit = (RecyclerView) findViewById(R.id.chose_devices_deleterecycl_edit);
        choseDevicesAddrecyclerEdit = (RecyclerView) findViewById(R.id.chose_devices_addrecycler_edit);
        textName = (TextView) findViewById(R.id.text_name);
        linerModifyAreaname = (RelativeLayout) findViewById(R.id.liner_modify_areaname);

        rlGroupPic = (RelativeLayout) findViewById(R.id.rl_group_pic);
        ivGroup = (ImageView) findViewById(R.id.iv_group);
    }

    @Override
    protected void initData() {

        rlGroupPic.setOnClickListener(this);

        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setText("完成");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        yixuanNullText.setVisibility(View.VISIBLE);
        kexuanNullText.setVisibility(View.VISIBLE);
        linerModifyAreaname.setOnClickListener(this);
        serial= AppContext.getInstance().getSerialInstance();
        groupNames=getIntent().getStringArrayListExtra(AreaFragment.GROUPINFOS);
        if(groupNames==null)return;
        //本区域最初拥有的设备
        originallyDevices=new ArrayList<>();
        //网关所有设备
        allDevices=AppContext.getmOurDevices();
        groupInfo= (GroupInfo) getIntent().getSerializableExtra(GROUPINFO);
        if(groupInfo==null)return;
        showAreaImage(groupInfo.getGroupName());
        titleName.setText(groupInfo.getGroupName());
        textName.setText(groupInfo.getGroupName());
        mCompositeSubscription  = getCompositeSubscription();

        String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);
        String gateway = gw.getUsername();
        icon = IconDbUtil.getIns().getIcon(account,gateway,IconDbUtil.AREA,groupInfo.getGroupName());
        if(null != icon)
            id  = icon.getId();
        //初始化要删除的DeviceInfo设备
        deleteDevicesInfo=new ArrayList<>();
        //初始化要删除的转化设备（已有设备）
        deleteDecices=new ArrayList<>();


        //初始化要添加的DeviceInfo设备
        addDevicesInfo=new ArrayList<>();
        //初始化所有的转化设备（可添加设备）
        addDecices=new ArrayList<>();

        deleteAdapter=new ChoseDevicesAddRecyclerAdapter(this,deleteDecices);
        deleteAdapter.setChecked(true);
        FullyLinearLayoutManager deletem=new FullyLinearLayoutManager(this);
       // LinearLayoutManager deletem = new LinearLayoutManager(this);
        choseDevicesDeleterecyclEdit.setLayoutManager(deletem);
        choseDevicesDeleterecyclEdit.setItemAnimator(new DefaultItemAnimator());
        choseDevicesDeleterecyclEdit.setAdapter(deleteAdapter);
        deleteAdapter.setOnItemClickLitener(this);


        addAdapter = new ChoseDevicesAddRecyclerAdapter(this,addDecices);
        addAdapter.setChecked(false);
        FullyLinearLayoutManager addm=new FullyLinearLayoutManager(this);
        //LinearLayoutManager addm = new LinearLayoutManager(this);
        choseDevicesAddrecyclerEdit.setLayoutManager(addm);
        choseDevicesAddrecyclerEdit.setItemAnimator(new DefaultItemAnimator());
        choseDevicesAddrecyclerEdit.setAdapter(addAdapter);
        addAdapter.setOnItemClickLitener(this);



        deleteAdapter.setOnCheckedListener(new ChoseDevicesAddRecyclerAdapter.OnCheckedListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b, int positon) {
                if(!b){
                    MyDeviceInfo addItem=deleteDecices.get(positon);
                    DeviceInfo addItemInfo=deleteDevicesInfo.get(positon);
                    deleteDecices.remove(positon);
                    deleteDevicesInfo.remove(positon);
                    if(deleteDecices.size()==0){
                        if(yixuanNullText.getVisibility()==View.GONE){
                            yixuanNullText.setVisibility(View.VISIBLE);
                        }
                    }


                    if(addDecices.size()==0){
                        if(kexuanNullText.getVisibility()==View.VISIBLE){
                            kexuanNullText.setVisibility(View.GONE);
                        }
                    }
                    addDecices.add(0,addItem);
                    addDevicesInfo.add(0,addItemInfo);
                    addAdapter.notifyDataSetChanged();
                    deleteAdapter.notifyDataSetChanged();
                }
            }
        });


        addAdapter.setOnCheckedListener(new ChoseDevicesAddRecyclerAdapter.OnCheckedListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b, int positon) {
                if(b){
                    MyDeviceInfo deleteItem=addDecices.get(positon);
                    DeviceInfo deleteItemInfo=addDevicesInfo.get(positon);
                    addDecices.remove(positon);
                    addDevicesInfo.remove(positon);
                    addAdapter.notifyDataSetChanged();
                    if(addDecices.size()==0){
                        if(kexuanNullText.getVisibility()==View.GONE){
                            kexuanNullText.setVisibility(View.VISIBLE);
                        }
                    }


                    if(deleteDecices.size()==0){
                        if(yixuanNullText.getVisibility()==View.VISIBLE){
                            yixuanNullText.setVisibility(View.GONE);
                        }
                    }
                    deleteDecices.add(0,deleteItem);
                    deleteDevicesInfo.add(0,deleteItemInfo);
                    deleteAdapter.notifyDataSetChanged();

                }
            }
        });
        //注册RXbus接收groupMember_CallBack(short groupId, int[] deviceUid)中数据
        Subscription groupSubscription = RxBus.getInstance().toObservable(GroupMemberInfo.class)
                .compose(TransformUtils.<GroupMemberInfo>defaultSchedulers())
                .subscribe(new Action1<GroupMemberInfo>() {
                    @Override
                    public void call(GroupMemberInfo event) {
                        if(event==null)return;
                        if(groupInfo.getGroupId()!=event.getGroupId())return;
                        for (int l = 0; l <allDevices.size() ; l++) {
                            boolean tag=false;
                            for (int i = 0; i <event.getDeviceUid().length ; i++) {

                                if(event.getDeviceUid()[i]==allDevices.get(l).getUId()){
                                    for (int j = 0; j <originallyDevices.size() ; j++) {
                                        if(event.getDeviceUid()[i]==originallyDevices.get(j).getUId()){
                                            return;
                                        }
                                    }
                                    //最初设备
                                    originallyDevices.add(allDevices.get(l));

                                    MyDeviceInfo bean=new MyDeviceInfo();
                                    bean.setSupplier("fbee");
                                    String type="";
                                    switch (allDevices.get(l).getDeviceId()){
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
                                        case DeviceList.DEVICE_ID_CURTAIN :
                                            type="窗帘";
                                            break;
                                        case DeviceList.DEVICE_ID_SOCKET:
                                            type="插座";
                                            break;
                                        case DeviceList.DEVICE_ID_SWITCH:
                                            type="开关";
                                            break;

                                    }
                                    bean.setDeviceType(type);
                                    bean.setId(String.valueOf(allDevices.get(l).getUId()));
                                    bean.setName(allDevices.get(l).getDeviceName());
                                    deleteDevicesInfo.add(allDevices.get(l));
                                    deleteDecices.add(bean);
                                    if(yixuanNullText.getVisibility()==View.VISIBLE){
                                        yixuanNullText.setVisibility(View.GONE);
                                    }
                                    deleteAdapter.notifyDataSetChanged();
                                    tag=true;
                                }
                            }
                            if(!tag){
                                MyDeviceInfo bean=new MyDeviceInfo();
                                bean.setSupplier("fbee");
                                String type="";
                                switch (allDevices.get(l).getDeviceId()){
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
                                    case DeviceList.DEVICE_ID_CURTAIN :
                                        type="窗帘";
                                        break;
                                    case DeviceList.DEVICE_ID_SOCKET:
                                        type="插座";
                                        break;
                                    case DeviceList.DEVICE_ID_SWITCH:
                                        type="开关";
                                        break;

                                }
                                bean.setDeviceType(type);
                                bean.setId(String.valueOf(allDevices.get(l).getUId()));
                                bean.setName(allDevices.get(l).getDeviceName());
                                addDevicesInfo.add(allDevices.get(l));
                                addDecices.add(bean);
                                if(kexuanNullText.getVisibility()==View.VISIBLE){
                                    kexuanNullText.setVisibility(View.GONE);
                                }
                                addAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        mCompositeSubscription.add(groupSubscription);
        //获取区域成员
        serial.getGroupMember(groupInfo.getGroupId(),null);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

            //完成
            case R.id.tv_right_menu:

                if(originallyDevices.size()==0&&deleteDevicesInfo.size()>0){

                    addDevicesToArea(deleteDevicesInfo);
                }else if(originallyDevices.size()>0&&deleteDevicesInfo.size()==0){
                    deleteDevicesFromArea(originallyDevices);
                }else if(originallyDevices.size()==0&&deleteDevicesInfo.size()==0){
                    hideLoadingDialog();
                    showToast("操作成功");
                    finish();
                }else{
                    //需要添加的设备
                    List<DeviceInfo> needAddDevicesInfo=new ArrayList<>();
                    //需要删除的设备
                    List<DeviceInfo> needDeleteDevicesInfo=new ArrayList<>();
                    for (int i = 0; i <deleteDevicesInfo.size() ; i++) {
                        for (int j = 0; j <originallyDevices.size() ; j++) {
                            if(originallyDevices.contains(deleteDevicesInfo.get(i)))continue;
                            needAddDevicesInfo.add(deleteDevicesInfo.get(i));
                        }
                    }

                    for (int i = 0; i < originallyDevices.size(); i++) {
                        for (int j = 0; j <deleteDevicesInfo.size() ; j++) {
                            if(deleteDevicesInfo.contains(originallyDevices.get(i)))continue;
                            needDeleteDevicesInfo.add(originallyDevices.get(i));
                        }
                    }

                    //添加和删除设备
                    addorDeleteDevicesToArea(needAddDevicesInfo,needDeleteDevicesInfo);

                }

                addIcon(textName.getText().toString());


                break;
            case R.id.liner_modify_areaname:
                //修改区域名称弹出对话框
                showCustomizeDialog();
                break;
            //选择图标
            case R.id.rl_group_pic:
                dialogList();
                break;


        }
    }


    /**
     * 列表
     */
    private void dialogList() {
        final String items[] = {"默认图标", "相册选取"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which){
                    case 0:
                        Intent i= new Intent(AreaDevicesEditActivity.this,ChooseImageActivity.class);
                        i.putExtra("type","sence");
                        startActivityForResult(i,1001);
                        break;
                    case 1:
                        Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
                        if (Build.VERSION.SDK_INT < 19) {
                            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
                        } else {
                            innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        }
                        innerIntent.setType("image/*");
                        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择头像");
                        startActivityForResult(wrapperIntent, 1003);
                        break;

                }

            }
        });

        builder.create().show();
    }


    private String imgPath = null;
    private int  resImage =-1;
    private int imageFrom;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1002:
                resImage = data.getIntExtra("res", -1);
                ivGroup.setImageResource(resImage);
                imageFrom = 1002;
                showAreaName(resImage);
                break;
            case RESULT_OK:
                imageFrom = 1003;
                Uri uri = data.getData();
                if ("Xiaomi".equals(Build.MANUFACTURER)) {
                    uri = UriToPathUtil.geturi(data,AreaDevicesEditActivity.this);
                }
                imgPath = UriToPathUtil.getImageAbsolutePath(this, uri);
                ImageLoader.loadCropCircle(this, imgPath, ivGroup, R.mipmap.default_scence);
                break;

        }
    }

    private void showAreaImage(String name){
        String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);
        String gateway = gw.getUsername();
        Icon icon= IconDbUtil.getIns().getIcon(account,gateway,IconDbUtil.AREA,name);
        if(null != icon){
            if(icon.getImageres() !=null && icon.getImageres()>0){
                ivGroup.setImageResource(icon.getImageres());
                return;
            }else if(!TextUtils.isEmpty(icon.getImageurl())){
                ImageLoader.loadCropCircle(this, icon.getImageurl(), ivGroup, R.mipmap.default_scence);
                return;
            }
        }

        switch (name){
            case "主卧":
                ivGroup.setImageResource(R.mipmap.master_bedroom);
                break;
            case "次卧":
                ivGroup.setImageResource(R.mipmap.second_bedroom);
                break;
            case "客厅":
                ivGroup.setImageResource(R.mipmap.living_room);
                break;
            case "阁楼":
                ivGroup.setImageResource(R.mipmap.area_cockloft);
                break;
            case "保姆房":
                ivGroup.setImageResource(R.mipmap.area_nanny_room);
                break;
            case "卫生间":
                ivGroup.setImageResource(R.mipmap.area_tolet);
                break;
            case "地下室":
                ivGroup.setImageResource(R.mipmap.area_undercroft);
                break;
            default:
                ivGroup.setImageResource(R.mipmap.default_area);
                break;
        }

    }
    private void  showAreaName(int res){
        switch (res){
            case R.mipmap.master_bedroom:
                textName.setText("主卧");
                break;
            case R.mipmap.second_bedroom:
                textName.setText("次卧");
                break;
            case R.mipmap.living_room:
                textName.setText("客厅");
                break;
            case R.mipmap.area_cockloft:
                textName.setText("阁楼");
                break;
            case R.mipmap.area_nanny_room:
                textName.setText("保姆房");
                break;
            case R.mipmap.area_tolet:
                textName.setText("卫生间");
                break;
            case R.mipmap.area_undercroft:
                textName.setText("地下室");
                break;

        }

    }


    private  void  addIcon(String name){
        String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);
        String gateway = gw.getUsername();
        if(imageFrom ==1002 && resImage>0){
            Icon icon= new Icon();
            icon.setType(IconDbUtil.AREA);
            icon.setAccount(account);
            icon.setGateway(gateway);
            icon.setName(name);
            icon.setImageres(resImage);
            icon.setImageurl(null);
            if(id>0)
                icon.setId(id);
            IconDbUtil.getIns().addIcon(icon);
            RxBus.getInstance().post(new AreaImageChangeEvent());
        }else if(1003 ==imageFrom && imgPath!=null){
            Icon icon= new Icon();
            icon.setType(IconDbUtil.AREA);
            icon.setName(name);
            icon.setAccount(account);
            icon.setGateway(gateway);
            icon.setImageurl(imgPath);
            icon.setImageres(null);
            if(id>0)
                icon.setId(id);
            IconDbUtil.getIns().addIcon(icon);
            RxBus.getInstance().post(new AreaImageChangeEvent());
        }
    }

    /**
     *  修改区域名称弹出对话框
     */
    private void showCustomizeDialog() {

    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_modify_doolock_name,null);
        TextView title= (TextView) dialogView.findViewById(R.id.tv_title);
        title.setText("修改区域名称");
        final EditText editText= (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        editText.setText(groupInfo.getGroupName());
        editText.setSelection(groupInfo.getGroupName().length());
        TextView cancleText= (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText= (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog("请稍后....");
                final  String editName= editText.getText().toString().trim();
                if(editName==null||editName.isEmpty()){
                    ToastUtils.showShort("区域名不能为空!");
                    hideLoadingDialog();
                    return;
                }

                for (int i = 0; i <groupNames.size() ; i++) {
                    String senceName = groupNames.get(i);
                    if(senceName.equals(editName)){
                        showToast("该区域已存在!");
                        hideLoadingDialog();
                        return;
                    }
                }
                try {
                    final byte[]  temp = editName.getBytes("utf-8");
                    if (temp.length > 16) {
                        ToastUtils.showShort("区域名过长!");
                        hideLoadingDialog();
                        return;
                    }
                if(editName.equals(textName.getText().toString())){
                    hideLoadingDialog();
                    ToastUtils.showShort("修改成功!");
                    if(alertDialog!=null)
                        alertDialog.dismiss();
                    return;
                }
                    Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            Serial mSerial = AppContext.getInstance().getSerialInstance();
                            int ret=mSerial.changeGroupName(groupInfo.getGroupId(), editName.getBytes());
                            subscriber.onNext(ret);
                            subscriber.onCompleted();
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
                                }

                                @Override
                                public void onNext(Integer ret) {
                                    hideLoadingDialog();
                                    if(alertDialog!=null)
                                        alertDialog.dismiss();
                                    if(ret>=0){
                                        groupInfo.setGroupName(editName);
                                        titleName.setText(editName);
                                        textName.setText(editName);
                                       if(null != icon){
                                           icon.setName(editName);
                                           IconDbUtil.getIns().addIcon(icon);
                                       }
                                        RxBus.getInstance().post(new AreaNameChange(groupInfo.getGroupId(),editName));
                                        ToastUtils.showShort("修改成功!");
                                    }else {
                                        ToastUtils.showShort("修改失败!");
                                    }

                                }
                            });
                    mCompositeSubscription.add(sub);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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

    private void addDevicesToArea(List<DeviceInfo> needAddDevicesInfo){
        showLoadingDialog("请稍后...");
        Observable.from(needAddDevicesInfo).map(new Func1<DeviceInfo, Integer>() {
            public Integer call(DeviceInfo info) {
                int ret=-1;
                if(info!=null){
                    //添加设备
                    ret= serial.addDeviceToGroup(info,groupInfo);
                }

                return ret;
            }
        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                            hideLoadingDialog();
                            showToast("操作成功");
                            RxBus.getInstance().post(new AreaDevicesChangeEvent(groupInfo.getGroupId()));
                            finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(Integer ret) {

                    }
                });
    }
    /**
     * 添加设备到区域
     * @param needAddDevicesInfo
     */
    private void  addorDeleteDevicesToArea(List<DeviceInfo> needAddDevicesInfo,final List<DeviceInfo> needDeleteDevicesInfo){
        showLoadingDialog("请稍后...");
        Observable.from(needAddDevicesInfo).map(new Func1<DeviceInfo, Integer>() {
            public Integer call(DeviceInfo info) {
                int ret=-1;
                if(info!=null){
                    //添加设备
                    ret= serial.addDeviceToGroup(info,groupInfo);
                }

                return ret;
            }
        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        if(needDeleteDevicesInfo.size()>0){
                            deleteDevicesFromArea(needDeleteDevicesInfo);
                        }else{
                            hideLoadingDialog();
                            showToast("操作成功");
                            RxBus.getInstance().post(new AreaDevicesChangeEvent(groupInfo.getGroupId()));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(Integer ret) {

                    }
                });


    }

    /**
     * 从区域中删除设备
     * @param needDeleteDevicesInfo
     */
    public  void deleteDevicesFromArea(List<DeviceInfo> needDeleteDevicesInfo){
        Observable.from(needDeleteDevicesInfo).map(new Func1<DeviceInfo, Integer>() {
            public Integer call(DeviceInfo info) {
                //删除设备
                int ret=serial.deleteDeviceFromGroup(info,groupInfo);

                return ret;
            }
        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingDialog();
                        showToast("操作成功");
                        RxBus.getInstance().post(new AreaDevicesChangeEvent(groupInfo.getGroupId()));
                        finish();

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(Integer ret) {

                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscriber!=null&&!subscriber.isUnsubscribed()){
            subscriber.unsubscribe();
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
