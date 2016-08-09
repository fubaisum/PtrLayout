package com.fubaisum.demo.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fubaisum.demo.base.PtrLayoutFragment;
import com.fubaisum.demo.fragment.WithTextView;
import com.fubaisum.ptrlayout.demo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sum on 8/9/16.
 */
public class MainFragment extends PtrLayoutFragment {

    @Bind(R.id.recycler_view_main)
    RecyclerView recyclerView;

    private MainItemAdapter mainItemAdapter;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, @Nullable ViewGroup contentContainer, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_with_recycler_view, contentContainer);
        ButterKnife.bind(this, contentView);
        initializeView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initializeView() {
        mainItemAdapter = new MainItemAdapter(getActivity(), getMainItemList());
        recyclerView.setAdapter(mainItemAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @NonNull
    private List<MainItem> getMainItemList() {
        List<MainItem> items = new ArrayList<>();

        items.add(new MainItem(R.string.with_text_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WithTextView withTextView = new WithTextView();
                showFragment(withTextView, "WithTextView");
            }
        }));

        return items;
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(this);
        transaction.add(R.id.main_container, fragment, tag);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.show(fragment);
        transaction.commit();
    }

    @Override
    public void onRefresh() {
        ptrLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayout.finishRefresh();
            }
        }, 1500);
    }

    @Override
    public void onLoading() {
        ptrLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrLayout.finishLoading();
            }
        }, 1500);
    }
}
