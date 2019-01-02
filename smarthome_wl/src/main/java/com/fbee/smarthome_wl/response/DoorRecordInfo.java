package com.fbee.smarthome_wl.response;

import java.io.Serializable;
import java.util.List;

/**
 * @class name：com.fbee.model
 * @anthor create by Zhaoli.Wang
 * @time 2017/1/3 15:23
 */
public class DoorRecordInfo implements Serializable {


    /**
     * Time : ["1483414170","1483414113","1483414106","1483410025"]
     * Value : [2,2,2,2]
     * Operation : [0,2,0,0]
     * UserID : [1,1,2,1]
     * AttData : [0,0,0,8]
     * ErrorMsg : OK
     */

    private String ErrorMsg;
    private List<String> Time;
    private List<Integer> Value;
    private List<Integer> Operation;
    private List<Integer> UserID;
    private List<Integer> AttData;

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

    public List<Integer> getUserID() {
        return UserID;
    }

    public void setUserID(List<Integer> UserID) {
        this.UserID = UserID;
    }

    public List<Integer> getAttData() {
        return AttData;
    }

    public void setAttData(List<Integer> AttData) {
        this.AttData = AttData;
    }
}
