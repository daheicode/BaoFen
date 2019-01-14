package com.example.administrator.baofen.control;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.baofen.base.JSUtil;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.bean.MobileNoVO;
import com.example.administrator.baofen.bean.NuoMiItemVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NuoMiCollectionController extends WebCollectionController {

    private String homeUrl = "https://m.nuomi.com/changecity";

    private String baseUrl;
    private String keyword;
    private boolean isSearching;
    private int pageIndex = 1;
    private int requestRetries = 0;

    private Runnable runRestart;
    private Runnable runGetUrl;
    private Runnable runGetPhone;

    private WebViewClient webViewClient = new WebViewClient(){
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
                if (url.toLowerCase().contains("nuomi")) {
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
        runRestart = new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(homeUrl);
                callBack.startErr("请选择城市");
            }
        };

        runGetUrl  = new Runnable() {
            @Override
            public void run() {
                String url = webView.getUrl();
                Log.i("WebViewUrl","url: "+url);
                if (TextUtils.isEmpty(url)){
                    runRestart.run();
                }else if (url.startsWith(homeUrl)){
                    callBack.startErr("请选择城市");
                }else {
                    String js = "console.log('start');  " +
                            "var searchView = document.getElementsByClassName('index-popup-search'); if (!searchView || searchView.length == 0) { window.snow.stop(); return; } " +
                            "var bUrl =  searchView[0].action; window.snow.sendBaseUrl(bUrl); ";
                    webView.loadUrl(JSUtil.buildJSF(js));
                }
            }
        };

        runGetPhone = new Runnable() {
            @Override
            public void run() {
                String url = baseUrl + "?kw=" + keyword + "&pn=" + pageIndex + "&pageSize=10&async=1";
                Log.i("url",url);
                String js = "console.log('runGetPhone'); " +
                        "var httpRequest = new XMLHttpRequest(); " +
                        "        httpRequest.open('GET', '"+url+"', true); " +
                        "        httpRequest.send(); " +
                        "        httpRequest.onreadystatechange = function () { " +
                        "            if (httpRequest.readyState == 4 && httpRequest.status == 200) { " +
                        "                var json = httpRequest.responseText; " +
                        "                window.snow.sendResult(json); " +
                        "            } " +
                        "        }; ";
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
        return new JsCallBack();
    }

    @Override
    public void startSearch(String keyword) {
        this.isSearching = true;
        if (!TextUtils.equals(this.keyword,keyword)){
            this.pageIndex = 1;
        }
        this.requestRetries = 0;
        this.keyword = keyword;
        runGetUrl.run();
    }

    @Override
    public void stopSearch() {
        this.isSearching = false;
        handler.removeCallbacks(runRestart);
        handler.removeCallbacks(runGetUrl);
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
        public void sendBaseUrl(String actionUrl){
            if (TextUtils.isEmpty(actionUrl)){
                if (isSearching) {
                    callBack.startErr("采集完成");
                }
                return;
            }
            if (isSearching) {
                baseUrl = actionUrl;
                handler.post(runGetPhone);
            }
        }

        @JavascriptInterface
        public void sendResult(String data){
            Log.i("testJsCallBack","data:"+(data==null ? "" : data));
            if (TextUtils.isEmpty(data)){
                this.stop();
                return;
            }
            List<NuoMiItemVO> nuoMiItems = null;
            try {
                JSONObject jsonObject = new JSONObject(data);
                jsonObject = jsonObject.getJSONObject("data");
                String json = jsonObject.getString("lists");
                nuoMiItems = new Gson().fromJson(json, new TypeToken<List<NuoMiItemVO>>(){}.getType());
                Log.i("result",nuoMiItems+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (nuoMiItems == null || nuoMiItems.size() == 0){
                if (isSearching && pageIndex < 10 && requestRetries < 5 || pageIndex < 29 && requestRetries < 3  ) {
                    requestRetries++;
                    handler.postDelayed(runGetPhone, 2000 * requestRetries);
                }else {
                    this.stop();
                }
                return;
            }
            if (callBack != null){
                List<MobileNoVO> mobileNoVOList = new ArrayList<>();
                MobileNoVO mobileNoVO;
                for (NuoMiItemVO vo : nuoMiItems) {
                    String phoneString = vo.getPhone();
                    if (TextUtils.isEmpty(phoneString)){
                        continue;
                    }
                    Log.w("phone",phoneString);
                    String[] phoneArray = phoneString.split("\\|");
                    for (String p : phoneArray) {
                        Log.w("phone2",p);
                        mobileNoVO = new MobileNoVO();
                        mobileNoVO.setResourceName(vo.getPoiname());
                        mobileNoVO.setMobileNo(p);
                        mobileNoVOList.add(mobileNoVO);
                    }
                }
                callBack.addMobileNoData(mobileNoVOList);
            }
            requestRetries = 0;
            pageIndex++;
            if (isSearching){
                handler.postDelayed(runGetPhone,1000);
            }
        }

    }


}
