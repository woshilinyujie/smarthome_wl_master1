package com.fbee.smarthome_wl.ui.areamanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.AreaManagerAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.AreaAddEvent;
import com.fbee.smarthome_wl.event.AreaDeleteEvent;
import com.fbee.smarthome_wl.event.AreaNameChange;
import com.fbee.smarthome_wl.ui.choseareaorsensces.AddAreaActicity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.Serial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.ui.main.area.AreaFragment.GROUPINFOS;

public class AreaManagerActivity extends BaseActivity {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private LinearLayout rlNodata;
    private TextView tvNodata;
    private SwipeMenuListView listViewAreaManager;
    private List<GroupInfo> mDatas;
    private AreaManagerAdapter adapter;
    public static final String GROUPINFO ="groupInfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_manager);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        listViewAreaManager = (SwipeMenuListView) findViewById(R.id.listView_area_manager);
        rlNodata = (LinearLayout) findViewById(R.id.rl_nodata);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);

    }

    @Override
    protected void initData() {
        tvNodata.setText("空空如也..请添加区域哦..");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("区域管理");
        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        ivRightMenu.setOnClickListener(this);
        mCompositeSubscription  = getCompositeSubscription();
        showLoadingDialog(null);
        mDatas = new ArrayList<>();
        adapter= new AreaManagerAdapter(this,mDatas);
        listViewAreaManager.setAdapter(adapter);
        listViewAreaManager.setMenuCreator(creator);
        listViewAreaManager.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showDelateDialog(AreaManagerActivity.this,position);
                        //delateArea(position);
                        break;
                }
                return false;
            }
        });
        listViewAreaManager.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1=new Intent(AreaManagerActivity.this,AreaDevicesEditActivity.class);
                ArrayList<String> groupNames=new ArrayList<>();
                for (int j = 0; j<mDatas.size() ; j++) {
                    if(i!=j){
                        groupNames.add(mDatas.get(j).getGroupName());
                    }
                }
                intent1.putStringArrayListExtra(GROUPINFOS,groupNames);
                intent1.putExtra(GROUPINFO,mDatas.get(i));
                startActivity(intent1);
            }
        });
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
                        if (mDatas.size() == 0) {
                            hideLoadingDialog();
                        }
                    }
                },onErrorAction);
        mCompositeSubscription.add(subscription1);

        //获取从Serial 中newGroup_CallBack方法中回调的区域列表
        Subscription subscription= RxBus.getInstance().toObservable(GroupInfo.class) .compose(TransformUtils.<GroupInfo>defaultSchedulers()).subscribe(new Action1<GroupInfo>() {
            @Override
            public void call(GroupInfo groupInfo) {
                if(groupInfo==null)return;
                if(rlNodata.getVisibility()==View.VISIBLE){
                    rlNodata.setVisibility(View.GONE);
                }
                for(int i=0;i<mDatas.size();i++){
                    if(mDatas.get(i).getGroupName().equals(groupInfo.getGroupName())){
                        return;
                    }
                }
                mDatas.add(0,groupInfo);
                adapter.notifyDataSetChanged();
                hideLoadingDialog();
            }
        },onErrorAction);
        mCompositeSubscription.add(subscription);
        //获取区域
        AppContext.getInstance().getSerialInstance().getGroups();


        //接收添加区域
        Subscription mSubscription03 = RxBus.getInstance().toObservable(AreaAddEvent.class)
                .compose(TransformUtils.<AreaAddEvent>defaultSchedulers())
                .subscribe(new Action1<AreaAddEvent>() {
                    @Override
                    public void call(AreaAddEvent event) {
                        mDatas.clear();
                        adapter.notifyDataSetChanged();
                        if(rlNodata.getVisibility()==View.GONE){
                            rlNodata.setVisibility(View.VISIBLE);
                        }
                        //获取区域列表
                        AppContext.getInstance().getSerialInstance().getGroups();
                    }
                });

        mCompositeSubscription.add(mSubscription03);

        //接收区域名改变
        receiveChangeName();
    }

    /**
     * 接收区域名改变
     */
    private void receiveChangeName(){
        Subscription mSubscription03 = RxBus.getInstance().toObservable(AreaNameChange.class)
                .compose(TransformUtils.<AreaNameChange>defaultSchedulers())
                .subscribe(new Action1<AreaNameChange>() {
                    @Override
                    public void call(AreaNameChange event) {
                        if(event==null)return;
                        for (int i = 0; i <mDatas.size() ; i++) {
                            if(mDatas.get(i).getGroupId()==event.getGroupId()){
                                mDatas.get(i).setGroupName(event.getGroupName());
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }
                });

        mCompositeSubscription.add(mSubscription03);
    }
    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(AppUtil.dp2px(AreaManagerActivity.this,75));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };
    private  void showDelateDialog(final Context context, final int position) {
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("是否确定删除？").cancelable(false).title("提示")
                .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("删除");

        DialogManager.showDialog(context, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                //判断是否有网络
                if (!AppUtil.isNetworkAvailable(AreaManagerActivity.this)) {
                    showToast("当前无网络，请连接网络！");
                    return;
                }
                delateArea(position);
            }
        });
    }

    private void delateArea(final int position){
        showLoadingDialog("正在删除区域");
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                mSerial.deleteGroup(mDatas.get(position).getGroupName());
                subscriber.onCompleted();
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        RxBus.getInstance().post(new AreaDeleteEvent(mDatas.get(position).getGroupName()));
                        mDatas.remove(mDatas.get(position));
                        adapter.notifyDataSetChanged();
                        hideLoadingDialog();
                        if(mDatas.size()==0){
                            if(rlNodata.getVisibility()==View.GONE){
                                rlNodata.setVisibility(View.VISIBLE);
                            }
                        }
                        showToast("删除成功");
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

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
            break;
            case R.id.iv_right_menu:
                ArrayList<String> groupNames=new ArrayList<>();
                for (int i = 0; i <mDatas.size() ; i++) {
                    groupNames.add(mDatas.get(i).getGroupName());
                }
                Intent intent=new Intent(AreaManagerActivity.this,AddAreaActicity.class);
                intent.putStringArrayListExtra(GROUPINFOS,groupNames);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }
}
