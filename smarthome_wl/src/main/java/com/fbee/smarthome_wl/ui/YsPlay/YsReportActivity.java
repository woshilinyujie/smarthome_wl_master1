package com.fbee.smarthome_wl.ui.YsPlay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.bean.YsDeleteMsgBean;
import com.fbee.smarthome_wl.bean.YsDeleteMsgBodyBean;
import com.fbee.smarthome_wl.bean.YsReportBean;
import com.fbee.smarthome_wl.bean.YsReportMsgBean;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.ui.devicemanager.DeviceManagerActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.DateUtils;
import com.fbee.smarthome_wl.utils.GsonUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by linyujie on 19/1/3.
 */

public class YsReportActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.header_rl)
    RelativeLayout headerRl;
    @BindView(R.id.ivArrow)
    ImageView ivArrow;

    @BindView(R.id.tvRefresh)
    TextView tvRefresh;
    @BindView(R.id.swipe_target)
    SwipeMenuListView swipeTarget;
    @BindView(R.id.ivSuccess)
    ImageView ivSuccess;
    @BindView(R.id.tvLoadMore)
    TextView tvLoadMore;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @BindView(R.id.lv_bitmap)
    ListView lvBitmap;
    @BindView(R.id.progressBar1)
    ProgressBar progressBar;
    @BindView(R.id.tv_no_device)
    TextView tvNoDevice;
    public static final AtomicInteger mSeqid = new AtomicInteger(1);
    private ArrayList<YsReportBean.UMSBean.BodyBean> datas;
    private boolean isRefresh = true;
    private int visitorSize;
    private DateUtils dataInstance;
    private YsReportAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ys_report_layout);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        initApi();
        title.setText("访客记录");
        back.setVisibility(View.VISIBLE);
        getYsMsg(0);
        datas = new ArrayList<>();
        dataInstance = DateUtils.getInstance();
        adapter = new YsReportAdapter();
        swipeTarget.setAdapter(adapter);

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
                deleteItem.setWidth(AppUtil.dp2px(YsReportActivity.this, 75));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };


        swipeTarget.setMenuCreator(creator);
        swipeTarget.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showDelateDialog(YsReportActivity.this, position);
                        break;
                }
                return false;
            }
        });
        swipeTarget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(YsReportActivity.this,YsVideoActivity.class);
                intent.putExtra("uuid",getIntent().getStringExtra("uuid"));
                intent.putExtra("time",datas.get(position).getTimestamp());
                intent.putExtra("random",getIntent().getStringExtra("random"));
                startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    //获取萤石记录
    public void getYsMsg(long time) {
        YsReportMsgBean bodyBean = new YsReportMsgBean();
        bodyBean.setVendor_name("ys7");
        bodyBean.setUuid(getIntent().getStringExtra("uuid"));
        bodyBean.setRecord_number("10");
        bodyBean.setAlarm_type("not_call");
        if (!isRefresh) {
            bodyBean.setEnd_timestamp(time + "");
        }
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JsonObject json) {
                progressBar.setVisibility(View.GONE);
                if (null != json) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    YsReportBean ysReportBean = GsonUtils.GsonToBean(json.toString(), YsReportBean.class);
                    if (ysReportBean.getUMS().getHeader().getReturn_string().equals("RETURN_SUCCESS_OK_STRING") &&
                            ysReportBean.getUMS().getHeader().getHttp_code().equals("200")) {
                        datas.addAll(ysReportBean.getUMS().getBody());
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });

        Ums ums = getUms("MSG_ALARM_DATA_QUERY_REQ", bodyBean);
        Subscription sub = mApiWrapper.queryYsInfo(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }

    protected <E> Ums getUms(String type, E bodyBean) {
        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type(type);
        header.setSeq_id(mSeqid.getAndIncrement() + "");
        Ums ums = new Ums();
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyBean);
        ums.setUMS(umsbean);
        return ums;
    }

    @Override
    public void onRefresh() {
        datas.clear();
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        isRefresh = true;
        getYsMsg(0);
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        visitorSize = datas.size();
        long time = Long.parseLong(datas.get(visitorSize - 1).getTimestamp());
        adapter.notifyDataSetChanged();
        getYsMsg(time);
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
    }


    public class YsReportAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            YsReportBean.UMSBean.BodyBean bodyBean = datas.get(position);
            YsReportHolder holder;
            if (convertView == null) {
                convertView = View.inflate(YsReportActivity.this, R.layout.ys_report_item, null);
                holder = new YsReportHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (YsReportHolder) convertView.getTag();
            }

            long l = Long.parseLong(bodyBean.getTimestamp()) * 1000;
            long l1 = System.currentTimeMillis();
            String s = dataInstance.dateFormat(l);
            if (s.equals(dataInstance.dateFormat(l1))) {
                holder.date.setText("今天");
            } else if (s.equals(dataInstance.dateFormat(l1 - 24 * 60 * 60 * 1000))) {
                holder.date.setText("昨天");
            } else {
                holder.date.setText(DateUtils.getInstance().getDayOrMonthOrYear4(l));
            }

            if (position == 0) {
                holder.ll.setVisibility(View.VISIBLE);
            } else {
                if (dataInstance.dateFormat(Long.parseLong(datas.get(position - 1).getTimestamp()) * 1000).equals(dataInstance.dateFormat(Long.parseLong(datas.get(position).getTimestamp()) * 1000))) {
                    holder.ll.setVisibility(View.GONE);
                } else {
                    holder.ll.setVisibility(View.VISIBLE);
                }
            }

            switch (bodyBean.getAlarm_type()) {
                case "noatmpt":
                    holder.msg.setText("非法操作");
                    break;
                case "not_noatmpt":
                    holder.msg.setText("除非法操作");
                    break;
                case "fakelock":
                    holder.msg.setText("假锁");
                    break;
                case "not_fakelock":
                    holder.msg.setText("除假锁");
                    break;
                case "nolock":
                    holder.msg.setText("门未关");
                    break;
                case "not_nolock":
                    holder.msg.setText("除门未关");
                    break;
                case "batt":
                    holder.msg.setText("低电量");
                    break;
                case "not_batt":
                    holder.msg.setText("除低电量");
                    break;
                case "infra":
                    holder.msg.setText("红外感应");
                    break;
                case "not_infra":
                    holder.msg.setText("除红外感应");
                    break;
                case "call":
                    holder.msg.setText("门铃呼叫");
                    break;
                case "not_call":
                    holder.msg.setText("除门铃呼叫");
                    break;
                case "relock":
                    holder.msg.setText("解除门未关");
                    break;
                case "not_relock":
                    holder.msg.setText("除解除门未关");
                    break;
                case "rm_fake":
                    holder.msg.setText("解除假锁");
                    break;
                case "not_rm_fake":
                    holder.msg.setText("除解除假锁");
                    break;
            }
            holder.time.setText(dataInstance.dateFormat6(Long.parseLong(datas.get(position).getTimestamp()) * 1000));
            Glide.with(YsReportActivity.this).load(bodyBean.getAlarm_message()).asBitmap().into(holder.iv);
            return convertView;
        }
    }


    public class YsReportHolder {
        public TextView date;
        public TextView msg;
        public TextView time;
        public LinearLayout ll;
        public ImageView iv;

        public YsReportHolder(View view) {
            date = (TextView) view.findViewById(R.id.ys_report_item_date);
            iv = (ImageView) view.findViewById(R.id.ys_report_item_iv);
            ll = (LinearLayout) view.findViewById(R.id.ys_report_item_ll);
            msg = (TextView) view.findViewById(R.id.ys_report_item_msg);
            time = (TextView) view.findViewById(R.id.ys_report_item_time);
        }
    }


    private void showDelateDialog(final Context context, final int position) {
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
                detele(position);
            }


        });
    }


    private void detele(final int position) {
        showLoadingDialog("正在删除设备");
        deleteMsg(position);
    }


    public void deleteMsg(final int position) {

        YsDeleteMsgBodyBean bodyBean = new YsDeleteMsgBodyBean();
        ArrayList<String> list = new ArrayList<>();
        list.add(datas.get(position).get_id());
        bodyBean.set_id_list(list);
        bodyBean.setVendor_name("ys7");
        bodyBean.setUuid(getIntent().getStringExtra("uuid"));
        bodyBean.setAlarm_type("not_call");
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<JsonObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
//pswpks
            @Override
            public void onNext(JsonObject json) {
                if (null != json) {
                    JsonObject jsonObj = json.getAsJsonObject("UMS");
                    if (null == jsonObj || jsonObj.size() == 0)
                        return;
                    YsDeleteMsgBean ysDeleteMsgBean = GsonUtils.GsonToBean(json.toString(), YsDeleteMsgBean.class);
                    if (ysDeleteMsgBean.getUMS().getHeader().getReturn_string().equals("RETURN_SUCCESS_OK_STRING") &&
                            ysDeleteMsgBean.getUMS().getHeader().getHttp_code().equals("200")) {
                        showToast("删除成功");
                        datas.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        showToast("删除失败");
                    }
                }
            }
        });

        Ums ums = getUms("MSG_ALARM_DATA_DEL_REQ", bodyBean);
        Subscription sub = mApiWrapper.queryYsInfo(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }
}
