package com.fbee.smarthome_wl.ui.doorlock;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.response.DoorlockpowerInfo;

import java.util.Map;

/**
 * @class name：com.fbee.smarthome_wl.activity.doorlock
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/2 14:40
 */
public interface DoorLockContract {

    interface View extends BaseView {
        /**
         * 获取电量信息返回
         * @param info
         */
       void responseDoorInfo(DoorlockpowerInfo info);

        /**
         * 获取门锁型号
         * @param info
         */
        void responseDeviceModel(DoorlockpowerInfo info);

    }

    interface Presenter extends BasePresenter {

        /**获取电量状态 */
        void getStatus(Map prams,String type);

    }


}
