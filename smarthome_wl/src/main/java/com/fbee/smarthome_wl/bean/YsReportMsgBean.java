package com.fbee.smarthome_wl.bean;

/**
 * Created by linyujie on 19/1/3.
 */

public class YsReportMsgBean {

    /**
     * vendor_name : feibee
     * uuid : xxxxxxxxxx
     * end_timestamp : 1499681215
     * start_timestamp : 1499681215
     * record_number : 10
     * alarm_type : not_call
     * has_deleted : false
     */

    private String vendor_name;
    private String uuid;
    private String end_timestamp;
    private String start_timestamp;
    private String record_number;
    private String alarm_type;
    private String has_deleted;

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEnd_timestamp() {
        return end_timestamp;
    }

    public void setEnd_timestamp(String end_timestamp) {
        this.end_timestamp = end_timestamp;
    }

    public String getStart_timestamp() {
        return start_timestamp;
    }

    public void setStart_timestamp(String start_timestamp) {
        this.start_timestamp = start_timestamp;
    }

    public String getRecord_number() {
        return record_number;
    }

    public void setRecord_number(String record_number) {
        this.record_number = record_number;
    }

    public String getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(String alarm_type) {
        this.alarm_type = alarm_type;
    }

    public String getHas_deleted() {
        return has_deleted;
    }

    public void setHas_deleted(String has_deleted) {
        this.has_deleted = has_deleted;
    }
}
