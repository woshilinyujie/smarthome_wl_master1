package com.fbee.smarthome_wl.adapter.videodoorlock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

import java.util.List;

/**
 * Created by WLPC on 2017/9/8.
 */

public class MySpinnerSdapter extends BaseAdapter {
    private List<String> items;
    private LayoutInflater inflater;
    public MySpinnerSdapter(Context context, List<String> items) {
        this.items = items;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items==null? 0:items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder=null;
        if(convertView==null){
            convertView= inflater.inflate(R.layout.item_lv,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        String itemData=items.get(position);
        if(itemData!=null){
            viewHolder.tv.setText(itemData);
        }
        return convertView;
    }
    private class ViewHolder{
        public TextView tv;

        public ViewHolder(View view) {
            tv= (TextView) view.findViewById(R.id.item_tv);
        }
    }
}
