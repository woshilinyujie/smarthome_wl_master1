package com.fbee.smarthome_wl.request;

import java.util.List;

/**
 * Created by wl on 2017/9/13.
 */

public class QueryDeleteDoorlockAlarm {

    /**
     * vendor_name : feibee
     * uuid : xxxxxxxxxx
     * end_timestamp : 1499681215
     * start_timestamp : 1499681215
     * _id_list : ["5420013272fe096c39901048","5420013272fe096c39901049"]
     * alarm_type : not_call
     */

    private String vendor_name;
    private String uuid;
    private String end_timestamp;
    private String start_timestamp;
    private String alarm_type;
    private List<String> _id_list;

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

    public String getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(String alarm_type) {
        this.alarm_type = alarm_type;
    }

    public List<String> get_id_list() {
        return _id_list;
    }

    public void set_id_list(List<String> _id_list) {
        this._id_list = _id_list;
    }
}
