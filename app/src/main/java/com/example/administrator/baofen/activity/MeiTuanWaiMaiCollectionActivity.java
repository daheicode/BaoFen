package com.example.administrator.baofen.activity;

import android.os.Bundle;

import com.example.administrator.baofen.base.WebCollectionActivity;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.control.ALiCollectionController;
import com.example.administrator.baofen.control.MeiTuanWaiMaiConnectionController;

public class MeiTuanWaiMaiCollectionActivity extends WebCollectionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }*/
        super.onCreate(savedInstanceState);
    }

    @Override
    protected WebCollectionController generateWebController() {
        return new MeiTuanWaiMaiConnectionController();
    }
}
