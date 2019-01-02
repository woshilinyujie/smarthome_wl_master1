package com.fbee.smarthome_wl.ui.equesdevice.videocall;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.EquesConfig;
import com.fbee.smarthome_wl.event.EquesAlarmDialogEvent;
import com.fbee.smarthome_wl.event.EquesVideoCallEvent;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

import rx.functions.Action1;

public class EquesVideoCallActivity extends BaseActivity {

    private LinearLayout hangUp;
    private String sid;
    private String from;
    private LinearLayout videoCall;
    private ImageView headOrtrait;
    private JSONObject jpushData;
    private TextView equesName;
    private EquesListInfo.bdylistEntity bdylistEntity;
    private MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_video_call);
    }

    @Override
    protected void initView() {
        hangUp = (LinearLayout) findViewById(R.id.hang_up);
        videoCall = (LinearLayout) findViewById(R.id.video_call);
        headOrtrait = (ImageView) findViewById(R.id.head_ortrait);
        equesName = (TextView) findViewById(R.id.tv_eques_name);
    }


    @Override
    protected void initData() {
        mediaplayer = MediaPlayer.create(this, R.raw.alarm);
        mediaplayer.setLooping(true);
        mediaplayer.start();
        sid = getIntent().getExtras().getString(Method.ATTR_CALL_SID);
        from = getIntent().getExtras().getString(Method.ATTR_FROM);
        String name = getIntent().getExtras().getString("name");
        String json = getIntent().getExtras().getString("json");
        List<EquesListInfo.bdylistEntity> bdylist = (List<EquesListInfo.bdylistEntity>) PreferencesUtils.getObject(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME) + "JpushArlam");
        for (int i = 0; i < bdylist.size(); i++) {
            bdylistEntity = bdylist.get(i);
            if (bdylistEntity.getBid().equals(from)) {
                if (bdylistEntity.getNick() != null) {
                    equesName.setText(bdylist.get(i).getNick() + "来电");
                } else {
                    equesName.setText(bdylist.get(i).getName() + "来电");
                }
            }
        }
        try {
            if (from == null) {
                jpushData = new JSONObject(json);
                from = jpushData.optString("bid");
                name = jpushData.optString("name");
                for (int i = 0; i < bdylist.size(); i++) {
                    bdylistEntity = bdylist.get(i);
                    if (bdylistEntity.getBid().equals(from)) {
                        if (bdylistEntity.getNick() != null) {
                            equesName.setText(bdylist.get(i).getNick() + "来电");
                        } else {
                            equesName.setText(bdylist.get(i).getName() + "来电");
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (name != null) {
            icvss.equesLogin(this, EquesConfig.SERVER_ADDRESS, name, EquesConfig.APPKEY);
        }
        hangUp.setOnClickListener(this);
        videoCall.setOnClickListener(this);

        mSubscription = RxBus.getInstance().toObservable(EquesVideoCallEvent.class)
                .compose(TransformUtils.<EquesVideoCallEvent>defaultSchedulers())
                .subscribe(new Action1<EquesVideoCallEvent>() {
                    @Override
                    public void call(EquesVideoCallEvent event) {
                        String fid = event.getFid();
                        URL url = icvss.equesGetRingPicture(fid, from);
                        ImageLoader.load(EquesVideoCallActivity.this, Uri.parse(url.toString()), headOrtrait);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_up:
                icvss.equesCloseCall(sid);
                mediaplayer.pause();
                finish();
                break;
            case R.id.video_call:
                Bundle bundle1 = new Bundle();
                bundle1.putString(Method.ATTR_BUDDY_UID, from);
                skipAct(EquesCallActivity.class, bundle1);
                mediaplayer.pause();
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mediaplayer != null) {
                mediaplayer.stop();
                mediaplayer.release();//释放资源
            }
        } catch (Exception e) {

        }
    }
}
