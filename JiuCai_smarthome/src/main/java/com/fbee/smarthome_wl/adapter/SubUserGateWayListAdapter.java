package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.response.QuerySubUserInfoResponse;

import java.util.List;

/**
 * Created by WLPC on 2017/4/19.
 */

public class SubUserGateWayListAdapter extends BaseAdapter {
    List<QuerySubUserInfoResponse.BodyBean.GatewayListBean> mDataList;
    private LayoutInflater inflater;
    private ViewHolder holder;
    public SubUserGateWayListAdapter(Context context, List<QuerySubUserInfoResponse.BodyBean.GatewayListBean> datas) {
        this.mDataList = datas;
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

    public List<QuerySubUserInfoResponse.BodyBean.GatewayListBean> getDatas(){
            return mDataList;
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

        QuerySubUserInfoResponse.BodyBean.GatewayListBean bean=mDataList.get(i);

        if(bean!=null){
            /*List<LoginResult.BodyBean.GatewayListBean> gateWayList= AppContext.getInstance().getBodyBean().getGateway_list();
            if(gateWayList!=null&&gateWayList.size()>0){
                for (int j = 0; j <gateWayList.size() ; j++) {
                    if(bean.getUuid().equals(gateWayList.get(j).getUuid())){
                        if(gateWayList.get(j).getNote()==null||gateWayList.get(j).getNote()==""){
                            holder.gateWay.setText(gateWayList.get(j).getUsername());
                        }else {
                            holder.gateWay.setText(gateWayList.get(j).getNote());
                        }
                    }
                }
            }*/

            if(bean.getNote()==null||bean.getNote()==""){
                holder.gateWay.setText(bean.getUsername());
            }else {
                holder.gateWay.setText(bean.getNote());
            }
            if(bean.getAuthorization()!=null&&bean.getAuthorization()!=""){
                if(bean.getAuthorization().equals("admin")){
                    holder.quaanxian.setText("管理员");
                }else {
                    holder.quaanxian.setText("普通用户");
                }
            }
        }
        return view;
    }
    public class ViewHolder {
        public TextView gateWay;
        public TextView quaanxian;

        public ViewHolder(View view) {
            gateWay= (TextView) view.findViewById(R.id.num_sub_user);
            quaanxian= (TextView) view.findViewById(R.id.alias_sub_user);
        }
    }
}
