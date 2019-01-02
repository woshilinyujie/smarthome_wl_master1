package com.fbee.smarthome_wl.ui.choseareaorsensces;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
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
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.dbutils.IconDbUtil;
import com.fbee.smarthome_wl.event.AreaAddEvent;
import com.fbee.smarthome_wl.greendao.Icon;
import com.fbee.smarthome_wl.layoutmanager.FullyLinearLayoutManager;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.chooseImage.ChooseImageActivity;
import com.fbee.smarthome_wl.ui.main.area.AreaFragment;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.UriToPathUtil;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.Serial;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AddAreaActicity extends BaseActivity implements BaseRecylerAdapter.OnItemClickLitener{
    private ImageView back;
    private TextView title;
    private TextView tvRightMenu;
    private EditText editGroupNameAreaActicity;
    private RecyclerView choseDevicesAddrecyclerview;
    private RecyclerView choseDevicesDeleterecyclerview;
    private ChoseDevicesAddRecyclerAdapter addAdapter;
    private ChoseDevicesAddRecyclerAdapter deleteAdapter;
    private TextView yixuanNullText;
    private TextView kexuanNullText;
    private ArrayList<String> groupNames;
    private List<EquesListInfo.bdylistEntity> bdylist;
    private List<DeviceInfo> deviceInfos;
    private List<MyDeviceInfo> deleteDecices;
    private List<MyDeviceInfo> addDecices;
    private Serial serial;
    private  String editName;
    private Subscriber<Integer> subscriber;
    private List<GroupInfo> groupInfos;
    private RelativeLayout rlGroupPic;
    private ImageView ivGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_area);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        yixuanNullText = (TextView) findViewById(R.id.yixuan_null_text);
        kexuanNullText = (TextView) findViewById(R.id.kexuan_null_text);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        editGroupNameAreaActicity = (EditText) findViewById(R.id.edit_group_name_area_acticity);
        choseDevicesAddrecyclerview = (RecyclerView) findViewById(R.id.chose_devices_addrecyclerview);
        choseDevicesDeleterecyclerview = (RecyclerView) findViewById(R.id.chose_devices_deleterecyclerview);

        rlGroupPic = (RelativeLayout) findViewById(R.id.rl_group_pic);
        ivGroup = (ImageView) findViewById(R.id.iv_group);
    }

    @Override
    protected void initData() {
        mCompositeSubscription  =getCompositeSubscription();
        rlGroupPic.setOnClickListener(this);

        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("区域编辑");
        tvRightMenu.setText("完成");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        yixuanNullText.setVisibility(View.VISIBLE);
        kexuanNullText.setVisibility(View.VISIBLE);
        serial=AppContext.getInstance().getSerialInstance();
        groupNames=getIntent().getStringArrayListExtra(AreaFragment.GROUPINFOS);
        //初始化组列表
        groupInfos=new ArrayList<>();
        //初始化要删除的设备（已有设备）
        deleteDecices=new ArrayList<>();
        //初始化所有的设备（可添加设备）
        addDecices=new ArrayList<>();
        //猫眼设备
        //bdylist = AppContext.getBdylist();

        //飞比设备
        deviceInfos = new ArrayList<DeviceInfo>();

        /*//猫眼设备转换成统一对象MyDeviceInfo
        if(bdylist!=null&&bdylist.size()>0){
            kexuanNullText.setVisibility(View.GONE);
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
                info.setSupplier("Eques");
                info.setDeviceType("猫眼");
                addDecices.add(info);

            }
        }*/
        addAdapter = new ChoseDevicesAddRecyclerAdapter(this,addDecices);
        addAdapter.setChecked(false);
        FullyLinearLayoutManager addm=new FullyLinearLayoutManager(this);
        choseDevicesAddrecyclerview.setLayoutManager(addm);
        choseDevicesAddrecyclerview.setNestedScrollingEnabled(false);
        choseDevicesAddrecyclerview.setItemAnimator(new DefaultItemAnimator());
        choseDevicesAddrecyclerview.setAdapter(addAdapter);
        addAdapter.setOnItemClickLitener(this);

        //注册RXbus接收飞比设备并转换成统一对象MyDeviceInfo
        mSubscription = RxBus.getInstance().toObservable(DeviceInfo.class)
                .compose(TransformUtils.<DeviceInfo>defaultSchedulers())
                .subscribe(new Action1<DeviceInfo>() {
                    @Override
                    public void call(DeviceInfo event) {
                        LogUtil.e("deviceinfo",event.toString());
                        for (int i = 0; i < deviceInfos.size(); i++) {
                            if (deviceInfos.get(i).getUId() == event.getUId()) {
                                return;
                            }
                        }
                        if(null != event){
                            if(kexuanNullText.getVisibility() ==View.VISIBLE){
                                kexuanNullText.setVisibility(View.GONE);
                            }
                            deviceInfos.add(event);
                            MyDeviceInfo bean=new MyDeviceInfo();
                            bean.setSupplier("fbee");
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
                            bean.setId(String.valueOf(event.getUId()));
                            bean.setName(event.getDeviceName());
                            addDecices.add(bean);
                            addAdapter.notifyDataSetChanged();

                        }
                    }
                });
        mCompositeSubscription.add(mSubscription);
        //获取飞比设备
        serial.getDevices();

        deleteAdapter=new ChoseDevicesAddRecyclerAdapter(this,deleteDecices);
        deleteAdapter.setChecked(true);
        FullyLinearLayoutManager deletem=new FullyLinearLayoutManager(this);
        choseDevicesDeleterecyclerview.setLayoutManager(deletem);
        choseDevicesDeleterecyclerview.setNestedScrollingEnabled(false);
        choseDevicesDeleterecyclerview.setItemAnimator(new DefaultItemAnimator());
        choseDevicesDeleterecyclerview.setAdapter(deleteAdapter);
        deleteAdapter.setOnItemClickLitener(this);


        addAdapter.setOnCheckedListener(new ChoseDevicesAddRecyclerAdapter.OnCheckedListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b, int position) {
                if(b){
                    MyDeviceInfo deleteItem=addDecices.get(position);
                    addDecices.remove(position);
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
                    deleteAdapter.notifyDataSetChanged();
                    choseDevicesDeleterecyclerview.smoothScrollToPosition(0);
                }
            }
        });

        deleteAdapter.setOnCheckedListener(new ChoseDevicesAddRecyclerAdapter.OnCheckedListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b,int position) {
                if(!b){
                    MyDeviceInfo addItem=deleteDecices.get(position);
                    deleteDecices.remove(position);
                    deleteAdapter.notifyDataSetChanged();
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
                    addAdapter.notifyDataSetChanged();
                    choseDevicesAddrecyclerview.smoothScrollToPosition(0);
                }
            }
        });

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        //获取从Serial 中newGroup_CallBack方法中回调的区域列表
        Subscription subscription= RxBus.getInstance().toObservable(GroupInfo.class) .compose(TransformUtils.<GroupInfo>defaultSchedulers()).subscribe(new Action1<GroupInfo>() {
            @Override
            public void call(GroupInfo groupInfo) {

                    if(groupInfo==null){
                        hideLoadingDialog();
                        return;
                    }
                    boolean tag=false;
                    for(int i=0;i<groupNames.size();i++){
                        if(!groupInfo.getGroupName().equals(groupNames.get(i))){
                            tag=true;
                            groupNames.add(groupInfo.getGroupName());
                            break;
                        }
                    }

                    if(tag){
                        if(editName!=null && editName.equals(groupInfo.getGroupName())){
                            List<DeviceInfo> needAddDeciceInfos=new ArrayList<DeviceInfo>();
                            for (int i = 0; i <deleteDecices.size() ; i++) {
                                MyDeviceInfo myInfo= deleteDecices.get(i);
                                for (int j = 0; j <deviceInfos.size() ; j++) {
                                    if(myInfo.getId().equals(String.valueOf(deviceInfos.get(j).getUId()))){
                                        needAddDeciceInfos.add(deviceInfos.get(j));
                                    }
                                }
                            }
                            addDevicetoGroup(needAddDeciceInfos,groupInfo);
                            return;
                        }
                    }

            }
        },onErrorAction);
        mCompositeSubscription.add(subscription);
    }
    public void addDevicetoGroup(List<DeviceInfo> devices,final GroupInfo groupInfo){
        subscriber = new Subscriber<Integer>() {
            @Override
            public void onNext(Integer s) {
            }

            @Override
            public void onCompleted() {
                RxBus.getInstance().post(new AreaAddEvent());
                hideLoadingDialog();
                showToast("添加成功!");
                finish();
            }

            @Override
            public void onError(Throwable e) {
                hideLoadingDialog();
                showToast("添加失败!");
            }
        };
       Observable observable= Observable.from(devices)
                .map(new Func1<DeviceInfo, Integer>() {
                    @Override
                    public Integer call(DeviceInfo info) {
                        return  serial.addDeviceToGroup(info,groupInfo);
                    }
                })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()); // 指定 Subscriber 的回调发生在主线程
        observable .subscribe(subscriber);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                try{
                    editName=editGroupNameAreaActicity.getText().toString().trim();
                    if (editName==null||editName.isEmpty()){
                        ToastUtils.showShort("区域名不能为空!");
                        return;
                    }
                    if(groupNames!=null){
                        for (int i = 0; i <groupNames.size() ; i++) {
                            if(editName.equals(groupNames.get(i))){
                                ToastUtils.showShort("区域名已存在!");
                                return;
                            }
                        }
                    }
                    showLoadingDialog("请稍后....");
                    Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            int result = serial.addGroup(editName);
                            subscriber.onNext(result);
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

                                    if(ret>=0){
                                        if(deleteDecices.size()==0){
                                            RxBus.getInstance().post(new AreaAddEvent());
                                            hideLoadingDialog();
                                            ToastUtils.showShort("修改成功!");
                                            finish();
                                        }else{
                                            serial.getGroups();
                                        }
                                        addIcon(editName);
                                    }else {
                                        hideLoadingDialog();
                                        ToastUtils.showShort("修改失败!");
                                    }

                                }
                            });
                    mCompositeSubscription.add(sub);


                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
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
        // builder.setMessage("是否确认退出?"); //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which){
                    case 0:
                        Intent i= new Intent(AddAreaActicity.this,ChooseImageActivity.class);
                        i.putExtra("type","area");
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
            IconDbUtil.getIns().addIcon(icon);
        }else if(1003 ==imageFrom && imgPath!=null){
            Icon icon= new Icon();
            icon.setType(IconDbUtil.AREA);
            icon.setName(name);
            icon.setAccount(account);
            icon.setGateway(gateway);
            icon.setImageurl(imgPath);
            icon.setImageres(null);
            IconDbUtil.getIns().addIcon(icon);
        }
    }


    private String imgPath = null;
    private int  resImage =-1;
    private int imageFrom;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1002:
                imageFrom = 1002;
                resImage = data.getIntExtra("res", -1);
                ivGroup.setImageResource(resImage);
                showAreaName(resImage);
                break;
            case RESULT_OK:
                imageFrom = 1003;
                Uri uri = data.getData();
                if ("Xiaomi".equals(Build.MANUFACTURER)) {
                    uri = UriToPathUtil.geturi(data,AddAreaActicity.this);
                }
                imgPath = UriToPathUtil.getImageAbsolutePath(this, uri);
                ImageLoader.loadCropCircle(this, imgPath, ivGroup, R.mipmap.default_scence);
                break;

        }
    }

    private void  showAreaName(int res){
        switch (res){
            case R.mipmap.master_bedroom:
                editGroupNameAreaActicity.setText("主卧");
                break;
            case R.mipmap.second_bedroom:
                editGroupNameAreaActicity.setText("次卧");
                break;
            case R.mipmap.living_room:
                editGroupNameAreaActicity.setText("客厅");
                break;
            case R.mipmap.area_cockloft:
                editGroupNameAreaActicity.setText("阁楼");
                break;
            case R.mipmap.area_nanny_room:
                editGroupNameAreaActicity.setText("保姆房");
                break;
            case R.mipmap.area_tolet:
                editGroupNameAreaActicity.setText("卫生间");
                break;
            case R.mipmap.area_undercroft:
                editGroupNameAreaActicity.setText("地下室");
                break;

        }

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
