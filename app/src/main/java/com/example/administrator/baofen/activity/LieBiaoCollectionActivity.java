package com.example.administrator.baofen.activity;

import com.example.administrator.baofen.base.WebCollectionActivity;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.control.ALiCollectionController;
import com.example.administrator.baofen.control.LieBiaoCollectionController;

public class LieBiaoCollectionActivity extends WebCollectionActivity {

    @Override
    public WebCollectionController generateWebController() {
        return new LieBiaoCollectionController();
    }

    @Override
    protected boolean needKeyword() {
        return false;
    }
}
