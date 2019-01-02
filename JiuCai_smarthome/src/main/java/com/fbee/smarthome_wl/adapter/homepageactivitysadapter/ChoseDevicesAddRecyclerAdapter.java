package com.fbee.smarthome_wl.adapter.homepageactivitysadapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;

import java.util.List;

/**
 * Created by WLPC on 2017/3/21.
 */

public class ChoseDevicesAddRecyclerAdapter extends BaseRecylerAdapter<MyDeviceInfo> {
    public OnCheckedListener mlistener;
    private boolean isChecked;
    public void setOnCheckedListener(OnCheckedListener mlistener) {
        this.mlistener = mlistener;
    }

    public ChoseDevicesAddRecyclerAdapter(Context context, List<MyDeviceInfo> mDatas) {
        super(context, mDatas, R.layout.item_addordeleterecycler);
    }
    public void  setChecked(boolean isChecked){
        this.isChecked=isChecked;
    }
    @Override
    public void convert(MyRecylerViewHolder holder, final int position) {
        switch (mDatas.get(position).getDeviceType()){
            case "门锁":
                holder.setImageResource(R.id.icon_addordeleterecycler,R.mipmap.device_door_lock);
                break;
            case "色温灯":
                holder.setImageResource(R.id.icon_addordeleterecycler,R.mipmap.device_color_bulb);
                break;
            case "插座":
                holder.setImageResource(R.id.icon_addordeleterecycler,R.mipmap.socket);
                break;
            case "彩灯":
                holder.setImageResource(R.id.icon_addordeleterecycler,R.mipmap.device_color_bulb);
                break;
            case "窗帘":
                holder.setImageResource(R.id.icon_addordeleterecycler,R.mipmap.blind);
                break;
            case "开关":
                holder.setImageResource(R.id.icon_addordeleterecycler,R.mipmap.mul_switch);
                break;
            default:
                holder.setImageResource(R.id.icon_addordeleterecycler,R.mipmap.default_device);
                break;
        }
        holder.setText(R.id.devicename_addordeleterecycler,mDatas.get(position).getName());
        CheckBox checkBox= (CheckBox) holder.getHolderView().findViewById(R.id.checkBox);
        if(this.isChecked){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mlistener.onCheckedChanged(compoundButton,b,position);
            }
        });

    }


    public interface OnCheckedListener{
        void onCheckedChanged(CompoundButton compoundButton, boolean b,int positon);
    }


}
