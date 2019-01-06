package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.MyDeviceInfo;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.constant.FactoryType;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/18 19:28
 */
public class DeviceManagerAdapter extends BaseAdapter {
    private List<MyDeviceInfo> mDataList;
    private ViewHolder holder;
    private Context context;

    public DeviceManagerAdapter(List<MyDeviceInfo> mDataList, Context context) {
        this.mDataList = mDataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mDataList==null ? 0: mDataList.size();
    }

    @Override
    public MyDeviceInfo getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_scenario_manager, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyDeviceInfo info= mDataList.get(position);
        holder.tvName.setText(info.getName());
        if(FactoryType.EQUES.equals(info.getSupplier())){
            holder.tvImage.setImageResource(R.mipmap.eques_monitor);
        }else if(FactoryType.FBEE.equals(info.getSupplier())){
            if(info.getDeviceType().equals("WonlySmartEyeYs7")){
                holder.tvImage.setImageResource(R.mipmap.eques_monitor);
            }else if(info.getDeviceType().equals("WonlyCameraYs7")){
                holder.tvImage.setImageResource(R.mipmap.item_visitor);
            }else{
                switch (Integer.parseInt(info.getDeviceType())){
                    //门锁
                    case DeviceList.DEVICE_ID_DOOR_LOCK:
                        holder.tvImage.setImageResource(R.mipmap.device_door_lock);

                        break;
                    //色温灯
                    case DeviceList.DEVICE_ID_COLOR_TEMP1:
                    case DeviceList.DEVICE_ID_COLOR_TEMP2:
                        holder.tvImage.setImageResource(R.mipmap.device_color_bulb);
                        break;
                    //插座
                    case DeviceList.DEVICE_ID_SOCKET:
                        holder.tvImage.setImageResource(R.mipmap.socket);
                        break;
                    //彩灯
                    case DeviceList.DEVICE_ID_COLOR_PHILIPS:
                        holder.tvImage.setImageResource(R.mipmap.device_color_bulb);
                        break;
                    //窗帘
                    case DeviceList.DEVICE_ID_CURTAIN :
                        holder.tvImage.setImageResource(R.mipmap.blind);
                        break;
                    //开关
                    case DeviceList.DEVICE_ID_SWITCH:
                        //未知设备
                        holder.tvImage.setImageResource(R.mipmap.mul_switch);
                        break;
                    default:
                        //未知设备
                        holder.tvImage.setImageResource(R.mipmap.default_device);
                        break;
                }

            }
        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView tvImage;
        private TextView tvName;

        public ViewHolder(View view) {
            tvImage = (ImageView) view.findViewById(R.id.tv_image);
            tvName = (TextView) view.findViewById(R.id.tv_name);
        }



    }
}
