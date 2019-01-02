package com.fbee.smarthome_wl.ui.equesdevice.equesaddlock;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.singleselectionadapter.Person;
import com.fbee.smarthome_wl.adapter.singleselectionadapter.SingleSelectionAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceListResponse;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverContract;
import com.fbee.smarthome_wl.ui.addordeldevicestosever.AddOrDelDevicesToSeverPresenter;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.view.ChoiceView;
import com.fbee.zllctl.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class EquesAddlockActivity extends BaseActivity<AddOrDelDevicesToSeverContract.Presenter> implements AddOrDelDevicesToSeverContract.View {

    private ListView bindingLock;
    private List<DeviceInfo> deviceInfos;
    private TextView title;
    private TextView rightMenu;
    private ImageView back;
    private String uId;
    private String bid;
    List<Person> datas;
    private int short_id;
    private SingleSelectionAdapter singleSelectionAdapter;
    int currentNum = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_addlock);
    }

    @Override
    protected void initView() {
        bindingLock = (ListView) findViewById(R.id.lv_binding_lock);
        title = (TextView) findViewById(R.id.title);
        rightMenu = (TextView) findViewById(R.id.tv_right_menu);
        back = (ImageView) findViewById(R.id.back);
        rightMenu.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        createPresenter(new AddOrDelDevicesToSeverPresenter(this));
    }

    @Override
    protected void initData() {

        bid = getIntent().getExtras().getString(Method.ATTR_BUDDY_BID);
        rightMenu.setText("完成");
        title.setText("请选择猫眼绑定的门锁");
        back.setOnClickListener(this);
        rightMenu.setOnClickListener(this);
        deviceInfos = AppContext.getmDoorLockDevices();
        QueryDevicesListInfo bodyEntity = new QueryDevicesListInfo();
        bodyEntity.setUuid(bid);
        bodyEntity.setVendor_name(FactoryType.EQUES);
        presenter.queryDevices(bodyEntity);
        if (datas == null || datas.size() > 0) {
            datas = new ArrayList();
            for (int i = 0; i < deviceInfos.size(); i++) {
                datas.add(new Person(deviceInfos.get(i).getDeviceName()));
            }
        }
        singleSelectionAdapter = new SingleSelectionAdapter(this);
        singleSelectionAdapter.setDatas(datas);
        bindingLock.setAdapter(singleSelectionAdapter);
        bindingLock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (Person person : datas) {
                    person.setChecked(false);
                }

                if (currentNum == -1) { //选中
                    datas.get(position).setChecked(true);
                    uId = ByteStringUtil.bytesToHexString(deviceInfos.get(position).getIEEE()).toUpperCase();
                    short_id = deviceInfos.get(position).getUId();
                    currentNum = position;
                } else if (currentNum == position) { //同一个item选中变未选中
                    for (Person person : datas) {
                        person.setChecked(false);
                    }
                    uId = null;
                    short_id = 0;
                    currentNum = -1;
                } else if (currentNum != position) { //不是同一个item选中当前的，去除上一个选中的
                    for (Person person : datas) {
                        person.setChecked(false);
                    }
                    uId = ByteStringUtil.bytesToHexString(deviceInfos.get(position).getIEEE()).toUpperCase();
                    short_id = deviceInfos.get(position).getUId();
                    datas.get(position).setChecked(true);
                    currentNum = position;
                }
                singleSelectionAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddDevicesReq addDevicesReq = new AddDevicesReq();
                AddDevicesReq.DeviceBean deviceBean = new AddDevicesReq.DeviceBean();
                addDevicesReq.setGateway_vendor_name(FactoryType.FBEE);
                addDevicesReq.setGateway_uuid(AppContext.getGwSnid());
                deviceBean.setVendor_name(FactoryType.EQUES);
                deviceBean.setUuid(bid);  //猫眼的bid
                deviceBean.setType(FactoryType.EQUESCATEYE);
                if (uId != null) {
                    deviceBean.setContext_uuid(uId);
                    deviceBean.setShort_id(short_id + "");
                } else {
                    deviceBean.setContext_uuid("");
                    deviceBean.setShort_id("");
                }
                addDevicesReq.setDevice(deviceBean);
                presenter.reqAddDevices(addDevicesReq);
                break;
            case R.id.tv_right_menu:
                AddDevicesReq addDevicesReq1 = new AddDevicesReq();
                AddDevicesReq.DeviceBean deviceBean1 = new AddDevicesReq.DeviceBean();
                addDevicesReq1.setGateway_vendor_name(FactoryType.FBEE);
                addDevicesReq1.setGateway_uuid(AppContext.getGwSnid());
                deviceBean1.setVendor_name(FactoryType.EQUES);
                deviceBean1.setUuid(bid);  //猫眼的bid
                deviceBean1.setType(FactoryType.EQUESCATEYE);
                if (uId != null) {
                    deviceBean1.setContext_uuid(uId);
                    deviceBean1.setShort_id(short_id + "");
                } else {
                    deviceBean1.setContext_uuid("");
                    deviceBean1.setShort_id("");
                }
                addDevicesReq1.setDevice(deviceBean1);
                presenter.reqAddDevices(addDevicesReq1);
                break;
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }


    /**
     * 添加设备返回
     *
     * @param bean
     */
    @Override
    public void resAddDevices(BaseResponse bean) {
        finish();
    }

    /**
     * 删除设备返回
     *
     * @param bean
     */
    @Override
    public void resDeleteDevices(BaseResponse bean) {

    }

    /***
     * 查询猫眼是否已经绑定门锁
     *
     * @param bean
     */
    @Override
    public void queryDevicesResult(QueryDeviceListResponse bean) {
        if (bean.getBody() != null) {
            if (bean.getBody().getContext_uuid() != null) {
                for (int i = 0; i < deviceInfos.size(); i++) {
                    String DeviceId = ByteStringUtil.bytesToHexString(deviceInfos.get(i).getIEEE()).toUpperCase();
                    String uuid = deviceInfos.get(i).getUId() + "";
                    String context_uuid = bean.getBody().getContext_uuid();
                    if (DeviceId.equals(context_uuid) || uuid.equals(context_uuid)) {
                        datas.get(i).setChecked(true);
                        this.uId = DeviceId;
                        currentNum = i;
                        singleSelectionAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }

    }

    @Override
    public void resReqGateWayInfo(QueryGateWayInfoReq body) {
        LogUtil.e("123456", "111");
    }

    @Override
    public void loginSuccess(Object obj) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (datas != null) {
            datas.clear();
        }
    }
}
