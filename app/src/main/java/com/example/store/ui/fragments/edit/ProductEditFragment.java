package com.example.store.ui.fragments.edit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.store.R;
import com.example.store.db.Product;
import com.example.store.ui.fragments.back.BackEndFragment;

public class ProductEditFragment extends Fragment {

    public ProductEditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_edit, container, false);
        if (getArguments() != null) {
            Product product = getArguments().getParcelable(BackEndFragment.PRODUCT);
            if (product != null) {
                EditText name = view.findViewById(R.id.productDetail__editName);
                name.setText(product.getName());
                EditText price = view.findViewById(R.id.productDetail__editPrice);
                price.setText(String.valueOf(product.getPrice()));
                EditText pcs = view.findViewById(R.id.productDetail__editAmount);
                pcs.setText(String.valueOf(product.getPcs()));
            }
        }
        return view;
    }

}