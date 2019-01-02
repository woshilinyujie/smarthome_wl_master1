package com.fbee.smarthome_wl.ui.videodoorlock.alarmlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.doorlocklog.DoorLockAlarmAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.QueryDeleteDoorlockAlarm;
import com.fbee.smarthome_wl.request.QueryDoorlockAlarm;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.DoorLockAlarmResponse;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;
import com.fbee.smarthome_wl.ui.doorlock.DoorLockContract;
import com.fbee.smarthome_wl.ui.doorlock.DoorlockPresenter;
import com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.sdpicture.SDpictureActivity;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wl on 2017/9/11.
 */

public class DoorlockAlarmActivity extends BaseActivity<DoorLockContract.Presenter> implements DoorLockContract.View, OnRefreshListener, OnLoadMoreListener {

    private ListView lv_bitmap;
    private TextView tv_no_device;
    private ImageView choseImage;
    private TextView allChose;
    private LinearLayout choseall;
    private LinearLayout delete;
    private LinearLayout botoomLayout;
    private ArrayList<DoorLockAlarmResponse.BodyEntity> bodyEntities;
    private TextView tv_right_menu;
    private DoorLockAlarmAdapter doorLockAlarmAdapter;
    private boolean bool = false;
    private ArrayList<DoorLockAlarmResponse.BodyEntity> selectlist;
    private ProgressBar progressbar;
    private String deviceUuid;
    private SwipeToLoadLayout swipeToLoadLayout;
    private Bundle extras;
    private String not_call;
    private TextView title;
    private String deviceName;
    private String timestamp;
    private int index;
    int type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_alarm);
    }

    @Override
    protected void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        progressbar = (ProgressBar) findViewById(R.id.progressBar1);
        lv_bitmap = (ListView) findViewById(R.id.swipe_target);
        tv_no_device = (TextView) findViewById(R.id.tv_no_device);
        tv_no_device.setOnClickListener(this);
        choseImage = (ImageView) findViewById(R.id.choseImage);
        allChose = (TextView) findViewById(R.id.allChose);
        choseall = (LinearLayout) findViewById(R.id.choseall);
        choseall.setOnClickListener(this);
        delete = (LinearLayout) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        botoomLayout = (LinearLayout) findViewById(R.id.botoomLayout);
        botoomLayout.setOnClickListener(this);
        tv_right_menu = (TextView) findViewById(R.id.tv_right_menu);
        tv_right_menu.setOnClickListener(this);
        ImageView back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        title.setText("报警消息");
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        not_call = "not_call";
        bodyEntities = new ArrayList<>();
        selectlist = new ArrayList<>();
        extras = getIntent().getExtras();
        deviceUuid = extras.getString("deviceUuid");
        deviceName = extras.getString("deviceName");
        if (extras.getString("type") != null) {
            not_call = "call";
            title.setText("访客记录");
            type = 2;
        }
        initApi();
        createPresenter(new DoorlockPresenter(this));
        tv_right_menu.setVisibility(View.VISIBLE);
        tv_right_menu.setText("编辑");
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        QueryDoorlockAlarm queryDoorlockAlarm = new QueryDoorlockAlarm();
        queryDoorlockAlarm.setVendor_name(FactoryType.GENERAL);
        queryDoorlockAlarm.setUuid(deviceUuid);
        queryDoorlockAlarm.setRecord_number("20");
        queryDoorlockAlarm.setAlarm_type(not_call);
        presenter.getDoorlockAlarm(queryDoorlockAlarm);


        lv_bitmap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (botoomLayout.getVisibility() == View.VISIBLE) {
                    boolean isSelect = doorLockAlarmAdapter.getisSelectedAt(position);
                    if (isSelect) {
                        doorLockAlarmAdapter.setItemisSelectedMap(position, !isSelect);
                        selectlist.remove(bodyEntities.get(position));
                        choseImage.setImageResource(R.mipmap.unselected);
                        allChose.setText("全选");
                        LogUtil.e("数据", "删除" + bodyEntities.get(position));
                    } else {
                        doorLockAlarmAdapter.setItemisSelectedMap(position, !isSelect);
                        selectlist.add(bodyEntities.get(position));
                        if (selectlist.size() == bodyEntities.size()) {
                            choseImage.setImageResource(R.mipmap.selected);
                            allChose.setText("取消全选");
                        }
                    }
                } else {
                    if (bodyEntities.get(position).getAlarm_message() != null) {
                        Intent intent = new Intent(DoorlockAlarmActivity.this, SDpictureActivity.class);
                        intent.putExtra("path", bodyEntities.get(position).getAlarm_message());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                if (doorLockAlarmAdapter == null) {
                    return;
                }
                bool = !bool;
                if (bool) {
                    doorLockAlarmAdapter.setIsCheckBoxVisiable(true);
                    botoomLayout.setVisibility(View.VISIBLE);
                    if (selectlist != null && selectlist.size() > 0) {
                        selectlist.clear();
                    }
                    for (int i = 0; i < bodyEntities.size(); i++) {
                        doorLockAlarmAdapter.setItemisSelectedMap(i, false);
                    }
                    choseImage.setImageResource(R.mipmap.unselected);
                    allChose.setText("全选");
                } else {
                    doorLockAlarmAdapter.setIsCheckBoxVisiable(false);
                    botoomLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.delete:
                ArrayList<String> strings = new ArrayList<>();
                if (selectlist == null || selectlist.size() == 0) {
                    return;
                }
                for (int i = 0; i < selectlist.size(); i++) {
                    strings.add(selectlist.get(i).get_id());
                }
                if (strings.size() == bodyEntities.size()) {
                    QueryDeleteDoorlockAlarm queryDeleteDoorlockAlarm = new QueryDeleteDoorlockAlarm();
                    queryDeleteDoorlockAlarm.setVendor_name(FactoryType.GENERAL);
                    queryDeleteDoorlockAlarm.setUuid(deviceUuid);
                    if (title.getText().equals("访客记录")) {
                        queryDeleteDoorlockAlarm.setAlarm_type("call");
                    } else {
                        queryDeleteDoorlockAlarm.setAlarm_type("not_call");
                    }
                    presenter.deleteDoorlockAlarm(queryDeleteDoorlockAlarm);
                } else {
                    QueryDeleteDoorlockAlarm queryDeleteDoorlockAlarm = new QueryDeleteDoorlockAlarm();
                    queryDeleteDoorlockAlarm.setVendor_name(FactoryType.GENERAL);
                    queryDeleteDoorlockAlarm.setUuid(deviceUuid);
                    queryDeleteDoorlockAlarm.set_id_list(strings);
                    presenter.deleteDoorlockAlarm(queryDeleteDoorlockAlarm);
                }
                break;
            case R.id.choseall:
                if (allChose.getText().equals("全选")) {
                    for (int i = 0; i < bodyEntities.size(); i++) {
                        doorLockAlarmAdapter.setItemisSelectedMap(i, true);
                    }
                    selectlist.addAll(bodyEntities);
                    choseImage.setImageResource(R.mipmap.selected);
                    allChose.setText("取消全选");
                } else {
                    for (int i = 0; i < bodyEntities.size(); i++) {
                        doorLockAlarmAdapter.setItemisSelectedMap(i, false);
                    }
                    selectlist.removeAll(bodyEntities);
                    choseImage.setImageResource(R.mipmap.unselected);
                    allChose.setText("全选");
                }
                break;
        }
    }

    /***
     * 门锁警报消息返回
     *
     * @param info
     */
    @Override
    public void responseDoorAlarm(DoorLockAlarmResponse info) {
        if (info.getHeader().getHttp_code().equals("200")) {
            progressbar.setVisibility(View.GONE);
            List<DoorLockAlarmResponse.BodyEntity> body = info.getBody();
            if (swipeToLoadLayout != null && swipeToLoadLayout.isRefreshing()) {
                swipeToLoadLayout.setRefreshing(false);
                botoomLayout.setVisibility(View.GONE);
                doorLockAlarmAdapter.setIsCheckBoxVisiable(false);
                if (selectlist != null && selectlist.size() > 0) {
                    selectlist.clear();
                }
                choseImage.setImageResource(R.mipmap.unselected);
                allChose.setText("全选");
                bool = false;
            }
            if (swipeToLoadLayout != null && swipeToLoadLayout.isLoadingMore()) {
                swipeToLoadLayout.setLoadingMore(false);
                botoomLayout.setVisibility(View.GONE);
                choseImage.setImageResource(R.mipmap.unselected);
                doorLockAlarmAdapter.setIsCheckBoxVisiable(false);
                if (selectlist != null && selectlist.size() > 0) {
                    selectlist.clear();
                }
                allChose.setText("全选");
                bool = false;
                if (body != null && body.size() > 0) {
                    for (DoorLockAlarmResponse.BodyEntity bean : body) {
                        if (bean.getAlarm_type().equals("call"))
                            continue;
                        bodyEntities.add(bean);
                    }
                    if (doorLockAlarmAdapter != null) {
                        doorLockAlarmAdapter.addAllData(bodyEntities);
                        return;
                    }
                }
            }
            if (body != null && body.size() > 0) {
                if ("not_call".equals(not_call)) {
                    bodyEntities.clear();
                    for (DoorLockAlarmResponse.BodyEntity bean : body) {
                        if (bean.getAlarm_type().equals("call"))
                            continue;
                        bodyEntities.add(bean);
                    }
                } else {
                    bodyEntities.clear();
                    for (DoorLockAlarmResponse.BodyEntity bean : body) {
                        if (bean.getAlarm_type().equals("call"))
                            bodyEntities.add(bean);
                    }
                }
                if (doorLockAlarmAdapter != null) {
                    doorLockAlarmAdapter.notifyDataSetChanged();
                } else {
                    doorLockAlarmAdapter = new DoorLockAlarmAdapter(this, bodyEntities, deviceName, type);
                    lv_bitmap.setAdapter(doorLockAlarmAdapter);
                }
            } else {
                showToast("暂无更多数据");
            }
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(info.getHeader().getReturn_string()));
        }
    }

    /***
     * 删除门锁报警列表返回
     *
     * @param info
     */
    @Override
    public void responseDeleteDoorAlarm(BaseResponse info) {
        if (info.getHeader().getHttp_code().equals("200")) {
            bool = false;
            botoomLayout.setVisibility(View.GONE);
            bodyEntities.removeAll(selectlist);
            doorLockAlarmAdapter.addAllData(bodyEntities);
            selectlist.clear();
        }
    }

    @Override
    public void responseDeviceModel(DoorlockpowerInfo info) {

    }


    @Override
    public void responseDoorInfo(DoorlockpowerInfo info) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void onRefresh() {

        QueryDoorlockAlarm queryDoorlockAlarm = new QueryDoorlockAlarm();
        queryDoorlockAlarm.setVendor_name(FactoryType.GENERAL);
        queryDoorlockAlarm.setUuid(deviceUuid);
        queryDoorlockAlarm.setRecord_number("20");
        queryDoorlockAlarm.setAlarm_type(not_call);
        presenter.getDoorlockAlarm(queryDoorlockAlarm);
    }

//    int integer = 20;

    @Override
    public void onLoadMore() {

        QueryDoorlockAlarm queryDoorlockAlarm = new QueryDoorlockAlarm();
        queryDoorlockAlarm.setVendor_name(FactoryType.GENERAL);
        queryDoorlockAlarm.setUuid(deviceUuid);
        queryDoorlockAlarm.setRecord_number("20");
        if (bodyEntities == null || bodyEntities.size() == 0) {
            showToast("暂无更多数据");
            return;
        }
        index = bodyEntities.size() - 1;
        timestamp = bodyEntities.get(index).getTimestamp();
        queryDoorlockAlarm.setEnd_timestamp(timestamp);
        queryDoorlockAlarm.setAlarm_type(not_call);
        presenter.getDoorlockAlarm(queryDoorlockAlarm);
    }
}
