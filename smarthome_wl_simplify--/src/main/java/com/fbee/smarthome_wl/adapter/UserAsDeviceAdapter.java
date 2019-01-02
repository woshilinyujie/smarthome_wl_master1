package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/18 9:08
 */
public class UserAsDeviceAdapter extends BaseAdapter {
    private List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> data;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private ArrayMap<Integer,Boolean> map;
    public UserAsDeviceAdapter(Context context, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> data) {
        this.data = data;
        inflater =LayoutInflater.from(context);
        map = new ArrayMap<Integer,Boolean>();
    }

    public void initMap(){
        for (int i = 0; i <data.size() ; i++) {
            map.put(i,false);
        }
    }
    public ArrayMap<Integer,Boolean> getMap(){
        return map;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_device_associate_user,null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        QueryDeviceUserResponse.BodyBean.DeviceUserListBean mUser = data.get(position);
        if(null != mUser ){
           if(!TextUtils.isEmpty(mUser.getNote())){
                holder.userNumText.setText(mUser.getNote());
           }else{
               holder.userNumText.setText(mUser.getId()+"号用户");
           }
            if(map.size()>0){
                if(map.get(position)){
                    holder.buttonUser.setBackgroundResource(R.mipmap.selected);
                }else {
                    holder.buttonUser.setBackgroundResource(R.mipmap.unselected);
                }
            }

            holder.buttonUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(map.get(position)==false){
                        for (Integer key:map.keySet()) {
                            if(key==position){
                                map.put(position,true);
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

        return convertView;
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
