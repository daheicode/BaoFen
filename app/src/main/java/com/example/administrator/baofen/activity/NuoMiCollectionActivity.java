package com.example.administrator.baofen.activity;

import com.example.administrator.baofen.base.WebCollectionActivity;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.control.ALiCollectionController;
import com.example.administrator.baofen.control.NuoMiCollectionController;

public class NuoMiCollectionActivity extends WebCollectionActivity {

    @Override
    public WebCollectionController generateWebController() {
//        return super.generateWebController();
        return new NuoMiCollectionController();
    }
}
