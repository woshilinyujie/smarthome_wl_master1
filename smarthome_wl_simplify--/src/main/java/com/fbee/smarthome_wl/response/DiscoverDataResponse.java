package com.fbee.smarthome_wl.response;

import java.util.List;

/**
 * 发现页，展示数据
 * @class name：com.fbee.smarthome_wl.response
 * @anthor create by Zhaoli.Wang
 * @time 2017/11/16 16:40
 */
public class DiscoverDataResponse extends BaseResponse {


    /**
     * body : {"discover_list":[{"img_url":"https://download.wonlycloud.com/slideshow/banner3.png","link_url":"https://detail.tmall.com/item.htm?spm=a1z10.1-b.w5003-17283987398.16.443cf2b5fIM1uK&id=557044357082&scene=taobao_shop","explain":"Z100: 王力智能，更便捷/更安全/更耐用"},{"img_url":"https://download.wonlycloud.com/slideshow/banner3.png","link_url":"https://detail.tmall.com/item.htm?spm=a1z10.1-b.w5003-17283987398.16.443cf2b5fIM1uK&id=557044357082&scene=taobao_shop","explain":"Z100: 王力智能，更便捷/更安全/更耐用"},{"img_url":"https://download.wonlycloud.com/slideshow/banner3.png","link_url":"https://detail.tmall.com/item.htm?spm=a1z10.1-b.w5003-17283987398.16.443cf2b5fIM1uK&id=557044357082&scene=taobao_shop","explain":"Z100: 王力智能，更便捷/更安全/更耐用"},{"img_url":"https://download.wonlycloud.com/slideshow/banner3.png","link_url":"https://detail.tmall.com/item.htm?spm=a1z10.1-b.w5003-17283987398.16.443cf2b5fIM1uK&id=557044357082&scene=taobao_shop","explain":"Z100: 王力智能，更便捷/更安全/更耐用"},{"img_url":"https://download.wonlycloud.com/slideshow/banner3.png","link_url":"https://detail.tmall.com/item.htm?spm=a1z10.1-b.w5003-17283987398.16.443cf2b5fIM1uK&id=557044357082&scene=taobao_shop","explain":"Z100: 王力智能，更便捷/更安全/更耐用"}]}
     */

    private BodyBean body;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        private List<DiscoverListBean> discover_list;

        public List<DiscoverListBean> getDiscover_list() {
            return discover_list;
        }

        public void setDiscover_list(List<DiscoverListBean> discover_list) {
            this.discover_list = discover_list;
        }

        public static class DiscoverListBean {
            /**
             * img_url : https://download.wonlycloud.com/slideshow/banner3.png
             * link_url : https://detail.tmall.com/item.htm?spm=a1z10.1-b.w5003-17283987398.16.443cf2b5fIM1uK&id=557044357082&scene=taobao_shop
             * explain : Z100: 王力智能，更便捷/更安全/更耐用
             */

            private String img_url;
            private String link_url;
            private String explain;

            public String getImg_url() {
                return img_url;
            }

            public void setImg_url(String img_url) {
                this.img_url = img_url;
            }

            public String getLink_url() {
                return link_url;
            }

            public void setLink_url(String link_url) {
                this.link_url = link_url;
            }

            public String getExplain() {
                return explain;
            }

            public void setExplain(String explain) {
                this.explain = explain;
            }
        }
    }
}
