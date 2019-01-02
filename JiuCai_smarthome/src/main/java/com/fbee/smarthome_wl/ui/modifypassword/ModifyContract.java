package com.fbee.smarthome_wl.ui.modifypassword;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.ModifyPasswordReq;
import com.fbee.smarthome_wl.response.BaseResponse;

/**
 * Created by WLPC on 2017/4/17.
 */

public interface ModifyContract {
    interface View extends BaseView {

        /**
         * 修改密码返回
         * @param bean
         */
        void resModifyPass(BaseResponse bean);

    }

    interface Presenter extends BasePresenter {

        /**
         * 请求修改
         */
        void sendModifyPassCode(ModifyPasswordReq body);


    }

}
