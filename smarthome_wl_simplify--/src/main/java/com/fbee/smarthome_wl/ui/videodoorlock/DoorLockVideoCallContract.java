package com.fbee.smarthome_wl.ui.videodoorlock;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;

/**
 * Created by WLPC on 2017/9/13.
 */

public class DoorLockVideoCallContract {
    public interface View extends BaseView {

        /**
         * 查询设备信息返回
         * @param bean
         */

        void resQueryDevice(QueryDeviceUserResponse bean);

        /**
         * 添加设备返回
         * @param bean
         */
        void resAddDevice(BaseResponse bean);

        void resDeleteGateWay(BaseResponse bean);


        /**
         * 添加token返回
         * @param bean
         */
        void resAddToken(AddTokenResponse bean);
    }

    public interface Presenter extends BasePresenter {

        /**
         * 查询设备信息
         * @param body
         */
        void reqQueryDevice(QueryDeviceuserlistReq body);

        /**
         * 添加设备请求
         * @param body
         */
        void reqAddDevices(AddDevicesReq body);

        void reqDeleteGateWay(DeleteDevicesReq body);


        /**
         * 添加token
         */
        void reqAddToken(AddTokenReq body);
    }
}
