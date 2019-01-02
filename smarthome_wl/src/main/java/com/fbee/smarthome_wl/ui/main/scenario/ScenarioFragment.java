package com.fbee.smarthome_wl.ui.main.scenario;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.ScenarioAdapter;
import com.fbee.smarthome_wl.adapter.itemdecoration.SpaceItemDecoration;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.SceneDeleteEvent;
import com.fbee.smarthome_wl.event.SenceImageChangeEvent;
import com.fbee.smarthome_wl.event.SwitchDataEvent;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.ui.scenario.ScenarioEditActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.Serial;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.scenario
 * @anthor create by Zhaoli.Wang
 * @time 2017/2/13 10:31
 */
public class ScenarioFragment extends BaseFragment implements BaseRecylerAdapter.OnItemClickLitener, OnRefreshListener, OnLoadMoreListener,View.OnClickListener {
    private ImageView back;
    private TextView title;
    private TextView tvRightMenu;
    private RecyclerView recScenario;
    private LinearLayout rlNodata;
    private TextView tvNodata;
    private ImageView ivRightMenu;

    private SwipeToLoadLayout swipeToLoadLayout;
    private ScenarioAdapter  scenarioAdapter;
    private List<SenceInfo>  mDatas;
    private Serial serial;
    private Action1<Throwable> onErrorAction;
    private long currTime;
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
        mCompositeSubscription  = mContext.getCompositeSubscription();
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
        title.setText("场景");
        tvNodata.setText("空空如也..请添加场景哦..");
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        ivRightMenu.setOnClickListener(this);
        showLoadingDialog(null);
        serial= AppContext.getInstance().getSerialInstance();

        mDatas = new ArrayList<>();
        scenarioAdapter = new ScenarioAdapter(mContext,mDatas);
        //接收到Serial中
        getSence();
        //默认列表，参数4就是几列
        GridLayoutManager gm = new GridLayoutManager(mContext,3);
        recScenario.setLayoutManager(gm);
        recScenario.setItemAnimator(new DefaultItemAnimator());
        recScenario.addItemDecoration(new SpaceItemDecoration(AppUtil.dip2px(mContext,10)));
        recScenario.setAdapter(scenarioAdapter);

        scenarioAdapter.setOnItemClickLitener(this);
        swipeToLoadLayout.setOnRefreshListener(this);
//        swipeToLoadLayout.setRefreshEnabled(false);
        // swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeToLoadLayout.setLoadMoreEnabled(false); //设置上拉不可用
        //获取场景列表
        serial.getSences();

        onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };

        Subscription subscription1 = Observable.timer(4000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        hideLoadingDialog();
                        if (mDatas.size() == 0) {
                            serial.addDeviceToSence("回家",0,(short)0,(byte)0,(byte)0,(byte)0,(byte)0,0,(byte)0);
                            serial.addDeviceToSence("离家",0,(short)0,(byte)0,(byte)0,(byte)0,(byte)0,0,(byte)0);
                            serial.addDeviceToSence("睡眠",0,(short)0,(byte)0,(byte)0,(byte)0,(byte)0,0,(byte)0);
                        }
                    }
                },onErrorAction);
        mCompositeSubscription.add(subscription1);

        //切换网关，更新界面数据
        Subscription mSubscription02 = RxBus.getInstance().toObservable(SwitchDataEvent.class)
                .compose(TransformUtils.<SwitchDataEvent>defaultSchedulers())
                .subscribe(new Action1<SwitchDataEvent>() {
                    @Override
                    public void call(SwitchDataEvent event) {
                        mDatas.clear();
                        scenarioAdapter.notifyDataSetChanged();
                        rlNodata.setVisibility(View.VISIBLE);
                        //获取场景列表
//                        serial.getSences();
                    }
                });


        //删除场景后更新界面数据
        Subscription mSubscriptionDelete = RxBus.getInstance().toObservable(SceneDeleteEvent.class)
                .compose(TransformUtils.<SceneDeleteEvent>defaultSchedulers())
                .subscribe(new Action1<SceneDeleteEvent>() {
                    @Override
                    public void call(SceneDeleteEvent event) {
                        if(null != mDatas){
                            for (int i = 0; i <mDatas.size() ; i++) {
                                if(mDatas.get(i).getSenceName().equals(event.getName())){
                                    mDatas.remove(i);
                                }
                            }
                        }
                        if(mDatas !=null && mDatas.size() ==0){
                            rlNodata.setVisibility(View.VISIBLE);
                        }
                        scenarioAdapter.notifyDataSetChanged();
                    }
                });

        //场景图标改动
        Subscription mSubscriptionChange = RxBus.getInstance().toObservable(SenceImageChangeEvent.class)
                .compose(TransformUtils.<SenceImageChangeEvent>defaultSchedulers())
                .subscribe(new Action1<SenceImageChangeEvent>() {
                    @Override
                    public void call(SenceImageChangeEvent event) {
                        if(null != mDatas) {
                            scenarioAdapter.notifyDataSetChanged();
                        }
                    }
                });



        mCompositeSubscription.add(mSubscription02);
        mCompositeSubscription.add(mSubscriptionDelete);
        mCompositeSubscription.add(mSubscriptionChange);
    }


    private void getSence(){
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                getSence();
            }
        };
        Subscription subscription= RxBus.getInstance().toObservable(SenceInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SenceInfo>() {
            @Override
            public void call(SenceInfo senceInfo) {
                onRefreshComplete();
                if (senceInfo==null)return;
                for (int i = 0; i <mDatas.size() ; i++) {
                    if (mDatas.get(i)==null)continue;
                    if(mDatas.get(i).getSenceId()==senceInfo.getSenceId()){
                        if (mDatas.get(i).getSenceName().equals(senceInfo.getSenceName())){
                            return;
                        }else{
                            mDatas.remove(i);
                            mDatas.add(0,senceInfo);
                            scenarioAdapter.notifyDataSetChanged();
                            hideLoadingDialog();
                            return;
                        }
                    }

                }
                rlNodata.setVisibility(View.GONE);
                mDatas.add(0,senceInfo);
                scenarioAdapter.notifyDataSetChanged();
                hideLoadingDialog();
            }
        },onErrorAction);
        mCompositeSubscription.add(subscription);
    }


    /**
     * item点击
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, final int position) {
            showLoadingDialog("正在执行场景");

            currTime=System.currentTimeMillis();

            Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    mContext.showLoadingDialog("正在执行场景");
                    Serial mSerial = AppContext.getInstance().getSerialInstance();
                    int ret= mSerial.setSence(mDatas.get(position).getSenceId());
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
                            mContext.hideLoadingDialog();
                        }

                        @Override
                        public void onNext(Integer ret) {
                            long nowTime=System.currentTimeMillis();
                            if((nowTime-currTime)>2000){
                                mContext.hideLoadingDialog();
                                if(ret >=0){
                                    showToast("场景执行成功");
                                }else{
                                    showToast("场景执行失败");
                                }
                            }else{
                                long time=2000-(nowTime-currTime);
                                delayDoing(time,ret);
                            }

                        }
                    });

        mCompositeSubscription.add(sub);
    }
    private void delayDoing(long time,final int ret){
        Subscription subscription1 = Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mContext.hideLoadingDialog();
                        if(ret >=0){
                            showToast("场景执行成功");
                        }else{
                            showToast("场景执行失败");
                        }
                    }
                },onErrorAction);
        mCompositeSubscription.add(subscription1);
    }

    /**
     * 上拉
     */
    @Override
    public void onLoadMore() {

    }

    /**
     * 刷新
     */
    public void onRefresh() {
        if(mDatas!= null )
        mDatas.clear();
        serial.getSences();
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefreshComplete();
            }
        },3000);

    }


    private void  onRefreshComplete(){
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                ((MainActivity) getActivity()).openLeftMenu();
                break;
            case R.id.iv_right_menu:
                skipAct(ScenarioEditActivity.class);
                break;


        }
    }

}
