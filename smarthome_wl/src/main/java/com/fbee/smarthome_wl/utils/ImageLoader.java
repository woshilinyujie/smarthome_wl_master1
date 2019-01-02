package com.fbee.smarthome_wl.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.fbee.smarthome_wl.base.BaseApplication;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by ZhaoLi.Wang on 2016/10/28.
 */
public class ImageLoader {

    /**
     * 切换 Picasso  或者  Glide
     *
     * @param context
     * @param uri
     * @param view
     */
    public static void load(Context context, Uri uri, ImageView view) {
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .into(view);

    }

    /**
     * 加载本地图片
     *
     * @param context
     * @param file
     * @param view
     */
    public static void load(Context context, String file, ImageView view) {
        Glide.with(context)
                .load(new File(file))
                .centerCrop()
                .into(view);
    }


    /**
     * @param uri
     * @param view
     * @param errorUrl
     */
    public static void load(Uri uri, ImageView view, int errorUrl, int defaultIv) {

        try{
            Glide.with(BaseApplication.getInstance().getContext())
                    .load(uri)
                    .placeholder(defaultIv) //默认图
                    .centerCrop()
                    .error(errorUrl)  //加载出错是显示
                    .into(view);
        }catch(Exception e){
        }


    }

    /**
     * 加载有错误图，默认图
     *
     * @param context
     * @param uri
     * @param view
     * @param errorImage
     */
    public static void load(Context context, Uri uri, ImageView view, Drawable errorImage) {
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .placeholder(errorImage) //默认图片
                .error(errorImage)    //加载出错图片
                .into(view);
    }


    /**
     * 设置加载优先级  Priority.HIGH    Priority.LOW
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadPriority(Context context, String url, ImageView view, Priority priority) {
        Glide.with(context)
                .load(url)
                .priority(priority)
                .into(view);
    }


    /**
     * Glide清除内存缓存
     */
    public static void clearMemory(Context context) {
        // 必须在UI线程中调用
        Glide.get(context).clearMemory();
    }


    /**
     * 清除磁盘缓存
     */
    public static void clearDiskCache(Context context) {
        // 必须在后台线程中调用，建议同时clearMemory()
        Glide.get(context).clearDiskCache();
    }


    /**
     * 加载圆形图
     *
     * @param context
     * @param uri
     * @param view
     * @param errorUrl
     */
    public static void loadCropCircle(Context context, String uri, ImageView view, int errorUrl) {
        Glide.with(context)
                .load(uri)
                .bitmapTransform(new CropCircleTransformation(context))
                .error(errorUrl)
                .into(view);
    }


    /**
     * 圆角处理
     *
     * @param context
     * @param uri
     * @param view
     * @param errorUrl
     */
    public static void loadRoundedCorners(Context context, String uri, int radius, int margin, ImageView view, int errorUrl) {
        //圆角处理
        Glide.with(context)
                .load(uri)
                .bitmapTransform(new RoundedCornersTransformation(context, radius, margin, RoundedCornersTransformation.CornerType.ALL))
                .error(errorUrl)
                .into(view);
    }


    /**
     * 灰度处理
     *
     * @param context
     * @param uri
     * @param view
     * @param errorUrl
     */
    public static void loadGrayscale(Context context, String uri, ImageView view, int errorUrl) {
        //灰度处理
        Glide.with(context)
                .load(uri)
                .bitmapTransform(new GrayscaleTransformation(context))
                .error(errorUrl)
                .into(view);
    }

    public static void loadResourceReady(Context context, Uri uri, int errorUrl, GlideDrawableImageViewTarget onResourceReady) {
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .error(errorUrl)
                .crossFade()
                .into(onResourceReady);

    }


}
