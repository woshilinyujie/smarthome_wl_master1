package com.fbee.smarthome_wl.ui.equesdevice.alarmlist.alarm.alarmbitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;

import java.util.ArrayList;

public class JiucaiImageActivity extends BaseActivity {
    private ImageView image;
    private ImageView top_bt_back;
    private String SdPath;
    private String substring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jiucai_image);
    }

    @Override
    protected void initView() {
        SdPath = getIntent().getStringExtra("path");
        TextView title = (TextView) findViewById(R.id.title);
        substring = SdPath.substring(SdPath.lastIndexOf("/") + 1);
        substring = this.substring.substring(0, this.substring.length() - 4);
        title.setText(substring);
        image = (ImageView) findViewById(R.id.alamss);
        top_bt_back = (ImageView) findViewById(R.id.back);
        top_bt_back.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        Bitmap bitmap = BitmapFactory.decodeFile(SdPath);
        image.setImageBitmap(bitmap);
        top_bt_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
