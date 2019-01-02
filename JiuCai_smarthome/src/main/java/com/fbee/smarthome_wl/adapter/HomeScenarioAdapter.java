package com.fbee.smarthome_wl.adapter;

import android.content.Context;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.response.HomePageResponse;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/24 9:27
 */
public class HomeScenarioAdapter extends BaseRecylerAdapter<HomePageResponse.BodyBean.SceneListBean> {


    public HomeScenarioAdapter(Context context, List<HomePageResponse.BodyBean.SceneListBean> mDatas) {
        super(context, mDatas,  R.layout.item_scenario);
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        holder.setText(R.id.tv_text_scenario,mDatas.get(position).getNote());
        switch (mDatas.get(position).getNote()){
            case "睡眠":
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.sleep);
                break;
            case "回家":
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.gohome);
                break;
            case "离家":
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.runaway);
                break;
            case "娱乐":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.scence_entertainment);
                break;
            case "会客":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.scence_receive);
                break;
            case "休息":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.scence_rest);
                break;
            case "聚会":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.scence_gettogether);
                break;
            default:
                holder.setImageResource(R.id.iv_icon_scenario,R.mipmap.default_scence);
                break;
        }

    }
}
