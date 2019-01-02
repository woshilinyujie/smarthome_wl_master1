package com.fbee.smarthome_wl.adapter.editfragment;

import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.response.HomePageResponse;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter.editfragment
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/5 17:48
 */
public class MyHomeScenarioAdapter extends RecyclerView.Adapter<MyHomeScenarioAdapter.ViewHolder>  {
    private final List< HomePageResponse.BodyBean.SceneListBean> mValues;
    private ArrayMap<String,Boolean> map;
    @Override
    public MyHomeScenarioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equip_scenario, parent, false);
        return new ViewHolder(view);
    }

    public ArrayMap<String,Boolean> getSelectMap(){
        return map;
    }
    public MyHomeScenarioAdapter(List< HomePageResponse.BodyBean.SceneListBean> mValues) {
        this.mValues = mValues;
        map = new ArrayMap<String,Boolean>();


    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        final HomePageResponse.BodyBean.SceneListBean itemBea=mValues.get(position);
        holder.mIdView.setText(itemBea.getNote());
        if (itemBea.getNote().equals("睡眠")) {
            holder.mIcomView.setImageResource(R.mipmap.sleep);
        } else if (itemBea.getNote().equals("离家")) {
            holder.mIcomView.setImageResource(R.mipmap.runaway);
        } else if (itemBea.getNote().equals("回家")) {
            holder.mIcomView.setImageResource(R.mipmap.gohome);
        } else {
            holder.mIcomView.setImageResource(R.mipmap.default_scence);
        }


        if( map.get(itemBea.getNote())){
            holder.checkBox.setBackgroundResource(R.mipmap.selected);
        }else {
            holder.checkBox.setBackgroundResource(R.mipmap.unselected);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map.get(itemBea.getNote())){
                    map.put(itemBea.getNote(),false);
                }else{
                    map.put(itemBea.getNote(),true);
                }
                notifyDataSetChanged();
            }
        });


    }


    private boolean isSelceted(int position){
        try{
            HomePageResponse.BodyBean homebody = AppContext.getInstance().getmHomebody();
            if(homebody  != null){
                List<HomePageResponse.BodyBean.SceneListBean> scenceList = homebody.getScene_list();
                if(scenceList ==null || scenceList.size()<=0){
                    return false;
                }
                for (int i =0; i<scenceList.size() ;i++){
                    if(scenceList.get(i).getNote().equals(mValues.get(position).getNote())){
                        return true;
                    }
                }
                return false;
            }else{
                return false;
            }

        }catch(Exception e){
            return false;
        }


    }


    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final ImageView mIcomView;
        public final Button checkBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.devicename_addordeleterecycler);
            mIcomView = (ImageView) view.findViewById(R.id.icon_addordeleterecycler);
            checkBox=(Button) view.findViewById(R.id.checkBox);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }

}
