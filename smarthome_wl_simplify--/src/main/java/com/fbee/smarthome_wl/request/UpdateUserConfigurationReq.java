package com.fbee.smarthome_wl.request;

import com.fbee.smarthome_wl.response.HomePageResponse;

import java.util.List;

/**
 * 首页更新用户配置
 * @class name：com.fbee.smarthome_wl.request
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/10 8:46
 */
public class UpdateUserConfigurationReq{


    /**
     * body : {"isfull":"true","gateway":{"vendor_name":"feibee","uuid":"xxxxxxxxxx"},"homepage_camera":{"operation":"delete","vendor_name":"feibee","uuid":"xxxxxxxxxx"},"device_list":[{"operation":"modify","vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"猫眼","alias":"设设的设备"},{"operation":"add","vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","alias":"备备的设备"}],"scene_list":[{"operation":"modify","uuid":"xxxxx","alias":"回家"},{"operation":"delete","uuid":"xxxxx","alias":"上班"}]}
     */

//    private BodyBean body;
    /**
     * isfull : true
     * gateway_vendor_name : feibee
     * gateway_uuid : xxxxxxxxxx
     * homepage_camera : {"operation":"delete","vendor_name":"feibee","uuid":"xxxxxxxxxx"}
     * device_list : [{"operation":"modify","vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"猫眼","note":"设设的设备"},{"operation":"add","vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","note":"备备的设备"}]
     * scene_list : [{"operation":"modify","uuid":"xxxxx","note":"回家"},{"operation":"delete","uuid":"xxxxx","note":"上班"}]
     */

    private String isfull;
    private String gateway_vendor_name;
    private String gateway_uuid;
    private List<HomePageResponse.BodyBean.DeviceListBean> device_list;
    private List<HomePageResponse.BodyBean.SceneListBean> scene_list;
    /**
     * homepage_camera : {"operation":"delete","vendor_name":"feibee","uuid":"xxxxxxxxxx"}
     */


//    public BodyBean getBody() {
//        return body;
//    }
//
//    public void setBody(BodyBean body) {
//        this.body = body;
//    }

    public String getIsfull() {
        return isfull;
    }

    public void setIsfull(String isfull) {
        this.isfull = isfull;
    }

    public String getGateway_vendor_name() {
        return gateway_vendor_name;
    }

    public void setGateway_vendor_name(String gateway_vendor_name) {
        this.gateway_vendor_name = gateway_vendor_name;
    }

    public String getGateway_uuid() {
        return gateway_uuid;
    }

    public void setGateway_uuid(String gateway_uuid) {
        this.gateway_uuid = gateway_uuid;
    }


    public List<HomePageResponse.BodyBean.DeviceListBean> getDevice_list() {
        return device_list;
    }

    public void setDevice_list(List<HomePageResponse.BodyBean.DeviceListBean> device_list) {
        this.device_list = device_list;
    }

    public List<HomePageResponse.BodyBean.SceneListBean> getScene_list() {
        return scene_list;
    }

    public void setScene_list(List<HomePageResponse.BodyBean.SceneListBean> scene_list) {
        this.scene_list = scene_list;
    }



//    public static class BodyBean {
//        /**
//         * isfull : true
//         * gateway : {"vendor_name":"feibee","uuid":"xxxxxxxxxx"}
//         * homepage_camera : {"operation":"delete","vendor_name":"feibee","uuid":"xxxxxxxxxx"}
//         * device_list : [{"operation":"modify","vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"猫眼","alias":"设设的设备"},{"operation":"add","vendor_name":"feibee","uuid":"xxxxxxxxxx","type":"门锁","alias":"备备的设备"}]
//         * scene_list : [{"operation":"modify","uuid":"xxxxx","alias":"回家"},{"operation":"delete","uuid":"xxxxx","alias":"上班"}]
//         */
//
//        private String isfull;
//        private GatewayBean gateway;
//        private HomePageResponse.BodyBean.HomepageCameraBean homepage_camera;
//        private List<HomePageResponse.BodyBean.DeviceListBean> device_list;
//        private List<HomePageResponse.BodyBean.SceneListBean> scene_list;
//
//        public String getIsfull() {
//            return isfull;
//        }
//
//        public void setIsfull(String isfull) {
//            this.isfull = isfull;
//        }
//
//        public GatewayBean getGateway() {
//            return gateway;
//        }
//
//        public void setGateway(GatewayBean gateway) {
//            this.gateway = gateway;
//        }
//
//        public HomePageResponse.BodyBean.HomepageCameraBean getHomepage_camera() {
//            return homepage_camera;
//        }
//
//        public void setHomepage_camera(HomePageResponse.BodyBean.HomepageCameraBean homepage_camera) {
//            this.homepage_camera = homepage_camera;
//        }
//
//        public List<HomePageResponse.BodyBean.DeviceListBean> getDevice_list() {
//            return device_list;
//        }
//
//        public void setDevice_list(List<HomePageResponse.BodyBean.DeviceListBean> device_list) {
//            this.device_list = device_list;
//        }
//
//        public List<HomePageResponse.BodyBean.SceneListBean> getScene_list() {
//            return scene_list;
//        }
//
//        public void setScene_list(List<HomePageResponse.BodyBean.SceneListBean> scene_list) {
//            this.scene_list = scene_list;
//        }
//
//        public static class GatewayBean {
//            /**
//             * vendor_name : feibee
//             * uuid : xxxxxxxxxx
//             */
//
//            private String vendor_name;
//            private String uuid;
//
//            public String getVendor_name() {
//                return vendor_name;
//            }
//
//            public void setVendor_name(String vendor_name) {
//                this.vendor_name = vendor_name;
//            }
//
//            public String getUuid() {
//                return uuid;
//            }
//
//            public void setUuid(String uuid) {
//                this.uuid = uuid;
//            }
//        }
//
//        public static class HomepageCameraBean {
//            /**
//             * operation : delete
//             * vendor_name : feibee
//             * uuid : xxxxxxxxxx
//             */
//
//
//            private String vendor_name;
//            private String uuid;
//            private String operation;
//            public String getOperation() {
//                return operation;
//            }

//
//            public void setOperation(String operation) {
//                this.operation = operation;
//            }
//
//            public String getVendor_name() {
//                return vendor_name;
//            }
//
//            public void setVendor_name(String vendor_name) {
//                this.vendor_name = vendor_name;
//            }
//
//            public String getUuid() {
//                return uuid;
//            }
//
//            public void setUuid(String uuid) {
//                this.uuid = uuid;
//            }
//        }
//
//        public static class DeviceListBean {
//            /**
//             * operation : modify
//             * vendor_name : feibee
//             * uuid : xxxxxxxxxx
//             * type : 猫眼
//             * alias : 设设的设备
//             */
//
//            private String operation;
//            private String vendor_name;
//            private String uuid;
//            private String type;
//            private String alias;
//
//            public String getOperation() {
//                return operation;
//            }
//
//            public void setOperation(String operation) {
//                this.operation = operation;
//            }
//
//            public String getVendor_name() {
//                return vendor_name;
//            }
//
//            public void setVendor_name(String vendor_name) {
//                this.vendor_name = vendor_name;
//            }
//
//            public String getUuid() {
//                return uuid;
//            }
//
//            public void setUuid(String uuid) {
//                this.uuid = uuid;
//            }
//
//            public String getType() {
//                return type;
//            }
//
//            public void setType(String type) {
//                this.type = type;
//            }
//
//            public String getAlias() {
//                return alias;
//            }
//
//            public void setAlias(String alias) {
//                this.alias = alias;
//            }
//        }
//
//        public static class SceneListBean {
//            /**
//             * operation : modify
//             * uuid : xxxxx
//             * alias : 回家
//             */
//
//            private String operation;
//            private String uuid;
//            private String alias;
//
//            public String getOperation() {
//                return operation;
//            }
//
//            public void setOperation(String operation) {
//                this.operation = operation;
//            }
//
//            public String getUuid() {
//                return uuid;
//            }
//
//            public void setUuid(String uuid) {
//                this.uuid = uuid;
//            }
//
//            public String getAlias() {
//                return alias;
//            }
//
//            public void setAlias(String alias) {
//                this.alias = alias;
//            }
//        }
//    }




}
