package com.fbee.smarthome_wl.ui.usermanage;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;

/**
 * Created by WLPC on 2017/4/24.
 */

public interface DeviceAssociateUserContract {
    interface View extends BaseView {

        /**
         * 添加设备用户返回1003
         * @param bean
         */
        void resAddDeviceUser(BaseResponse bean);

        /**
         * 查询设备信息返回
         * @param bean
         */

        void resQueryDevice(QueryDeviceUserResponse bean);

        /**
         * 修改设备用户返回1001
         * @param bean
         */
        void resModifyDeviceUser(BaseResponse bean);


        /**
         * 添加设备用户返回
         * @param bean
         */
       // void resDelDeviceUser(BaseResponse bean);
    }

    interface Presenter extends BasePresenter {

        /**
         * 添加设备用户请求
         * @param body
         */
        void reqAddDeviceUser(AddDeviceUser body);

        /**
         * 修改设备用户请求
         *
         */
        void reqModifyDeviceUser(AddDeviceUser body1,AddDeviceUser body2);



        /**
         * 查询设备信息
         * @param body
         */
        void reqQueryDevice(QueryDeviceuserlistReq body);

        /**
         * 删除设备用户
         * @param body
         */
        //void reqDelDeviceUser(DeleteDeviceUserInfo body);
    }
}
