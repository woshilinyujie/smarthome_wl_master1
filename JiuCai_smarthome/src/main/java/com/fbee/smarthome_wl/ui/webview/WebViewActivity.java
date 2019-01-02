package com.fbee.smarthome_wl.ui.webview;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.base.BaseApplication;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.DownLoadFileUtil;
import com.fbee.smarthome_wl.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class WebViewActivity extends BaseActivity {
    private LinearLayout activityWebView;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private WebView webview;
    private ProgressDialog progressDialog;
    // Injection token as specified in HTML source
    private static final String INJECTION_TOKEN = "**injection**";
    //https://help-doc.wonlycloud.com:10000
   // private String url="http://help.wonlycloud.com:10000";
    private String url="https://help-doc.wonlycloud.com:10000";
    private AlertDialog alertDialog;
    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
    }

    @Override
    protected void initView() {
        initApi();
        activityWebView = (LinearLayout) findViewById(R.id.activity_web_view);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        webview = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            verifyRecordPermissions(this);
    }


    public static void verifyRecordPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_RECORD,
                    1);
        }
    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        title.setText("帮助");
        back.setOnClickListener(this);

        // 让网页自适应屏幕宽度
        WebSettings webSettings= webview.getSettings(); // webView: 类WebView的实例
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        webSettings.setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webSettings.setSupportZoom(true);// 是否可以缩放，默认true
        webSettings.setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        webSettings.setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        webSettings.setAppCacheEnabled(true);// 是否使用缓存
        if(AppUtil.isNetworkAvailable(this)){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        }else{
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        }
        webSettings.setDomStorageEnabled(true);// DOM Storage

        MyWebViewClient mMyWebViewClient = new MyWebViewClient();
        mMyWebViewClient.onPageFinished(webview, url);
        mMyWebViewClient.shouldOverrideUrlLoading(webview, url);
        webview.setWebViewClient(mMyWebViewClient);
        webview.loadUrl(url);
    }



    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//网页页面开始加载的时候
            try{
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(BaseApplication.getInstance().getContext());
                    progressDialog.setMessage("加载中...");
                    progressDialog.show();
                    webview.setEnabled(false);// 当加载网页的时候将网页进行隐藏
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {//网页加载结束的时候
            //super.onPageFinished(view, url);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
                webview.setEnabled(true);
            }
            if(url!=null&&url.endsWith(".pdf")){
                showCustomizeDialog(url);
            }

        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse response = super.shouldInterceptRequest(view, url);

            if(url != null && url.contains(INJECTION_TOKEN)) {
                String assetPath = url.substring(url.indexOf(INJECTION_TOKEN) + INJECTION_TOKEN.length(), url.length());
                try {
                    response = new WebResourceResponse(
                            "application/javascript",
                            "UTF8",
                            WebViewActivity.this.getAssets().open(assetPath)
                    );
                } catch (IOException e) {
                    e.printStackTrace(); // Failed to load asset file
                }
            }
            return response;
        }




//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            view.loadUrl(request.toString());
//            return true;
//        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
        }

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (webview.canGoBack()) {
//                webview.goBack();
//            } else {
//              finish();
//            }
//        }
//        return true;
//    }


    @Override
    protected void onDestroy()
    {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        activityWebView.removeView(webview);
        if(null != webview){
            webview.removeAllViews();
            //清空所有Cookie
            CookieSyncManager.createInstance(this);  //Create a singleton CookieSyncManager within a context
            CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
            cookieManager.removeAllCookie();// Removes all cookies.
            CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

            webview.setWebChromeClient(null);
            webview.setWebViewClient(null);
            webview.getSettings().setJavaScriptEnabled(false);
            webview.clearCache(true);
            webview.destroy();
         }

        super.onDestroy();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;

        }

    }
    /**
     * 下载pdf弹出对话框
     */
    private void showCustomizeDialog(  final String url) {
        File directory= Environment.getExternalStorageDirectory();
        String fileName=url.substring(url.lastIndexOf("/")+1);
        final String filePath=directory.getAbsolutePath()+"/pdf"+"/"+fileName;
        if(FileUtils.isFileExist(filePath)){
            Intent intent = getPdfFileIntent(filePath);
            startActivity(intent);
        }else{
            final AlertDialog.Builder customizeDialog =
                    new AlertDialog.Builder(this);
            final View dialogView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_download, null);
            //TextView title = (TextView) dialogView.findViewById(R.id.tv_title);


            TextView cancleText = (TextView) dialogView.findViewById(R.id.tv_left_cancel_btn);
            TextView confirmText = (TextView) dialogView.findViewById(R.id.tv_right_confirm_btn);
            confirmText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    try {
                        if (alertDialog != null)
                            alertDialog.dismiss();

                        new DownLoadFileUtil(WebViewActivity.this, url, filePath, mApiWrapper).start();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            cancleText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (alertDialog != null)
                        alertDialog.dismiss();
                }
            });
            customizeDialog.setView(dialogView);
            alertDialog = customizeDialog.show();
        }

    }

    public Intent getPdfFileIntent(String path){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri= FileProvider.getUriForFile(this, "com.fbee.smarthome_wl.fileprovider", new File(path));
        } else{
            uri = Uri.fromFile(new File(path));
        }
        i.setDataAndType(uri, "application/pdf");
        return i;
    }

}
