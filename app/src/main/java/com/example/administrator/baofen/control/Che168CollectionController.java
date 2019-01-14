package com.example.administrator.baofen.control;

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

public class Che168CollectionController extends WebCollectionController {

    private String homeUrl = "https://m.che168.com/";

    private String keyword;
    private boolean isSearching;
    private int pageIndex = 1;
    private int itemIndex = 0;
    private String itemName;

    private Runnable runStart;
    private Runnable runClickItem;
    private Runnable runGetPhone;
    private Runnable runNextPage;

    private JsCallBack jsCallBack = new JsCallBack();

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebViewUrl webF",url);
            super.onPageFinished(view, url);
            if (view.getProgress() == 100){
                if (url.toLowerCase().contains("che168")) {
                    if (url.toLowerCase().contains("/list/")) {
                        if (isSearching) {
                            handler.post(runClickItem);
                        }
                    }else if (url.toLowerCase().contains("com/lianmeng/") || (url.toLowerCase().contains("com/dealer/") && !url.toLowerCase().contains("com/dealer/Ask"))){
                        if (isSearching){
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

            if (url.toLowerCase().startsWith("tel:")){
                if (!TextUtils.isEmpty(itemName)){
                    String phone = url.substring(4);
                    jsCallBack.sendResult(itemName,phone);
                    itemName = null;
                }
                return true;
            }else if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")){
                if (url.toLowerCase().contains("che168")) {
                    if (url.toLowerCase().contains("com/lianmeng/") || url.toLowerCase().contains("com/dealer/")){
                        webViewPage.loadUrl(url);
                    }else {
                        view.loadUrl(url);
                    }
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
                String js = "console.log('runStart'); document.getElementsByClassName('search-text')[0].click(); " +
                        "document.getElementsByClassName('search-enter')[0].value = '"+keyword+"'; " +
                        "document.getElementsByClassName('search-skip')[0].click(); ";
                webView.loadUrl(JSUtil.buildJSF(js));
            }
        };
        runClickItem = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runClickItem'); var listView = document.getElementsByClassName('billing'); " +
                        "if (!listView || listView.length <= "+itemIndex+") { window.snow.nextPage(); return; } " +
                        "var top = listView["+itemIndex+"].offsetTop; " +
                        "window.scrollTo(0,top - listView[0].offsetTop); " +
                        "listView["+itemIndex+"].children[1].click(); ";
                webView.loadUrl(JSUtil.buildJSF(js));
            }
        };
        runGetPhone = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runGetPhone'); var name = null; " +
                        "try {name = document.getElementsByClassName('company-list')[0].children[0].children[0].textContent; } catch(e) {}; " +
                        "if (!name) { name = document.getElementsByClassName('description-user')[0].children[1].textContent; } " +
                        "console.log('name: '+name); " +
                        "window.snow.callName(name); " +
                        "document.getElementById('callPhone').click(); ";
                webViewPage.loadUrl(JSUtil.buildJSF(js));
            }
        };
        runNextPage = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runNextPage'); var nextPage = document.getElementsByClassName('pager-next')[0]; " +
                        "if (nextPage.classList.contains('btn-disabled')) { window.snow.stop(); return; }; " +
                        "nextPage.click(); ";
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
//        this.requestRetries = 0;
        this.keyword = keyword;
        runStart.run();
    }

    @Override
    public void stopSearch() {
        this.isSearching = false;
        handler.removeCallbacks(runStart);
        handler.removeCallbacks(runClickItem);
        handler.removeCallbacks(runGetPhone);
        handler.removeCallbacks(runNextPage);
    }

    public class JsCallBack extends WebCollectionController.JsCallBack {

        @JavascriptInterface
        public void restart(){
//            handler.post(runRestart);
        }

        @JavascriptInterface
        public void stop(){
            if (isSearching) {
                callBack.startErr("采集完成");
            }
        }

        @JavascriptInterface
        public void clickItem(){
            if (isSearching) {
                handler.postDelayed(runClickItem, 1000);
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
            if (isSearching){
                itemIndex++;
                handler.postDelayed(runClickItem,1000);
            }
        }
    }
}
