package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 多选dialog
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/24 13:44
 */
public class DialogListChoose extends Dialog{
    private TextView tvName;
    private ListView lvWeek;
    private TextView tvLeftBtn;
    private TextView tvRightBtn;
    private Context mContext;
    private List<String> mDatas;
    private DialogListener listener;

    public DialogListChoose(Context context,DialogListener listener) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.listener = listener;
    }

    protected DialogListChoose(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public DialogListChoose(Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_week);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        tvName = (TextView) findViewById(R.id.tv_name);
        lvWeek = (ListView) findViewById(R.id.lv_week);
        tvLeftBtn = (TextView) findViewById(R.id.tv_left_btn);
        tvRightBtn = (TextView) findViewById(R.id.tv_right_btn);

        mDatas = new ArrayList<>();
        tvLeftBtn.setText("取消");
        tvRightBtn.setText("确定");
        tvLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                if(null != listener)
                listener.onLeftClick();
            }
        });

        tvRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != listener)
                listener.onRightClick();
                cancel();
            }
        });

    }


    public void setData(List<String> mDatas){
        this.mDatas =mDatas;
        MyListAdapter adapter = new MyListAdapter();
        lvWeek.setAdapter(adapter);

    }


    public interface DialogListener {
        void onLeftClick();

        void onRightClick();

        void OnItemCheckedListener(int postion,boolean checked);

    }


    public void  setTilte(String title){
        tvName.setText(title);
    }


    private class MyListAdapter extends BaseAdapter {


        public MyListAdapter() {
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_addordeleterecycler, null);
                holder.name = (TextView) convertView.findViewById(R.id.devicename_addordeleterecycler);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(mDatas.get(position));
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(null != listener)
                        listener.OnItemCheckedListener(position,isChecked);
                }
            });

            return convertView;
     }

        private  class ViewHolder {
            TextView name;
            CheckBox checkbox;
        }


    }
}
