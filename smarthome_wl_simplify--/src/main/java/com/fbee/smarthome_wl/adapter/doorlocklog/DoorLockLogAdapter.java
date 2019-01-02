package com.fbee.smarthome_wl.adapter.doorlocklog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.greendao.Doorlockrecord;

import java.util.List;

/**
 * Created by WLPC on 2017/3/31.
 */

public class DoorLockLogAdapter extends BaseRecylerAdapter<Doorlockrecord> {
    public DoorLockLogAdapter(Context context, List<Doorlockrecord> mDatas) {
        super(context, mDatas, R.layout.item_doorlocklog);
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        Doorlockrecord itemData=mDatas.get(position);
        ImageView image=holder.getImageView(R.id.imv_item_doorlocklog);
        TextView nameText=holder.getTextView(R.id.name_text_item_doorlocklog);
        TextView valueText=holder.getTextView(R.id.value_text_item_doorlocklog);
        TextView time=holder.getTextView(R.id.tiem_text_doorlocklog);
        if(itemData!=null&&itemData.getMsg()!=null&&!itemData.getMsg().isEmpty()&&itemData.getTime()!=null){
            if(itemData.getMsg().contains("-")){
                int index=itemData.getMsg().indexOf("-");
                String name=itemData.getMsg().substring(0,index);
                String value=itemData.getMsg().substring(index+1,itemData.getMsg().length());
                nameText.setText(name);
                valueText.setText(value);
                time.setText(itemData.getTime());
                if(valueText.getVisibility()== View.GONE){
                    valueText.setVisibility(View.VISIBLE);
                }
                if(value.contains("报警")||value.contains("非法")||value.contains("胁迫")){
                    image.setImageResource(R.mipmap.abnomal);
                    nameText.setTextColor(context.getResources().getColor(R.color.google_red));

                    valueText.setTextColor(context.getResources().getColor(R.color.google_red));
                    time.setTextColor(context.getResources().getColor(R.color.google_red));
                }else{
                    image.setImageResource(R.mipmap.nomal);
                    nameText.setTextColor(context.getResources().getColor(R.color.black));
                    valueText.setTextColor(context.getResources().getColor(R.color.black));
                    time.setTextColor(context.getResources().getColor(R.color.line_horizontal_smart));
                }
            }else{
                if(valueText.getVisibility()== View.VISIBLE){
                    valueText.setVisibility(View.GONE);
                }
                nameText.setText(itemData.getMsg());
                time.setText(itemData.getTime());
                if(itemData.getMsg().contains("报警")||itemData.getMsg().contains("非法")||itemData.getMsg().contains("胁迫")){
                    image.setImageResource(R.mipmap.abnomal);
                    nameText.setTextColor(context.getResources().getColor(R.color.google_red));
                    time.setTextColor(context.getResources().getColor(R.color.google_red));
                }else{
                    image.setImageResource(R.mipmap.nomal);
                    nameText.setTextColor(context.getResources().getColor(R.color.black));
                    time.setTextColor(context.getResources().getColor(R.color.line_horizontal_smart));
                }
            }


        }
    }


}
