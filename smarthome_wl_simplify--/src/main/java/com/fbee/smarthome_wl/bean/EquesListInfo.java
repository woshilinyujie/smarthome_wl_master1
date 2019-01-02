package com.fbee.smarthome_wl.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wl on 2017/3/23.
 */

public class EquesListInfo implements Serializable {
    //为确保序列化与反序列化一致，UID必须不可改变
    private static final long serialVersionUID = 1L;
    /**
     * method :  bdylist
     * bdylist : [{"name":"xxx","nick":"xxx","role":4,"bid":"abcdefg"}]
     * onlines : [{"bid":"abcdefg","uid":"123456","nid":"abde","status":1,"localupg":1,"remoteupg":0}]
     */

    private String method;
    private List<bdylistEntity> bdylist;
    private List<OnlinesEntity> onlines;

    public List<bdylistEntity> getBdylist() {
        return bdylist;
    }

    public void setBdylist(List<bdylistEntity> bdylist) {
        this.bdylist = bdylist;
    }

    public List<OnlinesEntity> getOnlines() {
        return onlines;
    }

    public void setOnlines(List<OnlinesEntity> onlines) {
        this.onlines = onlines;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public static class bdylistEntity implements Serializable {
        /**
         * name : xxx
         * nick : xxx
         * role : 4
         * bid : abcdefg
         */

        private String name;
        private String nick;
        private int role;
        private String bid;

        public void setName(String name) {
            this.name = name;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public void setBid(String bid) {
            this.bid = bid;
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

        public String getBid() {
            return bid;
        }
    }

    public static class OnlinesEntity  implements Serializable{
        /**
         * bid : abcdefg
         * uid : 123456
         * nid : abde
         * status : 1
         * localupg : 1
         * remoteupg : 0
         */

        private String bid;
        private String uid;
        private String nid;
        private int status;
        private int localupg;
        private int remoteupg;

        public void setBid(String bid) {
            this.bid = bid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setNid(String nid) {
            this.nid = nid;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setLocalupg(int localupg) {
            this.localupg = localupg;
        }

        public void setRemoteupg(int remoteupg) {
            this.remoteupg = remoteupg;
        }

        public String getBid() {
            return bid;
        }

        public String getUid() {
            return uid;
        }

        public String getNid() {
            return nid;
        }

        public int getStatus() {
            return status;
        }

        public int getLocalupg() {
            return localupg;
        }

        public int getRemoteupg() {
            return remoteupg;
        }
    }
}
