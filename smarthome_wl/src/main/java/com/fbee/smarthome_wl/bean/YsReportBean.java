package com.fbee.smarthome_wl.bean;

import java.util.List;

/**
 * Created by linyujie on 19/1/3.
 */

public class YsReportBean  {

    /**
     * UMS : {"header":{"api_version":"1.0","return_string":"RETURN_SUCCESS_OK_STRING","seq_id":"5","http_code":"200","message_type":"MSG_ALARM_DATA_QUERY_RSP"},"body":[{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103105009-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d7873d3af7c0007105260","alarm_message_type":"pic","timestamp":"1546483825"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103103300-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d746fd3af7c000710525f","alarm_message_type":"pic","timestamp":"1546482797"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103103209-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d742ad3af7c000710525e","alarm_message_type":"pic","timestamp":"1546482730"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103102010-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d715ed3af7c000710525d","alarm_message_type":"pic","timestamp":"1546482012"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103093132-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d65ffd3af7c000710525c","alarm_message_type":"pic","timestamp":"1546479101"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103092538-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d6495d3af7c000710525b","alarm_message_type":"pic","timestamp":"1546478740"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103083901-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d59a7d3af7c000710525a","alarm_message_type":"pic","timestamp":"1546475943"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103083800-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d596ad3af7c0007105259","alarm_message_type":"pic","timestamp":"1546475882"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103083216-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d5816d3af7c0007105258","alarm_message_type":"pic","timestamp":"1546475541"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103082924-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d579cd3af7c0007105257","alarm_message_type":"pic","timestamp":"1546475419"}]}
     */

    private UMSBean UMS;

    public UMSBean getUMS() {
        return UMS;
    }

    public void setUMS(UMSBean UMS) {
        this.UMS = UMS;
    }

    public static class UMSBean {
        /**
         * header : {"api_version":"1.0","return_string":"RETURN_SUCCESS_OK_STRING","seq_id":"5","http_code":"200","message_type":"MSG_ALARM_DATA_QUERY_RSP"}
         * body : [{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103105009-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d7873d3af7c0007105260","alarm_message_type":"pic","timestamp":"1546483825"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103103300-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d746fd3af7c000710525f","alarm_message_type":"pic","timestamp":"1546482797"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103103209-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d742ad3af7c000710525e","alarm_message_type":"pic","timestamp":"1546482730"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103102010-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d715ed3af7c000710525d","alarm_message_type":"pic","timestamp":"1546482012"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103093132-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d65ffd3af7c000710525c","alarm_message_type":"pic","timestamp":"1546479101"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103092538-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d6495d3af7c000710525b","alarm_message_type":"pic","timestamp":"1546478740"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103083901-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d59a7d3af7c000710525a","alarm_message_type":"pic","timestamp":"1546475943"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103083800-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d596ad3af7c0007105259","alarm_message_type":"pic","timestamp":"1546475882"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103083216-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d5816d3af7c0007105258","alarm_message_type":"pic","timestamp":"1546475541"},{"alarm_type":"infra","alarm_message":"https://i.ys7.com/streamer/alarm/url/get?fileId=20190103082924-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0","_id":"5c2d579cd3af7c0007105257","alarm_message_type":"pic","timestamp":"1546475419"}]
         */

        private HeaderBean header;
        private List<BodyBean> body;

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public List<BodyBean> getBody() {
            return body;
        }

        public void setBody(List<BodyBean> body) {
            this.body = body;
        }

        public static class HeaderBean {
            /**
             * api_version : 1.0
             * return_string : RETURN_SUCCESS_OK_STRING
             * seq_id : 5
             * http_code : 200
             * message_type : MSG_ALARM_DATA_QUERY_RSP
             */

            private String api_version;
            private String return_string;
            private String seq_id;
            private String http_code;
            private String message_type;

            public String getApi_version() {
                return api_version;
            }

            public void setApi_version(String api_version) {
                this.api_version = api_version;
            }

            public String getReturn_string() {
                return return_string;
            }

            public void setReturn_string(String return_string) {
                this.return_string = return_string;
            }

            public String getSeq_id() {
                return seq_id;
            }

            public void setSeq_id(String seq_id) {
                this.seq_id = seq_id;
            }

            public String getHttp_code() {
                return http_code;
            }

            public void setHttp_code(String http_code) {
                this.http_code = http_code;
            }

            public String getMessage_type() {
                return message_type;
            }

            public void setMessage_type(String message_type) {
                this.message_type = message_type;
            }
        }

        public static class BodyBean {
            /**
             * alarm_type : infra
             * alarm_message : https://i.ys7.com/streamer/alarm/url/get?fileId=20190103105009-C73277890-1-10000-2-1&deviceSerialNo=C73277890&cn=1&isEncrypted=1&isCloudStored=0&ct=1&lc=7&bn=1_hikalarm&isDevVideo=0
             * _id : 5c2d7873d3af7c0007105260
             * alarm_message_type : pic
             * timestamp : 1546483825
             */

            private String alarm_type;
            private String alarm_message;
            private String _id;
            private String alarm_message_type;
            private String timestamp;

            public String getAlarm_type() {
                return alarm_type;
            }

            public void setAlarm_type(String alarm_type) {
                this.alarm_type = alarm_type;
            }

            public String getAlarm_message() {
                return alarm_message;
            }

            public void setAlarm_message(String alarm_message) {
                this.alarm_message = alarm_message;
            }

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }

            public String getAlarm_message_type() {
                return alarm_message_type;
            }

            public void setAlarm_message_type(String alarm_message_type) {
                this.alarm_message_type = alarm_message_type;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }
        }
    }
}
