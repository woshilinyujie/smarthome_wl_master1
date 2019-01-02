package com.fbee.smarthome_wl.response;

/**
 * Created by wl on 2017/4/25.
 */

public class UpdataApkResponse extends BaseResponse {

    /**
     * body : {"new_version":"1.0.1","force_upgrade":"true","readme":"example: Repair major bug","url":"https://download.wonlycloud.com/wonly/android/1.0.1.apk"}
     */

    private  BodyEntity body;

    public void setBody(BodyEntity body) {
        this.body = body;
    }

    public BodyEntity getBody() {
        return body;
    }

    public static class BodyEntity {
        /**
         * new_version : 1.0.1
         * force_upgrade : true
         * readme : example: Repair major bug
         * url : https://download.wonlycloud.com/wonly/android/1.0.1.apk
         */

        private String new_version;
        private String force_upgrade;
        private String readme;
        private String url;

        public void setNew_version(String new_version) {
            this.new_version = new_version;
        }

        public void setForce_upgrade(String force_upgrade) {
            this.force_upgrade = force_upgrade;
        }

        public void setReadme(String readme) {
            this.readme = readme;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNew_version() {
            return new_version;
        }

        public String getForce_upgrade() {
            return force_upgrade;
        }

        public String getReadme() {
            return readme;
        }

        public String getUrl() {
            return url;
        }
    }
}
