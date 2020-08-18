package com.example.store.ui.fragments.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.store.App;
import com.example.store.R;
import com.example.store.databinding.FragmentEditProductBinding;
import com.example.store.db.Product;
import com.example.store.ui.fragments.back.BackEndFragment;

import org.jetbrains.annotations.NotNull;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class EditProductFragment extends Fragment implements InputAmountPcsDialog{
    private EditProductViewModel editModel;

    public EditProductFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentEditProductBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_product, container, false);
        if (getArguments() != null) {
            Product product = getArguments().getParcelable(BackEndFragment.PRODUCT);
            if (product != null) {
                editModel = new EditProductViewModel(((App) requireActivity().getApplication()).getProductRepo(), product, this);
            } else {
                editModel = new EditProductViewModel(((App) requireActivity().getApplication()).getProductRepo(), null, this);
            }
        } else {
            editModel = new EditProductViewModel(((App) requireActivity().getApplication()).getProductRepo(), null, this);
        }
        binding.setModel(editModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editModel.dispose();
    }

    @Override
    public void run(Bundle bundle) {
        Navigation.findNavController(requireView()).navigate(R.id.inputAmountDialog, bundle);
    }
}