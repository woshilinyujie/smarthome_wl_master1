package com.fbee.smarthome_wl.response;

import java.io.Serializable;

/**
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2017/3/31 10:56
 */
public class MessgaeCode extends BaseResponse implements Serializable{

    /**
     * body : {"secret_key":"xxxxxxxxx"}
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
         * secret_key : xxxxxxxxx
         */

        private String secret_key;

        public String getSecret_key() {
            return secret_key;
        }

        public void setSecret_key(String secret_key) {
            this.secret_key = secret_key;
        }
    }
}
