package com.fbee.smarthome_wl.widget.dialog.choosecateye;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.widget.wheel.OnWheelChangedListener;
import com.fbee.smarthome_wl.widget.wheel.WheelView;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/21 10:38
 */
public class DialogChooseCateye extends Dialog implements OnWheelChangedListener {

    private TextView tvDeviceName;
    private TextView tvOk;
    private TextView tvCancel;
    private WheelView wheelCateye;
    private Context mContext;
    private int selectedIndex;
    private OnChooseListener listener;
    private int[] SHADOWS_COLORS = new int[] { 0xefE9E9E9, 0xcfE9E9E9, 0x3fE9E9E9 };
//    private int[] SHADOWS_COLORS = new int[] { 0x00AAAAAA, 0x00AAAAAA,
//            0x00AAAAAA };
    public DialogChooseCateye(Context context,OnChooseListener listener) {
        super(context, R.style.address_dialog_style);
        this.mContext = context;
        this.listener =listener;
    }

    public DialogChooseCateye(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogChooseCateye(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_cateye);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvOk = (TextView) findViewById(R.id.tv_ok);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        wheelCateye = (WheelView) findViewById(R.id.wheel_cateye);
        initData();
        initWheel();

    }

    private void initWheel() {
        wheelCateye.setShadowColor(SHADOWS_COLORS[0], SHADOWS_COLORS[1], SHADOWS_COLORS[2]);
        wheelCateye.addChangingListener(this);
        wheelCateye.setVisibleItems(5);
        wheelCateye.setViewAdapter(new AddressTextAdapter(mContext,getDeviceNames()));
        wheelCateye.setCurrentItem(0);
        selectedIndex =0;
    }


    private void initData(){
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    listener.onChooseCallback(AppContext.getBdylist().get(selectedIndex));
                }catch (Exception e){
                }
                cancel();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }


    private String[] getDeviceNames() {
        List<EquesListInfo.bdylistEntity> list = AppContext.getBdylist();
        String[] names = new String[list.size()];
        for (int i = 0; i < list.size(); i ++) {
            names[i] = list.get(i).getNick() ==null || list.get(i).getNick().length() ==0 ?list.get(i).getName():list.get(i).getNick();
        }
        return names;
    }



    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        selectedIndex = newValue;
    }


    public interface  OnChooseListener{
        void onChooseCallback(EquesListInfo.bdylistEntity info);
    }


}
