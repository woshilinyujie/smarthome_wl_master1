package com.fbee.smarthome_wl.ui.videodoorlock.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.view.ChoiceView;

public class VLParametersettingActivity extends BaseActivity {
    private LinearLayout activityEquesAlarTime;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ListView lvAlarTime;
    private String[] datas = null;
    private ChoiceView view;
    private String resultStr;
    int pramatype;
    private String selcetdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_alar_time);
    }

    @Override
    protected void initView() {
        activityEquesAlarTime = (LinearLayout) findViewById(R.id.activity_eques_alar_time);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        lvAlarTime = (ListView) findViewById(R.id.lv_alar_time);
    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("完成");
        title.setText("视频锁配置");
        tvRightMenu.setOnClickListener(this);

        pramatype = getIntent().getIntExtra("prama", -1);
        selcetdata = getIntent().getStringExtra("data");
        switch (pramatype) {
            case VideoLockSettingActivity.ALARM:
                title.setText("报警数据类型");
                datas = new String[]{"pic(图片)", "av(视频)"};
                break;

            case VideoLockSettingActivity.LEVEL:
                title.setText("音量等级");
                datas = new String[]{"低", "中", "高"};
                break;

            case VideoLockSettingActivity.AUTH:
                title.setText("验证模式");
                datas = new String[]{"0(单人验证)", "1(双人验证)"};
                break;
            case VideoLockSettingActivity.SENSITIVE:
                title.setText("智能人体侦测灵敏度");
                datas = new String[]{"低", "中", "高"};
                break;
            case VideoLockSettingActivity.TIME:
                title.setText("智能人体侦测自动报警时间");
                datas = new String[]{"60", "90", "120"};
                break;

        }

        lvAlarTime.setAdapter(new ArrayAdapter<String>(this,
                R.layout.item_eques_alarm_time, datas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    view = new ChoiceView(VLParametersettingActivity.this);
                } else {
                    view = (ChoiceView) convertView;
                }
                view.setText(getItem(position));
                return view;
            }
        });
        lvAlarTime.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListviewItem();
        lvAlarTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ListView.INVALID_POSITION != position) {

                    switch (pramatype) {
                        case VideoLockSettingActivity.ALARM:
                            if(datas[position].contains("pic")){
                                resultStr="pic";
                            }else if(datas[position].contains("av")){
                                resultStr="av";
                            }
                            break;

                        case VideoLockSettingActivity.LEVEL:
                            resultStr = datas[position];

                            break;
                        case VideoLockSettingActivity.AUTH:
                            resultStr = datas[position].substring(0, 1);
                            break;
                        case VideoLockSettingActivity.SENSITIVE:
                            resultStr = datas[position];
                            break;
                        case VideoLockSettingActivity.TIME:
                            resultStr = datas[position].substring(0, datas[position].length());
                    }
                }
            }
        });


    }


    private void setListviewItem() {
        switch (pramatype) {
            case VideoLockSettingActivity.ALARM:
                if ("pic".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(0, true);
                } else if ("av".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(1, true);
                }
                break;

            case VideoLockSettingActivity.LEVEL:
                if ("低".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(0, true);
                } else if ("中".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(1, true);
                } else if ("高".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(2, true);
                }

                break;
            case VideoLockSettingActivity.AUTH:
                if ("0".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(0, true);
                } else if ("1".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(1, true);
                }
                break;
            case VideoLockSettingActivity.SENSITIVE:
                if ("低".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(0, true);
                } else if ("中".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(1, true);
                } else if ("高".equals(selcetdata)) {
                    lvAlarTime.setItemChecked(2, true);
                }
                break;
            case VideoLockSettingActivity.TIME:
                if (selcetdata.contains("60")) {
                    lvAlarTime.setItemChecked(0, true);
                } else if (selcetdata.contains("90")) {
                    lvAlarTime.setItemChecked(1, true);
                } else if (selcetdata.contains("120")) {
                    lvAlarTime.setItemChecked(2, true);
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                save();
                finish();
                break;

        }
    }

    private void save() {
        Intent intent = new Intent();
        intent.putExtra("resultstr", resultStr);
        switch (pramatype) {
            case VideoLockSettingActivity.ALARM:
                setResult(1001, intent);
                break;
            case VideoLockSettingActivity.LEVEL:
                setResult(1002, intent);
                break;
            case VideoLockSettingActivity.AUTH:
                int number = getIntent().getIntExtra("number", -1);
                if (number < 2 && resultStr != null && resultStr.equals("1")) {
                    ToastUtils.showShort("管理员人数不足两人，无法设置双人验证模式！");
                    return;
                }
                setResult(1003, intent);
                break;
            case VideoLockSettingActivity.SENSITIVE:
                setResult(1004, intent);
                break;
            case VideoLockSettingActivity.TIME:
                setResult(1005,intent);
                break;
        }

    }

}
