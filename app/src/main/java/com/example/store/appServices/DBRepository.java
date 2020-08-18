package com.example.store.appServices;

import android.content.Context;

import com.example.store.db.ProductDao;
import com.example.store.db.StoreDatabase;
import com.example.store.utils.Clear;

import androidx.room.Room;

public class DBRepository implements DBService, Clear {
    private final ProductDao productDao;
    private ProductRepositoryImpl productRepository;
    private Context context;

    public DBRepository(Context context) {
        this.context = context;
        StoreDatabase storeDatabase = Room.databaseBuilder(context, StoreDatabase.class, "store.db").build();
        productDao = storeDatabase.productDao();
    }


    @Override
    public ProductRepository productRepo() {
        if (productRepository == null) productRepository = new ProductRepositoryImpl(productDao, context);
        return productRepository;
    }

    @Override
    public void dispose() {
        if (productRepository != null) productRepository.dispose();
    }
}
