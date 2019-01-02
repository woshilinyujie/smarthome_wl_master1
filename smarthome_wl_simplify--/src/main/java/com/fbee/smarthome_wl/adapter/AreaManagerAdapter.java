package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.zllctl.GroupInfo;

import java.util.List;

/**
 * Created by WLPC on 2017/4/14.
 */

public class AreaManagerAdapter extends BaseAdapter {
    private List<GroupInfo> mDataList;
    private AreaManagerAdapter.ViewHolder holder;
    private LayoutInflater inflater;

    public AreaManagerAdapter(Context context, List<GroupInfo> mDataList) {
        this.mDataList = mDataList;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDataList==null ? 0: mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(R.layout.item_scenario_manager, null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else {
            holder = (AreaManagerAdapter.ViewHolder) view.getTag();
        }
        GroupInfo info=mDataList.get(i);
        holder.tvName.setText(info.getGroupName());

        switch (info.getGroupName()){
            case "客厅":
                holder.tvImage.setImageResource(R.mipmap.living_room);
                break;
            case "主卧":
                holder.tvImage.setImageResource(R.mipmap.living_room);
                break;
            case "次卧":
                holder.tvImage.setImageResource(R.mipmap.second_bedroom);
                break;
            case "阁楼":
                holder.tvImage.setImageResource(R.mipmap.area_cockloft);
                break;
            case "保姆房":
                holder.tvImage.setImageResource(R.mipmap.area_nanny_room);
                break;
            case "卫生间":
                holder.tvImage.setImageResource(R.mipmap.area_tolet);
                break;
            case "地下室":
                holder.tvImage.setImageResource(R.mipmap.area_undercroft);
                break;
            default:
                holder.tvImage.setImageResource(R.mipmap.default_area);
                break;
        }
        return view;
    }

    public class ViewHolder {
        private ImageView tvImage;
        private TextView tvName;

        public ViewHolder(View view) {
            tvImage = (ImageView) view.findViewById(R.id.tv_image);
            tvName = (TextView)view. findViewById(R.id.tv_name);
        }



    }
}
