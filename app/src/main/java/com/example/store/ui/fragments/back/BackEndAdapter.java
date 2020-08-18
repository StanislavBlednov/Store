package com.example.store.ui.fragments.back;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.store.R;
import com.example.store.appServices.ProductRepository;
import com.example.store.data.DataLoader;
import com.example.store.db.Product;
import com.example.store.utils.Clear;
import com.example.store.utils.Click;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BackEndAdapter extends RecyclerView.Adapter<BackEndAdapter.BackEndHolder> implements Clear {
    private static final String TAG = "BackEndAdapter";
    private ProductRepository productRepo;
    private List<Product> productList;
    private CompositeDisposable disposables;
    private Click click;

    public BackEndAdapter(ProductRepository productRepo, DataLoader loader, Click click) {
        this.productRepo = productRepo;
        this.click = click;
        disposables = new CompositeDisposable();

        productList = new ArrayList<>();

        // track insert product
        disposables.add(
                productRepo.insertChange()
                        .subscribe(this::insert, e -> Log.e(TAG, "insertChange: " + e.getMessage()))
        );
        // track update product
        disposables.add(
                productRepo.updateChange()
                        .subscribe(this::update, e -> Log.e(TAG, "insertChange: " + e.getMessage()))
        );
        // track delete product
        disposables.add(
                productRepo.deleteChange()
                        .subscribe(this::delete, e -> Log.e(TAG, "insertChange: " + e.getMessage()))
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
        int index = productList.indexOf(product);
        if (index >= 0) {
            productList.set(index, product);
            notifyItemChanged(index);
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
    public BackEndHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_back_end, parent, false);
        BackEndHolder holder = new BackEndHolder(view);
        view.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                click.edit(productList.get(adapterPosition));
            }
        });
        view.setOnLongClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                productRepo.delete(productList.get(adapterPosition));
            }
            return true;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BackEndHolder holder, int position) {
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

    class BackEndHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView pcs;

        public BackEndHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemBackEnd__name);
            pcs = itemView.findViewById(R.id.itemBackEnd__pcs);
        }

        void bind(int position) {
            name.setText(productList.get(position).getName());
            pcs.setText(String.format("%s шт.", productList.get(position).getPcs()));
        }
    }
}
