package com.fbee.smarthome_wl.anychatutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.anychat.config.ScreenInfo;
import com.bairuitech.anychat.config.SessionItem;
import com.bairuitech.anychat.config.UserItem;
import com.bairuitech.anychat.config.VideoCallContrlHandler;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.ui.equesdevice.videocall.JiucaiVideoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ControlCenter implements VideoCallContrlHandler {

	private static final String TAG = "ControlCenter";
	public static AnyChatCoreSDK anychat;
	private static ControlCenter mControlCenter = new ControlCenter();
	private MediaPlayer mMediaPlaer;
	private Vibrator mVibrator;
	public static SessionItem sessionItem;
	public static ScreenInfo mScreenInfo;
	public static Activity mContext;
	public static ArrayList<UserItem> mOnlineFriendItems;
	public static ArrayList<Integer> mOnlineFriendIds;

	public static int selfUserId;
	public static boolean bBack = false;// 程序是否在后台
	public static String selfUserName;
	public static String selfUserPassword;
	
	public static int mEventType = -1;
	
	public static String deviceVer=null;

	private ControlCenter() {
		initParams();
	}

	public static ControlCenter getControlCenter() {
		return mControlCenter;
	}

	private void initParams() {
		try{
			anychat =AnyChatCoreSDK.getInstance();
			mOnlineFriendItems = new ArrayList<UserItem>();
			mOnlineFriendIds = new ArrayList<Integer>();
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	/***
	 * 停止播放
	 */
	public void stopSessionMis() {
		/* Begin modify by shaunliu for phone can receive video request */
		if (mVibrator != null) {
			mVibrator.cancel();
		}
		/* End modify by shaunliu for phone can receive video request */
		if (mMediaPlaer == null)
			return;
		try {
			mMediaPlaer.pause();
			mMediaPlaer.stop();
			mMediaPlaer.release();
			mMediaPlaer = null;
		} catch (Exception e) {
			Log.i("media-stop", "er");
		}

	}

	/***
	 * 
	 * @param userId
	 *            用户id
	 * @param status
	 *            用户在线状态，1是上线，0是下线
	 */
	public void onUserOnlineStatusNotify(int userId, int status) {
		// TODO Auto-generated method stub
		String strMsg = "";
		UserItem userItem = getUserItemByUserId(userId);
		// Log.i(TAG, "userId:" + userId);
		if (!userItem.equals("")) {
			if (status == UserItem.USERSTATUS_OFFLINE) {
				// Log.i(TAG, "userId:"+userId);
				if (mOnlineFriendIds.indexOf(userId) >= 0) {
					mOnlineFriendItems.remove(userItem);
					mOnlineFriendIds.remove((Object) userId);
				}
				strMsg = userItem.getUserName() + "下线";
			} else {
				strMsg = userItem.getUserName() + "上线";
			}
			if (mContext != null)
				Toast.makeText(mContext, strMsg, Toast.LENGTH_SHORT).show();
		} else {
			Log.i(TAG, "userItem is null");
		}
	}

	public void realse() {
		Log.i(TAG, "137,realse");
		anychat = null;
		mOnlineFriendItems = null;
		mMediaPlaer = null;
		mScreenInfo = null;
		mContext = null;
		mControlCenter = null;
	}

	public void realseData() {
		mOnlineFriendItems.clear();
		mOnlineFriendIds.clear();
	}

	/***
	 * 发送呼叫事件
	 * 
	 * @param dwEventType
	 *            视频呼叫事件类型
	 * @param dwUserId
	 *            目标userid
	 * @param dwErrorCode
	 *            原因
	 * @param dwFlags
	 *            功能标志
	 * @param dwParam
	 *            自定义参数，传给对方
	 * @param szUserStr
	 *            自定义参数，传给对方
	 */
	public static void VideoCallContrl(int dwEventType, int dwUserId,
			int dwErrorCode, int dwFlags, int dwParam, String szUserStr) {
		try{
			mEventType = dwEventType;
			anychat.VideoCallControl(dwEventType, dwUserId, dwErrorCode, dwFlags,
					dwParam, szUserStr);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	@Override
	public void VideoCall_SessionRequest(int dwUserId, int dwFlags,
			int dwParam, String szUserStr) {
		// TODO Auto-generated method stub
		// 如果程序在后台，通知有呼叫请求
		Log.i(TAG, "bBack:" + bBack);
		if (bBack) {
			UserItem item = getUserItemByUserId(dwUserId);
			Bundle bundle = new Bundle();
			Log.i(TAG, "username:"+item.getUserName());
			if (item != null) {
				bundle.putString(
						"USERNAME",
						item.getUserName()
								+ mContext
										.getString(com.bairuitech.anychat.R.string.sessioning_reqite));
			} else {
				bundle.putString("USERNAME", "some one call you");
			}
			bundle.putInt("USERID", dwUserId);
//			BaseMethod.sendBroadCast(mContext,
//					BaseConst.ACTION_BACK_EQUESTSESSION, bundle);
		}
	}

	@Override
	public void VideoCall_SessionReply(int dwUserId, int dwErrorCode,
			int dwFlags, int dwParam, String szUserStr) {
		// TODO Auto-generated method stub
		String strMessage = null;
		switch (dwErrorCode) {
		case VideoCallContrlHandler.ERRORCODE_SESSION_BUSY:
			strMessage = mContext.getString(com.bairuitech.anychat.R.string.str_returncode_bussiness);
			break;
		case VideoCallContrlHandler.ERRORCODE_SESSION_REFUSE:
			strMessage = mContext
					.getString(com.bairuitech.anychat.R.string.str_returncode_requestrefuse);
			break;
		case VideoCallContrlHandler.ERRORCODE_SESSION_OFFLINE:
			strMessage = mContext.getString(com.bairuitech.anychat.R.string.str_returncode_offline);
			break;
		case VideoCallContrlHandler.ERRORCODE_SESSION_QUIT:
			strMessage = mContext
					.getString(com.bairuitech.anychat.R.string.str_returncode_requestcancel);
			break;
		case VideoCallContrlHandler.ERRORCODE_SESSION_TIMEOUT:
			strMessage = mContext.getString(com.bairuitech.anychat.R.string.str_returncode_timeout);
			break;
		case VideoCallContrlHandler.ERRORCODE_SESSION_DISCONNECT:
			strMessage = mContext.getString(com.bairuitech.anychat.R.string.str_returncode_disconnect);
			break;
		case VideoCallContrlHandler.ERRORCODE_SUCCESS:
			break;
		default:
			break;
		}
		if (strMessage != null) {
			Toast.makeText(mContext, strMessage, Toast.LENGTH_SHORT).show();
			stopSessionMis();
		}

	}

	@Override
	public void VideoCall_SessionStart(int dwUserId, int dwFlags, int dwParam,
			String szUserStr) {
		stopSessionMis();
		sessionItem = new SessionItem(dwFlags, selfUserId, dwUserId);
		sessionItem.setRoomId(dwParam);
		Intent intent = new Intent();
		intent.setClass(mContext,JiucaiVideoActivity.class);
		intent.putExtra("DeviceVer", deviceVer);
		intent.putExtra("dwUserId", dwUserId);
		mContext.startActivity(intent);
	}

	@Override
	public void VideoCall_SessionEnd(int dwUserId, int dwFlags, int dwParam,
			String szUserStr) {
		// TODO Auto-generated method stub
		deviceVer = null;
		sessionItem = null;
	}
	
	//通过用户名获取对象
	public UserItem getUserItemByName(String name){
		int size = mOnlineFriendItems.size();
		for (int i = 0; i < size; i++) {
			UserItem userItem = mOnlineFriendItems.get(i);

			if (userItem != null && userItem.getUserName().contentEquals(name)) {
				return userItem;
			}
		}

		return null;
	}

	/***
	 * 通过用户id获取用户对象
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 */
	public UserItem getUserItemByUserId(int userId) {

		int size = mOnlineFriendItems.size();
		if (userId != selfUserId) {
			Log.i(TAG, "userId != selfUserId");
		}
		for (int i = 0; i < size; i++) {
			UserItem userItem = mOnlineFriendItems.get(i);

			if (userItem != null && userItem.getUserId() == userId) {
				return userItem;
			}
		}

		return null;
	}

	public UserItem getUserItemByIndex(int index) {

		int size = mOnlineFriendItems.size() ;

		try {
			return mOnlineFriendItems.get(index);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void initFriendDatas(int isInit){
		UserItem userItem;
		List<HashMap<String, String>> list = AppContext.deviceList;
		mOnlineFriendItems.clear();
		mOnlineFriendIds.clear();
		for (int j = 0; j < list.size(); j++) {
			//获取好友在线状态
			userItem = new UserItem();
			userItem.setUserId(Integer.valueOf(list.get(j).get("device_anychat_id")));
			if (isInit==1){
				userItem.setIsOnline(2);
			}else if(isInit==2){
				userItem.setIsOnline(1);
			}else{
				userItem.setIsOnline(0);
			}
			userItem.setUserNameOther(list.get(j).get("device_name_other"));
			userItem.setUserName(list.get(j).get("device_name"));
			userItem.setDeviceVer(list.get(j).get("device_version"));
			mOnlineFriendItems.add(userItem);
			mOnlineFriendIds.add(Integer.valueOf(list.get(j).get("device_anychat_id")));//無意義
		}
	}
	
	/**
	 * 更新指定id好友的状态
	 * @param id
	 */
	public void updateFriendStatus(int id, int status){
		for (int i=0; i<mOnlineFriendIds.size(); i++){
			if (id == mOnlineFriendIds.get(i)){
				mOnlineFriendItems.get(i).setIsOnline(status);
				UserItem userItem = mOnlineFriendItems.get(i);
				for (int j=i; j>0; j--){
					mOnlineFriendItems.remove(j);
					mOnlineFriendItems.add(j, mOnlineFriendItems.get(j-1));
				}
				mOnlineFriendItems.remove(0);
				mOnlineFriendItems.add(0, userItem);
				break;
			}
		}
	}

	/***
	 * 获取好友数据
	 */
	public void getOnlineFriendDatas() {
		try{
			mOnlineFriendItems.clear();
			mOnlineFriendIds.clear();
			// 获取本地ip
			String ip = anychat.QueryUserStateString(-1,
					AnyChatDefine.BRAC_USERSTATE_LOCALIP);
			UserItem userItem = new UserItem(selfUserId, selfUserName, ip, "");
			Log.i(TAG, "selfUserId:" + selfUserId);
			Log.i(TAG, "selfUserName:" + selfUserName);
			Log.i(TAG, "ip:" + ip);
			// 获取用户好友userid列表
			int[] friendUserIds = anychat.GetUserFriends();
			int friendUserId = 0;
			List<HashMap<String, String>> list = AppContext.deviceList;
			for (int j = 0; j < list.size(); j++) {
				//获取好友在线状态
				int anychatid = Integer.valueOf(list.get(j).get("device_anychat_id"));
				int onlineStatus = anychat.GetFriendStatus(anychatid);
				userItem = new UserItem();
				userItem.setUserId(Integer.valueOf(list.get(j).get("device_anychat_id")));
				userItem.setIsOnline(onlineStatus);
				userItem.setUserNameOther(list.get(j).get("device_name_other"));
				userItem.setUserName(list.get(j).get("device_name"));
				userItem.setDeviceVer(list.get(j).get("device_version"));
				mOnlineFriendItems.add(userItem);
				mOnlineFriendIds.add(Integer.valueOf(list.get(j).get("device_anychat_id")));//無意義
			}

		/*	//按用户在线和不在线排序
			int len = mOnlineFriendItems.size();
			for (int k=len-1; k>=0; k--){
				if (mOnlineFriendItems.get(k).getIsOnline() == 0){
					//把它调整到最后
					mOnlineFriendItems.add(mOnlineFriendItems.get(k));
					mOnlineFriendIds.add(mOnlineFriendIds.get(k));
					mOnlineFriendItems.remove(k);
					mOnlineFriendIds.remove(k);
				}
			}*/

			Log.i(TAG, "mOnlineFriendItems length = "+mOnlineFriendItems.size());

		}catch (Exception e){
			e.printStackTrace();
		}

	}
	
	
	
}
