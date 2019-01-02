package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.zllctl.SenceInfo;

import java.util.List;

/**
 * 场景管理
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/13 19:38
 */
public class ScenaManagerAdapter extends BaseAdapter {
    private List<SenceInfo>  mDataList;
    private ViewHolder holder;
    private Context context;

    public ScenaManagerAdapter(List<SenceInfo> mDataList, Context context) {
        this.mDataList = mDataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mDataList==null ? 0: mDataList.size();
    }

    @Override
    public SenceInfo getItem(int position) {
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
        SenceInfo info= mDataList.get(position);
        holder.name.setText(info.getSenceName());

        switch (info.getSenceName()){
            case "睡眠":
                holder.tvImage.setImageResource(R.mipmap.sleep);
                break;
            case "离家":
                holder.tvImage.setImageResource(R.mipmap.runaway);
                break;
            case "回家":
                holder.tvImage.setImageResource(R.mipmap.gohome);
                break;
            case "娱乐":
                holder.tvImage.setImageResource(R.mipmap.scence_entertainment);
                break;
            case "会客":
                holder.tvImage.setImageResource(R.mipmap.scence_receive);
                break;
            case "休息":
                holder.tvImage.setImageResource(R.mipmap.scence_rest);
                break;
            case "聚会":
                holder.tvImage.setImageResource(R.mipmap.scence_gettogether);
                break;
            default:
                holder.tvImage.setImageResource(R.mipmap.default_scence);
                break;

        }
        return convertView;
    }

    public class ViewHolder {
        public TextView name;
        private ImageView tvImage;

        public ViewHolder(View view) {
            tvImage = (ImageView) view.findViewById(R.id.tv_image);
            name = (TextView) view.findViewById(R.id.tv_name);
        }



    }

}
