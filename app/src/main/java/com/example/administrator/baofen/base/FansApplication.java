package com.example.administrator.baofen.base;

import android.app.Application;

public class FansApplication extends Application {


    private static Application application;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static Application getApplication(){
        return application;
    };
}
