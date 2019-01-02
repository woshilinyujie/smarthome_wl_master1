package com.fbee.smarthome_wl.bean;

import java.net.URL;
import java.util.List;

/**
 * Created by wl on 2017/3/31.
 */

public class EquesAlarmInfo {

    /**
     * alarms : [{"aid":"6c1cfd6c30f9444aaddf6f9e5fb59cbb","bid":"f1f7e8c2533c4e2f84c50932bf5450dd","create":1490925484000,"fid":["1-0-20170331-14909254880.zip"],"pvid":["1-0-20170331-14909254881.jpg"],"time":1490925488000,"type":4,"uid":"f1f7e8c2533c4e2f84c50932bf5450dd"},{"aid":"836469d575884ae1a48784312d593bfd","bid":"f1f7e8c2533c4e2f84c50932bf5450dd","create":1490925263000,"fid":["1-0-20170331-14909252670.zip"],"pvid":["1-0-20170331-14909252671.jpg"],"time":1490925267000,"type":4,"uid":"f1f7e8c2533c4e2f84c50932bf5450dd"},{"aid":"cfb00b00c8a14449a702053c1d8c3f84","bid":"f1f7e8c2533c4e2f84c50932bf5450dd","create":1490924956000,"fid":["1-0-20170331-14909249600.zip"],"pvid":["1-0-20170331-14909249601.jpg"],"time":1490924960000,"type":4,"uid":"f1f7e8c2533c4e2f84c50932bf5450dd"},{"aid":"70d94e228e594f5799651739a88c9603","bid":"f1f7e8c2533c4e2f84c50932bf5450dd","create":1490924926000,"fid":["1-1-20170331-14909249300.zip"],"pvid":["1-1-20170331-14909249301.jpg"],"time":1490924930000,"type":4,"uid":"f1f7e8c2533c4e2f84c50932bf5450dd"},{"aid":"53a0691c8db248f29f8a663ee0eccfbb","bid":"f1f7e8c2533c4e2f84c50932bf5450dd","create":1490924446000,"fid":["1-1-20170331-14909244510.zip"],"pvid":["1-1-20170331-14909244511.jpg"],"time":1490924451000,"type":4,"uid":"f1f7e8c2533c4e2f84c50932bf5450dd"},{"aid":"f70c5700c4a44dd8ad39b893ef1baea5","bid":"f1f7e8c2533c4e2f84c50932bf5450dd","create":1490923902000,"fid":["1-0-20170331-14909239070.zip"],"pvid":["1-0-20170331-14909239071.jpg"],"time":1490923907000,"type":4,"uid":"f1f7e8c2533c4e2f84c50932bf5450dd"},{"aid":"3ef4d060d0c640ce99d3f581663d25af","bid":"f1f7e8c2533c4e2f84c50932bf5450dd","create":1490923571000,"fid":["1-1-20170331-14909235750.zip"],"pvid":["1-1-20170331-14909235751.jpg"],"time":1490923575000,"type":4,"uid":"f1f7e8c2533c4e2f84c50932bf5450dd"}]
     * code : 4000
     * limit : 7
     * max : 560
     * method : almlist
     * offset : 0
     */

    private int code;
    private int limit;
    private int max;
    private String method;
    private int offset;
    private List<AlarmsEntity> alarms;

    public void setCode(int code) {
        this.code = code;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setAlarms(List<AlarmsEntity> alarms) {
        this.alarms = alarms;
    }

    public int getCode() {
        return code;
    }

    public int getLimit() {
        return limit;
    }

    public int getMax() {
        return max;
    }

    public String getMethod() {
        return method;
    }

    public int getOffset() {
        return offset;
    }

    public List<AlarmsEntity> getAlarms() {
        return alarms;
    }

    public static class AlarmsEntity {
        /**
         * aid : 6c1cfd6c30f9444aaddf6f9e5fb59cbb
         * bid : f1f7e8c2533c4e2f84c50932bf5450dd
         * create : 1490925484000
         * fid : ["1-0-20170331-14909254880.zip"]
         * pvid : ["1-0-20170331-14909254881.jpg"]
         * time : 1490925488000
         * type : 4
         * uid : f1f7e8c2533c4e2f84c50932bf5450dd
         */

        private String aid;
        private String bid;
        private long create;
        private long time;
        private int type;
        private String uid;
        private List<String> fid;
        private List<String> pvid;
//        private URL uil;
//
//        public URL getUil() {
//            return uil;
//        }
//
//        public void setUil(URL uil) {
//            this.uil = uil;
//        }

        public void setAid(String aid) {
            this.aid = aid;
        }

        public void setBid(String bid) {
            this.bid = bid;
        }

        public void setCreate(long create) {
            this.create = create;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setFid(List<String> fid) {
            this.fid = fid;
        }

        public void setPvid(List<String> pvid) {
            this.pvid = pvid;
        }

        public String getAid() {
            return aid;
        }

        public String getBid() {
            return bid;
        }

        public long getCreate() {
            return create;
        }

        public long getTime() {
            return time;
        }

        public int getType() {
            return type;
        }

        public String getUid() {
            return uid;
        }

        public List<String> getFid() {
            return fid;
        }

        public List<String> getPvid() {
            return pvid;
        }
    }
}
