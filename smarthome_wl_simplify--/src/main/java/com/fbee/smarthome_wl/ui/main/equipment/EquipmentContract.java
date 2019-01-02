package com.fbee.smarthome_wl.ui.main.equipment;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddTokenReq;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.QueryGateWayReq;
import com.fbee.smarthome_wl.response.AddTokenResponse;
import com.fbee.smarthome_wl.response.QueryGateWayInfoResponse;

import java.util.List;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.equipment
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/11 9:03
 */
public interface EquipmentContract {
    interface View extends BaseView {
        //网关下的wifi设备
        void getGateWayResult(List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> mVideoLockList);
        //网关下的移康设备
        void equesDeviceListCallback(List<QueryGateWayInfoResponse.BodyBean.DeviceListBean> equesDeviceList);

        /**
         * 添加token返回
         * @param bean
         */
        void resAddTokenCallback(AddTokenResponse bean);
    }

    interface Presenter extends BasePresenter {
        //获取网关信息
        void getGateWayInfo(QueryGateWayReq req, String factorytype);

        /**
         * 删除设备请求
         */
        void reqDeleteDevices(DeleteDevicesReq body);

        /**
         * 添加token
         */
        void reqAddToken(AddTokenReq body);


    }



}
