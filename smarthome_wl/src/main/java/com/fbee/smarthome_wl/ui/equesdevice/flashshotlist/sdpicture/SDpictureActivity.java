package com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.sdpicture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.utils.ImageLoader;

public class SDpictureActivity extends BaseActivity {

    private ImageView ivSDpicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdpicture);
    }

    @Override
    protected void initView() {
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView title = (TextView) findViewById(R.id.title);
        ivSDpicture = (ImageView) findViewById(R.id.iv_sdpicture);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("图片详情");
    }

    @Override
    protected void initData() {
        String path = getIntent().getExtras().getString("path");
        ImageLoader.load(this, path, ivSDpicture);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
