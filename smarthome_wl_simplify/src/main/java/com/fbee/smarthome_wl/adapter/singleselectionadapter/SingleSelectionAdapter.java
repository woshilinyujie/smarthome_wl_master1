package com.fbee.smarthome_wl.adapter.singleselectionadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

import java.util.ArrayList;
import java.util.List;

public class SingleSelectionAdapter extends BaseAdapter {

    private Context mContext;
    private List<Person> mDatas = new ArrayList();
    private ViewHolder mViewHolder;

    public SingleSelectionAdapter(Context context) {
        mContext = context;
    }

    public void setDatas(List datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Person getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_eques_alarm_time, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.mCbCheckbox = (CheckBox) convertView.findViewById(R.id.checkedView);
            mViewHolder.mTvTitle = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }


        if (mDatas.get(position).isChecked()) {
            mViewHolder.mCbCheckbox.setChecked(true);
        } else {
            mViewHolder.mCbCheckbox.setChecked(false);
        }

        mViewHolder.mTvTitle.setText(mDatas.get(position).getTitle());
        return convertView;
    }

    static class ViewHolder {
        public TextView mTvTitle;
        public CheckBox mCbCheckbox;
    }
}
