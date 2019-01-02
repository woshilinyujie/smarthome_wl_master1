package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.widget.dialog.choosecateye.AddressTextAdapter;
import com.fbee.smarthome_wl.widget.dialog.choosecateye.AddressWhellView;
import com.fbee.smarthome_wl.widget.wheel.OnWheelChangedListener;
import com.fbee.smarthome_wl.widget.wheel.WheelView;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/24 16:25
 */
public class DialogtimeSelected extends Dialog implements OnWheelChangedListener {
    private LinearLayout addressDialogLl;
    private TextView btnCancel;
    private TextView tvTimeTitle;
    private TextView btnConfirm;
    private AddressWhellView idHour;
    private AddressWhellView idMinute;
    private AddressWhellView idSecond;
    private Context mContext;

    private int hours,minutes,seconds;

    private int[] SHADOWS_COLORS = new int[] { 0xefE9E9E9, 0xcfE9E9E9, 0x3fE9E9E9 };

    private String[] hour = new String[]{"0时","1时","2时","3时","4时","5时","6时","7时","8时","9时","10时","11时","12时",
            "13时","14时","15时","16时","17时","18时","19时","20时","21时","22时","23时"};
    private String[] minute = new String[60];
    private String[] second = new String[60];
    private OnChooseListener listener;
    public DialogtimeSelected(Context context,OnChooseListener listener) {
        super(context, R.style.address_dialog_style);
        this.mContext = context;
        this.listener = listener;
        setContentView(R.layout.dialog_plan_time);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        minute = context.getResources().getStringArray(R.array.minute);
        second = context.getResources().getStringArray(R.array.second);

        addressDialogLl = (LinearLayout) findViewById(R.id.address_dialog_ll);
        btnCancel = (TextView) findViewById(R.id.btn_cancel);
        tvTimeTitle = (TextView) findViewById(R.id.tv_time_title);
        btnConfirm = (TextView) findViewById(R.id.btn_confirm);
        idHour = (AddressWhellView) findViewById(R.id.id_hour);
        idMinute = (AddressWhellView) findViewById(R.id.id_minute);
        idSecond = (AddressWhellView) findViewById(R.id.id_second);

        initData();

//        for (int i = 0; i <60 ; i++) {
//            minute[i] = String.valueOf(i+1+"分");
//            second[i] = String.valueOf(i+1+"秒");
//        }


    }

    public DialogtimeSelected(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DialogtimeSelected(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void initData() {


        idHour.setShadowColor(SHADOWS_COLORS[0], SHADOWS_COLORS[1], SHADOWS_COLORS[2]);
        idHour.addChangingListener(this);
        idHour.setVisibleItems(7);
        idHour.setViewAdapter(new AddressTextAdapter(mContext,hour));
        idHour.setCurrentItem(6);


        idMinute.setShadowColor(SHADOWS_COLORS[0], SHADOWS_COLORS[1], SHADOWS_COLORS[2]);
        idMinute.addChangingListener(this);
        idMinute.setVisibleItems(7);
        idMinute.setViewAdapter(new AddressTextAdapter(mContext,minute));
        idMinute.setCurrentItem(30);


        idSecond.setShadowColor(SHADOWS_COLORS[0], SHADOWS_COLORS[1], SHADOWS_COLORS[2]);
        idSecond.addChangingListener(this);
        idSecond.setVisibleItems(7);
        idSecond.setViewAdapter(new AddressTextAdapter(mContext,second));
        idSecond.setCurrentItem(30);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(listener != null){
                listener.onTimeCallback(hours,minutes,seconds);
                cancel();
            }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        switch (wheel.getId()){
            case R.id.id_hour:
                hours = newValue;
                break;
            case R.id.id_minute:
                minutes = newValue;
                break;

            case R.id.id_second:
                seconds = newValue;
                break;

        }

    }


    public interface  OnChooseListener{
        void onTimeCallback(int hour,int minute,int second);
    }

}
