package com.fbee.zllctl;

import java.io.Serializable;

/**
 * 红外线数据
 * Created by ZhaoLi.Wang on 2017/3/9 9:42
 */
public class IRDataInfo implements Serializable
{
	private static final long serialVersionUID = -9190967878640797177L;
    public short IRDataId;
    public String IRDataName;
	public int uId;
	public int delayTime;
}
