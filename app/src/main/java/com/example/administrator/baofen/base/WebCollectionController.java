package com.example.administrator.baofen.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public abstract class WebCollectionController implements ICollectionController {

    protected Handler handler = new Handler();
    protected CallBack callBack;
    protected WebView webView;
    protected WebView webViewPage;
    private WebChromeClient webChromeClient = new OrangeWebChromeClient();

    protected abstract String getHomeUrl();
    protected abstract void initRunnable();
    protected abstract WebViewClient generateWebViewClient();
    protected abstract JsCallBack generateJsCallBack();

    public void setWebView(WebView webView) {
        this.webView = webView;
        initRunnable();
        initWebView(webView);
        webView.loadUrl(getHomeUrl());
    }

    public void setWebView2(WebView webView) {
        this.webViewPage = webView;
        initWebView(webView);
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void initWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(generateWebViewClient());
        webView.setWebChromeClient(webChromeClient);
        webView.addJavascriptInterface(generateJsCallBack(), "snow");

        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.getSettings().setDatabaseEnabled(true);
        String dir = FansApplication.getApplication().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setGeolocationDatabasePath(dir);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要

        String appCachePath = FansApplication.getApplication().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
    }

    @Override
    public void postMessage(int type) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        stopSearch();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

    @Override
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public class JsCallBack{

    }

}
