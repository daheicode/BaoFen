package com.example.administrator.baofen.control;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.bean.MobileNoVO;

import java.util.ArrayList;
import java.util.List;

public class DianPingCollectionController extends WebCollectionController {

    private String homeUrl = "https://m.dianping.com/citylist";

    private String keyword;
    private boolean isSearching;
    private int itemIndex = 0;

    private Runnable runRestart;
    private Runnable runStart;
    private Runnable runSearch;
    private Runnable runClickItem;
    private Runnable runRemoveItem;
    private Runnable runGetPhone;

    @Override
    protected String getHomeUrl() {
        return homeUrl;
    }

    @Override
    protected void initRunnable() {
//        super.initRunnable();
        runRestart = new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(homeUrl);
                callBack.startErr("请选择城市");
            }
        };
        runStart = new Runnable() {
            @Override
            public void run() {
                String url = webView.getUrl();
                Log.i("WebViewUrl","url: "+url);
                if (TextUtils.isEmpty(url)){
                    runRestart.run();
                }else if (url.startsWith(homeUrl)){
                    callBack.startErr("请选择城市");
                }else {
                    String start = "console.log('start');  " +
                            "try { var poiList = document.getElementsByClassName('list-search')[0]; } catch(e) {} " +
                            "if (poiList) { window.snow.clickItem(); return; } " +
                            "try { var searchBtn = document.getElementsByClassName('J_search_trigger')[0]; } catch(e) {}" +
                            "if (searchBtn) { searchBtn.click(); return;} " +
                            "window.snow.restart(); ";
                    webView.loadUrl("javascript:(function test(){ "+start+" })();");
                }
            }
        };
        runSearch = new Runnable() {
            @Override
            public void run() {
                String js = "document.getElementsByClassName('J_search_input')[0].focus(); document.getElementsByClassName('J_search_input')[0].value = '"+keyword+"'; " +
                        "setTimeout(function() { document.getElementsByClassName('J_suggest_list')[0].childNodes[0].childNodes[0].click(); },1000); ";
                webView.loadUrl("javascript:(function test(){"+js+"})();");
            }
        };
        runClickItem = new Runnable() {
            @Override
            public void run() {
                String see =
                        "var listView = document.getElementsByClassName('list-search')[0]; " +
                                "var top = listView.childNodes["+itemIndex+"].offsetTop; " +
                                "window.scrollTo(0,top); " +
                                "listView.childNodes["+itemIndex+"].childNodes[0].click();" +
                                "if ("+itemIndex+" == listView.childNodes.length - 1) {  window.snow.stop(); } else { window.snow.clickNextItem(); }";
                webView.loadUrl("javascript:(function test(){"+see+"})();");
            }
        };
        runRemoveItem = new Runnable() {
            @Override
            public void run() {
//                String remove = "var listView = document.getElementsByClassName('poiList')[0].childNodes[1]; listView.removeChild(listView.firstChild); listView.removeChild(listView.firstChild); ";
//                webView.loadUrl("javascript:(function test(){"+remove+"})();");
            }
        };
        runGetPhone = new Runnable() {
            @Override
            public void run() {
                Log.i("DianPing","runGetPhone");
                String result = " console.log('getP'); " +
//                        "document.getElementById('phoneClick').click(); " +
                        "var nameView = document.getElementsByClassName('shopName')[0]; if (!nameView) { nameView = document.getElementsByClassName('mutilPics-shop-name')[0]; } " +
                        "var name = nameView.textContent; " +
                        "var phone = document.getElementsByClassName('tel')[0].href; " +
                        "phone = phone.replace('tel:',''); " +
                        "window.snow.sendResult(name,phone); ";
                webViewPage.loadUrl("javascript:(function testJS(){"+result+"})();");
            }
        };
    }

    @Override
    protected WebViewClient generateWebViewClient() {
        return new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebViewUrl webF",url);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("WebViewUrl web",url);
                if (url == null || TextUtils.isEmpty(url.trim())){
                    return true;
                }

                if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")){
                    if (url.toLowerCase().contains("dianping")) {

                        if (url.toLowerCase().contains("com/shop/")) {
                            webViewPage.loadUrl(url);
                            handler.postDelayed(runGetPhone, 2000);
                        } else if (url.toLowerCase().contains("com/shoplist/")) {
                            view.loadUrl(url);
                            handler.postDelayed(runClickItem, 2000);
                        } else {
                            view.loadUrl(url);
                        }
                    }
                    return true;
                }else {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    activity.startActivity(intent);
                    return true;
                }

//                return super.shouldOverrideUrlLoading(view, url);
            }
        };
    }

    @Override
    protected WebCollectionController.JsCallBack generateJsCallBack() {
        return new JsCallBack();
    }

    @Override
    public void startSearch(final String keyword) {
        if (!webView.hasFocus()) {
            webView.requestFocus();
        }
        this.isSearching = true;
        if (!TextUtils.equals(this.keyword,keyword)){
            this.itemIndex = 0;
        }
        this.keyword = keyword;
        runStart.run();
        runSearch.run();
    }

    @Override
    public void stopSearch() {
        this.isSearching = false;
        handler.removeCallbacks(runSearch);
        handler.removeCallbacks(runClickItem);
        handler.removeCallbacks(runRemoveItem);
        handler.removeCallbacks(runGetPhone);
    }

    public class JsCallBack extends WebCollectionController.JsCallBack {

        @JavascriptInterface
        public void restart(){
            handler.post(runRestart);
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
        public void clickNextItem(){
            if (isSearching) {
                itemIndex++;
                handler.postDelayed(runClickItem, 4000);
            }
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
        }
    }

}
