package com.example.wl.WangLiPro_v1.main.fragment;


import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.adapter.FindAdapter;
import com.example.wl.WangLiPro_v1.bean.DiscoverDataResponse;
import com.example.wl.WangLiPro_v1.bean.UMSBean;
import com.example.wl.WangLiPro_v1.bean.Ums;
import com.example.wl.WangLiPro_v1.main.MyWebViewActivity;
import com.example.wl.WangLiPro_v1.utils.RetrofitUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends Fragment implements FindAdapter.OnItemClickLitener, OnRefreshListener, OnLoadMoreListener {
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView swipeTarget;

    private List<DiscoverDataResponse.BodyBean.DiscoverListBean> datas;
    private FindAdapter findAdapter;
    private View mContentView;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_scenario, container, false);
        initView();
        bindEvent();
        return mContentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void initView() {
        headerRl = (RelativeLayout) mContentView.findViewById(R.id.header_rl);
        back = (ImageView) mContentView.findViewById(R.id.back);
        title = (TextView) mContentView.findViewById(R.id.title);
        title.setText("发现");
        ivRightMenu = (ImageView) mContentView.findViewById(R.id.iv_right_menu);
        swipeToLoadLayout = (SwipeToLoadLayout) mContentView.findViewById(R.id.swipeToLoadLayout);
        swipeTarget = (RecyclerView) mContentView.findViewById(R.id.swipe_target);
    }

    public void bindEvent() {
        requestFindInfo();
        back.setVisibility(View.GONE);
        datas = new ArrayList();
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setRefreshEnabled(false);
        LinearLayoutManager gm = new LinearLayoutManager(getActivity());
        swipeTarget.setLayoutManager(gm);
        swipeTarget.setItemAnimator(new DefaultItemAnimator());
        findAdapter = new FindAdapter(context, datas);
        findAdapter.setOnItemClickLitener(this);
        swipeTarget.setAdapter(findAdapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        String linkUrl = datas.get(position).getLink_url();
        if (linkUrl != null) {
            Intent intent = new Intent(getActivity(), MyWebViewActivity.class);
            intent.putExtra("linkUrl", linkUrl);
            startActivity(intent);
        }
    }

    /**
     * 请求发现信息
     */
    private void requestFindInfo() {
        UMSBean.HeaderBean req = new UMSBean.HeaderBean();
        req.setApi_version("1.0");
        req.setMessage_type("MSG_DISCOVER_DATA_GET_REQ");
        req.setSeq_id("1");
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(req);
        ums.setUMS(umsbean);
        Call<JsonObject> ums1 = new RetrofitUtils().getApi(getActivity()).Ums(ums);
        ums1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body1 = response.body();
                JsonObject jsonObj = body1.getAsJsonObject("UMS");
                DiscoverDataResponse discoverDataResponse = new Gson().fromJson(jsonObj.toString(), DiscoverDataResponse.class);
                if (discoverDataResponse.getHeader() != null && discoverDataResponse.getHeader().getHttp_code().equals("200")) {
                    List<DiscoverDataResponse.BodyBean.DiscoverListBean> discover_list = discoverDataResponse.getBody().getDiscover_list();
                    datas.addAll(discover_list);
                    findAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
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
