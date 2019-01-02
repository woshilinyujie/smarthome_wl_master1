package com.fbee.smarthome_wl.ui.videodoorlock.locklog;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.QueryVideoLockRecordReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.VideolockRecordResponse;

/**
 * Created by wl on 2017/9/12.
 */

public class VideoDoorLockLogContract {
    interface view extends BaseView {
        /***
         * 开锁记录返回
         * @param res
         */
        void responseDoorLog(VideolockRecordResponse res);
        /***
         * 删除开锁记录返回
         */
        void responseDeleteDoor(BaseResponse info);
    }

    interface Presenter extends BasePresenter {
        /***
         * 获取开锁记录
         * @param body
         */
        void getVideoDoorLocklog(QueryVideoLockRecordReq body);
        /***
         * 删除开锁记录
         */
        void deleteDoorLocklog(QueryVideoLockRecordReq body);
    }
}
