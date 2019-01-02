package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.response.LoginResult;

import java.util.List;

/**
 * Created by WLPC on 2017/4/20.
 */

public class AddSubUserAdapter extends BaseAdapter {

    private List<LoginResult.BodyBean.GatewayListBean> mDataList;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private ArrayMap<Integer,String> map;
    public AddSubUserAdapter(Context context,List<LoginResult.BodyBean.GatewayListBean> mDatas) {
        this.mDataList = mDatas;
        inflater=LayoutInflater.from(context);
        map = new ArrayMap<Integer,String>();
        for (int i = 0; i <mDataList.size() ; i++) {
            map.put(i,null);
        }

    }

    @Override
    public int getCount() {
        return mDataList==null ? 0: mDataList.size();
    }
    public ArrayMap<Integer,String> getSelectMap(){
        return map;
    }
    public  void clearSelectMap(){
        map.clear();
        notifyDataSetChanged();
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(R.layout.item_add_sub_user, null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        LoginResult.BodyBean.GatewayListBean itemBean=mDataList.get(position);
        if(itemBean!=null){
            if(itemBean.getNote()!=null&&itemBean.getNote()!=""){
                holder.gatewayName.setText(itemBean.getNote());
            }else {
                holder.gatewayName.setText(itemBean.getUsername());
            }
        }

        if(map.get(position) ==null){
            holder.adminCheckb.setBackgroundResource(R.mipmap.unselected);
            holder.userCheckb.setBackgroundResource(R.mipmap.unselected);
        }else {
            switch (map.get(position)){
                case "admin":
                    holder.adminCheckb.setBackgroundResource(R.mipmap.selected);
                    holder.userCheckb.setBackgroundResource(R.mipmap.unselected);
                    break;
                case "user":
                    holder.userCheckb.setBackgroundResource(R.mipmap.selected);
                    holder.adminCheckb.setBackgroundResource(R.mipmap.unselected);
                    break;
            }
        }


        holder.adminCheckb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map.get(position) ==null || "user".equals(map.get(position))){
                    map.put(position,"admin");
                }else{
                    map.put(position,null);
                }
                notifyDataSetChanged();

            }
        });
        holder.userCheckb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map.get(position) ==null || "admin".equals(map.get(position))){
                    map.put(position,"user");
                }else{
                    map.put(position,null);
                }
                notifyDataSetChanged();
            }
        });

        return view;
    }
    public class ViewHolder {
        public TextView gatewayName;
        private Button userCheckb;
        private Button adminCheckb;


        public ViewHolder(View view) {
            gatewayName = (TextView) view.findViewById(R.id.gateway_name_item_add_subuser);
            userCheckb= (Button) view.findViewById(R.id.user_checkb_item_add_subuser);
            adminCheckb = (Button)view.findViewById(R.id.admin_checkb_item_add_subuser);

        }
    }
}
