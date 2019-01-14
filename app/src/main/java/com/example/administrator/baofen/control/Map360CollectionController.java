package com.example.administrator.baofen.control;


import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.administrator.baofen.base.JSUtil;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.bean.Map360VO;
import com.example.administrator.baofen.bean.MobileNoVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Map360CollectionController extends WebCollectionController {

    private final static String HOME_URL = "https://m.map.so.com/";
    private final static String POI_URL = "https://restapi.map.so.com/api/simple?resType=json&mobile=1&flag=callback&encode=UTF-8&jsoncallback=&keyword=";
    private final static String POI_URL2 = "&city=北京&cityname=北京&cityid=undefined&batch=";
    private final static String POI_URL3 = "&number=10&sid=1000&ext=1&qii=true&routePoint=&routeType=0&routeSelectPOI=0&mp=34.656681%2C112.400527&sort=&range=&order=&filter=&price=&star=&dataFrom=&src=&filter_adcode=&qc=&usermp=&business_area=&business_name=&business_switch=1&map_cpc=on";

    private String keyword;
    private boolean isSearching;
    private int pageIndex = 1;
    private int requestRetries = 0;

    private Runnable runHideMapBar;
    private Runnable runStart;

    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("WebViewUrl webF",url);
            super.onPageFinished(view, url);
            if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")){
                if (url.toLowerCase().contains("map.so")) {
                    handler.post(runHideMapBar);
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
                if (url.toLowerCase().contains("map.so")) {
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
        runHideMapBar = new Runnable() {
            @Override
            public void run() {
                String js = "console.log('runHideMapBar'); " +
                        "document.getElementById('conContainer').hidden = true; ";
                webView.loadUrl(JSUtil.buildJSF(js));
            }
        };
        runStart = new Runnable() {
            @Override
            public void run() {
                String poiUrl = POI_URL + keyword + POI_URL2 + pageIndex + POI_URL3;
                String js = "console.log('runStart'); " +
                        "var httpRequest = new XMLHttpRequest(); " +
                        "        httpRequest.open('GET', '"+poiUrl+"', true); " +
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
    protected JsCallBack generateJsCallBack() {
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
        runStart.run();
    }

    @Override
    public void stopSearch() {
        this.isSearching = false;
        handler.removeCallbacks(runStart);
        handler.removeCallbacks(runHideMapBar);
    }

    public class JsCallBack extends WebCollectionController.JsCallBack {
        @JavascriptInterface
        public void stop(){
            if (isSearching) {
                callBack.startErr("采集完成");
            }
        }

        @JavascriptInterface
        public void sendResult(String data){
            Log.i("testJsCallBack","data:"+(data==null ? "" : data));
            if (TextUtils.isEmpty(data)){
                this.stop();
                return;
            }
            List<Map360VO> map360Items = null;
            try {
                JSONObject jsonObject = new JSONObject(data);
                String json = jsonObject.getString("poi");
                map360Items = new Gson().fromJson(json, new TypeToken<List<Map360VO>>(){}.getType());
                Log.i("result",map360Items+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (map360Items == null || map360Items.size() == 0){
                if (isSearching && pageIndex < 10 && requestRetries < 5 || pageIndex < 29 && requestRetries < 3  ) {
                    requestRetries++;
                    handler.postDelayed(runStart, 2000 * requestRetries);
                }else {
                    this.stop();
                }
                return;
            }
            if (callBack != null){
                List<MobileNoVO> mobileNoVOList = new ArrayList<>();
                MobileNoVO mobileNoVO;
                for (Map360VO vo : map360Items) {
                    if (vo.getDetail() == null){
                        continue;
                    }
                    String phoneString = vo.getDetail().getPhone();
                    if (TextUtils.isEmpty(phoneString)){
                        continue;
                    }
                    Log.w("phone",phoneString);
                    String[] phoneArray = phoneString.split("\\|");
                    for (String p : phoneArray) {
                        Log.w("phone2",p);
                        mobileNoVO = new MobileNoVO();
                        mobileNoVO.setResourceName(vo.getDetail().getPoi_name());
                        mobileNoVO.setMobileNo(p);
                        mobileNoVOList.add(mobileNoVO);
                    }
                }
                callBack.addMobileNoData(mobileNoVOList);
            }
            requestRetries = 0;
            pageIndex++;
            if (isSearching){
                handler.postDelayed(runStart,1000);
            }
        }

    }
}
