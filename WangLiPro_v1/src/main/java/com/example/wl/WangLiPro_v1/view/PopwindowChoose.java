package com.example.wl.WangLiPro_v1.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.adapter.BaseRecylerAdapter;
import com.example.wl.WangLiPro_v1.adapter.FindAdapter;
import com.example.wl.WangLiPro_v1.adapter.MyRecylerViewHolder;

import java.util.List;

/**
 * 房子（网关）选择pop
 * @class name：com.fbee.smarthome_wl.widget.pop
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/27 16:18
 */
public class PopwindowChoose extends PopupWindow{
    private final boolean isshow;
    Context mContext;
    private LayoutInflater mInflater;
    public View mContentView;
    public PopAdapter adapter;
    public PopwindowChoose(Context context, List<Menu> menus, BaseRecylerAdapter.OnItemClickLitener listener, boolean isshow) {
        super(context);
        this.isshow = isshow;
        this.mContext=context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = mInflater.inflate(R.layout.popup_chosse,null);

        //设置View
        setContentView(mContentView);
        //设置宽与高
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        /**
         * 设置背景只有设置了这个才可以点击外边和BACK消失
         */
        setBackgroundDrawable(new ColorDrawable());

        /**
         * 设置可以获取集点
         */
        setFocusable(true);

        /**
         * 设置点击外边可以消失
         */
        setOutsideTouchable(true);

        /**
         *设置可以触摸
         */
        setTouchable(true);


        /**
         * 设置点击外部可以消失
         */

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                /**
                 * 判断是不是点击了外部
                 */
                if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
                    return true;
                }
                //不是点击外部
                return false;
            }
        });

        /**
         * 初始化View与监听器
         */
        initView(mContentView,menus,listener);
    }

    private void initView(View mContentView, List<Menu> menus,BaseRecylerAdapter.OnItemClickLitener listener) {
        RecyclerView  rvMenuList = (RecyclerView) mContentView.findViewById(R.id.rv_menu_list);
        adapter = new PopAdapter(mContext, menus);
        adapter.setOnItemClickLitener(listener);
        LinearLayoutManager lm = new LinearLayoutManager(mContext);
        rvMenuList.setLayoutManager(lm);
        rvMenuList.setAdapter(adapter);
    }

    public void changeData(List<Menu> mDatas){
        adapter.clearItems();
        adapter.addAllItem(mDatas);
    }

    public void notifydata(){
        adapter.notifyDataSetChanged();
    }


    public static class Menu {
        public int menuResId;
        public String menuText;
        private String uid;

        public Menu(int menuResId, String menuText) {
            this.menuResId = menuResId;
            this.menuText = menuText;
        }

        public Menu(int menuResId, String menuText, String uid) {
            this.menuResId = menuResId;
            this.menuText = menuText;
            this.uid = uid;
        }

        public String getUid() {
            return uid;
        }

        public String getMenuText() {
            return menuText;
        }

        public void setMenuText(String menuText) {
            this.menuText = menuText;
        }
    }

    private class PopAdapter extends BaseRecylerAdapter<Menu> {

        public PopAdapter(Context context, List<Menu> mDatas) {
            super(context, mDatas, R.layout.item_popup);
        }

        @Override
        public void convert(MyRecylerViewHolder holder, int position) {
            holder.setText(R.id.tv_item_menu,mDatas.get(position).menuText);
//            holder.setImageResource(R.id.iv_item_menu,mDatas.get(position).menuResId);
            holder.getImageView(R.id.iv_item_menu).setVisibility(View.GONE);
        }


    }

}
