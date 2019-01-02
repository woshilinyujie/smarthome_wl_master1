package com.fbee.smarthome_wl.ui.scenario;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.ScenaManagerAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.SenceBean;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.dbutils.IconDbUtil;
import com.fbee.smarthome_wl.event.SceneDeleteEvent;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.fbee.zllctl.SenceInfo;
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
import rx.subscriptions.CompositeSubscription;

public class ScenaManagerActivity extends BaseActivity {
    private LinearLayout activityScenaManager;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private SwipeMenuListView listView;
    private LinearLayout rlNodata;
    private TextView tvNodata;
    private List<SenceInfo> mDatas;
    private ScenaManagerAdapter  adapter;
    private String  senceName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scena_manager);
    }

    @Override
    protected void initView() {
        activityScenaManager = (LinearLayout) findViewById(R.id.activity_scena_manager);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        rlNodata = (LinearLayout) findViewById(R.id.rl_nodata);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);
    }

    @Override
    protected void initData() {
        mCompositeSubscription= new CompositeSubscription();
        tvNodata.setText("空空如也..请添加场景哦..");
        title.setText("场景管理");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        ivRightMenu.setOnClickListener(this);
        showLoadingDialog(null);
        mDatas = new ArrayList<>();
        adapter = new ScenaManagerAdapter(mDatas,this);

        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
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


        //接收到Serial中
        Subscription subscription= RxBus.getInstance().toObservable(SenceInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SenceInfo>() {
            @Override
            public void call(SenceInfo senceInfo) {
                if (senceInfo==null)return;
                if(rlNodata.getVisibility()==View.VISIBLE){
                    rlNodata.setVisibility(View.GONE);
                }
                for (int i = 0; i <mDatas.size() ; i++) {
                    if (mDatas.get(i)==null)
                        continue;
                    if(mDatas.get(i).getSenceId()==senceInfo.getSenceId()){
                        if (mDatas.get(i).getSenceName().equals(senceInfo.getSenceName())){
                            return;
                        }else{
                            mDatas.remove(i);
                            mDatas.add(0,senceInfo);
                            adapter.notifyDataSetChanged();
                            hideLoadingDialog();
                            return;
                        }
                    }
                }
                mDatas.add(0,senceInfo);
                adapter.notifyDataSetChanged();
                hideLoadingDialog();
            }
        },onErrorAction);
        mCompositeSubscription.add(subscription);
        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showDelateDialog(ScenaManagerActivity.this,position);
                        break;
                }
                return false;
            }
        });

        //获取场景
        AppContext.getInstance().getSerialInstance().getSences();


        //场景详情
        mSubscription = RxBus.getInstance().toObservable(SenceBean.class)
                .compose(TransformUtils.<SenceBean>defaultSchedulers())
                .subscribe(new Action1<SenceBean>() {
                    @Override
                    public void call(SenceBean event) {
                        hideLoadingDialog();
                        if(null != event){
                            Intent intent=new Intent(ScenaManagerActivity.this,ScenarioEditActivity.class);
                            event.setSenceName(senceName);
                            intent.putExtra("SenceBean",event);
                            startActivity(intent);
                        }

                    }
                });
        mCompositeSubscription.add(mSubscription);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mDatas != null && mDatas.size()>0){
                    SenceInfo info= mDatas.get(position);
                    senceName = info.getSenceName();
                    showLoadingDialog("获取场景详情中..");
                    AppContext.getInstance().getSerialInstance().getSenceDetails(info.getSenceId(),info.getSenceName());
                }

            }
        });

        //接收场景删除
        receiveDeleteScene();
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
            deleteItem.setWidth(AppUtil.dp2px(ScenaManagerActivity.this,75));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };



    private  void showDelateDialog(final Context context,final int position) {
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
                delateScene(position);
            }
        });
    }
    //接收场景删除
    private void receiveDeleteScene(){
        //删除场景后更新界面数据
        Subscription mSubscriptionDelete = RxBus.getInstance().toObservable(SceneDeleteEvent.class)
                .compose(TransformUtils.<SceneDeleteEvent>defaultSchedulers())
                .subscribe(new Action1<SceneDeleteEvent>() {
                    @Override
                    public void call(SceneDeleteEvent event) {
                        if(event==null) return;
                        for (int i = 0; i <mDatas.size() ; i++) {
                            if(event.getName().equals(mDatas.get(i).getSenceName())){
                                mDatas.remove(i);

                                break;
                            }
                        }
                        if(mDatas.size()==0){
                            if(rlNodata.getVisibility()==View.GONE){
                                rlNodata.setVisibility(View.VISIBLE);
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }
                });

        mCompositeSubscription.add(mSubscriptionDelete);
    }


    private void delateScene(final int position){
        showLoadingDialog("正在删除场景");
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                int ret= mSerial.deleteSence(mDatas.get(position).getSenceName());
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
                    }

                    @Override
                    public void onNext(Integer ret) {
                        hideLoadingDialog();
                        if(ret >=0){
                            showToast("删除成功");
                            String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
                            LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);
                            String gateway = gw.getUsername();
                            IconDbUtil.getIns().detele(account,gateway,IconDbUtil.SENCE,mDatas.get(position).getSenceName());

                            for (int i = 0; i <AppContext.getInstance().getmOurScenes().size() ; i++) {
                                if(AppContext.getInstance().getmOurScenes().get(i).getSenceName().equals(mDatas.get(position).getSenceName())){
                                    AppContext.getInstance().getmOurScenes().remove(i);
                                    break;
                                }
                            }

                            RxBus.getInstance().post(new SceneDeleteEvent(mDatas.get(position).getSenceName()));
                        }else{
                            showToast("删除失败");
                        }
                    }
                });

        mCompositeSubscription.add(sub);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.iv_right_menu:
                skipAct(ScenarioEditActivity.class);
                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }
}
