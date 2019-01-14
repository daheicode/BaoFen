package com.example.administrator.baofen.activity;

import android.view.View;
import android.widget.RadioGroup;

import com.example.administrator.baofen.R;
import com.example.administrator.baofen.base.WebCollectionActivity;
import com.example.administrator.baofen.base.WebCollectionController;
import com.example.administrator.baofen.control.ALiCollectionController;
import com.example.administrator.baofen.control.JiaXiaoCollectionController;

public class JiaXiaoCollectionActivity extends WebCollectionActivity {

    @Override
    public WebCollectionController generateWebController() {
        return new JiaXiaoCollectionController();
    }

    private RadioGroup radioGroup;
    @Override
    protected void initView() {
        super.initView();
        findViewById(R.id.edit_poi_keyword).setVisibility(View.GONE);
       /* ViewStub viewStub = findViewById(R.id.stub_bar_jiaiao);
        radioGroup = (RadioGroup) viewStub.inflate();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_btn_jiaxiao){
                    controller.postMessage(0);
                } else if (checkedId == R.id.radio_btn_jiaolian){
                    controller.postMessage(1);
                } else if (checkedId == R.id.radio_btn_peilian){
                    controller.postMessage(2);
                }
            }
        });*/

    }

    @Override
    protected boolean needKeyword() {
        return false;
    }

    @Override
    protected void onStartSearch() {
        super.onStartSearch();
        radioGroup.setEnabled(false);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    @Override
    protected void onStopSearch() {
        super.onStopSearch();
        radioGroup.setEnabled(true);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(true);
        }
    }
}
