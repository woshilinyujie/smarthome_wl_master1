package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.zllctl.TimerInfo;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/25 17:15
 */
public class PlanListAdapter extends BaseAdapter {
    private List<TimerInfo> mDatas;
    private Context mContext;


    public PlanListAdapter(List<TimerInfo> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_plan_list, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.tv_plan_time);
            holder.week = (TextView) convertView.findViewById(R.id.tv_plan_week);
            holder.perform = (TextView) convertView.findViewById(R.id.tv_perform);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TimerInfo info=mDatas.get(position);

        byte workmode = info.getWorkMode();

        StringBuffer buffer = new StringBuffer();
        if( (workmode &0x01)!= 0){
            buffer.append("周一");
            buffer.append(" ");
        }
        if((workmode &0x02)!= 0){
            buffer.append("周二");
            buffer.append(" ");
        }

        if((workmode &0x04)!= 0){
            buffer.append("周三");
            buffer.append(" ");
        }
        if((workmode &0x08)!= 0){
            buffer.append("周四");
            buffer.append(" ");
        }
        if((workmode &0x10)!= 0){
            buffer.append("周五");
            buffer.append(" ");
        }

        if((workmode &0x20)!= 0){
            buffer.append("周六");
            buffer.append(" ");
        }

        if((workmode &0x40)!= 0){
            buffer.append("周日");
        }
        StringBuffer str = new StringBuffer();
        if(info.getH()<=9){
            str.append("0");
        }
        str.append(String.valueOf(info.getH())+":");
        if(info.getM()<=9){
            str.append("0");
        }
        str.append(String.valueOf(info.getM())+":");
        if(info.getS()<=9){
            str.append("0");
        }
        str.append(String.valueOf(info.getS()));


        holder.time.setText(str.toString());
        holder.week.setText(buffer);
        if(info.AddressMode == 1 ){
            holder.perform.setText("执行区域");
        }else if(info.AddressMode ==2){
            holder.perform.setText("执行设备");
        }else if(info.AddressMode ==3){
            holder.perform.setText("执行场景");
        }

        return convertView;
    }


    public class ViewHolder {
        public TextView time;
        public TextView week;
        public TextView perform;
    }
}
