package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.utils.DateUtil;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/6/7 10:21
 */
public class DialogDeteleRecord extends Dialog {
    private RadioGroup rgDeteleRecord;
    private TextView tvLeftBtn;
    private TextView tvRightBtn;
    private Context mContext;
    private OnItemClickListener listener;
    private RadioButton rbOne;
    private RadioButton rbThree;
    private RadioButton rbSix;
    private RadioButton rbAll;
    long time = 0;
    private int timeChoseTag;
    int radioButtonId;
    public DialogDeteleRecord(Context context,OnItemClickListener listener) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.listener = listener;
    }

    public DialogDeteleRecord(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DialogDeteleRecord(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_detele_doorrecord);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        tvLeftBtn = (TextView) findViewById(R.id.tv_left_btn);
        tvRightBtn = (TextView) findViewById(R.id.tv_right_btn);

        rbOne = (RadioButton) findViewById(R.id.rb_one);
        rbThree = (RadioButton) findViewById(R.id.rb_three);
        rbSix = (RadioButton) findViewById(R.id.rb_six);
        rbAll = (RadioButton) findViewById(R.id.rb_all);

        tvLeftBtn.setText("取消");
        tvRightBtn.setText("确定");

        rbOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbThree.setChecked(false);
                    rbSix.setChecked(false);
                    rbAll.setChecked(false);
                    time=(DateUtil.getTodayZeroTime()-DateUtil.getOneDayMillis(30))/1000l;
                    timeChoseTag=1;
                }
            }
        });


        rbThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbOne.setChecked(false);
                    rbSix.setChecked(false);
                    rbAll.setChecked(false);
                    time=(DateUtil.getTodayZeroTime()-DateUtil.getOneDayMillis(90))/1000l;
                    timeChoseTag=3;
                }
            }
        });


        rbSix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbOne.setChecked(false);
                    rbThree.setChecked(false);
                    rbAll.setChecked(false);
                    time=(DateUtil.getTodayZeroTime()-DateUtil.getOneDayMillis(180))/1000l;
                    timeChoseTag=6;
                }
            }
        });

        rbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rbOne.setChecked(false);
                    rbThree.setChecked(false);
                    rbSix.setChecked(false);
                    time=DateUtil.getCurrentTime()/1000;
                    timeChoseTag=10;
                }
            }
        });



        tvLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        tvRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(0 == time)
                    return;
                if(null  != listener){
                    listener.onItemClickback(time,timeChoseTag);
                }

            }
        });


    }


    public interface  OnItemClickListener{
        void onItemClickback(long endTime,int timeChoseTag);
    }

}
