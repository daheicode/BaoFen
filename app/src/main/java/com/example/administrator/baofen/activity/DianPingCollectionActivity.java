package com.example.administrator.baofen.activity;

import com.example.administrator.baofen.base.WebCollectionActivity;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.control.ALiCollectionController;
import com.example.administrator.baofen.control.DianPingCollectionController;

public class DianPingCollectionActivity extends WebCollectionActivity {

    @Override
    protected WebCollectionController generateWebController() {
        return new DianPingCollectionController();
    }
}
