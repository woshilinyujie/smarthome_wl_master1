package com.example.wl.WangLiPro_v1.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.api.AppContext;

import java.io.IOException;

public class MyWebViewActivity extends Activity implements View.OnClickListener{
    private ImageView back;
    private TextView title;
    private WebView webview;
    private String url;
    ProgressDialog progressDialog;
    private static final String INJECTION_TOKEN = "**injection**";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        initData();
    }

    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        webview = (WebView) findViewById(R.id.webview);
    }

    protected void initData() {
        back.setVisibility(View.VISIBLE);
        title.setText("发现");
        back.setOnClickListener(this);
        url=getIntent().getStringExtra("linkUrl");
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
        if(isNetworkAvailable(this)){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        }else{
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        }
        webSettings.setDomStorageEnabled(true);// DOM Storage

        MyWebViewClient mMyWebViewClient = new MyWebViewClient();
        mMyWebViewClient.onPageFinished(webview, url);
        mMyWebViewClient.shouldOverrideUrlLoading(webview, url);
        webview.setWebViewClient(mMyWebViewClient);
        if(null != url)
        webview.loadUrl(url);
    }

    /**
     * 描述：判断网络是否有效.
     *
     * @param context the context
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    class MyWebViewClient extends WebViewClient {


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse response = super.shouldInterceptRequest(view, url);

            if(url != null && url.contains(INJECTION_TOKEN)) {
                String assetPath = url.substring(url.indexOf(INJECTION_TOKEN) + INJECTION_TOKEN.length(), url.length());
                try {
                    response = new WebResourceResponse(
                            "application/javascript",
                            "UTF8",
                            MyWebViewActivity.this.getAssets().open(assetPath)
                    );
                } catch (IOException e) {
                    e.printStackTrace(); // Failed to load asset file
                }
            }
            return response;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//网页页面开始加载的时候
            try {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(MyWebViewActivity.this);
                    progressDialog.setMessage("加载中...");
                    progressDialog.show();
                    webview.setEnabled(false);// 当加载网页的时候将网页进行隐藏
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {//网页加载结束的时候
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
                webview.setEnabled(true);
            }
            super.onPageFinished(view, url);

        }

    }
        @Override
        //设置回退
        //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (!webview.canGoBack()) {
                onBackPressed();
                return true;
            }
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
                webview.goBack();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
            }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
