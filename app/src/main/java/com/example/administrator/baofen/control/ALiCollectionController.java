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

import java.util.List;

public class ALiCollectionController extends WebCollectionController {

    private String homeUrl = "https://m.1688.com/offer_search/-undefined.html";
    private String searchUrl = "http://m.1688.com/page/search.html?type=company&keywords=";

    private String keyword;
    private boolean isSearching;
    private int pageIndex = 1;
    private int itemIndex = 0;

    private Runnable runSearch;
    private Runnable runClickItem;
    private Runnable runGetPhone;

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebViewUrl webF",url);
            super.onPageFinished(view, url);
            if (view.getProgress() == 100){
                if (url.toLowerCase().contains("1688")) {
                    if (url.toLowerCase().contains("com/winport/company")) {
                        if (isSearching) {
                            handler.post(runGetPhone);
                        }
                    }else if (url.toLowerCase().contains("com/gongsi_search")){
                        if (isSearching) {
                            handler.post(runClickItem);
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
                if (url.toLowerCase().contains("1688")) {
                    if (url.toLowerCase().contains("com/winport/company")){
                        webViewPage.loadUrl(url);
                    } else if (url.toLowerCase().contains("com/winport")) {
                        url = url.replace("/winport","/winport/company");
                        webViewPage.loadUrl(url);
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


    @Override
    protected String getHomeUrl() {
        return homeUrl;
    }

    @Override
    protected void initRunnable() {
        runSearch = new Runnable() {
            @Override
            public void run() {
                if (pageIndex == 1) {
                    webView.loadUrl(searchUrl + keyword);
                }else {
                    webView.loadUrl(searchUrl + keyword + "&sortType=pop&beginPage=" + pageIndex);
                }
            }
        };

        runClickItem = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runClickItem'); var listView = document.getElementsByClassName('bg_white pd15 mt15'); " +
                        "if (!listView || listView.length == 0) { window.snow.stop(); return; } " +
                        "if ("+itemIndex+" >= listView.length) { window.snow.nextPage(); return; } " +
                        "var top = listView["+itemIndex+"].offsetTop; " +
                        "window.scrollTo(0,top - listView[0].offsetTop); " +
                        "listView["+itemIndex+"].children[0].click(); ";
                webView.loadUrl(JSUtil.buildJSF(js));
                itemIndex++;
            }
        };

        runGetPhone = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runGetPhone'); var name = document.getElementsByClassName('info-container')[0].children[1].textContent; " +
                        "var phoneList = document.getElementsByClassName('archive-sheet-item phone'); var phones = []; " +
                        "if (!phoneList || phoneList.length == 0) { return; } " +
                        "for (var i = 0; i < phoneList.length; i++) { var phone =  phoneList[i].textContent; phones.push({'resourceName':name,'mobileNo':phone}); }; " +
                        "window.snow.sendResult(JSON.stringify(phones)); ";
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
    public void startSearch(String keyword) {
        this.isSearching = true;
        if (!TextUtils.equals(this.keyword,keyword)){
            this.pageIndex = 1;
            this.itemIndex = 0;
        }
        this.keyword = keyword;
        runSearch.run();
    }

    @Override
    public void stopSearch() {
        this.isSearching = false;
        handler.removeCallbacks(runSearch);
        handler.removeCallbacks(runClickItem);
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
        public void nextPage(){
            if (isSearching) {
                pageIndex++;
                itemIndex = 0;
                handler.postDelayed(runSearch,1000);
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
            handler.postDelayed(runClickItem,1000);
        }
    }
}
