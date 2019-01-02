package com.fbee.smarthome_wl.ui.login;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.RegReq;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;

/**
 * @class name：com.fbee.smarthome_wl.ui.login
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/31 10:28
 */
public interface RegisterContract {


    interface View extends BaseView {
        /**
         * 验证码返回
         */
        void resCode( CodeResponse resCode);

        /**
         * 验证码获取失败
         */
        void resCodeFail();

        /**
         * 注册返回
         * @param bean
         */
        void resRegister(BaseResponse bean);


    }

    interface Presenter extends BasePresenter {

        /**
         * 请求验证码
         */
        void sendMessageCode( SmsReq body);


        /**
         * 注册
         */
        void register(RegReq body);

    }
}
