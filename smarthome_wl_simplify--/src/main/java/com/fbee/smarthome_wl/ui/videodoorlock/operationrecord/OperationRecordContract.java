package com.fbee.smarthome_wl.ui.videodoorlock.operationrecord;

import com.fbee.smarthome_wl.base.BasePresenter;
import com.fbee.smarthome_wl.base.BaseView;
import com.fbee.smarthome_wl.request.QueryVideoLockRecordReq;
import com.fbee.smarthome_wl.request.RecordReq;
import com.fbee.smarthome_wl.response.RecordResponse;

/**
 * Created by wl on 2017/10/30.
 */

public class OperationRecordContract {
    interface view extends BaseView {
        /**
         * 操作记录返回
         */
        void responseRecord(RecordResponse bean);
    }

    interface Presenter extends BasePresenter {
        void getOperationRecord(RecordReq body);
    }
}
