package com.fbee.smarthome_wl.ui.subuser;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddChildUserReq;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;

/**
 * Created by WLPC on 2017/4/20.
 */

public interface AddSubUserContract {

    interface View extends BaseView {

        /**
         * 添加子用户返回
         * @param bean
         */
        void resAddSubUser(BaseResponse bean);


        /**
         * 验证码返回
         */
        void resCode( CodeResponse resCode);

        /**
         * 验证码获取失败
         */
        void resCodeFail();

    }

    interface Presenter extends BasePresenter {

        /**
         * 添加子用户请求
         * @param body
         */
        void reqAddSubUser(AddChildUserReq body);


        /**
         * 请求验证码
         */
       void sendMessageCode(SmsReq body);

    }
}
