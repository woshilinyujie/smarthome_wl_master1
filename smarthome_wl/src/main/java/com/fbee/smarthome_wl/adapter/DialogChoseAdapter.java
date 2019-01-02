package com.fbee.smarthome_wl.adapter;

import android.content.Context;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;

import java.util.List;

/**
 * Created by WLPC on 2017/5/12.
 */

public class DialogChoseAdapter extends BaseRecylerAdapter<String> {
    public DialogChoseAdapter(Context context, List<String> mDatas) {
        super(context, mDatas, R.layout.item_dialog_chose);
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        holder.setText(R.id.tv_item_dialog_chose,mDatas.get(position));
    }

}
