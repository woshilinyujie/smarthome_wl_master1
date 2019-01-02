package com.example.wl.WangLiPro_v1.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.bean.DiscoverDataResponse;

import java.util.List;

/**
 * Created by wl on 2018/6/1.
 */

public class FindAdapter extends RecyclerView.Adapter<FindAdapter.MyHolder> implements View.OnClickListener {

    private final Context context;
    private final List<DiscoverDataResponse.BodyBean.DiscoverListBean> deviceList;
    private OnItemClickLitener onItemClickLitener;
    private View inflate;

    public FindAdapter(Context context, List<DiscoverDataResponse.BodyBean.DiscoverListBean> datas) {
        this.context = context;
        this.deviceList = datas;
    }

    public FindAdapter setOnItemClickLitener(OnItemClickLitener onItemClickLitener) {
        this.onItemClickLitener = onItemClickLitener;
        return this;
    }

    @Override
    public FindAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflate = LayoutInflater.from(context).inflate(R.layout.item_find, parent, false);
        inflate.setOnClickListener(this);
        MyHolder myHolder = new MyHolder(inflate);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(FindAdapter.MyHolder holder, int position) {
        inflate.setTag(position);
        DiscoverDataResponse.BodyBean.DiscoverListBean item = deviceList.get(position);
        if (item.getImg_url() != null) {
            Glide.with(context)
                    .load(Uri.parse(item.getImg_url()))
                    .centerCrop()
                    .into(holder.imageFind);
        }
        if (item.getExplain() != null) {
            holder.textTind.setText(item.getExplain());
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (onItemClickLitener != null) {
            onItemClickLitener.onItemClick(v, position);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private final ImageView imageFind;
        private final TextView textTind;

        public MyHolder(View itemView) {
            super(itemView);
            imageFind = (ImageView) itemView.findViewById(R.id.image_find);
            textTind = (TextView) itemView.findViewById(R.id.text_find);
        }
    }
}
