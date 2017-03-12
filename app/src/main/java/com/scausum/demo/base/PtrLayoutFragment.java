package com.scausum.demo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fubaisum.ptrlayout.demo.R;
import com.scausum.ptrlayout.PtrLayout;


/**
 * Created by sum on 8/9/16.
 */
public abstract class PtrLayoutFragment extends Fragment
        implements PtrLayout.OnLoadingListener, PtrLayout.OnRefreshListener {

    protected PtrLayout ptrLayout;

    @Nullable
    @Override
    public final View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ptr_layout, null);
        ptrLayout = (PtrLayout) rootView.findViewById(R.id.ptr_layout);
        ptrLayout.setOnRefreshListener(this);
        ptrLayout.setOnLoadingListener(this);
        FrameLayout contentContainer = (FrameLayout) rootView.findViewById(R.id.ptr_content);
        onCreateContentView(inflater, contentContainer, savedInstanceState);

        return rootView;
    }

    protected abstract void onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup contentContainer, @Nullable Bundle savedInstanceState);
}
