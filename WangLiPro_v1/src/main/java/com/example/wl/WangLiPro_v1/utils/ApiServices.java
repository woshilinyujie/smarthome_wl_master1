package com.example.wl.WangLiPro_v1.utils;

import com.example.wl.WangLiPro_v1.bean.Ums;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by wl on 2018/5/30.
 */

public interface ApiServices {

    @POST("http://139.196.221.163:10300")
    Call<JsonObject> Ums(@Body Ums bean);
}
