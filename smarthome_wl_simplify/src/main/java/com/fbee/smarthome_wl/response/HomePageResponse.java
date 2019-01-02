package com.fbee.smarthome_wl.response;

import java.util.List;

/**
 * 首页配置
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/1 17:49
 */
public class HomePageResponse extends BaseResponse {
    /**
     * body : {"device_list":[{"type":"10","vendor_name":"feibee","uuid":"528184","note":"Z101"}],"scene_list":[{"uuid":"3","note":"睡眠"}],"slideshow":["https://download.wonlycloud.com/slideshow/banner1.png","https://download.wonlycloud.com/slideshow/banner2.png","https://download.wonlycloud.com/slideshow/banner3.png"]}
     */
    //为确保序列化与反序列化一致，UID必须不可改变
    private static final long serialVersionUID = 1L;
    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean  {
        private  List<DeviceListBean> device_list;
        private  List<SceneListBean> scene_list;
        private List<String> slideshow;

        public List<DeviceListBean> getDevice_list() {
            return device_list;
        }

        public void setDevice_list(List<DeviceListBean> device_list) {
            this.device_list = device_list;
        }

        public List<SceneListBean> getScene_list() {
            return scene_list;
        }

        public void setScene_list(List<SceneListBean> scene_list) {
            this.scene_list = scene_list;
        }

        public List<String> getSlideshow() {
            return slideshow;
        }

        public void setSlideshow(List<String> slideshow) {
            this.slideshow = slideshow;
        }

        public static class DeviceListBean {
            /**
             * type : 10
             * vendor_name : feibee
             * uuid : 528184
             * note : Z101
             */

            private String vendor_name;
            private String uuid;
            private String type;
            private String note;


            public String getVendor_name() {
                return vendor_name;
            }

            public void setVendor_name(String vendor_name) {
                this.vendor_name = vendor_name;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            private String operation;
            public String getOperation() {
                return operation;
            }
            public void setOperation(String operation) {
                this.operation = operation;
            }

        }

        public static class SceneListBean {
            /**
             * uuid : 3
             * note : 睡眠
             */

            private String uuid;
            private String note;

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getNote() {
                return note;
            }

            public void setNote(String note) {
                this.note = note;
            }

            private String operation;
            public String getOperation() {
                return operation;
            }
            public void setOperation(String operation) {
                this.operation = operation;
            }
        }
    }

    /**
     * body : {"homepage_camera":{"vendor_name":"feibee","uuid":"xxxxxxxxxx"},"device_list":[{"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"猫眼","alias":"设设的设备"},{"vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","alias":"备备的设备"}],"scene_list":[{"uuid":"xxxxx","alias":"回家"},{"uuid":"xxxxx","alias":"上班"}]}
     */



}
