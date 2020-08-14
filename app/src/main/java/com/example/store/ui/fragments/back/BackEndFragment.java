package com.example.store.ui.fragments.back;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.store.App;
import com.example.store.R;
import com.example.store.db.Product;
import com.example.store.utils.Click;

public class BackEndFragment extends Fragment implements Click {
    public static final String PRODUCT = "PRODUCT";
    private static final String POSITION = "BackEndFragment";
    private BackEndAdapter adapter;
    private LinearLayoutManager layoutManager;

    public BackEndFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_back_end, container, false);

        if (getActivity() != null) {
            App app = (App) getActivity().getApplication();

            adapter = new BackEndAdapter(app.getDb(), app.getLoader(), this);

            layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

            RecyclerView recyclerView = view.findViewById(R.id.backEnd__recyclerView);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
            recyclerView.addItemDecoration(divider);

            if (savedInstanceState != null) {
                recyclerView.post(() -> layoutManager.scrollToPosition(savedInstanceState.getInt(POSITION)));
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
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.dispose();
        }
    }

    @Override
    public void edit(Product product) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PRODUCT, product);
        View view = getView();
        if (view != null) {
            Navigation
                    .findNavController(view)
                    .navigate(R.id.action_backEndFragment_to_productDetailFragment, bundle);
        }
    }
}