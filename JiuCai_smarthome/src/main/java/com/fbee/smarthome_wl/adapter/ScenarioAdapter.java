package com.fbee.smarthome_wl.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseRecylerAdapter;
import com.fbee.smarthome_wl.base.MyRecylerViewHolder;
import com.fbee.smarthome_wl.dbutils.IconDbUtil;
import com.fbee.smarthome_wl.greendao.Icon;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.zllctl.SenceInfo;

import java.util.List;

/**
 * 场景的adapter
 *
 * @class name：com.fbee.smarthome_wl.adapter
 * @anthor create by Zhaoli.Wang
 * @time 2017/2/17 9:35
 */
public class ScenarioAdapter extends BaseRecylerAdapter<SenceInfo> {


    private String senceName;
    private Context context;
    public ScenarioAdapter(Context context, List<SenceInfo> mDatas) {
        super(context, mDatas, R.layout.item_scenario);
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {

        senceName = mDatas.get(position).getSenceName();
        holder.setText(R.id.tv_text_scenario, senceName);

        String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);
        if(null == gw)
            return;
        String gateway = gw.getUsername();
        Icon icon= IconDbUtil.getIns().getIcon(account,gateway,IconDbUtil.SENCE,senceName);
        if(null != icon){
            if(icon.getImageres() !=null && icon.getImageres()>0){
                holder.setImageResource(R.id.iv_icon_scenario, icon.getImageres());
                return;
            }else if(!TextUtils.isEmpty(icon.getImageurl())){
                ImageLoader.loadCropCircle(context, icon.getImageurl(), holder.getImageView(R.id.iv_icon_scenario), R.mipmap.default_scence);
                return;
            }
        }

        switch (senceName){
            case "睡眠":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.sleep);
            break;
            case "离家":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.runaway);
                break;
            case "回家":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.gohome);
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
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.default_scence);
                break;

        }

    }


}
