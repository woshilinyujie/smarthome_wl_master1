package com.fbee.smarthome_wl.response;

import java.io.Serializable;
import java.util.List;

/**
 * @class name：com.fbee.model
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/3 10:27
 */
public class DoorlockpowerInfo implements Serializable {


    /**
     * Time : ["1483410025"]
     * Value : ["150"]
     * ErrorMsg : OK
     */

    private String ErrorMsg;
    private List<String> Time;
    private List<String> Value;

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String ErrorMsg) {
        this.ErrorMsg = ErrorMsg;
    }

    public List<String> getTime() {
        return Time;
    }

    public void setTime(List<String> Time) {
        this.Time = Time;
    }

    public List<String> getValue() {
        return Value;
    }

    public void setValue(List<String> Value) {
        this.Value = Value;
    }
}
