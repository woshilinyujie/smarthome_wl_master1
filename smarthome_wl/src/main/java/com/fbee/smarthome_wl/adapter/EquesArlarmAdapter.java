package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.bean.EquesAlarmInfo;
import com.fbee.smarthome_wl.bean.SeleteEquesDeviceInfo;
import com.fbee.smarthome_wl.utils.ImageLoader;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by WLPC on 2016/12/14.
 */

public class EquesArlarmAdapter extends BaseAdapter {
    private final List<EquesAlarmInfo.AlarmsEntity> alarms;
    private final List<SeleteEquesDeviceInfo> seletes;
    private  boolean b;
    private List<URL> urls;
    private String devName;
    private HashMap<Integer, Boolean> isSelected;
    public int isCheckBoxVisiable;
    public static final int CHECKBOX_GONE = 2;
    private Context context;

    public EquesArlarmAdapter(Context context, String devName, List<EquesAlarmInfo.AlarmsEntity> alarms,
                              List<SeleteEquesDeviceInfo> seletes, boolean b, List<URL> urls) {
        isSelectedMap = new HashMap<Integer, Boolean>();
        this.b = b;
        this.seletes = seletes;
        this.alarms = alarms;
        this.context = context;
        this.devName = devName;
        this.urls = urls;

    }

    public void setB(boolean b) {
        this.b = b;
    }

    public void addAllData(List<EquesAlarmInfo.AlarmsEntity> alarms) {
        isSelectedMap = null;
        this.alarms.addAll(alarms);
        notifyDataSetChanged();
    }

    private HashMap<Integer, Boolean> isSelectedMap;

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
        return (alarms == null) ? 0 : alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        EquesArlarmAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_eques_visitor_list, null);
            holder = new EquesArlarmAdapter.ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.summary = (TextView) convertView.findViewById(R.id.summary);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.ivArrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
            holder.ivMessageIcon = (ImageView) convertView.findViewById(R.id.iv_message_icon);
            convertView.setTag(holder);
        } else {
            holder = (EquesArlarmAdapter.ViewHolder) convertView.getTag();
        }

        if (b) {
            isCheckBoxVisiable = CheckBox.VISIBLE;
        } else {
            isCheckBoxVisiable = View.GONE;
        }

        holder.ivArrow.setVisibility(View.INVISIBLE);
        holder.ivMessageIcon.setVisibility(View.GONE);
        holder.checkBox.setVisibility(isCheckBoxVisiable);
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy年MM月dd日    HH:mm:ss");
        EquesAlarmInfo.AlarmsEntity alarmsEntity = alarms.get(position);
        if (alarmsEntity != null) {
            Date curDate = new Date((alarmsEntity.getCreate() == 0) ? alarmsEntity.getTime() : alarmsEntity.getCreate());// 获取当前时间
            String str = formatter.format(curDate);
            holder.summary.setText(str);
            int tag = alarmsEntity.getType();
            if (tag == 3) {

                holder.title.setText(devName + "猫眼单拍");
                holder.ivArrow.setVisibility(View.VISIBLE);
                holder.ivMessageIcon.setVisibility(View.VISIBLE);
                holder.ivMessageIcon.setImageResource(R.mipmap.videolock_picture01);
            } else if (tag == 4) {
                holder.title.setText(devName + "猫眼多拍");
                holder.ivArrow.setVisibility(View.VISIBLE);
                holder.ivMessageIcon.setVisibility(View.VISIBLE);
                holder.ivMessageIcon.setImageResource(R.mipmap.videolock_picture01);
            } else if (tag == 5) {
                holder.title.setText(devName + "猫眼视频");
                holder.ivArrow.setVisibility(View.VISIBLE);
                holder.ivMessageIcon.setVisibility(View.VISIBLE);
                holder.ivMessageIcon.setImageResource(R.mipmap.videolock_camera);
            }
        }
        ImageLoader.load(Uri.parse(urls.get(position).toString()), holder.iv, R.mipmap.item_visitor, R.mipmap.item_visitor);
        holder.checkBox.setChecked(getisSelectedAt(position));

//            holder.iv.setImageBitmap(bitmaps.get(position));
//            final int myposition = position;

//            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(!buttonView.isPressed())return;
//                    onItemCheckedChangedListener.onItemCheckedChanged(buttonView, myposition, isChecked);
//                }
//            });
//            if(isCheckBoxVisiable==CHECKBOX_GONE){
//                holder.checkBox.setVisibility(View.GONE);
//            }else if(isCheckBoxVisiable==CHECKBOX_VISIBLE){
//                holder.checkBox.setVisibility(View.VISIBLE);
//                holder.checkBox.setChecked(isSelected.get(position));
//
//            }

        return convertView;
    }

    //
//    public interface OnItemCheckedChanged {
//        public void onItemCheckedChanged(CompoundButton view, int position, boolean isChecked);
//    }
//    private OnItemCheckedChanged onItemCheckedChangedListener;
//    public void setOnItemCheckedChangedListener(OnItemCheckedChanged onItemCheckedChangedListener) {
//        this.onItemCheckedChangedListener = onItemCheckedChangedListener;
//    }

    public class ViewHolder {
        public ImageView iv;
        public TextView title;
        public TextView summary;
        public CheckBox checkBox;
        private ImageView ivArrow;
        private ImageView ivMessageIcon;



    }

}
