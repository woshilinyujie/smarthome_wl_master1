package com.fbee.smarthome_wl.api;


/**
 * Created by ZhaoLi.Wang on 2016/9/26.
 */
public abstract  class SimpleMyCallBack<T> implements  RequestCallBack<T>{
    @Override
    public void onCompleted() {
    }
    @Override
    public void onError(Throwable e) {
    }

}
