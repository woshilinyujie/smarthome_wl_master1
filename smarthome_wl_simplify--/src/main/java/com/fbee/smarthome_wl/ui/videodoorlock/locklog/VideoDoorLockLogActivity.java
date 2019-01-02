package com.fbee.smarthome_wl.ui.videodoorlock.locklog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.QueryVideoLockRecordReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.VideolockRecordResponse;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogDeteleRecord;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class VideoDoorLockLogActivity extends BaseActivity<VideoDoorLockLogContract.Presenter> implements VideoDoorLockLogContract.view, OnRefreshListener, OnLoadMoreListener {

    private ListView lvDoorLock;
    private TextView tvRightMenu;
    private ImageView back;
    private int timeChoseTag;
    private DialogDeteleRecord dialog;
    private String deviceUuid;
    private SwipeToLoadLayout swipeToLoadLayout;
    private ArrayList<VideolockRecordResponse.BodyBean> beanList;
    private MyAdapter doorLockAlarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_door_lock_log);
    }

    @Override
    protected void initView() {
        initApi();
        createPresenter(new VideoDoorLockLogPresenter(this));
        lvDoorLock = (ListView) findViewById(R.id.swipe_target);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("开锁记录");
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        back = (ImageView) findViewById(R.id.back);

    }

    @Override
    protected void initData() {
        beanList = new ArrayList<>();
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("编辑");
        deviceUuid = getIntent().getExtras().getString("deviceUuid");
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        QueryVideoLockRecordReq queryVideoLockRecordReq = new QueryVideoLockRecordReq();
        queryVideoLockRecordReq.setVendor_name(FactoryType.GENERAL);
        queryVideoLockRecordReq.setRecord_number("20");
        queryVideoLockRecordReq.setUuid(deviceUuid);
        presenter.getVideoDoorLocklog(queryVideoLockRecordReq);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_menu:
                dialog = new DialogDeteleRecord(this, new DialogDeteleRecord.OnItemClickListener() {
                    @Override
                    public void onItemClickback(long endTime, int timeChoseTag) {
                        VideoDoorLockLogActivity.this.timeChoseTag = timeChoseTag;
                        deteleRecord(endTime);
                    }
                });
                dialog.show();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void deteleRecord(final long time) {
        QueryVideoLockRecordReq queryVideoLockRecordReq = new QueryVideoLockRecordReq();
        queryVideoLockRecordReq.setStart_timestamp("100");
        queryVideoLockRecordReq.setUuid(deviceUuid);
        queryVideoLockRecordReq.setEnd_timestamp(String.valueOf(time));
        queryVideoLockRecordReq.setVendor_name(FactoryType.GENERAL);
        presenter.deleteDoorLocklog(queryVideoLockRecordReq);
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void responseDoorLog(VideolockRecordResponse res) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        List<VideolockRecordResponse.BodyBean> body = res.getBody();
        if (res.getHeader().getHttp_code().equals("200")) {
            if (swipeToLoadLayout != null && swipeToLoadLayout.isRefreshing()) {
                swipeToLoadLayout.setRefreshing(false);
            }
            if (swipeToLoadLayout != null && swipeToLoadLayout.isLoadingMore()) {
                swipeToLoadLayout.setLoadingMore(false);

                if (body == null)
                    return;
            }
            if (body != null && body.size() > 0) {
                for (VideolockRecordResponse.BodyBean bean : body) {
                    beanList.add(bean);
                }
                if (doorLockAlarmAdapter != null) {
                    doorLockAlarmAdapter.notifyDataSetChanged();
                } else {
                    doorLockAlarmAdapter = new MyAdapter();
                    lvDoorLock.setAdapter(doorLockAlarmAdapter);
                }
            } else {
                showToast("暂无更多数据");
            }

        }else{
            ToastUtils.showShort(RequestCode.getRequestCode(res.getHeader().getReturn_string()));
        }
    }

    @Override
    public void responseDeleteDoor(BaseResponse info) {
        if (info.getHeader().getHttp_code().equals("200")) {
            beanList.clear();
            QueryVideoLockRecordReq queryVideoLockRecordReq = new QueryVideoLockRecordReq();
            queryVideoLockRecordReq.setVendor_name(FactoryType.GENERAL);
            queryVideoLockRecordReq.setRecord_number("20");
            queryVideoLockRecordReq.setUuid(deviceUuid);
            presenter.getVideoDoorLocklog(queryVideoLockRecordReq);
        }
    }

    @Override
    public void onRefresh() {
        beanList.clear();
        QueryVideoLockRecordReq queryVideoLockRecordReq = new QueryVideoLockRecordReq();
        queryVideoLockRecordReq.setVendor_name(FactoryType.GENERAL);
        queryVideoLockRecordReq.setRecord_number("20");
        queryVideoLockRecordReq.setUuid(deviceUuid);
        presenter.getVideoDoorLocklog(queryVideoLockRecordReq);
    }


    @Override
    public void onLoadMore() {
        QueryVideoLockRecordReq queryVideoLockRecordReq = new QueryVideoLockRecordReq();
        queryVideoLockRecordReq.setVendor_name(FactoryType.GENERAL);
        queryVideoLockRecordReq.setRecord_number("20");
        queryVideoLockRecordReq.setUuid(deviceUuid);
        queryVideoLockRecordReq.setEnd_timestamp(beanList.get(beanList.size()-1).getTimestamp());
        presenter.getVideoDoorLocklog(queryVideoLockRecordReq);
    }

    class MyAdapter extends BaseAdapter {

        private VideolockRecordResponse.BodyBean bodyBean;
        private StringBuffer stringBuffer;

        @Override
        public int getCount() {
            return beanList.size();
        }

        @Override
        public Object getItem(int position) {
            return beanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(VideoDoorLockLogActivity.this).inflate(R.layout.item_eques_visitor_list, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.summary = (TextView) convertView.findViewById(R.id.summary);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //用户ID
            stringBuffer = new StringBuffer();
            bodyBean = beanList.get(position);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String hms = formatter.format(Long.parseLong(bodyBean.getTimestamp()) * 1000);
            holder.summary.setText(hms);
            holder.iv.setImageResource(R.mipmap.binding_lock);
            String device_user_id = bodyBean.getDevice_user_id();
            stringBuffer.append(device_user_id + "号用户");
            //550号用户  胁迫密码进入菜单取消常开
            //胁迫状态
            String stress_status = bodyBean.getStress_status();
            StringBuffer stringBuffer1 = SelectCharacter(stress_status);

            //开锁方式
            String unlock_mode = bodyBean.getUnlock_mode();
            stringBuffer1 = SelectCharacter(unlock_mode);
            //操作类型
            String op_type = bodyBean.getOp_type();
            stringBuffer1 = SelectCharacter(op_type);
            //验证方式
            String auth_mode = bodyBean.getAuth_mode();
            stringBuffer1 = SelectCharacter(auth_mode);
            holder.title.setText(stringBuffer1);
            return convertView;
        }

        private StringBuffer SelectCharacter(String content) {
            switch (content) {
                case "pwd":
                    stringBuffer.append("密码");
                    break;
                case "fp":
                    stringBuffer.append("指纹");
                    break;
                case "card":
                    stringBuffer.append("刷卡");
                    break;
                case "face":
                    stringBuffer.append("人脸");
                    break;
                case "rf":
                    stringBuffer.append("感应");
                    break;
                case "eye":
                    stringBuffer.append("虹膜");
                    break;
                case "vena":
                    stringBuffer.append("指静脉");
                    break;
                case "remote":
                    stringBuffer.append("远程");
                    break;
                case "sgl":
                    stringBuffer.append("(单人)");
                    break;
                case "dbl":
                    stringBuffer.append("(双人)");
                    break;
                case "mutil":
                    stringBuffer.append("(多人)");
                    break;
                case "true":
                    stringBuffer.append("胁迫");
                    break;
                case "false":
                    break;
                case "enter_menu":
                    stringBuffer.append("进入菜单");
                    break;
                case "off_lock":
                    stringBuffer.append("取消常开");
                    break;
                case "on_lock":
                    stringBuffer.append("启用常开");
                    break;
                case "default":
                    stringBuffer.append("恢复出厂设置");
                    break;
                case "on_infra":
                    stringBuffer.append("启用红外报警");
                    break;
                case "off_infra":
                    stringBuffer.append("取消红外报警");
                    break;
            }
            return stringBuffer;
        }

        public class ViewHolder {
            public ImageView iv;
            public TextView title;
            public TextView summary;
            public CheckBox checkBox;
        }
    }

}
