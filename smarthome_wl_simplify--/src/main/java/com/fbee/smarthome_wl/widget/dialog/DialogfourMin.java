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
 * @time 2018/1/24 11:27
 */
public class DialogfourMin extends Dialog implements OnWheelChangedListener {
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

//    private String[] minute = new String[]{"0分","1分","2分","3分"};
    private String[] second = new String[60];
    private OnChooseListener listener;
    public DialogfourMin(Context context,OnChooseListener listener) {
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

        second = context.getResources().getStringArray(R.array.second);

        addressDialogLl = (LinearLayout) findViewById(R.id.address_dialog_ll);
        btnCancel = (TextView) findViewById(R.id.btn_cancel);
        tvTimeTitle = (TextView) findViewById(R.id.tv_time_title);
        btnConfirm = (TextView) findViewById(R.id.btn_confirm);
        idHour = (AddressWhellView) findViewById(R.id.id_hour);
        idMinute = (AddressWhellView) findViewById(R.id.id_minute);
        idSecond = (AddressWhellView) findViewById(R.id.id_second);
        idHour.setVisibility(View.GONE);
        idMinute.setVisibility(View.GONE);
        initData();



    }

    public DialogfourMin(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DialogfourMin(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void initData() {

        idSecond.setShadowColor(SHADOWS_COLORS[0], SHADOWS_COLORS[1], SHADOWS_COLORS[2]);
        idSecond.addChangingListener(this);
        idSecond.setVisibleItems(7);
        idSecond.setViewAdapter(new AddressTextAdapter(mContext,second));
        idSecond.setCurrentItem(1);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onTimeCallback(seconds);
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
        void onTimeCallback(int second);
    }


}
