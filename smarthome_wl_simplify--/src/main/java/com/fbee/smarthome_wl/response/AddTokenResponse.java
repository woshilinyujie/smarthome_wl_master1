package com.fbee.smarthome_wl.response;

import java.io.Serializable;

/**
 * Created by WLPC on 2017/10/10.
 */

public class AddTokenResponse extends BaseResponse implements Serializable {

    /**
     * body : {"token":"xxxxxxxxxxx"}
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
         * token : xxxxxxxxxxx
         */

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
