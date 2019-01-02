package com.fbee.smarthome_wl.constant;

public class DoorLockGlobal {
	public static final int R_DOOR_LOCK_WAY_KEY = 0;// 按键
	public static final int R_DOOR_LOCK_WAY_FP = 2;// 指纹
	public static final int R_DOOR_LOCK_WAY_CARD = 3;// 刷卡
	public static final int R_DOOR_LOCK_WAY_HANDKEY = 0x0e;// 钥匙
	public static final int R_DOOR_LOCK_WAY_REMOTE = 0x0f;// 远程
	
	public static final int DOOR_LOCK_WAY_REMOTE=0x04;//远程

	
	public static final int R_DOOR_LOCK_STATE_LOCK = 0x01;// 关锁
	public static final int R_DOOR_LOCK_STATE_UNLOCK = 0x02;// 开锁
	public static final int R_DOOR_LOCK_STATE_ILLEGAL_OPEN = 0x03;// 非法开门
	public static final int R_DOOR_LOCK_STATE_ILLEGAL_CARD = 0x05;// 非法卡

	public static final int R_DOOR_LOCK_FALG_SUC = 0x00;// 成功
	public static final int R_DOOR_LOCK_FALG_FAIL = 0x01;// 失败
	public static final int R_DOOR_LOCK_FALG_STOP = 0x02;// 禁止远程操作

	public static final int R_DOOR_LOCK_CONTROL_ABLE = 0x01;// 可控
	public static final int R_DOOR_LOCK_CONTROL_UNABLE = 0x00;// 不可控

	public static final int R_DOOR_LOCK_DVC_NORMAL = 0x00;// 正常
	public static final int R_DOOR_LOCK_DVC_ALARM = 0x01;// 低压

	public static final int R_DOOR_LOCK_ALARM_FLAG = 0x0C;// 非法操作、也就是非法开门

	public static final int R_DOOR_LOCK_TAMPER_ALARM = 0x04;// 异常

	/*************** alarmCode 报警编码 *****************************/
	public static final int R_DOOR_LOCK_ALARM_TYPE_TAMPER = 0x04;	//防拆报警
	public static final int R_DOOR_LOCK_ALARM_TYPE_UNLOCK = 0x05;	//未关锁报警
	public static final int R_DOOR_LOCK_ALARM_TYPE_THREAT = 0x06;	//胁迫报警
	public static final int R_DOOR_LOCK_ALARM_TYPE_FALSE = 0x07;	//假锁报警
	public static final int R_DOOR_LOCK_ALARM_TYPE_CANCEL = 0x00;	//取消报警
	
	
	/*************************** clusterID ***********************/
	
	public static final int CLUSTER_ID_DOOR_REPORT=0x101;	// 门锁上报的clusterId
	public static final int CLUSTER_ID_POWER_REPORT=0x01;	// 电量上报的clusterId
	
	/**
	 * 门锁上报的返回数据识别，已经用广播区别所以，不要管值得大小
	 */
	public static final int DOOR_LOCK_STATE_FEED_BACK=0xff;	 //门锁状态返回的识别标志我自己定义的可以改
	public static final int DOOR_UNLOCK=0X01;					//判断为开锁
	public static final int DOOR_LOCK=0x00;					//判断为关锁
	public static final int DOOR_LOCK_SUCCESS=0x00;			//成功
	public static final int DOOR_LOCK_FAILURE=0x01;			//失败
	public static final int DOOR_LOCK_UNABLE=0x02;			//不允许开门
	
	/*************** battery report attrId ***********/
	public static final int BATTERY_ATTRID_STATE=0x3E; 		//电量状态上报，0x01表示低压0x00表示正常
	public static final int BATTERY_STATE_NORMAL=0x00;
	public static final int BATTERY_STATE_LOW_POWER_ALARM=0x01;
	
	public static final int BATTERY_ATTRID_POWER=0x21;		//电量百分比
	
	
}












