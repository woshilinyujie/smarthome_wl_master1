package com.fbee.smarthome_wl.adapter;

import android.content.Context;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;

import java.util.List;

/**
 * Created by WLPC on 2017/4/12.
 */

public class ItemAreaAdapter extends BaseRecylerAdapter<MyDeviceInfo> {
    public ItemAreaAdapter(Context context, List<MyDeviceInfo> mDatas) {
        super(context, mDatas, R.layout.item_area_activity);
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        holder.setText(R.id.devicename_item_area,mDatas.get(position).getName());
        switch (mDatas.get(position).getDeviceType()){
            case "门锁":
                holder.setImageResource(R.id.icon_item_area,R.mipmap.device_door_lock);
                break;
            case "色温灯":
                holder.setImageResource(R.id.icon_item_area,R.mipmap.device_color_bulb);
                break;
            case "开关/插座":
                holder.setImageResource(R.id.icon_item_area,R.mipmap.socket);
                break;
            case "彩灯":
                holder.setImageResource(R.id.icon_item_area,R.mipmap.device_color_bulb);
                break;
            case "智能开关":
                holder.setImageResource(R.id.icon_item_area,R.mipmap.mul_switch);
                break;
            case "窗帘":
                holder.setImageResource(R.id.icon_item_area,R.mipmap.blind);
                break;
            default:
                holder.setImageResource(R.id.icon_item_area,R.mipmap.default_device);
                break;
        }
    }
}
