package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.zllctl.TaskInfo;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/27 14:09
 */
public class TaskAdapter extends BaseAdapter{



    private List<TaskInfo> mDataList;
    private ViewHolder holder;
    private Context context;

    public TaskAdapter(List<TaskInfo> mDataList, Context context) {
        this.mDataList = mDataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mDataList==null ? 0: mDataList.size();
    }

    @Override
    public TaskInfo getItem(int position) {
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
        TaskInfo info= mDataList.get(position);
        holder.tvImage.setVisibility(View.GONE);
        holder.tvName.setText(info.getTaskName());
        //holder.tvImage.setImageResource(R.mipmap.menu_rule);
        return convertView;
    }

    public class ViewHolder {
        private ImageView tvImage;
        private TextView tvName;

        public ViewHolder(View view) {
            tvImage = (ImageView)view.findViewById(R.id.tv_image);
            tvName = (TextView) view.findViewById(R.id.tv_name);
        }



    }
}
