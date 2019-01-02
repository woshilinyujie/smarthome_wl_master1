package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.view.ChoiceView;

/**
 * 单选dialog
 *
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/27 9:01
 */
public class DialogSingleChoose extends Dialog {
    private TextView tvTitleWifidialog;
    private ListView lvSingleList;
    private TextView tvLeftCancelBtnWifidialog;
    private TextView tvRightConfirmBtnWifidialog;

    private String[] datas;
    private Context context;
    private DialogListener listener;

    public DialogSingleChoose(Context context, DialogListener listener) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.listener = listener;
    }

    public DialogSingleChoose(Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_single_choose);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        tvTitleWifidialog = (TextView) findViewById(R.id.tv_title_wifidialog);
        lvSingleList = (ListView) findViewById(R.id.lv_single_list);
        tvLeftCancelBtnWifidialog = (TextView) findViewById(R.id.tv_left_cancel_btn_wifidialog);
        tvRightConfirmBtnWifidialog = (TextView) findViewById(R.id.tv_right_confirm_btn_wifidialog);

        tvLeftCancelBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                if (null != listener)
                    listener.onLeftClick();
            }
        });

        tvRightConfirmBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onRightClick();
                cancel();
            }
        });

        lvSingleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != listener)
                    listener.OnItemCheckedListener(position);
            }
        });
    }

    public void setData(String title, String[] mDatas) {
        tvTitleWifidialog.setText(title);
        ArrayAdapter adapter = new ArrayAdapter<String>(context,
                R.layout.item_eques_alarm_time, mDatas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ChoiceView view;
                if (convertView == null) {
                    view = new ChoiceView(context);
                } else {
                    view = (ChoiceView) convertView;
                }
                view.setText(getItem(position));
                return view;
            }
        };
        lvSingleList.setAdapter(adapter);
    }

    public interface DialogListener {
        void onLeftClick();

        void onRightClick();

        void OnItemCheckedListener(int postion);

    }

}
