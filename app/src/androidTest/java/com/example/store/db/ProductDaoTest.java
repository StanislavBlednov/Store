package com.example.store.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

public class ProductDaoTest {
    private StoreDatabase db;
    private ProductDao productDao;
    private Product product1 = new Product("name1", 1.0, 0);
    private Product product2 = new Product("name2", 2.0, 1);
    private Product product3 = new Product("name2", 3.0, 2);
    private List<Product> list;
    private List<Product> bigList;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        db = Room.
                inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), StoreDatabase.class)
                .allowMainThreadQueries()
                .build();
        productDao = db.productDao();
        product1.setId(1);
        product2.setId(2);
        product3.setId(3);
        list = new ArrayList<>(Arrays.asList(product1, product2, product3));
        bigList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Product product = new Product("name" + i, i + 1.0, i);
            product.setId(i + 1);
            bigList.add(product);
        }

    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertProduct() {
        productDao.insert(product1).blockingAwait();
        productDao.getAllProducts()
                .test()
                .assertValue(products -> products.size() == 1 &&
                        products.get(0).getName().equals(product1.getName()));

    }

    @Test
    public void testInsertProductsCollection() {
        productDao.insert(list).blockingAwait();
        productDao.getAllProducts()
                .test()
                .assertValue(products -> products.size() == 3);
    }

    @Test
    public void updateProduct() {
        productDao.insert(list).blockingAwait();
        Product product = new Product("name4", product2.getPrice(), 3);
        long id = product2.getId();
        product.setId(id);
        productDao.update(product).blockingAwait();
        productDao.getProductById(id)
                .test()
                .assertValue(prod -> prod.getName().equals(product.getName()) &&
                        prod.getPrice() == product2.getPrice() && prod.getPcs() == 3);
    }

    @Test
    public void deleteProduct() {
        productDao.insert(list).blockingAwait();
        productDao.delete(product2).blockingAwait();
        productDao.getProductById(product2.getId())
                .test()
                .assertNoValues();
    }

    @Test
    public void deleteAllProducts() {
        productDao.insert(list).blockingAwait();
        productDao.deleteAll().blockingAwait();
        productDao.getAllProducts()
                .test()
                .assertValue(List::isEmpty);
    }

    @Test
    public void getProductByName() {
        productDao.insert(bigList).blockingAwait();
        productDao.getProductByName("name25")
                .test()
                .assertValue(prod -> prod.getId() == 26 && prod.getPcs() == 25);
        productDao.getProductByName("noName")
                .test()
                .assertNoValues();
    }

    @Test
    public void getProductsFromPage() {
        productDao.insert(bigList).blockingAwait();
        productDao.getProductsFromPage(1)
                .test()
                .assertValue(products -> products.size() == 20 &&
                        products.get(0).getName().equals("name20"));
        productDao.getProductsFromPage(2)
                .test()
                .assertValue(products -> products.size() == 10);
        productDao.getProductsFromPage(5)
                .test()
                .assertValue(List::isEmpty);
    }
}