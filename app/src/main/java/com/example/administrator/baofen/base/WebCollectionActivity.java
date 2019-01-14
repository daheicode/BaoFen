package com.example.administrator.baofen.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.baofen.R;
import com.example.administrator.baofen.bean.MobileNoVO;
import com.example.administrator.baofen.control.ALiCollectionController;

import java.util.List;

public abstract class WebCollectionActivity extends Activity implements View.OnClickListener, ICollectionController.CallBack {

    private View startPoiBtn;
    private View stopPoiBtn;
    private EditText editText;
    private TextView poiResultText;
    private RecyclerView recyclerView;
    private MobileNoRecyclerAdapter recyclerAdapter;
    protected WebCollectionController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_web);

        initView();
        initWeb();
    }

    protected void initView() {
        poiResultText = findViewById(R.id.text_poi_result_size);
        startPoiBtn = findViewById(R.id.btn_poi_start);
        startPoiBtn.setOnClickListener(this);
        stopPoiBtn = findViewById(R.id.btn_poi_stop);
        stopPoiBtn.setOnClickListener(this);
        stopPoiBtn.setEnabled(false);
        editText = findViewById(R.id.edit_poi_keyword);
        recyclerView = findViewById(R.id.recycler_channel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerAdapter = new MobileNoRecyclerAdapter(this);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void initWeb() {
        controller = generateWebController();
        controller.setWebView((WebView) findViewById(R.id.web_view));
        controller.setWebView2((WebView) findViewById(R.id.web_view2));
        controller.setCallBack(this);
        if (!needKeyword()){
            editText.setEnabled(false);
            editText.setFocusable(false);
            editText.setKeyListener(null);
            editText.setHint("不需要输入关键字");
        }
    }

    protected abstract WebCollectionController generateWebController();

    @Override
    public void setMobileNoData(final List<MobileNoVO> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerAdapter.setData(list);
                poiResultText.setText("当前已经采集："+recyclerAdapter.getItemCount()+"条数据");
                recyclerView.smoothScrollToPosition(recyclerAdapter.getItemCount()-1);
            }
        });
    }

    @Override
    public void addMobileNoData(final List<MobileNoVO> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerAdapter.addData(list);
                poiResultText.setText("当前已经采集："+recyclerAdapter.getItemCount()+"条数据");
                recyclerView.smoothScrollToPosition(recyclerAdapter.getItemCount()-1);
            }
        });
    }

    @Override
    public void startErr(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onStopSearch();
                Toast.makeText(WebCollectionActivity.this,message,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == startPoiBtn){
            String keyword = editText.getText().toString();
            if (needKeyword() && keyword.trim().length() == 0){
                Toast.makeText(this,"请先输入关键词",Toast.LENGTH_SHORT).show();
                return;
            }
            onStartSearch();
            controller.startSearch(keyword);
        }else if (view == stopPoiBtn){
            onStopSearch();
            controller.stopSearch();
        }
    }

    protected void onStartSearch() {
        startPoiBtn.setEnabled(false);
        stopPoiBtn.setEnabled(true);
    }

    protected void onStopSearch() {
        startPoiBtn.setEnabled(true);
        stopPoiBtn.setEnabled(false);
    }

    protected boolean needKeyword() {
        return true;
    }
}
