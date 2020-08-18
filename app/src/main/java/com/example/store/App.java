package com.example.store;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.store.appServices.AnimationService;
import com.example.store.appServices.AppAnimation;
import com.example.store.appServices.AppNavigation;
import com.example.store.appServices.AppServices;
import com.example.store.appServices.DBRepository;
import com.example.store.appServices.NavigationService;
import com.example.store.appServices.ProductRepository;
import com.example.store.data.CsvToProductParser;
import com.example.store.data.DataLoader;
import com.example.store.ui.fragments.back.BackEndFragment;
import com.example.store.ui.fragments.front.StoreFrontFragment;

public class App extends Application implements AppServices {
    public static final String PREFERENCE_SUFFIX = "_pref";
    private AnimationService animationService;
    private NavigationService navigationService;
    private DBRepository dbRepository;
    private DataLoader loader;

    @Override
    public void onCreate() {
        super.onCreate();
        dbRepository = new DBRepository(getApplicationContext());
        // reset recycleView positions
        getPreference()
                .edit()
                .putInt(StoreFrontFragment.FRONT_ITEM, 0)
                .putInt(BackEndFragment.BACK_ITEM, 0)
                .apply();
    }

    @Override
    public AnimationService getAppAnimations() {
        if (animationService == null){
            animationService = new AppAnimation(getApplicationContext());
        }
        return animationService;
    }

    @Override
    public NavigationService getNavService() {
        if (navigationService == null){
            navigationService = new AppNavigation();
        }
        return navigationService;
    }

    @Override
    public ProductRepository getProductRepo() {
        return dbRepository.productRepo();
    }

    @Override
    public DataLoader getLoader() {
        if (loader == null) loader = new CsvToProductParser(getResources());
        return loader;
    }

    @Override
    public SharedPreferences getPreference() {
        return getSharedPreferences(getPackageName() + PREFERENCE_SUFFIX, MODE_PRIVATE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (dbRepository != null) dbRepository.dispose();
    }
}
