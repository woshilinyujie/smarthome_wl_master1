package com.fbee.smarthome_wl.ui.forgetpassword;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.ForgetPwd;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;

/**
 * Created by WLPC on 2017/4/17.
 */

public class ForgetContract {
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
         * 找回密码返回
         * @param bean
         */
        void resForgetPassWord(BaseResponse bean);


    }

    interface Presenter extends BasePresenter {

        /**
         * 请求验证码
         */
        void sendMessageCode(SmsReq body);


        /**
         * 找回密码
         */
        void findPassWord(ForgetPwd body);

    }
}
