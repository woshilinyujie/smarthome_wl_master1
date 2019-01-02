package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.DeviceStateInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by WLPC on 2017/4/18.
 */

public class DialogSwitch extends Dialog {
    private TextView tvDeviceName;
    private ImageView tvCancel;
    private SwitchCompat switch1;
    protected CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private DeviceInfo info;
    public DialogSwitch(Context context, DeviceInfo info) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.info = info;
    }

    public DialogSwitch(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogSwitch(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_switch);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvCancel = (ImageView) findViewById(R.id.tv_cancel);
        switch1 = (SwitchCompat) findViewById(R.id.switch1);
        if(info ==null)
            return;
        mCompositeSubscription = new CompositeSubscription();
        if(info.getDeviceState() == 1){
            switch1.setChecked(true);
        }else if(info.getDeviceState() == 0){
            switch1.setChecked(false);
        }



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
                        if(info.getUId() == event.getuId()){
                            if(event.getState() ==0) {
                                switch1.setChecked(false);
                            }else if(1==event.getState()){
                                switch1.setChecked(true);
                            }
                        }

                    }
                },onErrorAction);
        mCompositeSubscription.add(mSubscriptionState);
        tvDeviceName.setText(info.getDeviceName());
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppContext.getInstance().getSerialInstance().setDeviceState(info,1);
                }else{
                    AppContext.getInstance().getSerialInstance().setDeviceState(info,0);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("DialogSwitch","onStop");
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
