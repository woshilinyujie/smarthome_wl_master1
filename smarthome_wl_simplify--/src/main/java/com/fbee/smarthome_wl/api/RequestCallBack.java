package com.fbee.smarthome_wl.api;


/**
 * Created by ZhaoLi.Wang on 2016/9/26.
 */
public interface RequestCallBack<T> {
    void onCompleted();
    void onError(Throwable e);
    void onNext(T t);

}
