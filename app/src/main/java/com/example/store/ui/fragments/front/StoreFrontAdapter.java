package com.example.store.ui.fragments.front;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.store.R;
import com.example.store.appServices.ProductRepository;
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
    private ProductRepository productRepo;
    private List<Product> productList;
    private CompositeDisposable disposables;

    public StoreFrontAdapter(ProductRepository productRepo, DataLoader loader) {
        this.productRepo = productRepo;
        disposables = new CompositeDisposable();

        productList = new ArrayList<>();

        // track insert product
        disposables.add(
                productRepo.insertChange()
                        .filter(product -> product.getPcs() > 0)
                        .subscribe(this::insert, e -> Log.e(TAG, "insertChange: " + e.getMessage()))
        );
        // track update product
        disposables.add(
                productRepo.updateChange()
                        .subscribe(this::update, e -> Log.e(TAG, "updateChange: " + e.getMessage()))
        );
        // track delete product
        disposables.add(
                productRepo.deleteChange()
                        .subscribe(this::delete, e -> Log.e(TAG, "deleteChange: " + e.getMessage()))
        );
        // initialization products list
        disposables.add(
                productRepo.dao()
                        .getAllProducts()
                        .flatMap(p -> !p.isEmpty() ? Single.just(p) : Single.error(new Throwable("Empty db")))
                        .onErrorResumeNext(t -> {
                            if (t.getMessage() != null && t.getMessage().equals("Empty db")) {
                                disposables.add(
                                        loader.getProducts()
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(productRepo::insert, e -> Log.e(TAG, "load date: " + e.getMessage()))
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
        private Product product;
        private TextView name;
        private TextView price;
        private TextView pcs;
        private Button buy;
        private ProgressBar progress;

        public StoreFrontHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemStoreFront__name);
            price = itemView.findViewById(R.id.itemStoreFront__value);
            pcs = itemView.findViewById(R.id.itemStoreFront__pcs);
            buy = itemView.findViewById(R.id.itemStoreFront__buttonBuy);
            progress = itemView.findViewById(R.id.itemStoreFront__progress);
        }

        void bind(int position) {
            product = productList.get(position);
            progress.setVisibility(View.GONE);
            buy.setEnabled(true);
            name.setText(product.getName());
            NumberFormat formatter = NumberFormat.getNumberInstance();
            price.setText(String.format("%s руб.", formatter.format(product.getPrice())));
            pcs.setText(String.format("%s шт.", product.getPcs()));

            buy.setOnClickListener(v -> {
                progress.setVisibility(View.VISIBLE);
                buy.setEnabled(false);
                productRepo.buy(product);
            });
        }
    }
}
