package com.fbee.smarthome_wl.bean;

/**
 * Created by wl on 2017/5/5.
 */

public class Pus<T> {

    /**
     * UMS : {"header":{"api_version":"1.0","message_type":"MSG_SMS_CODE_REQ","seq_id":"1"},"body":{"global_roaming":"0086","username":"18888888888"}}
     */
    private T PUS;

    public T getPUS() {
        return PUS;
    }

    public void setPUS(T PUS) {
        this.PUS = PUS;
    }

}
