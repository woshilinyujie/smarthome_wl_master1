package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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
import com.fbee.smarthome_wl.event.DeviceSatEvent;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.view.CircleView;
import com.fbee.zllctl.DeviceInfo;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.fbee.smarthome_wl.R.id.iv_color;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/17 14:08
 */
public class DialogColor extends Dialog {
    private TextView tvDeviceName;
    private ImageView tvCancel;
    private SwitchCompat switch1;
    private CircleView ivColor;
    private SeekBar seekBarLight;
    private ImageView ivSlatebule;
    private ImageView ivBlue;
    private ImageView ivGreen;
    private ImageView ivOrange;
    private ImageView ivRed;
    private ImageView ivPalette;
    private ImageView ivFlashing;
    private ImageView ivDiscoloration;

    protected CompositeSubscription mCompositeSubscription;
    private ColorSelectDialog dialog;
    private Context mContext;
    private DeviceInfo info;
    private int hue;

    public DialogColor(Context context, DeviceInfo info) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.info = info;
    }

    public DialogColor(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogColor(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color_light);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        ivFlashing = (ImageView) findViewById(R.id.iv_flashing);
        ivDiscoloration = (ImageView) findViewById(R.id.iv_discoloration);
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvCancel = (ImageView) findViewById(R.id.tv_cancel);
        switch1 = (SwitchCompat) findViewById(R.id.switch1);
        ivColor = (CircleView) findViewById(iv_color);
        seekBarLight = (SeekBar) findViewById(R.id.seekBar_light);
        ivSlatebule = (ImageView) findViewById(R.id.iv_slatebule);
        ivBlue = (ImageView) findViewById(R.id.iv_blue);
        ivGreen = (ImageView) findViewById(R.id.iv_green);
        ivOrange = (ImageView) findViewById(R.id.iv_orange);
        ivRed = (ImageView) findViewById(R.id.iv_red);
        ivPalette = (ImageView) findViewById(R.id.iv_palette);
        if(info ==null)
            return;
        mCompositeSubscription = new CompositeSubscription();
//        if(info.getDeviceState() == 1){
//            switch1.setChecked(true);
//        }else if(info.getDeviceState() == 0){
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
        //亮度
        Subscription mSubscriptionLevel = RxBus.getInstance().toObservable(DeviceLevelEvent.class) .compose(TransformUtils.<DeviceLevelEvent>defaultSchedulers())
                .subscribe(new Action1<DeviceLevelEvent>() {
                    @Override
                    public void call(DeviceLevelEvent event) {
                        if(info.getUId() == event.getuId()){
                            seekBarLight.setProgress(event.getLevel());
                        }
                    }
                });
        mCompositeSubscription.add(mSubscriptionLevel);
        //饱和度
        Subscription mSubscriptionSat = RxBus.getInstance().toObservable(DeviceSatEvent.class) .compose(TransformUtils.<DeviceSatEvent>defaultSchedulers())
                .subscribe(new Action1<DeviceSatEvent>() {
                    @Override
                    public void call(DeviceSatEvent event) {
                        if(info.getUId() == event.getuId()){
                            int sat = event.getSat();
                            float[] arrayOfObject2 = new float[3];
                            arrayOfObject2[0] = hue*360.0f/255.0f;
                            arrayOfObject2[1] = sat/254.0f;
                            arrayOfObject2[2] = 1.0f;
                            ivColor.setColor( Color.HSVToColor(arrayOfObject2));
                        }

                    }
                });
        mCompositeSubscription.add(mSubscriptionSat);
        //颜色
        Subscription mSubscriptionHue = RxBus.getInstance().toObservable(DeviceHueEvent.class) .compose(TransformUtils.<DeviceHueEvent>defaultSchedulers())
                .subscribe(new Action1<DeviceHueEvent>() {
                    @Override
                    public void call(DeviceHueEvent event) {
                        if(info.getUId() == event.getuId()){
                            hue = event.getHue();
                            AppContext.getInstance().getSerialInstance().getDeviceSat(info);
                        }

                    }
                });
        mCompositeSubscription.add(mSubscriptionHue);
        ThreadPoolUtils.getInstance().getSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                //设备状态
                AppContext.getInstance().getSerialInstance().getDeviceState(info);
                //获取亮度
                AppContext.getInstance().getSerialInstance().getDeviceLevel(info);
                //颜色
                AppContext.getInstance().getSerialInstance().getDeviceHue(info);


            }
        });

        initData();
    }

    private void initData() {

        ivFlashing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(info.getDeviceName()+"已开启闪烁");
                AppContext.getInstance().getSerialInstance().identifyDevice(info,(byte)0x03);
            }
        });

        ivDiscoloration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(info.getDeviceName()+"已开启变色");
                AppContext.getInstance().getSerialInstance().setDeviceState(info,1);
                AppContext.getInstance().getSerialInstance().SendautocolorZCL(info,(byte)0x44,(byte)1,(byte)1,(short)10,(short)0);
            }
        });


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
                    AppContext.getInstance().getSerialInstance().SendautocolorZCL(info,(byte)0x44,(byte)0,(byte)1,(short)30,(short)0);
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
                        int ret = AppContext.getInstance().getSerialInstance().setDeviceLevel(info,(byte)seekBar.getProgress());
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


        ivSlatebule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#6A5ACD"));
                setColor("6A5ACD");
            }
        });


        ivBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#0000FF"));
                setColor("0000FF");
            }
        });

        ivGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#00FF00"));
                setColor("00FF00");
            }
        });


        ivOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#FFA500"));
                setColor("FFA500");
            }
        });

        ivRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivColor.setColor(Color.parseColor("#FF0000"));
                setColor("FF0000");
            }
        });

        ivPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == dialog){
                    dialog = new ColorSelectDialog(mContext);
                    dialog.setOnColorSelectListener(new ColorSelectDialog.OnColorSelectListener() {
                        @Override
                        public void onSelectFinish(int color) {
                            int red=Color.red(color);
                            int green=Color.green(color);
                            int blue=Color.blue(color);

                            ivColor.setColor(Color.rgb(red,green,blue));
                            float[] arrayOfObject2 = new float[3];
                            Color.colorToHSV(Color.rgb(red,green,blue), arrayOfObject2);
                            byte hue = (byte)((int)(arrayOfObject2[0] / 360.0F * 255.0F));
                            byte sat = (byte)((int)(arrayOfObject2[1] * 254.0F));
                            setDeviceHueSat(hue,sat);

                        }
                    });
                    dialog.show();
                }else{
                    dialog.show();
                }
            }
        });


    }


    private void setColor(String color){
        float[] arrayOfObject2 = new float[3];
        Color.colorToHSV(Integer.parseInt(color, 16), arrayOfObject2);
        byte hue = (byte)((int)(arrayOfObject2[0] / 360.0F * 255.0F));
        byte sat = (byte)((int)(arrayOfObject2[1] * 254.0F));
        setDeviceHueSat(hue,sat);
    }


    private  void setDeviceHueSat(final byte hue, final byte sat){
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret =  AppContext.getInstance().getSerialInstance().setDeviceHueSat(info,hue,sat);
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


    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("DialogColor","onStop");
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
