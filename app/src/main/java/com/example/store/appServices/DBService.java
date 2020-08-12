package com.example.store.appServices;

import com.example.store.db.Product;
import com.example.store.db.ProductDao;

import io.reactivex.subjects.PublishSubject;

public interface DBService {
    void insert(Product product);
    void insert(Iterable<Product> products);
    void update(Product product);
    void update(Iterable<Product> products);
    void delete(Product product);
    void delete(Iterable<Product> products);
    void deleteAll();
    ProductDao dao();
    PublishSubject<Product> insertChange();
    PublishSubject<Product> updateChange();
    PublishSubject<Product> deleteChange();
}
