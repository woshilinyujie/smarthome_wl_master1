package com.fbee.smarthome_wl.ui.subuser;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.DeleteDevicesReq;
import com.fbee.smarthome_wl.request.DeleteGateWayReq;
import com.fbee.smarthome_wl.request.QueryDevicesListInfo;
import com.fbee.smarthome_wl.request.QueryGateWayInfoReq;
import com.fbee.smarthome_wl.request.QuerySubUserInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.QuerySubUserInfoResponse;

/**
 * Created by WLPC on 2017/4/19.
 */

public interface SubUserInfoContract {
    interface View extends BaseView {

        /**
         * 查询子用户返回
         * @param bean
         */
        void resQuerySubUser(QuerySubUserInfoResponse bean);

        /**
         * 删除网关返回
         * @param bean
         * @param id
         */
        void resDeleteGateWay(BaseResponse bean, int postion);
        void resReqGateWayInfo(QueryGateWayInfoReq res);
    }

    interface Presenter extends BasePresenter {

        /**
         * 查询子用户请求
         */
        void reqQuerySubUser(QuerySubUserInfoReq body);

        /**
         * 删除网关请求
         */
        void reqDeleteGateWay(DeleteDevicesReq body, int postion);

        void reqGateWayInfo(QueryDevicesListInfo bodyEntity);
    }
}
