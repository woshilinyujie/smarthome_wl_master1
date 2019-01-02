package com.fbee.smarthome_wl.response;

import java.io.Serializable;
import java.util.List;

/**
 * @class name：com.fbee.model
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/3 15:58
 */
public class DoorAlarmRecordinfo  implements Serializable {


    /**
     * Time : []
     * Value : []
     * Operation : []
     * ClusterID : []
     * ErrorMsg : OK
     */

    private String ErrorMsg;
    private List<String> Time;
    private List<Integer> Value;
    private List<Integer> Operation;
    private List<Integer> ClusterID;

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

    public List<Integer> getValue() {
        return Value;
    }

    public void setValue(List<Integer> Value) {
        this.Value = Value;
    }

    public List<Integer> getOperation() {
        return Operation;
    }

    public void setOperation(List<Integer> Operation) {
        this.Operation = Operation;
    }

    public List<Integer> getClusterID() {
        return ClusterID;
    }

    public void setClusterID(List<Integer> ClusterID) {
        this.ClusterID = ClusterID;
    }
}
