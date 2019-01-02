package com.fbee.smarthome_wl.base;

import android.text.TextUtils;

import com.fbee.smarthome_wl.BuildConfig;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.api.ApiWrapper;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.constant.StatusCode;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.ToastUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ZhaoLi.Wang on 2016/9/26.
 */
public class BaseCommonPresenter<T extends BaseView> {
    /**
     * Api类的包装 对象
     */
    protected ApiWrapper mApiWrapper;
    /**
     * 使用CompositeSubscription来持有所有的Subscriptions
     */
    protected CompositeSubscription mCompositeSubscription;

    public T view;

    public static final AtomicInteger mSeqid= new AtomicInteger(1);

    public BaseCommonPresenter(T view) {
        //创建 CompositeSubscription 对象 使用CompositeSubscription来持有所有的Subscriptions，然后在onDestroy()或者onDestroyView()里取消所有的订阅。
        mCompositeSubscription = new CompositeSubscription();
        // 构建 ApiWrapper 对象
        mApiWrapper = new ApiWrapper();
        this.view  = view;
    }


    /**
     * 创建观察者  过滤出错误信息
     * @param onNext
     * @param <E>
     * @return
     */
    protected  <E> Subscriber newMySubscriber(final SimpleMyCallBack onNext) {
        return new Subscriber<E>() {
            @Override
            public void onCompleted() {
                if(view != null){
                    view.hideLoading();
                }
                onNext.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e("测试","==============");
                String msg = e.getMessage();
                if (e instanceof Api.APIException) {
                    Api.APIException exception = (Api.APIException) e;
                    if (view != null) {
                        ToastUtils.showShort(exception.message);
                    }
                } else if (e instanceof HttpException) {
                    if (e instanceof HttpException) {

                        try {
                        HttpException httpException = (HttpException) e;
                        if(TextUtils.isEmpty(httpException.getMessage())){
                            ToastUtils.showShort("服务器错误");
                        }else {
                            String errorMsg = httpException.getMessage();
                            if(TextUtils.isEmpty(errorMsg)){
                                ToastUtils.showShort("服务器错误");
                            }else {
                                ToastUtils.showShort(errorMsg);
                            }

                        }
                        }  catch (Exception IOe) {
                            ToastUtils.showShort("服务器异常");
                        }
//                        ResponseBody body = ((HttpException) e).response().errorBody();
//                        try {
//                            String json = body.string();
//                            Gson gson = new Gson();
//                            HttpExceptionBean mHttpExceptionBean = gson.fromJson(json, HttpExceptionBean.class);
//                            if (mHttpExceptionBean != null && mHttpExceptionBean.getMessage() != null) {
//                                ToastUtils.showShort(mHttpExceptionBean.getMessage());
//
//                            }
//                        }  catch (Exception IOe) {
//                            ToastUtils.showShort("服务器异常");
//                        }

                    }
                }else if (e instanceof SocketTimeoutException) {
                 ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.SERV_NET_TIME_OUT));
                } else if (e instanceof ConnectException) {
                    ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.INTERNAL_SERVER_ERROR));
                }else if (e instanceof SocketException && ("Socket closed".equals(msg) || "sendto failed: EBADF (Bad file number)".equals(msg))) {
                    ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.CANCEL));
                } else if (e instanceof IOException) {
                    if ("Canceled".equals(msg)) {
                        ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.CANCEL));
                    } else {
                        ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.INTERNAL_SERVER_ERROR));
                    }
                }else if (e instanceof IllegalAccessException) {
                    ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.SERV_ILLEGAL_ERROR));
                }
                else if(e instanceof ClassCastException){
                    ToastUtils.showShort("数据解析异常");
                }
                else {
                    LogUtil.e("exceptionMsg",e.getMessage());
                    if(BuildConfig.DEBUG)
                    ToastUtils.showShort("未知异常");
                }
                if (view != null) {
                    view.hideLoading();
                }
                if (!mCompositeSubscription.isUnsubscribed()) {
                    onNext.onError(e);
                }

            }

            @Override
            public void onNext(E t) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    onNext.onNext(t);
                }
            }
        };

    }


    protected <E> Ums getUms(String type,E bodyBean ){
        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type(type);
        header.setSeq_id(mSeqid.getAndIncrement()+"");
        Ums ums = new Ums();
        UMSBean umsbean=new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyBean);
        ums.setUMS(umsbean);

        return ums;
    }


    /**
     * 解绑 CompositeSubscription
     */
    public void unsubscribe() {
        if(mCompositeSubscription != null){
            mCompositeSubscription.unsubscribe();
        }
    }


}
