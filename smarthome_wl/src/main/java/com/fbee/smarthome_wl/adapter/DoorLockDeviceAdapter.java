package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.ui.main.MainActivity;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.zllctl.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by WLPC on 2017/4/13.
 */

public class DoorLockDeviceAdapter extends BaseRecylerAdapter<DeviceInfo> {
    public DoorLockDeviceAdapter(Context context, List<DeviceInfo> mDatas) {
        super(context, mDatas, R.layout.item_equess_doorlock);
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        DeviceInfo info=mDatas.get(position);
        holder.setText(R.id.tv_text_equess_doorlock,info.getDeviceName());
        ImageView imageIcon=holder.getImageView(R.id.iv_icon_equess_doorlock);
        imageIcon.setImageResource(R.mipmap.device_door_lock);
//        TextView btn_lock_record_hint=holder.getTextView(R.id.text_lock_record_hint);
        //btn_lock_record_hint.setText(0);
       // btn_lock_record_hint.setVisibility(View.GONE);
        int state= info.getDeviceState();
        int status=info.getDeviceStatus();
        if(state>0||status>0){
            imageIcon.setAlpha(1.0f);
        }else{
            imageIcon.setAlpha(0.4f);
        }
//        try {
//            String jsString = PreferencesUtils.getString(
//                    MainActivity.DOORMESSAGECOUNT);
//
//            // ===============如果jsonArray不为空，有消息的门锁，在设备分页处对应的设备显示红点
//
//            if (jsString!=null) {
//                JSONArray jsonArrayGet = new JSONArray(jsString);
//                for (int i = 0; i < jsonArrayGet.length(); i++) {
//                    JSONObject jsonObjectGet = (JSONObject) jsonArrayGet.get(i);
//                    if (jsonObjectGet.getInt("uid") == info.getUId()) {
//                        int count = jsonObjectGet.getInt("messageCount");
//                        LogUtil.e("record",count+"");
//                        if (count != 0) {
//
//                            btn_lock_record_hint.setVisibility(View.VISIBLE);
//                            btn_lock_record_hint.setText(String.valueOf(count));
//                        } else {
//                            btn_lock_record_hint.setVisibility(View.GONE);
//                        }
//
//                        break;
//                    }
//                }
//            } else {
//                btn_lock_record_hint.setVisibility(View.GONE);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }
}
