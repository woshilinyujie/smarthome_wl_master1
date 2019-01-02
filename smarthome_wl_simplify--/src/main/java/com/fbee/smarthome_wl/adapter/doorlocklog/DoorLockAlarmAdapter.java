package com.fbee.smarthome_wl.adapter.doorlocklog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.response.DoorLockAlarmResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wl on 2017/9/11.
 */

public class DoorLockAlarmAdapter extends BaseAdapter {

    private int type;
    private String deviceName;
    private ArrayList<DoorLockAlarmResponse.BodyEntity> bodyEntities;
    public int isCheckBoxVisiable;
    private Context context;
    private HashMap<Integer, Boolean> isSelectedMap;
    private String message;

    public DoorLockAlarmAdapter(Context context, ArrayList<DoorLockAlarmResponse.BodyEntity> bodyEntities, String deviceName, int type) {
        this.bodyEntities = bodyEntities;
        this.deviceName = deviceName;
        isSelectedMap = new HashMap<Integer, Boolean>();
        isCheckBoxVisiable = View.GONE;
        this.context = context;
        this.type = type;

    }

//    public void setB(boolean b) {
//        this.b = b;
//    }

    public void addAllData(ArrayList<DoorLockAlarmResponse.BodyEntity> alarms) {
        bodyEntities = alarms;
        setIsCheckBoxVisiable(false);
        notifyDataSetChanged();
        for (int i = 0; i < isSelectedMap.size(); i++) {
            isSelectedMap.put(i, false);
        }

    }

    public void setIsCheckBoxVisiable(boolean isshow) {
        if (isshow) {
            isCheckBoxVisiable = CheckBox.VISIBLE;
        } else {
            isCheckBoxVisiable = View.GONE;
        }
        notifyDataSetChanged();

    }

    public boolean getisSelectedAt(int position) {

        //如果当前位置的key值为空，则表示该item未被选择过，返回false，否则返回true

        if (isSelectedMap.get(position) != null) {
            return isSelectedMap.get(position);
        }
        return false;
    }

    public void setItemisSelectedMap(int position, boolean isSelectedMap) {
        this.isSelectedMap.put(position, isSelectedMap);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return bodyEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return bodyEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_eques_visitor_list, null);
            holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.summary = (TextView) convertView.findViewById(R.id.summary);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        message = new String();
        String alarm_message = bodyEntities.get(position).getAlarm_type();
        holder.title.setText(deviceName + SelectCharacter(alarm_message));
        holder.checkBox.setVisibility(isCheckBoxVisiable);
        if (type == 1) {
            holder.iv.setImageResource(R.mipmap.visitor);
        } else {
            holder.iv.setImageResource(R.mipmap.eques_visitor);
        }
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy年MM月dd日    HH:mm:ss");
        String format = formatter.format(Long.parseLong(bodyEntities.get(position).getTimestamp()) * 1000);
        holder.summary.setText(format);
        holder.checkBox.setChecked(getisSelectedAt(position));
        return convertView;
    }

    /**
     * noatmpt (非法操作)fakelock (假锁) nolock (门未关)
     * batt (低电量) infra (红外感应) call (门铃呼叫)
     * relock (解除门未关)  rm_fake (解除假锁)
     */
    private String SelectCharacter(String alarm_message) {
        switch (alarm_message) {
            case "noatmpt":
                message = "非法操作";
                break;
            case "fakelock":
                message = "假锁" + "报警";
                break;
            case "nolock":
                message = "门未关" + "报警";
                break;
            case "batt":
                message = "低电量" + "报警";
                break;
            case "infra":
                message = "红外感应" + "报警";
                break;
            case "call":
                message = "门铃呼叫";
                break;
            case "relock":
                message = "解除门未关";
                break;
            case "rm_fake":
                message = "解除假锁";
                break;
        }
        return message;
    }

    public class ViewHolder {
        public ImageView iv;
        public TextView title;
        public TextView summary;
        public CheckBox checkBox;
    }
}
