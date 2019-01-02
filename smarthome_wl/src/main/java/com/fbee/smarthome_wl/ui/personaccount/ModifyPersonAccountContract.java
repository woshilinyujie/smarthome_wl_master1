package com.fbee.smarthome_wl.ui.personaccount;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.request.UpdateUserinfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;

/**
 * Created by WLPC on 2017/4/27.
 */

public interface ModifyPersonAccountContract {
    interface View extends BaseView {

        /**
         * 修改个人账户返回
         * @param bean
         */
        void resModifyPersonAccount(BaseResponse bean);
        /**
         * 验证码返回
         */
        void resCode( BaseResponse resCode);

        /**
         * 验证码获取失败
         */
        void resCodeFail();

    }

    interface Presenter extends BasePresenter {

        /**
         * 修改个人账户请求
         * @param body
         */
        void reqModifyPersonAccount(UpdateUserinfoReq body);
        /**
         * 请求验证码
         */
       void sendMessageCode(SmsReq body);
    }
}
