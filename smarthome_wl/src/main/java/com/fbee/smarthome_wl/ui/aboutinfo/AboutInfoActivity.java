package com.fbee.smarthome_wl.ui.aboutinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.GateWayInfo;
import com.fbee.smarthome_wl.bean.Pus;
import com.fbee.smarthome_wl.bean.UMSBean;
import com.fbee.smarthome_wl.common.ActivityPageManager;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.UpdateProgressEvevt;
import com.fbee.smarthome_wl.request.PusBodyBean;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.UpdataApkResponse;
import com.fbee.smarthome_wl.ui.webview.WebViewActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.DownloadProgressUtil;
import com.fbee.smarthome_wl.utils.FileUtils;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.utils.WeakHandler;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.utils.AppUtil.getPackageInfo;
import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

public class AboutInfoActivity extends BaseActivity implements Handler.Callback{
    private ImageView back;
    private TextView title;
    private TextView iconTitleAboutInfo;
    private RelativeLayout softwareUpdateRelative;
    private RelativeLayout useingHelpRelative;
    private LoginResult.BodyBean.GatewayListBean gw;
    private String versionCode;
    private TextView gwVersion;
    private RelativeLayout versionInfoRelative;
    private ProgressDialog mDialog;
    private WeakHandler handler;
    private Subscription subInterval;
    ProgressDialog pdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_info);

    }

    @Override
    protected void initView() {
        initApi();
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        iconTitleAboutInfo = (TextView) findViewById(R.id.icon_title_about_info);
        softwareUpdateRelative = (RelativeLayout) findViewById(R.id.software_update_relative);
        useingHelpRelative = (RelativeLayout) findViewById(R.id.useing_help_relative);
        gwVersion = (TextView) findViewById(R.id.gw_version);
        versionInfoRelative = (RelativeLayout) findViewById(R.id.version_info_relative);
        initHander();

    }

    private void initHander() {
        handler = new WeakHandler(this);

    }


    @Override
    protected void initData() {
        title.setText("关于与帮助");
//        gw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+PreferencesUtils.getString(LOCAL_USERNAME));
//        if(gw !=null){
//            String version = gw.getVersion();
//            gwVersion.setText(version);
//        }
        receiveGateWayInfo();
        AppContext.getInstance().getSerialInstance().getGateWayInfo();
        //升级回调
        Subscription subscription = RxBus.getInstance().toObservable(UpdateProgressEvevt.class)
                .compose(TransformUtils.<UpdateProgressEvevt>defaultSchedulers())
                .subscribe(new Action1<UpdateProgressEvevt>() {
                    @Override
                    public void call(final UpdateProgressEvevt groupInfo) {
                        ThreadPoolUtils.execute(new Runnable() {
                            @Override
                            public void run() {
                                showProgressDialog(groupInfo.getUpgradedata());
                            }
                        });

                    }
                });
        mCompositeSubscription.add(subscription);

        mDialog = new ProgressDialog(this);
        mDialog.setMax(100);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        versionCode = getPackageInfo(this).versionName;
        iconTitleAboutInfo.setText("王力智能v" + versionCode);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        softwareUpdateRelative.setOnClickListener(this);
        useingHelpRelative.setOnClickListener(this);
        versionInfoRelative.setOnClickListener(this);
    }


    /**
     * 接收网关信息
     */
    private void receiveGateWayInfo() {
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                receiveGateWayInfo();
            }
        };
        //注册RXbus接收arriveReport_CallBack（）回调中的数据
        Subscription gateWaySubscription = RxBus.getInstance().toObservable(GateWayInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GateWayInfo>() {
                    @Override
                    public void call(GateWayInfo event) {
                        if (event == null) return;
                        gwVersion.setText(new String(event.getVer()));

                        if(subInterval !=null && !subInterval.isUnsubscribed() ){
                            subInterval.unsubscribe();
                            if(progressDialog !=null)
                                progressDialog.dismiss();
                            showToast("网关升级成功！");
                        }


                    }
                }, onErrorAction);
        mCompositeSubscription.add(gateWaySubscription);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            //升级软件
            case R.id.software_update_relative:
                requestUpdate();
                break;
            //使用帮助
            case R.id.useing_help_relative:
                skipAct(WebViewActivity.class);
                break;
            //网关版本
            case R.id.version_info_relative:
                requestGwUpdate();
                break;


        }
    }


    private void showProgressDialog(final int progress) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress <= 100) {
                    mDialog.setTitle("正在下载中...\n切勿切断网关电源");
                    mDialog.setProgress(progress);

                } else if (progress > 100 && progress < 200) {
                    mDialog.setTitle("正在更新...");
                    mDialog.setProgress(progress - 100);
                }
                mDialog.show();
                if (progress >= 200) {
                    mDialog.dismiss();
                    showToast("升级完成");
                    AppContext.getInstance().getSerialInstance().getGateWayInfo();
                }

            }
        });


    }


    /**
     * 检查是否有更新
     *
     * @author ZhaoLi.Wang
     * @date 2016-12-6 上午11:35:44
     */
    private void requestGwUpdate() {
        if (TextUtils.isEmpty(gwVersion.getText().toString()))
            return;

        showLoadingDialog(null);
        Subscriber subscriber = new Subscriber<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(JsonObject json) {
                try {
                    if (null != json) {
                        hideLoadingDialog();
                        JsonObject jsonObj = json.getAsJsonObject("PUS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        UpdataApkResponse updateinfo = new Gson().fromJson(jsonObj.toString(), UpdataApkResponse.class);
                        String responseCode = updateinfo.getHeader().getHttp_code();
                        if ("200".equals(responseCode)) {
                            String newVersion = updateinfo.getBody().getNew_version();
                            int falg = newVersion.compareTo(gwVersion.getText().toString());
                            //有新版本
                            if (falg > 0) {
                                if (gwVersion.getText().toString().compareTo("2.2.5") <= 0) {
                                    showupdateDialog(true,updateinfo.getBody().getUrl());
                                }else{
                                    showupdateDialog(updateinfo.getBody().getUrl());
                                }


                            } else {
                                showToast("已是最新版本");
                            }

                        } else {
                            showToast("获取网关信息失败");
                        }

                    }
                } catch (Exception e) {

                }
            }
        };

        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        header.setSeq_id("1");
        Pus pus = new Pus();
        PusBodyBean bodyBean = new PusBodyBean();
        bodyBean.setToken("");
        bodyBean.setProduct_name("wonly");
        bodyBean.setPlatform("gateway");
        bodyBean.setEndpoint_type(FactoryType.FBEE);
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyBean);
        pus.setPUS(umsbean);


        Subscription subscription = mApiWrapper.updateVersion(pus).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }



    private void showupdateDialog(final boolean isMust, final String url) {
        String cancel_txt = "";
        if (isMust) {
            cancel_txt = "退出";
        } else {
            cancel_txt = "暂时不";
        }
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("网关需要升级，不消耗额外流量").cancelable(false).title("更新提示")
                .leftBtnText(cancel_txt).Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("更新");
        DialogManager.showDialog(AboutInfoActivity.this, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onDismiss() {
                super.onDismiss();
            }

            @Override
            public void onRightClick() {
                    if ("WIFI".equals(AppUtil.getNetworkType(AboutInfoActivity.this))) {
                        pdialog = new ProgressDialog(AboutInfoActivity.this);
                        pdialog.setMessage("正在连接网关...");
                        pdialog.show();
                        ThreadPoolUtils.execute(new Runnable() {
                            @Override
                            public void run() {
                                int number = AppContext.getInstance().getSerialInstance().connectLANZll();
                                if (number > 0) {
                                    final String[] ips = AppContext.getInstance().getSerialInstance().getGatewayIps(number);
                                    final String[] snids = AppContext.getInstance().getSerialInstance().getBoxSnids(number);
                                    final LoginResult.BodyBean.GatewayListBean currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                                    boolean flag= true;
                                    for (int i = 0; i < ips.length; i++) {
                                        if (currentGw.getUuid().equals(snids[i])) {
                                            flag = false;
                                            LoginLocalGateway(snids[i], ips[i], url);
                                        }
                                    }
                                    if(flag){
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AboutInfoActivity.this,"本地连接网关失败，请确保手机与网关在相同局域网",Toast.LENGTH_LONG).show();
                                                if(null != pdialog)
                                                    pdialog.dismiss();
                                                AppContext.getInstance().getSerialInstance().connectRemoteZll(currentGw.getUsername(), currentGw.getPassword());

                                            }
                                        });
                                    }

                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast("本地连接网关失败，请确保手机与网关在相同局域网");
                                            if(null != pdialog)
                                                pdialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        showToast("本地连接网关失败，请确保手机与网关在相同局域网");
                    }


            }

            @Override
            public void onLeftClick() {
                if (isMust) {
                    finish();
                    ActivityPageManager.getInstance().finishAllActivity();
                    System.exit(0);
                }
            }
        });
    }


    /**
     * 登录本地网关
     */
    private void LoginLocalGateway(final String snid, final String ip, final String url) {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int result = AppContext.getInstance().getSerialInstance().connectLANZllByIp(ip, snid);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer ret) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != pdialog)
                            pdialog.dismiss();
                    }
                });

                if (ret > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("本地连接成功.");
                        }
                    });
                    return true;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("本地连接网关失败，请确保手机与网关在相同局域网");
                        }
                    });
                    LoginResult.BodyBean.GatewayListBean currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));
                    AppContext.getInstance().getSerialInstance().connectRemoteZll(currentGw.getUsername(), currentGw.getPassword());
                    return false;
                }
            }
        }).observeOn(Schedulers.io())
                .flatMap(new Func1<Integer, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Integer ret) {
                        return downloadBin(url);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(null != pdialog)
                            pdialog.dismiss();
                        if (null != progressDialog)
                            progressDialog.dismiss();//关闭对话框
                        showToast("网关升级出错！");
                    }

                    @Override
                    public void onNext(Boolean b) {
                        if (b) {
                            showPross();
                        } else {
                            progressDialog.dismiss();//关闭对话框
                            showToast("网关升级出错！");
                        }
                    }
                });
        mCompositeSubscription.add(sub);
    }


    private ProgressDialog progressDialog;//进度条
    int progress = 0;

    /**
     * 下载bin 同时发送给网关 进行升级
     */
    private Observable<Boolean> downloadBin(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AboutInfoActivity.this);
                progressDialog.setMessage("正在往网关发升级包，\n切勿关闭APP，切勿关闭网关电源");
                progressDialog.setMax(100);//进度条最大值
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平样式
                progressDialog.show();
            }
        });

        Observable<Boolean> call = mApiWrapper.downloadFile(url).
                observeOn(Schedulers.io()).
                map(new Func1<ResponseBody, Boolean>() {
                    @Override
                    public Boolean call(ResponseBody body) {
                        InputStream inputStream = null;
                        FileInputStream stream = null;
                        File directory = Environment.getExternalStorageDirectory();
                        String fileName = url.substring(url.lastIndexOf("/") + 1);
                        final String filePath = directory.getAbsolutePath() + "/WangLi/" + fileName;
                        File file = new File(directory.getAbsolutePath() + "/WangLi");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        FileUtils.createFile(filePath);
                        OutputStream outputStream = null;
                        try {
                            byte[] fileReader = new byte[4096];
                            long fileSize = body.contentLength();
                            long fileSizeDownloaded = 0;
                            inputStream = body.byteStream();
                            outputStream = new FileOutputStream(filePath);
                            while (true) {
                                int read = inputStream.read(fileReader);
                                if (read == -1) {
                                    break;
                                }
                                fileSizeDownloaded += read;
                                outputStream.write(fileReader, 0, read);

                                //计算进度
                                progress= (int) ((double) fileSizeDownloaded*100 / fileSize );//先计算出百分比在转换成整型
                                Message msg = Message.obtain();
                                msg.what = 5;
                                msg.arg1 = progress;
                                handler.sendMessage(msg);
                            }
                            outputStream.flush();
                            int bytcnt11 = 256;
                            stream = new FileInputStream(filePath);
                            long fCRC = 0;
                            long total = 0;
                            byte[] upbyte = new byte[bytcnt11 + 5];
                            while (true) {
                                int upread = stream.read(upbyte, 5, bytcnt11);
                                if (upread == -1) {
                                    break;
                                }
                                //计算进度
                                progress = (int) ((double) total * 100 / fileSize);//先计算出百分比在转换成整型
                                Message msg = Message.obtain();
                                msg.what = 2;
                                msg.arg1 = progress;
                                handler.sendMessage(msg);
                                for (int i2 = 5; i2 < upread + 5; i2++) {
                                    fCRC += ((long) upbyte[i2]) & 0xff;
                                }
                                AppContext.getInstance().getSerialInstance().upd(upbyte, upread, total);
//                                total += upread;
                                total += (long) bytcnt11;
                                LogUtil.e("total=", total + "/" + fileSize);
                                try {
                                    Thread.sleep(50);
                                    LogUtil.e("Thread", Thread.currentThread().getName());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            AppContext.getInstance().getSerialInstance().updStart(fCRC);
                            //将获得的所有字节全部返回
                        } catch (IOException e) {
                            return false;
                        } finally {
                            try {
                                if (stream != null) {
                                    stream.close();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            } catch (Exception e) {
                                progressDialog.dismiss();//关闭对话框
                                return false;
                            }
                        }
                        return true;
                    }
                });
        return call;
    }





    private void showupdateDialog(final String url) {
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("发现新版本网关固件是否更新？").cancelable(false).title("更新提示")
                .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("更新");
        DialogManager.showDialog(AboutInfoActivity.this, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onDismiss() {
                super.onDismiss();
            }

            @Override
            public void onRightClick() {
                ThreadPoolUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppContext.getInstance().getSerialInstance().upgradeGatewayGD(url);

                    }
                });


            }

            @Override
            public void onLeftClick() {

            }
        });

    }


    /**
     * 检查是否有更新
     *
     * @author ZhaoLi.Wang
     * @date 2016-12-6 上午11:35:44
     */
    private void requestUpdate() {
        showLoadingDialog(null);
        Subscriber subscriber = new Subscriber<JsonObject>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(JsonObject json) {
                try {
                    if (null != json) {
                        hideLoadingDialog();
                        JsonObject jsonObj = json.getAsJsonObject("PUS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        UpdataApkResponse updateinfo = new Gson().fromJson(jsonObj.toString(), UpdataApkResponse.class);
                        String responseCode = updateinfo.getHeader().getHttp_code();
                        if ("200".equals(responseCode)) {
                            String newVersion = updateinfo.getBody().getNew_version();
                            int falg = newVersion.compareTo(AppUtil.getAppVersionName(AboutInfoActivity.this));
                            //有新版本
                            if (falg > 0) {
                                boolean isMust = false;
                                //是否为强制更新  true 为强制更新
                                if (updateinfo.getBody().getForce_upgrade().equals("true")) {
                                    isMust = true;
                                }
                                showDialog(isMust, updateinfo.getBody().getUrl(), updateinfo.getBody().getReadme());

                            } else {
                                showToast("已是最新版本");
                            }

                        } else {
                            ToastUtils.showShort(RequestCode.getRequestCode(updateinfo.getHeader().getReturn_string()));
                        }

                    }
                } catch (Exception e) {

                }
            }
        };

        UMSBean.HeaderBean header = new UMSBean.HeaderBean();
        header.setApi_version("1.0");
        header.setMessage_type("MSG_PRODUCT_UPGRADE_DOWN_REQ");
        header.setSeq_id("1");
        Pus pus = new Pus();
        PusBodyBean bodyBean = new PusBodyBean();
        bodyBean.setToken("");
        bodyBean.setProduct_name("wonly");
        bodyBean.setPlatform("android");
        bodyBean.setEndpoint_type("general");
        UMSBean umsbean = new UMSBean();
        umsbean.setHeader(header);
        umsbean.setBody(bodyBean);
        pus.setPUS(umsbean);


        Subscription subscription = mApiWrapper.updateVersion(pus).subscribe(subscriber);
        mCompositeSubscription.add(subscription);
    }


    ProgressDialog  progressApkDialog;
    /**
     * isMust 是否为强制更新
     * apkUrl apk下载路径
     *
     * @author ZhaoLi.Wang
     * @date 2016-12-6 上午11:49:03
     */
    private void showDialog(final boolean isMust, final String apkUrl, String msg) {
        String cancel_txt = "";
        String content_txt = "有新版本是否更新？";
        if (isMust) {
            cancel_txt = "退出";
        } else {
            cancel_txt = "暂时不";
        }

        if (msg != null && msg.length() > 0) {
            content_txt = msg;
        }

        DialogManager.Builder builder = new DialogManager.Builder()
                .msg(content_txt).cancelable(false).title("更新提示")
                .leftBtnText(cancel_txt).Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("更新");

        DialogManager.showDialog(AboutInfoActivity.this, builder, new DialogManager.ConfirmDialogListener() {

            @Override
            public void onDismiss() {
                super.onDismiss();
            }

            @Override
            public void onRightClick() {

                progressApkDialog = new ProgressDialog(AboutInfoActivity.this);
                progressApkDialog.setMessage("正在下载中，请稍候……");
                progressApkDialog.setMax(100);//进度条最大值
                progressApkDialog.setCancelable(false);
                progressApkDialog.setCanceledOnTouchOutside(false);
                progressApkDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平样式
                progressApkDialog.show();
                File directory= Environment.getExternalStorageDirectory();
                final String saveurl = directory.getAbsolutePath() + "/WangLi/" + "smarthome_wl.apk";
                new DownloadProgressUtil(apkUrl, handler,saveurl, mApiWrapper, new DownloadProgressUtil.DownloadListener() {
                    @Override
                    public void onSuccess() {
                        if(progressApkDialog !=null)
                            progressApkDialog.dismiss();
                        Intent apkIntent = new Intent(Intent.ACTION_VIEW);
                        apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        apkIntent.setAction(Intent.ACTION_VIEW);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            apkIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = FileProvider.getUriForFile(AboutInfoActivity.this, "com.fbee.smarthome_wl.fileprovider", new File(saveurl));
                            apkIntent.setDataAndType(uri,
                                    "application/vnd.android.package-archive");
                        } else {
                            apkIntent.setDataAndType(Uri.fromFile(new File(saveurl)),
                                    "application/vnd.android.package-archive");
                        }
                        startActivity(apkIntent);
                        Process.killProcess(Process.myPid());
                    }

                    @Override
                    public void onFail() {
                        if(progressApkDialog !=null)
                            progressApkDialog.dismiss();
                        showToast("升级失败！");
                    }
                }).start();



                // 不设置down.setDestinationInExternalFilesDir(context,null,apkname);
                //会默认将下载的apk文件放在/data/data/com.android.providers.downloads/cache/xxx.apk
//                DownloadManager.Request request = new DownloadManager.Request(
//                        Uri.parse(apkUrl));
//                // 设置通知栏标题
//                request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE);
//                request.setTitle("下载");
//                request.setDescription("下载王力智能");
//                request.setAllowedOverRoaming(false);
//                // 设置文件存放目录
//                DownloadManager.Query query = new DownloadManager.Query();
//                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
//
//                if (!checkSDCardAvailable()) {
//
//                    DownloadManager downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                    Long id = downManager.enqueue(request);
//                    PreferencesUtils.saveLong("downID", id);
//                } else {
//                    request.setDestinationInExternalFilesDir(
//                            getApplicationContext(),
//                            Environment.DIRECTORY_DOWNLOADS,
//                            "smarthome_wl.apk");
//
//                    DownloadManager downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                    Long id = downManager.enqueue(request);
//                    PreferencesUtils.saveLong("downID", id);
//                }

            }

            @Override
            public void onLeftClick() {
                if (isMust) {
                    finish();
                    ActivityPageManager.getInstance().finishAllActivity();
                    System.exit(0);
                }
            }
        });

    }

    /**
     * Check the SD card
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }



    //网关升级进度
    private void showPross(){
        subInterval= Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn( Schedulers.newThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != progressDialog)
                            progressDialog.dismiss();//关闭对话框
                        showToast("网关升级出错！");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong >=40){
                            handler.sendEmptyMessage(4);
                            return;
                        }

                        if (aLong >= 5 && aLong%5 ==0) {
                            ThreadPoolUtils.getInstance().getSingleThreadExecutor().submit(new Runnable() {
                                @Override
                                public void run() {
                                    AppContext.getInstance().getSerialInstance().getGateWayInfo();
                                }
                            });
                        }

                        if(aLong<40){
                            long pross = (long) (aLong * 2.5);
                            Message  msg= Message.obtain();
                            msg.what =3;
                            msg.arg1 =(int)pross;
                            handler.sendMessage(msg);
                        }


                    }
                });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != pdialog)
            pdialog.dismiss();
        if (mDialog != null)
            mDialog.dismiss();
        hideLoadingDialog();
        if(subInterval !=null && !subInterval.isUnsubscribed() ) {
            subInterval.unsubscribe();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 2:
                if (null != progressDialog){
                    progressDialog.setMessage("正在往网关发升级包，\n切勿关闭APP，切勿关闭网关电源");
                    progressDialog.setProgress(msg.arg1);
                }
                break;
            case 3:
                if (null != progressDialog){
                    progressDialog.setMessage("网关升级中...");
                    progressDialog.setProgress(msg.arg1);
                }
                break;
            case 4:
                if(null != subInterval)
                    subInterval.unsubscribe();
                if(null != progressDialog){
                    progressDialog.dismiss();
                }
                showToast("升级失败");
                break;
            case 5:
                if (null != progressDialog){
                    progressDialog.setMessage("升级包下载中...");
                    progressDialog.setProgress(msg.arg1);
                }
                break;
            case 8:
                if(null !=progressApkDialog)
                progressApkDialog.setProgress(msg.arg1);
                break;


        }



        return false;
    }
}
