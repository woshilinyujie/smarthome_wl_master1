package com.fbee.smarthome_wl.ui.subuser;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddChildUserReq;
import com.fbee.smarthome_wl.request.QuerySubUserInfoReq;
import com.fbee.smarthome_wl.request.SmsReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.CodeResponse;
import com.fbee.smarthome_wl.response.QuerySubUserInfoResponse;

/**
 * Created by WLPC on 2017/5/8.
 */

public interface SubUserGateManagerContract {

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
         * 查询子用户返回
         * @param bean
         */
        void resQuerySubUser(QuerySubUserInfoResponse bean);


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

        /**
         * 查询子用户请求
         */
        void reqQuerySubUser(QuerySubUserInfoReq body);

    }
}
