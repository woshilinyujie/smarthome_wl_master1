package com.fbee.smarthome_wl.ui.videodoorlock.videolocknotice;

import android.content.Context;
import android.content.Intent;

import com.fbee.smarthome_wl.request.videolockreq.DeviceAlarmRequest;
import com.fbee.smarthome_wl.ui.videodoorlock.DoorLockCallActivity;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.widget.dialog.AlamDialogActivity;

/**
 * Created by WLPC on 2017/12/7.
 */

public class VideoLockArlmNotify {
    private Context context;
    private DeviceAlarmRequest response;

    public VideoLockArlmNotify(Context context, DeviceAlarmRequest response) {
        this.context = context;
        this.response = response;
    }
    public void handleVideoLockArlmNotify(){
        if(response!=null){
            DeviceAlarmRequest.DataBean dataBean = response.getData();
            if(response.getUuid()!=null&&dataBean!=null&&dataBean.getType()!=null&&dataBean.getAlarm_type()!=null){
                switch (dataBean.getType()){
                    //视频锁
                    case "WonlyVideoLock":
                        String arlmType=null;
                        switch(dataBean.getAlarm_type()){
                            //门铃呼叫
                            case "call":
                                if(PreferencesUtils.getBoolean(response.getUuid())){
                                    Intent intent=new Intent(context,DoorLockCallActivity.class);
                                    intent.putExtra("deviceUuid",response.getUuid());
                                    context.startActivity(intent);
                                }
                                break;
                            //非法操作
                            case "noatmpt":
                                arlmType="非法操作";
                                break;
                            //假锁
                            case "fakelock":
                                arlmType="假锁报警";
                                break;
                            //门未关
                            case "nolock":
                                arlmType="门未关";
                                break;
                            //低电量
                            case "batt":
                                arlmType="低电量";
                                break;
                            //红外感应
                            case "infra":
                                arlmType="红外感应";
                                break;
                            //解除门未关
                            case "relock ":
                                arlmType="解除门未关";
                                break;
                            //解除假锁
                            case "rm_fake":
                                arlmType="解除假锁";
                                break;
                        }
                        if(arlmType!=null){
                            String note=dataBean.getNote();
                            if(note==null)note="视频锁";
                            Intent intent = new Intent(context,
                                    AlamDialogActivity.class);
                            intent.putExtra("isAlarm", true);
                            intent.putExtra("name", note);
                            intent.putExtra("str", arlmType);
                            context.startActivity(intent);
                        }
                    break;
                    //猫眼
                    case "WonlySmartEye":
                        break;
                    //晾霸
                    case "WonlySmartAirer":
                        break;
                }
            }
        }
    }
}
