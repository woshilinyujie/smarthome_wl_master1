package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/21 16:31
 */
public class DialogCurtain extends Dialog {
    private TextView tvDeviceName;
    private ImageView tvCancel;
    private TextView tvClose;
    private TextView tvStop;
    private TextView tvOpen;
    private Context mContext;
    private DeviceInfo info;
    private CompositeSubscription mCompositeSubscription;
    public DialogCurtain(Context context,DeviceInfo info) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.info = info;
        mCompositeSubscription = new CompositeSubscription();
    }

    public DialogCurtain(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogCurtain(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_curtain);
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
        tvClose = (TextView) findViewById(R.id.tv_close);
        tvStop = (TextView) findViewById(R.id.tv_stop);
        tvOpen = (TextView) findViewById(R.id.tv_open);
        if(info ==null)
            return;
        tvDeviceName.setText(info.getDeviceName());
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        int ret =  AppContext.getInstance().getSerialInstance().setDeviceState(info,0);
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

                mCompositeSubscription.add(sub);
            }
        });


        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        int ret =  AppContext.getInstance().getSerialInstance().setDeviceState(info,1);
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

                mCompositeSubscription.add(sub);
            }
        });


        tvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        int ret =  AppContext.getInstance().getSerialInstance().setDeviceState(info,2);
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


                mCompositeSubscription.add(sub);
            }
        });


    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }

    }
}
