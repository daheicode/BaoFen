package com.example.administrator.baofen.base;

import android.app.Activity;

import com.example.administrator.baofen.bean.MobileNoVO;

import java.util.List;

public interface ICollectionController {

    public void postMessage(int type);

    public void startSearch(String keyword);

    public void stopSearch();

    public void setCallBack(CallBack callBack);

    public void onResume();

    public void onPause();

    public void onDestroy();

    public static interface CallBack{
        public void setMobileNoData(List<MobileNoVO> list);
        public void addMobileNoData(List<MobileNoVO> list);
        public void startErr(String message);
        public Activity getActivity();
    }


}
