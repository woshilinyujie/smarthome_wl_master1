package com.example.wl.WangLiPro_v1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.jwl.android.jwlandroidlib.bean.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wl on 2018/5/14.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.myHolder> implements View.OnClickListener {

    private final Context myContext;
    private final List<Device> myDevices;
    private OnItemClickLitener mOnItemClickLitener;

    public MyRecyclerAdapter(Context context, List<Device> devices) {
        this.myContext = context;
        this.myDevices = devices;
    }

    @Override
    public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(myContext).inflate(R.layout.item_popup, parent, false);
        if (mOnItemClickLitener != null)
            inflate.setOnClickListener(this);
        return new myHolder(inflate);
    }

    @Override
    public void onBindViewHolder(myHolder holder, int position) {
        holder.deviceName.setText(myDevices.get(position).getDevice().getDeviceName());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return myDevices.size();
    }

    class myHolder extends RecyclerView.ViewHolder {

        private final TextView deviceName;

        public myHolder(View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.tv_item_menu);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    /**
     * 该方法需要在setAdapter之前调用
     */
    public MyRecyclerAdapter setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
        return this;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        if (mOnItemClickLitener != null)
            mOnItemClickLitener.onItemClick(view, position);
    }
}
