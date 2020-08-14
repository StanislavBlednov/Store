package com.example.store.appServices;

import android.content.Context;
import android.util.Log;

import com.example.store.db.Product;
import com.example.store.db.ProductDao;
import com.example.store.db.StoreDatabase;
import com.example.store.utils.Clear;

import androidx.room.Room;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class DBRepository implements DBService, Clear {
    private static final String TAG = "DBRepository";
    private final ProductDao productDao;
    private PublishSubject<Product> insertChanges;
    private PublishSubject<Product> updateChanges;
    private PublishSubject<Product> deleteChanges;
    private CompositeDisposable disposables;

    public DBRepository(Context context) {
        StoreDatabase storeDatabase = Room.databaseBuilder(context, StoreDatabase.class, "store.db").build();
        productDao = storeDatabase.productDao();
        disposables = new CompositeDisposable();
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
                                for (Product product: products) insertChanges.onNext(product);
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
                        .subscribe(() -> {}, e -> Log.e(TAG, "deleteAll: " + e.getMessage()))
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
    public void dispose() {
        if (disposables != null) disposables.dispose();
    }
}
