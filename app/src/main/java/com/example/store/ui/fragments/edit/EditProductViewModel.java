package com.example.store.ui.fragments.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.store.R;
import com.example.store.appServices.ProductRepository;
import com.example.store.db.Product;
import com.example.store.ui.fragments.dialogs.DialogButtons;
import com.example.store.ui.fragments.dialogs.InputPcsAmountDialog;
import com.example.store.ui.fragments.dialogs.ReturnAmount;
import com.example.store.utils.Clear;
import com.example.store.utils.ObservableFieldToRx;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class EditProductViewModel implements Clear {
    private static final String TAG = "EditProductViewModel";
    private static final String NO_EMPTY = "Field can't be empty";
    private static final String NO_ZERO = "Price must be more then zero";
    private final long id;
    private int startPcs;
    private int changePcs = 0;
    private ProductRepository productRepo;
    private InputAmountPcsDialog dialog;
    private ObservableField<String> name = new ObservableField<>("");
    private ObservableField<String> price = new ObservableField<>("");
    private ObservableField<String> pcs = new ObservableField<>("");
    private ObservableBoolean nameErrorEnable = new ObservableBoolean(false);
    private ObservableBoolean priceErrorEnable = new ObservableBoolean(false);
    private ObservableBoolean pcsErrorEnable = new ObservableBoolean(false);
    private ObservableBoolean showAmountInput = new ObservableBoolean(false);
    private ObservableField<String> errorName = new ObservableField<>("");
    private ObservableField<String> errorPrice = new ObservableField<>("");
    private ObservableField<String> errorPcs = new ObservableField<>("");
    private CompositeDisposable disposables;

    public EditProductViewModel(ProductRepository productRepo, Product product, InputAmountPcsDialog dialog) {
        this.productRepo = productRepo;
        this.dialog = dialog;
        disposables = new CompositeDisposable();
        // Edit exist product
        if (product != null) {
            id = product.getId();
            startPcs = product.getPcs();
            name.set(product.getName());
            price.set(String.valueOf(product.getPrice()));
            pcs.set(String.valueOf(startPcs));
            // listen buying and update start value
            disposables.add(
                productRepo.updateChange()
                        .filter(p -> p.equals(product))
                        .subscribe(p -> {
                            startPcs = p.getPcs();
                            pcs.set(String.valueOf(startPcs + changePcs));
                        }, e -> Log.e(TAG, "product update: " + e.getMessage()))
            );
        } else { // Init addition new product
            showAmountInput.set(true);
            startPcs = 0;
            id = 0;
        }
        // Listen name editing
        disposables.add(
                ObservableFieldToRx.observable(name)
                        .filter(n -> !n.isEmpty() && nameErrorEnable.get())
                        .subscribe(n -> {
                                    nameErrorEnable.set(false);
                                    errorName.set("");
                                },
                                e -> Log.e(TAG, "name error: " + e.getMessage()))
        );
        // Listen prise editing
        disposables.add(
                ObservableFieldToRx.observable(price)
                        .filter(n -> !n.isEmpty() && priceErrorEnable.get())
                        .subscribe(n -> {
                                    if (Double.parseDouble(n) > 0) {
                                        priceErrorEnable.set(false);
                                        errorPrice.set("");
                                    } else {
                                        errorPrice.set(NO_ZERO);
                                    }
                                },
                                e -> Log.e(TAG, "price error: " + e.getMessage()))
        );
        // Listen pcs editing for new product
        if (showAmountInput.get()) {
            disposables.add(
                    ObservableFieldToRx.observable(pcs)
                            .filter(n -> !n.isEmpty() && pcsErrorEnable.get())
                            .subscribe(n -> {
                                        pcsErrorEnable.set(false);
                                        errorPcs.set("");
                                    },
                                    e -> Log.e(TAG, "pcs error: " + e.getMessage()))
            );
        }
        // Listen toolbar save button
        disposables.add(
                productRepo.productSave()
                        .filter(save -> checkFields())
                        .subscribe(save -> productRepo.save(updateProduct(), save, changePcs),
                                e -> Log.e(TAG, "product save: " + e.getMessage()))
        );
    }

    private Product updateProduct(){
        Product prod = new Product(name.get(),
                Double.parseDouble(Objects.requireNonNull(price.get())),
                Integer.parseInt(Objects.requireNonNull(pcs.get())));
        if (id != 0) prod.setId(id);
        return prod;
    }
    // error management
    private boolean checkFields(){
        if (Objects.requireNonNull(name.get()).isEmpty()) {
            errorName.set(NO_EMPTY);
            nameErrorEnable.set(true);
        }
        if (Objects.requireNonNull(price.get()).isEmpty()) {
            errorPrice.set(NO_EMPTY);
            priceErrorEnable.set(true);
        }else if (Double.parseDouble(Objects.requireNonNull(price.get())) <= 0){
            errorPrice.set(NO_ZERO);
            priceErrorEnable.set(true);
        }
        if (showAmountInput.get()) {
            if (Objects.requireNonNull(pcs.get()).isEmpty()) {
                errorPcs.set(NO_EMPTY);
                pcsErrorEnable.set(true);
            }
        }
        return !nameErrorEnable.get() && !priceErrorEnable.get() && (!showAmountInput.get() || !pcsErrorEnable.get());
    }
    // plus, minus buttons handle
    public void click(View v){
        Bundle bundle = new Bundle();
        switch (v.getId()){
            case R.id.editProduct__addPcs : {
                disposables.add(
                        productRepo.dialogAmountPcs()
                                .first(ReturnAmount.empty())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {
                                    if (response.getDb().equals(DialogButtons.POSITIVE)){
                                        changePcs += response.getPcs();
                                        pcs.set(String.valueOf(startPcs + changePcs));
                                    }
                                })
                );
                bundle.putString(InputPcsAmountDialog.OPERATION_TYPE, "Add");
                dialog.run(bundle);
                break;
            }
            case R.id.editProduct__subPcs : {
                disposables.add(
                        productRepo.dialogAmountPcs()
                                .first(ReturnAmount.empty())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {
                                    if (response.getDb().equals(DialogButtons.POSITIVE)){
                                        changePcs -= response.getPcs();
                                        pcs.set(String.valueOf(Math.max(startPcs + changePcs, 0)));
                                    }
                                })
                );
                bundle.putString(InputPcsAmountDialog.OPERATION_TYPE, "Subtract");
                dialog.run(bundle);
                break;
            }
        }
    }

    @BindingAdapter("android:error_text")
    public static void setErrorText(View textInput, String error) {
        ((TextInputLayout) textInput).setError(error);
    }

    public ObservableField<String> getName() {
        return name;
    }

    public ObservableField<String> getPrice() {
        return price;
    }

    public ObservableField<String> getPcs() {
        return pcs;
    }

    public ObservableBoolean getNameErrorEnable() {
        return nameErrorEnable;
    }

    public ObservableBoolean getPriceErrorEnable() {
        return priceErrorEnable;
    }

    public ObservableBoolean getPcsErrorEnable() {
        return pcsErrorEnable;
    }

    public ObservableField<String> getErrorName() {
        return errorName;
    }

    public ObservableField<String> getErrorPrice() {
        return errorPrice;
    }

    public ObservableField<String> getErrorPcs() {
        return errorPcs;
    }

    public ObservableBoolean getShowAmountInput() {
        return showAmountInput;
    }

    @Override
    public void dispose() {
        if (disposables != null) disposables.dispose();
    }
}
