package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.EquesVisitorInfo;
import com.fbee.smarthome_wl.utils.ImageLoader;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import rx.functions.Action1;

/**
 * Created by wl on 2017/4/5.
 */

public class VisitorAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<EquesVisitorInfo.RingsEntity> visitors;
    private final String name;
    private boolean b;
    private ViewHolder holder;
    private EquesVisitorInfo.RingsEntity ringsEntity;
    int isCheckBoxVisiable;
    private HashMap<Integer, Boolean> isSelectedMap;

    public VisitorAdapter(Context context, ArrayList<EquesVisitorInfo.RingsEntity> visitors, String name, boolean b) {
        isSelectedMap = new HashMap<Integer, Boolean>();
        this.visitors = visitors;
        this.context = context;
        this.name = name;
        this.b = b;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public boolean getisSelectedAt(int position) {
        //如果当前位置的key值为空，则表示该item未被选择过，返回false，否则返回true

        if (isSelectedMap.get(position) != null) {
            return isSelectedMap.get(position);
        } else {
            return false;
        }
    }

    public void setItemisSelectedMap(int position, boolean isSelectedMap) {
        this.isSelectedMap.put(position, isSelectedMap);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return visitors.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_eques_visitor_list, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.summary = (TextView) convertView.findViewById(R.id.summary);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (b) {
            isCheckBoxVisiable = CheckBox.VISIBLE;
        } else {
            isCheckBoxVisiable = View.GONE;
        }
        ringsEntity = visitors.get(position);
        long data = ringsEntity.getRingtime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String hms = formatter.format(data);
        holder.title.setText(name);
        holder.summary.setText(hms);
        holder.checkBox.setVisibility(isCheckBoxVisiable);
        holder.checkBox.setChecked(getisSelectedAt(position));
        return convertView;
    }

    public class ViewHolder {
        public ImageView iv;
        public TextView title;
        public TextView summary;
        public CheckBox checkBox;
    }
}
