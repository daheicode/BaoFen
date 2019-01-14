package com.example.administrator.baofen.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.administrator.baofen.base.WebCollectionActivity;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.control.ALiCollectionController;
import com.example.administrator.baofen.control.Map360CollectionController;

public class Map360CollectioinActivity extends WebCollectionActivity {

    @Override
    public WebCollectionController generateWebController() {
        return new Map360CollectionController();
    }

    private static Activity act;
    public static Activity getAct(){
        return act;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
    }
}
