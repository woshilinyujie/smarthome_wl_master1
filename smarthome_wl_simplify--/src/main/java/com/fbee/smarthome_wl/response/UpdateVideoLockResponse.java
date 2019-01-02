package com.fbee.smarthome_wl.response;

/**
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2018/2/27 14:10
 */
public class UpdateVideoLockResponse extends BaseResponse{


    /**
     * body : {"new_version":"1.0.1","force_upgrade":"true","readme":"example: Repair major bug","url":"https://download.wonlycloud.com/wonly/android/1.0.1.apk","md5":"202cb962ac59075b964b07152d234b70"}
     */

    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        /**
         * new_version : 1.0.1
         * force_upgrade : true
         * readme : example: Repair major bug
         * url : https://download.wonlycloud.com/wonly/android/1.0.1.apk
         * md5 : 202cb962ac59075b964b07152d234b70
         */

        private String new_version;
        private String force_upgrade;
        private String readme;
        private String url;
        private String md5;

        public String getNew_version() {
            return new_version;
        }

        public void setNew_version(String new_version) {
            this.new_version = new_version;
        }

        public String getForce_upgrade() {
            return force_upgrade;
        }

        public void setForce_upgrade(String force_upgrade) {
            this.force_upgrade = force_upgrade;
        }

        public String getReadme() {
            return readme;
        }

        public void setReadme(String readme) {
            this.readme = readme;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
}
