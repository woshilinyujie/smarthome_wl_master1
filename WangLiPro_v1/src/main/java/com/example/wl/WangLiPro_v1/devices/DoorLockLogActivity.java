package com.example.wl.WangLiPro_v1.devices;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.adapter.DeviceUserIdAdapter;
import com.example.wl.WangLiPro_v1.adapter.DoorLockLogAdapter;
import com.example.wl.WangLiPro_v1.adapter.DoorLockUserAdapter;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.jwl.android.jwlandroidlib.Exception.HttpException;
import com.jwl.android.jwlandroidlib.bean.DeviceUser;
import com.jwl.android.jwlandroidlib.bean.DeviceUsersBean;
import com.jwl.android.jwlandroidlib.bean.IncallLog;
import com.jwl.android.jwlandroidlib.bean.IncallLogsBean;
import com.jwl.android.jwlandroidlib.http.HttpManager;
import com.jwl.android.jwlandroidlib.httpInter.HttpDataCallBack;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.util.List;


public class DoorLockLogActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener {

    private ImageView mBack;
    private RecyclerView mSwipeTarget;
    private SwipeToLoadLayout mSwipeToLoadLayout;
    private String msg;
    private TextView title;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorlock_log);
    }

    @Override
    protected void initData() {
        deviceId = getIntent().getStringExtra("ID");
        if (msg.equals("LOG")) {
            title.setText("开门记录");
            netDoorLockWork();
        } else {
            title.setText("用户列表");
            netUserListWork();
        }
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setLoadingMore(false);
//        mSwipeToLoadLayout.setOnLoadMoreListener(this);
    }

    private void netDoorLockWork() {
        HttpManager.getInstance(this).incallLogs(AppContext.getUSERID(), AppContext.getTOKEN(), 0, 0, null, null, new HttpDataCallBack<IncallLogsBean>() {

            private List<IncallLog> incallLogs;

            @Override
            public void httpDateCallback(IncallLogsBean incallLogsBean) {
                if (mSwipeToLoadLayout.isRefreshing()) {
                    mSwipeToLoadLayout.setRefreshing(false);
                }
                incallLogs = incallLogsBean.getData().getIncallLogs();
                DoorLockLogAdapter doorLockLogAdapter = new DoorLockLogAdapter(DoorLockLogActivity.this, incallLogs);
                mSwipeTarget.setAdapter(doorLockLogAdapter);
            }

            @Override
            public void httpException(HttpException e) {

            }

            @Override
            public void complet() {

            }
        });
    }


    private void netUserListWork() {
        HttpManager.getInstance(this).getDeviceUserList(AppContext.getUSERID(), AppContext.getTOKEN(), deviceId, new HttpDataCallBack<DeviceUsersBean>() {
            @Override
            public void httpDateCallback(DeviceUsersBean b) {
                stopRefresh();
                List<DeviceUser> deviceUsers = b.getData().getDeviceUsers();
                DoorLockUserAdapter doorLockLogAdapter = new DoorLockUserAdapter(DoorLockLogActivity.this, deviceUsers);
                mSwipeTarget.setAdapter(doorLockLogAdapter);
            }

            @Override
            public void httpException(HttpException e) {

            }

            @Override
            public void complet() {

            }
        });
    }

    @Override
    protected void initView() {
        msg = getIntent().getStringExtra("MSG");
        title = (TextView) findViewById(R.id.title);
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mSwipeTarget = (RecyclerView) findViewById(R.id.swipe_target);
        mSwipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        mSwipeToLoadLayout.setOnClickListener(this);
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (msg.equals("LOG")) {
            netDoorLockWork();
        } else {
            netUserListWork();
        }
    }

    private void stopRefresh() {
        if (mSwipeToLoadLayout.isLoadingMore()) {
            mSwipeToLoadLayout.setLoadingMore(false);
        }
        if (mSwipeToLoadLayout.isRefreshing()) {
            mSwipeToLoadLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoadMore() {
        if (msg.equals("LOG")) {
            netDoorLockWork();
        } else {
            netUserListWork();
        }
    }
}
