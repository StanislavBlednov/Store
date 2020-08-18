package com.example.store.appServices;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.store.db.Product;
import com.example.store.db.ProductDao;
import com.example.store.ui.fragments.dialogs.DialogButtons;
import com.example.store.ui.fragments.dialogs.ExistNowDialog;
import com.example.store.ui.fragments.dialogs.ReturnAmount;
import com.example.store.utils.Clear;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ProductRepositoryImpl implements ProductRepository, Clear {
    private static final String TAG = "ProductRepositoryImpl";
    private final ProductDao productDao;
    private PublishSubject<Product> insertChanges;
    private PublishSubject<Product> updateChanges;
    private PublishSubject<Product> deleteChanges;
    private PublishSubject<ProductSave> saveProduct;
    private PublishSubject<DialogButtons> saveProductAlert;
    private PublishSubject<ReturnAmount> returnAmountPcs;
    private CompositeDisposable disposables;
    private Map<Product, Integer> productInProcess;
    private Context context;

    public ProductRepositoryImpl(ProductDao productDao, Context context) {
        this.productDao = productDao;
        this.context = context;
        disposables = new CompositeDisposable();
        productInProcess = new HashMap<>();
    }

    @Override
    public void insert(Product product) {
        disposables.add(
                productDao.insert(product)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            if (insertChanges != null) insertChanges.onNext(product);
                        }, e -> Log.e(TAG, "insert: " + e.getMessage()))
        );
    }

    @Override
    public void insert(Iterable<Product> products) {
        disposables.add(
                productDao.insert(products)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            if (insertChanges != null) {
                                for (Product product : products) insertChanges.onNext(product);
                            }
                        }, e -> Log.e(TAG, "insert list:" + e.getMessage()))
        );
    }

    @Override
    public void update(Product product) {
        disposables.add(
                productDao.update(product)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            productInProcess.remove(product);
                            if (updateChanges != null) updateChanges.onNext(product);
                        }, e -> Log.e(TAG, "update: " + e.getMessage()))
        );
    }

    @Override
    public void delete(Product product) {
        disposables.add(
                productDao.delete(product)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            if (deleteChanges != null) deleteChanges.onNext(product);
                        }, e -> Log.e(TAG, "delete: " + e.getMessage()))
        );
    }

    @Override
    public void deleteAll() {
        disposables.add(
                productDao.deleteAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                        }, e -> Log.e(TAG, "deleteAll: " + e.getMessage()))
        );
    }

    @Override
    public ProductDao dao() {
        return productDao;
    }

    @Override
    public PublishSubject<Product> insertChange() {
        if (insertChanges == null) insertChanges = PublishSubject.create();
        return insertChanges;
    }

    @Override
    public PublishSubject<Product> updateChange() {
        if (updateChanges == null) updateChanges = PublishSubject.create();
        return updateChanges;
    }

    @Override
    public PublishSubject<Product> deleteChange() {
        if (deleteChanges == null) deleteChanges = PublishSubject.create();
        return deleteChanges;
    }

    @Override
    public PublishSubject<ProductSave> productSave() {
        if (saveProduct == null) saveProduct = PublishSubject.create();
        return saveProduct;
    }

    @Override
    public PublishSubject<DialogButtons> productSaveAlert() {
        if (saveProductAlert == null) saveProductAlert = PublishSubject.create();
        return saveProductAlert;
    }

    @Override
    public PublishSubject<ReturnAmount> dialogAmountPcs() {
        if (returnAmountPcs == null) returnAmountPcs = PublishSubject.create();
        return returnAmountPcs;
    }

    @Override
    public void save(Product product, ProductSave save, int pcsDifference) {
        if (product.getId() == 0) { // new product
            disposables.add(
                    // check product with the same name
                    dao().getProductByName(product.getName())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    p -> { // exist product with the same name
                                        conflictDialog(p, product, save);
                                    },
                                    e -> Log.e(TAG, "save -> insert: " + e.getMessage()),
                                    () -> { // insert new product
                                        insertDelay(product);
                                        save.done();
                                    })
            );
        } else {  // update product
            disposables.add(
                    dao().getProductById(product.getId())
                            // check if it need to update
                            .map(p -> !p.getName().equals(product.getName()) ||
                                    p.getPrice() != product.getPrice() || p.getPcs() != product.getPcs())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(different -> { // update if needed
                                        if (different) updateDelay(product, pcsDifference);
                                        save.done();
                                    },
                                    e -> Log.e(TAG, "save -> update: " + e.getMessage()),
                                    () -> {
                                        insertDelay(product);
                                        save.done();
                                    })
            );
        }
    }

    private void conflictDialog(Product oldProduct, Product newProduct, ProductSave save){
        disposables.add(
                // wait dialog response
                productSaveAlert()
                        .first(DialogButtons.CANCEL)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(button -> {
                            // replace product
                            if (button.equals(DialogButtons.POSITIVE)){
                                delete(oldProduct);
                                insertDelay(newProduct);
                                save.done();
                                save.done();
                            }
                        }, e -> Log.e(TAG, "save -> replace: " + e.getMessage()))
        );
        // run dialog
        Bundle bundle = new Bundle();
        bundle.putString(ExistNowDialog.PRODUCT_NAME, oldProduct.getName());
        save.alert(bundle);
    }

    private void insertDelay(Product product){
        disposables.add(
                Completable.timer(5, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .subscribe(() -> insert(product),
                                e -> Log.e(TAG, "insert delay: " + e.getMessage()))
        );
    }

    private void updateDelay(Product product, int pcsDifference){
        Integer currentPcs = productInProcess.remove(product);
        if (currentPcs != null){
            Product prod = new Product(product.getName(), product.getPrice(),  currentPcs + pcsDifference);
            prod.setId(product.getId());
            productInProcess.put(prod, prod.getPcs());
        } else {
            productInProcess.put(product, product.getPcs());
        }

        disposables.add(
                Completable.timer(5, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .subscribe(() -> update(product),
                                e -> Log.e(TAG, "update delay: " + e.getMessage()))
        );
    }

    @Override
    public void buy(Product product) {
        // check a product buying or adding
        Integer currentPcs = productInProcess.remove(product);
        // decrease pcs
        Product prod = new Product(product.getName(), product.getPrice(),  currentPcs != null ? currentPcs - 1 : product.getPcs() - 1);
        prod.setId(product.getId());
        // check negative pcs
        if (prod.getPcs() >= 0) {
            productInProcess.put(prod, prod.getPcs());

            disposables.add( // buy with delay
                    Completable.timer(3, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .subscribe(() -> update(prod),
                                    e -> Log.e(TAG, "buy: " + e.getMessage()))
            );
        } else {
            Toast.makeText(context, "Sorry " + prod.getName() + " ended already", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void dispose() {
        if (disposables != null) disposables.dispose();
    }
}
