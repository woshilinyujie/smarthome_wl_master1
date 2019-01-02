package com.fbee.smarthome_wl.bean;

import java.util.List;

/**
 * Created by wl on 2017/4/5.
 */

public class EquesVisitorInfo {

    /**
     * limit : 10
     * max : 5
     * method : ringlist
     * offset : 0
     * rings : [{"bid":"97f9eafe08ee4c6aa6e8cc82e11d77fe","fid":"24cc1a1483cc4cf99eee9dfda6be1098","ringtime":1491362213000},{"bid":"97f9eafe08ee4c6aa6e8cc82e11d77fe","fid":"ffdc13c720644a25b0cddaf961322db6","ringtime":1491362205000},{"bid":"97f9eafe08ee4c6aa6e8cc82e11d77fe","fid":"f6635288c7854647909b6f5c0f78df22","ringtime":1491362193000},{"bid":"97f9eafe08ee4c6aa6e8cc82e11d77fe","fid":"af1679cb4d954013a40022acbf396c35","ringtime":1491362186000},{"bid":"97f9eafe08ee4c6aa6e8cc82e11d77fe","fid":"d5537be0cc95462bbdbf519e800b6548","ringtime":1491356353000}]
     */

    private int limit;
    private int max;
    private String method;
    private int offset;
    private List<RingsEntity> rings;

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

    public void setRings(List<RingsEntity> rings) {
        this.rings = rings;
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

    public List<RingsEntity> getRings() {
        return rings;
    }

    public static class RingsEntity {
        /**
         * bid : 97f9eafe08ee4c6aa6e8cc82e11d77fe
         * fid : 24cc1a1483cc4cf99eee9dfda6be1098
         * ringtime : 1491362213000
         */

        private String bid;
        private String fid;
        private long ringtime;

        public void setBid(String bid) {
            this.bid = bid;
        }

        public void setFid(String fid) {
            this.fid = fid;
        }

        public void setRingtime(long ringtime) {
            this.ringtime = ringtime;
        }

        public String getBid() {
            return bid;
        }

        public String getFid() {
            return fid;
        }

        public long getRingtime() {
            return ringtime;
        }
    }
}
