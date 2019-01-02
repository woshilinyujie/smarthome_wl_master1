package com.fbee.smarthome_wl.adapter.videodoorlock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.FunctionOnOffInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WLPC on 2017/9/22.
 */

public class FunctionOnOffAdapter extends BaseAdapter {

    private List<FunctionOnOffInfo> items;
    private LayoutInflater inflater;
    private Map<Integer,String> map;
    public FunctionOnOffAdapter(Context context, List<FunctionOnOffInfo> items) {
        this.items = items;
        inflater=LayoutInflater.from(context);
        map=new HashMap<>();
        initMap();
    }
    public void initMap(){
        for (int i = 0; i <items.size() ; i++) {
            map.put(i,items.get(i).isSelect());
        }
    }
    public Map<Integer, String> getMap(){
        return map;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView= inflater.inflate(R.layout.item_function_on_off,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        FunctionOnOffInfo itemData=items.get(position);
        if(itemData!=null){
            viewHolder.tv.setText(itemData.getName());
            if(itemData.isSelect().equals("1")){
                viewHolder.checkBox.setChecked(true);
            }else if(itemData.isSelect().equals("0")){
                viewHolder.checkBox.setChecked(false);
            }
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    map.put(position,"1");
                }else{
                    map.put(position,"0");
                }
            }
        });
        return convertView;
    }

    private class ViewHolder{
        public TextView tv;
        public CheckBox checkBox;
        public ViewHolder(View view) {
            tv= (TextView) view.findViewById(R.id.door_lock_msg);
            checkBox= (CheckBox) view.findViewById(R.id.chencB_item_function);
        }
    }
}
