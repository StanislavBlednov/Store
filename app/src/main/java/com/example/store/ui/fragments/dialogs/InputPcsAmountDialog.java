package com.example.store.ui.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.store.App;
import com.example.store.R;
import com.example.store.appServices.ProductRepository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InputPcsAmountDialog extends DialogFragment {
    public static final String OPERATION_TYPE = "operationType";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String  type = null;
        if (getArguments() != null) {
            type = getArguments().getString(OPERATION_TYPE);
        }
        ProductRepository repository = ((App) requireActivity().getApplication()).getProductRepo();

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_input_amount, null);
        EditText editText = view.findViewById(R.id.dialogInputAmount__editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setMessage((type != null ? type : "") + " pieces")
                .setPositiveButton("Apply", (di, i) ->
                    repository.dialogAmountPcs().onNext(ReturnAmount.of(Integer.parseInt(editText.getText().toString()), DialogButtons.POSITIVE)))
                .setNegativeButton("Cancel", (di, i) ->
                    repository.dialogAmountPcs().onNext(ReturnAmount.empty()))
                .setOnCancelListener(di ->
                    repository.dialogAmountPcs().onNext(ReturnAmount.empty()));

        return builder.create();
    }
}
