package com.fbee.smarthome_wl.ui.login;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.LocalLoginReq;
import com.fbee.smarthome_wl.response.LoginResult;

/**
 * Created by WLPC on 2017/1/6.
 */

public interface LoginContract {

    interface View extends BaseView {
        //九彩猫眼
        void loginSuccess(Object obj);

        //飞比登录返回
        void loginFbeeResult(int result);

        //本地服务器登录返回
        void loginLocalsuccess(LoginResult result,long time);


    }

    interface Presenter extends BasePresenter {

        /**
         * 九彩anychat注册并登录
         */
        void registerAndlogin(String username, String psw);


        /**
         * 飞比登录
         * @param username
         * @param psw
         */
        void loginFbee(String username, String psw);

        /**
         * 本地服务登录
         */
        void loginLocal(LocalLoginReq bodyBean);

    }


}
