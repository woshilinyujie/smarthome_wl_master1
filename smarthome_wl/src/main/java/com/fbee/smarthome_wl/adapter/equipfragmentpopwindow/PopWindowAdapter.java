package com.fbee.smarthome_wl.adapter.equipfragmentpopwindow;

import android.content.Context;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;

import java.util.List;

/**
 * Created by WLPC on 2017/4/6.
 */

public class PopWindowAdapter extends BaseRecylerAdapter<String> {
    public PopWindowAdapter(Context context, List<String> mDatas) {
        super(context, mDatas, R.layout.item_popwindow);
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        holder.setText(R.id.text_popwindow,mDatas.get(position));
    }
}
