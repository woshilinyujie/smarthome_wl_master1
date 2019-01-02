package com.fbee.smarthome_wl.ui.mainsimplify.home;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.AddGateWayReq;
import com.fbee.smarthome_wl.response.BaseResponse;

/**
 * @class name：com.fbee.smarthome_wl.ui.mainsimplify.home
 * @anthor create by Zhaoli.Wang
 * @time 2017/11/8 13:29
 */
public interface HomeSimpleContract {


    interface View extends BaseView {
        //飞比登录返回
        void loginFbeeResult(int result);
        //网关添加返回
        void setCallBack(BaseResponse info);
    }

    interface Presenter extends BasePresenter {
        //飞比登录 返回
        void loginFbee(String username, String psw);

        //添加网关
        void addGateway(AddGateWayReq bodyBean);
    }


}
