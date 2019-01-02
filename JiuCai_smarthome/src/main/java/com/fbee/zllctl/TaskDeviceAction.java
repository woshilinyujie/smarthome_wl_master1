package com.fbee.zllctl;

import java.io.Serializable;

/**
 * 设备触发任务，每个任务包含当前设备的Uid，deviceId，条件一（condition1）,条件一的值（data1），（条件二（condition2）,
 * 条件二的值（data2））
 *  大多数下只用到condition1，（除温湿度），代表成立条件，2代表等于，1代表小于，3代表大于。data1代表状态值，举个例子 门磁联动，  condition1 =2，data1=1 代表门磁开门了，  condition2 代表温湿度里面的湿度，用法和condition1一样了
 *  * 每个动作包含的数据有：tasktype，(senceId / 短地址) ，endpoint，Device Id
 * ,data1，data2，data3,data4,data5,data6,data7 其中通过tasktype 来确定动作的类型： 定时： 0x01
 * 场景： 0x02 设备： 0x03
 *
 * 
 */
public class TaskDeviceAction implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6788487849446346915L;
	private int uId;
	// 端口号
	private short deviceId;

	private byte condition1;
	private int data1;
	private byte condition2;
	private int data2;

	public int getData2()
	{
		return data2;
	}

	public void setData2(int data2)
	{
		this.data2 = data2;
	}

	public byte getCondition1()
	{
		return condition1;
	}

	public void setCondition1(byte condition1)
	{
		this.condition1 = condition1;
	}

	public byte getCondition2()
	{
		return condition2;
	}

	public void setCondition2(byte condition2)
	{
		this.condition2 = condition2;
	}

	public int getData1()
	{
		return data1;
	}

	public void setData1(int data1)
	{
		this.data1 = data1;
	}

	public int getuId()
	{
		return uId;
	}

	public void setuId(int uId)
	{
		this.uId = uId;
	}

	public short getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(short deviceId)
	{
		this.deviceId = deviceId;
	}

}
