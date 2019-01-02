package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.response.QueryGateWayInfoResponse;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.zllctl.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/30 9:51
 */
public class DeviceAdapter extends BaseAdapter {
    private final List<EquesListInfo.bdylistEntity> bdylist;
    private final List<DeviceInfo> deviceInfos;
    private final Context mcontext;
    private EquesListInfo.bdylistEntity bdylistEntity;
    private List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> wifi_list;
    private String onLinesBid;
    private String bid;

    public DeviceAdapter(Context context, List<EquesListInfo.bdylistEntity> bdylist, List<DeviceInfo> deviceInfos
    , List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> device_list) {
        this.mcontext = context;
        this.bdylist = bdylist;
        this.deviceInfos = deviceInfos;
        this.wifi_list = device_list;
    }

    @Override
    public int getCount() {
        return deviceInfos.size() + bdylist.size()+wifi_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.item_scenario, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_text_scenario);
            holder.iv_icon_scenario = (ImageView) convertView.findViewById(R.id.iv_icon_scenario);
            holder.btn_lock_record_hint = (TextView) convertView.findViewById(R.id.btn_lock_record_hint);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btn_lock_record_hint.setText(String.valueOf(0));
        holder.btn_lock_record_hint.setVisibility(View.GONE);
        if (position < deviceInfos.size()) {
            DeviceInfo info = deviceInfos.get(position);
            holder.name.setText(info.getDeviceName());
            int status=info.getDeviceStatus();
            if(status>0){
                holder.iv_icon_scenario.setAlpha(1.0f);
            }else {
                holder.iv_icon_scenario.setAlpha(0.4f);
            }
            switch (info.getDeviceId()) {
                //门锁
                case DeviceList.DEVICE_ID_DOOR_LOCK:
                    holder.iv_icon_scenario.setImageResource(R.mipmap.device_door_lock);

                    try {
                        String jsString = PreferencesUtils.getString(
                                MainActivity.DOORMESSAGECOUNT);

                        // ===============如果jsonArray不为空，有消息的门锁，在设备分页处对应的设备显示红点

                        if (jsString != null) {
                            JSONArray jsonArrayGet = new JSONArray(jsString);
                            for (int i = 0; i < jsonArrayGet.length(); i++) {
                                JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
                                if (jsonObjectGet.getInt("uid") == info.getUId()) {
                                    int count = jsonObjectGet.getInt("messageCount");
                                    if (count != 0) {
                                        holder.btn_lock_record_hint
                                                .setVisibility(View.VISIBLE);
                                        holder.btn_lock_record_hint.setText(String
                                                .valueOf(count));
                                    } else {
                                        holder.btn_lock_record_hint
                                                .setVisibility(View.GONE);
                                    }

                                    break;
                                }
                            }
                        } else {
                            holder.btn_lock_record_hint.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                //色温灯
                case DeviceList.DEVICE_ID_COLOR_TEMP1:
                case DeviceList.DEVICE_ID_COLOR_TEMP2:
                    holder.iv_icon_scenario.setImageResource(R.mipmap.device_color_bulb);
                    break;
                //开关插座
                case DeviceList.DEVICE_ID_SOCKET:
                    holder.iv_icon_scenario.setImageResource(R.mipmap.socket);
                    break;
                //彩灯
                case DeviceList.DEVICE_ID_COLOR_PHILIPS:
                    holder.iv_icon_scenario.setImageResource(R.mipmap.device_color_bulb);
                    break;
                //窗帘
                case DeviceList.DEVICE_ID_CURTAIN:
                    holder.iv_icon_scenario.setImageResource(R.mipmap.blind);
                    break;
                //智能开关
                case DeviceList.DEVICE_ID_SWITCH:
                    holder.iv_icon_scenario.setImageResource(R.mipmap.mul_switch);
                    break;
                default:
                    //未知设备
                    holder.iv_icon_scenario.setImageResource(R.mipmap.default_device);
                    break;


            }

        }
        /*else if(position == deviceInfos.size() + bdylist.size()){
            holder.name.setText("添加");
            holder.btn_lock_record_hint.setVisibility(View.GONE);
            holder.iv_icon_scenario.setImageResource(R.mipmap.device_add);
           // holder.iv_icon_scenario.setAlpha(1.0f);

        }*/
        else if (bdylist.size() > position - deviceInfos.size() && position - deviceInfos.size() >= 0 && bdylist.size() > 0) {
            holder.btn_lock_record_hint.setVisibility(View.GONE);
            if (bdylist.get(position - deviceInfos.size()).getNick() != null) {
                holder.name.setText(bdylist.get(position - deviceInfos.size()).getNick());
            } else {
                holder.name.setText(bdylist.get(position - deviceInfos.size()).getName());
            }
            List<EquesListInfo.OnlinesEntity> onlines = AppContext.getOnlines();
            if (onlines.size() > 0) {
                onLinesBid = bdylist.get(position - deviceInfos.size()).getBid();
                boolean tag=false;
                for (int i = 0; i < onlines.size(); i++) {
                    bid = onlines.get(i).getBid();
                    if (bid.equals(onLinesBid)) {
                        tag=true;
                        holder.iv_icon_scenario.setImageResource(R.mipmap.eques_monitor);
                        holder.iv_icon_scenario.setAlpha(1.0f);
                        break;
                    }
                }
                if(!tag){
                    holder.iv_icon_scenario.setImageResource(R.mipmap.eques_monitor);
                    holder.iv_icon_scenario.setAlpha(0.4f);
                }
            } else {
                holder.iv_icon_scenario.setImageResource(R.mipmap.eques_monitor);
                holder.iv_icon_scenario.setAlpha(0.4f);
            }

        }
        //视频锁
        else if(position >=deviceInfos.size() + bdylist.size() &&
                deviceInfos.size() + bdylist.size() +wifi_list.size()> position &&
                wifi_list.size() >0){
            String note=wifi_list.get(position-deviceInfos.size() - bdylist.size()).getNote();
            if(TextUtils.isEmpty(note)||"".equals(note)){
                holder.name.setText("视频锁");
            }else{
                holder.name.setText(note);
            }
            holder.iv_icon_scenario.setImageResource(R.mipmap.eques_monitor);
            String state = wifi_list.get(position - deviceInfos.size() - bdylist.size()).getState();
            if("online".equals(state)||"bind".equals(state)){
                holder.iv_icon_scenario.setAlpha(1.0f);
            }else{
                holder.iv_icon_scenario.setAlpha(0.4f);
            }

        }
        return convertView;
    }


    private class ViewHolder {
        TextView name;
        ImageView iv_icon_scenario;
        TextView btn_lock_record_hint;// 显示消息数量的小红点
    }


}
