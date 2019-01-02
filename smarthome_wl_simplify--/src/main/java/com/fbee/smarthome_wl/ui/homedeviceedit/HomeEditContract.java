package com.fbee.smarthome_wl.ui.homedeviceedit;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.UpdateUserConfigurationReq;
import com.fbee.smarthome_wl.response.BaseResponse;

/**
 * @class name：com.fbee.smarthome_wl.ui.homedeviceedit
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/20 9:54
 */
public interface HomeEditContract {

    interface View extends BaseView {
        //设置用户配置返回
        void setCallBack(BaseResponse info);

    }

    interface Presenter extends BasePresenter {
        //设置用户配置
        void setUserConfig(UpdateUserConfigurationReq body);

    }

}
