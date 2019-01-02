package com.fbee.smarthome_wl.ui.doorlocklog;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.greendao.Doorlockrecord;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;

import java.util.List;
import java.util.Map;

/**
 * @class name：com.fbee.smarthome_wl.activity.doorlocklog
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/4 8:49
 */
public interface DoorLockLogContract {
    interface View extends BaseView {
        /**
         * 门锁记录返回
         * @param data
         */
        void resDoorInfoData(List<Doorlockrecord> data);

        /**
         * 获取数据失败
         */
        void resDatafail();

        /**
         * 删除成功
         */
        void deteleSuccess();

        /**
         * 删除失败
         */
        void deteleFail();


        /**
         * 查询设备信息返回
         * @param bean
         */

        void resQueryDevice(QueryDeviceUserResponse bean);

    }

    interface Presenter extends BasePresenter {
        /**获取门锁记录 */
        void getDoorInfo(Map prams, int mdeviceUid, String mdeviceName,String ieee);

        /**删除门锁记录 */
        void deteleDoorinfo(Map params);


        /**
         * 添加设备用户请求
         * @param body
         */
        void reqAddDeviceUser(AddDeviceUser body);


        /**
         * 查询设备信息
         * @param body
         */
        void reqQueryDevice(QueryDeviceuserlistReq body);
    }

}
