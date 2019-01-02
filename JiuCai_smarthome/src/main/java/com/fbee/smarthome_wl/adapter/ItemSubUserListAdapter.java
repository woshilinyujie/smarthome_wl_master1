package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.response.LoginResult;

import java.util.List;


/**
 * Created by WLPC on 2017/4/19.
 */

public class ItemSubUserListAdapter extends BaseAdapter {
    private List<LoginResult.BodyBean.ChildUserListBean> mDataList;
    private LayoutInflater inflater;
    private  ViewHolder holder;
    public ItemSubUserListAdapter(Context context, List<LoginResult.BodyBean.ChildUserListBean> mDataList) {
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
            view=inflater.inflate(R.layout.item_sub_user_list, null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        LoginResult.BodyBean.ChildUserListBean bean= mDataList.get(i);
        if(bean!=null){
            String userName=bean.getUsername();
            holder.num.setText(userName);
            String remark=bean.getUser_alias();
            if (remark==null|remark.isEmpty()){
                holder.alias.setText(userName);
            }else {
                holder.alias.setText(remark);
            }
        }
        return view;
    }
    public class ViewHolder {
        public TextView num;
        public TextView alias;

        public ViewHolder(View view) {
            num= (TextView) view.findViewById(R.id.num_sub_user);
            alias= (TextView) view.findViewById(R.id.alias_sub_user);
        }
    }
}
