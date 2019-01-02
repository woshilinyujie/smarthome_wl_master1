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
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.view.CircleView;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/19 17:30
 */
public class DialogGroupSet  extends Dialog {
    private TextView tvDeviceName;
    private TextView tvCancel;
    private CircleView ivColor;
    private SeekBar seekBarLight;
    private SeekBar seekBarTemperature;
    private ImageView ivSlatebule;
    private ImageView ivBlue;
    private ImageView ivGreen;
    private ImageView ivOrange;
    private ImageView ivRed;
    private ImageView ivPalette;
    private SwitchCompat switch1;



    private short groupid;
    private ColorSelectDialog dialog;
    private Context mContext;
    public DialogGroupSet(Context context,short groupid) {
        super(context, R.style.address_dialog_style);
        this.groupid = groupid;
        this.mContext = context;
    }

    public DialogGroupSet(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogGroupSet(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_group_seting);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        ivColor = (CircleView) findViewById(R.id.iv_color);
        seekBarLight = (SeekBar) findViewById(R.id.seekBar_light);
        seekBarTemperature = (SeekBar) findViewById(R.id.seekBar_temperature);
        ivSlatebule = (ImageView) findViewById(R.id.iv_slatebule);
        ivBlue = (ImageView) findViewById(R.id.iv_blue);
        ivGreen = (ImageView) findViewById(R.id.iv_green);
        ivOrange = (ImageView) findViewById(R.id.iv_orange);
        ivRed = (ImageView) findViewById(R.id.iv_red);
        ivPalette = (ImageView) findViewById(R.id.iv_palette);
        switch1 = (SwitchCompat) findViewById(R.id.switch1);
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
                    allcontrol((byte)1);
                }else{
                    allcontrol((byte)0);
                }
            }
        });

        //组调亮度
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
                        int ret = AppContext.getInstance().getSerialInstance().setGroupLevel(groupid,(byte)seekBar.getProgress());
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

        //组调色温
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
                            ret=   AppContext.getInstance().getSerialInstance().setGroupColorTemperature(groupid,2700);

                        }else if(progress>=242){
                            ret =AppContext.getInstance().getSerialInstance().setGroupColorTemperature(groupid,6500);
                        }else{
                            int temp = 3800*progress/255 + 2700;
                            ret= AppContext.getInstance().getSerialInstance().setGroupColorTemperature(groupid,temp);
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


        //组调颜色
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



    private void allcontrol(final byte b){
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret= AppContext.getInstance().getSerialInstance().setGroupState(groupid,b);
                subscriber.onNext(ret);
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
                int ret =  AppContext.getInstance().getSerialInstance().setGroupHueSat(groupid,hue,sat);
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



}
