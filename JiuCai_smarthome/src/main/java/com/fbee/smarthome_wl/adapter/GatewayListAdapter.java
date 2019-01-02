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
 * Created by wl on 2017/4/20.
 */

public class GatewayListAdapter extends BaseAdapter {

    private final List<LoginResult.BodyBean.GatewayListBean> gatewayList;
    private final Context context;
    private String note;

    public GatewayListAdapter(Context context, List<LoginResult.BodyBean.GatewayListBean> gatewayList) {
        this.context = context;
        this.gatewayList = gatewayList;
    }

    @Override
    public int getCount() {
        return gatewayList.size();
    }

    @Override
    public Object getItem(int position) {
        return gatewayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gateway, null);
            holder.gatewayName = (TextView) convertView.findViewById(R.id.tv_gateway_name);
            holder.gatewayVersion = (TextView) convertView.findViewById(R.id.tv_gateway_version);
            holder.gatewayModel = (TextView) convertView.findViewById(R.id.tv_gateway_model);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        note = gatewayList.get(position).getNote();
        if (note == null || note.isEmpty()) {
            holder.gatewayName.setText(gatewayList.get(position).getUsername());
            note = gatewayList.get(position).getUsername();
        } else {
            holder.gatewayName.setText(note);
        }
        holder.gatewayVersion.setText(gatewayList.get(position).getVersion());
        holder.gatewayModel.setText(gatewayList.get(position).getUsername());
        return convertView;
    }

    public class ViewHolder {
        public TextView gatewayName;
        public TextView gatewayVersion;
        public TextView gatewayModel;
    }
}
