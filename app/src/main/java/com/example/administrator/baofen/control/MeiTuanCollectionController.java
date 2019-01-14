package com.example.administrator.baofen.control;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.baofen.bean.CallBack;
import com.example.administrator.baofen.bean.MobileNoVO;

import java.util.ArrayList;
import java.util.List;

public class MeiTuanCollectionController extends IWebCollectionController {

    private Handler handler = new Handler();
    private String homeUrl = "http://i.meituan.com/index/changecity";

    private WebView webView;
    private String keyword;
    private boolean isSearching;
    private CallBack callBack;

    private Runnable runRestart;
    private Runnable runStart;
    private Runnable runSearch;
    private Runnable runClickItem;
    private Runnable runRemoveItem;
    private Runnable runGetPhone;
    private Activity activity;
    private WebView webViewPage;

    public void setWebView(Activity activity,WebView webView){
        this.webView = webView;
        this.activity=activity;
//        webViewPage=new WebView(activity);
        initWebView();
        initRunnable();
    }

    private void initRunnable() {
        runRestart = new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(homeUrl);
//                callBack.startErr("请选择城市");
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
//                    callBack.startErr("请选择城市");
                }else {
                    String start = "console.log('start');  " +
                            "try { var poiList = document.getElementsByClassName('poiList')[0].childNodes[1]; } catch(e) {} " +
                            "if (poiList) { window.snow.clickItem(); return; } " +
                            "try { var searchBtn = document.getElementsByClassName('J-box-search')[0].getElementsByClassName('react')[0]; } catch(e) {}" +
                            "if (searchBtn) { searchBtn.click(); return;} " +
                            "window.snow.restart(); ";
                    webView.loadUrl("javascript:(function test(){ "+start+" })();");
                }
            }
        };
        runSearch = new Runnable() {
            @Override
            public void run() {
                String js = "document.getElementById('keyword').value = '"+keyword+"'; document.getElementById('search-submit').disabled = false;  document.getElementById('search-submit').click(); ";
                webView.loadUrl("javascript:(function test(){"+js+"})();");
            }
        };
        runClickItem = new Runnable() {
            @Override
            public void run() {
                String see = "document.getElementsByClassName('poi-list-item')[0].getElementsByClassName('react')[0].click();" +
                        "var listView = document.getElementsByClassName('poiList')[0].childNodes[1]; " +
                        "if (listView.childNodes.length < 4) {  document.getElementsByClassName('pager')[0].childNodes[5].click(); window.snow.nextPage(); } else { window.snow.clickNextItem(); }";
                webView.loadUrl("javascript:(function test(){"+see+"})();");
            }
        };
        runRemoveItem = new Runnable() {
            @Override
            public void run() {
                String remove = "var listView = document.getElementsByClassName('poiList')[0].childNodes[1]; listView.removeChild(listView.firstChild); listView.removeChild(listView.firstChild); ";
                webView.loadUrl("javascript:(function test(){"+remove+"})();");
            }
        };
        runGetPhone = new Runnable() {
            @Override
            public void run() {
                String result = " var phoneBtn = document.getElementsByClassName('poi-phone')[0]; if (!phoneBtn){ phoneBtn = document.getElementsByClassName('poi-info-phone')[0] } phoneBtn.click(); " +
                        "var nameText = document.getElementsByClassName('poi-brand')[0]; if (!nameText) { nameText = document.getElementsByClassName('dealcard-brand')[0]; } var name = nameText.textContent; " +
                        "var phone = document.getElementsByClassName('msg-btn')[0].textContent; console.log('getResult: '+name+'-'+phone); " +
                        "window.snow.sendResult(name,phone); ";
                webViewPage.loadUrl("javascript:(function test(){"+result+"})();");
            }
        };
    }

    public void setWebView2(WebView webView){

        this.webViewPage = webView;
        webViewPage.getSettings().setJavaScriptEnabled(true);
        webViewPage.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebViewUrl web2F",url);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d("WebViewUrl web2",url);
                if(url == null){
                    return true;
                }
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                } else {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        activity.startActivity(intent);
                    return true;
                }
//                return super.shouldOverrideUrlLoading(view, url);
            }
        });
//        webViewPage.setWebChromeClient(new OrangeWebChromeClient());
        webViewPage.addJavascriptInterface(new JsCallBack(),"snow");
    }

    private void initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
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
                    if (url.toLowerCase().contains("meituan")) {

                        if (url.toLowerCase().contains("com/i/poi/") || url.toLowerCase().contains("com/poi/")) {
                            Log.e("phone",url);
                            webViewPage.loadUrl(url);
//                            view.loadUrl(url);
                            handler.postDelayed(runGetPhone, 2000);
                        } else if (url.toLowerCase().contains("search")) {
                            view.loadUrl(url);
                            handler.postDelayed(runSearch, 2000);
                            handler.postDelayed(runClickItem, 4000);
                        }else if(url.toLowerCase().contains("movie")){
                            Log.e("phone",url);
                            webViewPage.loadUrl(url);
//                            view.loadUrl(url);
                            handler.postDelayed(runGetPhone, 2000);
                        } else {
                            view.loadUrl(url);
                            Log.e("phone1",url);
                        }
                    }
                    return true;
                }else {
                    return true;
                }

//                return super.shouldOverrideUrlLoading(view, url);
            }
        });
//        webView.setWebChromeClient(new OrangeWebChromeClient());
        webView.loadUrl(homeUrl);
        //snow javascript中的一个函数 需要调用java中JsCallBack中的一个函数
        webView.addJavascriptInterface(new JsCallBack(),"snow");
    }
    @Override
    public void startSearch(final String keyword) {
        this.isSearching = true;
        this.keyword = keyword;
        runStart.run();
    }

    @Override
    public void stopSearch() {
        this.isSearching = false;
        handler.removeCallbacks(runSearch);
        handler.removeCallbacks(runClickItem);
        handler.removeCallbacks(runRemoveItem);
        handler.removeCallbacks(runGetPhone);
    }

    @Override
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
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

    public class JsCallBack{

        @JavascriptInterface
        public void restart(){
            handler.post(runRestart);
        }

        @JavascriptInterface
        public void nextPage(){
            if (isSearching) {
                handler.postDelayed(runClickItem, 2000);
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
                handler.postDelayed(runRemoveItem, 3000);
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
