package com.example.wl.WangLiPro_v1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.devices.DoorLockLogActivity;
import com.jwl.android.jwlandroidlib.bean.IncallLog;
import java.util.List;

/**
 * Created by wl on 2018/5/22.
 */

public class DoorLockLogAdapter extends RecyclerView.Adapter<DoorLockLogAdapter.MyRecylerViewHolder> {


    private final Context myContext;
    private final List<IncallLog> deviceList;
    private IncallLog incallLog;


    public DoorLockLogAdapter(DoorLockLogActivity context, List<IncallLog> incallLogs) {
        this.myContext = context;
        this.deviceList = incallLogs;
    }

    @Override
    public MyRecylerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(myContext).inflate(R.layout.item_doorlocklog, null);
        MyRecylerViewHolder myRecylerViewHolder = new MyRecylerViewHolder(inflate);
        return myRecylerViewHolder;
    }

    @Override
    public void onBindViewHolder(MyRecylerViewHolder holder, int position) {
        incallLog = deviceList.get(position);
        holder.nameText.setText(incallLog.getDeviceName());
        holder.time.setText(incallLog.getOpenLockTime());
    }


    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class MyRecylerViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView time;

        public MyRecylerViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.name_text_item_doorlocklog);
            time = (TextView) itemView.findViewById(R.id.tiem_text_doorlocklog);
        }
    }
}