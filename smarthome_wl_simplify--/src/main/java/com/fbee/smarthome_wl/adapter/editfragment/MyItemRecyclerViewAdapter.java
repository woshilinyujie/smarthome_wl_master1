package com.fbee.smarthome_wl.adapter.editfragment;

import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.response.HomePageResponse;

import java.util.List;

/**
 * 常用设备adapter
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<HomePageResponse.BodyBean.DeviceListBean> mValues;

    public ArrayMap<String,Boolean>  map= new ArrayMap<String,Boolean>();

    public MyItemRecyclerViewAdapter( List<HomePageResponse.BodyBean.DeviceListBean> mValues) {
        this.mValues =mValues;
    }
    public ArrayMap<String,Boolean> getSelectMap(){
        return map;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equip_scenario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HomePageResponse.BodyBean.DeviceListBean itemBean= mValues.get(position);
        holder.mIdView.setText(itemBean.getNote());
        if(FactoryType.EQUES.equals(itemBean.getVendor_name())){
            holder.mIcomView.setImageResource(R.mipmap.eques_monitor);
        }else if(FactoryType.FBEE.equals(itemBean.getVendor_name())){
            switch (Integer.parseInt(itemBean.getType())){
                //门锁
                case DeviceList.DEVICE_ID_DOOR_LOCK:
                    holder.mIcomView.setImageResource(R.mipmap.device_door_lock);

                    break;
                //色温灯
                case DeviceList.DEVICE_ID_COLOR_TEMP1:
                case DeviceList.DEVICE_ID_COLOR_TEMP2:
                    holder.mIcomView.setImageResource(R.mipmap.device_color_bulb);
                    break;
                //开关插座
                case DeviceList.DEVICE_ID_SOCKET:
                    holder.mIcomView.setImageResource(R.mipmap.socket);
                    break;
                //彩灯
                case DeviceList.DEVICE_ID_COLOR_PHILIPS:
                    holder.mIcomView.setImageResource(R.mipmap.device_color_bulb);
                    break;
                //窗帘
                case DeviceList.DEVICE_ID_CURTAIN :
                    holder.mIcomView.setImageResource(R.mipmap.blind);
                    break;
                //智能开关
                case DeviceList.DEVICE_ID_SWITCH:
                    holder.mIcomView.setImageResource(R.mipmap.mul_switch);
                    break;
                default:
                    //未知设备
                    holder.mIcomView.setImageResource(R.mipmap.default_device);
                    break;
            }

        }
        if(map.get(itemBean.getUuid())){
            holder.checkBox.setBackgroundResource(R.mipmap.selected);
        }else {

            holder.checkBox.setBackgroundResource(R.mipmap.unselected);

        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map.get(itemBean.getUuid())){

                    map.put(itemBean.getUuid(),false);
                }else{
                    map.put(itemBean.getUuid(),true);
                }
                notifyDataSetChanged();
            }
        });
    }



       private boolean isSelceted(int position){
        try{
            HomePageResponse.BodyBean homebody = AppContext.getInstance().getmHomebody();
            if(homebody  != null){
                List<HomePageResponse.BodyBean.DeviceListBean> list = homebody.getDevice_list();
                if(list ==null || list.size()<=0){
                    return false;
                }
                for (int i =0; i<list.size() ;i++){
                    if(list.get(i).getUuid().equals(mValues.get(position).getUuid())){
                        return true;
                    }
                }
                return false;
            }else{
                return false;
            }

        }catch(Exception e){
            return false;
        }


    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final ImageView mIcomView;
        public final Button checkBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.devicename_addordeleterecycler);
            mIcomView = (ImageView) view.findViewById(R.id.icon_addordeleterecycler);
            checkBox=(Button) view.findViewById(R.id.checkBox);
        }

    }

}
