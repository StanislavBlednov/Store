package com.example.store.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;

import com.example.store.R;
import com.example.store.appServices.AnimationService;
import com.example.store.appServices.NavigationService;
import com.example.store.utils.Clear;
import com.example.store.utils.MenuItemVisibility;
import com.example.store.utils.SaveRestoreState;

import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import io.reactivex.disposables.Disposable;

public class MainActivityViewModel implements Clear, SaveRestoreState {
    private static final String TAG = "MainActivityViewModel";
    private static final String BOTTOM_BAR = "MainActivityBottomBar";
    private static final String MENU = "MainActivityMenu";
    private final AnimationService animationService;
    private Disposable listener;
    private ObservableBoolean hideBottomBar = new ObservableBoolean(true);
    private ObservableField<Animation> animateBottomBar = new ObservableField<>();
    private ObservableField<MenuItemVisibility> menu = new ObservableField<>();

    public MainActivityViewModel(AnimationService animationService, NavigationService navigationService) {
        this.animationService = animationService;

        listener = navigationService.getDestination()
                .map(id -> {
                    switch (id){
                        case R.id.productDetailFragment: {
                            if (!hideBottomBar.get()) hideBottomBar();
                            menu.set(MenuItemVisibility.SAVE);
                            break;
                        }
                        case R.id.storeFrontFragment: {
                            if (hideBottomBar.get()) showBottomBar();
                            menu.set(MenuItemVisibility.HIDE_ADD_SAVE);
                            break;
                        }
                        case R.id.backEndFragment: {
                            if (hideBottomBar.get()) showBottomBar();
                            menu.set(MenuItemVisibility.ADD);
                            break;
                        }
                    }
                    return id;
                })
                .subscribe(n -> {}, e -> Log.e(TAG, "Error: " + e.getMessage()));
    }

    private void hideBottomBar(){
        hideBottomBar.set(true);
        animateBottomBar.set(animationService.apply(R.anim.anim_fall_down));
    }

    private void showBottomBar(){
        hideBottomBar.set(false);
        animateBottomBar.set(animationService.apply(R.anim.anim_rise_up));
    }

    @Override
    public void dispose(){
        if (listener != null && !listener.isDisposed()) listener.dispose();
    }

    @BindingAdapter("android:showMenuItem")
    public static void showMenuItem(Toolbar toolbar, MenuItemVisibility item) {
        toolbar.post(() -> {
            if (item != null && toolbar.getMenu() != null) {
                if (item.getShow().length > 0) {
                    for (int show : item.getShow()) {
                        MenuItem menuItem = toolbar.getMenu().findItem(show);
                        if (menuItem != null) menuItem.setVisible(true);
                    }
                }
                if (item.getHide().length > 0) {
                    for (int hide : item.getHide()) {
                        MenuItem menuItem = toolbar.getMenu().findItem(hide);
                        if (menuItem != null) menuItem.setVisible(false);
                    }
                }
            }
        });
    }

    @BindingAdapter("android:animate")
    public static void animate(View view, Animation anim) {
        if (anim != null) {
            view.setAnimation(anim);
        }
    }

    public ObservableBoolean getHideBottomBar() {
        return hideBottomBar;
    }

    public ObservableField<Animation> getAnimateBottomBar() {
        return animateBottomBar;
    }

    public ObservableField<MenuItemVisibility> getMenu() {
        return menu;
    }

    @Override
    public void save(Bundle bundle) {
        if (bundle != null){
            bundle.putBoolean(BOTTOM_BAR, hideBottomBar.get());
            bundle.putString(MENU, Objects.requireNonNull(menu.get()).name());
        }
    }

    @Override
    public void restore(Bundle bundle) {
        if (bundle != null){
            hideBottomBar.set(bundle.getBoolean(BOTTOM_BAR));
            menu.set(MenuItemVisibility.valueOf(bundle.getString(MENU)));
        }
    }
}
