package com.fbee.smarthome_wl.ui.mainsimplify.find;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.simplyadapter.FindAdapter;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.response.DiscoverDataResponse;
import com.fbee.smarthome_wl.ui.webview.MyWebViewActivity;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends BaseFragment implements BaseRecylerAdapter.OnItemClickLitener, OnRefreshListener, OnLoadMoreListener {
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView swipeTarget;
    private LinearLayout rlNodata;
    private ImageView ivNodata;
    private TextView tvNodata;

    private List<DiscoverDataResponse.BodyBean.DiscoverListBean> datas;
    private FindAdapter findAdapter;

    public FindFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int onSetLayoutId() {
        //return R.layout.fragment_find;
        return R.layout.fragment_scenario;
    }

    @Override
    public void initView() {
        headerRl = (RelativeLayout) mContentView.findViewById(R.id.header_rl);
        back = (ImageView) mContentView.findViewById(R.id.back);
        title = (TextView) mContentView.findViewById(R.id.title);
        title.setText("发现");
        ivRightMenu = (ImageView) mContentView.findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) mContentView.findViewById(R.id.tv_right_menu);
        swipeToLoadLayout = (SwipeToLoadLayout) mContentView.findViewById(R.id.swipeToLoadLayout);
        swipeTarget = (RecyclerView) mContentView.findViewById(R.id.swipe_target);
        rlNodata = (LinearLayout) mContentView.findViewById(R.id.rl_nodata);
        ivNodata = (ImageView) mContentView.findViewById(R.id.iv_nodata);
        tvNodata = (TextView) mContentView.findViewById(R.id.tv_nodata);
    }

    @Override
    public void bindEvent() {
        initApi();
        requestFindInfo();
        datas=new ArrayList();
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setRefreshEnabled(false);
        LinearLayoutManager gm = new LinearLayoutManager(getActivity());
        swipeTarget.setLayoutManager(gm);
        swipeTarget.setItemAnimator(new DefaultItemAnimator());
        findAdapter=new FindAdapter(getActivity(),datas);
        findAdapter.setOnItemClickLitener(this);
        swipeTarget.setAdapter(findAdapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        String linkUrl=datas.get(position).getLink_url();
        if(linkUrl!=null){
            Intent intent=new Intent(getActivity(), MyWebViewActivity.class);
            intent.putExtra("linkUrl",linkUrl);
            startActivity(intent);
        }
    }

    /**
     * 请求发现信息
     */
    private void requestFindInfo(){
        UMSBean.HeaderBean req=new UMSBean.HeaderBean();
        req.setApi_version("1.0");
        req.setMessage_type("MSG_DISCOVER_DATA_GET_REQ");
        req.setSeq_id("1");
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(JsonObject json) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        DiscoverDataResponse resCode = new Gson().fromJson(jsonObj.toString(), DiscoverDataResponse.class);
                        if(resCode!=null&&resCode.getHeader()!=null){
                            if("200".equals(resCode.getHeader().getHttp_code())){
                                if(resCode.getBody()!=null&&resCode.getBody().getDiscover_list()!=null){
                                    List<DiscoverDataResponse.BodyBean.DiscoverListBean> listBeen= resCode.getBody().getDiscover_list();
                                    datas.addAll(listBeen);
                                    if(datas!=null&&datas.size()>0){
                                        rlNodata.setVisibility(View.GONE);
                                    }
                                    findAdapter.notifyDataSetChanged();
                                }
                            }else {
                                ToastUtils.showShort(RequestCode.getRequestCode(resCode.getHeader().getReturn_string()));
                            }
                        }
                    }
                }
            }
        });

        Ums ums=new Ums();
        UMSBean umsbean=new UMSBean();
        umsbean.setHeader(req);
        ums.setUMS(umsbean);
        Subscription subscription=mApiWrapper.discoverInfoReq(ums).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefreshComplete();
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefreshComplete();
            }
        }, 2000);
    }
    private void onRefreshComplete() {
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }
}
