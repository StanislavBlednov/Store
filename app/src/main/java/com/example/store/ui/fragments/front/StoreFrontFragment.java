package com.example.store.ui.fragments.front;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.store.App;
import com.example.store.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class StoreFrontFragment extends Fragment {
    private static final String POSITION = "StoreFrontFragment";
    private StoreFrontAdapter adapter;
    private LinearLayoutManager layoutManager;

    public StoreFrontFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_front, container, false);

        if (getActivity() != null) {
            App app = (App) getActivity().getApplication();

            adapter = new StoreFrontAdapter(app.getDb(), app.getLoader());

            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            RecyclerView recyclerView = view.findViewById(R.id.storeFront__recyclerView);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
            pagerSnapHelper.attachToRecyclerView(recyclerView);

            if (savedInstanceState != null) {
                recyclerView.post(() -> layoutManager.scrollToPosition(savedInstanceState.getInt(POSITION, 0)));
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (layoutManager != null) {
            outState.putInt(POSITION, layoutManager.findLastCompletelyVisibleItemPosition());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) adapter.dispose();
    }
}