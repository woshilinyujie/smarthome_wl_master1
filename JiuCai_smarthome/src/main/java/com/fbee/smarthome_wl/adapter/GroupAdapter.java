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
import com.fbee.zllctl.GroupInfo;

import java.util.List;

/**
 * Created by WLPC on 2017/3/23.
 */

public class GroupAdapter extends BaseRecylerAdapter<GroupInfo> {

    private String groupName;

    public GroupAdapter(Context context, List<GroupInfo> mDatas) {
        super(context, mDatas, R.layout.item_scenario);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void convert(MyRecylerViewHolder holder, int position) {
        groupName = mDatas.get(position).getGroupName();
        holder.setText(R.id.tv_text_scenario, groupName);

        String account = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
        LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+account);

        if(null !=gw){
           String gateway = gw.getUsername();
           Icon icon= IconDbUtil.getIns().getIcon(account,gateway,IconDbUtil.AREA,groupName);
           if(null != icon){
               if(icon.getImageres() !=null && icon.getImageres()>0){
                   holder.setImageResource(R.id.iv_icon_scenario, icon.getImageres());
                   return;
               }else if(!TextUtils.isEmpty(icon.getImageurl())){
                   ImageLoader.loadCropCircle(context, icon.getImageurl(), holder.getImageView(R.id.iv_icon_scenario), R.mipmap.default_scence);
                   return;
               }
           }
       }
        switch (groupName){
            case "客厅":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.living_room);
                break;
            case "主卧":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.master_bedroom);
                break;
            case "次卧":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.second_bedroom);
                break;
            case "阁楼":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.area_cockloft);
                break;
            case "保姆房":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.area_nanny_room);
                break;
            case "卫生间":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.area_tolet);
                break;
            case "地下室":
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.area_undercroft);
                break;
            default:
                holder.setImageResource(R.id.iv_icon_scenario, R.mipmap.default_area);
                break;
        }

    }


}
