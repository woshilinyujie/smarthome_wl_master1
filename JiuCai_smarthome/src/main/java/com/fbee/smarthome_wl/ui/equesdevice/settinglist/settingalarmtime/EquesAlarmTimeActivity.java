package com.fbee.smarthome_wl.ui.equesdevice.settinglist.settingalarmtime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.view.ChoiceView;

public class EquesAlarmTimeActivity extends BaseActivity {

    private ListView lvAlarTime;
    private ChoiceView view;
    private ImageView back;
    private TextView title;
    private TextView finsh;
    private String substring;
    private String[] times;
    private int sense_time;
    private String doorbellRing;
    private int index = -1;
    private String uid;
    private int capture_num;
    private int captureNum = -1;
    private String tvPirRingtone;
    public int ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_alar_time);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        lvAlarTime = (ListView) findViewById(R.id.lv_alar_time);
        title = (TextView) findViewById(R.id.title);
        finsh = (TextView) findViewById(R.id.tv_right_menu);
        back.setVisibility(View.VISIBLE);
        finsh.setVisibility(View.VISIBLE);
        title.setText("猫眼配置");
        finsh.setText("完成");
        finsh.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("Uid");
        sense_time = intent.getIntExtra("sense_time", -1);
        doorbellRing = intent.getStringExtra("doorbellRing");
        capture_num = intent.getIntExtra("capture_num", -1);
        tvPirRingtone = intent.getStringExtra("tvPirRingtone");
        if (sense_time != -1) {
            times = new String[]{
                    "1秒", "3秒", "5秒", "10秒", "15秒", "20秒"
            };

        } else if (doorbellRing != null) {
            times = new String[]{
                    "铃声一", "铃声二", "铃声三"
            };
        } else if (capture_num != -1) {
            times = new String[]{
                    "1张", "3张", "5张"
            };
        } else if (tvPirRingtone != null) {
            times = new String[]{
                    "你是谁呀", "嘟嘟声", "警报声", "尖啸声", "静音(默认)"
            };
        }

        lvAlarTime.setAdapter(new ArrayAdapter<String>(this,
                R.layout.item_eques_alarm_time, times) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    view = new ChoiceView(EquesAlarmTimeActivity.this);
                } else {
                    view = (ChoiceView) convertView;
                }
                view.setText(getItem(position));
                return view;
            }
        });
        lvAlarTime.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //设置条目选中
        setListviewItem();
        lvAlarTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ListView.INVALID_POSITION != position) {
                    String time = times[position];
                    if (sense_time != -1) {
                        substring = time.substring(0, time.length() - 1);
                    } else if (doorbellRing != null) {
                        index = position;
                    } else if (capture_num != -1) {
                        captureNum = position;
                    } else if (tvPirRingtone != null) {
                        ringtone = position;
                    }
                }
            }
        });
    }


    private void setListviewItem() {
        if (sense_time != -1) {
            switch (sense_time) {
                case 1:
                    lvAlarTime.setItemChecked(0, true);
                    break;
                case 3:
                    lvAlarTime.setItemChecked(1, true);
                    break;
                case 5:
                    lvAlarTime.setItemChecked(2, true);
                    break;
                case 10:
                    lvAlarTime.setItemChecked(3, true);
                    break;
                case 15:
                    lvAlarTime.setItemChecked(4, true);
                    break;
                case 20:
                    lvAlarTime.setItemChecked(5, true);
                    break;
            }
        } else if (doorbellRing != null) {
            if (doorbellRing.equals("铃声一")) {
                lvAlarTime.setItemChecked(0, true);
            } else if (doorbellRing.equals("铃声二")) {
                lvAlarTime.setItemChecked(1, true);
            } else if (doorbellRing.equals("铃声三")) {
                lvAlarTime.setItemChecked(2, true);
            }
        } else if (capture_num != -1) {
            switch (capture_num) {
                case 1:
                    lvAlarTime.setItemChecked(0, true);
                    break;
                case 3:
                    lvAlarTime.setItemChecked(1, true);
                    break;
                case 5:
                    lvAlarTime.setItemChecked(2, true);
                    break;
            }
        }else if (tvPirRingtone!=null){
            if (tvPirRingtone.equals("你是谁呀")) {
                lvAlarTime.setItemChecked(0, true);
            } else if (tvPirRingtone.equals("嘟嘟声")) {
                lvAlarTime.setItemChecked(1, true);
            } else if (tvPirRingtone.equals("警报声")) {
                lvAlarTime.setItemChecked(2, true);
            }else if (tvPirRingtone.equals("尖啸声")) {
                lvAlarTime.setItemChecked(3, true);
            }else if (tvPirRingtone.equals("静音(默认)")) {
                lvAlarTime.setItemChecked(4, true);
            }
        }

    }

    Intent intent = new Intent();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                if (substring != null) {
                    intent.putExtra("ALARMTIME", substring);
                    setResult(1, intent);
                } else if (index != -1) {
                    icvss.equesSetDoorbellRingtone(uid, index);
                    intent.putExtra("INDEX", index);
                    setResult(2, intent);
                } else if (captureNum != -1) {
                    intent.putExtra("CAPTURENUM", captureNum);
                    setResult(3, intent);
                }
                else if (ringtone != -1){
                    intent.putExtra("PIRRINGTONE", ringtone);
                    setResult(4, intent);
                }
                finish();
                break;
        }
    }
}
