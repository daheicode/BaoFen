package com.example.administrator.baofen.control;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.baofen.base.JSUtil;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.bean.MobileNoVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class HuiCongCollectionController extends WebCollectionController {

    private String homeUrl = "https://m.hc360.com/search/";
    private String searchUrl = "https://m.hc360.com/search/shop?shop=";
    private String keyword;
    private boolean isSearching;
    private int pageIndex = 0;

    private Runnable runStart;
    private Runnable runGetPhone;

    @Override
    protected String getHomeUrl() {
        return homeUrl;
    }

    @Override
    protected void initRunnable() {
        runStart = new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(searchUrl + keyword + "&pn=" + pageIndex++);
            }
        };
        runGetPhone = new Runnable() {
            @Override
            public void run() {
                String getP = "console.log('runGetPhone'); var phones = []; " +
                        "var listView = document.getElementsByClassName('comList'); if (!listView || listView.length == 0 || listView[0].children.length == 0) { window.snow.stop(); return; } " +
                        "listView = listView[0].children[0]; if (!listView || listView.children.length == 0) { window.snow.stop(); return; } " +
                        "for (var i = 0; i < listView.children.length; i++){ " +
                        "var cn = listView.children[i]; " +
                        "if (cn.nodeName == 'LI') { " +
                        "var resourceName = cn.children[0].children[0].children[0].title; " +
                        "var mobileNo = cn.getElementsByClassName('ImgBoxTel')[0].href; " +
                        "mobileNo = mobileNo.replace('tel:',''); " +
                        "phones.push({'resourceName':resourceName,'mobileNo':mobileNo}); " +
                        "} " +
                        "}; " +
                        "window.snow.sendResult(JSON.stringify(phones)); ";
                webView.loadUrl(JSUtil.buildJSF(getP));
            }
        };

    }

    @Override
    protected WebViewClient generateWebViewClient() {
        return new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebViewUrl webF",url);
//                super.onPageFinished(view, url);
                if (view.getProgress() == 100){
                    if (url.toLowerCase().contains("hc360")) {
                        if (url.toLowerCase().contains("com/search/shop")) {
                            if (isSearching) {
                                handler.post(runGetPhone);
                            }
                        }
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
                    if (url.toLowerCase().contains("hc360")) {
                        if (url.toLowerCase().contains("com/search/shop")) {
                            view.loadUrl(url);

                        } else {
                            view.loadUrl(url);
                        }
                    }
                    return true;
                }else {
                    return true;
                }

            }
        };
    }

    @Override
    protected WebCollectionController.JsCallBack generateJsCallBack() {
        return new JsCallBack();
    }

    @Override
    public void startSearch(String keyword) {
        this.isSearching = true;
        try {
            Log.w("encode0",keyword+"");
            keyword = URLEncoder.encode(keyword,"UTF-8");
            Log.w("encode1",keyword+"");
            keyword = URLEncoder.encode(keyword,"UTF-8");
            Log.w("encode2",keyword+"");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!TextUtils.equals(this.keyword,keyword)){
            this.pageIndex = 0;
        }
        this.keyword = keyword;
        runStart.run();
    }

    @Override
    public void stopSearch() {
        this.isSearching = false;
        handler.removeCallbacks(runStart);
        handler.removeCallbacks(runGetPhone);
    }

    public class JsCallBack extends WebCollectionController.JsCallBack {

        @JavascriptInterface
        public void stop(){
            if (isSearching) {
                callBack.startErr("采集完成");
            }
        }

        @JavascriptInterface
        public void sendResult(String phones){
            Log.w("testJsCallBack",""+phones);
            if (TextUtils.isEmpty(phones)){
                return;
            }
            if (callBack != null){
                List<MobileNoVO> mobileNoVOList = new Gson().fromJson(phones,new TypeToken<List<MobileNoVO>>(){}.getType());
                if (mobileNoVOList != null && mobileNoVOList.size() != 0) {
                    callBack.addMobileNoData(mobileNoVOList);
                }
            }
            handler.postDelayed(runStart,1000);
        }
    }
}
