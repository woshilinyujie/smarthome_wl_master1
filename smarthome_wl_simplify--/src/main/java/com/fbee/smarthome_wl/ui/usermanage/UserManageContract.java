package com.fbee.smarthome_wl.ui.usermanage;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;

/**
 * Created by WLPC on 2017/6/15.
 */

public class UserManageContract {

    public interface View extends BaseView {

        /**
         * 查询设备信息返回
         * @param bean
         */

        void resQueryDevice(QueryDeviceUserResponse bean);


        /**
         * 添加设备用户返回
         * @param bean
         */
        void resAddDeviceUser(BaseResponse bean);

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
         * 添加设备用户请求
         * @param body
         */
        void reqAddDeviceUser(AddDeviceUser body);



        /**
         * 添加token
         */
        void reqAddToken(AddTokenReq body);

    }
}
