package com.fbee.smarthome_wl.ui.subuser;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.DeteleChildUser;
import com.fbee.smarthome_wl.response.BaseResponse;

/**
 * Created by WLPC on 2017/4/19.
 */

public interface SubUserListContract {

    interface View extends BaseView {

        /**
         * 删除子用户返回
         * @param bean
         */
        void resDeleteSubUser(BaseResponse bean);


    }

    interface Presenter extends BasePresenter {

        /**
         * 删除子用户请求
         * @param body
         */
        void reqDeleteSubUser(DeteleChildUser body);


    }
}
