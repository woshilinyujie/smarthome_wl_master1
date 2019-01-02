package com.fbee.smarthome_wl.ui.addordeldevicestosever;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceListResponse;

/**
 * Created by WLPC on 2017/5/5.
 */

public interface AddOrDelDevicesToSeverContract {
    interface View extends BaseView {

        /**
         * 添加设备返回
         *
         * @param bean
         */
        void resAddDevices(BaseResponse bean);

        /**
         * 删除设备返回
         *
         * @param bean
         */
        void resDeleteDevices(BaseResponse bean);

        void queryDevicesResult(QueryDeviceListResponse bean);

        void resReqGateWayInfo(QueryGateWayInfoReq body);
        //九彩猫眼
        void loginSuccess(Object obj);

    }

    interface Presenter extends BasePresenter {

        /**
         * 添加设备请求
         *
         * @param body
         */
        void reqAddDevices(AddDevicesReq body);

        /**
         * 删除设备请求
         *
         * @param body
         */
        void reqDeleteDevices(DeleteDevicesReq body);

        /***
         * 查询猫眼是否已经绑定门锁
         *
         * @param bodyEntity
         */
        void queryDevices(QueryDevicesListInfo bodyEntity);

        void reqGateWayInfo(QueryDevicesListInfo bodyEntity);
        /**
         * 九彩anychat注册并登录
         */
        void registerAndlogin(String username, String psw);
    }
}
