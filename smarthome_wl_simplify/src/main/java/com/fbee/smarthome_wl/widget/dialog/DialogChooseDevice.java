package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.widget.dialog.choosecateye.AddressTextAdapter;
import com.fbee.smarthome_wl.widget.dialog.choosecateye.AddressWhellView;
import com.fbee.smarthome_wl.widget.wheel.OnWheelChangedListener;
import com.fbee.smarthome_wl.widget.wheel.WheelView;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/25 9:53
 */
public class DialogChooseDevice extends Dialog implements OnWheelChangedListener {

    private TextView tvDeviceorgroupName;
    private AddressWhellView wheelDeviceorgroup;
    private TextView tvLeftBtn;
    private TextView tvRightBtn;
    private OnChooseListener listener;


    private Context mContext;
    private int[] SHADOWS_COLORS = new int[] { 0x00F5F5F5, 0x00FFFFFF,
            0x00AAAAAA };
    private  List<String> list;
    private int  selectIndex;

    public DialogChooseDevice(Context context, List<String>  list,OnChooseListener listener) {
        super(context, R.style.address_dialog_style);
        this.mContext = context;
        this.list = list;
        this.listener = listener;
        setContentView(R.layout.dialog_choose_deviceorgroup);
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

    public DialogChooseDevice(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DialogChooseDevice(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initData() {
        tvDeviceorgroupName = (TextView) findViewById(R.id.tv_deviceorgroup_name);
        wheelDeviceorgroup = (AddressWhellView) findViewById(R.id.wheel_deviceorgroup);
        tvLeftBtn = (TextView) findViewById(R.id.tv_left_btn);
        tvRightBtn = (TextView) findViewById(R.id.tv_right_btn);

        tvLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        tvRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                listener.onChooseCallback(selectIndex);
            }
        });

        wheelDeviceorgroup.setShadowColor(SHADOWS_COLORS[0], SHADOWS_COLORS[1], SHADOWS_COLORS[2]);
        wheelDeviceorgroup.addChangingListener(this);
        wheelDeviceorgroup.setVisibleItems(5);
        wheelDeviceorgroup.setViewAdapter(new AddressTextAdapter(mContext,getDeviceNames()));
        wheelDeviceorgroup.setCurrentItem(0);
    }


    private String[] getDeviceNames() {
        String[] toBeStored = list.toArray(new String[list.size()]);
        return toBeStored;
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        selectIndex = newValue;
    }

    public interface  OnChooseListener{
        void onChooseCallback(int index);
    }


}
