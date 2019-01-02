package com.fbee.smarthome_wl.event;

import java.util.List;

/**
 * Created by wl on 2017/4/26.
 */

public class EquesAddLockEvent {

    /**
     * added_bdy : {"bid":"abcefg","name":"123456","nick":"猫眼","role":3}
     * code : 4000
     * method : on_addbdy_result
     * onlines : [{"bid":"abcefg","localupg":1,"nid":"123456","remoteupg":0,"status":1,"uid":"cdfg"}]
     */

    private AddedBdyEntity added_bdy;
    private String code;
    private String method;
    private List<OnlinesEntity> onlines;

    public void setAdded_bdy(AddedBdyEntity added_bdy) {
        this.added_bdy = added_bdy;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setOnlines(List<OnlinesEntity> onlines) {
        this.onlines = onlines;
    }

    public AddedBdyEntity getAdded_bdy() {
        return added_bdy;
    }

    public String getCode() {
        return code;
    }

    public String getMethod() {
        return method;
    }

    public List<OnlinesEntity> getOnlines() {
        return onlines;
    }

    public static class AddedBdyEntity {
        /**
         * bid : abcefg
         * name : 123456
         * nick : 猫眼
         * role : 3
         */

        private String bid;
        private String name;
        private String nick;
        private int role;

        public void setBid(String bid) {
            this.bid = bid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public String getBid() {
            return bid;
        }

        public String getName() {
            return name;
        }

        public String getNick() {
            return nick;
        }

        public int getRole() {
            return role;
        }
    }

    public static class OnlinesEntity {
        /**
         * bid : abcefg
         * localupg : 1
         * nid : 123456
         * remoteupg : 0
         * status : 1
         * uid : cdfg
         */

        private String bid;
        private int localupg;
        private String nid;
        private int remoteupg;
        private int status;
        private String uid;

        public void setBid(String bid) {
            this.bid = bid;
        }

        public void setLocalupg(int localupg) {
            this.localupg = localupg;
        }

        public void setNid(String nid) {
            this.nid = nid;
        }

        public void setRemoteupg(int remoteupg) {
            this.remoteupg = remoteupg;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getBid() {
            return bid;
        }

        public int getLocalupg() {
            return localupg;
        }

        public String getNid() {
            return nid;
        }

        public int getRemoteupg() {
            return remoteupg;
        }

        public int getStatus() {
            return status;
        }

        public String getUid() {
            return uid;
        }
    }
}
