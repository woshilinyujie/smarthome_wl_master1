package com.fbee.smarthome_wl.response;



/**
 * 与服务端返回格式
 * Created by ZhaoLi.Wang on 2016/9/27.
 */
public class HttpResponse<T> extends  BaseNetBean{

    public T data;

    /**
     * 是否请求成功
     */
    public boolean isSuccess() {
        return code.equals("200");
    }


}
