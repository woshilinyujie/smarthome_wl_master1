package com.fbee.smarthome_wl.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.event.EquesAlarmDialogEvent;
import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.EquesAlarmActivity;

public class DialogActivity extends Activity {


    private TextView cancel;
    private TextView ok;
    private boolean istop;
    private String name;
    private TextView message;
    private String method;
    private String bid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        setContentView(R.layout.activity_dialog);
        istop = getIntent().getBooleanExtra("istop", false);
        name = getIntent().getStringExtra("name");
        bid = getIntent().getStringExtra(Method.ATTR_BUDDY_UID);
        initview();
    }

    private void initview() {
        cancel = (TextView) findViewById(R.id.tv_cancel);
        ok = (TextView) findViewById(R.id.tv_ok);
        message = (TextView) findViewById(R.id.tv_message);
        message.setText(name + "发来一条警报消息");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (istop) {
                    EquesAlarmDialogEvent equesAlarmDialogEvent = new EquesAlarmDialogEvent();
                    RxBus.getInstance().post(equesAlarmDialogEvent);
                    finish();
                } else {
                    Intent intent = new Intent(DialogActivity.this, EquesAlarmActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra(Method.ATTR_BUDDY_BID,bid);
                    startActivity(intent);
                    finish();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
