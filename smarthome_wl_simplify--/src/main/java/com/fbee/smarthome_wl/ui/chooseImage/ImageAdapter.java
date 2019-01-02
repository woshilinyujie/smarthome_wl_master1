package com.fbee.smarthome_wl.ui.chooseImage;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.fbee.smarthome_wl.R;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.ui.scenario
 * @anthor create by Zhaoli.Wang
 * @time 2017/6/9 16:19
 */
public class ImageAdapter extends BaseAdapter {
    private List<Integer>   mdatalsit;
    private Context mContext;
    private ViewHolder holder;
    public ArrayMap<Integer,Boolean>   map;
    public ImageAdapter(List<Integer> mdatalsit,Context context) {
        this.mdatalsit = mdatalsit;
        this.mContext = context;
        map  = new ArrayMap<>();
        if(null != mdatalsit){
            for (int i = 0; i < mdatalsit.size(); i++) {
                map.put(i,false);
            }
        }

    }

    public ArrayMap<Integer, Boolean> getMap() {
        return map;
    }

    @Override
    public int getCount() {
        return mdatalsit.size();
    }

    @Override
    public Object getItem(int position) {
        return mdatalsit.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid, null);
            holder=new ViewHolder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.id_item_image);
            holder.imageButton = (ImageButton) convertView.findViewById(R.id.id_item_select);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mImageView.setColorFilter(null);
        holder.mImageView.setImageResource(mdatalsit.get(position));

        if(map.get(position)){
            holder.imageButton.setImageResource(R.mipmap.pictures_selected);
            holder.mImageView.setColorFilter(Color.parseColor("#77000000"));
        }else{
            holder.imageButton.setImageResource(R.mipmap.picture_unselected);
            holder.mImageView.setColorFilter(null);
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(map.get(position)){
                    holder.imageButton.setImageResource(R.mipmap.picture_unselected);
                    map.put(position,false);

                }else{
                    holder.imageButton.setImageResource(R.mipmap.pictures_selected);
                    if(null != mdatalsit){
                        for (int i = 0; i < mdatalsit.size(); i++) {
                            if(i== position){
                                map.put(position,true);
                                continue;
                            }
                            map.put(i,false);
                        }
                    }


                }

                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    public static class ViewHolder{
        public ImageView mImageView;
        public ImageButton imageButton;
    }


}
