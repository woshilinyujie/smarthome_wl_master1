package com.fbee.smarthome_wl.adapter.simplyadapter;

import android.content.Context;
import android.net.Uri;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.response.DiscoverDataResponse;
import com.fbee.smarthome_wl.utils.ImageLoader;

import java.util.List;

/**
 * Created by WLPC on 2017/11/8.
 */

public class FindAdapter extends BaseRecylerAdapter<DiscoverDataResponse.BodyBean.DiscoverListBean> {


    public FindAdapter(Context context, List<DiscoverDataResponse.BodyBean.DiscoverListBean> mDatas) {
        super(context, mDatas, R.layout.item_find);
        this.context=context;
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        DiscoverDataResponse.BodyBean.DiscoverListBean item=mDatas.get(position);
        if(item.getImg_url()!=null){
            ImageLoader.load(context, Uri.parse(item.getImg_url()), holder.getImageView(R.id.image_find));
        }
        if(item.getExplain()!=null){
            holder.getTextView(R.id.text_find).setText(item.getExplain());
        }


    }

}
