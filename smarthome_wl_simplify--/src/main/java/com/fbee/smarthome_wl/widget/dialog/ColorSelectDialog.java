package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.widget.ColorPicker;


/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/13 14:25
 */
public class ColorSelectDialog extends Dialog implements ColorPicker.OnColorSelectListener {

    private int slelctcolor=-1;
    private OnColorSelectListener onColorSelectListener;
    private TextView dialogTitle;
    private ColorPicker colorpicker;
    private LinearLayout askedLl;
    private TextView tvCancel;
    private TextView tvOk;

    private int red,green,blue;
    public ColorSelectDialog(Context context) {
        super(context,R.style.MyDialog);
    }

    public ColorSelectDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public ColorSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color_picker);

        Window window = getWindow();
        window.setWindowAnimations(android.R.style.Animation_Translucent); // 添加动画

        dialogTitle = (TextView) findViewById(R.id.dialog_title);
        colorpicker = (ColorPicker) findViewById(R.id.colorpicker);
        askedLl = (LinearLayout) findViewById(R.id.asked_ll);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvOk = (TextView) findViewById(R.id.tv_ok);

        colorpicker.setOnColorSelectListener(this);

        initData();

    }

    private void initData() {

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onColorSelectListener != null) {
                    onColorSelectListener.onSelectFinish(slelctcolor);
                    cancel();
                }
            }
        });

    }

    @Override
    public void onColorSelect(int color) {
        slelctcolor=color;
    }

    public static interface OnColorSelectListener {
        public void onSelectFinish(int color);
    }

    public OnColorSelectListener getOnColorSelectListener() {
        return onColorSelectListener;
    }

    public void setOnColorSelectListener(OnColorSelectListener onColorSelectListener) {
        this.onColorSelectListener = onColorSelectListener;
    }

}
