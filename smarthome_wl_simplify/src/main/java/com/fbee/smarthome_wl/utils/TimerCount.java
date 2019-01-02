package com.fbee.smarthome_wl.utils;

import android.os.CountDownTimer;

import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.ControlTimeEvent;

import static com.fbee.smarthome_wl.common.AppContext.timerMap;

/**
 * Created by WLPC on 2017/6/22.
 */

public class TimerCount extends CountDownTimer {

    //设备ID
    private int mDeviceUid;
    //计时器是否正在运行
    private boolean  isRuning;

    public TimerCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }
    public TimerCount(long millisInFuture, long countDownInterval,int mDeviceUid) {
        super(millisInFuture, countDownInterval);
        this.mDeviceUid =  mDeviceUid;
    }
    @Override
    public void onTick(long l) {
        //正在运行
        LogUtil.e("ControlTimeService",l/1000+"");
        if(null!= AppContext.timerMap.get(mDeviceUid))
            RxBus.getInstance().post(new ControlTimeEvent((int)l/1000,mDeviceUid));
        isRuning =true;
    }
    public int getmDeviceUid() {
        return mDeviceUid;
    }

    public boolean isRuning() {
        return isRuning;
    }
    @Override
    public void onFinish() {
        //倒计时结束调用
        isRuning = false;

        if(timerMap != null){
            timerMap.remove(mDeviceUid);

        }
    }
}
