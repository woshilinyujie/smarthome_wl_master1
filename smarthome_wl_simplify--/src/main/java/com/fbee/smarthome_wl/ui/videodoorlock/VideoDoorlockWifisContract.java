package com.fbee.smarthome_wl.ui.videodoorlock;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.response.BaseResponse;

/**
 * Created by WLPC on 2017/9/11.
 */

public class VideoDoorlockWifisContract {


    interface View extends BaseView {

        /**
         * 添加设备返回
         * @param bean
         */
        void resAddDevice(BaseResponse bean);

    }

    interface Presenter extends BasePresenter {
        /**
         * 添加设备请求
         * @param body
         */
        void reqAddDevices(AddDevicesReq body);


    }
}
