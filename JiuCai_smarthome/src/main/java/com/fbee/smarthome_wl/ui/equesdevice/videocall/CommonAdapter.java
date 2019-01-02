package com.fbee.smarthome_wl.ui.equesdevice.videocall;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

import java.util.List;

/**
 *
 * @descrption 通用适配器
 * @author
 * @date 2015-5-23
 * @param
 *
 */
public class CommonAdapter extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    /**
     * 实体bean容器
     */
    protected List<GvData> mDatas;
//    public CommonAdapter(Context context, List<GvData> datas) {
//        this.mContext = context;
//        this.mDatas = mDatas;
//    }

    public CommonAdapter(Context context, List<GvData> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
    }

    public List<GvData> getDatas() {
        return mDatas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public GvData getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_jiucai, null);

        TextView textView = (TextView) view.findViewById(R.id.tv);
        final GvData b = mDatas.get(position);
        textView.setText(b.text);
        Drawable topDrawable = mContext.getResources().getDrawable(b.img);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        textView.setCompoundDrawables(null, topDrawable, null, null);
        return view;
    }
    public static class GvData {
        public String text;
        public int img;
        public GvData(String text, int img) {
            this.text = text;
            this.img = img;
        }
    }
}
