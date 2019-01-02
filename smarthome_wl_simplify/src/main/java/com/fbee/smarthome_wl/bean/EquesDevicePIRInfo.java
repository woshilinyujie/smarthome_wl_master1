package com.fbee.smarthome_wl.bean;

/**
 * Created by wl on 2017/3/29.
 */

public class EquesDevicePIRInfo {

    /**
     * capture_num : 3
     * format : 0
     * from : 5358954ed3133c1c88185bb82e99e651
     * ingtone : 5
     * method : alarm_get_result
     * sense_sensitivity : 1
     * sense_time : 3
     * to : 3f7ee74a34ef4aa4b89cdfcf6ee183e8
     * volume : 5
     */

    private int capture_num;
    private int format;
    private String from;
    private int ringtone;
    private String method;
    private int sense_sensitivity;
    private int sense_time;
    private String to;
    private int volume;

    public void setCapture_num(int capture_num) {
        this.capture_num = capture_num;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setIngtone(int ringtone) {
        this.ringtone = ringtone;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setSense_sensitivity(int sense_sensitivity) {
        this.sense_sensitivity = sense_sensitivity;
    }

    public void setSense_time(int sense_time) {
        this.sense_time = sense_time;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getCapture_num() {
        return capture_num;
    }

    public int getFormat() {
        return format;
    }

    public String getFrom() {
        return from;
    }

    public int getRngtone() {
        return ringtone;
    }

    public String getMethod() {
        return method;
    }

    public int getSense_sensitivity() {
        return sense_sensitivity;
    }

    public int getSense_time() {
        return sense_time;
    }

    public String getTo() {
        return to;
    }

    public int getVolume() {
        return volume;
    }
}
