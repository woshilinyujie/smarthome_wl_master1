package com.fbee.smarthome_wl.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

/**
 * @author Created by ZhaoLi.Wang
 * @Description: TODO dialog 工具类，所有对话框统一创建在这个类，统一调用
 * @date 2016-12-6 上午11:09:31
 */
public class DialogManager {

    private static View partingLine;

    /**
     * 普通对话框
     *
     * @param builder
     * @param listener
     */
    public static void showDialog(Context context, Builder builder, final DialogListener listener) {
        final AlertDialog dlg = new AlertDialog.Builder(context, R.style.MyDialog).create();
        dlg.show();
        init(dlg, builder, listener);
    }


    private static void init(final AlertDialog dlg, Builder builder,
                             final DialogListener listener) {
        //点击外部区域能否被销毁
        dlg.setCancelable(builder.cancelable);
        Window window = dlg.getWindow();
        window.setContentView(R.layout.dialog_common);


//        if(android.os.Build.VERSION.SDK_INT  <= Build.VERSION_CODES.KITKAT_WATCH){
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
//        }


        TextView title = (TextView) window.findViewById(R.id.tv_dialog_title);
        TextView msg = (TextView) window.findViewById(R.id.tv_dialog_content);
        TextView mcancle_btn = (TextView) window.findViewById(R.id.tv_left_btn);
        TextView ok_btn = (TextView) window.findViewById(R.id.tv_right_btn);
        partingLine = window.findViewById(R.id.parting_line);

        if (builder.gravity != 0) {
            msg.setGravity(builder.gravity);
        }
        //title
        if (TextUtils.isEmpty(builder.title)) {
            title.setVisibility(View.GONE);
            partingLine.setVisibility(View.GONE);
        } else {
            title.setText(builder.title);
        }
        msg.setText(builder.msg);
        if (builder.titleColor != 0) {
            title.setTextColor(builder.titleColor);
        }
        if (builder.msgColor != 0) {
            msg.setTextColor(builder.msgColor);
        }

        //右侧按钮
        ok_btn.setText(builder.rightBtnText);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.cancel();
                if (listener != null)
                    listener.onRightClick();
            }
        });
        if (builder.rightBtnTextColor != 0) {
            ok_btn.setTextColor(builder.rightBtnTextColor);
        }

        //左侧按钮
        mcancle_btn.setText(builder.leftBtnText);
        mcancle_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.cancel();
                if (listener != null)
                    listener.onLeftClick();
            }
        });
        if (builder.leftBtnTextColor != 0) {
            mcancle_btn.setTextColor(builder.leftBtnTextColor);
        }


    }


    public static class Builder {
        private String title;
        private String msg;
        private String confirmBtnText;
        private String leftBtnText;
        private String rightBtnText;
        private boolean cancelable;
        private int gravity;
        // other setting
        private int titleColor;
        private int msgColor;
        private int confirmBtnTextColor;
        private int leftBtnTextColor;
        private int rightBtnTextColor;


        public Builder Contentgravity(int gravity) {
            this.gravity = gravity;
            return this;
        }


        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder confirmBtnText(String confirmBtnText) {
            this.confirmBtnText = confirmBtnText;
            return this;
        }

        public Builder leftBtnText(String leftBtnText) {
            this.leftBtnText = leftBtnText;
            return this;
        }

        public Builder rightBtnText(String rightBtnText) {
            this.rightBtnText = rightBtnText;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder titleColor(int color) {
            this.titleColor = color;
            return this;
        }

        public Builder msgColor(int color) {
            this.msgColor = color;
            return this;
        }

        public Builder confirmBtnTextColor(int color) {
            this.confirmBtnTextColor = color;
            return this;
        }

        public Builder leftBtnTextColor(int color) {
            this.leftBtnTextColor = color;
            return this;
        }

        public Builder rightBtnTextColor(int color) {
            this.rightBtnTextColor = color;
            return this;
        }
    }


    public interface DialogListener {
        void onLeftClick();

        void onRightClick();

        void onConfirmClick();

        void onDismiss();

    }


    public static abstract class ConfirmDialogListener implements DialogListener {
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onConfirmClick() {
            // TODO Auto-generated method stub

        }
    }


}
