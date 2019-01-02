package com.example.wl.WangLiPro_v1.utils;

import android.content.Context;

import com.example.wl.WangLiPro_v1.api.AppContext;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wl on 2018/5/30.
 */

public class RetrofitUtils {
    public String BaseURL = "https://www.fbeecloud.com/c2/";


    public Retrofit getRetrofit(Context context) {
//设置 请求的缓存
        File cacheFile = new File(context.getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) //连接超时
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new AddCookiesInterceptor(context))
                .addInterceptor(new SaveCookiesInterceptor(context))
                .cookieJar(cookieJar)
                .retryOnConnectionFailure(true) //出现错误进行重连
                .cache(cache)
                .build();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ApiServices getApi(Context context) {
        return getRetrofit(context).create(ApiServices.class);
    }

}
