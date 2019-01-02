package com.fbee.smarthome_wl.utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @class name：com.fbee.smarthome_wl.utils
 * @anthor create by Zhaoli.Wang
 * @time 2017/2/9 14:21
 */
public class TransformUtils {
    /**
     * onBackpressureDrop：将observable发送的事件抛弃掉，
     * 直到subscriber再次调用request（n）方法的时候，就发送给它这之后的n个事件。
     */
    public static <T> Observable.Transformer<T, T> defaultSchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .onBackpressureBuffer(1000)
                  //      .onBackpressureDrop()
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
