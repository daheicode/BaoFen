package com.example.administrator.baofen.control;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.baofen.base.JSUtil;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.bean.MobileNoVO;

import java.util.ArrayList;
import java.util.List;

public class LieBiaoCollectionController extends WebCollectionController {

    private String homeUrl = "http://m.liebiao.com/";

    private String keyword;
    private boolean isSearching;
    private int pageIndex = 1;
    private int itemIndex = 0;
    private String itemName;

    private Runnable runStart;
    private Runnable runGetPhone;
    private Runnable runNextPage;

    private JsCallBack jsCallBack = new JsCallBack();

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebViewUrl webF",url);
            super.onPageFinished(view, url);
            if (view.getProgress() == 100) {
                if (url.toLowerCase().contains("liebiao")) {
                    if (url.toLowerCase().contains("/index") && url.toLowerCase().contains(".html")) {
                        if (isSearching){
                            handler.removeCallbacks(runGetPhone);
                            handler.postDelayed(runGetPhone,1000);
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

            if (url.toLowerCase().startsWith("tel:")){
                if (!TextUtils.isEmpty(itemName)){
                    String phone = url.substring(4);
                    jsCallBack.sendResult(itemName,phone);
                    itemName = null;
                }
                return true;
            }else if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")){
                if (url.toLowerCase().contains("liebiao")) {
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
        return homeUrl;
    }

    @Override
    protected void initRunnable() {
        runStart = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runStart'); var listView = document.getElementsByClassName('post-info'); " +
                        "if (!listView || listView.length == 0) { window.snow.startErr(); return; } " +
                        "window.snow.getPhone(); ";
                webView.loadUrl(JSUtil.buildJSF(js));
            }
        };

        runGetPhone = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runGetPhone'); var listView = document.getElementsByClassName('post-info'); " +
                        "if (!listView || listView.length <= "+itemIndex+") { window.snow.nextPage(); return; } " +
                        "listView["+itemIndex+"].scrollIntoView(); " +
                        "var name =  listView["+itemIndex+"].getElementsByClassName('post-fields')[0].children[1].textContent; " +
                        "console.log('name: '+name); " +
                        "window.snow.callName(name); " +
                        "var phoneView = listView["+itemIndex+"].getElementsByClassName('link-contact'); " +
                        "if (!phoneView || phoneView.length < 1) { window.snow.getPhone(); return; } " +
                        "phoneView[0].click(); ";
                webView.loadUrl(JSUtil.buildJSF(js));
            }
        };

        runNextPage = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runNextPage'); var nextPage = document.getElementsByClassName('next'); " +
                        "if (!nextPage || nextPage.length == 0) { window.snow.stop(); return; }; " +
                        "nextPage[0].click(); ";
                webView.loadUrl(JSUtil.buildJSF(js));
            }
        };
    }

    @Override
    protected WebViewClient generateWebViewClient() {
        return webViewClient;
    }

    @Override
    protected WebCollectionController.JsCallBack generateJsCallBack() {
        return jsCallBack;
    }

    @Override
    public void startSearch(String keyword) {
        this.isSearching = true;
        if (!TextUtils.equals(this.keyword,keyword)){
            this.pageIndex = 1;
        }
        this.keyword = keyword;
        runStart.run();
    }

    @Override
    public void stopSearch() {
        this.isSearching = false;
        handler.removeCallbacks(runStart);
        handler.removeCallbacks(runGetPhone);
        handler.removeCallbacks(runNextPage);
    }

    public class JsCallBack extends WebCollectionController.JsCallBack {

        @JavascriptInterface
        public void startErr() {
            if (isSearching) {
                callBack.startErr("当前采集页面不可采集");
            }
        }

        @JavascriptInterface
        public void stop() {
            if (isSearching) {
                callBack.startErr("采集完成");
            }
        }

        @JavascriptInterface
        public void getPhone() {
            if (isSearching) {
                itemIndex++;
                handler.postDelayed(runGetPhone,1000);
            }
        }

        @JavascriptInterface
        public void nextPage() {
            if (isSearching) {
                itemIndex = 0;
                handler.postDelayed(runNextPage,1000);
            }
        }

        @JavascriptInterface
        public void callName(String name){
            itemName = name;
        }

        @JavascriptInterface
        public void sendResult(String name, String phone){
            Log.w("testJsCallBack",name+"&"+phone);
            if (callBack != null){
                List<MobileNoVO> mobileNoVOList = new ArrayList<>();
                MobileNoVO mobileNoVO = new MobileNoVO();
                mobileNoVO.setResourceName(name);
                mobileNoVO.setMobileNo(phone);
                mobileNoVOList.add(mobileNoVO);
                callBack.addMobileNoData(mobileNoVOList);
            }
            getPhone();
        }
    }
}
