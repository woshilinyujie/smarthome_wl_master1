package com.fbee.smarthome_wl.ui.videodoorlock.operationrecord;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.RecordReq;
import com.fbee.smarthome_wl.response.RecordResponse;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.swipetoloadlayout.OnLoadMoreListener;
import com.swipetoloadlayout.OnRefreshListener;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OperationRecordActivity extends BaseActivity<OperationRecordContract.Presenter> implements OperationRecordContract.view, OnRefreshListener, OnLoadMoreListener {

    private ImageView back;
    private TextView tvRightMenu;
    private TextView title;
    private SwipeToLoadLayout swipeToLoadLayout;
    private ListView lvDoorLock;
    private ArrayList<RecordResponse.BodyEntity> bodyArrayList;
    private MyAdapter doorLockAlarmAdapter;
    private String deviceUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_record);
    }

    @Override
    protected void initView() {
        initApi();
        createPresenter(new OperationRecordPresenter(this));
        lvDoorLock = (ListView) findViewById(R.id.swipe_target);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("操作记录");
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        tvRightMenu.setVisibility(View.GONE);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        bodyArrayList = new ArrayList<>();
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        deviceUuid = getIntent().getExtras().getString(Method.ATTR_BUDDY_UID);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        lvDoorLock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecordResponse.BodyEntity bodyEntity = bodyArrayList.get(position);
                Intent intent = new Intent(OperationRecordActivity.this, OperationRecordInfoActivity.class);
                intent.putExtra("bean", bodyEntity);
                startActivity(intent);
            }
        });
        RecordReq recordReq = new RecordReq();
        recordReq.setRecord_number("20");
        recordReq.setUuid(deviceUuid);
//        recordReq.setUuid("ccb0da102f1e");
        recordReq.setVendor_name(FactoryType.GENERAL);
        presenter.getOperationRecord(recordReq);
    }

    @Override
    public void onRefresh() {
        bodyArrayList.clear();
        RecordReq recordReq = new RecordReq();
        recordReq.setRecord_number("20");
        recordReq.setUuid(deviceUuid);
//        recordReq.setUuid("ccb0da102f1e");
        recordReq.setVendor_name(FactoryType.GENERAL);
        presenter.getOperationRecord(recordReq);
    }

    @Override
    public void onLoadMore() {
        RecordReq recordReq = new RecordReq();
        recordReq.setRecord_number("20");
        recordReq.setUuid(deviceUuid);
//        recordReq.setUuid("ccb0da102f1e");
        recordReq.setVendor_name(FactoryType.GENERAL);
        recordReq.setEnd_timestamp(bodyArrayList.get(bodyArrayList.size() - 1).getTimestamp());
        presenter.getOperationRecord(recordReq);
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
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void responseRecord(RecordResponse bean) {
        if (bean.getHeader().getHttp_code().equals("200")) {
            List<RecordResponse.BodyEntity> body = bean.getBody();
            if (swipeToLoadLayout != null && swipeToLoadLayout.isRefreshing()) {
                swipeToLoadLayout.setRefreshing(false);
            }
            if (swipeToLoadLayout != null && swipeToLoadLayout.isLoadingMore()) {
                swipeToLoadLayout.setLoadingMore(false);
                if (body == null)
                    return;

            }
            if (body != null && body.size() > 0) {
                for (RecordResponse.BodyEntity bodyEntity : body) {
                    bodyArrayList.add(bodyEntity);
                }
                if (doorLockAlarmAdapter != null) {
                    doorLockAlarmAdapter.notifyDataSetChanged();
                } else {
                    doorLockAlarmAdapter = new MyAdapter();
                    lvDoorLock.setAdapter(doorLockAlarmAdapter);
                }
            }
        } else {
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }


    class MyAdapter extends BaseAdapter {

        private RecordResponse.BodyEntity bodyEntity;
        private String stringmsg;
        private StringBuffer stringBuffer;
        private String substring;

        @Override
        public int getCount() {
            return bodyArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return bodyArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(OperationRecordActivity.this).inflate(R.layout.item_eques_visitor_list, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.summary = (TextView) convertView.findViewById(R.id.summary);
                holder.summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                holder.iv.setVisibility(View.INVISIBLE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//             新增/删除/修改     002/王卫东     指纹 密码 卡
//            2017-07-12 14：00：00
            //用户ID
            bodyEntity = bodyArrayList.get(position);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String hms = formatter.format(Long.parseLong(bodyEntity.getTimestamp()) * 1000);
            holder.summary.setText(hms);
            holder.iv.setImageResource(R.mipmap.visitor);
            String device_user_id = bodyEntity.getBe_operated().getObject();
            String object_note = bodyEntity.getBe_operated().getObject_note();
            if (object_note != null) {
                device_user_id = device_user_id + "/" + object_note;
            }
            stringmsg = new String();
            RecordResponse.BodyEntity.BeOperatedEntity be_operated = bodyEntity.getBe_operated();
//            新增/删除/修改
            String mode = be_operated.getMode();
            String msg = SelectCharacter(mode);
            String unlock_mode = be_operated.getUnlock_mode();
            stringBuffer = new StringBuffer();
            String selectmode = Selectmode(unlock_mode).toString();
            if(selectmode.length()>1){
                substring = selectmode.substring(1, selectmode.length());
            }
            msg = msg + device_user_id + "\u3000\u3000" + substring;
            holder.title.setText(msg);
            holder.summary.setText(hms);
            return convertView;
        }

        private StringBuffer Selectmode(String mode) {
            if (mode.contains("pwd")) {
                stringBuffer.append("/密码");
            }
            if (mode.contains("fp")) {
                stringBuffer.append("/指纹");
            }
            if (mode.contains("card")) {
                stringBuffer.append("/刷卡");
            }
            if (mode.contains("face")) {
                stringBuffer.append("/人脸");
            }
            if (mode.contains("rf")) {
                stringBuffer.append("/感应");
            }
            if (mode.contains("eye")) {
                stringBuffer.append("/虹膜");
            }
            if (mode.contains("vena")) {
                stringBuffer.append("/指静脉");
            }
            if (mode.contains("remote")) {
                stringBuffer.append("/远程");
            }
            if (mode.contains("timeslot")) {
                stringBuffer.append("/时间段");
            }else{
                stringBuffer.append("/所有");
            }
            return stringBuffer;
        }

        private String SelectCharacter(String content) {
            switch (content) {
                case "del":
                    stringmsg = ("删除" + "\u3000\u3000");
                    break;
                case "update":
                    stringmsg = ("修改" + "\u3000\u3000");
                    break;
                case "add":
                case "create":
                    stringmsg = ("新增" + "\u3000\u3000");
                    break;
            }
            return stringmsg;
        }

        public class ViewHolder {
            public ImageView iv;
            public TextView title;
            public TextView summary;
            public CheckBox checkBox;
        }
    }

}
