package com.fbee.smarthome_wl.response;

import java.io.Serializable;

/**
 * @class name：com.fbee.model
 * @anthor create by Zhaoli.Wang
 * @time 2016/12/8 17:37
 */
public class UpdateModel extends BaseResponse implements Serializable{

    /**
     * header : {"api_version":"1.0","message_type":"MSG_PRODUCT_UPGRADE_RSP","seq_id":"1","return_value":"true","return_string":"Success OK"}
     * body : {"new_version":"1.0.1","force_upgrade":"true","readme":"example: Repair major bug","url":"https://download.wonlycloud.com/wonly/android/1.0.1.apk"}
     */

    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean implements Serializable{
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
    }
}
