package com.fbee.smarthome_wl.request;

/**
 * 查询视频锁开锁记录
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/11 14:48
 */
public class QueryVideoLockRecordReq {

    /**
     * vendor_name : feibee
     * uuid : xxxxxxxxxx
     * end_timestamp : 1499681215
     * start_timestamp : 1499681215
     * record_number : 10
     */

    private String vendor_name;
    private String uuid;
    private String end_timestamp;
    private String start_timestamp;
    private String record_number;

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
}
