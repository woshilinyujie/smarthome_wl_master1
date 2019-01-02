package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.DialogChoseAdapter;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.layoutmanager.FullyLinearLayoutManager;

import java.util.List;

/**
 * Created by WLPC on 2017/5/12.
 */

public class DialogChose extends Dialog  implements BaseRecylerAdapter.OnItemClickLitener{
    private DialogChose.OnItemClickListener listener;
    private RecyclerView lvDialogChose;
    private TextView tvDismiss;
    private Context mContext;
    private int[] SHADOWS_COLORS = new int[] { 0x00F5F5F5, 0x00FFFFFF,
            0x00AAAAAA };
    private  List<String> list;
    private DialogChoseAdapter adapter;

    public DialogChose(Context context, List<String> list, DialogChose.OnItemClickListener listener){
        super(context, R.style.address_dialog_style);
        this.mContext = context;
        this.list = list;
        this.listener = listener;
        setContentView(R.layout.dialog_chose);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        initData();
    }

    private void initData() {
        tvDismiss = (TextView) findViewById(R.id.tv_dismiss);
        lvDialogChose = (RecyclerView) findViewById(R.id.lv_dialog_chose);
        FullyLinearLayoutManager addm=new FullyLinearLayoutManager(mContext);
        //LinearLayoutManager addm = new LinearLayoutManager(this);
        lvDialogChose.setLayoutManager(addm);
        lvDialogChose.setItemAnimator(new DefaultItemAnimator());
        adapter=new DialogChoseAdapter(mContext,list);
        lvDialogChose.setAdapter(adapter);
        adapter.setOnItemClickLitener(this);
        tvDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    public DialogChose(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogChose(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onItemClick(View view, int position) {
        listener.onItemClickback(view,position);
        cancel();
    }

    public interface  OnItemClickListener{
        void onItemClickback(View view,int position);
    }

}
