package com.fbee.smarthome_wl.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.fbee.smarthome_wl.api.ApiWrapper;
import com.fbee.smarthome_wl.base.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * @class name：com.fbee.smarthome_wl.utils
 * @anthor create by Zhaoli.Wang
 * @time 2017/5/23 11:06
 */
public class DownLoadFileUtil {
    private String url;
    private String  saveUrl;
    private Activity context;
    ApiWrapper mApiWrapper;

    private ProgressDialog progressDialog;//进度条
    public DownLoadFileUtil(Activity context,String url, String saveUrl,ApiWrapper mApiWrapper) {
        this.url = url;
        this.saveUrl = saveUrl;
        this.context = context;
        this.mApiWrapper =mApiWrapper;

//        progressDialog.setIndeterminate(false);//进度条的动画效果（有动画则无进度值）
    }
    int progress =0;
    public void start() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在下载中，请稍候……");
        progressDialog.setMax(100);//进度条最大值
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平样式
        progressDialog.show();
        try {

            Observable<Boolean> call = mApiWrapper.downloadFile(url).
                    observeOn(Schedulers.io()).
                    map(new Func1<ResponseBody, Boolean>() {
                @Override
                public Boolean call(ResponseBody body) {
                    FileUtils.createFile(saveUrl);
                    File futureStudioIconFile = new File(saveUrl);
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        byte[] fileReader = new byte[4096];

                        long fileSize = body.contentLength();
                        long fileSizeDownloaded = 0;

                        inputStream = body.byteStream();
                        outputStream = new FileOutputStream(futureStudioIconFile);

                        while (true) {
                            int read = inputStream.read(fileReader);
                            if (read == -1) {
                                break;
                            }
                            outputStream.write(fileReader, 0, read);
                            fileSizeDownloaded += read;

                            //计算进度
                            progress= (int) ((double) fileSizeDownloaded*100 / fileSize );//先计算出百分比在转换成整型
                            //更新进度
                            progressDialog.setProgress(progress);

                        }

                        outputStream.flush();
                        //将获得的所有字节全部返回
                    } catch (IOException e) {
                        return false;
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }

                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();//关闭对话框
                        }

                    }

                    return true;
                }
            });

            Subscriber subscriber = new Subscriber<Boolean>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    progressDialog.dismiss();//关闭对话框
                    Toast.makeText(BaseApplication.getInstance().getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Boolean body) {
                    progressDialog.dismiss();//关闭对话框
                    if (body) {
                        Intent intent = getPdfFileIntent(saveUrl);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(BaseApplication.getInstance().getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                    }


                }
            };

            call.subscribe(subscriber);

        }catch (Exception e){
            progressDialog.dismiss();//关闭对话框
        }

    }




    public Intent getPdfFileIntent(String path){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri= FileProvider.getUriForFile(context, "com.fbee.smarthome_wl.fileprovider", new File(path));
        } else{
            uri = Uri.fromFile(new File(path));
        }
        i.setDataAndType(uri, "application/pdf");
        return i;
    }






}
