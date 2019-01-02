package com.fbee.smarthome_wl.adapter.doorlocklog;

import android.content.Context;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.greendao.User;

import java.util.List;

/**
 * Created by WLPC on 2017/4/1.
 */

public class UserManagerAdapter extends BaseRecylerAdapter<User> {
    public UserManagerAdapter(Context context, List<User> mDatas) {
        super(context, mDatas, R.layout.item_user_manage);
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        if(mDatas!=null&&mDatas.size()>0){
            holder.setText(R.id.user_num_user_manage,mDatas.get(position).getUserid()+"号用户");
            if(mDatas.get(position).getUseralias()==null||mDatas.get(position).getUseralias().isEmpty()){
                holder.setText(R.id.user_name_user_manage,mDatas.get(position).getUserid()+"号用户");
                return;
            }
            holder.setText(R.id.user_name_user_manage,mDatas.get(position).getUseralias());
        }

    }
}
