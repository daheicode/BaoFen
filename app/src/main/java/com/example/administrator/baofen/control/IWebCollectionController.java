package com.example.administrator.baofen.control;

import com.example.administrator.baofen.bean.CallBack;

public abstract class IWebCollectionController {
   public abstract void startSearch( String keyword);
    public abstract void stopSearch();
    public abstract void setCallBack(CallBack callBack);
    public abstract void onResume();
    public abstract void onPause();
    public abstract void onDestroy();
}
