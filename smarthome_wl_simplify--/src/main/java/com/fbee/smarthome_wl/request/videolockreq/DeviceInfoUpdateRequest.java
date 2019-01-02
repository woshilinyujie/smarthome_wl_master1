package com.fbee.smarthome_wl.request.videolockreq;


public class DeviceInfoUpdateRequest extends MnsBaseRequest {


    /**
     * vendor_name : general
     * uuid : xxxxxxxxxxxxx
     * data : {"status":"online","power":"50","version":"xxxxxxxx","network_ssid":"xxxxxxxxx","infrared_alarm_interval_time":"30","infrared_alarm_type":"txt","volume_level":"2","infrared_sensitivity":"50","auth_mode":"0","activation_code":"12311","on_off":"0,1,1,0,1,0,1,0"}
     */

    private String vendor_name;
    private String uuid;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * status : online
         * power : 50
         * version : xxxxxxxx
         * network_ssid : xxxxxxxxx
         * infrared_alarm_interval_time : 30
         * infrared_alarm_type : txt
         * volume_level : 2
         * infrared_sensitivity : 50
         * auth_mode : 0
         * activation_code : 12311
         * on_off : 0,1,1,0,1,0,1,0
         */

        private String status;
        private String power;
        private String version;
        private String network_ssid;
        private String infrared_alarm_interval_time;
        private String infrared_alarm_type;
        private String volume_level;
        private String infrared_sensitivity;
        private String auth_mode;
        private String activation_code;
        private String on_off;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPower() {
            return power;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getNetwork_ssid() {
            return network_ssid;
        }

        public void setNetwork_ssid(String network_ssid) {
            this.network_ssid = network_ssid;
        }

        public String getInfrared_alarm_interval_time() {
            return infrared_alarm_interval_time;
        }

        public void setInfrared_alarm_interval_time(String infrared_alarm_interval_time) {
            this.infrared_alarm_interval_time = infrared_alarm_interval_time;
        }

        public String getInfrared_alarm_type() {
            return infrared_alarm_type;
        }

        public void setInfrared_alarm_type(String infrared_alarm_type) {
            this.infrared_alarm_type = infrared_alarm_type;
        }

        public String getVolume_level() {
            return volume_level;
        }

        public void setVolume_level(String volume_level) {
            this.volume_level = volume_level;
        }

        public String getInfrared_sensitivity() {
            return infrared_sensitivity;
        }

        public void setInfrared_sensitivity(String infrared_sensitivity) {
            this.infrared_sensitivity = infrared_sensitivity;
        }

        public String getAuth_mode() {
            return auth_mode;
        }

        public void setAuth_mode(String auth_mode) {
            this.auth_mode = auth_mode;
        }

        public String getActivation_code() {
            return activation_code;
        }

        public void setActivation_code(String activation_code) {
            this.activation_code = activation_code;
        }

        public String getOn_off() {
            return on_off;
        }

        public void setOn_off(String on_off) {
            this.on_off = on_off;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "status='" + status + '\'' +
                    ", power='" + power + '\'' +
                    ", version='" + version + '\'' +
                    ", network_ssid='" + network_ssid + '\'' +
                    ", infrared_alarm_interval_time='" + infrared_alarm_interval_time + '\'' +
                    ", infrared_alarm_type='" + infrared_alarm_type + '\'' +
                    ", volume_level='" + volume_level + '\'' +
                    ", infrared_sensitivity='" + infrared_sensitivity + '\'' +
                    ", auth_mode='" + auth_mode + '\'' +
                    ", activation_code='" + activation_code + '\'' +
                    ", on_off='" + on_off + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DeviceInfoUpdateRequest{" +
                "vendor_name='" + vendor_name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
