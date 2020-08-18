package com.example.store.appServices;

import com.example.store.db.Product;
import com.example.store.db.ProductDao;
import com.example.store.ui.fragments.dialogs.DialogButtons;
import com.example.store.ui.fragments.dialogs.ReturnAmount;

import io.reactivex.subjects.PublishSubject;

public interface ProductRepository {
    void insert(Product product);
    void insert(Iterable<Product> products);
    void update(Product product);
    void delete(Product product);
    void deleteAll();
    ProductDao dao();
    PublishSubject<Product> insertChange();
    PublishSubject<Product> updateChange();
    PublishSubject<Product> deleteChange();
    PublishSubject<ProductSave> productSave();
    PublishSubject<DialogButtons> productSaveAlert();
    PublishSubject<ReturnAmount> dialogAmountPcs();
    void save(Product product, ProductSave save, int pcsDifference);
    void buy(Product product);
}
