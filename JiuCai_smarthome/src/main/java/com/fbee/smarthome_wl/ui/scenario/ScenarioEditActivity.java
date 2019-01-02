package com.fbee.smarthome_wl.ui.scenario;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.ScenariioEditAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.SenceBean;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.dbutils.IconDbUtil;
import com.fbee.smarthome_wl.event.SceneDeleteEvent;
import com.fbee.smarthome_wl.event.SenceImageChangeEvent;
import com.fbee.smarthome_wl.greendao.Icon;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.chooseImage.ChooseImageActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.UriToPathUtil;
import com.fbee.zllctl.SenceData;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.Serial;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * 场景编辑类
 * @class name：com.fbee.smarthome_wl.ui.scenario
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/27 9:56
 */
public class ScenarioEditActivity extends BaseActivity implements ScenariioEditAdapter.OnSwitchListener,ScenariioEditAdapter.OnStopSeekBarTouch {
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private SwipeMenuListView listView;
    private TextView sencenameText;
    private RelativeLayout layoutScenarioImage;
    private ImageView ivScene;


    private RelativeLayout layoutScenarioName;
    private TextView tvName;
    private LinearLayout layoutAddDevice;
    private EditText etName;
    private AlertDialog alertDialog;

//    private List<DeviceInfo>  list;
    private List<SenceData>  list;
    private List<SenceData> tempList;
    private ScenariioEditAdapter adapter;
    private String name;
    private List<SenceInfo> senceInfos;
    private int tag;
    private SenceBean senbean;
    private int imageFrom;
    private  int resImage;
    private String imgPath = null;
    private long  id = -1;
    private Icon icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_addorupdate);
    }


    @Override
    protected void initView() {
        layoutScenarioImage = (RelativeLayout) findViewById(R.id.layout_scenario_image);
        ivScene = (ImageView) findViewById(R.id.iv_scene);


        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        etName = (EditText) findViewById(R.id.et_name);
        layoutScenarioName = (RelativeLayout) findViewById(R.id.layout_scenario_name);
        tvName = (TextView) findViewById(R.id.tv_name);
        sencenameText = (TextView) findViewById(R.id.sencename_text);

    }

    private void verifyRecordPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    protected void initData() {
        mCompositeSubscription=new CompositeSubscription();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            verifyRecordPermissions();
        if(getIntent().hasExtra("SenceBean")){
            title.setText("场景编辑");
            layoutScenarioName.setOnClickListener(this);
            senbean= (SenceBean) getIntent().getSerializableExtra("SenceBean");
            list= senbean.getSenceDatas();
            if(list==null){
                list=new ArrayList<>();
            }
            tempList = new ArrayList<>();
            if(senbean.getSenceDatas()!=null){
                tempList.addAll(senbean.getSenceDatas());
            }
            name=senbean.getSenceName();

            String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
            LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);
            String gateway = gw.getUsername();
            icon= IconDbUtil.getIns().getIcon(account,gateway,IconDbUtil.SENCE,name);
            if(null != icon)
            id  = icon.getId();
            etName.setVisibility(View.GONE);
            sencenameText.setVisibility(View.VISIBLE);
            sencenameText.setText(name);
            showSenceImage(name);
            tvName.setText("修改名称");
            tag=1;
        }else{
            title.setText("场景添加");
            tvName.setText("场景名称");
            etName.setVisibility(View.VISIBLE);
            sencenameText.setVisibility(View.GONE);
            senceInfos = AppContext.getmOurScenes();
            list= new ArrayList<SenceData>();
        }

        layoutScenarioImage.setOnClickListener(this);

        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("完成");
        tvRightMenu.setOnClickListener(this);


        adapter = new ScenariioEditAdapter(this, list);
        adapter.setSwitchListener(this);
        adapter.setSeekbarStopListener(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout footView = (LinearLayout) inflater.inflate(R.layout.item_listview_foot, null);//得到尾部的布局
        layoutAddDevice = (LinearLayout) footView.findViewById(R.id.layout_add_device);
        layoutAddDevice.setOnClickListener(this);

        listView.addFooterView(footView);

        listView.setAdapter(adapter);
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        break;
                }
                return false;
            }
        });

    }



    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "open" item
//            SwipeMenuItem openItem = new SwipeMenuItem(
//                    getApplicationContext());
//            // set item background
//            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                    0xCE)));
//            // set item width
//            openItem.setWidth(dp2px(90));
//            // set item title
//            openItem.setTitle("Open");
//            // set item title fontsize
//            openItem.setTitleSize(18);
//            // set item title font color
//            openItem.setTitleColor(Color.WHITE);
            // add to menu
//            menu.addMenuItem(openItem);

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(AppUtil.dp2px(ScenarioEditActivity.this,80));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

    /**
     *  修改场景名称弹出对话框
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
        title.setText("修改场景名称");
        final EditText editText= (EditText) dialogView.findViewById(R.id.tv_dialog_content);
        editText.setText(name);
        editText.setSelection(name.length());
        TextView cancleText= (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
        TextView confirmText= (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  String editName= editText.getText().toString().trim();
                if(editName==null||editName.isEmpty()){
                    ToastUtils.showShort("场景名不能为空!");
                    return;
                }

                if(null != senceInfos){
                    for (int i = 0; i <senceInfos.size() ; i++) {
                        String senceName = senceInfos.get(i).getSenceName();
                        if(senceName.equals(editName)){
                            showToast("该场景已存在");
                            return;
                        }
                    }
                }

                try {
                    final byte[]  temp = editName.getBytes("utf-8");
                    if (temp.length > 16) {
                        ToastUtils.showShort("场景名名过长!");
                        return;
                    }

                    Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            Serial mSerial = AppContext.getInstance().getSerialInstance();
                            int ret=mSerial.ChangeSceneName(senbean.getSenceId(), editName);
                            subscriber.onNext(ret);
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
                                    if(alertDialog!=null)
                                        alertDialog.dismiss();
                                    if(ret>=0){
                                        sencenameText.setText(editName);
                                        if(null != icon){
                                            icon.setName(editName);
                                            IconDbUtil.getIns().addIcon(icon);
                                        }else{
                                            addIcon(editName);
                                        }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //添加设备
            case R.id.layout_add_device:
                Intent intent=new Intent(this,AddDevicetoScenarioActivity.class);
                intent.putExtra("selected", (Serializable) list);
                startActivityForResult(intent,1001);
                break;
            case R.id.back:
                finish();
                break;
            //场景编辑时修改名称
            case R.id.layout_scenario_name:
                if(tag==1){
                    showCustomizeDialog();
                }
                 break;
            //提交
            case R.id.tv_right_menu:
                String sencename=null;
                if(tag==0){
                   sencename = etName.getText().toString().trim();

                    if(sencename.isEmpty()){
                        showToast("请输入场景名称");
                        return;
                    }
                    senceInfos= AppContext.getmOurScenes();
                    if(null != senceInfos){
                        for (int i = 0; i <senceInfos.size() ; i++) {
                            String senceName = senceInfos.get(i).getSenceName();
                            if(senceName.equals(sencename)){
                                showToast("该场景已存在");
                                return;
                            }
                        }
                    }
                }else if(tag==1){
                    sencename=sencenameText.getText().toString();
                }
                createSence(sencename);


                break;
            //添加图标
            case R.id.layout_scenario_image:
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
                        Intent i= new Intent(ScenarioEditActivity.this,ChooseImageActivity.class);
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




    private void  createSence(final String sencename){
        showLoadingDialog("加载中..");

        if(tag==0){
            if(list.size()==0){

                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        //                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                        Serial mSerial = AppContext.getInstance().getSerialInstance();
                        mSerial.addDeviceToSence(sencename,0,(short)0,(byte)0,(byte)0,(byte)0,(byte)0,0,(byte)0);
                        subscriber.onCompleted();
                    }

                }).compose(TransformUtils.<Integer>defaultSchedulers())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                hideLoadingDialog();
                                addIcon(sencename);
                                showToast("操作成功");
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

                mCompositeSubscription.add(sub);

            }else{
                Observable.from(list).map(new Func1<SenceData, Integer>() {
                    public Integer call(SenceData info) {

                        AppContext.getInstance().getSerialInstance().addDeviceToSence(
                                sencename,info.getuId(),info.getDeviceId(),info.getData1(),info.getData2(),info.getData3(),info.getData4(),0,(byte)0);


                        return -1;
                    }
                }).compose(TransformUtils.<Integer>defaultSchedulers())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                hideLoadingDialog();
                                addIcon(sencename);
                                showToast("操作成功");
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

        }else if(tag==1){
            if(list.size()==0&&tempList.size()>0){

                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        Serial mSerial = AppContext.getInstance().getSerialInstance();
                        int ret= mSerial.deleteSence(sencename);
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
                                if(ret >=0){
                                    showToast("操作成功");
                                    RxBus.getInstance().post(new SceneDeleteEvent(sencename));
                                    finish();
                                }else{
                                    showToast("操作失败");
                                }
                            }
                        });

                mCompositeSubscription.add(sub);

                /*Observable.from(tempList).map(new Func1<SenceData, Integer>() {
                    public Integer call(SenceData info) {

                        AppContext.getInstance().getSerialInstance().deleteSenceMember(sencename,info.getuId());


                        return -1;
                    }
                }).compose(TransformUtils.<Integer>defaultSchedulers())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                hideLoadingDialog();
                                showToast("操作成功");

                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoadingDialog();
                            }

                            @Override
                            public void onNext(Integer ret) {

                            }
                        });*/
            }else if(tempList.size()==0&&list.size()>0){
                Observable.from(list).map(new Func1<SenceData, Integer>() {
                    public Integer call(SenceData info) {

                        AppContext.getInstance().getSerialInstance().addDeviceToSence(
                                sencename,info.getuId(),info.getDeviceId(),info.getData1(),info.getData2(),info.getData3(),info.getData4(),0,(byte)0);


                        return -1;
                    }
                }).compose(TransformUtils.<Integer>defaultSchedulers())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                hideLoadingDialog();
                                addIcon(sencename);
                                showToast("操作成功");
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
            }else if(tempList.size()==0&&list.size()==0){
                hideLoadingDialog();
                if(null != icon){
                    if(imageFrom ==1002 && resImage>0){
                        icon.setImageurl(null);
                        icon.setImageres(resImage);
                        IconDbUtil.getIns().addIcon(icon);
                    }else if(1003 ==imageFrom && imgPath!=null) {
                        icon.setImageurl(imgPath);
                        icon.setImageres(-1);
                        IconDbUtil.getIns().addIcon(icon);
                    }
                }else{
                    addIcon(sencename);
                }

                showToast("操作成功");
                finish();
            }else{

                Observable.from(tempList).map(new Func1<SenceData, Integer>() {
                    public Integer call(SenceData info) {
                        boolean tag=false;
                        if(list != null){
                            for (int i = 0; i < list.size(); i++) {
                                if(info.getuId()==list.get(i).getuId()){
                                    tag=true;
                                    break;
                                }
                            }

                        }
                        if(!tag) {
                            AppContext.getInstance().getSerialInstance().deleteSenceMember(sencename,info.getuId());
                        }


                        return -1;
                    }
                }).compose(TransformUtils.<Integer>defaultSchedulers())
                        .subscribe(new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                Observable.from(list).map(new Func1<SenceData, Integer>() {
                                    public Integer call(SenceData info) {

                                        AppContext.getInstance().getSerialInstance().addDeviceToSence(
                                                sencename,info.getuId(),info.getDeviceId(),info.getData1(),info.getData2(),info.getData3(),info.getData4(),0,(byte)0);
                                        return -1;

                                    }
                                }).compose(TransformUtils.<Integer>defaultSchedulers())
                                        .subscribe(new Subscriber<Integer>() {
                                            @Override
                                            public void onCompleted() {
                                                hideLoadingDialog();
                                                if(null != icon){
                                                    if(imageFrom ==1002 && resImage>0){
                                                        icon.setImageurl(null);
                                                        icon.setImageres(resImage);
                                                        IconDbUtil.getIns().addIcon(icon);
                                                    }else if(1003 ==imageFrom && imgPath!=null) {
                                                        icon.setImageurl(imgPath);
                                                        icon.setImageres(-1);
                                                        IconDbUtil.getIns().addIcon(icon);
                                                    }
                                                }else{
                                                    addIcon(sencename);
                                                }
                                                showToast("操作成功");
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
                            public void onError(Throwable e) {
                                hideLoadingDialog();
                            }

                            @Override
                            public void onNext(Integer ret) {

                            }
                        });
            }

        }



    }


    private  void  addIcon(String sencename){
        String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);
        String gateway = gw.getUsername();
        if(imageFrom ==1002 && resImage>0){
            Icon icon= new Icon();
            icon.setType(IconDbUtil.SENCE);
            icon.setAccount(account);
            icon.setGateway(gateway);
            icon.setName(sencename);
            icon.setImageres(resImage);
            icon.setImageurl(null);
            if(id>0)
                icon.setId(id);
            IconDbUtil.getIns().addIcon(icon);
            RxBus.getInstance().post(new SenceImageChangeEvent());
        }else if(1003 ==imageFrom && imgPath!=null){
            Icon icon= new Icon();
            icon.setType(IconDbUtil.SENCE);
            icon.setName(sencename);
            icon.setAccount(account);
            icon.setGateway(gateway);
            icon.setImageurl(imgPath);
            icon.setImageres(null);
            if(id>0)
                icon.setId(id);
            IconDbUtil.getIns().addIcon(icon);
            RxBus.getInstance().post(new SenceImageChangeEvent());
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            switch (resultCode) {
                case RESULT_OK:
                    result(requestCode,data);
                    break;
                case 1002:
                    resImage= data.getIntExtra("res", -1);
                    ivScene.setImageResource(resImage);
                    imageFrom = 1002;
                    showSenceName(resImage);
                    break;

            default:
                break;
            }

        }catch(Exception e){

        }


    }

    private void  result(int requestCode,Intent data){
        switch (requestCode){
            case 1001:
                List<SenceData> resultList = (List<SenceData>) data.getSerializableExtra("Result");
                if(resultList != null && resultList.size() >0){
                    list.addAll(resultList);
                    adapter.notifyDataSetChanged();
                }

                break;
            case 1003:
                imageFrom = 1003;
                Uri uri = data.getData();
                if ("Xiaomi".equals(Build.MANUFACTURER)) {
                    uri = UriToPathUtil.geturi(data,ScenarioEditActivity.this);
                }
                imgPath = UriToPathUtil.getImageAbsolutePath(this, uri);
                ImageLoader.loadCropCircle(this, imgPath, ivScene, R.mipmap.default_scence);

                break;

        }

    }




    private void  showSenceName(int res){
        switch (res){
            case R.mipmap.sleep:
                etName.setText("睡眠");
                break;
            case R.mipmap.runaway:
                etName.setText("离家");
                break;
            case R.mipmap.gohome:
                etName.setText("回家");
                break;
            case R.mipmap.scence_entertainment:
                etName.setText("娱乐");
                break;
            case R.mipmap.scence_receive:
                etName.setText("会客");
                break;
            case R.mipmap.scence_rest:
                etName.setText("休息");
                break;
            case R.mipmap.scence_video:
                etName.setText("影音");
                break;
            case R.mipmap.scence_gettogether:
                etName.setText("聚会");
                break;
        }

    }

    private void showSenceImage(String name){
        String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);
        String gateway = gw.getUsername();
        Icon icon= IconDbUtil.getIns().getIcon(account,gateway,IconDbUtil.SENCE,name);
        if(null != icon){
            if(icon.getImageres() !=null && icon.getImageres()>0){
                ivScene.setImageResource(icon.getImageres());
                return;
            }else if(!TextUtils.isEmpty(icon.getImageurl())){
                ImageLoader.loadCropCircle(this, icon.getImageurl(), ivScene, R.mipmap.default_scence);
                return;
            }
        }

        if(null == name){
            ivScene.setImageResource(R.mipmap.default_scence);
            return;
        }

        switch (name){
            case "回家":
                ivScene.setImageResource(R.mipmap.gohome);
                break;
            case "离家":
                ivScene.setImageResource(R.mipmap.runaway);
                break;
            case "娱乐":
                ivScene.setImageResource(R.mipmap.scence_entertainment);
                break;
            case "睡眠":
                ivScene.setImageResource(R.mipmap.sleep);
                break;
            case "会客":
                ivScene.setImageResource(R.mipmap.scence_receive);
                break;
            case "休息":
                ivScene.setImageResource(R.mipmap.scence_rest);
                break;
            case "影音":
                ivScene.setImageResource(R.mipmap.scence_video);
                break;
            case "聚会":
                ivScene.setImageResource(R.mipmap.scence_gettogether);
                break;
            default:
                ivScene.setImageResource(R.mipmap.default_scence);
                break;








        }

    }



    /**
     * 开关监听
     * @param compoundButton
     * @param b
     * @param position
     */
    @Override
    public void onSwitchInteraction(CompoundButton compoundButton, boolean b, int position) {
        if(position< list.size()){
            if(b){
                list.get(position).setData1((byte)1);

                //            AppContext.getInstance().getSerialInstance().setDeviceState(list.get(position),(byte)1);
            }else{
                list.get(position).setData1((byte)0);
                //            AppContext.getInstance().getSerialInstance().setDeviceState(list.get(position),(byte)0);
            }
        }


    }

    //亮度
    @Override
    public void onStopLightTouch(int progress, int position) {
        list.get(position).setData2((byte)progress);
    }

    //色温
    @Override
    public void onStopTempTouch(int progress, int position) {
        list.get(position).setData3((byte)progress);
        list.get(position).setData4((byte)progress);

    }

    //颜色
    @Override
    public void onColorResult(byte color, byte sat, int position) {
        list.get(position).setData3(color);
        list.get(position).setData4(sat);
    }




}
