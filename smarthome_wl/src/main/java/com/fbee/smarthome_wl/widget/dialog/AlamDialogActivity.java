package com.fbee.smarthome_wl.widget.dialog;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;

public class AlamDialogActivity extends Activity {
	private TextView Title;
	private TextView Msg;
	private String title;
	private String msg;
	private Button Button;
	private MediaPlayer mediaplayer;
	private boolean isAlarm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alam_dialog);
		title = getIntent().getStringExtra("name");
		isAlarm = getIntent().getBooleanExtra("isAlarm", false);
		msg = getIntent().getStringExtra("str");
		if (isAlarm) {
			mediaplayer = MediaPlayer.create(this, R.raw.jingchejingbaosheng);
			mediaplayer.setLooping(true);
			mediaplayer.start();
		}
		initView();
		initData();
	}

	private void initData() {
		Title.setText(title);
		Msg.setText(msg);
		Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					if(null != mediaplayer)
					mediaplayer.stop();
				}catch (Exception e){
				}
				finish();

			}
		});
	}

	private void initView() {
		Title = (TextView) findViewById(R.id.textView1);
		Msg = (TextView) findViewById(R.id.textView2);
		Button = (Button) findViewById(R.id.button1);
	}

	protected void onStop() {
		super.onStop();
		try{
			if (mediaplayer != null ) {
				mediaplayer.stop();
				mediaplayer.release();//释放资源
			}
		}catch (Exception e){
		}
	}
}
