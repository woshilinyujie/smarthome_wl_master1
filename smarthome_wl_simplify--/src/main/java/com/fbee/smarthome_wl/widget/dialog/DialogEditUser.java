package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/27 14:51
 */
public class DialogEditUser extends Dialog {
    private TextView tvTitleWifidialog;
    private CheckBox cbDelFingerprint;
    private CheckBox cbDelPsw;
    private CheckBox cbDelCard;
    private CheckBox cbAdd;
    private CheckBox cbDelAll;
    private TextView tvLeftCancelBtnWifidialog;
    private TextView tvRightConfirmBtnWifidialog;
    private String resultStr;
    private String model;
    private DialogListener listener;
    public DialogEditUser(Context context,DialogListener listener) {
        super(context, R.style.MyDialog);
        this.listener =listener;
    }

    protected DialogEditUser(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public DialogEditUser(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_user);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        tvTitleWifidialog = (TextView) findViewById(R.id.tv_title_wifidialog);
        cbDelFingerprint = (CheckBox) findViewById(R.id.cb_del_fingerprint);
        cbDelPsw = (CheckBox) findViewById(R.id.cb_del_psw);
        cbDelCard = (CheckBox) findViewById(R.id.cb_del_card);
        cbAdd = (CheckBox) findViewById(R.id.cb_add);
        cbDelAll = (CheckBox) findViewById(R.id.cb_del_all);
        tvLeftCancelBtnWifidialog = (TextView) findViewById(R.id.tv_left_cancel_btn_wifidialog);
        tvRightConfirmBtnWifidialog = (TextView) findViewById(R.id.tv_right_confirm_btn_wifidialog);


        cbDelFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbAdd.setChecked(false);
                    cbDelAll.setChecked(false);
                }
            }
        });
        cbDelPsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbAdd.setChecked(false);
                    cbDelAll.setChecked(false);
                }
            }
        });
        cbDelCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbAdd.setChecked(false);
                    cbDelAll.setChecked(false);
                }
            }
        });
        cbAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbDelFingerprint.setChecked(false);
                    cbDelPsw.setChecked(false);
                    cbDelCard.setChecked(false);
                    cbDelAll.setChecked(false);
                }

            }
        });
        cbDelAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbDelFingerprint.setChecked(false);
                    cbDelPsw.setChecked(false);
                    cbDelCard.setChecked(false);
                    cbAdd.setChecked(false);
                }
            }
        });

        tvLeftCancelBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        tvRightConfirmBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbDelFingerprint.isChecked()){
                    resultStr = "fd";
                    model ="del";
                }
                if(cbDelPsw.isChecked()){
                    model ="del";
                    if(null != resultStr){
                        resultStr=resultStr+","+"pwd";
                    }else{
                        resultStr = "pwd";
                    }
                }
                if(cbDelCard.isChecked()){
                    model ="del";
                    if(null != resultStr){
                        resultStr=resultStr+","+"card";
                    }else{
                        resultStr = "card";
                    }
                }
                if(cbAdd.isChecked()){
                    model ="update";
                    resultStr = "pwd,fp,card";
                }
                if(cbDelAll.isChecked()){
                    resultStr = "pwd,fp,card";
                    model ="del";
                }
                if(listener != null)
                listener.onRightClick(model,resultStr);
                cancel();
            }
        });




    }


    public interface DialogListener {
        void onRightClick(String mode, String unlock_mode);
    }



}
