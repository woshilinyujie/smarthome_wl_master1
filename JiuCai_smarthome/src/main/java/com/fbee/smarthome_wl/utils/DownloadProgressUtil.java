package com.fbee.smarthome_wl.utils;

import android.os.Message;
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
 * @time 2017/8/7 18:07
 */
public class DownloadProgressUtil {
    private String url;
    private String  saveUrl;
    ApiWrapper mApiWrapper;
    int progress =0;
    private WeakHandler mhandler;
    DownloadListener mListener;
    public DownloadProgressUtil(String url, WeakHandler mhandler,String saveUrl, ApiWrapper mApiWrapper, DownloadListener mListener) {
        this.url = url;
        this.saveUrl = saveUrl;
        this.mhandler = mhandler;
        this.mApiWrapper = mApiWrapper;
        this.mListener = mListener;

    }

    public void start(){
        try {
            Observable<Boolean> call = mApiWrapper.downloadFile(url).
                    observeOn(Schedulers.newThread()).
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
                                    progress = (int) ((double) fileSizeDownloaded * 100 / fileSize);//先计算出百分比在转换成整型

                                    Message msg = Message.obtain();
                                    msg.what =8;
                                    msg.arg1 =progress;
                                    mhandler.sendMessage(msg);

                                }

                                outputStream.flush();
                                //将获得的所有字节全部返回
                            } catch (IOException e) {
                                if(mListener != null)
                                    mListener.onFail();
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
                                    if(mListener != null)
                                        mListener.onFail();
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
                    if(mListener != null)
                        mListener.onFail();
                    Toast.makeText(BaseApplication.getInstance().getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Boolean b) {
                    if (b) {
                        if(mListener != null)
                            mListener.onSuccess();
                    } else {
                        if(mListener != null)
                            mListener.onFail();

                    }

                }
            };

            call.subscribe(subscriber);

        }catch (Exception e){
            if(mListener != null)
                mListener.onFail();
        }


    }


    public interface  DownloadListener{
        void onSuccess();

        void onFail();

    }



}
