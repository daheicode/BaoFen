package com.example.administrator.baofen.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.administrator.baofen.R;
import com.example.administrator.baofen.bean.CallBack;
import com.example.administrator.baofen.bean.MobileNoVO;
import com.example.administrator.baofen.control.MeiTuanCollectionController;

import java.util.List;

public class MeituanActivity extends AppCompatActivity implements CallBack {

    private WebView webView;
    private MeiTuanCollectionController mc;
    private WebView webView2;
    private LinearLayout lin;
    private ScrollView listview;
    private RelativeLayout rela;
    private LinearLayout linear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metuan);
        webView = (WebView) findViewById(R.id.webview);
        webView2 = (WebView) findViewById(R.id.webview2);
        rela = (RelativeLayout) findViewById(R.id.parent);
        linear = (LinearLayout) findViewById(R.id.line1);
        mc = new MeiTuanCollectionController();
        mc.setWebView(this,webView);
        mc.setWebView2(webView2);
//        mc.setWebView2(webView);
        mc.setCallBack(this);
        mc.startSearch("华联影院");
        /*listview = new ScrollView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.=Gravity.BOTTOM;
        params.topMargin=700;

        params.addRule(RelativeLayout.BELOW);
        listview.setLayoutParams(params);
        rela.addView(listview);
        lin = new LinearLayout(this);
        lin.setBackgroundColor(Color.GRAY);
//        listview.setBackgroundColor(Color.WHITE);
        listview.addView(lin,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));*/
    }

    @Override
    public void addMobileNoData(final List<MobileNoVO> mobileNoVOList) {

        for (int i = 0; i < mobileNoVOList.size(); i++) {
            Log.e("content",mobileNoVOList.get(i).getMobileNo()+":"+mobileNoVOList.get(i).getResourceName());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        TextView textView = new TextView(MeituanActivity.this);
        textView.setText(mobileNoVOList.get(0).getResourceName()+":"+mobileNoVOList.get(0).getMobileNo());
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        linear.addView(textView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });
    }
}
