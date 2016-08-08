package com.fubaisum.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fubaisum.ptrlayout.PtrLayout;
import com.fubaisum.ptrlayout.demo.R;

public class MainActivity extends AppCompatActivity
        implements
        PtrLayout.OnLoadingListener,
        PtrLayout.OnRefreshListener {

    private PtrLayout ptrLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ptrLayout = (PtrLayout) findViewById(R.id.ptr_layout_main);
        assert ptrLayout != null;
        ptrLayout.setOnRefreshListener(this);
        ptrLayout.setOnLoadingListener(this);
    }

    @Override
    public void onRefresh() {
        Log.e("MainActivity", "onRefresh");
        ptrLayout.finishRefresh();
    }

    @Override
    public void onLoading() {
        Log.e("MainActivity", "onLoading");
        ptrLayout.finishLoading();
    }
}
