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
import com.fbee.smarthome_wl.greendao.User;

import java.util.List;

/**
 * Created by WLPC on 2017/4/24.
 */

public class DecviceAssociateUserAdapter extends BaseAdapter{

    private List<User> mDatas;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private ArrayMap<Integer,Boolean> map;
    public DecviceAssociateUserAdapter(Context context,List<User> mDatas) {
        this.mDatas = mDatas;
        inflater=LayoutInflater.from(context);
        map = new ArrayMap<Integer,Boolean>();
        /*for (int i = 0; i <mDatas.size() ; i++) {
            map.put(i,false);
        }*/
    }
    public void initMap(){
        for (int i = 0; i <mDatas.size() ; i++) {
            map.put(i,false);
        }
    }
    public ArrayMap<Integer,Boolean> getMap(){
        return map;
    }
    @Override
    public int getCount() {
        return mDatas==null ? 0: mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inflater.inflate(R.layout.item_device_associate_user,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        User itemUser=mDatas.get(i);
        if(itemUser!=null){
            if(itemUser.getUseralias()==null&&itemUser.getUseralias().isEmpty()){
                holder.userNumText.setText(itemUser.getUserid()+"号用户");
            }else{
                holder.userNumText.setText(itemUser.getUseralias());
            }
            if(map.size()>0){
                if(map.get(i)){
                    holder.buttonUser.setBackgroundResource(R.mipmap.selected);
                }else {
                    holder.buttonUser.setBackgroundResource(R.mipmap.unselected);
                }
            }
            holder.buttonUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(map.get(i)==false){
                        for (Integer key:map.keySet()) {
                            if(key==i){
                                map.put(i,true);
                            }else {
                                map.put(key,false);
                            }
                        }
                    }else {
                        for (Integer key:map.keySet()) {
                            map.put(key,false);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }
        return view;
    }
    private class ViewHolder{
        private TextView userNumText;
        private TextView userAlairsText;
        private Button buttonUser;

        public ViewHolder(View view) {
            userNumText = (TextView)view. findViewById(R.id.user_num_text);
            userAlairsText = (TextView) view.findViewById(R.id.user_alairs_text);
            buttonUser = (Button) view.findViewById(R.id.button_user);
        }



    }
}
