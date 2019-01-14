package com.example.administrator.baofen.activity;

import android.app.AliasActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.baofen.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.baidu, R.id.n1688, R.id.n58, R.id.amap, R.id.che168, R.id.dazhong, R.id.eleme, R.id.ganji, R.id.huicong, R.id.liebiaowang, R.id.meituan, R.id.meituanwaimai, R.id.nuomi, R.id.qq, R.id.so, R.id.yidiantong, R.id.zhixiao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.baidu:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.n1688:
                startActivity(new Intent(MainActivity.this,ALiCollectionActivity.class));
                break;
            case R.id.n58:
                startActivity(new Intent(MainActivity.this,N58Acitivity.class));
                break;
            case R.id.amap:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.che168:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.dazhong:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.eleme:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.ganji:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.huicong:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.liebiaowang:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.meituan:
                startActivity(new Intent(MainActivity.this,MeituanActivity.class));
                break;
            case R.id.meituanwaimai:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.nuomi:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.qq:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.so:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.yidiantong:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
            case R.id.zhixiao:
                startActivity(new Intent(MainActivity.this,BaiDuAcitivity.class));
                break;
        }
    }
}
