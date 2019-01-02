package com.example.wl.WangLiPro_v1.bean;

import java.io.Serializable;

/**
 * 添加网关请求体
 * Created by WLPC on 2017/4/18.
 */

public class AddGateWayReq implements Serializable{


    /**
     * vendor_name : feibee
     * uuid : xxxxxxxxxx
     * username : xxxxxxxxx
     * password : xxxxxxxxx
     * authorization : admin
     * note : 网网的网关
     * version : xxx
     * location : {"longitude":"xxxx","latitude":"xxxx","highly":"xxxx","countries":"xxxx","province":"xxxx","city":"xxxx","partition":"xxxx","street":"xxxx"}
     */

    private String vendor_name;
    private String uuid;
    private String username;
    private String password;
    private String authorization;
    private String note;
    private String version;
    private LocationBean location;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public static class LocationBean implements Serializable{
        /**
         * longitude : xxxx
         * latitude : xxxx
         * highly : xxxx
         * countries : xxxx
         * province : xxxx
         * city : xxxx
         * partition : xxxx
         * street : xxxx
         */

        private String longitude;
        private String latitude;
        private String highly;
        private String countries;
        private String province;
        private String city;
        private String partition;
        private String street;

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getHighly() {
            return highly;
        }

        public void setHighly(String highly) {
            this.highly = highly;
        }

        public String getCountries() {
            return countries;
        }

        public void setCountries(String countries) {
            this.countries = countries;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPartition() {
            return partition;
        }

        public void setPartition(String partition) {
            this.partition = partition;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }
}
