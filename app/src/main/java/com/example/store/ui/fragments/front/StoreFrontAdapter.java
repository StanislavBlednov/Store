package com.example.store.ui.fragments.front;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.store.R;
import com.example.store.appServices.DBService;
import com.example.store.data.DataLoader;
import com.example.store.db.Product;
import com.example.store.utils.Clear;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class StoreFrontAdapter extends RecyclerView.Adapter<StoreFrontAdapter.StoreFrontHolder> implements Clear {
    private static final String TAG = "StoreFrontAdapter";
    private DBService dbService;
    private List<Product> productList;
    private CompositeDisposable disposables;

    public StoreFrontAdapter(DBService dbService, DataLoader loader) {
        this.dbService = dbService;
        disposables = new CompositeDisposable();

        productList = new ArrayList<>();

        // track insert product
        disposables.add(
                dbService.insertChange()
                        .filter(product -> product.getPcs() > 0)
                        .subscribe(this::insert, e -> Log.e(TAG, "insertChange: " + e.getMessage()))
        );
        // track update product
        disposables.add(
                dbService.updateChange()
                        .subscribe(this::update, e -> Log.e(TAG, "insertChange: " + e.getMessage()))
        );
        // track delete product
        disposables.add(
                dbService.deleteChange()
                        .subscribe(this::delete, e -> Log.e(TAG, "insertChange: " + e.getMessage()))
        );
        // initialization products list
        disposables.add(
                dbService.dao()
                        .getAllProducts()
                        .flatMap(p -> !p.isEmpty() ? Single.just(p) : Single.error(new Throwable("Empty db")))
                        .onErrorResumeNext(t -> {
                            if (t.getMessage() != null && t.getMessage().equals("Empty db")) {
                                disposables.add(
                                        loader.getProducts()
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(dbService::insert, e -> Log.e(TAG, "load date: " + e.getMessage()))
                                );
                                return Single.just(new ArrayList<>());
                            } else {
                                return Single.error(t);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::insert, e -> Log.e(TAG, "getAll: " + e.getMessage()))
        );
    }
    // insert products from db
    private void insert(List<Product> products) {
        if (!products.isEmpty()) {
            disposables.add(
                    Observable.fromIterable(products)
                            .filter(product -> product.getPcs() > 0)
                            .subscribe(product -> {
                                productList.add(product);
                                notifyItemInserted(productList.size() - 1);
                            }, e -> Log.e(TAG, "insert: " + e.getMessage()))
            );
        }
    }
    // refresh insert product
    private void insert(Product product) {
        productList.add(product);
        notifyItemInserted(productList.size() - 1);
    }
    // refresh updated product
    private void update(Product product) {
        if (product.getPcs() > 0) {
            int index = productList.indexOf(product);
            if (index >= 0) {
                productList.set(index, product);
                notifyItemChanged(index);
            } else {
                insert(product);
            }
        } else {
            delete(product);
        }
    }
    // refresh delete product
    private void delete(Product product) {
        int index = productList.indexOf(product);
        if (index >= 0) {
            productList.remove(product);
            notifyItemRemoved(index);
        }
    }

    @NonNull
    @Override
    public StoreFrontHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_front, parent, false);
        return new StoreFrontHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreFrontHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    @Override
    public void dispose() {
        if (disposables != null) disposables.dispose();
    }

    class StoreFrontHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView price;
        private TextView pcs;
        private Button buy;

        public StoreFrontHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemStoreFront__name);
            price = itemView.findViewById(R.id.itemStoreFront__value);
            pcs = itemView.findViewById(R.id.itemStoreFront__pcs);
            buy = itemView.findViewById(R.id.itemStoreFront__buttonBuy);
        }

        void bind(int position){
            name.setText(productList.get(position).getName());
            NumberFormat formatter = NumberFormat.getNumberInstance();
            price.setText(String.format("%s руб.", formatter.format(productList.get(position).getPrice())));
            pcs.setText(String.format("%s шт.", productList.get(position).getPcs()));
        }
    }
}
