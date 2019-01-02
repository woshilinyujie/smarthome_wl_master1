package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/15 16:49
 */
public class DeviceUserAdapter extends BaseAdapter {
    private Context context;
    private List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> datas;

    public DeviceUserAdapter(Context context, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device_user, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        if(null !=datas && datas.size()>0){
            holder.tvNumber.setText(datas.get(position).getId()+"号用户");
            String note = datas.get(position).getNote();
            if(!TextUtils.isEmpty(note)){
                holder.tvDevicename.setText(note);
            }else{
                holder.tvDevicename.setText(datas.get(position).getId()+"号用户");
            }

            String mlevel = datas.get(position).getLevel();
            if(null != mlevel){
                switch (mlevel){
                    case "adm":
                        holder.level.setText("管理员");
                        break;
                    case "usr":
                        holder.level.setText("普通用户");
                        break;
                    case "tmp":
                        holder.level.setText("临时用户");
                        break;
                    default:
                        holder.level.setText("未知");
                        break;
                }
            }


        }
        return convertView;
    }
     class ViewHolder {
        private TextView tvNumber;
        private TextView tvDevicename;
        private TextView level;

        public ViewHolder(View view) {
            tvNumber = (TextView) view.findViewById(R.id.user_num_user_manage);
            tvDevicename = (TextView)view. findViewById(R.id.user_name_user_manage);
            level = (TextView) view.findViewById(R.id.tv_user_level);
        }


    }


}
