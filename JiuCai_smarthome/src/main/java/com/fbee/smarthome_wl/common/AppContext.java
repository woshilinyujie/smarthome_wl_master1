package com.fbee.smarthome_wl.common;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.TimerCount;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.GroupInfo;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.Serial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 缓存类
 *
 * @class name：com.fbee.common
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/7 14:23
 */
public class AppContext {

    protected static AppContext mWlContext;
    //    private Context mContext;
    private Serial mSerial;
    public static String JIUCAI_NAME = "JIUCAI_NAME";

    public static final String ACTION_BACKSERVICE = "com.smartdoorbell.activity.backserveric";
    public static List<HashMap<String, String>> deviceList = new ArrayList<HashMap<String, String>>() {
    };
    /**
     * 设备列表存储
     */
    public static List<ResAnychatLogin.UserBean.UserDevicesBean> devices = new ArrayList();
    public static ArrayList jiucaiVisitorPic = new ArrayList();

    public static ArrayList<String> getJiucaiVisitorPic() {
        return jiucaiVisitorPic;
    }

    public static void setJiucaiVisitorPic(String jiucaiVisitorPic) {
        AppContext.jiucaiVisitorPic.add(jiucaiVisitorPic);
    }


    public static void setDevices(List<ResAnychatLogin.UserBean.UserDevicesBean> devices) {
        AppContext.devices = devices;
    }

    public static List<ResAnychatLogin.UserBean.UserDevicesBean> getDevices() {
        return devices;
    }

    public static List DeleteList(ResAnychatLogin.UserBean.UserDevicesBean obj) {
        for (int i = 0; i < devices.size(); i++) {
            if ((obj.toString()).equals(devices.get(i).toString())) {
                devices.remove(i);
            }
        }
        return devices;
    }

    public static String getGwSnid() {
        return gwSnid;
    }

    public static void setGwSnid(String gwSnid) {
        AppContext.gwSnid = gwSnid;
    }

    public static String getVer() {
        return ver;
    }

    public static void setVer(String ver) {
        AppContext.ver = ver;
    }

    private static String ver; //网关版本
    private static String gwSnid;
    private static String mcountryName;  //国家
    private static String madminArea;    // 省
    private static String mlocality;     //市
    private static String msubLocality;   //区
    private static String mfeatureName;  //街道
    private static AddGateWayReq.LocationBean location = new AddGateWayReq.LocationBean();

    public static AddGateWayReq.LocationBean getLocation() {
        return location;
    }

    public static String getMcountryName() {
        return mcountryName;
    }

    public static void setMcountryName(String mcountryName) {
        AppContext.mcountryName = mcountryName;
    }

    public static String getMadminArea() {
        return madminArea;
    }

    public static void setMadminArea(String madminArea) {
        AppContext.madminArea = madminArea;
    }

    public static String getMlocality() {
        return mlocality;
    }

    public static void setMlocality(String mlocality) {
        AppContext.mlocality = mlocality;
    }

    public static String getMsubLocality() {
        return msubLocality;
    }

    public static void setMsubLocality(String msubLocality) {
        AppContext.msubLocality = msubLocality;
    }

    public static String getMfeatureName() {
        return mfeatureName;
    }

    public static void setMfeatureName(String mfeatureName) {
        AppContext.mfeatureName = mfeatureName;
    }


    //计时器Map
    public static ArrayMap<Integer, TimerCount> timerMap = new ArrayMap<>();

    /**
     * 网关设备列表
     */
    private static List<DeviceInfo> mOurDevices = new ArrayList();
    /**
     * 门锁设备列表
     */
    private static List<DeviceInfo> mDoorLockDevices = new ArrayList();
    /**
     * 区域列表
     */
    private static List<GroupInfo> mOurGroups = new ArrayList();
    /**
     * 场景列表
     */
    private static List<SenceInfo> mOurScenes = new ArrayList();
    /**
     * 移康猫眼设备列表
     */
    private static List<EquesListInfo.bdylistEntity> bdylist = new ArrayList();

    public static List<EquesListInfo.OnlinesEntity> getOnlines() {
        return onlines;
    }

    /**
     * 本地登录返回的 网关列表，和子账号列表
     */
    public LoginResult.BodyBean bodyBean;

    /**
     * 首页展示
     */
    private HomePageResponse.BodyBean mHomebody;

    //每个门锁最新电量缓存
    private static HashMap<Integer, Integer> doorLockPowerMap = new HashMap<>();

    /**
     * 设备的用户列表
     *
     * @return
     */
    private static HashMap<String, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean>> deviceUsermap = new HashMap<String, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean>>();

    public static HashMap<String, List<QueryDeviceUserResponse.BodyBean.DeviceUserListBean>> getMap() {
        return deviceUsermap;
    }

    public static HashMap<Integer, Integer> getDoorLockPowerMap() {
        return doorLockPowerMap;
    }


    public HomePageResponse.BodyBean getmHomebody() {
        if (mHomebody == null)
            mHomebody = new HomePageResponse.BodyBean();
        return mHomebody;
    }

    public void setmHomebody(HomePageResponse.BodyBean mHomebody) {
        this.mHomebody = mHomebody;
    }

    public LoginResult.BodyBean getBodyBean() {
        if (bodyBean == null) {
            bodyBean = new LoginResult.BodyBean();
        }
        return bodyBean;
    }

    public void setBodyBean(LoginResult.BodyBean bodyBean) {
        this.bodyBean = bodyBean;
    }

    public static void setOnlines(List<EquesListInfo.OnlinesEntity> onlines) {
        AppContext.onlines = onlines;
    }

    /***
     * 移康设备是否在线列表
     */
    private static List<EquesListInfo.OnlinesEntity> onlines = new ArrayList();

    public static AppContext getInstance() {
        if (mWlContext == null) {
            mWlContext = new AppContext();
        }
        return mWlContext;
    }

    private AppContext() {
    }

    private AppContext(Context context) {
//        mContext = context;
        mWlContext = this;
    }

    public static void init(Context context) {
        mWlContext = new AppContext(context);
    }

    /**
     * 获取serial  单例
     *
     * @return
     */
    public Serial getSerialInstance() {
        if (mSerial == null) {
            mSerial = new Serial();
        }
        return mSerial;
    }

    public static List<DeviceInfo> getmOurDevices() {
        return mOurDevices;
    }

    public static List<DeviceInfo> getmDoorLockDevices() {
        for (int i = 0; i < mOurDevices.size(); i++) {
            DeviceInfo d = mOurDevices.get(i);
            if (d.getDeviceId() == 10) {
                int uId = d.getUId();
                if (mDoorLockDevices.size() == 0) {
                    mDoorLockDevices.add(d);
                } else {
                    boolean tag = false;
                    for (int j = 0; j < mDoorLockDevices.size(); j++) {
                        int uId1 = mDoorLockDevices.get(j).getUId();
                        if (uId == uId1) {
                            tag = true;
                        }
                    }
                    if (!tag) {
                        mDoorLockDevices.add(d);
                    }
                }
            }
        }
        return mDoorLockDevices;
    }

    public static List<GroupInfo> getmOurGroups() {
        return mOurGroups;
    }


    public static List<SenceInfo> getmOurScenes() {
        return mOurScenes;
    }

    public static List<EquesListInfo.bdylistEntity> getBdylist() {
        return bdylist;
    }

    //根据设备uid查询门锁设备是否存在
    public static int findDoorLockExist(int uid) {
        List<DeviceInfo> doorLocks = getmDoorLockDevices();
        for (int i = 0; i < doorLocks.size(); ++i) {
            DeviceInfo d = doorLocks.get(i);
            if (d.getDeviceId() == 10 && d.getUId() == uid) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 清除所有数据
     */
    public static void clearAllDatas() {

        if (ver != null) {
            ver = null;
        }
        if (gwSnid != null) {
            gwSnid = null;
        }
        if (mcountryName != null) {
            mcountryName = null;
        }
        if (madminArea != null) {
            madminArea = null;
        }
        if (mlocality != null) {
            mlocality = null;
        }
        if (msubLocality != null) {
            msubLocality = null;
        }
        if (mfeatureName != null) {
            mfeatureName = null;
        }
        if (location != null) {
            location = null;
        }
        if (timerMap != null && timerMap.size() > 0) {
            timerMap.clear();
        }
        if (mOurDevices != null && mOurDevices.size() > 0) {
            mOurDevices.clear();
        }
        if (mDoorLockDevices != null && mDoorLockDevices.size() > 0) {
            mDoorLockDevices.clear();
        }
        if (mOurGroups != null && mOurGroups.size() > 0) {
            mOurGroups.clear();
        }
        if (mOurScenes != null && mOurScenes.size() > 0) {
            mOurScenes.clear();
        }
        if (bdylist != null && bdylist.size() > 0) {
            bdylist.clear();
        }
        if (AppContext.getInstance().bodyBean != null) {
            AppContext.getInstance().bodyBean = null;
        }
        if (AppContext.getInstance().mHomebody != null) {
            AppContext.getInstance().mHomebody = null;
        }
        if (deviceUsermap != null && deviceUsermap.size() > 0) {
            deviceUsermap.clear();
        }
        if (onlines != null && onlines.size() > 0) {
            onlines.clear();
        }
        LogUtil.e("homeFragment", "clear::::::::::::::::::::::::");
    }
}
