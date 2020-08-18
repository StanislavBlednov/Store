package com.example.store.ui.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import com.example.store.App;
import com.example.store.appServices.ProductRepository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ExistNowDialog extends DialogFragment {
    public static final String PRODUCT_NAME = "productName";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String productName = null;
        if (getArguments() != null) {
             productName = getArguments().getString(PRODUCT_NAME);
        }
        ProductRepository repository = ((App) requireActivity().getApplication()).getProductRepo();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Conflict")
                .setMessage((productName != null ? productName : "Product") + " exist now")
                .setPositiveButton("Replace", (di, i) -> repository.productSaveAlert().onNext(DialogButtons.POSITIVE))
                .setNegativeButton("Cancel", (di, i) -> repository.productSaveAlert().onNext(DialogButtons.NEGATIVE))
                .setOnCancelListener(di -> repository.productSaveAlert().onNext(DialogButtons.CANCEL));
        return builder.create();
    }
}
