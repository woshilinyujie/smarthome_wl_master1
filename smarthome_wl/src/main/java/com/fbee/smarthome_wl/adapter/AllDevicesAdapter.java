package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.zllctl.DeviceInfo;

import java.util.List;

/**
 * Created by wl on 2017/3/23.
 */

public class AllDevicesAdapter extends RecyclerView.Adapter<AllDevicesAdapter.MyViewHolder> {


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    private final List<EquesListInfo.bdylistEntity> bdylist;
    private final List<DeviceInfo> deviceInfos;
    private final Context mcontext;

    public AllDevicesAdapter(Context context, List<EquesListInfo.bdylistEntity> bdylist, List<DeviceInfo> deviceInfos) {
        this.mcontext = context;
        this.bdylist = bdylist;
        this.deviceInfos = deviceInfos;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mcontext).inflate(R.layout.item_scenario, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });
        }
        if (position == deviceInfos.size() + bdylist.size()) {
            holder.tv.setText("添加");
            return;
        }

        if (position < deviceInfos.size()) {
            holder.tv.setText(deviceInfos.get(position).getDeviceName());
        } else if(bdylist.size()>(position - deviceInfos.size())){
            if (bdylist.get(position - deviceInfos.size()).getNick()!=null){
                holder.tv.setText(bdylist.get(position - deviceInfos.size()).getNick());
            }else {
                holder.tv.setText(bdylist.get(position - deviceInfos.size()).getName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return deviceInfos.size() + bdylist.size() + 1;
    }

    class MyViewHolder extends ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_text_scenario);
        }
    }
}
