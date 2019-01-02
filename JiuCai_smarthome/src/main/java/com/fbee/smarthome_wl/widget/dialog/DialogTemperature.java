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
import android.widget.SeekBar;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.DeviceStateInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.DeviceHueEvent;
import com.fbee.smarthome_wl.event.DeviceLevelEvent;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/17 10:37
 */
public class DialogTemperature extends Dialog{
    private TextView tvDeviceName;
    private ImageView tvCancel;
    private SwitchCompat switch1;
    private SeekBar seekBarLight;
    private SeekBar seekBarTemperature;
    protected CompositeSubscription mCompositeSubscription;
    private OnTemperatureListener listener;
    private DeviceInfo deviceInfo;
    public DialogTemperature(Context context, DeviceInfo info) {
        super(context, R.style.MyDialog);
        this.deviceInfo = info;
    }

    protected DialogTemperature(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public DialogTemperature(Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_color_temperature_light);
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
        seekBarLight = (SeekBar) findViewById(R.id.seekBar_light);
        seekBarTemperature = (SeekBar) findViewById(R.id.seekBar_temperature);



        if(deviceInfo == null)
            return;
        mCompositeSubscription = new CompositeSubscription();

        tvDeviceName.setText(deviceInfo.getDeviceName());
//        if(deviceInfo.getDeviceState() == 1){
//            switch1.setChecked(true);
//        }else if(deviceInfo.getDeviceState() == 0){
//            switch1.setChecked(false);
//        }


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
                               switch1.setChecked(false);
                           }else if(1==event.getState()){
                               switch1.setChecked(true);
                           }
                        }

                    }
                },onErrorAction);
        //亮度
        Subscription mSubscriptionLevel = RxBus.getInstance().toObservable(DeviceLevelEvent.class) .compose(TransformUtils.<DeviceLevelEvent>defaultSchedulers())
                .subscribe(new Action1<DeviceLevelEvent>() {
                    @Override
                    public void call(DeviceLevelEvent event) {
                        if(deviceInfo.getUId() == event.getuId()){
                            seekBarLight.setProgress(event.getLevel());
                        }

                    }
                });

        //饱和度
//        Subscription mSubscriptionSat = RxBus.getInstance().toObservable(DeviceSatEvent.class) .compose(TransformUtils.<DeviceSatEvent>defaultSchedulers())
//                .subscribe(new Action1<DeviceSatEvent>() {
//                    @Override
//                    public void call(DeviceSatEvent event) {
//                        if(deviceInfo.getUId() == event.getuId()){
//
//                            int sat = event.getSat();
//                            LogUtil.e("DialogTemperature","sat"+sat);
//                            int  progress=(hue*sat)/(254*254);
//
//                        }
//
//                    }
//                });

        //颜色
        Subscription mSubscriptionHue = RxBus.getInstance().toObservable(DeviceHueEvent.class) .compose(TransformUtils.<DeviceHueEvent>defaultSchedulers())
                .subscribe(new Action1<DeviceHueEvent>() {
                    @Override
                    public void call(DeviceHueEvent event) {
                        if(deviceInfo.getUId() == event.getuId()){
                            seekBarTemperature.setProgress( event.getHue());
                        }

                    }
                });
        mCompositeSubscription.add(mSubscriptionState);
        mCompositeSubscription.add(mSubscriptionLevel);
        mCompositeSubscription.add(mSubscriptionHue);




        ThreadPoolUtils.getInstance().getSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                //设备状态
                AppContext.getInstance().getSerialInstance().getDeviceState(deviceInfo);
                //获取亮度
                AppContext.getInstance().getSerialInstance().getDeviceLevel(deviceInfo);
                //颜色
                AppContext.getInstance().getSerialInstance().getDeviceHue(deviceInfo);

            }
        });


        initData();

    }

    private void initData() {
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
                    AppContext.getInstance().getSerialInstance().setDeviceState(deviceInfo,1);
                }else{
                    AppContext.getInstance().getSerialInstance().setDeviceState(deviceInfo,0);
                }


            }
        });

        seekBarLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                       int ret = AppContext.getInstance().getSerialInstance().setDeviceLevel(deviceInfo,(byte)seekBar.getProgress());
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

                            }
                        });



            }
        });

        seekBarTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        int progress = seekBar.getProgress();
                        int ret=-1;
                        if(progress<=12){
                            ret=   AppContext.getInstance().getSerialInstance().SetColorTemperature(deviceInfo,2700);

                        }else if(progress>=242){
                            ret =AppContext.getInstance().getSerialInstance().SetColorTemperature(deviceInfo,6500);
                        }else{
                            int temp = 3800*progress/255 + 2700;
                            ret= AppContext.getInstance().getSerialInstance().SetColorTemperature(deviceInfo,temp);
                        }

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


                            }
                        });


            }
        });


    }


    public interface  OnTemperatureListener{
        //亮度
        void onStopLightTouch(int progress);
        //色温
        void onStopTempTouch(int progress);
        //开关
        void onSwitch(boolean b);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("DialogTemperature","onStop");
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
