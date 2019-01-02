package com.fbee.smarthome_wl.ui.mainsimplify.home.other;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.bean.DeviceStateInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogColor;
import com.fbee.smarthome_wl.widget.dialog.DialogCurtain;
import com.fbee.smarthome_wl.widget.dialog.DialogSwitch;
import com.fbee.smarthome_wl.widget.dialog.DialogTemperature;
import com.fbee.zllctl.DeviceInfo;

import rx.Subscription;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 */
public class FBotherFragment extends BaseFragment implements View.OnClickListener{

    private TextView tvOnlineState;
    private TextView tvDeviceType;
    private TextView tvDeviceControlName;
    private TextView tvDeviceMore;

    private DeviceInfo deviceInfo;

    public FBotherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_fbother;
    }

    @Override
    public void initView() {
        tvOnlineState = (TextView) mContentView.findViewById(R.id.tv_online_state);
        tvDeviceType = (TextView) mContentView.findViewById(R.id.tv_device_type);
        tvDeviceControlName = (TextView) mContentView.findViewById(R.id.tv_device_control_name);
        tvDeviceMore = (TextView) mContentView.findViewById(R.id.tv_device_more);
        tvDeviceType.setOnClickListener(this);
        tvDeviceMore.setOnClickListener(this);
    }

    @Override
    public void bindEvent() {
        initApi();
        deviceInfo= (DeviceInfo) getArguments().getSerializable("other");
        if(deviceInfo!=null){
            receiveDeviceState();
            byte state=deviceInfo.getDeviceStatus();
            if(state>0){
                tvOnlineState.setText("在线");
                tvOnlineState.setTextColor(getResources().getColor(R.color.colorAccent));
            }else{
                tvOnlineState.setText("离线");
                tvOnlineState.setTextColor(getResources().getColor(R.color.red));
            }
            if(deviceInfo.getDeviceState() == 1){
                tvDeviceType.setText("已开");
            }else if(deviceInfo.getDeviceState() == 0){
                tvDeviceType.setText("已关");
            }

        }
    }
    
    private void receiveDeviceState(){
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };

        //接收设备状态serial中getDeviceState_CallBack(int state, int uId) 上报来的数据
        Subscription mSubscriptionState = RxBus.getInstance().toObservable(DeviceStateInfo.class)
                .compose(TransformUtils.<DeviceStateInfo>defaultSchedulers())
                .subscribe(new Action1<DeviceStateInfo>() {
                    @Override
                    public void call(DeviceStateInfo event) {
                        if(deviceInfo.getUId() == event.getuId()){
                            if(event.getState() ==0) {
                                tvDeviceType.setText("已关");
                            }else if(1==event.getState()){
                                tvDeviceType.setText("已开");
                            }
                        }
                    }
                },onErrorAction);
        mCompositeSubscription.add(mSubscriptionState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //开关控制
            case R.id.tv_device_type:
                if("已开".equals(tvDeviceType.getText().toString())){
                    AppContext.getInstance().getSerialInstance().setDeviceState(deviceInfo,0);
                }else if("已关".equals(tvDeviceType.getText().toString())){
                    AppContext.getInstance().getSerialInstance().setDeviceState(deviceInfo,1);
                }
            break;

            //更多操作
            case R.id.tv_device_more:
                //判断是否是插座
                if (deviceInfo.getDeviceId() == DeviceList.DEVICE_ID_SOCKET) {
                    DialogSwitch dialogSwitch = new DialogSwitch(getActivity(), deviceInfo);
                    dialogSwitch.show();
                }
                //色温灯
                else if (deviceInfo.getDeviceId() == DeviceList.DEVICE_ID_COLOR_TEMP1 ||
                        deviceInfo.getDeviceId() == DeviceList.DEVICE_ID_COLOR_TEMP2
                        ) {
                    DialogTemperature dialog = new DialogTemperature(mContext, deviceInfo);
                    dialog.show();

                }
                //彩灯
                else if (deviceInfo.getDeviceId() == DeviceList.DEVICE_ID_COLOR_PHILIPS) {
                    DialogColor dialog = new DialogColor(mContext, deviceInfo);
                    dialog.show();
                }
                //窗帘
                else if (deviceInfo.getDeviceId() == DeviceList.DEVICE_ID_CURTAIN) {
                    new DialogCurtain(mContext, deviceInfo).show();
                }
                //智能开关
                else if (deviceInfo.getDeviceId() == DeviceList.DEVICE_ID_SWITCH) {
                    DialogSwitch dialogSwitch = new DialogSwitch(getActivity(), deviceInfo);
                    dialogSwitch.show();
                }else{
                    showToast("未知设备");
                }
            break;
        }
    }
}
