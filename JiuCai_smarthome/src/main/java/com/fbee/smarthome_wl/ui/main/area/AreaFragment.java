package com.fbee.smarthome_wl.ui.main.area;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.GroupAdapter;
import com.fbee.smarthome_wl.adapter.itemdecoration.SpaceItemDecoration;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.AreaAddEvent;
import com.fbee.smarthome_wl.event.AreaDeleteEvent;
import com.fbee.smarthome_wl.event.AreaImageChangeEvent;
import com.fbee.smarthome_wl.event.AreaNameChange;
import com.fbee.smarthome_wl.event.SwitchDataEvent;
import com.fbee.smarthome_wl.ui.choseareaorsensces.AddAreaActicity;
import com.fbee.smarthome_wl.ui.choseareaorsensces.ItemAreaActivity;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.Serial;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.area
 * @anthor create by Zhaoli.Wang
 * @time 2017/2/13 10:29
 */
public class AreaFragment extends BaseFragment implements BaseRecylerAdapter.OnItemClickLitener, OnRefreshListener, OnLoadMoreListener, View.OnClickListener {
    private ImageView back;
    private TextView title;
    private TextView tvRightMenu;
    private RecyclerView recScenario;
    private SwipeToLoadLayout swipeToLoadLayout;
    private LinearLayout rlNodata;
    private TextView tvNodata;
    private ImageView ivRightMenu;
    private GroupAdapter groupAdapter;
    private List<GroupInfo> mDatas;
    private Serial serial;
    private EditText editNmae;
    private static final int REQCODE = 300;
    public static final int RESCODE = 500;
    public static final String GROUPINFOS = "groupInfos";
    public static final String GROUPINFO = "groupInfo";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_scenario;
    }

    @Override
    public void initView() {
        mCompositeSubscription = mContext.getCompositeSubscription();
        swipeToLoadLayout = (SwipeToLoadLayout) mContentView.findViewById(R.id.swipeToLoadLayout);
        back = (ImageView) mContentView.findViewById(R.id.back);
        title = (TextView) mContentView.findViewById(R.id.title);
        ivRightMenu = (ImageView) mContentView.findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) mContentView.findViewById(R.id.tv_right_menu);
        recScenario = (RecyclerView) mContentView.findViewById(R.id.swipe_target);
        rlNodata = (LinearLayout) mContentView.findViewById(R.id.rl_nodata);
        tvNodata = (TextView) mContentView.findViewById(R.id.tv_nodata);

    }

    @Override
    public void bindEvent() {
        back.setVisibility(View.VISIBLE);
        back.setImageResource(R.mipmap.home_menu);
        back.setOnClickListener(this);
        title.setText("区域");
        tvNodata.setText("空空如也..请添加区域哦..");
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        ivRightMenu.setOnClickListener(this);
        showLoadingDialog(null);
        //初始化Serial
        serial = AppContext.getInstance().getSerialInstance();
        mDatas = new ArrayList<>();
        groupAdapter = new GroupAdapter(mContext, mDatas);
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };
        //接收当前区域
        receiveGetGroup();

        //默认列表，参数4就是几列
        GridLayoutManager gm = new GridLayoutManager(mContext, 3);
        recScenario.setLayoutManager(gm);
        recScenario.setItemAnimator(new DefaultItemAnimator());
        recScenario.addItemDecoration(new SpaceItemDecoration(AppUtil.dip2px(mContext, 10)));
        recScenario.setAdapter(groupAdapter);

        groupAdapter.setOnItemClickLitener(this);
        swipeToLoadLayout.setOnRefreshListener(this);
//        swipeToLoadLayout.setRefreshEnabled(false);
        //        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeToLoadLayout.setLoadMoreEnabled(false); //设置上拉不可用
        //获取区域列表
        serial.getGroups();

        Subscription subscription1 = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        hideLoadingDialog();
                        if (mDatas.size() == 0) {
                            serial.addGroup("客厅");
                            serial.addGroup("主卧");
                            serial.addGroup("次卧");
                        }
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);

        /*//1.6秒后更新适配器
        subscription1= Observable.timer(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                groupAdapter.notifyDataSetChanged();
            }
        });*/

        //切换网关，更新界面数据
        Subscription mSubscription02 = RxBus.getInstance().toObservable(SwitchDataEvent.class)
                .compose(TransformUtils.<SwitchDataEvent>defaultSchedulers())
                .subscribe(new Action1<SwitchDataEvent>() {
                    @Override
                    public void call(SwitchDataEvent event) {
                        mDatas.clear();
                        groupAdapter.notifyDataSetChanged();
                        rlNodata.setVisibility(View.VISIBLE);
                        //获取区域列表
                        serial.getGroups();
                    }
                });


        //删除区域后更新界面数据
        Subscription mSubscriptionDelete = RxBus.getInstance().toObservable(AreaDeleteEvent.class)
                .compose(TransformUtils.<AreaDeleteEvent>defaultSchedulers())
                .subscribe(new Action1<AreaDeleteEvent>() {
                    @Override
                    public void call(AreaDeleteEvent event) {
                        if (null != mDatas) {
                            for (int i = 0; i < mDatas.size(); i++) {
                                if (mDatas.get(i).getGroupName().equals(event.getName())) {
                                    mDatas.remove(i);
                                }
                            }
                        }
                        if (mDatas !=null && mDatas.size() == 0) {
                            rlNodata.setVisibility(View.VISIBLE);
                        }
                        groupAdapter.notifyDataSetChanged();
                    }
                });


        //接收添加区域
        Subscription mSubscription03 = RxBus.getInstance().toObservable(AreaAddEvent.class)
                .compose(TransformUtils.<AreaAddEvent>defaultSchedulers())
                .subscribe(new Action1<AreaAddEvent>() {
                    @Override
                    public void call(AreaAddEvent event) {
                        mDatas.clear();
                        groupAdapter.notifyDataSetChanged();
                        //获取区域列表
                        serial.getGroups();
                    }
                });

        mCompositeSubscription.add(mSubscription03);
        mCompositeSubscription.add(mSubscription02);
        mCompositeSubscription.add(mSubscriptionDelete);

        //接收区域名改变
        receiveChangeName();
    }


    /**
     * 接收上报区域
     */
    private void receiveGetGroup() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                receiveGetGroup();
            }
        };
        //获取从Serial 中newGroup_CallBack方法中回调的区域列表
        Subscription subscription = RxBus.getInstance().toObservable(GroupInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<GroupInfo>() {
                    @Override
                    public void call(GroupInfo groupInfo) {
                        onRefreshComplete();
                        if (groupInfo == null) return;
                        for (int i = 0; i < mDatas.size(); i++) {
                            if (mDatas.get(i).getGroupName().equals(groupInfo.getGroupName())) {
                                return;
                            }
                        }
                        rlNodata.setVisibility(View.GONE);
                        mDatas.add(0, groupInfo);
                        groupAdapter.notifyDataSetChanged();
                        hideLoadingDialog();
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription);
    }


    /**
     * 接收区域名改变
     */
    private void receiveChangeName() {
        //接收添加区域
        Subscription mSubscription03 = RxBus.getInstance().toObservable(AreaNameChange.class)
                .compose(TransformUtils.<AreaNameChange>defaultSchedulers())
                .subscribe(new Action1<AreaNameChange>() {
                    @Override
                    public void call(AreaNameChange event) {
                        if (event == null) return;
                        for (int i = 0; i < mDatas.size(); i++) {
                            if (mDatas.get(i).getGroupId() == event.getGroupId()) {
                                mDatas.get(i).setGroupName(event.getGroupName());
                                break;
                            }
                        }
                        groupAdapter.notifyDataSetChanged();

                    }
                });

        //接收添加区域
        Subscription mSubscription04 = RxBus.getInstance().toObservable(AreaImageChangeEvent.class)
                .compose(TransformUtils.<AreaImageChangeEvent>defaultSchedulers())
                .subscribe(new Action1<AreaImageChangeEvent>() {
                    @Override
                    public void call(AreaImageChangeEvent event) {
                        if (event == null) return;
                        groupAdapter.notifyDataSetChanged();

                    }
                });


        mCompositeSubscription.add(mSubscription03);
        mCompositeSubscription.add(mSubscription04);
    }

    @Override
    public void onItemClick(View view, int position) {

        Intent intent1 = new Intent(getActivity(), ItemAreaActivity.class);

        ArrayList<String> groupNames = new ArrayList<>();
        for (int j = 0; j < mDatas.size(); j++) {
            if (position != j) {
                groupNames.add(mDatas.get(j).getGroupName());
            }
        }
        intent1.putStringArrayListExtra(GROUPINFOS, groupNames);


        intent1.putExtra(GROUPINFO, mDatas.get(position));
        startActivity(intent1);

    }

    /**
     * 上拉加载更多
     */
    @Override
    public void onLoadMore() {

    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        if (mDatas != null)
        mDatas.clear();
        serial.getGroups();
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefreshComplete();
            }
        }, 3000);
    }

    private void onRefreshComplete() {
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ((MainActivity) getActivity()).openLeftMenu();
                break;
            case R.id.iv_right_menu:
                ArrayList<String> groupNames = new ArrayList<>();
                for (int i = 0; i < mDatas.size(); i++) {
                    groupNames.add(mDatas.get(i).getGroupName());
                }
                Intent intent = new Intent(getActivity(), AddAreaActicity.class);
                intent.putStringArrayListExtra(GROUPINFOS, groupNames);
                startActivity(intent);
                break;

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
       /* if (subscription1 != null && !subscription1.isUnsubscribed()) {
            subscription1.unsubscribe();
        }*/
    }

}
