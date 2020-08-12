package com.example.store.db;

import android.icu.math.BigDecimal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "price")
    private BigDecimal price;
    @ColumnInfo(name = "pieces")
    private int pieces;

    public Product(String name, BigDecimal price, int pieces) {
        this.name = name;
        this.price = price;
        this.pieces = pieces;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getPieces() {
        return pieces;
    }
}
