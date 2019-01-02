package com.fbee.smarthome_wl.ui.main;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddDeviceUser;
import com.fbee.smarthome_wl.request.AddDevicesReq;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.PusBodyBean;
import com.fbee.smarthome_wl.request.QueryDeviceuserlistReq;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.UpdataApkResponse;
import com.fbee.smarthome_wl.response.UpdateModel;

/**
 * @class name：com.fbee.smarthome_wl.activity.mian
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/4 10:20
 */
public interface MainContract {

    interface View extends BaseView {
        /**
         * 升级接口返回
         *
         * @param updateinfo
         */
        void resUpdate(UpdateModel updateinfo);

        /**
         * 网关升级接口返回
         *
         * @param updateinfo
         */
        void resGwCallback(UpdataApkResponse updateinfo);

        /**
         * 添加设备用户返回
         *
         * @param bean
         */
        void resAddDeviceUser(BaseResponse bean);

        /**
         * 添加token返回
         *
         * @param bean
         */
        void resAddToken(AddTokenResponse bean);

    }

    interface Presenter extends BasePresenter {
        /**
         * 添加设备白名单
         */
        void requestDeviceList(String username, String psw);

        /**
         * 检测升级接口
         */
        void updateVersion(PusBodyBean bodyEntity);

        /**
         * 检测网关版本升级
         */
        void updateGateWay(PusBodyBean bodyEntity);

        /**
         * 查询设备用户列表
         */
        void getUserEquipmentlist(int uid, QueryDeviceuserlistReq info);

        /**
         * 添加设备请求
         *
         * @param body
         */
        void reqAddDevices(AddDevicesReq body);

        /**
         * 添加设备用户请求
         *
         * @param body
         */
        void reqAddDeviceUser(AddDeviceUser body);

        /***
         * 注销用户
         */
        void reqDestroyUser();

        /**
         * 添加token
         */
        void reqAddToken(AddTokenReq body);
    }

}
