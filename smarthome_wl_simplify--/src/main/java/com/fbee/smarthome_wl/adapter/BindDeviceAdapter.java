package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.zllctl.DeviceInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WLPC on 2018/1/24.
 */

public class BindDeviceAdapter extends BaseAdapter {

    private Context context;
    private List<DeviceInfo> datas;
    private Map<Integer, Boolean> isSelectedMap;
    public BindDeviceAdapter(Context context, List<DeviceInfo> datas) {
        isSelectedMap = new HashMap<Integer, Boolean>();
        this.context=context;
        this.datas=datas;
    }
    public boolean getisSelectedAt(int position) {

        //如果当前位置的key值为空，则表示该item未被选择过，返回false，否则返回true

        if (isSelectedMap.get(position) != null) {
            return isSelectedMap.get(position);
        }
        return false;
    }
    public void setmultiItemisSelectedMap(int position, boolean isSelectedMap) {
        this.isSelectedMap.clear();
        this.isSelectedMap.put(position, isSelectedMap);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return (datas == null) ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder=null;

        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_bindscence,null);
            holder=new ViewHolder();
            holder.title= (TextView) convertView.findViewById(R.id.tv_scence_name);
            holder.checkBox= (CheckBox) convertView.findViewById(R.id.cb_bind_sence);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        DeviceInfo deviceInfo=datas.get(position);
        if(deviceInfo!=null&&deviceInfo.getDeviceName()!=null){
            holder.title.setText(deviceInfo.getDeviceName());
        }
        holder.checkBox.setChecked(getisSelectedAt(position));
        return convertView;
    }
    public class ViewHolder {
        public TextView title;
        public CheckBox checkBox;
    }
}
