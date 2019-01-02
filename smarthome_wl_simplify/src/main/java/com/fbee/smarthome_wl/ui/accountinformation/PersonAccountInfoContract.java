package com.fbee.smarthome_wl.ui.accountinformation;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.UpdateUserinfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;

/**
 * Created by WLPC on 2017/4/27.
 */

public interface PersonAccountInfoContract {
    interface View extends BaseView {

        /**
         * 修改个人账户返回
         * @param bean
         */
        void resModifyPersonAccount(BaseResponse bean);


    }

    interface Presenter extends BasePresenter {

        /**
         * 修改个人账户请求
         * @param body
         */
        void reqModifyPersonAccount(UpdateUserinfoReq body);

        /**
         * 注销账号
         */
        void reqDestroyUser();

    }
}
