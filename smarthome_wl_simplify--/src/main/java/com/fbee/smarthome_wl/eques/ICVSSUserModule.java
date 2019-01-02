package com.fbee.smarthome_wl.eques;

import com.eques.icvss.api.ICVSSInstanceCreator;
import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.core.iface.ICVSSRoleType;

public class ICVSSUserModule {
	
	private static final String TAG = "UserICVSSModule";
	
	private Object obj = new Object();
    
	private static ICVSSUserModule mUserICVSSModule;
	private ICVSSUserInstance mIcvssInstance;
	
	public  synchronized static ICVSSUserModule getInstance(ICVSSListener listener) {
		if (null == mUserICVSSModule) {
				mUserICVSSModule = new ICVSSUserModule(listener);
		}
		return mUserICVSSModule;
	}
	
	private ICVSSUserModule(ICVSSListener listener) {
		synchronized (ICVSSUserModule.class) {
			mIcvssInstance = ICVSSInstanceCreator.createUserInstance(ICVSSRoleType.CLIENT, listener);
		}
	}
	
	public ICVSSUserInstance getIcvss() {
		return mIcvssInstance;
	}
	
	public void closeIcvss(){
		synchronized (obj) {
			if (null != mUserICVSSModule) {
				mUserICVSSModule = null;
			}
			
			if (null != mIcvssInstance) {
				ICVSSInstanceCreator.close();
				mIcvssInstance = null;
				
			}
		}
	}

}
