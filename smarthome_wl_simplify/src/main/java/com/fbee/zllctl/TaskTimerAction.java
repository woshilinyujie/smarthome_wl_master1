package com.fbee.zllctl;

import java.io.Serializable;

/**
 * 定时联动的定时信息
 * 
 * @author Administrator
 * 
 */
public class TaskTimerAction implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5029772237257296604L;
	private byte WorkMode; // week
	private byte h; // hour
	private byte m; // minute
	private byte s; // second
	// 1:防区处于布防状态时，实行该任务  2 ：防区处于撤防状态时，实行该任务 3:两者都实时
	private byte zoneabel=0;
	//在第那个防区 0-15bit 各代表一个防区  可以在多个防区
	private  int zoneArea=0;
	public byte getZoneabel() {
		return zoneabel;
	}

	public void setZoneabel(byte zoneabel) {
		this.zoneabel = zoneabel;
	}

	public int getZoneArea() {
		return zoneArea;
	}

	public void setZoneArea(int zoneArea) {
		this.zoneArea = zoneArea;
	}
	public byte getWorkMode()
	{
		return WorkMode;
	}

	public void setWorkMode(byte workMode)
	{
		WorkMode = workMode;
	}

	public byte getH()
	{
		return h;
	}

	public void setH(byte h)
	{
		this.h = h;
	}

	public byte getM()
	{
		return m;
	}

	public void setM(byte m)
	{
		this.m = m;
	}

	public byte getS()
	{
		return s;
	}

	public void setS(byte s)
	{
		this.s = s;
	}

}
