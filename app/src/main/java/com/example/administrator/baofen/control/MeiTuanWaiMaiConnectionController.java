package com.example.administrator.baofen.control;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.baofen.base.WebCollectionController;

public class MeiTuanWaiMaiConnectionController extends WebCollectionController {

    private final static String HOME_URL = "http://h5.waimai.meituan.com/waimai/mindex/home";

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebViewUrl webF",url);
            super.onPageFinished(view, url);
            if (view.getProgress() == 100) {
                if (url.toLowerCase().contains("waimai.meituan")) {

                }
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("WebViewUrl web",url);
            if (url == null || TextUtils.isEmpty(url.trim())){
                return true;
            }

            if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")){
                if (url.toLowerCase().contains("waimai.meituan")) {
                    view.loadUrl(url);
                }
                return true;
            }else {
                return true;
            }

        }
    };

    @Override
    protected String getHomeUrl() {
        return HOME_URL;
    }

    @Override
    protected void initRunnable() {

    }

    @Override
    protected WebViewClient generateWebViewClient() {
        return webViewClient;
    }

    @Override
    protected JsCallBack generateJsCallBack() {
        return new JsCallBack();
    }

    @Override
    public void startSearch(String keyword) {

    }

    @Override
    public void stopSearch() {

    }
}
