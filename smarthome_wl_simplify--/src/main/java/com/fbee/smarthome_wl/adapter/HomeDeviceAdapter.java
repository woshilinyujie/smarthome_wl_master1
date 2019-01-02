package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.constant.DeviceList;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.PreferencesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter.itemdecoration
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/24 9:25
 */
public class HomeDeviceAdapter  extends BaseRecylerAdapter<HomePageResponse.BodyBean.DeviceListBean> {
    public HomeDeviceAdapter(Context context, List<HomePageResponse.BodyBean.DeviceListBean> mDatas) {
        super(context, mDatas,  R.layout.item_scenario);
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position)  {
        holder.setText(R.id.tv_text_scenario,mDatas.get(position).getNote());
        TextView redText=holder.getTextView(R.id.btn_lock_record_hint);
        redText.setVisibility(View.GONE);
        int type = -1;
        try{
            type=Integer.parseInt(mDatas.get(position).getType());
        }catch (Exception e){
        }
        switch (type){
            //门锁
            case DeviceList.DEVICE_ID_DOOR_LOCK:
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.device_door_lock);
                String jsString = PreferencesUtils.getString(
                        MainActivity.DOORMESSAGECOUNT);
                // ===============如果jsonArray不为空，有消息的门锁，在设备分页处对应的设备显示红点
                try{
                    if (jsString != null) {
                        JSONArray jsonArrayGet = new JSONArray(jsString);
                        for (int i = 0; i < jsonArrayGet.length(); i++) {
                            JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
                            if (jsonObjectGet.getInt("uid") == Integer.valueOf(mDatas.get(position).getUuid())) {
                                int count = jsonObjectGet.getInt("messageCount");
                                if (count != 0) {
                                    redText.setVisibility(View.VISIBLE);
                                    redText.setText(String.valueOf(count));
                                } else {
                                    redText.setVisibility(View.GONE);
                                }
                                break;
                            }
                        }
                    } else {
                        redText.setVisibility(View.GONE);
                    }


                }catch(Exception e){
                }
                break;
            //色温灯
            case DeviceList.DEVICE_ID_COLOR_TEMP1:
            case DeviceList.DEVICE_ID_COLOR_TEMP2:
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.device_color_bulb);
                break;
            //开关插座
            case DeviceList.DEVICE_ID_SOCKET:
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.socket);
                break;
            //彩灯
            case DeviceList.DEVICE_ID_COLOR_PHILIPS:
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.device_color_bulb);
                break;
            //窗帘
            case DeviceList.DEVICE_ID_CURTAIN :
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.blind);
                break;
            //智能开关
            case DeviceList.DEVICE_ID_SWITCH:
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.mul_switch);
                break;
            //移康猫眼
            case 1001:
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.eques_monitor);
                break;
            default:
                //未知设备
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.default_device);
                break;

        }

    }



}
