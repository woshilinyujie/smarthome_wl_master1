package com.fbee.smarthome_wl.response;

/**
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2017/5/11 16:07
 */
public class CodeResponse extends BaseResponse{

    /**
     * body : {"public_key":"-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDf9Nwvc/c4RGCMSyCq00dsfV+r\nfFTd7BFojpCvhWHzq6Psaeqw+NqHZyDYmaJ84zwKoy+pS6Wv0w3lLZ5IMoGpiVG5\nly0fPlITWZKfVrGsWonaXs5wQ5+E5P9O6vTZnYAqtBnSCA0DhugIE1AsUDQIimFF\nbFMxZOKqfL4sGP+ojwIDAQAB\n-----END PUBLIC KEY-----\n3\u0001"}
     */

    private  BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        /**
         * public_key : -----BEGIN PUBLIC KEY-----
         MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDf9Nwvc/c4RGCMSyCq00dsfV+r
         fFTd7BFojpCvhWHzq6Psaeqw+NqHZyDYmaJ84zwKoy+pS6Wv0w3lLZ5IMoGpiVG5
         ly0fPlITWZKfVrGsWonaXs5wQ5+E5P9O6vTZnYAqtBnSCA0DhugIE1AsUDQIimFF
         bFMxZOKqfL4sGP+ojwIDAQAB
         -----END PUBLIC KEY-----
         3
         */

        private String public_key;

        public String getPublic_key() {
            return public_key;
        }

        public void setPublic_key(String public_key) {
            this.public_key = public_key;
        }
    }
}
