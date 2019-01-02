package com.fbee.smarthome_wl.ui.gateway;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.request.DeleteGateWayReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;

/**
 * Created by WLPC on 2017/4/19.
 */

public interface GateWayManagerContract {
    interface View extends BaseView {

        //飞比登录返回
        void loginFbeeResult(int result);
        /**
         * 删除网关返回
         * @param bean
         */
        void resDeleteGateWay(BaseResponse bean);
        /***
         * 查询网关信息返回
         */
        void resReqGateWayInfo(QueryGateWayInfoReq body);

        /**
         * 更新添加网关返回
         * @param bean
         * @param username
         */
        void resAddGateWay(BaseResponse bean, String username);
    }

    interface Presenter extends BasePresenter {

        /**
         * 更新添加网关
         */
        void reqUpdateGateWay(AddGateWayReq body, String username);

        /**
         * 删除网关请求
         */
        void reqDeleteGateWay(DeleteGateWayReq body);
        /***
         * 飞比登录
         */
        void loginFbee(String username, String psw);
        /***
         * 查询虚拟网关是否有猫眼设备
         */
        void reqGateWayInfo(QueryDevicesListInfo bodyEntity);
    }
}
