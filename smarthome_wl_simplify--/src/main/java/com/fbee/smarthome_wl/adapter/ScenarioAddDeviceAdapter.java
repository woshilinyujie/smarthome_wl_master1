package com.fbee.smarthome_wl.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.zllctl.DeviceInfo;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/11 11:35
 */
public class ScenarioAddDeviceAdapter extends RecyclerView.Adapter<ScenarioAddDeviceAdapter.ViewHolder> {
    private final List<DeviceInfo> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ScenarioAddDeviceAdapter(List<DeviceInfo> mValues, OnListFragmentInteractionListener mListener) {
        this.mValues = mValues;
        this.mListener = mListener;
    }

    @Override
    public ScenarioAddDeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_addordeleterecycler, parent, false);
        return new ScenarioAddDeviceAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.mIdView.setText(mValues.get(position).getDeviceName());
        switch (mValues.get(position).getDeviceId()){

            //色温灯
            case DeviceList.DEVICE_ID_COLOR_TEMP1:
            case DeviceList.DEVICE_ID_COLOR_TEMP2:
                holder.mIcomView.setImageResource(R.mipmap.device_color_bulb);
                break;
            //插座
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
            //开关
            case  DeviceList.DEVICE_ID_SWITCH:
                //未知设备
                holder.mIcomView.setImageResource(R.mipmap.mul_switch);
                break;
            default:
                //未知设备
                holder.mIcomView.setImageResource(R.mipmap.default_device);
                break;


        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(compoundButton,b,position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final ImageView mIcomView;
        public final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.devicename_addordeleterecycler);
            mIcomView = (ImageView) view.findViewById(R.id.icon_addordeleterecycler);
            checkBox=(CheckBox) view.findViewById(R.id.checkBox);
        }

    }
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(CompoundButton compoundButton, boolean b, int position);
    }
}
