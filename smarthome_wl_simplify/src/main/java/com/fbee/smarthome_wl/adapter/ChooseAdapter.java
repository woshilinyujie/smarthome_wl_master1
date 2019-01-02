package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

import java.util.HashMap;
import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/27 9:12
 */
public class ChooseAdapter extends BaseAdapter{

    private Context context;
    private List<String> userList;
    HashMap<String,Boolean> states=new HashMap<String,Boolean>();//用于记录每个RadioButton的状态，并保证只可选一个
    private OnWitchSelectListener listener;
    private String tag;
    public ChooseAdapter(Context context, List<String> userList,String tag)
    {
        this.context = context;
        this.userList= userList;
        this.tag=tag;
    }

    public void setListener(OnWitchSelectListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_radio, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        String info=userList.get(position);
        final RadioButton radio=(RadioButton) convertView.findViewById(R.id.radio_btn);
        holder.radioBtn = radio;

        holder.tvDevicename.setText(userList.get(position));
        if("设备".equals(tag)){
            holder.tvImage.setImageResource(R.mipmap.device_door_lock);
        }else if("场景".equals(tag)){

            switch (info){
                case "睡眠":
                    holder.tvImage.setImageResource(R.mipmap.sleep);
                    break;
                case "离家":
                    holder.tvImage.setImageResource(R.mipmap.runaway);
                    break;
                case "回家":
                    holder.tvImage.setImageResource(R.mipmap.gohome);
                    break;
                case "娱乐":
                    holder.tvImage.setImageResource(R.mipmap.scence_entertainment);
                    break;
                case "会客":
                    holder.tvImage.setImageResource(R.mipmap.scence_receive);
                    break;
                case "休息":
                    holder.tvImage.setImageResource(R.mipmap.scence_rest);
                    break;
                case "聚会":
                    holder.tvImage.setImageResource(R.mipmap.scence_gettogether);
                    break;
                default:
                    holder.tvImage.setImageResource(R.mipmap.default_scence);
                    break;

            }
        }

        //当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中
        holder.radioBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //重置，确保最多只有一项被选中
                for(String key:states.keySet()){
                    states.put(key, false);
                }
                states.put(String.valueOf(position), radio.isChecked());
                if(null != listener)
                listener.onSelectItem(position);
                notifyDataSetChanged();
            }
        });

        boolean res=false;
        if(states.get(String.valueOf(position)) == null || states.get(String.valueOf(position))== false){
            res=false;
            states.put(String.valueOf(position), false);
        }
        else
            res = true;

        holder.radioBtn.setChecked(res);

        return convertView;
    }

    static class ViewHolder {
        private ImageView tvImage;
        private TextView tvDevicename;
        private RadioButton radioBtn;

        public ViewHolder(View view) {
            tvImage = (ImageView) view.findViewById(R.id.tv_image);
            tvDevicename = (TextView)view. findViewById(R.id.tv_devicename);
            radioBtn = (RadioButton) view.findViewById(R.id.radio_btn);

        }


    }


    public interface OnWitchSelectListener{

        void onSelectItem(int position);

    }


}
