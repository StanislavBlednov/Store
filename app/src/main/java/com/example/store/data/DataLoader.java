package com.example.store.data;

import com.example.store.db.Product;

import java.util.List;

import io.reactivex.Single;

public interface DataLoader {
    Single<List<Product>> getProducts();
}
