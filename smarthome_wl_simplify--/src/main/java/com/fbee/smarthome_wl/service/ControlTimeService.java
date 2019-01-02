package com.fbee.smarthome_wl.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.ControlTimeEvent;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.RxBus;

import static com.fbee.smarthome_wl.common.AppContext.timerMap;

public class ControlTimeService extends Service {

//	public static  int time;
	public Thread serviceThread;
	//计时器
    private TimeCount timeCount;

    
	@Override
	public void onCreate() {
		super.onCreate();
		//timerMap = new ArrayMap<Integer, TimeCount>();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			int deviceUid=intent.getIntExtra("deviceUid", 0);
			timeCount = new TimeCount(50000,1000,deviceUid);
			timeCount.start();
			//timerMap.put(deviceUid, timeCount);
		}catch (Exception e){
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);

	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(null != timeCount){
			timeCount.cancel();
			timeCount =null;
		}
		if(timerMap != null){
			timerMap.clear();
			timerMap=null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	/**
	 * @Description: TODO 倒计时timer
	 * @author Created by ZhaoLi.Wang
	 * @date 2016-11-25 上午10:38:01 
	 *
	 */
	public class TimeCount extends CountDownTimer {
		//设备ID
		private int mDeviceUid;
		 //计时器是否正在运行
	    private boolean  isRuning;
		
        public TimeCount(long millisInFuture, long countDownInterval,int mDeviceUid) {
            super(millisInFuture, countDownInterval);
            this.mDeviceUid =  mDeviceUid;          
        }

        public void onFinish() {    
        	//倒计时结束调用 
        	isRuning = false;

			if(timerMap != null){
				timerMap.remove(mDeviceUid);	
				if(timerMap.size() == 0)
					stopSelf();
			}
			//RxBus.getInstance().post(new ControlTimeInfo(mDeviceUid));
        }

        public void onTick(long millisUntilFinished) {
        	//正在运行
			LogUtil.e("ControlTimeService",millisUntilFinished/1000+"");
			if(null!=AppContext.timerMap.get(mDeviceUid))
			RxBus.getInstance().post(new ControlTimeEvent((int)millisUntilFinished/1000,mDeviceUid));
        	isRuning =true;
        }

		public int getmDeviceUid() {
			return mDeviceUid;
		}

		public boolean isRuning() {
			return isRuning;
		}
    
        
    }
	
	

}
