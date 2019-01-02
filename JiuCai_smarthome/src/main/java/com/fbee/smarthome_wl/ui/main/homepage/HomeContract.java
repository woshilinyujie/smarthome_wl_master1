package com.fbee.smarthome_wl.ui.main.homepage;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.request.HomePageInfoReq;
import com.fbee.smarthome_wl.request.UpdateUserConfigurationReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.HomePageResponse;

/**
 * @class name：com.fbee.smarthome_wl.ui.main.homepage
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/28 13:39
 */
public interface HomeContract {

    interface View extends BaseView {

        //飞比登录返回
        void loginFbeeResult(int result);

        //返回用户配置
        void userConfigResult(HomePageResponse response);

        //设置用户配置返回
        void setCallBack(BaseResponse info);

        void userConfigResultNoBody(BaseResponse info);
        //九彩猫眼
        void loginSuccess(Object obj);
    }

    interface Presenter extends BasePresenter {

        /**
         * 飞比登录
         *
         * @param username
         * @param psw
         */
        void loginFbee(String username, String psw);


        /**
         * 查询首页配置
         */
        void getHomePageSetting(HomePageInfoReq bodyBean);


        //设置用户配置
        void setUserConfig(UpdateUserConfigurationReq body);

        //添加网关
        void addGateway(AddGateWayReq bodyBean);
        /**
         * 九彩anychat注册并登录
         */
        void registerAndlogin(String username, String psw);
    }
}