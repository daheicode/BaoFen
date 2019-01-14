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

public class JiaXiaoCollectionController extends WebCollectionController {

    private String homeUrl = "http://m.jxedt.com/jiaxiao/";
    private String url_jiaxiao = "http://m.jxedt.com/jiaxiao/";
    private String url_jiaolian = "http://m.jxedt.com/jl/";
    private String url_peilian = "http://m.jxedt.com/pl/";

    private String keyword;
    private boolean isSearching;
    private int pageIndex = 1;
    private int itemIndex = 0;
    private String itemName;

    private Runnable runStart;
    private Runnable runGetPhone;
    private Runnable runNextPage;

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebViewUrl webF",url);
            super.onPageFinished(view, url);
            if (view.getProgress() == 100) {
                if (url.toLowerCase().contains("jxedt")) {
                    if (isSearching && view == webViewPage){
                        handler.removeCallbacks(runGetPhone);
                        handler.postDelayed(runGetPhone,1000);
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
                if (url.toLowerCase().contains("jxedt")) {
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
                String js = "var listContent = document.getElementsByClassName('content'); " +
                        "if(!listContent || listContent.length == 0) { return; } " +
                        "var listView = listContent[0].children; " +
                        "if (!listView || listView.length <= "+itemIndex+") { window.snow.nextPage(); return; } " +
                        "listView["+itemIndex+"].scrollIntoView(); " +
                        "var href = listView["+itemIndex+"].children[0].children[0].href; console.log('href:'+href); " +
                        "window.snow.loadPage(href); ";
                webView.loadUrl(JSUtil.buildJSF(js));
            }
        };
        runNextPage = new Runnable() {
            @Override
            public void run() {
                String js = "var moreView = document.getElementsByClassName('jp-more-btn')[0]; " +
                        "if (moreView.style.display == 'none') { window.snow.stop(); return; } " +
                        "moreView.click(); " +
                        "window.snow.getNextPageItem(); ";
                webView.loadUrl(JSUtil.buildJSF(js));
            }
        };
        runGetPhone = new Runnable() {
            @Override
            public void run() {
                String js = "var name = ''; try { name = document.getElementsByClassName('tit')[0].children[0].textContent; } catch(e) {} " +
                        "var phone = ''; try { phone = document.getElementsByClassName('tel')[0].textContent; } catch(e) {}  " +
                        "window.snow.sendResult(name,phone); ";
                webViewPage.loadUrl(JSUtil.buildJSF(js));
            }
        };
    }

    @Override
    protected WebViewClient generateWebViewClient() {
        return webViewClient;
    }

    @Override
    protected WebCollectionController.JsCallBack generateJsCallBack() {
        return new JsCallBack();
    }


    @Override
    public void postMessage(int type) {
        super.postMessage(type);
        if (type == 0){
            homeUrl = url_jiaxiao;
        } else if (type == 1){
            homeUrl = url_jiaolian;
        } else if (type == 2){
            homeUrl = url_peilian;
        }
        itemIndex = 0;
        webView.loadUrl(homeUrl);
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
        public void getNextPageItem() {
            if (isSearching) {
                itemIndex++;
                handler.postDelayed(runStart,1000);
            }
        }

        @JavascriptInterface
        public void getNextItem() {
            if (isSearching) {
                itemIndex++;
                handler.postDelayed(runStart,100);
            }
        }

        @JavascriptInterface
        public void nextPage() {
            if (isSearching) {
                itemIndex--;
                handler.postDelayed(runNextPage,1000);
            }
        }

        @JavascriptInterface
        public void loadPage(final String href) {
            if (TextUtils.isEmpty(href)){
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (href.startsWith("http")){
                        webViewPage.loadUrl(href);
                    }else {
                        webViewPage.loadUrl(homeUrl+href);
                    }
                }
            });
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
            getNextItem();
        }
    }
}
