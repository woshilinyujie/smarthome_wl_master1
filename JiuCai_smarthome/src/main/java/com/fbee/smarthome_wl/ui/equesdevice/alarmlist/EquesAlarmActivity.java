package com.fbee.smarthome_wl.ui.equesdevice.alarmlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.EquesArlarmAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.EquesAlarmInfo;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.SeleteEquesDeviceInfo;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.event.EquesAlarmDialogEvent;
import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.alarm.alarmbitmap.EquesBitmapActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class EquesAlarmActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {

    private String Bid;
    private String name;
    private SwipeToLoadLayout swipeToLoadLayout;
    private EquesArlarmAdapter adapter;
    private ListView lv;
    private TextView top_tv_name;
    private ProgressBar progressBar;
    private TextView top_bt_verfiy;
    private LinearLayout deleteChose;
    private LinearLayout botoomLayout;
    private ImageView choseImage;
    private TextView allChose;
    private TextView choseNumber;
    private List<SeleteEquesDeviceInfo> Seletes = new ArrayList<SeleteEquesDeviceInfo>();
    private ArrayList<EquesAlarmInfo.AlarmsEntity> alarmsEntitys;
    private ImageView ivBack;
    private int alarmSize;
    private JSONObject jpushData;
    private LinearLayout choseAll;
    private TextView noDevice;
    private List<SeleteEquesDeviceInfo> contactSelectedList = new ArrayList<SeleteEquesDeviceInfo>();    //记录被选中过的item
    private List<EquesAlarmInfo.AlarmsEntity> alarms;
    private List<URL> urls;
    private int size;
    private List<EquesAlarmInfo.AlarmsEntity> alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_alarm);
    }

    @Override
    protected void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        lv = (ListView) this.findViewById(R.id.swipe_target);
        top_tv_name = (TextView) findViewById(R.id.title);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        top_bt_verfiy = (TextView) findViewById(R.id.tv_right_menu);
        ivBack = (ImageView) findViewById(R.id.back);
        deleteChose = (LinearLayout) findViewById(R.id.delete);
        noDevice = (TextView) findViewById(R.id.tv_no_device);
        botoomLayout = (LinearLayout) findViewById(R.id.botoomLayout);
        choseAll = (LinearLayout) findViewById(R.id.choseall);
        allChose = (TextView) findViewById(R.id.allChose);
        choseImage = (ImageView) findViewById(R.id.choseImage);
        choseNumber = (TextView) findViewById(R.id.choseNumber);
        ivBack.setVisibility(View.VISIBLE);
        top_bt_verfiy.setText("编辑");
        top_tv_name.setText("报警消息");
        top_bt_verfiy.setVisibility(View.VISIBLE);
        top_bt_verfiy.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        choseAll.setOnClickListener(this);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        deleteChose.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (urls != null) {
            urls.clear();
        }
        if (alarms != null) {
            alarms.clear();
        }
        if (Seletes != null) {
            Seletes.clear();
        }
    }

    @Override
    protected void initData() {
        alarmsEntitys = new ArrayList();
        urls = new ArrayList<URL>();
        alarms = new ArrayList<EquesAlarmInfo.AlarmsEntity>();
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString(Method.ATTR_BUDDY_NICK);
        if (name == null) {
            name = bundle.getString("name");
        }
        Bid = bundle.getString(Method.ATTR_BUDDY_BID);
        String json = getIntent().getExtras().getString("json");
        if (json != null) {
            try {
                jpushData = new JSONObject(json);
                String logName = jpushData.optString("name");
                icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, logName, EquesConfig.APPKEY);
                if (Bid == null) {
                    Bid = jpushData.optString("bid");
                }
                List<EquesListInfo.bdylistEntity> bdylist = (List<EquesListInfo.bdylistEntity>) PreferencesUtils.getObject(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME) + "JpushArlam");
                if (name == null) {
                    for (int i = 0; i < bdylist.size(); i++) {
                        EquesListInfo.bdylistEntity bdylistEntity = bdylist.get(i);
                        if (bdylistEntity.getBid().equals(Bid)) {
                            if (bdylistEntity.getNick() != null) {
                                name = bdylistEntity.getNick();
                            } else {
                                name = bdylistEntity.getName();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mSubscription = RxBus.getInstance().toObservable(EquesAlarmDialogEvent.class)
                .compose(TransformUtils.<EquesAlarmDialogEvent>defaultSchedulers())
                .subscribe(new Action1<EquesAlarmDialogEvent>() {
                    @Override
                    public void call(EquesAlarmDialogEvent event) {
                        onRefresh();
                    }
                });
        mSubscription = RxBus.getInstance().toObservable(EquesAlarmInfo.class)
                .compose(TransformUtils.<EquesAlarmInfo>defaultSchedulers())
                .subscribe(new Action1<EquesAlarmInfo>() {
                    boolean FirstFlag = true;

                    @Override
                    public void call(EquesAlarmInfo equesAlarmInfo) {
                        if (equesAlarmInfo.getAlarms() == null || equesAlarmInfo.getAlarms().size() == 0) {
                            onRefreshComplete();
                            if (FirstFlag) {
                                noDevice.setVisibility(View.VISIBLE);
                                noDevice.setText("当前无警报消息");
                            }
                            return;
                        }
                        noDevice.setVisibility(View.GONE);
                        alarm = equesAlarmInfo.getAlarms();
                        FirstFlag = false;
                        alarms.addAll(alarm);
                        EquesAlarmActivity.this.size = equesAlarmInfo.getAlarms().size();
                        for (int i = 0; i < EquesAlarmActivity.this.size; i++) {
                            EquesAlarmInfo.AlarmsEntity alarmsEntity = equesAlarmInfo.getAlarms().get(i);
                            String pvid = alarmsEntity.getPvid().get(0);
                            String aid = alarmsEntity.getAid();
                            String bid = alarmsEntity.getBid();
                            SeleteEquesDeviceInfo seleteEquesDeviceInfo = new SeleteEquesDeviceInfo();
                            seleteEquesDeviceInfo.aid = aid;
                            seleteEquesDeviceInfo.bid = bid;
                            seleteEquesDeviceInfo.pvid = pvid;
                            URL url = icvss.equesGetThumbUrl(pvid, bid);
                            urls.add(url);
                            Seletes.add(seleteEquesDeviceInfo);
                        }
                        if (adapter != null) {
                            adapter.setB(false);
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter = new EquesArlarmAdapter(EquesAlarmActivity.this, name, alarms, Seletes, false, urls);
                            lv.setAdapter(adapter);
                        }
                        progressBar.setVisibility(View.GONE);
                        onRefreshComplete();

                    }
                });
        icvss.equesGetAlarmMessageList(Bid, 0, 0, 10);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (botoomLayout.getVisibility() == View.VISIBLE) {
                    boolean isSelect = adapter.getisSelectedAt(position);
                    if (!isSelect) {
                        //当前为被选中，记录下来，用于删除
//                        contactSelectedList.clear();
                        contactSelectedList.add(Seletes.get(position));
                        EquesAlarmInfo.AlarmsEntity alarmsEntity = alarms.get(position);
                        alarmsEntitys.add(alarmsEntity);
                        choseNumber.setText("(" + contactSelectedList.size() + ")");
                        if (contactSelectedList.size() == Seletes.size() && contactSelectedList.size() != 0) {
                            allChose.setText("取消全选");
                            choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.selected));
                        } else {
                            allChose.setText("全选");
                            choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                        }
                    } else {
                        contactSelectedList.remove(Seletes.get(position));
                        alarmsEntitys.remove(alarms.get(position));
                        allChose.setText("全选");
                        choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                        choseNumber.setText("(" + contactSelectedList.size() + ")");
                    }
                    //选中状态的切换
                    adapter.setItemisSelectedMap(position, !isSelect);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("FID", alarms.get(position).getFid().get(0));
                    bundle.putString("BID", alarms.get(position).getBid());
                    bundle.putInt("TYPE", alarms.get(position).getType());
                    skipAct(EquesBitmapActivity.class, bundle);
                }
            }

        });
        mSubscription = RxBus.getInstance().toObservable(EquesListInfo.class)
                .compose(TransformUtils.<EquesListInfo>defaultSchedulers())
                .subscribe(new Action1<EquesListInfo>() {
                    @Override
                    public void call(EquesListInfo event) {
                        icvss.equesGetAlarmMessageList(Bid, 0, 0, 10);
                    }
                });
    }

    boolean bool = false;
    int falg = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_menu:
                bool = !bool;
                adapter = new EquesArlarmAdapter(EquesAlarmActivity.this, name, alarms, Seletes, bool, urls);
                if (top_bt_verfiy.getText().equals("编辑")) {
                    top_bt_verfiy.setText("取消");
                    botoomLayout.setVisibility(View.VISIBLE);
                    contactSelectedList.clear();
                    choseNumber.setText("(0)");
                } else {
                    top_bt_verfiy.setText("编辑");
                    allChose.setText("全选");
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                    botoomLayout.setVisibility(View.GONE);
                }
                lv.setAdapter(adapter);
                break;
            case R.id.choseall:
                if (allChose.getText().equals("取消全选")) {
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                    allChose.setText("全选");
                    for (int i = 0; i < Seletes.size(); i++) {
                        adapter.setItemisSelectedMap(i, false);
                        contactSelectedList.removeAll(Seletes);
                    }
                    choseNumber.setText("(0)");
                } else {
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.selected));
                    contactSelectedList.clear();
                    allChose.setText("取消全选");
                    for (int i = 0; i < Seletes.size(); i++) {
                        adapter.setItemisSelectedMap(i, true);
                        contactSelectedList.add(Seletes.get(i));
                        alarmsEntitys.add(alarms.get(i));
                    }
                    choseNumber.setText("(" + Seletes.size() + ")");
                    falg = 2;
                }
                break;
            case R.id.delete:
                for (SeleteEquesDeviceInfo c : contactSelectedList) {
                    String[] aids = {c.aid};
                    icvss.equesDelAlarmMessage(c.bid, aids, falg);
                    alarms.removeAll(alarmsEntitys);
                }
                contactSelectedList.clear();
                adapter = new EquesArlarmAdapter(this, name, alarms, Seletes, false, urls);
                lv.setAdapter(adapter);
                top_bt_verfiy.setText("编辑");
                allChose.setText("全选");
                choseNumber.setText("(0)");
                botoomLayout.setVisibility(View.GONE);
                bool = false;
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onLoadMore() {
        onnet();
        if (alarms.size() > 0) {
            alarmSize = alarms.size();
            long time = alarms.get(alarmSize - 1).getCreate()-1000;
            icvss.equesGetAlarmMessageList(Bid, 0, time, 10);
        } else {
            onRefreshComplete();
        }
    }

    @Override
    public void onRefresh() {
        bool = false;
        alarms.clear();
        Seletes.clear();
        if (urls != null) {
            urls.clear();
        }
        botoomLayout.setVisibility(View.GONE);
        onnet();
        icvss.equesGetAlarmMessageList(Bid, 0, 0, 10);
    }

    private void onRefreshComplete() {
        botoomLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        bool = false;
        onnet();
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }

    public void onnet() {
        alarmsEntitys.clear();
        allChose.setText("全选");
        top_bt_verfiy.setText("编辑");
        choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
        if (!AppUtil.isNetworkAvailable(EquesAlarmActivity.this)) {
            progressBar.setVisibility(View.GONE);
            showToast("当前无网络，请连接网络！");
            onRefreshComplete();
            return;
        }
    }
}

