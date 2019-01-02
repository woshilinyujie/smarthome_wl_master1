package com.example.wl.WangLiPro_v1.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;


/**
 * Created by fanliang on 17/8/1.
 */

public class DialogUtils {
    private static Dialog progressDialog = null;


    public static Dialog getDialog(Context context) {
        progressDialog = new Dialog(context, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("加载中...");
        return progressDialog;
    }


    static AlertDialog.Builder b;

    public static void showErroMsg(Context context, String msg) {
        b = new AlertDialog.Builder(context);
        b.setMessage(msg);
        b.setPositiveButton("取消", null);
        b.show();
    }
}
