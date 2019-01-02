package com.fbee.smarthome_wl.api;


import android.content.Context;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;

import com.fbee.smarthome_wl.BuildConfig;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.response.HttpResponse;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ZhaoLi.Wang on 2016/9/26.
 */
public class Api {
    /**
     * 服务器地址
     */
    public static final String BASE_URL = BuildConfig.URL;

//    public static final String BASE_UPDATE_URL ="http://cloud.bmob.cn/8e775204bbfedf14/login";

    // 消息头
    private static final String HEADER_X_W_Client_Type = "X-W-Client-Type";
    private static final String FROM_ANDROID = "android";
    /**
     * 用户代理. 设置消息头
     */
    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 BIDUBrowser/6.x Safari/537.31";
    private static final String USER_AGENT = "User-Agent";

    private static ApiService service;
    private static Retrofit retrofit;

    private static ApiService downlaodservice;
    private static Retrofit downlaodretrofit;

    public static ApiService getService() {
        if (service == null) {
            service = getRetrofit().create(ApiService.class);
        }
        return service;
    }

    public static ApiService getDownloadService() {
        if (downlaodservice == null) {
            downlaodservice = getDownloadRetrofit().create(ApiService.class);
        }
        return downlaodservice;
    }


    /**
     * addInterceptor:设置应用拦截器，主要用于设置公共参数，头信息，日志拦截等
     * addNetworkInterceptor：设置网络拦截器，主要用于重试或重写
     * 拦截器  给所有的请求添加消息头
     */
    private static Interceptor mInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
//                  .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                    .addHeader(HEADER_X_W_Client_Type, FROM_ANDROID)
                    .addHeader(USER_AGENT, userAgent)
                    .build();
            return chain.proceed(request);
        }
    };


    private static Retrofit getDownloadRetrofit() {
        if (downlaodretrofit == null) {
            try {
                // log拦截器  打印所有的log
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        //Set-Cookie: 35e35909c7d922a8198e5909c7d91537
                        LogUtil.i("HttpLogging", message);
                    }
                });
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS) //连接超时
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
//                        .addInterceptor(interceptor)  //应用拦截器
                        .retryOnConnectionFailure(true) //出现错误进行重连
                        .build();

                downlaodretrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl(BASE_URL)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // Rxjava
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return downlaodretrofit;

    }

    /**
     * 获取 Retrofit
     *
     * @return
     */
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            // log拦截器  打印所有的log
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    //Set-Cookie: 35e35909c7d922a8198e5909c7d91537
                    LogUtil.i("HttpLogging", message);
                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //设置 请求的缓存
            File cacheFile = new File(BaseApplication.getInstance().getCacheDir(), "cache");
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb

            try {
                ClearableCookieJar cookieJar =
                        new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(BaseApplication.getInstance().getContext()));
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS) //连接超时
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .addInterceptor(new AddCookiesInterceptor(BaseApplication.getInstance().getContext()))
                        .addInterceptor(new SaveCookiesInterceptor(BaseApplication.getInstance().getContext()))
                        .cookieJar(cookieJar)
                        .addInterceptor(interceptor)  //应用拦截器
                        .addInterceptor(mInterceptor)
                        .retryOnConnectionFailure(true) //出现错误进行重连
                        .cache(cache)
                        .build();

                retrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create()) //gson解析
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // Rxjava
                        .build();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return retrofit;

    }


    /**
     * 对 Observable<T> 做统一的处理，处理了线程调度、分割返回结果等操作组合了起来
     *
     * @param responseObservable
     * @param <T>
     * @return
     */
    protected <T> Observable<T> applySchedulers(Observable<T> responseObservable) {
        return responseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<T, Observable<T>>() {
                    @Override
                    public Observable<T> call(T tResponse) {
                        return flatResponse(tResponse);
                    }
                })
                ;
    }
    /**
     * 对网络接口返回的Response进行分割操作 对于jasn 解析错误以及返回的 响应实体为空的情况
     *
     * @param response
     * @return
     */
    public <T> Observable<T> flatResponse(final T response) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    if (response != null) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(response);
                        }
                    } else {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(new APIException("自定义异常类型", "解析json错误或者服务器返回空的json"));
                        }
                        return;
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }

                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new APIException("自定义异常类型", "解析json错误或者服务器返回空的json"));
                    }
                }

            }
        });
    }


    protected <T> Observable.Transformer<HttpResponse<T>, T> applySchedulers() {
        return (Observable.Transformer<HttpResponse<T>, T>) transformer;
    }

    final Observable.Transformer transformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1() {
                        @Override
                        public Object call(Object response) {
                            return flatResponse((HttpResponse<Object>) response);
                        }
                    })
                    ;
        }
    };


    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     * @param <T>
     * @return
     */
    public <T> Observable<T> flatResponse(final HttpResponse<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {

                if (response == null) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new APIException("自定义异常类型", "解析json错误或者服务器返回空的json"));
                    }
                    return;
                }

                if (response.isSuccess()) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response.data);
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {

                        subscriber.onError(new APIException(response.code, response.msg));
                    }
                    return;
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }

            }
        });
    }


    /**
     * 自定义异常类
     */
    public static class APIException extends Exception {
        public String code;
        public String message;

        public APIException(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

    }


    /**
     * 当{@link ApiService}中接口的注解为{@link retrofit2.http.Multipart}时，参数为{@link RequestBody}
     * 生成对应的RequestBody
     *
     * @param param
     * @return
     */
    protected RequestBody createRequestBody(int param) {
        return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(param));
    }

    protected RequestBody createRequestBody(long param) {
        return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(param));
    }

    protected RequestBody createRequestBody(String param) {
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }

    protected RequestBody createRequestBody(File param) {
        return RequestBody.create(MediaType.parse("image/*"), param);
    }

    protected RequestBody createDataRequestBody(File param) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), param);
    }


    public static String getCamPath() {
        String rootPath = getRootFilePath();
        String camPicPath = rootPath + "WangLi" + File.separator;
        return camPicPath;
    }


    public static String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/";
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/";
        }
    }


    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


    //配置极光
    public static void jpushSetting(Context context, String name) {
        boolean jpushAlarm = PreferencesUtils.getBoolean(name + PreferencesUtils.JPUSH_ALARM);
        boolean jpushCall = PreferencesUtils.getBoolean(name + PreferencesUtils.JPUSH_CALL);
        boolean jpushLock = PreferencesUtils.getBoolean(name + PreferencesUtils.JPUSH_LOCK);
        HashSet<String> stringSet = new HashSet<>();
        String Alarm = name + "__DBAlarm";
        String Call = name + "__DBCall";
        if (jpushAlarm) {
            stringSet.add(Alarm);
        } else {
            stringSet.remove(Alarm);
        }
        if (jpushCall) {
            stringSet.add(Call);
        } else {
            stringSet.remove(Call);
        }
        if (jpushLock) {
            stringSet.add(name);
        } else {
            stringSet.remove(name);
        }
        final Set<String> tags = JPushInterface.filterValidTags(stringSet);
        JPushInterface.setTags(context, tags,
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int arg0, String arg1, Set<String> arg2) {

                    }
                });
    }

    public final static int maxLen = 10;
    /***
     * EditText限制文字5个字母数字10个
     */
    public static final InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int dindex = 0;
            int count = 0;

            while (count <= maxLen && dindex < dest.length()) {
                char c = dest.charAt(dindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > maxLen) {
                return dest.subSequence(0, dindex - 1);
            }

            int sindex = 0;
            while (count <= maxLen && sindex < source.length()) {
                char c = source.charAt(sindex++);
                if (c < 128) {
                    count = count + 1;
                } else {
                    count = count + 2;
                }
            }

            if (count > maxLen) {
                sindex--;
            }

            return source.subSequence(0, sindex);
        }
    };
    /***
     * 将秒转换成时分秒
     * @param time
     * @return
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
