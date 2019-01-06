package com.fbee.smarthome_wl.ui.equesdevice.visitorlist;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.VisitorAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.EquesVisitorInfo;
import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.alarm.alarmbitmap.EquesBitmapActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.net.URL;
import java.util.ArrayList;

import rx.functions.Action1;

public class EquesVisitorActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {

    private ListView lv;
    private String Bid;
    private String name;
    private ProgressBar progressBar;
    private ArrayList<EquesVisitorInfo.RingsEntity> visitors;
    private VisitorAdapter adapter;
    private ArrayList<URL> urls;
    private ArrayList<EquesVisitorInfo.RingsEntity> contactSelectedList;
    private LinearLayout deleteChose;
    private LinearLayout botoomLayout;
    private TextView allChose;
    private TextView choseNumber;
    private boolean bool = false;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private TextView tvRightMenu;
    private ImageView choseImage;
    private int visitorSize;
    private LinearLayout choseAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_alarm);
    }

    @Override
    protected void initView() {

        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        lv = (ListView) findViewById(R.id.swipe_target);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        deleteChose = (LinearLayout) findViewById(R.id.delete);
        choseImage = (ImageView) findViewById(R.id.choseImage);
        botoomLayout = (LinearLayout) findViewById(R.id.botoomLayout);
        allChose = (TextView) findViewById(R.id.allChose);
        choseAll = (LinearLayout) findViewById(R.id.choseall);
        choseNumber = (TextView) findViewById(R.id.choseNumber);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        choseAll.setOnClickListener(this);
        deleteChose.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        title.setText("访客记录");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setText("编辑");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        visitors = new ArrayList<>();
        urls = new ArrayList<URL>();
        contactSelectedList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString(Method.ATTR_BUDDY_NICK);
        Bid = bundle.getString(Method.ATTR_BUDDY_BID);
        if (AppUtil.isNetworkAvailable(this)) {
            icvss.equesGetRingRecordList(Bid, 0, 0, 20);
        } else {
            progressBar.setVisibility(View.GONE);
            showToast("网络已断开，请连接网络！");
        }
        mSubscription = RxBus.getInstance().toObservable(EquesVisitorInfo.class)
                .compose(TransformUtils.<EquesVisitorInfo>defaultSchedulers())
                .subscribe(new Action1<EquesVisitorInfo>() {
                    @Override
                    public void call(EquesVisitorInfo event) {
                        visitors.addAll(event.getRings());
                        if (adapter != null) {
                            adapter.setB(false);
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter = new VisitorAdapter(EquesVisitorActivity.this, visitors, name, false);
                            lv.setAdapter(adapter);
                        }
                        progressBar.setVisibility(View.GONE);
                        onRefreshComplete();
                    }
                });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isSelect = adapter.getisSelectedAt(position);
                if (botoomLayout.getVisibility() == View.VISIBLE) {
                    falg = 0;
                    if (!isSelect) {
                        //当前为被选中，记录下来，用于删除
                        contactSelectedList.add(visitors.get(position));
                        if (contactSelectedList.size() == visitors.size() && contactSelectedList.size() != 0) {
                            allChose.setText("取消全选");
                            choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.selected));
                        } else {
                            allChose.setText("全选");
                            choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                        }
                        choseNumber.setText("(" + contactSelectedList.size() + ")");
                    } else {
                        allChose.setText("全选");
                        contactSelectedList.remove(visitors.get(position));
                        choseNumber.setText("(" + contactSelectedList.size() + ")");
                        choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));

                    }
                    //选中状态的切换
                    adapter.setItemisSelectedMap(position, !isSelect);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("FID", visitors.get(position).getFid());
                    bundle.putString("BID", visitors.get(position).getBid());
                    skipAct(EquesBitmapActivity.class, bundle);
                }
            }
        });
    }

    int falg = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_menu:
                bool = !bool;
                falg = 0;
                adapter = new VisitorAdapter(EquesVisitorActivity.this, visitors, name, bool);
                if (tvRightMenu.getText().equals("编辑")) {
                    tvRightMenu.setText("取消");
                    botoomLayout.setVisibility(View.VISIBLE);
                    allChose.setText("全选");
                    contactSelectedList.clear();
                    choseNumber.setText("(0)");
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                } else {
                    tvRightMenu.setText("编辑");
                    allChose.setText("全选");
                    contactSelectedList.clear();
                    choseNumber.setText("(0)");
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                    botoomLayout.setVisibility(View.GONE);
                }
                lv.setAdapter(adapter);
                break;
            case R.id.choseall:
                if (allChose.getText().equals("取消全选")) {
                    allChose.setText("全选");
                    for (int i = 0; i < visitors.size(); i++) {
                        adapter.setItemisSelectedMap(i, false);
                        contactSelectedList.removeAll(visitors);
                    }
                    choseNumber.setText("(0)");
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                } else {
                    contactSelectedList.clear();
                    falg = 2;
                    allChose.setText("取消全选");
                    for (int i = 0; i < visitors.size(); i++) {
                        adapter.setItemisSelectedMap(i, true);
                        contactSelectedList.add(visitors.get(i));
                    }
                    choseNumber.setText("(" + visitors.size() + ")");
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.selected));
                }
                break;
            case R.id.delete:
                for (EquesVisitorInfo.RingsEntity c : contactSelectedList) {
                    String[] fids = {c.getFid()};
                    icvss.equesDelRingRecord(c.getBid(), fids, falg);
                    visitors.removeAll(contactSelectedList);
                }
                contactSelectedList.clear();
                adapter = new VisitorAdapter(EquesVisitorActivity.this, visitors, name, false);
                lv.setAdapter(adapter);
                tvRightMenu.setText("编辑");
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
        init();
        if (!AppUtil.isNetworkAvailable(this)) {
            showToast("当前网络异常！");
            onRefreshComplete();
        }
        if (visitors.size() > 0) {
            visitorSize = visitors.size();
            long time = visitors.get(visitorSize - 1).getRingtime();
            long startTime = time - 48 * 3600 * 1000;
            icvss.equesGetRingRecordList(Bid, 0, time - 1, 20);
        } else {
            onRefreshComplete();
        }
    }

    private void init() {
        contactSelectedList.clear();
        allChose.setText("全选");
        botoomLayout.setVisibility(View.GONE);
        choseNumber.setText("(0)");
        tvRightMenu.setText("编辑");
        choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
    }

    @Override
    public void onRefresh() {
        init();
        bool = false;
        visitors.clear();
        if (AppUtil.isNetworkAvailable(this)) {
            icvss.equesGetRingRecordList(Bid, 0, 0, 20);
        } else {
            progressBar.setVisibility(View.GONE);
            showToast("网络已断开，请连接网络！");
        }
        onRefreshComplete();
    }

    private void onRefreshComplete() {
        falg = 0;
        bool = false;
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }
}
