package com.example.store.data;

import android.content.res.Resources;

import com.example.store.db.Product;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

public class CsvToProductParser implements DataLoader {
    private Resources resources;

    public CsvToProductParser(Resources resources) {
        this.resources = resources;
    }

    @Override
    public Single<List<Product>> getProducts() {
        return Single.fromCallable(() -> {
            List<Product> products = new ArrayList<>();
            InputStream inputStream = resources.getAssets().open("data.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while ((str = br.readLine()) != null) {
                str = str.replace("\"", "");
                String[] s = str.split(",");
                if (s.length == 3) {
                    products.add(new Product(s[0].trim(), Double.parseDouble(s[1].trim()), Integer.parseInt(s[2].trim())));
                }
            }
            br.close();
            inputStream.close();
            return products;
        });
    }
}
