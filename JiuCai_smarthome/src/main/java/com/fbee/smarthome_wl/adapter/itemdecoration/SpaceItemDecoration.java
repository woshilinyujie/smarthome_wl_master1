package com.fbee.smarthome_wl.adapter.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @class name：com.fbee.smarthome_wl.adapter.itemdecoration
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/28 17:59
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int space;

    private int line = 3;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.bottom = space;
        //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) %line==0) {
            outRect.left = 0;
        }
    }
}
