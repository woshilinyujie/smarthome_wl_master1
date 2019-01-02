package com.fbee.zllctl;

import java.io.Serializable;

/**
 * 场景中单个设备，还有该设备的状态设置
 * Created by ZhaoLi.Wang on 2017/3/9 9:38
 */

public class SenceData implements Serializable
{
  private int uId;
  private short deviceId;
  private byte data1;
  private byte data2;
  private byte data3;
  private byte data4;
  private byte delaytime = 0;
  private short irId;
  
  public SenceData() {}


  public SenceData(int uId, short deviceId, byte data1, byte data2, byte data3, byte data4)
  {
    this.uId = uId;
    this.deviceId = deviceId;
    this.data1 = data1;
    this.data2 = data2;
    this.data3 = data3;
    this.data4 = data4;
  }
  
  public SenceData(int uId, short deviceId, byte data1, byte data2, byte data3, byte data4, byte irid, byte delaytime)
  {
    this.uId = uId;
    this.deviceId = deviceId;
    this.data1 = data1;
    this.data2 = data2;
    this.data3 = data3;
    this.data4 = data4;
    this.irId = irid;
    this.delaytime = delaytime;
  }
  
  public short getIrId()
  {
    return this.irId;
  }
  
  public void setIrId(short irId)
  {
    this.irId = irId;
  }
  
  public byte getDelaytime()
  {
    return this.delaytime;
  }
  
  public void setDelaytime(byte delaytime)
  {
    this.delaytime = delaytime;
  }
  
  public int getuId()
  {
    return this.uId;
  }
  
  public void setuId(int uId)
  {
    this.uId = uId;
  }
  
  public short getDeviceId()
  {
    return this.deviceId;
  }
  
  public void setDeviceId(short deviceId)
  {
    this.deviceId = deviceId;
  }
  
  public byte getData1()
  {
    return this.data1;
  }
  
  public void setData1(byte data1)
  {
    this.data1 = data1;
  }
  
  public byte getData2()
  {
    return this.data2;
  }
  
  public void setData2(byte data2)
  {
    this.data2 = data2;
  }
  
  public byte getData3()
  {
    return this.data3;
  }
  
  public void setData3(byte data3)
  {
    this.data3 = data3;
  }
  
  public byte getData4()
  {
    return this.data4;
  }
  
  public void setData4(byte data4)
  {
    this.data4 = data4;
  }
}
