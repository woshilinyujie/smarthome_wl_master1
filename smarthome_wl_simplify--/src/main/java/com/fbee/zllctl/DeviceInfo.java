package com.fbee.zllctl;

import java.io.Serializable;
import java.util.Arrays;

public class DeviceInfo implements Serializable {
	private static final long serialVersionUID = 5423083013475017400L;
	private String deviceName;
	private String deviceSnid;
	private byte deviceStatus;// 设备是否在线；
	private byte deviceState; // 设备状态（灯:开、关)
	private int uId;
	private short deviceId;
	private short ProfileId;
	public String type = "Unknown Device";

	private int Sensordata = 0;// 用来存储传感器设备上传的数据
	private short clusterId;
	private short attribID;
	// 判断设备类型
	public byte hasColourable = 0;
	public byte hasDimmable = 0;
	public byte hasSwitchable = 0;
	public byte hasThermometer = 0;
	public byte hasPowerUsage = 0;
	public byte hasOutSwitch = 0;
	public byte hasOutLeveL = 0;
	public byte hasOutColor = 0;
	public byte hasOutScene = 0;
	public byte hasOutGroup = 0;
	public byte hasSensor = 0; // 是传感器
	public byte issmartplug = 0; // 是智能开关
	private byte[] IEEE = new byte[8];
	private String iconPath;
	private short zoneType;
	private boolean isLowPower=false;

	public DeviceInfo() {

	}

	public DeviceInfo(String deviceName, int uId, short deviceId,
					  short profileId, byte hasColourable, byte hasDimmable,
					  byte hasSwitchable, byte hasThermometer, byte hasPowerUsage,
					  byte hasOutSwitch, byte hasOutLeveL, byte hasOutColor,
					  byte hasOutScene, byte hasOutGroup, byte hasSensor,
					  byte issmartplug, short zoneType) {
		super();
		this.deviceName = deviceName;
		this.uId = uId;
		this.deviceId = deviceId;
		this.ProfileId = profileId;
		this.hasColourable = hasColourable;
		this.hasDimmable = hasDimmable;
		this.hasSwitchable = hasSwitchable;
		this.hasThermometer = hasThermometer;
		this.hasPowerUsage = hasPowerUsage;
		this.hasOutSwitch = hasOutSwitch;
		this.hasOutLeveL = hasOutLeveL;
		this.hasOutColor = hasOutColor;
		this.hasOutScene = hasOutScene;
		this.hasOutGroup = hasOutGroup;
		this.hasSensor = hasSensor;
		this.issmartplug = issmartplug;
		this.zoneType = zoneType;
	}

	public DeviceInfo(int uId, int data, short clusterId, short attribID) {
		this.uId = uId;
		this.Sensordata = data;
		this.clusterId = clusterId;
		this.attribID = attribID;
	}

	public DeviceInfo(int uId, int data, short clusterId, short attribID,
					  String deviceName) {
		this.uId = uId;
		this.Sensordata = data;
		this.clusterId = clusterId;
		this.attribID = attribID;
		this.deviceName = deviceName;
	}
  
	public boolean getIsLowpower() {
		return isLowPower;
	}

	public void setIsLowpower(boolean isLowPower) {
		this.isLowPower = isLowPower;
	}
	public short getZoneType() {
		return zoneType;
	}

	public void setZoneType(short zoneType) {
		this.zoneType = zoneType;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getDeviceName() {
		if (deviceName == null) {
			return "";
		}
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getUId() {
		return uId;
	}

	public void setUId(int uid) {
		this.uId = uid;
	}

	public short getDeviceId() {
		return deviceId;
	}

	public short getProfileId() {
		return ProfileId;
	}

	public void setProfileId(short profileId) {
		ProfileId = profileId;
	}

	public byte getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(byte deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public byte getDeviceState() {
		return deviceState;
	}

	public void setDeviceState(byte deviceState) {
		this.deviceState = deviceState;
	}

	public int getSensordata() {
		return Sensordata;
	}

	public void setSensordata(int sensordata) {
		Sensordata = sensordata;
	}

	public short getClusterId() {
		return clusterId;
	}

	public void setClusterId(short clusterId) {
		this.clusterId = clusterId;
	}

	public short getAttribID() {
		return attribID;
	}

	public void setAttribID(short attribID) {
		this.attribID = attribID;
	}

	public byte[] getIEEE() {
		return IEEE;
	}

	public void setIEEE(byte[] iEEE) {
		IEEE = iEEE;
	}

	public String getDeviceSnid() {
		return deviceSnid;
	}
	
	public void setDeviceSnid(String deviceSnid) {
		this.deviceSnid = deviceSnid;
	}
//	@Override
//	public String toString() {
//		return "DeviceInfo [deviceName=" + deviceName + ", deviceStatus="
//				+ deviceStatus + ", deviceState=" + deviceState
//				+ ", Sensordata=" + Sensordata + ", clusterId=" + clusterId
//				+ ", attribID=" + attribID + "]";
//	}


	@Override
	public String toString() {
		return "DeviceInfo{" +
				"deviceName='" + deviceName + '\'' +
				", deviceSnid='" + deviceSnid + '\'' +
				", deviceStatus=" + deviceStatus +
				", deviceState=" + deviceState +
				", uId=" + uId +
				", deviceId=" + deviceId +
				", ProfileId=" + ProfileId +
				", type='" + type + '\'' +
				", Sensordata=" + Sensordata +
				", clusterId=" + clusterId +
				", attribID=" + attribID +
				", hasColourable=" + hasColourable +
				", hasDimmable=" + hasDimmable +
				", hasSwitchable=" + hasSwitchable +
				", hasThermometer=" + hasThermometer +
				", hasPowerUsage=" + hasPowerUsage +
				", hasOutSwitch=" + hasOutSwitch +
				", hasOutLeveL=" + hasOutLeveL +
				", hasOutColor=" + hasOutColor +
				", hasOutScene=" + hasOutScene +
				", hasOutGroup=" + hasOutGroup +
				", hasSensor=" + hasSensor +
				", issmartplug=" + issmartplug +
				", IEEE=" + Arrays.toString(IEEE) +
				", iconPath='" + iconPath + '\'' +
				", zoneType=" + zoneType +
				", isLowPower=" + isLowPower +
				'}';
	}
}
