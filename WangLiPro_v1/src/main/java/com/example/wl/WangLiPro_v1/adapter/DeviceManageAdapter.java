package com.example.wl.WangLiPro_v1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.jwl.android.jwlandroidlib.bean.Device;

import java.util.List;

/**
 * Created by wl on 2017/4/20.
 */

public class DeviceManageAdapter extends BaseAdapter {

    private final List<Device> deviceList;
    private final Context context;
    private String note;

    public DeviceManageAdapter(Context context, List<Device> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device, null);
            holder.gatewayName = (TextView) convertView.findViewById(R.id.tv_gateway_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.gatewayName.setText(deviceList.get(position).getDevice().getDeviceName());
        return convertView;
    }

    public class ViewHolder {
        public TextView gatewayName;
    }
}
