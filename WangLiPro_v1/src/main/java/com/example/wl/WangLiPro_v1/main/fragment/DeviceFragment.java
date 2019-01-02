package com.example.wl.WangLiPro_v1.main.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.devices.DevicesInfoActivity;
import com.example.wl.WangLiPro_v1.devices.DoorLockLogActivity;
import com.jwl.android.jwlandroidlib.bean.Device;
import com.jwl.android.jwlandroidlib.bean.DeviceInfo;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends Fragment implements View.OnClickListener {

    private DeviceInfo deviceInfo;
    private TextView power;
    private ImageView imageRecord;
    private Context myContext;
    //    private ImageView userList;
    private Intent intent;
    private Intent intent1;
    private String deviceId;
    private TextView tvDeviceInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_device, container, false);
        power = (TextView) mContentView.findViewById(R.id.tv_eq_power_state);
        imageRecord = (ImageView) mContentView.findViewById(R.id.image_arlm_record);
        tvDeviceInfo = (TextView) mContentView.findViewById(R.id.tv_eq_device_info);
        return mContentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        Device device = (Device) arguments.getSerializable("EQ");
        deviceInfo = device.getDevice();
        //电量
        String batteryPower = deviceInfo.getBatteryPower();
        Log.e("消息", "batteryPower:" + batteryPower);
        power.setText(batteryPower);

        String bloothMacaddress = deviceInfo.getBloothMacaddress();
        Log.e("消息", "bloothMacaddress:" + bloothMacaddress);

        String bloothName = deviceInfo.getBloothName();
        Log.e("消息", "bloothName:" + bloothName);
        //门锁ID
        deviceId = deviceInfo.getDeviceId();
        Log.e("消息", "deviceId:" + deviceId);
        //门锁名称
        String deviceName = deviceInfo.getDeviceName();
        Log.e("消息", "deviceName:" + deviceName);

        String openDoorKey = deviceInfo.getOpenDoorKey();
        Log.e("消息", "openDoorKey:" + openDoorKey);

        String randomKey = deviceInfo.getRandomKey();
        Log.e("消息", "randomKey:" + randomKey);
        //一个序列号
        String serialNumber = deviceInfo.getSerialNumber();
        Log.e("消息", "serialNumber:" + serialNumber);
        //状态？
        int status = deviceInfo.getStatus();
        Log.e("消息", "status:" + status);
        //门锁版本？
        String version = deviceInfo.getVersion();
        Log.e("消息", "version:" + version);
        //QQ?
        String wifiSSID = deviceInfo.getWifiSSID();
        Log.e("消息", "wifiSSID:" + wifiSSID);
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myContext = context;
    }

    private void initData() {
        imageRecord.setOnClickListener(this);
        tvDeviceInfo.setOnClickListener(this);
//        userList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_arlm_record:
                intent = new Intent(myContext, DoorLockLogActivity.class);
                intent.putExtra("MSG", "LOG");
                startActivity(intent);
                break;
            case R.id.tv_eq_device_info:

                intent1 = new Intent(myContext, DevicesInfoActivity.class);
                intent1.putExtra("MSG", "USER");
                intent1.putExtra("ID", deviceId);
                startActivity(intent1);
                break;
        }
    }

}
