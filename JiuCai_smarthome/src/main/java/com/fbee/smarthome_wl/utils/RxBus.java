/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.fbee.smarthome_wl.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 使用 RxJava 来实现了 EventBus
 */
public class RxBus {
    private static volatile RxBus sRxBus;
    // 主题Subject rxjava中既是 Observable 又是 Observer
    private final Subject<Object, Object> mBus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    //SerializedSubject   最后转化成了 SerializedObserver 一次只会允许一个线程进行发送事物
    //    如果其他线程已经准备就绪，会通知给队列
    //    在发送事物中，不会持有任何锁和阻塞任何线程
    public RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    // 单例RxBus
    public static RxBus getInstance() {
        if (sRxBus == null) {
            synchronized (RxBus.class) {
                if (sRxBus == null) {
                    sRxBus = new RxBus();
                }
            }
        }
        return sRxBus;
    }

    // 提供了一个新的事件
    public void post(Object o) {
        try {
            mBus.onNext(o);
        } catch (Exception e) {
        }

    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }


    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    /**
     * ofType操作符 = filter操作符 + cast操作符
     filter只有符合过滤条件的数据才会被“发射”
     cast将一个Observable转换成指定类型的Observable
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return mBus.ofType(eventType);
    }
}
