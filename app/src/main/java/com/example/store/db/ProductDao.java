package com.example.store.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface ProductDao {
    @Insert
    Completable insert(Product product);
    @Insert
    Completable insert(Iterable<Product> products);
    @Update
    Completable update(Product product);
    @Update
    Completable update(Iterable<Product> products);
    @Delete
    Completable delete(Product product);
    @Delete
    Completable delete(Iterable<Product> products);
    @Query("DELETE FROM products")
    Completable deleteAll();
    @Query("SELECT * FROM products WHERE name = :name")
    Maybe<Product> getProductByName(String name);
    @Query("SELECT * FROM products WHERE id = :id")
    Maybe<Product> getProductById(long id);
    @Query("SELECT * FROM products")
    Observable<List<Product>> getAllProducts();
    @Query("SELECT * FROM products LIMIT 20 OFFSET :page")
    Observable<List<Product>> getProductsFromPage(int page);
}
