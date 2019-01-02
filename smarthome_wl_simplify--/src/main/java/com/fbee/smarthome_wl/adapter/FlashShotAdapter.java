package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.EquesFlashShotActivity;
import com.fbee.smarthome_wl.utils.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wl on 2017/4/10.
 */

public class FlashShotAdapter extends BaseAdapter {
    private final ArrayList<String> imagePathFromSD;
    private final Context context;
    private final String name;
    private final HashMap<Integer, Boolean> isSelectedMap;
    int isCheckBoxVisiable;
    public FlashShotAdapter(Context context, ArrayList<String> imagePathFromSD,String name,boolean b) {
        this.name = name;
        this.context=context;
        this.imagePathFromSD=imagePathFromSD;
        isSelectedMap = new HashMap<Integer, Boolean>();
        if (b) {
            isCheckBoxVisiable = CheckBox.VISIBLE;
        } else {
            isCheckBoxVisiable = View.GONE;
        }
    }

    public boolean getisSelectedAt(int position) {

        //如果当前位置的key值为空，则表示该item未被选择过，返回false，否则返回true

        if (isSelectedMap.get(position) != null) {
            return isSelectedMap.get(position);
        }else {
            return false;
        }
    }

    public void setItemisSelectedMap(int position, boolean isSelectedMap) {
        this.isSelectedMap.put(position, isSelectedMap);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imagePathFromSD.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePathFromSD.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        String imagePath = imagePathFromSD.get(position);
        File file1 = new File(imagePath);
        String fileName = file1.getName();
        holder.title.setText(name);
        holder.summary.setText(fileName);
        ImageLoader.load(context,imagePathFromSD.get(position),holder.iv);
        holder.checkBox.setVisibility(isCheckBoxVisiable);
        holder.checkBox.setChecked(getisSelectedAt(position));
        return convertView;
    }

    public class ViewHolder {
        public ImageView iv;
        public TextView title;
        public TextView summary;
        public CheckBox checkBox;
    }
}
