package com.fbee.smarthome_wl.ui.devicemanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.BindDeviceAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.event.BindDevSenceEvent;
import com.fbee.smarthome_wl.event.BindDeviceInfoEvent;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.Serial;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

import static com.fbee.smarthome_wl.constant.DeviceList.DEVICE_ID_COLOR_PHILIPS;
import static com.fbee.smarthome_wl.constant.DeviceList.DEVICE_ID_COLOR_TEMP1;
import static com.fbee.smarthome_wl.constant.DeviceList.DEVICE_ID_COLOR_TEMP2;
import static com.fbee.smarthome_wl.constant.DeviceList.DEVICE_ID_CURTAIN;
import static com.fbee.smarthome_wl.constant.DeviceList.DEVICE_ID_SOCKET;

public class BindDeviceActivity extends BaseActivity {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ListView lvBindDeciceSence;
    private int[] tartUids;
    private List<DeviceInfo> datas;
    private BindDeviceAdapter adapter;
    private int mDeviceUid = -1;
    private Serial serial;
    private List<Integer> primaryList;
    private DeviceInfo priDevice;
    private DeviceInfo mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device_sence);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        lvBindDeciceSence = (ListView) findViewById(R.id.lv_bind_decice_sence);
    }

    @Override
    protected void initData() {
        initApi();
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("绑定设备");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        tvRightMenu.setText("完成");
        mDeviceUid = getIntent().getIntExtra("deviceUid", -1);
        //tartUids=getIntent().getIntArrayExtra("deviceUids");
        primaryList = new ArrayList<>();
        datas = new ArrayList<DeviceInfo>();
        List<DeviceInfo> devices = AppContext.getmOurDevices();
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getUId() == mDeviceUid) {
                mDeviceInfo = devices.get(i);
            }

            if (devices.get(i).getDeviceId() == DeviceList.DEVICE_ID_SWITCH
                    || devices.get(i).getDeviceId() == DEVICE_ID_SOCKET
                    || devices.get(i).getDeviceId() == DEVICE_ID_COLOR_TEMP1
                    || devices.get(i).getDeviceId() == DEVICE_ID_COLOR_TEMP2
                    || devices.get(i).getDeviceId() == DEVICE_ID_COLOR_PHILIPS
                    || devices.get(i).getDeviceId() == DEVICE_ID_CURTAIN) {
                datas.add(devices.get(i));
            }
        }
        adapter = new BindDeviceAdapter(this, datas);
        lvBindDeciceSence.setAdapter(adapter);
        lvBindDeciceSence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getisSelectedAt(position)) {
                    adapter.setmultiItemisSelectedMap(position, false);
                } else {
                    adapter.setmultiItemisSelectedMap(position, true);
                }
            }
        });

        mSubscription = RxBus.getInstance().toObservable(BindDeviceInfoEvent.class)
                .compose(TransformUtils.<BindDeviceInfoEvent>defaultSchedulers())
                .subscribe(new Action1<BindDeviceInfoEvent>() {
                    @Override
                    public void call(BindDeviceInfoEvent event) {
                        //接收绑定设备信息
                        if (event == null) return;
                        if (event.getUid() == mDeviceUid) {

                            int[] tartUids = event.getTargetDeviceIds();
                            if (tartUids != null && tartUids.length > 0) {

                                for (int j = 0; j < datas.size(); j++) {
                                    if (tartUids[0] == datas.get(j).getUId()) {
                                        priDevice = datas.get(j);
                                        adapter.setmultiItemisSelectedMap(j, true);
                                    }
                                }

                            }
                        }
                    }
                });
        mCompositeSubscription.add(mSubscription);
        serial = AppContext.getInstance().getSerialInstance();
        if (serial != null) {
            serial.getBindInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                showLoadingDialog(null);
                DeviceInfo selectDevice = null;
                for (int i = 0; i < datas.size(); i++) {
                    if (adapter.getisSelectedAt(i)) {
                        selectDevice = datas.get(i);
                    }
                }
                if (priDevice != null) {
                    if (selectDevice != null) {
                        if (selectDevice.getUId() == priDevice.getUId()) {
                            hideLoadingDialog();
                            ToastUtils.showShort("绑定成功");
                            RxBus.getInstance().post(new BindDevSenceEvent(1,selectDevice.getUId()));
                            finish();
                        } else {
                            unBandDevice(priDevice,selectDevice,1);
                        }
                    } else {
                        unBandDevice(priDevice,null,0);
                    }
                } else {
                    if(selectDevice != null){
                        bindDevice(selectDevice);
                    }else{
                        hideLoadingDialog();
                        ToastUtils.showShort("请选择绑定的设备");
                        return;
                    }
                }
                break;
        }

    }

    private void unBandDevice(final DeviceInfo deviceInfo,final DeviceInfo bindDevice,final int chose) {
        Subscription sub=Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret=serial.unBindDevice(mDeviceInfo,deviceInfo);
                subscriber.onNext(ret);
                subscriber.onCompleted();
            }
        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer ret) {
                        if(ret>=0){
                            if(chose==1){
                                priDevice=null;
                                bindDevice(bindDevice);
                            }else{
                                RxBus.getInstance().post(new BindDevSenceEvent(1,-1,""));
                                hideLoadingDialog();
                                ToastUtils.showShort("解绑成功");
                                finish();
                            }

                        }else{
                            hideLoadingDialog();
                            ToastUtils.showShort("绑定失败请重试");
                        }
                    }
                });

        mCompositeSubscription.add(sub);

    }

    private void bindDevice(final DeviceInfo deviceInfo){
        Subscription sub=Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret=serial.bindDevices(mDeviceInfo,deviceInfo);
                subscriber.onNext(ret);
                subscriber.onCompleted();
            }
        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer ret) {
                        if(ret>=0){
                            RxBus.getInstance().post(new BindDevSenceEvent(1,deviceInfo.getUId(),deviceInfo.getDeviceName()));
                            hideLoadingDialog();
                            ToastUtils.showShort("绑定成功");
                            finish();
                        }else{
                            hideLoadingDialog();
                            ToastUtils.showShort("绑定失败请重试");
                        }
                    }
                });

        mCompositeSubscription.add(sub);
    }

}
